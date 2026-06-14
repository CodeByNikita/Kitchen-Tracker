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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.auth.AuthService;
import com.nikita.kitchentracker.model.Category;
import com.nikita.kitchentracker.model.Item;
import com.nikita.kitchentracker.model.Location;
import com.nikita.kitchentracker.service.KitchenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final KitchenService service;
    private final AuthService authService;

    public ItemController(KitchenService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    // GET /api/items
    @GetMapping
    public List<Item> getAllItems(@RequestHeader(name = "Authorization", required = false) String authorization) {
        return service.getAllItems(user(authorization));
    }

    // GET /api/items/1
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        return service.getItemById(user(authorization), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/items/expiring?days=7
    @GetMapping("/expiring")
    public List<Item> getExpiringSoon(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam(defaultValue = "7") int days
    ) {
        return service.getExpiringSoon(user(authorization), days);
    }

    // GET /api/items/category/DAIRY
    @GetMapping("/category/{category}")
    public List<Item> getByCategory(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Category category
    ) {
        return service.getByCategory(user(authorization), category);
    }

    // GET /api/items/location/FRIDGE
    @GetMapping("/location/{location}")
    public List<Item> getByLocation(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Location location
    ) {
        return service.getByLocation(user(authorization), location);
    }

    // POST /api/items
    @PostMapping
    public ResponseEntity<Item> addItem(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody Item item
    ) {
        Item saved = service.addItem(user(authorization), item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/items/1
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody Item item
    ) {
        return ResponseEntity.ok(service.updateItem(user(authorization), id, item));
    }

    // PATCH /api/items/1/open
    @PatchMapping("/{id}/open")
    public ResponseEntity<Item> markAsOpened(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        return service.markAsOpened(user(authorization), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/use-one")
    public ResponseEntity<Item> useOne(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        return service.useOne(user(authorization), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/items/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        service.deleteItem(user(authorization), id);
        return ResponseEntity.noContent().build();
    }

    private AppUser user(String authorization) {
        return authService.requireUser(authorization);
    }
}
