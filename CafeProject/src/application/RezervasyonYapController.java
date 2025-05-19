package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;

public class RezervasyonYapController {
    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField telefonField;
    @FXML private DatePicker tarihPicker;
    @FXML private ComboBox<String> saatComboBox;
    @FXML private TextField kisiSayisiField;
    @FXML private TextField notField;
    @FXML private Button gonderButton;

    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = MySQLConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Saat seçenekleri
        saatComboBox.setItems(FXCollections.observableArrayList(
            Arrays.asList("10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00")
        ));
        gonderButton.setOnAction(e -> rezervasyonTalepEt());
        // Telefon alanı sadece rakam alsın
        telefonField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                telefonField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
        // Kişi sayısı alanı sadece rakam alsın
        kisiSayisiField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                kisiSayisiField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void rezervasyonTalepEt() {
        String ad = adField.getText().trim();
        String soyad = soyadField.getText().trim();
        String telefon = telefonField.getText().trim();
        LocalDate tarih = tarihPicker.getValue();
        String saat = saatComboBox.getValue();
        String kisiSayisiStr = kisiSayisiField.getText().trim();
        String not = notField.getText().trim();

        boolean valid = true;
        // Alanları eski haline döndür
        telefonField.setStyle("-fx-background-color: #2C2C2C; -fx-text-fill: white;");
        kisiSayisiField.setStyle("-fx-background-color: #2C2C2C; -fx-text-fill: white;");

        if (ad.isEmpty() || soyad.isEmpty() || telefon.isEmpty() || tarih == null || saat == null || kisiSayisiStr.isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun!");
            return;
        }
        // Telefon validasyonu
        if (!telefon.matches("\\d{10,11}")) {
            telefonField.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
            showAlert(AlertType.ERROR, "Hata", "Telefon numarası 10 veya 11 haneli olmalı ve sadece rakam içermelidir!");
            valid = false;
        }
        int kisiSayisi = 0;
        try {
            kisiSayisi = Integer.parseInt(kisiSayisiStr);
            if (kisiSayisi <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            kisiSayisiField.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
            showAlert(AlertType.ERROR, "Hata", "Kişi sayısı pozitif bir sayı olmalı!");
            valid = false;
        }
        if (!valid) return;
        try {
            String sql = "INSERT INTO bekleyen_rezervasyonlar (musteri_adi, musteri_soyadi, telefon, tarih, saat, kisi_sayisi, notlar, durum) VALUES (?, ?, ?, ?, ?, ?, ?, 'Beklemede')";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, ad);
            stmt.setString(2, soyad);
            stmt.setString(3, telefon);
            stmt.setDate(4, Date.valueOf(tarih));
            stmt.setString(5, saat);
            stmt.setInt(6, kisiSayisi);
            stmt.setString(7, not);
            stmt.executeUpdate();
            showAlert(AlertType.INFORMATION, "Başarılı", "Rezervasyon talebiniz alınmıştır. En kısa sürede sizinle iletişime geçilecektir.");
            // Alanları temizle
            adField.clear();
            soyadField.clear();
            telefonField.clear();
            tarihPicker.setValue(null);
            saatComboBox.setValue(null);
            kisiSayisiField.clear();
            notField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Hata", "Rezervasyon talebi kaydedilemedi: " + e.getMessage());
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
