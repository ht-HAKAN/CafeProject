package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class menuYonetController implements Initializable {
    @FXML private GridPane urunGrid;
    @FXML private Button urunEkleButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        urunEkleButton.setOnAction(e -> urunEklePopup());
        urunleriYenile();
    }

    private void urunleriYenile() {
        urunGrid.getChildren().clear();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT * FROM menu_urunler";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int col = 0, row = 0;
            while (rs.next()) {
                BorderPane kart = new BorderPane();
                kart.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 14; -fx-padding: 6; -fx-effect: dropshadow(gaussian, #00000055, 6,0,0,2);");
                kart.setPrefWidth(130);
                kart.setPrefHeight(200);

                // Üst kısım: resim veya gri kutu
                VBox ustKisim = new VBox();
                ustKisim.setAlignment(Pos.CENTER);
                ustKisim.setPrefHeight(70);
                ustKisim.setMinHeight(70);
                ustKisim.setMaxHeight(70);
                String resimYolu = rs.getString("resim_yolu");
                if (resimYolu != null && !resimYolu.isEmpty()) {
                    ImageView img = new ImageView(new Image("file:" + resimYolu, 70, 60, true, true));
                    img.setFitWidth(70);
                    img.setFitHeight(60);
                    img.setPreserveRatio(true);
                    ustKisim.getChildren().add(img);
                } else {
                    Rectangle rect = new Rectangle(70, 60, Color.web("#444"));
                    ustKisim.getChildren().add(rect);
                }
                kart.setTop(ustKisim);

                // Alt kısım: yazılar ve butonlar
                VBox altKisim = new VBox(4);
                altKisim.setAlignment(Pos.CENTER);
                Text ad = new Text(rs.getString("ad"));
                ad.setStyle("-fx-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
                Text fiyat = new Text(String.format("%.2f ₺", rs.getDouble("fiyat")));
                fiyat.setStyle("-fx-fill: #FFD700; -fx-font-size: 11px; -fx-font-weight: bold;");
                String aciklamaStr = rs.getString("aciklama");
                Text aciklamaText = new Text(aciklamaStr != null ? aciklamaStr : "");
                aciklamaText.setStyle("-fx-fill: #BBBBBB; -fx-font-size: 10px; -fx-font-style: italic;");
                final int urunId = rs.getInt("urun_id");
                final String adStr = rs.getString("ad");
                final double fiyatVal = rs.getDouble("fiyat");
                final String resimYoluStr = rs.getString("resim_yolu");
                Button duzenleBtn = new Button("Düzenle");
                duzenleBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6;");
                duzenleBtn.setOnAction(ev -> {
                    try {
                        urunGuncellePopup(urunId, adStr, fiyatVal, resimYoluStr, aciklamaStr);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                Button silBtn = new Button("Sil");
                silBtn.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6;");
                silBtn.setOnAction(ev -> {
                    try {
                        urunSil(urunId);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                altKisim.getChildren().addAll(ad, fiyat, aciklamaText, duzenleBtn, silBtn);
                altKisim.setPrefHeight(90);
                kart.setBottom(altKisim);

                urunGrid.add(kart, col, row);
                col++;
                if (col > 5) { col = 0; row++; }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void urunEklePopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UrunForm.fxml"));
            Parent root = loader.load();
            UrunFormController formController = loader.getController();
            formController.setFormData("", 0.0, null, "", null);
            formController.setListener(new UrunFormController.UrunFormListener() {
                @Override
                public void onKaydet(String ad, double fiyat, String resimYolu, String aciklama, Integer urunId) {
                    try (Connection conn = MySQLConnection.connect()) {
                        String sql = "INSERT INTO menu_urunler (ad, fiyat, resim_yolu, aciklama) VALUES (?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, ad);
                        stmt.setDouble(2, fiyat);
                        stmt.setString(3, resimYolu);
                        stmt.setString(4, aciklama);
                        stmt.executeUpdate();
                        urunleriYenile();
                    } catch (Exception e) {
                        showAlert("Hata", "Ürün eklenemedi: " + e.getMessage(), AlertType.ERROR);
                    }
                }
                @Override
                public void onIptal() {
                    // Gerekirse pencereyi kapatabilirsin, şimdilik boş bırakıldı.
                }
            });
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ürün Ekle");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void urunGuncellePopup(int urunId, String mevcutAd, double mevcutFiyat, String mevcutResim, String mevcutAciklama) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UrunForm.fxml"));
            Parent root = loader.load();
            UrunFormController formController = loader.getController();
            formController.setFormData(mevcutAd, mevcutFiyat, mevcutResim, mevcutAciklama, urunId);
            formController.setListener(new UrunFormController.UrunFormListener() {
                @Override
                public void onKaydet(String ad, double fiyat, String resimYolu, String aciklama, Integer guncelUrunId) {
                    try (Connection conn = MySQLConnection.connect()) {
                        String sql = "UPDATE menu_urunler SET ad=?, fiyat=?, resim_yolu=?, aciklama=? WHERE urun_id=?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, ad);
                        stmt.setDouble(2, fiyat);
                        stmt.setString(3, resimYolu);
                        stmt.setString(4, aciklama);
                        stmt.setInt(5, guncelUrunId);
                        stmt.executeUpdate();
                        urunleriYenile();
                    } catch (Exception e) {
                        showAlert("Hata", "Ürün güncellenemedi: " + e.getMessage(), AlertType.ERROR);
                    }
                }
                @Override
                public void onIptal() {}
            });
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ürün Güncelle");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void urunSil(int urunId) {
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "DELETE FROM menu_urunler WHERE urun_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, urunId);
            stmt.executeUpdate();
            urunleriYenile();
        } catch (Exception e) {
            showAlert("Hata", "Ürün silinemedi: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 