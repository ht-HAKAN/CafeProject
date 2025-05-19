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

public class BekleyenRezervasyonlarController {
    @FXML private TableView<BekleyenRezervasyon> tableView;
    @FXML private TableColumn<BekleyenRezervasyon, String> adColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> soyadColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> telefonColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> tarihColumn;
    @FXML private TableColumn<BekleyenRezervasyon, String> saatColumn;
    @FXML private TableColumn<BekleyenRezervasyon, Integer> kisiSayisiColumn;
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
        onaylaColumn.setCellFactory(new Callback<TableColumn<BekleyenRezervasyon, Void>, TableCell<BekleyenRezervasyon, Void>>() {
            @Override
            public TableCell<BekleyenRezervasyon, Void> call(final TableColumn<BekleyenRezervasyon, Void> param) {
                return new TableCell<BekleyenRezervasyon, Void>() {
                    private final Button btn = new Button("Onayla");
                    {
                        btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        btn.setOnAction((event) -> {
                            BekleyenRezervasyon data = getTableView().getItems().get(getIndex());
                            onaylaRezervasyon(data);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });
    }

    private void loadBekleyenRezervasyonlar() {
        ObservableList<BekleyenRezervasyon> list = FXCollections.observableArrayList();
        try {
            String sql = "SELECT talep_id, musteri_adi, musteri_soyadi, telefon, tarih, saat, kisi_sayisi, notlar FROM bekleyen_rezervasyonlar WHERE durum = 'Beklemede' ORDER BY talep_tarihi DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int talepId = rs.getInt("talep_id");
                String ad = rs.getString("musteri_adi");
                String soyad = rs.getString("musteri_soyadi");
                String telefon = rs.getString("telefon");
                String tarih = rs.getString("tarih");
                String saat = rs.getString("saat");
                int kisiSayisi = rs.getInt("kisi_sayisi");
                String notlar = rs.getString("notlar");
                list.add(new BekleyenRezervasyon(talepId, ad, soyad, telefon, tarih, saat, kisiSayisi, notlar));
            }
            tableView.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onaylaRezervasyon(BekleyenRezervasyon rezervasyon) {
        try {
            // 1. Onaylanan rezervasyonu onaylanan_rezervasyonlar tablosuna ekle
            String insertOnay = "INSERT INTO onaylanan_rezervasyonlar (talep_id) VALUES (?)";
            PreparedStatement stmtOnay = connection.prepareStatement(insertOnay);
            stmtOnay.setInt(1, rezervasyon.getTalepId());
            stmtOnay.executeUpdate();
            // 2. Rezervasyonu rezervasyonlar tablosuna ekle (masa_id NULL olarak)
            String insertRez = "INSERT INTO rezervasyonlar (masa_id, musteri_adi, musteri_soyadi, telefon, tarih, saat, kisi_sayisi, notlar) VALUES (NULL,?,?,?,?,?,?,?)";
            PreparedStatement stmtRez = connection.prepareStatement(insertRez);
            stmtRez.setString(1, rezervasyon.getAd());
            stmtRez.setString(2, rezervasyon.getSoyad());
            stmtRez.setString(3, rezervasyon.getTelefon());
            stmtRez.setString(4, rezervasyon.getTarih());
            stmtRez.setString(5, rezervasyon.getSaat());
            stmtRez.setInt(6, rezervasyon.getKisiSayisi());
            stmtRez.setString(7, rezervasyon.getNotlar());
            stmtRez.executeUpdate();
            // 3. Bekleyen rezervasyonu sil
            String deleteBekleyen = "DELETE FROM bekleyen_rezervasyonlar WHERE talep_id = ?";
            PreparedStatement stmtDel = connection.prepareStatement(deleteBekleyen);
            stmtDel.setInt(1, rezervasyon.getTalepId());
            stmtDel.executeUpdate();
            showAlert(AlertType.INFORMATION, "Başarılı", "Rezervasyon onaylandı ve listeye eklendi!");
            loadBekleyenRezervasyonlar();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Hata", "Rezervasyon onaylanırken hata oluştu: " + e.getMessage());
        }
    }

    public static class BekleyenRezervasyon {
        private int talepId;
        private String ad;
        private String soyad;
        private String telefon;
        private String tarih;
        private String saat;
        private int kisiSayisi;
        private String notlar;
        public BekleyenRezervasyon(int talepId, String ad, String soyad, String telefon, String tarih, String saat, int kisiSayisi, String notlar) {
            this.talepId = talepId;
            this.ad = ad;
            this.soyad = soyad;
            this.telefon = telefon;
            this.tarih = tarih;
            this.saat = saat;
            this.kisiSayisi = kisiSayisi;
            this.notlar = notlar;
        }
        public int getTalepId() { return talepId; }
        public String getAd() { return ad; }
        public String getSoyad() { return soyad; }
        public String getTelefon() { return telefon; }
        public String getTarih() { return tarih; }
        public String getSaat() { return saat; }
        public int getKisiSayisi() { return kisiSayisi; }
        public String getNotlar() { return notlar; }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
