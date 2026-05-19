package com.nikita.kitchentracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.model.ShoppingListItem;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingListItem, Long> {
    List<ShoppingListItem> findAllByOrderByCheckedAscCreatedAtDesc();
}
