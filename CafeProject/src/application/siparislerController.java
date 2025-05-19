package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;

public class siparislerController implements Initializable {
    @FXML private GridPane masaGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        masaGrid.getChildren().clear();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT * FROM masalar";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int col = 0, row = 0;
            while (rs.next()) {
                VBox kart = new VBox(10);
                kart.setAlignment(Pos.CENTER);
                kart.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 16; -fx-padding: 16; -fx-effect: dropshadow(gaussian, #00000055, 8,0,0,2);");
                kart.setPrefWidth(120);
                kart.setPrefHeight(120);
                String masaAdi = rs.getString("masa_adi");
                String durum = rs.getString("durum");
                Color renk;
                switch (durum) {
                    case "DOLU": renk = Color.web("#4CAF50"); break;
                    case "KIRLI": renk = Color.web("#FF6347"); break;
                    default: renk = Color.web("#888"); break;
                }
                Rectangle durumRect = new Rectangle(40, 40, renk);
                durumRect.setArcWidth(12);
                durumRect.setArcHeight(12);
                Text masaText = new Text(masaAdi);
                masaText.setStyle("-fx-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                kart.getChildren().addAll(durumRect, masaText);
                masaGrid.add(kart, col, row);
                col++;
                if (col > 5) { col = 0; row++; }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 