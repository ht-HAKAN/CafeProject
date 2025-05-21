package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class PersonelYonetimiController {
    @FXML private TextField adField, soyadField, kullaniciAdField, sifreField, rolField, telefonField;
    @FXML private TableView<Personel> personelTable;
    @FXML private TableColumn<Personel, String> adColumn, soyadColumn, kullaniciAdColumn, rolColumn, telefonColumnT;
    @FXML private Button ekleBtn, guncelleBtn, silBtn;

    private ObservableList<Personel> personelList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        adColumn.setCellValueFactory(data -> data.getValue().adProperty());
        soyadColumn.setCellValueFactory(data -> data.getValue().soyadProperty());
        kullaniciAdColumn.setCellValueFactory(data -> data.getValue().kullaniciAdProperty());
        rolColumn.setCellValueFactory(data -> data.getValue().rolProperty());
        telefonColumnT.setCellValueFactory(data -> data.getValue().telefonProperty());
        personelTable.setItems(personelList);
        personelTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                adField.setText(newSel.getAd());
                soyadField.setText(newSel.getSoyad());
                kullaniciAdField.setText(newSel.getKullaniciAd());
                sifreField.setText(newSel.getSifre());
                rolField.setText(newSel.getRol());
                telefonField.setText(newSel.getTelefon());
            }
        });
        ekleBtn.setOnAction(e -> personelEkle());
        guncelleBtn.setOnAction(e -> personelGuncelle());
        silBtn.setOnAction(e -> personelSil());
        personelTabloYukle();
    }

    private void personelTabloYukle() {
        personelList.clear();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT * FROM personel";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                personelList.add(new Personel(
                    rs.getString("ad"),
                    rs.getString("soyad"),
                    rs.getString("kullanici_ad"),
                    rs.getString("sifre"),
                    rs.getString("rol"),
                    rs.getString("telefon")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void personelEkle() {
        if (!alanKontrol()) return;
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "INSERT INTO personel (ad, soyad, kullanici_ad, sifre, rol, telefon) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, adField.getText());
            stmt.setString(2, soyadField.getText());
            stmt.setString(3, kullaniciAdField.getText());
            stmt.setString(4, sifreField.getText());
            stmt.setString(5, rolField.getText());
            stmt.setString(6, telefonField.getText());
            stmt.executeUpdate();
            personelTabloYukle();
            temizle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void personelGuncelle() {
        Personel secili = personelTable.getSelectionModel().getSelectedItem();
        if (secili == null) return;
        if (!alanKontrol()) return;
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "UPDATE personel SET ad=?, soyad=?, sifre=?, rol=?, telefon=? WHERE kullanici_ad=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, adField.getText());
            stmt.setString(2, soyadField.getText());
            stmt.setString(3, sifreField.getText());
            stmt.setString(4, rolField.getText());
            stmt.setString(5, telefonField.getText());
            stmt.setString(6, kullaniciAdField.getText());
            stmt.executeUpdate();
            personelTabloYukle();
            temizle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean alanKontrol() {
        String ad = adField.getText().trim();
        String soyad = soyadField.getText().trim();
        String kullaniciAd = kullaniciAdField.getText().trim();
        String sifre = sifreField.getText().trim();
        String rol = rolField.getText().trim();
        String telefon = telefonField.getText().trim();
        if (ad.isEmpty() || soyad.isEmpty() || kullaniciAd.isEmpty() || sifre.isEmpty() || rol.isEmpty() || telefon.isEmpty()) {
            uyariGoster("Hata", "Tüm alanları doldurun!");
            return false;
        }
        if (!telefon.matches("\\d{10,11}")) {
            uyariGoster("Hata", "Telefon numarası 10 veya 11 haneli olmalı ve sadece rakam içermelidir!");
            return false;
        }
        return true;
    }

    private void uyariGoster(String baslik, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    private void personelSil() {
        Personel secili = personelTable.getSelectionModel().getSelectedItem();
        if (secili == null) return;
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "DELETE FROM personel WHERE kullanici_ad=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, secili.getKullaniciAd());
            stmt.executeUpdate();
            personelTabloYukle();
            temizle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void temizle() {
        adField.clear();
        soyadField.clear();
        kullaniciAdField.clear();
        sifreField.clear();
        rolField.clear();
        telefonField.clear();
    }

    public static class Personel {
        private final javafx.beans.property.SimpleStringProperty ad, soyad, kullaniciAd, sifre, rol, telefon;
        public Personel(String ad, String soyad, String kullaniciAd, String sifre, String rol, String telefon) {
            this.ad = new javafx.beans.property.SimpleStringProperty(ad);
            this.soyad = new javafx.beans.property.SimpleStringProperty(soyad);
            this.kullaniciAd = new javafx.beans.property.SimpleStringProperty(kullaniciAd);
            this.sifre = new javafx.beans.property.SimpleStringProperty(sifre);
            this.rol = new javafx.beans.property.SimpleStringProperty(rol);
            this.telefon = new javafx.beans.property.SimpleStringProperty(telefon);
        }
        public String getAd() { return ad.get(); }
        public String getSoyad() { return soyad.get(); }
        public String getKullaniciAd() { return kullaniciAd.get(); }
        public String getSifre() { return sifre.get(); }
        public String getRol() { return rol.get(); }
        public String getTelefon() { return telefon.get(); }
        public javafx.beans.property.StringProperty adProperty() { return ad; }
        public javafx.beans.property.StringProperty soyadProperty() { return soyad; }
        public javafx.beans.property.StringProperty kullaniciAdProperty() { return kullaniciAd; }
        public javafx.beans.property.StringProperty sifreProperty() { return sifre; }
        public javafx.beans.property.StringProperty rolProperty() { return rol; }
        public javafx.beans.property.StringProperty telefonProperty() { return telefon; }
    }
} 