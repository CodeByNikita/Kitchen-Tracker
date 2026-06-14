package com.nikita.kitchentracker.shopping;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nikita.kitchentracker.auth.AppUser;
import com.nikita.kitchentracker.model.ShoppingListItem;
import com.nikita.kitchentracker.repository.ShoppingListRepository;

@Service
public class ShoppingListService {
    private final ShoppingListRepository repository;

    public ShoppingListService(ShoppingListRepository repository) {
        this.repository = repository;
    }

    public List<ShoppingListItem> getAll(AppUser user) {
        return repository.findAllByOwnerOrderByCheckedAscCreatedAtDesc(user);
    }

    public ShoppingListItem add(AppUser user, String name) {
        ShoppingListItem item = new ShoppingListItem();
        item.setOwner(user);
        item.setName(name.trim());
        item.setCreatedAt(LocalDateTime.now());
        return repository.save(item);
    }

    public ShoppingListItem toggle(AppUser user, Long id) {
        ShoppingListItem item = repository.findByIdAndOwner(id, user).orElseThrow();
        item.setChecked(!item.isChecked());
        return repository.save(item);
    }

    public void delete(AppUser user, Long id) {
        repository.findByIdAndOwner(id, user).ifPresent(repository::delete);
    }
}
