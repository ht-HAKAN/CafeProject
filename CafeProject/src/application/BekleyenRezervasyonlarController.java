package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;
import java.time.LocalDate;
import application.BekleyenRezervasyon;

public class BekleyenRezervasyonlarController {
    @FXML private TableView<BekleyenRezervasyon> tableView;
    @FXML private TableColumn<BekleyenRezervasyon, String> adColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> soyadColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> telefonColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> tarihColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> saatColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> kisiSayisiColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> notlarColumn;
    @FXML private TableColumn<BekleyenRezervasyon, Void> onaylaColumn;

    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = MySQLConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setupTable();
        loadBekleyenRezervasyonlar();
    }

    private void setupTable() {
        adColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        soyadColumn.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        telefonColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        tarihColumn.setCellValueFactory(new PropertyValueFactory<>("tarih"));
        saatColumn.setCellValueFactory(new PropertyValueFactory<>("saat"));
        kisiSayisiColumn.setCellValueFactory(new PropertyValueFactory<>("kisiSayisi"));
        notlarColumn.setCellValueFactory(new PropertyValueFactory<>("notlar"));
        onaylaColumn.setCellFactory(col -> new TableCell<BekleyenRezervasyon, Void>() {
            private final Button onaylaBtn = new Button("Onayla");
            {
                onaylaBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                onaylaBtn.setOnAction(e -> {
                    BekleyenRezervasyon rez = getTableView().getItems().get(getIndex());
                    onaylaRezervasyon(rez);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(onaylaBtn);
                }
            }
        });
    }

    private void loadBekleyenRezervasyonlar() {
        ObservableList<BekleyenRezervasyon> list = FXCollections.observableArrayList();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT * FROM bekleyen_rezervasyonlar WHERE durum = 'Beklemede'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            boolean kayitVar = false;
            while (rs.next()) {
                kayitVar = true;
                list.add(new BekleyenRezervasyon(
                    rs.getInt("talep_id"),
                    rs.getString("musteri_adi"),
                    rs.getString("musteri_soyadi"),
                    rs.getString("telefon"),
                    rs.getDate("tarih"),
                    rs.getString("saat"),
                    rs.getInt("kisi_sayisi"),
                    rs.getString("notlar")
                ));
            }
            if (!kayitVar) {
                showAlert(AlertType.INFORMATION, "Bilgi", "Bekleyen rezervasyon bulunamadı!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Veritabanı Hatası", e.getMessage());
        }
        tableView.setItems(list);
    }

    private void onaylaRezervasyon(BekleyenRezervasyon rez) {
        try (Connection conn = MySQLConnection.connect()) {
            // Onaylanan rezervasyonu ana tabloya ekle
            String insertSql = "INSERT INTO rezervasyonlar (musteri_adi, musteri_soyadi, telefon, tarih, saat, kisi_sayisi, notlar) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, rez.getAd());
            insertStmt.setString(2, rez.getSoyad());
            insertStmt.setString(3, rez.getTelefon());
            insertStmt.setDate(4, java.sql.Date.valueOf(rez.getTarih()));
            insertStmt.setString(5, rez.getSaat());
            insertStmt.setInt(6, Integer.parseInt(rez.getKisiSayisi()));
            insertStmt.setString(7, rez.getNotlar());
            insertStmt.executeUpdate();
            // Bekleyen rezervasyondan sil
            String deleteSql = "DELETE FROM bekleyen_rezervasyonlar WHERE talep_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, rez.getId());
            deleteStmt.executeUpdate();
            loadBekleyenRezervasyonlar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
