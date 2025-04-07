package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Form1Controller {

    @FXML
    private Button GirisYapButton;

    @FXML
    private Button GirisYapButton1;

    @FXML
    private Button KayitOlButton;  

    @FXML
    private TextField KullaniciAdiGiris;

    @FXML
    private PasswordField SifreGiris;

    @FXML
    void initialize() {
        // Giriş Yap butonuna tıklama işlemi
        GirisYapButton.setOnAction(event -> {
            String kullaniciAdi = KullaniciAdiGiris.getText();
            String sifre = SifreGiris.getText();

            if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
                showAlert("Hata", "Kullanıcı adı veya şifre boş olamaz!", AlertType.ERROR);
            } else {
                //  örnek
                if (kullaniciAdi.equals("admin") && sifre.equals("1234")) {
                    showAlert("Başarılı", "Giriş başarılı!", AlertType.INFORMATION);
                } else {
                    showAlert("Hata", "Kullanıcı adı veya şifre hatalı!", AlertType.ERROR);
                }
            }
        });

        // Misafir Olarak Devam Et butonu tıklama işlemi
        GirisYapButton1.setOnAction(event -> {
            showAlert("Misafir Girişi", "Misafir olarak devam ediyorsunuz.", AlertType.INFORMATION);
        });

        // Kayıt Ol butonuna tıklama işlemi
        KayitOlButton.setOnAction(event -> {
            try {
                // Kullanıcı kayıt ekranına geçiş
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

    // Hata veya bilgi mesajlarının gösterilmesi
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
