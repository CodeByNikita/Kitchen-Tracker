package com.nikita.kitchentracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nikita.kitchentracker.model.Category;
import com.nikita.kitchentracker.model.Item;
import com.nikita.kitchentracker.model.Location;
import com.nikita.kitchentracker.service.KitchenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final KitchenService service;

    public ItemController(KitchenService service) {
        this.service = service;
    }

    // GET /api/items
    @GetMapping
    public List<Item> getAllItems() {
        return service.getAllItems();
    }

    // GET /api/items/1
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return service.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/items/expiring?days=7
    @GetMapping("/expiring")
    public List<Item> getExpiringSoon(@RequestParam(defaultValue = "7") int days) {
        return service.getExpiringSoon(days);
    }

    // GET /api/items/category/DAIRY
    @GetMapping("/category/{category}")
    public List<Item> getByCategory(@PathVariable Category category) {
        return service.getByCategory(category);
    }

    // GET /api/items/location/FRIDGE
    @GetMapping("/location/{location}")
    public List<Item> getByLocation(@PathVariable Location location) {
        return service.getByLocation(location);
    }

    // POST /api/items
    @PostMapping
    public ResponseEntity<Item> addItem(@Valid @RequestBody Item item) {
        Item saved = service.addItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/items/1
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @Valid @RequestBody Item item) {
        return ResponseEntity.ok(service.updateItem(id, item));
    }

    // PATCH /api/items/1/open
    @PatchMapping("/{id}/open")
    public ResponseEntity<Item> markAsOpened(@PathVariable Long id) {
        return service.markAsOpened(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/use-one")
    public ResponseEntity<Item> useOne(@PathVariable Long id) {
        return service.useOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/items/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
