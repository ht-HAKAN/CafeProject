package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Form1Controller {

    @FXML private Button GirisYapButton;
    @FXML private Button GirisYapButton1;
    @FXML private Button KayitOlButton;
    @FXML private TextField KullaniciAdiGiris;
    @FXML private PasswordField SifreGiris;

    @FXML
    void initialize() {
        GirisYapButton.setOnAction(event -> {
            String kullaniciAdi = KullaniciAdiGiris.getText();
            String sifre = SifreGiris.getText();

            if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
                showAlert("Hata", "Kullanıcı adı veya şifre boş olamaz!", Alert.AlertType.ERROR);
            } else {
                if (girisKontrol(kullaniciAdi, sifre)) {
                    showAlert("Başarılı", "Giriş başarılı!", Alert.AlertType.INFORMATION);
                    anaEkranaGec();
                } else {
                    showAlert("Hata", "Kullanıcı adı veya şifre hatalı!", Alert.AlertType.ERROR);
                }
            }
        });

        GirisYapButton1.setOnAction(event -> {
            showAlert("Misafir Girişi", "Misafir olarak devam ediyorsunuz.", Alert.AlertType.INFORMATION);
            // Misafir ekranına yönlendirme 
        });

        KayitOlButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("KullaniciKayit.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Kayıt Ol");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean girisKontrol(String kullaniciAdi, String sifre) {
        String query = "SELECT * FROM admins WHERE admin_adi = ? AND admin_sifre = ?";
        try (Connection conn = MySQLConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, kullaniciAdi);
            stmt.setString(2, sifre);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private void anaEkranaGec() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminPanel.fxml")); 
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Admin Panel");
            stage.setScene(new Scene(root));
            stage.show();

            // Giriş sahnesini kapat
            Stage currentStage = (Stage) GirisYapButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
