package com.nikita.kitchentracker.recipe;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/saved-recipes")
public class SavedRecipeController {
    private final SavedRecipeService service;

    public SavedRecipeController(SavedRecipeService service) {
        this.service = service;
    }

    @GetMapping
    public List<SavedRecipe> getSavedRecipes() {
        return service.getSavedRecipes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedRecipe saveRecipe(@Valid @RequestBody RecipeSuggestion recipe) {
        return service.saveRecipe(recipe);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id) {
        service.deleteRecipe(id);
    }
}
