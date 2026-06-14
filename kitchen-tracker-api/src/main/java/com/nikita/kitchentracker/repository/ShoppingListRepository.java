package com.nikita.kitchentracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.model.ShoppingListItem;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingListItem, Long> {
    List<ShoppingListItem> findAllByOwnerOrderByCheckedAscCreatedAtDesc(AppUser owner);

    java.util.Optional<ShoppingListItem> findByIdAndOwner(Long id, AppUser owner);
}
