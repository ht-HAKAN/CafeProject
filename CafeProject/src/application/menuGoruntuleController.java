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
import javafx.scene.layout.BorderPane;

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
                BorderPane kart = new BorderPane();
                kart.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 16; -fx-padding: 10; -fx-effect: dropshadow(gaussian, #00000055, 8,0,0,2);");
                kart.setPrefWidth(180);
                kart.setPrefHeight(260);

                // Üst kısım: resim veya gri kutu
                VBox ustKisim = new VBox();
                ustKisim.setAlignment(Pos.CENTER);
                ustKisim.setPrefHeight(100);
                ustKisim.setMinHeight(100);
                ustKisim.setMaxHeight(100);
                String resimYolu = rs.getString("resim_yolu");
                if (resimYolu != null && !resimYolu.isEmpty()) {
                    ImageView img = new ImageView(new Image("file:" + resimYolu, 120, 90, true, true));
                    img.setFitWidth(120);
                    img.setFitHeight(90);
                    img.setPreserveRatio(true);
                    ustKisim.getChildren().add(img);
                } else {
                    Rectangle rect = new Rectangle(120, 90, Color.web("#444"));
                    ustKisim.getChildren().add(rect);
                }
                kart.setTop(ustKisim);

                // Alt kısım: yazılar
                VBox altKisim = new VBox(6);
                altKisim.setAlignment(Pos.CENTER);
                Text ad = new Text(rs.getString("ad"));
                ad.setStyle("-fx-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                Text fiyat = new Text(String.format("%.2f ₺", rs.getDouble("fiyat")));
                fiyat.setStyle("-fx-fill: #FFD700; -fx-font-size: 15px; -fx-font-weight: bold;");
                String aciklamaStr = rs.getString("aciklama");
                Text aciklamaText = new Text(aciklamaStr != null ? aciklamaStr : "");
                aciklamaText.setStyle("-fx-fill: #BBBBBB; -fx-font-size: 12px; -fx-font-style: italic;");
                altKisim.getChildren().addAll(ad, fiyat, aciklamaText);
                altKisim.setPrefHeight(80);
                kart.setBottom(altKisim);

                urunGrid.add(kart, col, row);
                col++;
                if (col > 3) { col = 0; row++; }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 