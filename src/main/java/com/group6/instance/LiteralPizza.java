package com.group6.instance;

import java.util.List;

public class LiteralPizza extends Pizza {
    private final List<Integer> ingredientIds;

    public LiteralPizza(int id, String name, double price, List<Integer> ingredientIds) {
        super(id, name, price);
        this.ingredientIds = ingredientIds;
    }

    public List<Integer> getIngredientIds() {
        return ingredientIds;
    }
}
