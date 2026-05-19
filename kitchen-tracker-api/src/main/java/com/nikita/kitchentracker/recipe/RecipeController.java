package com.nikita.kitchentracker.recipe;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/suggest")
    public RecipeSuggestionResponse suggestRecipes(@Valid @RequestBody RecipeSuggestionRequest request) {
        return recipeService.suggestRecipes(request.getIngredients(), request.getExcludeTitles());
    }
}
