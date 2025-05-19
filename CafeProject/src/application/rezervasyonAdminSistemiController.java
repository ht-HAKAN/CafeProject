package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.DatePicker;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;

import java.io.IOException;

public class rezervasyonAdminSistemiController {
    
    // Sol menü butonları
    @FXML private Button anasayfa;
    @FXML private Button menu;
    @FXML private Button siparisler;
    @FXML private Button masalarverezervasyon;
    @FXML private Button adminpanel;
    @FXML private Button bekleyenRezervasyonlarBtn;
    
    // Masa Ekleme/Güncelleme formları
    @FXML private TextField masaNoField;
    @FXML private TextField kapasiteField;
    @FXML private ComboBox<String> durumComboBox;
    @FXML private ComboBox<String> konumComboBox;
    @FXML private TextField rezAdField;
    @FXML private TextField rezSoyadField;
    @FXML private TextField rezTelefonField;
    @FXML private DatePicker rezTarihPicker;
    @FXML private ComboBox<String> rezSaatComboBox;
    
    // Butonlar
    @FXML private Button ekleButton;
    @FXML private Button guncelleButton;
    @FXML private Button silButton;
    @FXML private Button temizleButton;
    
    // Masa Grid
    @FXML private GridPane masalarGrid;
    
    // Kullanıcı bilgisi
    @FXML private Text kullaniciWelcomeText;
    
    // Veritabanı bağlantısı
    private Connection connection;
    
    // Seçili masa
    private int seciliMasaId = -1;
    private String seciliMasaNo = "";
    
    // Kullanıcı bilgisi
    private String kullaniciAdi = "Kullanıcı";
    private boolean isAdmin = false;
    
    public rezervasyonAdminSistemiController() {
        try {
            connection = MySQLConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void initialize() {
        // Başlangıçta welcome text'i güncelle
        updateWelcomeText();
        
        // Combobox'ları doldur
        setupComboBoxes();
        
        // Buton olaylarını ayarla
        setupButtonActions();
        
        // Sol menü butonlarını ayarla
        setupMenuButtons();
        
        // Masaları yükle
        loadMasalar();
        setupBekleyenRezervasyonlarButton();
        
        // Saat ComboBox'u doldur
        if (rezSaatComboBox != null) {
            rezSaatComboBox.setItems(FXCollections.observableArrayList(
                "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
            ));
        }
    }
    
    private void setupComboBoxes() {
        // Durum ComboBox
        ObservableList<String> durumlar = FXCollections.observableArrayList(
            "bos", "dolu", "kirli"
        );
        if (durumComboBox != null) {
            durumComboBox.setItems(durumlar);
            durumComboBox.setValue("bos");
        }
        
        // Konum ComboBox
        ObservableList<String> konumlar = FXCollections.observableArrayList(
            "Giriş Katı", "Bahçe"
        );
        if (konumComboBox != null) {
            konumComboBox.setItems(konumlar);
            konumComboBox.setValue("Giriş Katı");
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
    }
    
    // Sol menü butonlarını ayarlayan metod
    private void setupMenuButtons() {
        String buttonStyle = "-fx-background-color: #2D2D2D; -fx-border-color: #c99825; -fx-border-width: 2;";
        
        if (anasayfa != null) {
            anasayfa.setStyle(buttonStyle);
            anasayfa.setOnAction(event -> {
                sayfaAc("AnaSayfa.fxml", "Ana Sayfa");
            });
        }
        
        if (menu != null) {
            menu.setStyle(buttonStyle);
            menu.setOnAction(event -> {
                sayfaAc("menu.fxml", "Menü");
            });
        }
        
        if (siparisler != null) {
            siparisler.setStyle(buttonStyle);
            siparisler.setOnAction(event -> {
                sayfaAc("siparis.fxml", "Siparişler");
            });
        }
        
        if (masalarverezervasyon != null) {
            masalarverezervasyon.setStyle(buttonStyle);
            masalarverezervasyon.setOnAction(event -> {
                sayfaAc("masaverezervasyon.fxml", "Masa ve Rezervasyon");
            });
        }
        
        if (adminpanel != null) {
            adminpanel.setStyle(buttonStyle);
            adminpanel.setOnAction(event -> {
                sayfaAc("adminpanel.fxml", "Admin Panel");
            });
        }
        
        if (bekleyenRezervasyonlarBtn != null) {
            bekleyenRezervasyonlarBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("BekleyenRezervasyonlar.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Bekleyen Rezervasyonlar");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Hata", "Bekleyen rezervasyonlar ekranı açılamadı: " + e.getMessage());
                }
            });
        }
    }
    
