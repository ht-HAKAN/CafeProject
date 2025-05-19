package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;

public class menuGoruntuleController implements Initializable {
    @FXML private GridPane urunGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        urunGrid.getChildren().clear();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT * FROM menu_urunler";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int col = 0, row = 0;
            while (rs.next()) {
                VBox kart = new VBox(10);
                kart.setAlignment(Pos.CENTER);
                kart.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 16; -fx-padding: 16; -fx-effect: dropshadow(gaussian, #00000055, 8,0,0,2);");
                kart.setPrefWidth(180);
                kart.setPrefHeight(220);

                String resimYolu = rs.getString("resim_yolu");
                ImageView img = new ImageView();
                if (resimYolu != null && !resimYolu.isEmpty()) {
                    img.setImage(new Image("file:" + resimYolu, 120, 90, true, true));
                } else {
                    Rectangle rect = new Rectangle(120, 90, Color.web("#444"));
                    kart.getChildren().add(rect);
                }
                img.setFitWidth(120);
                img.setFitHeight(90);
                img.setPreserveRatio(true);
                kart.getChildren().add(img);

                Text ad = new Text(rs.getString("ad"));
                ad.setStyle("-fx-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                Text fiyat = new Text(String.format("%.2f ₺", rs.getDouble("fiyat")));
                fiyat.setStyle("-fx-fill: #FFD700; -fx-font-size: 15px; -fx-font-weight: bold;");
                String aciklamaStr = rs.getString("aciklama");
                Text aciklamaText = new Text(aciklamaStr != null ? aciklamaStr : "");
                aciklamaText.setStyle("-fx-fill: #CCCCCC; -fx-font-size: 13px; -fx-font-style: italic; -fx-wrap-text: true;");
                kart.getChildren().addAll(ad, fiyat, aciklamaText);

                urunGrid.add(kart, col, row);
                col++;
                if (col > 3) { col = 0; row++; }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 