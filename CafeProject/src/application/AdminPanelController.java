package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class AdminPanelController {

    @FXML
    private TextField adField, soyadField, kullaniciAdiField, sifreField, rolField, telefonField;

    @FXML
    private ListView<String> personelListView;

    @FXML
    private Button addButton;

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
    }

    private void addPersonel() {
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText();
        String rol = rolField.getText();
        String telefon = telefonField.getText();

        if (ad.isEmpty() || soyad.isEmpty() || kullaniciAdi.isEmpty() || sifre.isEmpty() || rol.isEmpty() || telefon.isEmpty()) {
            System.out.println("Tüm alanları doldurun.");
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
            stmt.executeUpdate();
            System.out.println("Personel başarıyla eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
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
}
