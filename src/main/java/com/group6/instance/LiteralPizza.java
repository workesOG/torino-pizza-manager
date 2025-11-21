package com.group6.instance;

import java.util.List;

// A class for a literal pizza. Used to store a pizza with its ingredients in a list of ids, for working with the ingredients individually.
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
