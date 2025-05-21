module CafeProject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

     // gerekli kütüphaneleri ekle
    opens application to javafx.base, javafx.fxml, javafx.graphics;
}