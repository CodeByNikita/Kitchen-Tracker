package com.nikita.kitchentracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.model.Category;
import com.nikita.kitchentracker.model.Item;
import com.nikita.kitchentracker.model.Location;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerOrderByIdDesc(AppUser owner);

    Optional<Item> findByIdAndOwner(Long id, AppUser owner);

    List<Item> findByOwnerAndCategory(AppUser owner, Category category);

    List<Item> findByOwnerAndLocation(AppUser owner, Location location);

    List<Item> findByOwnerAndExpiryDateBefore(AppUser owner, LocalDate date);

    List<Item> findByOwnerAndExpiryDateLessThanEqual(AppUser owner, LocalDate date);

}
