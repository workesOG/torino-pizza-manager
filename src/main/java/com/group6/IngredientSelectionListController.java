package com.group6;

import java.util.ArrayList;
import java.util.List;

import com.group6.instance.Ingredient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class IngredientSelectionListController {
    // Ingredients that should not be shown in the list, because they are already in
    // the pizza
    private List<Ingredient> allIngredients;
    private List<Ingredient> ignoreIngredients;

    private EditPizzaController editPizzaController;

    @FXML
    private ListView<Ingredient> ingredientListView;
    @FXML
    private Button selectButton;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        selectButton.disableProperty().bind(ingredientListView.getSelectionModel().selectedItemProperty().isNull());
        allIngredients = App.databaseManager.getAllIngredients();
    }

    public void manualInitialize() {
        updateIngredientListView();
    }

    public void setIgnoreIngredients(List<Ingredient> ignoreIngredients) {
        this.ignoreIngredients = ignoreIngredients;
    }

    public void setEditPizzaController(EditPizzaController editPizzaController) {
        this.editPizzaController = editPizzaController;
    }

    private void updateIngredientListView() {
        List<Ingredient> ingredients = new ArrayList<>(allIngredients);
        ingredients.removeAll(ignoreIngredients);
        ingredientListView.getItems().setAll(ingredients);
    }

    public void onSelectButtonClick() {
        Ingredient ingredient = ingredientListView.getSelectionModel().getSelectedItem();
        if (ingredient != null) {
            editPizzaController.addIngredient(ingredient);
        }
        closeStage();
    }

    public void onCancelButtonClick() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
