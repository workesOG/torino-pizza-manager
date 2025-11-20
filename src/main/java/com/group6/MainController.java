package com.group6;

import java.io.IOException;
import java.util.List;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.group6.instance.DisplayPizza;
import com.group6.instance.LiteralPizza;

public class MainController {
    @FXML
    private TableView<DisplayPizza> pizzaTableView;
    @FXML
    private Button editPizzaButton;

    @FXML
    private TableColumn<DisplayPizza, Integer> idColumn;
    @FXML
    private TableColumn<DisplayPizza, String> nameColumn;
    @FXML
    private TableColumn<DisplayPizza, Double> priceColumn;
    @FXML
    private TableColumn<DisplayPizza, String> toppingsColumn;

    @FXML
    private void populatePizzaTable() {
        List<DisplayPizza> pizzas = App.databaseManager.getAllPizzas();
        pizzaTableView.getItems().setAll(pizzas);
    }

    @FXML
    public void initialize() {
        initializePizzaTable();
        initializeEditPizzaButton();
    }

    @FXML
    private void onEditPizzaButtonClick() {
        DisplayPizza selectedPizza = pizzaTableView.getSelectionModel().getSelectedItem();
        if (selectedPizza == null) {
            return;
        }

        LiteralPizza literalPizza = App.databaseManager.getLiteralPizzaByDisplayPizza(selectedPizza);

        openEditPizzaWindow(literalPizza);
    }

    private void initializePizzaTable() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        toppingsColumn.setCellValueFactory(cellData -> cellData.getValue().ingredientsProperty());

        pizzaTableView.setRowFactory(tableView -> {
            TableRow<DisplayPizza> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEditing()) {
                    DisplayPizza clickedPizza = row.getItem();
                    System.out.println("Clicked pizza: " + clickedPizza.getName());
                }
            });
            return row;
        });

        populatePizzaTable();
    }

    private void initializeEditPizzaButton() {
        editPizzaButton.disableProperty().bind(pizzaTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void openEditPizzaWindow(LiteralPizza pizza) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("editPizza.fxml"));
            Parent root = loader.load();

            EditPizzaController controller = loader.getController();
            controller.setSelectedPizza(pizza);
            controller.manualInitialize();

            Stage stage = new Stage();
            stage.setTitle("Edit Pizza: " + pizza.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
