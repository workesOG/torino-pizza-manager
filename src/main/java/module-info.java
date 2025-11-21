module com.group6 {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive java.sql;
    requires transitive javafx.base;
    requires transitive javafx.graphics;

    opens com.group6 to javafx.fxml;

    exports com.group6;
    exports com.group6.instance;
}
