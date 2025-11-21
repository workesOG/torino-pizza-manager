package com.group6.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.group6.App;

public class PendingPizzaOperation {
    public enum OperationType {
        SET_NAME,
        SET_PRICE,
        ADD_INGREDIENT,
        REMOVE_INGREDIENT,
    }

    private final OperationType type;
    private final int pizzaId;
    private String name;
    private double price;
    private Ingredient ingredient;
    private boolean add;

    public PendingPizzaOperation(OperationType type, int pizzaId, Object... args) throws IllegalArgumentException {
        this.type = type;
        this.pizzaId = pizzaId;
        switch (type) {
            case SET_NAME:
                if (!validateArgumentCount(1, 1, args)) {
                    throw new IllegalArgumentException("SET_NAME operation requires 1 argument");
                }
                if (!validateArgumentType(String.class, args[0])) {
                    throw new IllegalArgumentException("SET_NAME operation requires a String argument");
                }
                this.name = (String) args[0];
                break;
            case SET_PRICE:
                if (!validateArgumentCount(1, 1, args)) {
                    throw new IllegalArgumentException("SET_PRICE operation requires 1 argument");
                }
                if (!validateArgumentType(Double.class, args[0])) {
                    throw new IllegalArgumentException("SET_PRICE operation requires a Double argument");
                }
                this.price = (double) args[0];
                break;
            case ADD_INGREDIENT:
                if (!validateArgumentCount(2, 2, args)) {
                    throw new IllegalArgumentException("ADD_INGREDIENT operation requires 2 arguments");
                }
                if (!validateArgumentType(Ingredient.class, args[0])) {
                    throw new IllegalArgumentException(
                            "ADD_INGREDIENT operation requires an Ingredient argument at index 0");
                }
                if (!validateArgumentType(java.lang.Boolean.class, args[1])) {
                    throw new IllegalArgumentException(
                            "ADD_INGREDIENT operation requires a boolean argument at index 1");
                }
                this.ingredient = (Ingredient) args[0];
                this.add = (boolean) args[1];
                break;
            case REMOVE_INGREDIENT:
                if (!validateArgumentCount(2, 2, args)) {
                    throw new IllegalArgumentException("REMOVE_INGREDIENT operation requires 2 arguments");
                }
                if (!validateArgumentType(Ingredient.class, args[0])) {
                    throw new IllegalArgumentException(
                            "REMOVE_INGREDIENT operation requires an Ingredient argument at index 0");
                }
                if (!validateArgumentType(java.lang.Boolean.class, args[1])) {
                    throw new IllegalArgumentException(
                            "REMOVE_INGREDIENT operation requires a boolean argument at index 1");
                }
                this.ingredient = (Ingredient) args[0];
                this.add = (boolean) args[1];
                break;
            default:
                throw new IllegalArgumentException("Invalid operation type");
        }
    }

    private boolean validateArgumentCount(int min, int max, Object... args) {
        return args.length >= min && args.length <= max;
    }

    private boolean validateArgumentType(Class<?> type, Object arg) {
        return arg.getClass() == type;
    }

    public OperationType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public boolean isAdd() {
        return add;
    }

    public static List<PendingPizzaOperation> trimPendingPizzaOperations(
            List<PendingPizzaOperation> pendingOperations) {
        List<PendingPizzaOperation> trimmed = new ArrayList<>();

        PendingPizzaOperation lastSetName = null;
        PendingPizzaOperation lastSetPrice = null;

        Map<Integer, Integer> ingredientNetChanges = new HashMap<>();
        Map<Integer, Ingredient> ingredientMap = new HashMap<>();

        for (PendingPizzaOperation op : pendingOperations) {
            switch (op.getType()) {
                case SET_NAME:
                    lastSetName = op;
                    break;
                case SET_PRICE:
                    lastSetPrice = op;
                    break;
                case ADD_INGREDIENT:
                    int addId = op.getIngredient().getId();
                    ingredientMap.put(addId, op.getIngredient());
                    ingredientNetChanges.put(addId, ingredientNetChanges.getOrDefault(addId, 0) + 1);
                    break;
                case REMOVE_INGREDIENT:
                    int removeId = op.getIngredient().getId();
                    ingredientMap.put(removeId, op.getIngredient());
                    ingredientNetChanges.put(removeId, ingredientNetChanges.getOrDefault(removeId, 0) - 1);
                    break;
            }
        }

        if (lastSetName != null) {
            trimmed.add(lastSetName);
        }

        if (lastSetPrice != null) {
            trimmed.add(lastSetPrice);
        }

        for (Map.Entry<Integer, Integer> entry : ingredientNetChanges.entrySet()) {
            int ingredientId = entry.getKey();
            int netChange = entry.getValue();

            if (netChange != 0) {
                Ingredient ingredient = ingredientMap.get(ingredientId);
                if (netChange > 0) {
                    trimmed.add(new PendingPizzaOperation(
                            OperationType.ADD_INGREDIENT,
                            pendingOperations.get(0).pizzaId,
                            ingredient,
                            true));
                } else {
                    trimmed.add(new PendingPizzaOperation(
                            OperationType.REMOVE_INGREDIENT,
                            pendingOperations.get(0).pizzaId,
                            ingredient,
                            false));
                }
            }
        }

        return trimmed;
    }

    public void execute() {
        switch (type) {
            case SET_NAME:
                App.databaseManager.changePizzaName(pizzaId, name);
                break;
            case SET_PRICE:
                App.databaseManager.changePizzaPrice(pizzaId, price);
                break;
            case ADD_INGREDIENT:
                App.databaseManager.addIngredientToPizza(pizzaId, ingredient.getId());
                break;
            case REMOVE_INGREDIENT:
                App.databaseManager.removeIngredientFromPizza(pizzaId, ingredient.getId());
                break;
        }
    }
}
