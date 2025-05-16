package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AnaSayfaController {

    @FXML private Text kullaniciWelcomeText;
    @FXML private Button anasayfa;
    @FXML private Button menu;
    @FXML private Button siparisler;
    @FXML private Button masalarverezervasyon;
    @FXML private Button adminpanel;

    private String kullaniciAdi = "Kullanıcı";
    private boolean isAdmin = false;

    public void initialize() {
        // Button click olaylarını ayarla
        anasayfa.setOnAction(event -> {
            // Zaten ana sayfadayız, bir şey yapmaya gerek yok
        });

        menu.setOnAction(event -> {
            sayfaAc("menu.fxml", "Menü");
        });

        siparisler.setOnAction(event -> {
            sayfaAc("siparis.fxml", "Siparişler");
        });

        masalarverezervasyon.setOnAction(event -> {
            sayfaAc("masaverezervasyon.fxml", "Masa ve Rezervasyon");
        });

        adminpanel.setOnAction(event -> {
            if (isAdmin) {
                sayfaAc("adminpanel.fxml", "Admin Panel");
            } else {
                // Admin yetkisi yoksa butonu gizle veya devre dışı bırak
                adminpanel.setVisible(false);
                adminpanel.setDisable(true);
            }
        });

        // Admin değilse admin panel butonunu gizle
        if (!isAdmin) {
            adminpanel.setVisible(false);
            adminpanel.setDisable(true);
        }
    }

    // Kullanıcı adını ayarla
    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        updateWelcomeText();
    }

    // Admin yetkisi ayarla
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        adminpanel.setVisible(isAdmin);
        adminpanel.setDisable(!isAdmin);
    }

    // Karşılama metnini güncelle
    private void updateWelcomeText() {
        if (kullaniciWelcomeText != null) {
            kullaniciWelcomeText.setText("Merhaba, " + kullaniciAdi + "!");
        }
    }

    // Sayfa açma yardımcı fonksiyonu
    private void sayfaAc(String fxmlDosya, String baslik) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AnaSayfaController.class.getResource(fxmlDosya));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(baslik);
            stage.setScene(new Scene(root));
            stage.show();
            
            // Mevcut sayfayı kapat
            Stage currentStage = (Stage) anasayfa.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Hata: " + e.getMessage() + " - Dosya yolu: " + fxmlDosya);
        }
    }
}
