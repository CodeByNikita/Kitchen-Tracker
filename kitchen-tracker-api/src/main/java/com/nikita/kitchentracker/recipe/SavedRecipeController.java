package com.nikita.kitchentracker.recipe;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.auth.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/saved-recipes")
public class SavedRecipeController {
    private final SavedRecipeService service;
    private final AuthService authService;

    public SavedRecipeController(SavedRecipeService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping
    public List<SavedRecipe> getSavedRecipes(
            @RequestHeader(name = "Authorization", required = false) String authorization
    ) {
        return service.getSavedRecipes(user(authorization));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedRecipe saveRecipe(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody RecipeSuggestion recipe
    ) {
        return service.saveRecipe(user(authorization), recipe);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        service.deleteRecipe(user(authorization), id);
    }

    private AppUser user(String authorization) {
        return authService.requireUser(authorization);
    }
}
