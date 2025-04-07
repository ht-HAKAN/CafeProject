package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Form1Controller {

    @FXML
    private Button GirisYapButton;

    @FXML
    private Button GirisYapButton1;

    @FXML
    private TextField KullaniciAdiGiris;

    @FXML
    private PasswordField SifreGiris;

    @FXML
    void initialize() {
        GirisYapButton.setOnAction(event -> {
            String kullaniciAdi = KullaniciAdiGiris.getText();
            String sifre = SifreGiris.getText();

            if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
                showAlert("Hata", "Kullanıcı adı veya şifre boş olamaz!", AlertType.ERROR);
            } else {
                if (girisKontrol(kullaniciAdi, sifre)) {
                    showAlert("Başarılı", "Giriş başarılı!", AlertType.INFORMATION);
                } else {
                    showAlert("Hata", "Kullanıcı adı veya şifre hatalı!", AlertType.ERROR);
                }
            }
        });

        GirisYapButton1.setOnAction(event -> {
            showAlert("Misafir Girişi", "Misafir olarak devam ediyorsunuz.", AlertType.INFORMATION);
        });
    }

    private boolean girisKontrol(String kullaniciAdi, String sifre) {
        try {
            Connection conn = MySQLConnection.connect();
            // SQL SORGULAMA 
            String sql = "SELECT * FROM adminler WHERE kullanici_adi = ? AND sifre = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kullaniciAdi);
            stmt.setString(2, sifre);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // eşleşme varsa true döner

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
