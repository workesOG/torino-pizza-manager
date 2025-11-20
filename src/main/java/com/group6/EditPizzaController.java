package com.group6;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.group6.instance.Ingredient;
import com.group6.instance.LiteralPizza;
import com.group6.instance.PendingPizzaOperation;

public class EditPizzaController {
    private LiteralPizza selectedPizza;
    private List<Ingredient> ingredients;

    private List<PendingPizzaOperation> pendingOperations = new ArrayList<>();

    private MainController mainController;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private ListView<Ingredient> ingredientListView;
    @FXML
    private Button saveChangesButton;
    @FXML
    private Button discardChangesButton;

    @FXML
    private Button addIngredientButton;
    @FXML
    private Button removeIngredientButton;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void manualInitialize() {
        nameTextField.setText(selectedPizza.getName());
        priceTextField.setText(String.valueOf(selectedPizza.getPrice()));
        removeIngredientButton.disableProperty()
                .bind(ingredientListView.getSelectionModel().selectedItemProperty().isNull());
        initializeIngredients();
        updateIngredientListView();
    }

    public void setSelectedPizza(LiteralPizza pizza) {
        selectedPizza = pizza;
    }

    private void initializeIngredients() {
        ingredients = App.databaseManager.getIngredientsFromIdList(selectedPizza.getIngredientIds());
    }

    private void updateIngredientListView() {
        ingredientListView.getItems().clear();
        ingredientListView.getItems().setAll(ingredients);
    }

    public void changeName(String newName) {
        pendingOperations.add(new PendingPizzaOperation(PendingPizzaOperation.OperationType.SET_NAME,
                selectedPizza.getId(), newName));
    }

    public void changePrice(double newPrice) {
        pendingOperations.add(
                new PendingPizzaOperation(PendingPizzaOperation.OperationType.SET_PRICE, selectedPizza.getId(),
                        newPrice));
    }

    public void addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            return;
        }

        pendingOperations.add(
                new PendingPizzaOperation(PendingPizzaOperation.OperationType.ADD_INGREDIENT, selectedPizza.getId(),
                        ingredient, true));

        ingredients.add(ingredient);
        updateIngredientListView();
    }

    public void removeIngredient() {
        Ingredient ingredient = ingredientListView.getSelectionModel().getSelectedItem();

        if (ingredient == null) {
            return;
        }

        pendingOperations.add(
                new PendingPizzaOperation(PendingPizzaOperation.OperationType.REMOVE_INGREDIENT, selectedPizza.getId(),
                        ingredient, false));

        ingredients.remove(ingredient);
        updateIngredientListView();
    }

    public void validateNameInputAndApply() {
        String text = nameTextField.getText();
        if (text.isEmpty()) {
            return;
        }

        if (text.length() > 50) {
            int caretPosition = nameTextField.getCaretPosition();
            nameTextField.setText(text.substring(0, 50));
            nameTextField.positionCaret(caretPosition);
            return;
        }

        changeName(text);
    }

    public void validatePriceInputAndApply() {
        String text = priceTextField.getText();
        if (text.isEmpty()) {
            return;
        }

        StringBuilder filtered = new StringBuilder();
        boolean sawDot = false;

        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                filtered.append(c);
            } else if (c == '.' && !sawDot) {
                filtered.append('.');
                sawDot = true;
            }
        }

        String cleaned = filtered.toString();

        if (cleaned.isEmpty() || cleaned.equals(".")) {
            priceTextField.setText("");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            priceTextField.setText("");
            return;
        }

        if (value > 1000) {
            value = 1000;
            cleaned = "1000";
        }

        int caretPosition = priceTextField.getCaretPosition();
        priceTextField.setText(cleaned);
        priceTextField.positionCaret(caretPosition);

        changePrice(value);
    }

    public void openIngredientSelectionList() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("ingredientSelectionList.fxml"));
            Parent root = loader.load();

            IngredientSelectionListController controller = loader.getController();
            controller.setEditPizzaController(this);
            controller.setIgnoreIngredients(ingredients);
            controller.manualInitialize();

            Stage stage = new Stage();
            stage.setTitle("Ingredient Selection");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAndClose() {
        pendingOperations = PendingPizzaOperation.trimPendingPizzaOperations(pendingOperations);
        for (PendingPizzaOperation operation : pendingOperations) {
            operation.execute();
        }
        mainController.updatePizzaTable();
        closeStage();
    }

    public void discardAndClose() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) discardChangesButton.getScene().getWindow();
        stage.close();
    }
}
