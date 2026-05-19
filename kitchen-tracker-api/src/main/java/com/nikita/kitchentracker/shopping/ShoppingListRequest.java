package com.nikita.kitchentracker.shopping;

import jakarta.validation.constraints.NotBlank;

public class ShoppingListRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
