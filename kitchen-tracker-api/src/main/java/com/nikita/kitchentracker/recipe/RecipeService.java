package com.nikita.kitchentracker.recipe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RecipeService {
    private static final String GEMINI_URL_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public RecipeService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model
    ) {
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public RecipeSuggestionResponse suggestRecipes(
            List<RecipeIngredientDto> ingredients,
            List<String> excludeTitles
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gemini API key is missing. Set GEMINI_API_KEY before starting the backend."
            );
        }

        try {
            String requestBody = objectMapper.writeValueAsString(buildGeminiRequest(ingredients, excludeTitles));
            HttpRequest request = HttpRequest.newBuilder(geminiUrl())
                    .timeout(Duration.ofSeconds(45))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return sanitizeResponse(parseGeminiResponse(response), ingredients);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Recipe generation was interrupted.", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not reach OpenAI.", e);
        }
    }

    private URI geminiUrl() {
        return URI.create(GEMINI_URL_TEMPLATE.formatted(model));
    }

    private Map<String, Object> buildGeminiRequest(
            List<RecipeIngredientDto> ingredients,
            List<String> excludeTitles
    ) throws JsonProcessingException {
        String ingredientsJson = objectMapper.writeValueAsString(
                ingredients.stream().map(this::ingredientForPrompt).toList()
        );
        String titlesJson = objectMapper.writeValueAsString(excludeTitles == null ? List.of() : excludeTitles);
        String userPrompt = """
                Suggest 2 to 4 practical home recipes that use as many of these expiring kitchen items as possible.
                Prioritise items that expire soonest. Keep recipes realistic and avoid suggesting unsafe food use.
                Only use the supplied item names in the 'uses' field.
                Do not repeat any recipe titles from the excluded titles list.

                Expiring items JSON:
                %s

                Excluded recipe titles JSON:
                %s
                """.formatted(ingredientsJson, titlesJson);

        return Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of(
                                                "text",
                                                "You are a practical home-cooking assistant. Return concise recipe ideas.\n\n"
                                                        + userPrompt
                                        )
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", recipeSchema()
                )
        );
    }

    private Map<String, Object> ingredientForPrompt(RecipeIngredientDto ingredient) {
        return Map.of(
                "name", ingredient.getName(),
                "quantity", ingredient.getQuantity(),
                "unit", ingredient.getUnit() == null ? "" : ingredient.getUnit().name(),
                "category", ingredient.getCategory() == null ? "" : ingredient.getCategory().name(),
                "expiryDate", ingredient.getExpiryDate() == null ? "" : ingredient.getExpiryDate().toString()
        );
    }

    private RecipeSuggestionResponse sanitizeResponse(
            RecipeSuggestionResponse response,
            List<RecipeIngredientDto> ingredients
    ) {
        List<String> ingredientNames = ingredients.stream()
                .map(RecipeIngredientDto::getName)
                .filter(name -> name != null && !name.isBlank())
                .toList();
        List<RecipeSuggestion> recipes = response.getRecipes().stream()
                .filter(recipe -> recipe.getTitle() != null && !recipe.getTitle().isBlank())
                .map(recipe -> sanitizeRecipe(recipe, ingredientNames))
                .toList();
        response.setRecipes(recipes);
        return response;
    }

    private RecipeSuggestion sanitizeRecipe(RecipeSuggestion recipe, List<String> ingredientNames) {
        List<String> uses = recipe.getUses() == null ? List.of() : recipe.getUses();
        if (uses.isEmpty()) {
            recipe.setUses(ingredientNames.stream().limit(3).toList());
        }
        if (recipe.getExtraIngredients() == null) {
            recipe.setExtraIngredients(List.of());
        }
        List<String> steps = recipe.getSteps() == null ? List.of() : recipe.getSteps();
        if (steps.isEmpty()) {
            recipe.setSteps(List.of(
                    "Prepare the listed ingredients.",
                    "Cook gently until everything is hot and safe to eat.",
                    "Taste, season, and serve."
            ));
        }
        return recipe;
    }

    private RecipeSuggestionResponse parseGeminiResponse(HttpResponse<String> response) throws JsonProcessingException {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini returned status " + response.statusCode() + ": " + response.body()
            );
        }

        JsonNode root = objectMapper.readTree(response.body());
        String outputText = root.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText(null);
        if (outputText == null || outputText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini response did not contain recipe JSON.");
        }
        return objectMapper.readValue(outputText, RecipeSuggestionResponse.class);
    }

    private Map<String, Object> recipeSchema() {
        Map<String, Object> stringArray = Map.of(
                "type", "ARRAY",
                "items", Map.of("type", "STRING")
        );
        Map<String, Object> recipe = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "title", Map.of("type", "STRING"),
                        "uses", stringArray,
                        "extraIngredients", stringArray,
                        "steps", stringArray,
                        "timeMinutes", Map.of("type", "INTEGER"),
                        "difficulty", Map.of("type", "STRING", "enum", List.of("Easy", "Medium"))
                ),
                "propertyOrdering", List.of("title", "uses", "extraIngredients", "steps", "timeMinutes", "difficulty"),
                "required", List.of("title", "uses", "extraIngredients", "steps", "timeMinutes", "difficulty")
        );
        return Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "recipes", Map.of(
                                "type", "ARRAY",
                                "items", recipe
                        )
                ),
                "propertyOrdering", List.of("recipes"),
                "required", List.of("recipes")
        );
    }
}
