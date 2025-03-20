package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // FXML dosyasını yükleme
            AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Form.fxml"));
            Scene scene = new Scene(root, 400, 400);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Kullanıcı adı TEXTFIELD ve şifre PASSWORD_FIELD
            TextField kullaniciAdi = (TextField) root.lookup("#KullaniciAdiGiris");
            TextField sifre = (TextField) root.lookup("#SifreGiris");

            // Giriş butonuna tıklama işlemi
            root.lookup("#GirisYapButton").setOnMouseClicked(e -> {
                String kullaniciAd = kullaniciAdi.getText();
                String sifreAd = sifre.getText();
                
                if (kullaniciAd.isEmpty() || sifreAd.isEmpty()) {
                    // Kullanıcı adı veya şifre boşsa hata mesajı
                    showAlert("Hata", "Kullanıcı adı ve şifre boş olamaz!", AlertType.ERROR);
                } else {
                    // PHP'ye veri gönderme işlemi
                    if (checkLoginWithPHP(kullaniciAd, sifreAd)) {
                        showAlert("Başarılı", "Giriş başarılı!", AlertType.INFORMATION);
                    } else {
                        showAlert("Hata", "Giriş bilgileri yanlış!", AlertType.ERROR);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // PHP'ye istek göndermek için method
    private boolean checkLoginWithPHP(String kullaniciAd, String sifreAd) {
        try {
            // PHP dosyasının URL'si (sunucunuzda olacak)
            URL url = new URL("http://localhost/CafeProject/giris.php");

            // URL'yi açma
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Parametreleri göndermek için
            String urlParameters = "kullaniciadi=" + URLEncoder.encode(kullaniciAd, "UTF-8")
                                + "&sifre=" + URLEncoder.encode(sifreAd, "UTF-8");

            // İstek verisini göndermek
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(urlParameters);
            writer.flush();
            writer.close();

            // Yanıtı al
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // PHP'den dönen yanıtı kontrol et
            return response.toString().equals("success");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kullanıcıya alert gösterme methodu
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
