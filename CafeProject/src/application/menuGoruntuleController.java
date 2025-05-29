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
import javafx.scene.layout.FlowPane;

public class menuGoruntuleController implements Initializable {
    @FXML private FlowPane urunFlow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        urunFlow.getChildren().clear();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT * FROM menu_urunler";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BorderPane kart = new BorderPane();
                kart.setStyle("-fx-background-color: #292929; -fx-background-radius: 18; -fx-padding: 10; -fx-effect: dropshadow(gaussian, #00000099, 12,0,0,4);");
                kart.setPrefWidth(180);
                kart.setPrefHeight(140);

                // Üst kısım: resim veya gri kutu
                VBox ustKisim = new VBox();
                ustKisim.setAlignment(Pos.CENTER);
                ustKisim.setPrefHeight(48);
                ustKisim.setMinHeight(48);
                ustKisim.setMaxHeight(48);
                String resimYolu = rs.getString("resim_yolu");
                if (resimYolu != null && !resimYolu.isEmpty()) {
                    ImageView img = new ImageView(new Image("file:" + resimYolu, 60, 48, true, true));
                    img.setFitWidth(60);
                    img.setFitHeight(48);
                    img.setPreserveRatio(true);
                    ustKisim.getChildren().add(img);
                } else {
                    Rectangle rect = new Rectangle(60, 48, Color.web("#444"));
                    rect.setArcWidth(12);
                    rect.setArcHeight(12);
                    ustKisim.getChildren().add(rect);
                }
                kart.setTop(ustKisim);

                // Alt kısım
                VBox altKisim = new VBox(2);
                altKisim.setAlignment(Pos.CENTER);
                // Ürün adı ve fiyatı aynı satırda
                HBox adFiyatBox = new HBox(4);
                adFiyatBox.setAlignment(Pos.CENTER);
                Text ad = new Text(rs.getString("ad"));
                ad.setStyle("-fx-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
                Text fiyat = new Text(String.format("%.2f ₺", rs.getDouble("fiyat")));
                fiyat.setStyle("-fx-fill: #FFD700; -fx-font-size: 15px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #000, 2,0,0,1);");
                adFiyatBox.getChildren().addAll(ad, fiyat);
                // Açıklama
                String aciklamaStr = rs.getString("aciklama");
                String aciklamaKisa = aciklamaStr != null && aciklamaStr.length() > 48 ? aciklamaStr.substring(0, 45) + "..." : aciklamaStr;
                Text aciklama = new Text(aciklamaKisa != null ? aciklamaKisa : "");
                aciklama.setStyle("-fx-fill: #CCCCCC; -fx-font-size: 11px; -fx-text-alignment: center;");
                aciklama.setWrappingWidth(150);
                altKisim.getChildren().addAll(adFiyatBox, aciklama);
                kart.setCenter(altKisim);

                urunFlow.getChildren().add(kart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 