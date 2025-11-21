package com.group6.instance;

import javafx.beans.property.SimpleStringProperty;
import java.util.List;

// A class for a display pizza. Used to store a pizza with its ingredients in a way that is easy to display.
public class DisplayPizza extends Pizza {
    private final SimpleStringProperty ingredients = new SimpleStringProperty();

    public DisplayPizza(int id, String name, double price, List<String> ingredientNames) {
        super(id, name, price);
        this.ingredients.set(String.join(", ", ingredientNames));
    }

    public DisplayPizza(int id, String name, double price, String ingredients) {
        super(id, name, price);
        this.ingredients.set(ingredients);
    }

    public SimpleStringProperty ingredientsProperty() {
        return ingredients;
    }

    public String getIngredients() {
        return ingredients.get();
    }

    public List<String> getIngredientNames() {
        String ingredientsStr = ingredients.get();
        if (ingredientsStr == null || ingredientsStr.isEmpty()) {
            return List.of();
        }
        return List.of(ingredientsStr.split(",\\s*"));
    }
}
