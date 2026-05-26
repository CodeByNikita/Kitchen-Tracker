package com.nikita.kitchentracker.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "saved_recipes")
public class SavedRecipe {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    @Column(name = "uses_json")
    private String usesJson = "[]";
    @Lob
    @Column(name = "extra_ingredients_json")
    private String extraIngredientsJson = "[]";
    @Lob
    @Column(name = "steps_json")
    private String stepsJson = "[]";
    private int timeMinutes;
    private String difficulty;
    private LocalDateTime savedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Transient
    public List<String> getUses() {
        return parseList(usesJson);
    }

    public void setUses(List<String> uses) {
        this.usesJson = formatList(uses);
    }

    @Transient
    public List<String> getExtraIngredients() {
        return parseList(extraIngredientsJson);
    }

    public void setExtraIngredients(List<String> extraIngredients) {
        this.extraIngredientsJson = formatList(extraIngredients);
    }

    @Transient
    public List<String> getSteps() {
        return parseList(stepsJson);
    }

    public void setSteps(List<String> steps) {
        this.stepsJson = formatList(steps);
    }

    public int getTimeMinutes() {
        return timeMinutes;
    }

    public void setTimeMinutes(int timeMinutes) {
        this.timeMinutes = timeMinutes;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    private static List<String> parseList(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(value, STRING_LIST);
        } catch (JsonProcessingException ex) {
            return new ArrayList<>();
        }
    }

    private static String formatList(List<String> value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value == null ? List.of() : value);
        } catch (JsonProcessingException ex) {
            return "[]";
        }
    }
}
