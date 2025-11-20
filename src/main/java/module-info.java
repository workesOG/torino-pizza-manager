module com.group6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.group6 to javafx.fxml;

    exports com.group6;
}
