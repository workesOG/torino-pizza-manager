package com.group6;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmationScreenController {
    @FXML
    private Label choiceLabel;
    @FXML
    private Button option1Button;
    @FXML
    private Button option2Button;

    private Runnable option1Action;
    private Runnable option2Action;

    @FXML
    public void initializeManual(String choice, String option1, String option2, Runnable option1Action,
            Runnable option2Action) {
        choiceLabel.setText(choice);
        option1Button.setText(option1);
        option2Button.setText(option2);
        this.option1Action = option1Action;
        this.option2Action = option2Action;
    }

    public void onOption1ButtonClick() {
        option1Action.run();
        closeStage();
    }

    public void onOption2ButtonClick() {
        option2Action.run();
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) choiceLabel.getScene().getWindow();
        stage.close();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void openConfirmationWindow(String choice, String option1, String option2, Runnable option1Action,
            Runnable option2Action) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("confirmationScreen.fxml"));
            Parent root = loader.load();

            ConfirmationScreenController controller = loader.getController();
            controller.initializeManual(choice, option1, option2, option1Action, option2Action);

            Stage stage = new Stage();
            stage.setTitle("Confirmation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
