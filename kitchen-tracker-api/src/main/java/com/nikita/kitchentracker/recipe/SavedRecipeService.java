package com.nikita.kitchentracker.recipe;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nikita.kitchentracker.repository.SavedRecipeRepository;

@Service
public class SavedRecipeService {
    private final SavedRecipeRepository repository;

    public SavedRecipeService(SavedRecipeRepository repository) {
        this.repository = repository;
    }

    public List<SavedRecipe> getSavedRecipes() {
        return repository.findAllByOrderBySavedAtDesc();
    }

    public SavedRecipe saveRecipe(RecipeSuggestion recipe) {
        return repository.findByTitleIgnoreCase(recipe.getTitle())
                .map(existing -> updateRecipe(existing, recipe))
                .orElseGet(() -> repository.save(toSavedRecipe(recipe)));
    }

    public void deleteRecipe(Long id) {
        repository.deleteById(id);
    }

    private SavedRecipe updateRecipe(SavedRecipe saved, RecipeSuggestion recipe) {
        saved.setUses(recipe.getUses());
        saved.setExtraIngredients(recipe.getExtraIngredients());
        saved.setSteps(recipe.getSteps());
        saved.setTimeMinutes(recipe.getTimeMinutes());
        saved.setDifficulty(recipe.getDifficulty());
        saved.setSavedAt(LocalDateTime.now());
        return repository.save(saved);
    }

    private SavedRecipe toSavedRecipe(RecipeSuggestion recipe) {
        SavedRecipe saved = new SavedRecipe();
        saved.setTitle(recipe.getTitle());
        saved.setUses(recipe.getUses());
        saved.setExtraIngredients(recipe.getExtraIngredients());
        saved.setSteps(recipe.getSteps());
        saved.setTimeMinutes(recipe.getTimeMinutes());
        saved.setDifficulty(recipe.getDifficulty());
        saved.setSavedAt(LocalDateTime.now());
        return saved;
    }
}
