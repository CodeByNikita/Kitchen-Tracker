package com.nikita.kitchentracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.model.Category;
import com.nikita.kitchentracker.model.Item;
import com.nikita.kitchentracker.model.Location;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCategory(Category category);

    List<Item> findByLocation(Location location);

    List<Item> findByExpiryDateBefore(LocalDate date);

    List<Item> findByExpiryDateLessThanEqual(LocalDate date);

}