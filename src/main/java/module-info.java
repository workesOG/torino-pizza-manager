module com.group6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;

    opens com.group6 to javafx.fxml;

    exports com.group6;
    exports com.group6.instance;
}
