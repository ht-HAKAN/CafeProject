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
        // Giriş Yap butonunun tıklanması
        GirisYapButton.setOnAction(event -> {
            String kullaniciAdi = KullaniciAdiGiris.getText();
            String sifre = SifreGiris.getText();

            if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
                showAlert("Hata", "Kullanıcı adı veya şifre boş olamaz!", Alert.AlertType.ERROR);
            } else {
                // Admin ve normal kullanıcıyı kontrol et
                if (girisKontrol(kullaniciAdi, sifre, true)) { // Admin kontrolü
                    showAlert("Başarılı", "Admin giriş başarılı!", Alert.AlertType.INFORMATION);
                    anaEkranaGec();
                } else if (girisKontrol(kullaniciAdi, sifre, false)) { // Kullanıcı kontrolü
                    showAlert("Başarılı", "Kullanıcı giriş başarılı!", Alert.AlertType.INFORMATION);
                    kullaniciEkraninaGec();
                } else {
                    showAlert("Hata", "Kullanıcı adı veya şifre hatalı!", Alert.AlertType.ERROR);
                }
            }
        });

        // Misafir olarak devam et butonunun tıklanması
        GirisYapButton1.setOnAction(event -> {
            showAlert("Misafir Girişi", "Misafir olarak devam ediyorsunuz.", Alert.AlertType.INFORMATION);
            // Misafir ekranına yönlendirme 
        });

        // Kayıt Ol butonunun tıklanması
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

    // Kullanıcı adı ve şifreyi kontrol eden fonksiyon (hem admin hem normal kullanıcı)
    private boolean girisKontrol(String kullaniciAdi, String sifre, boolean isAdmin) {
        String query;
        if (isAdmin) {
            query = "SELECT * FROM admins WHERE admin_adi = ? AND admin_sifre = ?";
        } else {
            query = "SELECT * FROM kullanicilar WHERE kullanici_adi = ? AND sifre = ?";
        }

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

    // Admin paneline geçiş
    private void anaEkranaGec() {
        try {
            // Giriş yapan kullanıcının adını doğrudan kullan
            String adminName = KullaniciAdiGiris.getText();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminpanel.fxml"));
            Parent root = loader.load();
            
            // AdminPanelController'a erişim
            AdminPanelController controller = loader.getController();
            
            // Admin adını controller'a aktar
            controller.setAdminName(adminName);
            
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

    // Kullanıcı paneline geçiş
    private void kullaniciEkraninaGec() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KullaniciPanel.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Kullanıcı Paneli");
            stage.setScene(new Scene(root));
            stage.show();

            // Giriş sahnesini kapat
            Stage currentStage = (Stage) GirisYapButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hata veya başarı mesajı gösteren fonksiyon
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
