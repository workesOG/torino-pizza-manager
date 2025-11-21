package com.group6;

import java.io.IOException;
import java.util.List;

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

// Controller class for the main screen
public class MainController {
    @FXML
    private TableView<DisplayPizza> pizzaTableView;
    @FXML
    private Button editPizzaButton;
    @FXML
    private Button removePizzaButton;

    @FXML
    private TableColumn<DisplayPizza, Integer> idColumn;
    @FXML
    private TableColumn<DisplayPizza, String> nameColumn;
    @FXML
    private TableColumn<DisplayPizza, Double> priceColumn;
    @FXML
    private TableColumn<DisplayPizza, String> toppingsColumn;

    @FXML
    public void initialize() {
        initializePizzaTable();
        updatePizzaTable();
        initializeButtons();
    }

    public void onEditPizzaButtonClick() {
        DisplayPizza selectedPizza = pizzaTableView.getSelectionModel().getSelectedItem();
        if (selectedPizza == null) {
            return;
        }

        LiteralPizza literalPizza = App.databaseManager.getLiteralPizzaByDisplayPizza(selectedPizza);

        openEditPizzaWindow(literalPizza);
    }

    public void onAddPizzaButtonClick() {
        openCreatePizzaWindow();
    }

    public void onRemovePizzaButtonClick() {
        LiteralPizza selectedPizza = App.databaseManager
                .getLiteralPizzaByDisplayPizza(pizzaTableView.getSelectionModel().getSelectedItem());
        ConfirmationScreenController.openConfirmationWindow(
                String.format("Remove Pizza '%s'?", selectedPizza.getName()), "Confirm",
                "Cancel", () -> {
                    App.databaseManager.removePizza(selectedPizza.getId());
                    updatePizzaTable();
                }, () -> {

                });
    }

    public void updatePizzaTable() {
        List<DisplayPizza> pizzas = App.databaseManager.getAllPizzas();
        pizzaTableView.getItems().setAll(pizzas);
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
    }

    private void initializeButtons() {
        editPizzaButton.disableProperty().bind(pizzaTableView.getSelectionModel().selectedItemProperty().isNull());
        removePizzaButton.disableProperty().bind(pizzaTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void openEditPizzaWindow(LiteralPizza pizza) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("editOrCreatePizza.fxml"));
            Parent root = loader.load();

            EditOrCreatePizzaController controller = loader.getController();
            controller.setMainController(this);
            controller.manualInitialize(pizza);

            Stage stage = new Stage();
            stage.setTitle("Edit Pizza: " + pizza.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void openCreatePizzaWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("editOrCreatePizza.fxml"));
            Parent root = loader.load();

            EditOrCreatePizzaController controller = loader.getController();
            controller.setMainController(this);
            controller.manualInitialize();

            Stage stage = new Stage();
            stage.setTitle("Create new pizza");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
