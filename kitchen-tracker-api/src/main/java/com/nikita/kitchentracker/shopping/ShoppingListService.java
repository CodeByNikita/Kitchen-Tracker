package com.nikita.kitchentracker.shopping;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nikita.kitchentracker.model.ShoppingListItem;
import com.nikita.kitchentracker.repository.ShoppingListRepository;

@Service
public class ShoppingListService {
    private final ShoppingListRepository repository;

    public ShoppingListService(ShoppingListRepository repository) {
        this.repository = repository;
    }

    public List<ShoppingListItem> getAll() {
        return repository.findAllByOrderByCheckedAscCreatedAtDesc();
    }

    public ShoppingListItem add(String name) {
        ShoppingListItem item = new ShoppingListItem();
        item.setName(name.trim());
        item.setCreatedAt(LocalDateTime.now());
        return repository.save(item);
    }

    public ShoppingListItem toggle(Long id) {
        ShoppingListItem item = repository.findById(id).orElseThrow();
        item.setChecked(!item.isChecked());
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
