package com.nikita.kitchentracker.recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeSuggestionResponse {
    private List<RecipeSuggestion> recipes = new ArrayList<>();

    public List<RecipeSuggestion> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeSuggestion> recipes) {
        this.recipes = recipes;
    }
}
