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

import java.io.IOException;

public class menuController {
    @FXML private Text kullaniciWelcomeText;
    @FXML private Button anasayfa;
    @FXML private Button menu;
    @FXML private Button siparisler;
    @FXML private Button masalarverezervasyon;
    @FXML private Button adminpanel;

    private String kullaniciAdi = "Kullanıcı";
    private boolean isAdmin = false;

    public void initialize() {
        // Başlangıçta welcome text ve admin panel butonunu güncelle
        updateWelcomeText();
        updateAdminPanelButton();

        anasayfa.setOnAction(event -> {
            sayfaAc("AnaSayfa.fxml", "Ana Sayfa");
        });

        menu.setOnAction(event -> {
            // Zaten menü sayfasındayız
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
                showAlert("Yetki Hatası", "Bu sekmeyi görüntülemek için yetkiniz yok!", AlertType.WARNING);
            }
        });
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        updateWelcomeText();
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        updateAdminPanelButton();
    }

    public void updateAdminPanelButton() {
        if (adminpanel != null) {
            // Admin değilse butonu devre dışı bırak ve stilini ayarla
            adminpanel.setDisable(!isAdmin);
            if (!isAdmin) {
                adminpanel.setStyle("-fx-background-color: #404040;");
            } else {
                adminpanel.setStyle("-fx-background-color: #2D2D2D; -fx-border-color: #FFD700; -fx-border-width: 2;");
            }
        }
    }

    public void updateWelcomeText() {
        if (kullaniciWelcomeText != null) {
            kullaniciWelcomeText.setText("Merhaba, " + kullaniciAdi + "!");
        }
    }

    private void sayfaAc(String fxmlDosya, String baslik) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(menuController.class.getResource(fxmlDosya));
            Parent root = loader.load();
            
            // Controller'a yetki ve kullanıcı bilgilerini aktar
            Object controller = loader.getController();
            if (controller instanceof masaverezervasyonController) {
                ((masaverezervasyonController) controller).setKullaniciAdi(kullaniciAdi);
                ((masaverezervasyonController) controller).setAdmin(isAdmin);
            } else if (controller instanceof AdminPanelController) {
                ((AdminPanelController) controller).setAdminName(kullaniciAdi);
                ((AdminPanelController) controller).setAdmin(isAdmin);
            } else if (controller instanceof AnaSayfaController) {
                ((AnaSayfaController) controller).setKullaniciAdi(kullaniciAdi);
                ((AnaSayfaController) controller).setAdmin(isAdmin);
            } else if (controller instanceof siparisController) {
                ((siparisController) controller).setKullaniciAdi(kullaniciAdi);
                ((siparisController) controller).setAdmin(isAdmin);
            }
            
            // Yeni sayfayı göster
            Stage stage = new Stage();
            stage.setTitle(baslik);
            stage.setScene(new Scene(root));
            stage.show();
            
            // Mevcut sayfayı kapat
            Stage currentStage = (Stage) anasayfa.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Sayfa açılamadı: " + e.getMessage(), AlertType.ERROR);
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
