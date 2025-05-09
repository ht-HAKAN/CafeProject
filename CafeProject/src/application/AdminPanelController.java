package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

import java.sql.*;

public class AdminPanelController {

    @FXML
    private TextField adField, soyadField, kullaniciAdiField, sifreField, rolField, telefonField;

    @FXML
    private ListView<String> personelListView;
    
    @FXML
    private Text adminWelcomeText; // Karşılama metni için Text nesnesi

    @FXML
    private Button addButton, deleteButton, updateButton;

    private Connection connection;
    private String selectedPersonelTelefon; // Seçilen personelin telefon numarası
    private String adminName = "Admin"; // Varsayılan değer

    public AdminPanelController() {
        try {
            connection = MySQLConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Giriş yapan admin adını ayarlamak için
    public void setAdminName(String name) {
        this.adminName = name;
        updateWelcomeText();
    }
    
    // Karşılama metnini güncelle
    private void updateWelcomeText() {
        if (adminWelcomeText != null) {
            adminWelcomeText.setText("Merhaba, " + adminName + "!");
        }
    }

    @FXML
    public void initialize() {
        loadPersonelList();
        
        // Karşılama metnini ayarla
        updateWelcomeText();
        
        // PersonelListView tıklama olayını ekle
        personelListView.setOnMouseClicked(event -> {
            String selected = personelListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String[] parts = selected.split(" - ");
                if (parts.length >= 4) {
                    // İlk kısım ad soyad birleşik
                    String fullName = parts[0];
                    String[] nameParts = fullName.split(" ", 2);
                    
                    // Ad ve soyadı ayır
                    String ad = nameParts[0];
                    String soyad = nameParts.length > 1 ? nameParts[1] : "";
                    
                    // Diğer bilgileri al
                    String kullaniciAdi = parts[1];
                    String rol = parts[2];
                    String telefon = parts[3];
                    
                    // Telefon numarasını seçili personel olarak sakla
                    selectedPersonelTelefon = telefon;
                    
                    // Şifreyi veritabanından al
                    String sifre = getPasswordByTelefon(telefon);
                    
                    // Form alanlarını doldur
                    adField.setText(ad);
                    soyadField.setText(soyad);
                    kullaniciAdiField.setText(kullaniciAdi);
                    sifreField.setText(sifre);
                    rolField.setText(rol);
                    telefonField.setText(telefon);
                }
            }
        });

        // Buton olaylarını ekle
        addButton.setOnAction(event -> {
            addPersonel();
            loadPersonelList(); 
            clearFields();      
        });
        
        updateButton.setOnAction(event -> {
            updatePersonel();
            loadPersonelList();
            clearFields();
        });
        
        deleteButton.setOnAction(event -> {
            deletePersonel();
            loadPersonelList();
        });
    }
    
    // Telefon numarasına göre şifreyi veritabanından alma
    private String getPasswordByTelefon(String telefon) {
        String sql = "SELECT sifre FROM personel WHERE telefon = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, telefon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("sifre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    // Personel bilgilerini güncelleme
    private void updatePersonel() {
        if (selectedPersonelTelefon == null || selectedPersonelTelefon.isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen önce listeden bir personel seçin.");
            return;
        }
        
        // Form alanlarından verileri al
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText();
        String rol = rolField.getText();
        String telefon = telefonField.getText();

        // Boş alan kontrolü
        if (ad.isEmpty() || soyad.isEmpty() || kullaniciAdi.isEmpty() || sifre.isEmpty() || rol.isEmpty() || telefon.isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Tüm alanları doldurun.");
            return;
        }

        try {
            // SQL sorgusu
            String sql = "UPDATE personel SET ad = ?, soyad = ?, kullanici_ad = ?, sifre = ?, rol = ?, telefon = ? WHERE telefon = ?";
            
            // Veritabanı bağlantısını kontrol et
            if (connection == null || connection.isClosed()) {
                connection = MySQLConnection.connect();
            }
            
            // Güncelleme işlemini gerçekleştir
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, ad);
            stmt.setString(2, soyad);
            stmt.setString(3, kullaniciAdi);
            stmt.setString(4, sifre);
            stmt.setString(5, rol);
            stmt.setString(6, telefon);
            stmt.setString(7, selectedPersonelTelefon);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Personel bilgileri başarıyla güncellendi!");
                selectedPersonelTelefon = null; // Seçimi sıfırla
            } else {
                showAlert(AlertType.ERROR, "Hata", "Güncelleme işlemi başarısız. Kayıt bulunamadı.");
            }
            
            stmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }

    private void addPersonel() {
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText();
        String rol = rolField.getText();
        String telefon = telefonField.getText();

        if (ad.isEmpty() || soyad.isEmpty() || kullaniciAdi.isEmpty() || sifre.isEmpty() || rol.isEmpty() || telefon.isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Tüm alanları doldurun.");
            return;
        }

        String sql = "INSERT INTO personel (ad, soyad, kullanici_ad, sifre, rol, telefon) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ad);
            stmt.setString(2, soyad);
            stmt.setString(3, kullaniciAdi);
            stmt.setString(4, sifre);
            stmt.setString(5, rol);
            stmt.setString(6, telefon);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Personel başarıyla eklendi!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }

    private void deletePersonel() {
        String selected = personelListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen silinecek bir personel seçin.");
            return;
        }
        String[] parts = selected.split(" - ");
        if (parts.length < 4) {
            showAlert(AlertType.ERROR, "Hata", "Veri formatı hatalı.");
            return;
        }
        String telefon = parts[3];

        String sql = "DELETE FROM personel WHERE telefon = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, telefon);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Personel başarıyla silindi!");
            } else {
                showAlert(AlertType.ERROR, "Hata", "Silme işlemi başarısız.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
    }

    private void loadPersonelList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT ad, soyad, kullanici_ad, rol, telefon FROM personel";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ad = rs.getString("ad");
                String soyad = rs.getString("soyad");
                String kullanici_ad = rs.getString("kullanici_ad");
                String rol = rs.getString("rol");
                String telefon = rs.getString("telefon");
                
                // Format: "Ad Soyad - kullaniciAdi - rol - telefon"
                String personelBilgisi = ad + " " + soyad + " - " + kullanici_ad + " - " + rol + " - " + telefon;
                list.add(personelBilgisi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        personelListView.setItems(list);
    }

    private void clearFields() {
        adField.clear();
        soyadField.clear();
        kullaniciAdiField.clear();
        sifreField.clear();
        rolField.clear();
        telefonField.clear();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
