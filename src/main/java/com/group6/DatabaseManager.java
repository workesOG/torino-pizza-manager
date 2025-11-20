package com.group6;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.group6.instance.LiteralPizza;
import com.group6.instance.DisplayPizza;
import com.group6.instance.Ingredient;

public class DatabaseManager {
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public DatabaseManager(String url, String user, String password) {
        URL = url;
        USER = user;
        PASSWORD = password;
    }

    private Connection establishConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DisplayPizza> getAllPizzas() {
        Connection conn = establishConnection();

        List<DisplayPizza> pizzas = new ArrayList<>();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL listDisplayMenu()}");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pizzas.add(new DisplayPizza(rs.getInt("pizza_id"), rs.getString("pizza_name"),
                        rs.getDouble("pizza_price"), rs.getString("ingredients")));
            }
        } catch (Exception e) {
        }
        return pizzas;

    }

    public LiteralPizza getPizzaById(int id) {
        Connection conn = establishConnection();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL getPizzaDetails(?)}");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new LiteralPizza(rs.getInt("pizza_id"), rs.getString("pizza_name"),
                        rs.getDouble("pizza_price"),
                        Util.interpretLiteralPizzaIngredientList(rs.getString("ingredient_id_list")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public LiteralPizza getLiteralPizzaByDisplayPizza(DisplayPizza displayPizza) {
        return getPizzaById(displayPizza.getId());
    }

    public Ingredient getIngredientById(int ingredientId) {
        Connection conn = establishConnection();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL getIngredientDetails(?)}");
            stmt.setInt(1, ingredientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("ingredient_name"),
                        rs.getString("ingredient_desc"),
                        rs.getDouble("ingredient_price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public List<Ingredient> getIngredientsFromIdList(List<Integer> ingredientIds) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (Integer ingredientId : ingredientIds) {
            Ingredient ingredient = getIngredientById(ingredientId);
            if (ingredient != null) {
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    public List<Ingredient> getAllIngredients() {
        Connection conn = establishConnection();
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL listIngredients()}");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getInt("ingredient_id"), rs.getString("ingredient_name"),
                        rs.getString("ingredient_desc"), rs.getDouble("ingredient_price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return ingredients;
    }
    
    public void changePizzaName(int pizzaId, String name) {
        Connection conn = establishConnection();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL changePizzaName(?, ?)}");
            stmt.setInt(1, pizzaId);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changePizzaPrice(int pizzaId, double price) {
        Connection conn = establishConnection();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL changePizzaPrice(?, ?)}");
            stmt.setInt(1, pizzaId);
            stmt.setDouble(2, price);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addIngredientToPizza(int pizzaId, int ingredientId) {
        Connection conn = establishConnection();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL addPizzaIngredient(?, ?)}");
            stmt.setInt(1, pizzaId);
            stmt.setInt(2, ingredientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeIngredientFromPizza(int pizzaId, int ingredientId) {
        Connection conn = establishConnection();
        try {
            CallableStatement stmt = conn.prepareCall("{CALL removePizzaIngredient(?, ?)}");
            stmt.setInt(1, pizzaId);
            stmt.setInt(2, ingredientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
