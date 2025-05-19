module CafeProject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens application to javafx.base, javafx.fxml, javafx.graphics;
}