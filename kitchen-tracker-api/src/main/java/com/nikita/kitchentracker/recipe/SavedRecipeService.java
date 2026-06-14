package com.nikita.kitchentracker.recipe;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.repository.SavedRecipeRepository;

@Service
public class SavedRecipeService {
    private final SavedRecipeRepository repository;

    public SavedRecipeService(SavedRecipeRepository repository) {
        this.repository = repository;
    }

    public List<SavedRecipe> getSavedRecipes(AppUser user) {
        return repository.findAllByOwnerOrderBySavedAtDesc(user).stream()
                .filter(this::hasRecipeDetails)
                .toList();
    }

    public SavedRecipe saveRecipe(AppUser user, RecipeSuggestion recipe) {
        if (!hasRecipeDetails(recipe)) {
            throw new IllegalArgumentException("Saved recipes must include ingredients and steps.");
        }
        List<SavedRecipe> matches = repository.findAllByOwnerAndTitleIgnoreCase(user, recipe.getTitle());
        if (matches.isEmpty()) {
            return repository.save(toSavedRecipe(user, recipe));
        }

        SavedRecipe latest = matches.stream()
                .max(Comparator.comparing(SavedRecipe::getSavedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(matches.get(0));
        matches.stream()
                .filter(saved -> !saved.getId().equals(latest.getId()))
                .forEach(repository::delete);
        return updateRecipe(latest, recipe);
    }

    public void deleteRecipe(AppUser user, Long id) {
        repository.findByIdAndOwner(id, user).ifPresent(repository::delete);
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

    private SavedRecipe toSavedRecipe(AppUser user, RecipeSuggestion recipe) {
        SavedRecipe saved = new SavedRecipe();
        saved.setOwner(user);
        saved.setTitle(recipe.getTitle());
        saved.setUses(recipe.getUses());
        saved.setExtraIngredients(recipe.getExtraIngredients());
        saved.setSteps(recipe.getSteps());
        saved.setTimeMinutes(recipe.getTimeMinutes());
        saved.setDifficulty(recipe.getDifficulty());
        saved.setSavedAt(LocalDateTime.now());
        return saved;
    }

    private boolean hasRecipeDetails(RecipeSuggestion recipe) {
        return recipe.getUses() != null && !recipe.getUses().isEmpty()
                && recipe.getSteps() != null && !recipe.getSteps().isEmpty();
    }

    private boolean hasRecipeDetails(SavedRecipe recipe) {
        return recipe.getUses() != null && !recipe.getUses().isEmpty()
                && recipe.getSteps() != null && !recipe.getSteps().isEmpty();
    }
}
