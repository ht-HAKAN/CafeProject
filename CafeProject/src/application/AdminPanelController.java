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
    private TextField adField, soyadField, kullaniciAdiField, sifreField, rolField;

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
            loadPersonelList(); 
            clearFields();      
        });
    }

    private void addPersonel() {
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText();
        String rol = rolField.getText();

        if (ad.isEmpty() || soyad.isEmpty() || kullaniciAdi.isEmpty() || sifre.isEmpty() || rol.isEmpty()) {
            System.out.println("Tüm alanları doldurun.");
            return;
        }

        // Tablo kolonları SQL sorgusu 
        String sql = "INSERT INTO personel (ad, soyad, kullanici_ad, sifre, rol) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ad);
            stmt.setString(2, soyad);
            stmt.setString(3, kullaniciAdi);
            stmt.setString(4, sifre);
            stmt.setString(5, rol);
            stmt.executeUpdate();
            System.out.println("Personel başarıyla eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPersonelList() {
        ObservableList<String> list = FXCollections.observableArrayList();

        // Sadece ad, soyad ve rol bilgileri
        String sql = "SELECT ad, soyad, rol FROM personel";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String fullName = rs.getString("ad") + " " + rs.getString("soyad") + " - " + rs.getString("rol");
                list.add(fullName);
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
    }
}
