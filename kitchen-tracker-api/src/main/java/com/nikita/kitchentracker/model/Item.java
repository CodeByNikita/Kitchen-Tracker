package com.nikita.kitchentracker.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Enumerated(EnumType.STRING)
    private Unit unit;
    private LocalDate expiryDate;
    @Enumerated(EnumType.STRING)
    private Location location;
    private LocalDate dateOpened;
    private Integer onceOpenedDays;

    public Item() {
    }

    public Item(String name, int quantity, Category category, Unit unit, LocalDate expiryDate, Location location,
            LocalDate dateOpened, Integer onceOpenedDays) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.location = location;
        this.dateOpened = dateOpened;
        this.onceOpenedDays = onceOpenedDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDate dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Integer getOnceOpenedDays() {
        return onceOpenedDays;
    }

    public void setOnceOpenedDays(Integer onceOpenedDays) {
        this.onceOpenedDays = onceOpenedDays;
    }

    @Override
    public String toString() {
        return "Item [id=" + id + ", name=" + name + ", quantity=" + quantity +
                ", category=" + category + ", unit=" + unit +
                ", expiryDate=" + expiryDate + ", location=" + location +
                ", dateOpened=" + dateOpened + ", onceOpenedDays=" + onceOpenedDays + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + quantity;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((dateOpened == null) ? 0 : dateOpened.hashCode());
        result = prime * result + ((onceOpenedDays == null) ? 0 : onceOpenedDays.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (quantity != other.quantity)
            return false;
        if (category != other.category)
            return false;
        if (unit != other.unit)
            return false;
        if (expiryDate == null) {
            if (other.expiryDate != null)
                return false;
        } else if (!expiryDate.equals(other.expiryDate))
            return false;
        if (location != other.location)
            return false;
        if (dateOpened == null) {
            if (other.dateOpened != null)
                return false;
        } else if (!dateOpened.equals(other.dateOpened))
            return false;
        if (onceOpenedDays == null) {
            if (other.onceOpenedDays != null)
                return false;
        } else if (!onceOpenedDays.equals(other.onceOpenedDays))
            return false;
        return true;
    }

}
