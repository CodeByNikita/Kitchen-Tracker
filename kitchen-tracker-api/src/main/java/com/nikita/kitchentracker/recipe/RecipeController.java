package com.nikita.kitchentracker.recipe;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nikita.kitchentracker.auth.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final AuthService authService;

    public RecipeController(RecipeService recipeService, AuthService authService) {
        this.recipeService = recipeService;
        this.authService = authService;
    }

    @PostMapping("/suggest")
    public RecipeSuggestionResponse suggestRecipes(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody RecipeSuggestionRequest request
    ) {
        authService.requireUser(authorization);
        return recipeService.suggestRecipes(request.getIngredients(), request.getExcludeTitles());
    }
}
