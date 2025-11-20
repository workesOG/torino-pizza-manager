package com.group6;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

import com.group6.instance.Ingredient;
import com.group6.instance.LiteralPizza;

public class EditPizzaController {
    private LiteralPizza selectedPizza;
    private List<Ingredient> ingredients;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private ListView<Ingredient> ingredientListView;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    public void manualInitialize() {
        nameTextField.setText(selectedPizza.getName());
        priceTextField.setText(String.valueOf(selectedPizza.getPrice()));
        initializeIngredients();
    }

    public void setSelectedPizza(LiteralPizza pizza) {
        selectedPizza = pizza;
    }

    private void initializeIngredients() {
        ingredients = App.databaseManager.getIngredientsFromIdList(selectedPizza.getIngredientIds());
        ingredientListView.getItems().setAll(ingredients);
    }
}
