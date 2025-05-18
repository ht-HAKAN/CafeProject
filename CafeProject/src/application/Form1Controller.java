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
                // Sırasıyla admin, personel ve normal kullanıcı girişini kontrol et
                if (girisKontrol(kullaniciAdi, sifre, "admins", "admin_adi", "admin_sifre")) { 
                    // Admin kontrolü
                    showAlert("Başarılı", "Admin giriş başarılı!", Alert.AlertType.INFORMATION);
                    anaEkranaGec(kullaniciAdi, true); // Admin girişi
                } else if (girisKontrol(kullaniciAdi, sifre, "personel", "kullanici_ad", "sifre")) { 
                    // Personel kontrolü
                    showAlert("Başarılı", "Personel giriş başarılı!", Alert.AlertType.INFORMATION);
                    anaEkranaGec(kullaniciAdi, true); // Personel de admin yetkisine sahip olsun
                } else if (girisKontrol(kullaniciAdi, sifre, "kullanicilar", "kullanici_adi", "sifre")) { 
                    // Normal kullanıcı kontrolü
                    showAlert("Başarılı", "Kullanıcı giriş başarılı!", Alert.AlertType.INFORMATION);
                    kullaniciEkraninaGec(kullaniciAdi);
                } else {
                    showAlert("Hata", "Kullanıcı adı veya şifre hatalı!", Alert.AlertType.ERROR);
                }
            }
        });

        // Misafir olarak devam et butonunun tıklanması
        GirisYapButton1.setOnAction(event -> {
            showAlert("Misafir Girişi", "Misafir olarak devam ediyorsunuz.", Alert.AlertType.INFORMATION);
            misafirEkraninaGec();
        });

        // Kayıt Ol butonunun tıklanması
        KayitOlButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Form1Controller.class.getResource("KullaniciKayit.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Kayıt Ol");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Hata", "Kayıt formu açılamadı: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // Giriş kontrolü - herhangi bir tablo için
    private boolean girisKontrol(String kullaniciAdi, String sifre, String tablo, String kullaniciAdiKolonu, String sifreKolonu) {
        String query = "SELECT * FROM " + tablo + " WHERE " + kullaniciAdiKolonu + " = ? AND " + sifreKolonu + " = ?";

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

    // Admin paneline geçiş - parametreli
    private void anaEkranaGec(String kullaniciAdi, boolean isAdmin) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Form1Controller.class.getResource("AnaSayfa.fxml"));
            Parent root = loader.load();
            
            // AnaSayfaController'a erişim
            AnaSayfaController controller = loader.getController();
            
            // Admin adını ve yetkisini controller'a aktar
            controller.setKullaniciAdi(kullaniciAdi);
            controller.setAdmin(isAdmin);
            
            Stage stage = new Stage();
            stage.setTitle("Ana Sayfa");
            stage.setScene(new Scene(root));
            stage.show();

            // Giriş sahnesini kapat
            Stage currentStage = (Stage) GirisYapButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Ana sayfa açılamadı: " + e.getMessage(), Alert.AlertType.ERROR);
            System.out.println("Hata detayı: " + e.getMessage());
        }
    }

    // Kullanıcı paneline geçiş - parametreli 
    private void kullaniciEkraninaGec(String kullaniciAdi) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Form1Controller.class.getResource("AnaSayfa.fxml"));
            Parent root = loader.load();
            
            // AnaSayfaController'a erişim
            AnaSayfaController controller = loader.getController();
            
            // Kullanıcı adını ve yetkisini controller'a aktar
            controller.setKullaniciAdi(kullaniciAdi);
            controller.setAdmin(false); // Normal kullanıcı, admin değil
            
            Stage stage = new Stage();
            stage.setTitle("Ana Sayfa");
            stage.setScene(new Scene(root));
            stage.show();

            // Giriş sahnesini kapat
            Stage currentStage = (Stage) GirisYapButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Ana sayfa açılamadı: " + e.getMessage(), Alert.AlertType.ERROR);
            System.out.println("Hata detayı: " + e.getMessage());
        }
    }
    
    // Misafir ekranına geçiş
    private void misafirEkraninaGec() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Form1Controller.class.getResource("AnaSayfa.fxml"));
            Parent root = loader.load();
            
            // AnaSayfaController'a erişim
            AnaSayfaController controller = loader.getController();
            
            // Misafir olarak ayarla
            controller.setKullaniciAdi("Misafir");
            controller.setAdmin(false); // Misafir, admin değil
            
            Stage stage = new Stage();
            stage.setTitle("Ana Sayfa - Misafir");
            stage.setScene(new Scene(root));
            stage.show();

            // Giriş sahnesini kapat
            Stage currentStage = (Stage) GirisYapButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Ana sayfa açılamadı: " + e.getMessage(), Alert.AlertType.ERROR);
            System.out.println("Hata detayı: " + e.getMessage());
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
