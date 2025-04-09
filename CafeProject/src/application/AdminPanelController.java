package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;

public class AdminPanelController {

    @FXML
    private TextField adField, soyadField, kullaniciAdiField, sifreField, rolField, telefonField;

    @FXML
    private ListView<String> personelListView;

    @FXML
    private Button addButton, deleteButton;

    private Connection connection;

    public AdminPanelController() {
        try {
            connection = MySQLConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadPersonelList();

        addButton.setOnAction(event -> {
            addPersonel();
            loadPersonelList(); // Eklemeden sonra listeyi yenile
            clearFields();      // Alanları temizle
        });
        
        deleteButton.setOnAction(event -> {
            deletePersonel();
            loadPersonelList();
        });
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

        // id sütunu AUTO_INCREMENT olduğu için onu göndermiyoruz.
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
        // ListView'den seçili olanı alıyoruz.
        String selected = personelListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen silinecek bir personel seçin.");
            return;
        }
        // Seçili satır "Ad Soyad - Kullanici_ad - Rol - Telefon" formatında.
        // Benzersiz olması için telefon numarasını kullanıyoruz.
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
                String personelBilgisi = rs.getString("ad") + " " + rs.getString("soyad") +
                        " - " + rs.getString("kullanici_ad") + " - " + rs.getString("rol") +
                        " - " + rs.getString("telefon");
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
