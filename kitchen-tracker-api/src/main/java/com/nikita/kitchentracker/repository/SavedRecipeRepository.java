package com.nikita.kitchentracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.recipe.SavedRecipe;

@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {
    List<SavedRecipe> findAllByOwnerOrderBySavedAtDesc(AppUser owner);

    Optional<SavedRecipe> findByOwnerAndTitleIgnoreCase(AppUser owner, String title);

    List<SavedRecipe> findAllByOwnerAndTitleIgnoreCase(AppUser owner, String title);

    Optional<SavedRecipe> findByIdAndOwner(Long id, AppUser owner);
}
