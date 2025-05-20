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
    @FXML private Button menuGoruntuleButton;

    private String kullaniciAdi = "Kullanıcı";
    private boolean isAdmin = false;

    public void initialize() {
        updateWelcomeText();
        updateAdminPanelButton();

        anasayfa.setOnAction(event -> sayfaAc("AnaSayfa.fxml", "Ana Sayfa"));
        menu.setOnAction(event -> {});
        siparisler.setOnAction(event -> {
            if (isAdmin) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(menuController.class.getResource("siparislerAdmin.fxml"));
                    Parent root = loader.load();
                    Object controller = loader.getController();
                    if (controller instanceof siparislerAdminController) {
                        // Gerekirse kullanıcı adı/rol aktar
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Siparişler");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Hata", "Siparişler ekranı açılamadı: " + e.getMessage(), AlertType.ERROR);
                }
            } else {
                showAlert("Yetki Hatası", "Bu sekmeyi görüntülemek için yetkiniz yok!", AlertType.WARNING);
            }
        });
        masalarverezervasyon.setOnAction(event -> sayfaAc("masaverezervasyon.fxml", "Masa ve Rezervasyon"));
        adminpanel.setOnAction(event -> {
            if (isAdmin) {
                sayfaAc("adminpanel.fxml", "Admin Panel");
            } else {
                showAlert("Yetki Hatası", "Bu sekmeyi görüntülemek için yetkiniz yok!", AlertType.WARNING);
            }
        });
        adminpanel.setDisable(!isAdmin);
        if (!isAdmin) {
            adminpanel.setStyle("-fx-background-color: #404040;");
        } else {
            adminpanel.setStyle("-fx-background-color: #2D2D2D;");
        }
        // Menü sekmesi açılır açılmaz ürünler penceresini aç
        sayfaAc("menuGoruntule.fxml", "Menüyü Görüntüle");
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

    private void sayfaAc(String fxml, String baslik) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(baslik);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
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
