package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import application.Rezervasyon;

public class RezervasyonListesiController {
    @FXML private TableView<Rezervasyon> tableView;
    @FXML private TableColumn<Rezervasyon, String> adColumn;
    @FXML private TableColumn<Rezervasyon, String> soyadColumn;
    @FXML private TableColumn<Rezervasyon, String> telefonColumn;
    @FXML private TableColumn<Rezervasyon, LocalDate> tarihColumn;
    @FXML private TableColumn<Rezervasyon, String> saatColumn;
    @FXML private TableColumn<Rezervasyon, Integer> kisiSayisiColumn;
    @FXML private TableColumn<Rezervasyon, String> masaNoColumn;

    @FXML
    public void initialize() {
        adColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        soyadColumn.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        telefonColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        tarihColumn.setCellValueFactory(new PropertyValueFactory<>("tarih"));
        saatColumn.setCellValueFactory(new PropertyValueFactory<>("saat"));
        kisiSayisiColumn.setCellValueFactory(new PropertyValueFactory<>("kisiSayisi"));
        masaNoColumn.setCellValueFactory(new PropertyValueFactory<>("masaNo"));
        loadRezervasyonlar();
    }

    private void loadRezervasyonlar() {
        ObservableList<Rezervasyon> list = FXCollections.observableArrayList();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT r.*, m.masa_no FROM rezervasyonlar r LEFT JOIN masalar m ON r.masa_id = m.masa_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Rezervasyon(
                    rs.getString("musteri_adi"),
                    rs.getString("musteri_soyadi"),
                    rs.getString("telefon"),
                    rs.getDate("tarih").toLocalDate(),
                    rs.getString("saat"),
                    rs.getInt("kisi_sayisi"),
                    rs.getString("masa_no")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableView.setItems(list);
    }
} 
