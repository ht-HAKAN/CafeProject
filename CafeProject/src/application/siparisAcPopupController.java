package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class siparisAcPopupController implements Initializable {
    @FXML private ComboBox<String> urunCombo;
    @FXML private TextField miktarField;
    @FXML private Button ekleButton;
    @FXML private Button iptalButton;

    private int masaId = -1;
    public void setMasaId(int masaId) { this.masaId = masaId; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ürünleri ComboBoxa yükle
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT ad FROM menu_urunler";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                urunCombo.getItems().add(rs.getString("ad"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ekleButton.setOnAction(e -> {
            String urun = urunCombo.getValue();
            String miktarStr = miktarField.getText();
            if (urun == null || urun.isEmpty() || miktarStr == null || miktarStr.isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING, "Lütfen ürün ve miktar giriniz.");
                alert.showAndWait();
                return;
            }
            int miktar;
            try { miktar = Integer.parseInt(miktarStr); } catch (Exception ex) {
                Alert alert = new Alert(AlertType.WARNING, "Miktar sayısal olmalı.");
                alert.showAndWait();
                return;
            }
            try (Connection conn = MySQLConnection.connect()) {
                // 1. Ürün id'sini bul
                String urunIdQuery = "SELECT urun_id FROM menu_urunler WHERE ad = ?";
                PreparedStatement stmt2 = conn.prepareStatement(urunIdQuery);
                stmt2.setString(1, urun);
                ResultSet rs2 = stmt2.executeQuery();
                int urunId = -1;
                if (rs2.next()) urunId = rs2.getInt(1);
                // 2. Sipariş ekle
                String insertSiparis = "INSERT INTO siparisler (masa_id, urun_id, adet, durum) VALUES (?, ?, ?, 'aktif')";
                PreparedStatement stmt = conn.prepareStatement(insertSiparis);
                stmt.setInt(1, masaId);
                stmt.setInt(2, urunId);
                stmt.setInt(3, miktar);
                stmt.executeUpdate();
                // 3. Masa durumunu güncelle
                String updateMasa = "UPDATE masalar SET durum = 'dolu' WHERE masa_id = ?";
                PreparedStatement stmt4 = conn.prepareStatement(updateMasa);
                stmt4.setInt(1, masaId);
                stmt4.executeUpdate();
                ekleButton.getScene().getWindow().hide();
            } catch (Exception ex) {
                Alert alert = new Alert(AlertType.ERROR, "Sipariş eklenemedi: " + ex.getMessage());
                alert.showAndWait();
            }
        });
        iptalButton.setOnAction(e -> {
            ekleButton.getScene().getWindow().hide();
        });
    }
} 