    // Sayfa açma yardımcı fonksiyonu
    private void sayfaAc(String fxmlDosya, String baslik) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(rezervasyonAdminSistemiController.class.getResource(fxmlDosya));
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
                ((menuController) controller).updateWelcomeText();
                ((menuController) controller).updateAdminPanelButton();
            } else if (controller instanceof siparisController) {
                ((siparisController) controller).setKullaniciAdi(kullaniciAdi);
                ((siparisController) controller).setAdmin(isAdmin);
                ((siparisController) controller).updateWelcomeText();
                ((siparisController) controller).updateAdminPanelButton();
            } else if (controller instanceof AnaSayfaController) {
                ((AnaSayfaController) controller).setKullaniciAdi(kullaniciAdi);
                ((AnaSayfaController) controller).setAdmin(isAdmin);
            }
            
            Stage stage = new Stage();
            stage.setTitle(baslik);
            stage.setScene(new Scene(root));
            stage.show();
            
            // Mevcut sayfayı kapat
            if (anasayfa != null && anasayfa.getScene() != null) {
                Stage currentStage = (Stage) anasayfa.getScene().getWindow();
                currentStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, masaNo);
            stmt.setInt(2, kapasite);
            stmt.setString(3, durum);
            stmt.setString(4, konum);
            
            int affected = stmt.executeUpdate();
            int masaId = -1;
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) masaId = rs.getInt(1);
                // Rezervasyon bilgileri girilmişse rezervasyonlar tablosuna da ekle
                String ad = rezAdField.getText().trim();
                String soyad = rezSoyadField.getText().trim();
                String telefon = rezTelefonField.getText().trim();
                LocalDate tarih = rezTarihPicker.getValue();
                String saat = rezSaatComboBox.getValue();
                if (!ad.isEmpty() && !soyad.isEmpty() && !telefon.isEmpty() && tarih != null && saat != null && masaId != -1) {
                    try {
                        String sqlRez = "INSERT INTO rezervasyonlar (masa_id, musteri_adi, musteri_soyadi, telefon, tarih, saat, kisi_sayisi, notlar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement stmtRez = connection.prepareStatement(sqlRez);
                        stmtRez.setInt(1, masaId);
                        stmtRez.setString(2, ad);
                        stmtRez.setString(3, soyad);
                        stmtRez.setString(4, telefon);
                        stmtRez.setDate(5, Date.valueOf(tarih));
                        stmtRez.setString(6, saat);
                        stmtRez.setInt(7, kapasite); // Kişi sayısı olarak masa kapasitesi
                        stmtRez.setString(8, ""); // Notlar boş
                        stmtRez.executeUpdate();
                    } catch (SQLException e) {
                        showAlert(AlertType.ERROR, "Hata", "Rezervasyon eklenirken hata: " + e.getMessage());
                    }
                }
                showAlert(AlertType.INFORMATION, "Başarılı", "Masa ve rezervasyon başarıyla eklendi!");
                formTemizle();
                loadMasalar();
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
        durumComboBox.setValue("bos");
        konumComboBox.setValue("Giriş Katı");
        seciliMasaId = -1;
        seciliMasaNo = "";
        rezAdField.setText("");
        rezSoyadField.setText("");
        rezTelefonField.setText("");
        rezTarihPicker.setValue(null);
        rezSaatComboBox.setValue(null);
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
    
    // Uyarı mesajı gösteren fonksiyon
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Kullanıcı adını ayarla
    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        updateWelcomeText();
    }
    
    // Admin yetkisi ayarla
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    // Karşılama metnini güncelle
    private void updateWelcomeText() {
        if (kullaniciWelcomeText != null) {
            kullaniciWelcomeText.setText("Merhaba, " + kullaniciAdi + "!");
        }
    }

    private void setupBekleyenRezervasyonlarButton() {
        if (bekleyenRezervasyonlarBtn != null) {
            bekleyenRezervasyonlarBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("BekleyenRezervasyonlar.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Bekleyen Rezervasyonlar");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Hata", "Bekleyen rezervasyonlar ekranı açılamadı: " + e.getMessage());
                }
            });
        }
    }
}
