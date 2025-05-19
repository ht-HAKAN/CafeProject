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
import javafx.scene.layout.Pane;

import java.io.IOException;

public class siparisController {
    @FXML private Text kullaniciWelcomeText;
    @FXML private Button anasayfa;
    @FXML private Button menu;
    @FXML private Button siparisler;
    @FXML private Button masalarverezervasyon;
    @FXML private Button adminpanel;
    @FXML private Pane icerikPanel;

    private String kullaniciAdi = "Kullanıcı";
    private boolean isAdmin = false;

    public void initialize() {
        // Başlangıçta welcome text ve admin panel butonunu güncelle
        updateWelcomeText();
        updateAdminPanelButton();

        // Siparişler panelini yükle
        try {
            Parent siparislerRoot = FXMLLoader.load(getClass().getResource("siparislerAdmin.fxml"));
            icerikPanel.getChildren().setAll(siparislerRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }

        anasayfa.setOnAction(event -> {
            sayfaAc("AnaSayfa.fxml", "Ana Sayfa");
        });

        menu.setOnAction(event -> {
            sayfaAc("menu.fxml", "Menü");
        });

        siparisler.setOnAction(event -> {
            sayfaAc("siparislerAdmin.fxml", "Siparişler");
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

        // Admin değilse butonu devre dışı bırak ve stilini ayarla
        adminpanel.setDisable(!isAdmin);
        if (!isAdmin) {
            adminpanel.setStyle("-fx-background-color: #404040;");
        } else {
            adminpanel.setStyle("-fx-background-color: #2D2D2D;");
        }
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        updateWelcomeText();
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        
        // Admin yetkisi durumuna göre butonları ayarla
        if (adminpanel != null) {
            adminpanel.setDisable(!isAdmin);  // Admin ise aktif, değilse pasif
            if (!isAdmin) {
                adminpanel.setStyle("-fx-background-color: #404040;");
            } else {
                adminpanel.setStyle("-fx-background-color: #2D2D2D;");
            }
        }
        
        // Kullanıcı adını güncelle
        updateWelcomeText();
    }

    public void updateAdminPanelButton() {
        if (adminpanel != null) {
            // Admin değilse butonu devre dışı bırak ve stilini ayarla
            adminpanel.setDisable(!isAdmin);
            if (!isAdmin) {
                adminpanel.setStyle("-fx-background-color: #404040;");
            } else {
                adminpanel.setStyle("-fx-background-color: #2D2D2D;");
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
            if (fxmlDosya.equals("siparisler.fxml") || fxmlDosya.equals("siparis.fxml")) {
                fxmlDosya = "siparislerAdmin.fxml";
            }
            loader.setLocation(siparisController.class.getResource(fxmlDosya));
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
            } else if (controller instanceof menuController) {
                ((menuController) controller).setKullaniciAdi(kullaniciAdi);
                ((menuController) controller).setAdmin(isAdmin);
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
