package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;

public class siparisEklePopupController implements Initializable {
    @FXML private ComboBox<String> urunComboBox;
    @FXML private TextField miktarField;
    @FXML private Button ekleButton;
    @FXML private Button iptalButton;

    private int masaId;

    public void setMasaId(int masaId) {
        this.masaId = masaId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ürünleri ComboBox'a yükle
        try {
            Connection conn = MySQLConnection.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT urun_adi FROM menu");
            while (rs.next()) {
                urunComboBox.getItems().add(rs.getString("urun_adi"));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ekleButton.setOnAction(e -> siparisEkle());
        iptalButton.setOnAction(e -> ((Stage) iptalButton.getScene().getWindow()).close());
    }

    private void siparisEkle() {
        String urun = urunComboBox.getValue();
        String miktarStr = miktarField.getText();
        if (urun == null || urun.isEmpty() || miktarStr == null || miktarStr.isEmpty()) {
            showAlert("Hata", "Lütfen ürün ve miktar girin!", AlertType.ERROR);
            return;
        }
        int miktar;
        try {
            miktar = Integer.parseInt(miktarStr);
        } catch (NumberFormatException ex) {
            showAlert("Hata", "Miktar sayısal olmalı!", AlertType.ERROR);
            return;
        }
        try {
            Connection conn = MySQLConnection.connect();
            // Ürün id ve fiyatını bul
            PreparedStatement urunStmt = conn.prepareStatement("SELECT urun_id, fiyat FROM menu WHERE urun_adi = ?");
            urunStmt.setString(1, urun);
            ResultSet urunRS = urunStmt.executeQuery();
            if (!urunRS.next()) {
                showAlert("Hata", "Ürün bulunamadı!", AlertType.ERROR);
                return;
            }
            int urunId = urunRS.getInt("urun_id");
            double fiyat = urunRS.getDouble("fiyat");
            urunRS.close();
            urunStmt.close();
            // Siparişi ekle
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO siparisler (masa_id, urun_id, miktar, toplam_tutar, durum) VALUES (?, ?, ?, ?, 'aktif')");
            stmt.setInt(1, masaId);
            stmt.setInt(2, urunId);
            stmt.setInt(3, miktar);
            stmt.setDouble(4, fiyat * miktar);
            stmt.executeUpdate();
            stmt.close();
            // Masayı dolu yap
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'dolu' WHERE masa_id = ?");
            masaStmt.setInt(1, masaId);
            masaStmt.executeUpdate();
            masaStmt.close();
            conn.close();
            showAlert("Başarılı", "Sipariş eklendi!", AlertType.INFORMATION);
            ((Stage) ekleButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Sipariş eklenemedi!", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 