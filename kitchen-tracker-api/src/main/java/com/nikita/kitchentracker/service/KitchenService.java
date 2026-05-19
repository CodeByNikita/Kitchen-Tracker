package com.nikita.kitchentracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
    public List<Item> getAllItems() {
        return repository.findAll();
    }

    // get one item by id
    public Optional<Item> getItemById(Long id) {
        return repository.findById(id);
    }

    // add a new item
    public Item addItem(Item item) {
        return repository.save(item);
    }

    // update an existing item
    public Item updateItem(Long id, Item updatedItem) {
        updatedItem.setId(id);
        return repository.save(updatedItem);
    }

    // delete an item
    public void deleteItem(Long id) {
        repository.deleteById(id);
    }

    // mark as opened
    public Optional<Item> markAsOpened(Long id) {
        Optional<Item> found = repository.findById(id);
        found.ifPresent(item -> {
            item.setDateOpened(LocalDate.now());
            repository.save(item);
        });
        return found;
    }

    public Optional<Item> useOne(Long id) {
        Optional<Item> found = repository.findById(id);
        found.ifPresent(item -> {
            int nextQuantity = Math.max(0, item.getQuantity() - 1);
            item.setQuantity(nextQuantity);
            repository.save(item);
        });
        return found;
    }

    // filter by category
    public List<Item> getByCategory(Category category) {
        return repository.findByCategory(category);
    }

    // filter by location
    public List<Item> getByLocation(Location location) {
        return repository.findByLocation(location);
    }

    // expiring soon (exclusive upper bound — items before cutoff)
    public List<Item> getExpiringSoon(int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return repository.findByExpiryDateBefore(cutoff);
    }

    // expiring soon inclusive — includes today+daysAhead and expired items
    public List<Item> getExpiringSoonInclusive(int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return repository.findByExpiryDateLessThanEqual(cutoff);
    }
}
