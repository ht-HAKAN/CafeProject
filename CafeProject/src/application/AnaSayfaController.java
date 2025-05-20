package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AnaSayfaController {

    @FXML private Text kullaniciWelcomeText;
    @FXML private Button anasayfa;
    @FXML private Button menu;
    @FXML private Button masalarverezervasyon;
    @FXML private Button adminpanel;
    @FXML private AnchorPane rootPane;

    private String kullaniciAdi = "Kullanıcı";
    private boolean isAdmin = false;

    public void initialize() {
        anasayfa.setOnAction(event -> sayfaAc("AnaSayfa.fxml", "Ana Sayfa"));
        menu.setOnAction(event -> sayfaAc("menuGoruntule.fxml", "Menüyü Görüntüle"));
        masalarverezervasyon.setOnAction(event -> sayfaAc("masaverezervasyon.fxml", "Masa ve Rezervasyon"));
        adminpanel.setOnAction(event -> {
            if (isAdmin) {
                sayfaAc("adminpanel.fxml", "Admin Panel");
            } else {
                showAlert("Yetki Hatası", "Bu sekmeyi görüntülemek için yetkiniz yok!", AlertType.WARNING);
            }
        });
    }

    // Kullanıcı adını ayarla
    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        updateWelcomeText();
    }

    // Admin yetkisi ayarla
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        
        // Admin panel butonunu güncelle
        updateAdminPanelButton();
        
        // Scene hazır olduğunda rezervasyon butonunu güncelle
        updateReservationButton();
    }

    // Admin panel butonunu güncelleme
    private void updateAdminPanelButton() {
        if (adminpanel != null) {
            if (!isAdmin) {
                // Admin değilse butonu devre dışı bırak ve gri yap
                adminpanel.setDisable(true);
                adminpanel.setStyle("-fx-background-color: #404040; -fx-text-fill: #808080; -fx-opacity: 0.7;");
                
                // Tooltip ekle
                javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip("Bu özelliği kullanmak için admin yetkisine sahip olmanız gerekiyor.");
                tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #2D2D2D; -fx-text-fill: white;");
                adminpanel.setTooltip(tooltip);
            } else {
                // Admin ise normal stil
                adminpanel.setDisable(false);
                adminpanel.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: white;");
            }
        }
    }

    // Rezervasyon butonunu güncelleme
    private void updateReservationButton() {
        if (masalarverezervasyon != null && masalarverezervasyon.getScene() != null) {
            try {
                Button rezervasyonSistemiBtn = (Button) masalarverezervasyon.getScene().lookup("#rezervasyonSistemiBtn");
                if (rezervasyonSistemiBtn != null) {
                    if (!isAdmin) {
                        rezervasyonSistemiBtn.setDisable(true);
                        rezervasyonSistemiBtn.setStyle("-fx-background-color: #404040; -fx-text-fill: #808080; -fx-opacity: 0.7;");
                        
                        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip("Bu özelliği kullanmak için admin yetkisine sahip olmanız gerekiyor.");
                        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #2D2D2D; -fx-text-fill: white;");
                        rezervasyonSistemiBtn.setTooltip(tooltip);
                    } else {
                        rezervasyonSistemiBtn.setDisable(false);
                        rezervasyonSistemiBtn.setStyle("-fx-background-color: #2C2C2C; -fx-border-color: #FFD700; -fx-border-radius: 5; -fx-text-fill: white;");
                    }
                }
            } catch (Exception e) {
                // Hata durumunda sessizce devam et
                System.out.println("Rezervasyon butonu güncellenirken hata: " + e.getMessage());
            }
        }
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
            if (fxmlDosya.equals("siparisler.fxml") || fxmlDosya.equals("siparis.fxml")) {
                fxmlDosya = "siparislerAdmin.fxml";
            }
            loader.setLocation(AnaSayfaController.class.getResource(fxmlDosya));
            Parent root = loader.load();
            
            // Controller'a yetki ve kullanıcı bilgilerini aktar
            Object controller = loader.getController();
            if (controller instanceof masaverezervasyonController) {
                ((masaverezervasyonController) controller).setKullaniciAdi(kullaniciAdi);
                ((masaverezervasyonController) controller).setAdmin(isAdmin);
            } else if (controller instanceof AdminPanelController) {
                ((AdminPanelController) controller).setAdminName(kullaniciAdi);
                ((AdminPanelController) controller).setAdmin(isAdmin);
            } else if (controller instanceof menuController) {
                ((menuController) controller).setKullaniciAdi(kullaniciAdi);
                ((menuController) controller).setAdmin(isAdmin);
            } else if (controller instanceof siparisController) {
                ((siparisController) controller).setKullaniciAdi(kullaniciAdi);
                ((siparisController) controller).setAdmin(isAdmin);
            }
            
            Stage stage = new Stage();
            stage.setTitle(baslik);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Sayfa açılamadı: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Uyarı mesajı gösteren fonksiyon
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
