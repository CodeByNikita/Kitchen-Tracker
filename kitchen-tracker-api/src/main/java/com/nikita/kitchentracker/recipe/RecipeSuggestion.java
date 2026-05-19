package com.nikita.kitchentracker.recipe;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public class RecipeSuggestion {
    @NotBlank
    private String title;
    @NotEmpty
    private List<String> uses;
    private List<String> extraIngredients;
    @NotEmpty
    private List<String> steps;
    @Positive
    private int timeMinutes;
    @NotBlank
    private String difficulty;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getUses() {
        return uses;
    }

    public void setUses(List<String> uses) {
        this.uses = uses;
    }

    public List<String> getExtraIngredients() {
        return extraIngredients;
    }

    public void setExtraIngredients(List<String> extraIngredients) {
        this.extraIngredients = extraIngredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
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
}
