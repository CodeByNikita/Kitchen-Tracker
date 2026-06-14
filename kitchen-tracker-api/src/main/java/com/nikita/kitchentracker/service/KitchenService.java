package com.nikita.kitchentracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.model.Category;
import com.nikita.kitchentracker.model.Item;
import com.nikita.kitchentracker.model.Location;
import com.nikita.kitchentracker.repository.ItemRepository;

@Service
public class KitchenService {

    private final ItemRepository repository;

    public KitchenService(ItemRepository repository) {
        this.repository = repository;
    }

    // get all items
    public List<Item> getAllItems(AppUser user) {
        return repository.findByOwnerOrderByIdDesc(user);
    }

    // get one item by id
    public Optional<Item> getItemById(AppUser user, Long id) {
        return repository.findByIdAndOwner(id, user);
    }

    // add a new item
    public Item addItem(AppUser user, Item item) {
        item.setOwner(user);
        return repository.save(item);
    }

    // update an existing item
    public Item updateItem(AppUser user, Long id, Item updatedItem) {
        repository.findByIdAndOwner(id, user).orElseThrow();
        updatedItem.setId(id);
        updatedItem.setOwner(user);
        return repository.save(updatedItem);
    }

    // delete an item
    public void deleteItem(AppUser user, Long id) {
        repository.findByIdAndOwner(id, user).ifPresent(repository::delete);
    }

    // mark as opened
    public Optional<Item> markAsOpened(AppUser user, Long id) {
        Optional<Item> found = repository.findByIdAndOwner(id, user);
        found.ifPresent(item -> {
            item.setDateOpened(LocalDate.now());
            repository.save(item);
        });
        return found;
    }

    public Optional<Item> useOne(AppUser user, Long id) {
        Optional<Item> found = repository.findByIdAndOwner(id, user);
        found.ifPresent(item -> {
            int nextQuantity = Math.max(0, item.getQuantity() - 1);
            item.setQuantity(nextQuantity);
            repository.save(item);
        });
        return found;
    }

    // filter by category
    public List<Item> getByCategory(AppUser user, Category category) {
        return repository.findByOwnerAndCategory(user, category);
    }

    // filter by location
    public List<Item> getByLocation(AppUser user, Location location) {
        return repository.findByOwnerAndLocation(user, location);
    }

    // expiring soon (exclusive upper bound — items before cutoff)
    public List<Item> getExpiringSoon(AppUser user, int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return repository.findByOwnerAndExpiryDateBefore(user, cutoff);
    }

    // expiring soon inclusive — includes today+daysAhead and expired items
    public List<Item> getExpiringSoonInclusive(AppUser user, int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return repository.findByOwnerAndExpiryDateLessThanEqual(user, cutoff);
    }
}
