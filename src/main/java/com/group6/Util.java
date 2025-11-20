package com.group6;

import java.util.ArrayList;
import java.util.List;

import com.group6.instance.DisplayPizza;
import com.group6.instance.Ingredient;
import com.group6.instance.Pizza;

public class Util {
    public static void printIngredientList(List<Ingredient> list) {
        for (Ingredient ingredient : list) {
            System.out.println(ingredient.toString());
        }
    }

    public static void printDisplayPizzaList(List<DisplayPizza> list) {
        for (DisplayPizza displayPizza : list) {
            System.out.println(
                    displayPizza.getName() + " - " + displayPizza.getPrice() + " - " + displayPizza.getIngredients());
        }
    }

    public static List<Integer> interpretLiteralPizzaIngredientList(String ingredientList) {
        List<Integer> ingredientIds = new ArrayList<>();
        String[] ingredientStrings = ingredientList.split(", ");
        for (String ingredientString : ingredientStrings) {
            try {
                ingredientIds.add(Integer.valueOf(ingredientString));
            } catch (NumberFormatException e) {
                System.out.println("Invalid ingredient string: " + ingredientString);
                e.printStackTrace();
            }
        }
        return ingredientIds;
    }
}
