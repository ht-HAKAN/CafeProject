package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KullaniciKayitController {

    @FXML
    private TextField KullaniciAdiKayit;

    @FXML
    private PasswordField SifreKayit;

    @FXML
    private PasswordField SifreOnayKayit;

    @FXML
    private Button KayitOlButton;

    @FXML
    void initialize() {
        KayitOlButton.setOnAction(event -> {
            String kullaniciAdi = KullaniciAdiKayit.getText();
            String sifre = SifreKayit.getText();
            String sifreOnay = SifreOnayKayit.getText();

            // Şifrelerin uyuşup uyuşmadığını kontrol et
            if (kullaniciAdi.isEmpty() || sifre.isEmpty() || sifreOnay.isEmpty()) {
                showAlert("Hata", "Tüm alanları doldurun!", AlertType.ERROR);
            } else if (!sifre.equals(sifreOnay)) {
                showAlert("Hata", "Şifreler uyuşmuyor!", AlertType.ERROR);
            } else {
                try {
                    // Veritabanına kaydetme
                    Connection connection = MySQLConnection.connect();
                    String query = "INSERT INTO kullanicilar (kullanici_adi, sifre) VALUES (?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, kullaniciAdi);
                    statement.setString(2, sifre);
                    statement.executeUpdate();

                    showAlert("Başarılı", "Kayıt başarılı! Giriş yapabilirsiniz.", AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Hata", "Veritabanı hatası oluştu!", AlertType.ERROR);
                }
            }
        });
    }

    // Hata veya bilgi mesajlarının gösterilmesi
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
