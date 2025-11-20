package com.group6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

/**
 * JavaFX App
 */
public class App extends Application {
    private final static String URL = "jdbc:mysql://localhost:3306/torino_pizza";
    private final static String USER = "root";
    private final static String PASSWORD = System.getenv("LOCAL_DB_PASSWORD");

    public static DatabaseManager databaseManager;

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"), 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        databaseManager = new DatabaseManager(URL, USER, PASSWORD);
        launch();
    }
}