package com.group6.instance;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Ingredient {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();

    public Ingredient(int id, String name, String description, double price) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.price.set(price);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getDescription() {
        return description.get();
    }

    public double getPrice() {
        return price.get();
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Ingredient that = (Ingredient) obj;
        return this.getId() == that.getId();
    }
}
