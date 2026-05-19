package com.nikita.kitchentracker.recipe;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class RecipeSuggestionRequest {
    @Valid
    @NotEmpty
    private List<RecipeIngredientDto> ingredients = new ArrayList<>();
    private List<String> excludeTitles = new ArrayList<>();

    public List<RecipeIngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientDto> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getExcludeTitles() {
        return excludeTitles;
    }

    public void setExcludeTitles(List<String> excludeTitles) {
        this.excludeTitles = excludeTitles;
    }
}
