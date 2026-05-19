package com.nikita.kitchentracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.recipe.SavedRecipe;

@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {
    List<SavedRecipe> findAllByOrderBySavedAtDesc();

    Optional<SavedRecipe> findByTitleIgnoreCase(String title);
}
