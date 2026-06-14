package com.nikita.kitchentracker.shopping;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.auth.AuthService;
import com.nikita.kitchentracker.model.ShoppingListItem;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {
    private final ShoppingListService service;
    private final AuthService authService;

    public ShoppingListController(ShoppingListService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping
    public List<ShoppingListItem> getAll(@RequestHeader(name = "Authorization", required = false) String authorization) {
        return service.getAll(user(authorization));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListItem add(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody ShoppingListRequest request
    ) {
        return service.add(user(authorization), request.getName());
    }

    @PatchMapping("/{id}/toggle")
    public ShoppingListItem toggle(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        return service.toggle(user(authorization), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        service.delete(user(authorization), id);
    }

    private AppUser user(String authorization) {
        return authService.requireUser(authorization);
    }
}
