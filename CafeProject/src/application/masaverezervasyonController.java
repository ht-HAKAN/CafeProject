package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class masaverezervasyonController {

    // Sol menü butonları
    @FXML private Button anasayfa;
    @FXML private Button menu;
    @FXML private Button siparisler;
    @FXML private Button masalarverezervasyon;
    @FXML private Button adminpanel;
    
    // Üst kısım
    @FXML private Text kullaniciWelcomeText;
    
    // Ana içerik butonları
    @FXML private Button rezervasyonListesiBtn;
    @FXML private Button rezervasyonYapBtn;
    @FXML private Button rezervasyonSistemiBtn;
    
    // Masa Grid ve Container
    @FXML private GridPane masalarGrid;
    @FXML private VBox masaDetayContainer;
    
    // Masa Ekleme/Güncelleme formları
    @FXML private TextField masaNoField;
    @FXML private TextField kapasiteField;
    @FXML private ComboBox<String> durumComboBox;
    @FXML private ComboBox<String> konumComboBox;
    
    // Butonlar
    @FXML private Button ekleButton;
    @FXML private Button guncelleButton;
    @FXML private Button silButton;
    @FXML private Button temizleButton;
    
    // Rezervasyon alanları
    @FXML private TextField musteriAdiField;
    @FXML private TextField musteriSoyadiField;
    @FXML private TextField telefonField;
    @FXML private DatePicker tarihPicker;
    @FXML private TextField saatField;
    @FXML private TextField kisiSayisiField;
    @FXML private TextField notlarField;
    @FXML private Button rezervasyonEkleButton;
    
    // Veritabanı bağlantısı
    private Connection connection;
    
    // Seçili masa
    private int seciliMasaId = -1;
    private String seciliMasaNo = "";
    
    // Kullanıcı bilgisi
    private String kullaniciAdi = "Kullanıcı";
    private boolean isAdmin = false;
    
    public masaverezervasyonController() {
        try {
            connection = MySQLConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void initialize() {
        setupComboBoxes();
        
        setupMenuButtons();
        
        setupContentButtons();
        
        loadMasalar();
        
        setupButtonActions();
    }
    
    private void setupComboBoxes() {
        ObservableList<String> durumlar = FXCollections.observableArrayList(
            "BOŞ", "DOLU", "KİRLİ"
        );
        if (durumComboBox != null) {
            durumComboBox.setItems(durumlar);
            durumComboBox.setValue("BOŞ");
            
            // Ana ComboBox stilini ayarla
            durumComboBox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-text-fill: white; " +
                                 "-fx-background-color: #2D2D2D; -fx-border-color: #FFD700; " +
                                 "-fx-border-width: 2; -fx-border-radius: 3; -fx-prompt-text-fill: white;");
            
            durumComboBox.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                    }
                }
            });
            
            // Popup liste öğelerinin stilini ayarla
            durumComboBox.setCellFactory(listView -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-background-color: #2D2D2D; " +
                                "-fx-padding: 5 10; -fx-font-weight: bold;");
                    }
                }
            });
        }
        
        // Konum ComboBox
        ObservableList<String> konumlar = FXCollections.observableArrayList(
            "GİRİŞ KATI", "BAHÇE"
        );
        if (konumComboBox != null) {
            konumComboBox.setItems(konumlar);
            konumComboBox.setValue("GİRİŞ KATI");
            
            // Ana ComboBox stilini ayarla
            konumComboBox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-text-fill: white; " +
                                 "-fx-background-color: #2D2D2D; -fx-border-color: #FFD700; " +
                                 "-fx-border-width: 2; -fx-border-radius: 3; -fx-prompt-text-fill: white;");
            
            // Button cell (seçili öğe) stilini ayarla
            konumComboBox.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                    }
                }
            });
            
            // Popup liste öğelerinin stilini ayarla
            konumComboBox.setCellFactory(listView -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-background-color: #2D2D2D; " +
                                "-fx-padding: 5 10; -fx-font-weight: bold;");
                    }
                }
            });
        }
    }
    
    private void setupButtonActions() {
        if (ekleButton != null) {
            ekleButton.setOnAction(event -> masaEkle());
        }
        
        if (guncelleButton != null) {
            guncelleButton.setOnAction(event -> masaGuncelle());
        }
        
        if (silButton != null) {
            silButton.setOnAction(event -> masaSil());
        }
        
        if (temizleButton != null) {
            temizleButton.setOnAction(event -> formTemizle());
        }
        
        if (rezervasyonEkleButton != null) {
            rezervasyonEkleButton.setOnAction(event -> rezervasyonEkle());
        }
        
        if (rezervasyonListesiBtn != null) {
            rezervasyonListesiBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("rezervasyonListesi.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Rezervasyon Listesi");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Hata", "Rezervasyon Listesi ekranı açılamadı: " + e.getMessage());
                }
            });
        }
    }
    
    // Sol menü butonlarını ayarlayan metod
    private void setupMenuButtons() {
        if (anasayfa != null) {
            anasayfa.setOnAction(event -> {
                sayfaAc("AnaSayfa.fxml", "Ana Sayfa");
            });
        }
        
        if (menu != null) {
            menu.setOnAction(event -> {
                sayfaAc("menuGoruntule.fxml", "Menü");
            });
        }
        
        if (siparisler != null) {
            siparisler.setOnAction(event -> {
                sayfaAc("siparislerAdmin.fxml", "Siparişler");
            });
        }
        
        if (masalarverezervasyon != null) {
            // Zaten bu sayfadayız
        }
        
        if (adminpanel != null) {
            adminpanel.setOnAction(event -> {
                if (isAdmin) {
                    sayfaAc("adminpanel.fxml", "Admin Panel");
                } else {
                    showAlert(AlertType.WARNING, "Yetki Hatası", "Bu sekmeyi görüntülemek için admin yetkisine sahip olmanız gerekiyor!");
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
    }
    
    // Ana içerik butonlarını ayarlayan metod
    private void setupContentButtons() {
        if (rezervasyonListesiBtn != null) {
            rezervasyonListesiBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("rezervasyonListesi.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Rezervasyon Listesi");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Hata", "Rezervasyon Listesi ekranı açılamadı: " + e.getMessage());
                }
            });
        }
        if (rezervasyonYapBtn != null) {
            rezervasyonYapBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("rezervasyonYap.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Rezervasyon Yap");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Hata", "Rezervasyon ekranı açılamadı: " + e.getMessage());
                }
            });
        }
        if (rezervasyonSistemiBtn != null) {
            rezervasyonSistemiBtn.setDisable(true);
            rezervasyonSistemiBtn.setVisible(false);
        }
    }
    
    // Sayfa açma yardımcı fonksiyonu
    private void sayfaAc(String fxmlDosya, String baslik) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(masaverezervasyonController.class.getResource(fxmlDosya));
            Parent root = loader.load();
            // Controller'a yetki ve kullanıcı bilgilerini aktar
            Object controller = loader.getController();
            if (controller instanceof rezervasyonAdminSistemiController) {
                ((rezervasyonAdminSistemiController) controller).setKullaniciAdi(kullaniciAdi);
                ((rezervasyonAdminSistemiController) controller).setAdmin(isAdmin);
            } else if (controller instanceof AnaSayfaController) {
                ((AnaSayfaController) controller).setKullaniciAdi(kullaniciAdi);
                ((AnaSayfaController) controller).setAdmin(isAdmin);
            } else if (controller instanceof menuController) {
                ((menuController) controller).setKullaniciAdi(kullaniciAdi);
                ((menuController) controller).setAdmin(isAdmin);
                ((menuController) controller).updateWelcomeText();
                ((menuController) controller).updateAdminPanelButton();
            } else if (controller instanceof siparisController) {
                ((siparisController) controller).setKullaniciAdi(kullaniciAdi);
                ((siparisController) controller).setAdmin(isAdmin);
                ((siparisController) controller).updateWelcomeText();
                ((siparisController) controller).updateAdminPanelButton();
            }
            Stage stage = new Stage();
            stage.setTitle(baslik);
            stage.setScene(new Scene(root));
            stage.show();
            // Mevcut pencereyi kapat
            Stage currentStage = (Stage) anasayfa.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Hata: " + e.getMessage() + " - Dosya yolu: " + fxmlDosya);
            showAlert(AlertType.ERROR, "Hata", "Sayfa açılamadı: " + e.getMessage());
        }
    }
    
    // Masa ekle
    private void masaEkle() {
        if (masaNoField.getText().isEmpty() || kapasiteField.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Masa no ve kapasite alanları boş bırakılamaz!");
            return;
        }
        
        String masaNo = masaNoField.getText();
        int kapasite;
        try {
            kapasite = Integer.parseInt(kapasiteField.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Hata", "Kapasite sayısal bir değer olmalıdır!");
            return;
        }
        
        String durum = durumComboBox.getValue();
        String konum = konumComboBox.getValue();
        
        try {
            String sql = "INSERT INTO masalar (masa_no, kapasite, durum, konum) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, masaNo);
            stmt.setInt(2, kapasite);
            stmt.setString(3, durum);
            stmt.setString(4, konum);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Masa başarıyla eklendi!");
                formTemizle();
                loadMasalar(); // Masaları yeniden yükle
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }
    
    // Masa güncelle
    private void masaGuncelle() {
        if (seciliMasaId == -1) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen önce bir masa seçin!");
            return;
        }
        
        if (masaNoField.getText().isEmpty() || kapasiteField.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Masa no ve kapasite alanları boş bırakılamaz!");
            return;
        }
        
        String masaNo = masaNoField.getText();
        int kapasite;
        try {
            kapasite = Integer.parseInt(kapasiteField.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Hata", "Kapasite sayısal bir değer olmalıdır!");
            return;
        }
        
        String durum = durumComboBox.getValue();
        String konum = konumComboBox.getValue();
        
        try {
            String sql = "UPDATE masalar SET masa_no = ?, kapasite = ?, durum = ?, konum = ? WHERE masa_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, masaNo);
            stmt.setInt(2, kapasite);
            stmt.setString(3, durum);
            stmt.setString(4, konum);
            stmt.setInt(5, seciliMasaId);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Masa başarıyla güncellendi!");
                formTemizle();
                loadMasalar(); // Masaları yeniden yükle
            } else {
                showAlert(AlertType.ERROR, "Hata", "Masa güncellenemedi!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }
    
    // Masa sil
    private void masaSil() {
        if (seciliMasaId == -1) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen önce bir masa seçin!");
            return;
        }
        
        try {
            String sql = "DELETE FROM masalar WHERE masa_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, seciliMasaId);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Masa başarıyla silindi!");
                formTemizle();
                loadMasalar(); // Masaları yeniden yükle
            } else {
                showAlert(AlertType.ERROR, "Hata", "Masa silinemedi!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }
    
    // Form temizle
    private void formTemizle() {
        masaNoField.setText("");
        kapasiteField.setText("");
        durumComboBox.setValue("BOŞ");
        konumComboBox.setValue("GİRİŞ KATI");
        seciliMasaId = -1;
        seciliMasaNo = "";
        
        // Rezervasyon formunu da temizle
        if (musteriAdiField != null) musteriAdiField.setText("");
        if (musteriSoyadiField != null) musteriSoyadiField.setText("");
        if (telefonField != null) telefonField.setText("");
        if (tarihPicker != null) tarihPicker.setValue(LocalDate.now());
        if (saatField != null) saatField.setText("");
        if (kisiSayisiField != null) kisiSayisiField.setText("");
        if (notlarField != null) notlarField.setText("");
    }
    
    // Rezervasyon ekle
    private void rezervasyonEkle() {
        if (seciliMasaId == -1) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen önce bir masa seçin!");
            return;
        }
        
        if (musteriAdiField.getText().isEmpty() || musteriSoyadiField.getText().isEmpty() || 
            telefonField.getText().isEmpty() || tarihPicker.getValue() == null || 
            saatField.getText().isEmpty() || kisiSayisiField.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen gerekli tüm alanları doldurun!");
            return;
        }
        
        String musteriAdi = musteriAdiField.getText();
        String musteriSoyadi = musteriSoyadiField.getText();
        String telefon = telefonField.getText();
        LocalDate tarih = tarihPicker.getValue();
        String saat = saatField.getText();
        int kisiSayisi;
        try {
            kisiSayisi = Integer.parseInt(kisiSayisiField.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Hata", "Kişi sayısı sayısal bir değer olmalıdır!");
            return;
        }
        String notlar = notlarField.getText() != null ? notlarField.getText() : "";
        
        try {
            String sql = "INSERT INTO rezervasyonlar (masa_id, musteri_adi, musteri_soyadi, telefon, tarih, saat, kisi_sayisi, notlar) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, seciliMasaId);
            stmt.setString(2, musteriAdi);
            stmt.setString(3, musteriSoyadi);
            stmt.setString(4, telefon);
            stmt.setDate(5, Date.valueOf(tarih));
            stmt.setString(6, saat);
            stmt.setInt(7, kisiSayisi);
            stmt.setString(8, notlar);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                // Masanın durumunu dolu olarak güncelle
                String updateSql = "UPDATE masalar SET durum = 'dolu' WHERE masa_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, seciliMasaId);
                updateStmt.executeUpdate();
                
                showAlert(AlertType.INFORMATION, "Başarılı", "Rezervasyon başarıyla eklendi!");
                formTemizle();
                loadMasalar(); // Masaları yeniden yükle
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }
    
    // Veritabanından masaları yükle ve grid'e ekle
    private void loadMasalar() {
        if (masalarGrid == null) return;
        
        // Grid'i temizle
        masalarGrid.getChildren().clear();
        
        try {
            String sql = "SELECT * FROM masalar ORDER BY masa_id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int row = 0;
            int col = 0;
            int colCount = 4; // Her satırda 4 masa gösterelim
            
            while (rs.next()) {
                int masaId = rs.getInt("masa_id");
                String masaNo = rs.getString("masa_no");
                int kapasite = rs.getInt("kapasite");
                String durum = rs.getString("durum");
                String konum = rs.getString("konum");
                
                // Masa görsel elemanını oluştur
                StackPane masaPane = createMasaPane(masaId, masaNo, kapasite, durum, konum);
                
                // Grid'e ekle
                masalarGrid.add(masaPane, col, row);
                
                // Sonraki sütuna geç, satır dolunca alt satıra geç
                col++;
                if (col >= colCount) {
                    col = 0;
                    row++;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }
    
    // Masa görsel elemanını oluştur
    private StackPane createMasaPane(int masaId, String masaNo, int kapasite, String durum, String konum) {
        // Ana konteyner
        StackPane stackPane = new StackPane();
        stackPane.setMinSize(100, 100);
        stackPane.setMaxSize(100, 100);
        
        // Arka plan dikdörtgeni - duruma göre renk belirle
        Rectangle rect = new Rectangle(100, 100);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        
        switch (durum) {
            case "bos":
                rect.setFill(Color.GREEN);
                break;
            case "dolu":
                rect.setFill(Color.RED);
                break;
            case "kirli":
                rect.setFill(Color.YELLOW);
                break;
            default:
                rect.setFill(Color.GRAY);
        }
        
        // Masa bilgisi
        VBox infoBox = new VBox(5);
        Text masaNoText = new Text(masaNo);
        masaNoText.setFill(Color.WHITE);
        masaNoText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Text kapasiteText = new Text("Kapasite: " + kapasite);
        kapasiteText.setFill(Color.WHITE);
        
        Text konumText = new Text(konum);
        konumText.setFill(Color.WHITE);
        
        infoBox.getChildren().addAll(masaNoText, kapasiteText, konumText);
        
        // Öğeleri stack'e ekle
        stackPane.getChildren().addAll(rect, infoBox);
        
        // Tıklama olayı ekle
        stackPane.setOnMouseClicked(event -> {
            seciliMasaId = masaId;
            seciliMasaNo = masaNo;
            masaNoField.setText(masaNo);
            kapasiteField.setText(String.valueOf(kapasite));
            durumComboBox.setValue(durum);
            konumComboBox.setValue(konum);
            
            // Tüm masaların stilini resetle, seçili olanı belirginleştir
            masalarGrid.getChildren().forEach(node -> {
                if (node instanceof StackPane) {
                    Rectangle r = (Rectangle) ((StackPane) node).getChildren().get(0);
                    r.setStroke(Color.TRANSPARENT);
                    r.setStrokeWidth(0);
                }
            });
            
            // Seçili masayı belirginleştir
            rect.setStroke(Color.WHITE);
            rect.setStrokeWidth(3);
        });
        
        return stackPane;
    }
    
    // Kullanıcı adını ayarla
    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        if (kullaniciWelcomeText != null) {
            kullaniciWelcomeText.setText("Merhaba, " + kullaniciAdi + "!");
        }
    }
    
    // Admin yetkisi ayarla
    public void setAdmin(boolean isAdmin) {
        System.out.println("setAdmin çağrıldı: " + isAdmin); // Debug için
        this.isAdmin = isAdmin;
        
        // Admin yetkisi durumuna göre butonları ayarla
        if (rezervasyonSistemiBtn != null) {
            rezervasyonSistemiBtn.setDisable(true);
            rezervasyonSistemiBtn.setVisible(false);
        }
        
        // Admin paneli butonunu ayarla
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
        
        System.out.println("Kullanıcı adı: " + kullaniciAdi + ", Admin: " + isAdmin); // Debug için
    }
    
    // Karşılama metnini güncelle
    private void updateWelcomeText() {
        if (kullaniciWelcomeText != null) {
            kullaniciWelcomeText.setText("Merhaba, " + kullaniciAdi + "!");
        }
    }
    
    // Uyarı mesajı gösteren fonksiyon
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
