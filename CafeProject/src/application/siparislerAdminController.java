package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class siparislerAdminController implements Initializable {
    @FXML private GridPane masalarGrid;
    @FXML private Text panelBaslik;
    @FXML private Text masaBilgiText;
    @FXML private TableView<SiparisRow> siparisTable;
    @FXML private TableColumn<SiparisRow, String> urunCol;
    @FXML private TableColumn<SiparisRow, Integer> miktarCol;
    @FXML private TableColumn<SiparisRow, String> tutarCol;
    @FXML private Text toplamTutarText;
    @FXML private ComboBox<String> urunComboBox;
    @FXML private TextField miktarField;
    @FXML private Button siparisEkleButton;
    @FXML private Button hesapKesButton;
    @FXML private Button temizleButton;
    @FXML private ComboBox<String> durumComboBox;
    @FXML private Button durumGuncelleButton;
    @FXML private TableColumn<SiparisRow, Void> silCol;
    @FXML private Button tumSiparisleriSilButton;

    private int seciliMasaId = -1;
    private String seciliMasaNo = "";
    private String seciliMasaDurum = "";
    private String seciliMasaKonum = "";
    private int seciliMasaKapasite = 0;
    private ObservableList<SiparisRow> siparisList = FXCollections.observableArrayList();
    private double toplamTutar = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        urunCol.setCellValueFactory(data -> data.getValue().urunAdiProperty());
        miktarCol.setCellValueFactory(data -> data.getValue().miktarProperty().asObject());
        tutarCol.setCellValueFactory(data -> data.getValue().tutarStrProperty());
        siparisTable.setItems(siparisList);
        loadUrunler();
        urunComboBox.setDisable(true);
        miktarField.setDisable(true);
        siparisEkleButton.setDisable(true);
        durumComboBox.setItems(FXCollections.observableArrayList("bos", "dolu", "kirli"));
        durumGuncelleButton.setOnAction(e -> masaDurumGuncelle());
        loadMasalar();
        siparisEkleButton.setOnAction(e -> siparisEkle());
        hesapKesButton.setOnAction(e -> hesapKes());
        temizleButton.setOnAction(e -> temizleMasa());
        if (tumSiparisleriSilButton != null) {
            tumSiparisleriSilButton.setOnAction(e -> tumSiparisleriSil());
        }
        updatePanel(null);
    }

    private void loadUrunler() {
        urunComboBox.getItems().clear();
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT ad FROM menu_urunler";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    urunComboBox.getItems().add(rs.getString("ad"));
                }
                rs.close();
            } catch (Exception e) {
                try (PreparedStatement stmt2 = conn.prepareStatement("SELECT urun_adi FROM menu")) {
                    ResultSet rs2 = stmt2.executeQuery();
                    while (rs2.next()) {
                        urunComboBox.getItems().add(rs2.getString("urun_adi"));
                    }
                    rs2.close();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMasalar() {
        masalarGrid.getChildren().clear();
        try (Connection conn = MySQLConnection.connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM masalar ORDER BY masa_id");
            int row = 0, col = 0, colCount = 4;
            while (rs.next()) {
                int masaId = rs.getInt("masa_id");
                String masaNo = rs.getString("masa_no");
                int kapasite = rs.getInt("kapasite");
                String durum = rs.getString("durum");
                String konum = rs.getString("konum");
                StackPane stackPane = new StackPane();
                stackPane.setMinSize(100, 100);
                stackPane.setMaxSize(100, 100);
                Rectangle rect = new Rectangle(100, 100);
                rect.setArcWidth(15);
                rect.setArcHeight(15);
                switch (durum.toLowerCase()) {
                    case "bos": rect.setFill(Color.GREEN); break;
                    case "dolu": rect.setFill(Color.RED); break;
                    case "kirli": rect.setFill(Color.YELLOW); break;
                    default: rect.setFill(Color.GRAY);
                }
                VBox infoBox = new VBox(3);
                Text masaNoText = new Text(masaNo);
                masaNoText.setFill(Color.WHITE);
                masaNoText.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
                Text kapasiteText = new Text("Kapasite: " + kapasite);
                kapasiteText.setFill(Color.WHITE);
                Text konumText = new Text(konum);
                konumText.setFill(Color.WHITE);
                infoBox.getChildren().addAll(masaNoText, kapasiteText, konumText);
                stackPane.getChildren().addAll(rect, infoBox);
                stackPane.setOnMouseClicked((MouseEvent event) -> {
                    updatePanel(new MasaSecim(masaId, masaNo, kapasite, durum, konum));
                });
                masalarGrid.add(stackPane, col, row);
                col++;
                if (col >= colCount) { col = 0; row++; }
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updatePanel(MasaSecim masa) {
        if (masa == null) {
            seciliMasaId = -1;
            masaBilgiText.setText("Masa seçiniz...");
            siparisList.clear();
            toplamTutarText.setText("0 TL");
            urunComboBox.setDisable(true);
            miktarField.setDisable(true);
            siparisEkleButton.setDisable(true);
            hesapKesButton.setDisable(true);
            temizleButton.setDisable(true);
            durumComboBox.setDisable(true);
            durumGuncelleButton.setDisable(true);
            return;
        }
        seciliMasaId = masa.masaId;
        seciliMasaNo = masa.masaNo;
        seciliMasaKapasite = masa.kapasite;
        seciliMasaDurum = masa.durum;
        seciliMasaKonum = masa.konum;
        masaBilgiText.setText("Masa No: " + seciliMasaNo + "\nKapasite: " + seciliMasaKapasite + "\nKonum: " + seciliMasaKonum + "\nDurum: " + seciliMasaDurum.toUpperCase());
        loadUrunler();
        urunComboBox.setDisable(false);
        miktarField.setDisable(false);
        siparisEkleButton.setDisable(false);
        durumComboBox.setDisable(false);
        durumComboBox.setValue(seciliMasaDurum);
        durumGuncelleButton.setDisable(false);
        loadSiparisler();
        hesapKesButton.setDisable(siparisList.isEmpty());
        temizleButton.setDisable(!(seciliMasaDurum.equalsIgnoreCase("kirli")));
    }

    private void loadSiparisler() {
        siparisList.clear();
        toplamTutar = 0;
        if (seciliMasaId == -1) return;
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT s.siparis_id, m.ad as urun_adi, s.adet, s.adet * m.fiyat as toplam_tutar FROM siparisler s JOIN menu_urunler m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND s.durum = 'aktif'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, seciliMasaId);
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery();
            } catch (SQLException e) {
                sql = "SELECT s.siparis_id, m.urun_adi, s.adet, s.adet * m.fiyat as toplam_tutar FROM siparisler s JOIN menu m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND s.durum = 'aktif'";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, seciliMasaId);
                rs = stmt.executeQuery();
            }
            while (rs.next()) {
                String urunAdi = rs.getString("urun_adi");
                int adet = rs.getInt("adet");
                double tutar = rs.getDouble("toplam_tutar");
                toplamTutar += tutar;
                siparisList.add(new SiparisRow(urunAdi, adet, tutar));
            }
            if (rs != null) rs.close();
            stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        toplamTutarText.setText(String.format("%.2f TL", toplamTutar));
        siparisTable.refresh();
    }

    private void addSilButtonToTable() {
        silCol.setCellFactory(new Callback<TableColumn<SiparisRow, Void>, TableCell<SiparisRow, Void>>() {
            @Override
            public TableCell<SiparisRow, Void> call(final TableColumn<SiparisRow, Void> param) {
                final TableCell<SiparisRow, Void> cell = new TableCell<SiparisRow, Void>() {
                    private final Button btn = new Button("Sil");
                    {
                        btn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8;");
                        btn.setOnAction((event) -> {
                            SiparisRow data = getTableView().getItems().get(getIndex());
                            siparisSil(data);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void siparisEkle() {
        if (seciliMasaId == -1) return;
        String urun = urunComboBox.getValue();
        String miktarStr = miktarField.getText();
        if (urun == null || urun.isEmpty() || miktarStr == null || miktarStr.isEmpty()) {
            showAlert("Hata", "Ürün ve miktar alanları boş olamaz!");
            return;
        }
        int adet;
        try { adet = Integer.parseInt(miktarStr); } catch (NumberFormatException ex) { showAlert("Hata", "Miktar sayısal olmalı!"); return; }
        if (adet <= 0) { showAlert("Hata", "Miktar sıfırdan büyük olmalı!"); return; }
        try (Connection conn = MySQLConnection.connect()) {
            int urunId = -1; double fiyat = 0;
            try (PreparedStatement urunStmt = conn.prepareStatement("SELECT urun_id, fiyat FROM menu_urunler WHERE ad = ?")) {
                urunStmt.setString(1, urun);
                ResultSet urunRS = urunStmt.executeQuery();
                if (urunRS.next()) {
                    urunId = urunRS.getInt("urun_id");
                    fiyat = urunRS.getDouble("fiyat");
                }
                urunRS.close();
            }
            if (urunId == -1) {
                try (PreparedStatement urunStmt2 = conn.prepareStatement("SELECT urun_id, fiyat FROM menu WHERE urun_adi = ?")) {
                    urunStmt2.setString(1, urun);
                    ResultSet urunRS2 = urunStmt2.executeQuery();
                    if (urunRS2.next()) {
                        urunId = urunRS2.getInt("urun_id");
                        fiyat = urunRS2.getDouble("fiyat");
                    }
                    urunRS2.close();
                }
            }
            if (urunId == -1) { showAlert("Hata", "Ürün bulunamadı!"); return; }
            PreparedStatement checkStmt = conn.prepareStatement("SELECT siparis_id, adet FROM siparisler WHERE masa_id = ? AND urun_id = ? AND durum = 'aktif'");
            checkStmt.setInt(1, seciliMasaId);
            checkStmt.setInt(2, urunId);
            ResultSet checkRS = checkStmt.executeQuery();
            if (checkRS.next()) {
                int siparisId = checkRS.getInt("siparis_id");
                int eskiAdet = checkRS.getInt("adet");
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE siparisler SET adet = ?, aciklama = NULL WHERE siparis_id = ?");
                updateStmt.setInt(1, eskiAdet + adet);
                updateStmt.setInt(2, siparisId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO siparisler (masa_id, urun_id, adet, durum) VALUES (?, ?, ?, 'aktif')");
                stmt.setInt(1, seciliMasaId);
                stmt.setInt(2, urunId);
                stmt.setInt(3, adet);
                stmt.executeUpdate();
                stmt.close();
            }
            checkRS.close(); checkStmt.close();
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'dolu' WHERE masa_id = ?");
            masaStmt.setInt(1, seciliMasaId);
            masaStmt.executeUpdate();
            masaStmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        loadMasalar();
        updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, "dolu", seciliMasaKonum));
        miktarField.clear();
    }

    private void hesapKes() {
        if (seciliMasaId == -1) return;
        double hesapTutari = toplamTutar; // Toplamı kaydet
        try (Connection conn = MySQLConnection.connect()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE siparisler SET durum = 'hesap_kesildi' WHERE masa_id = ? AND durum = 'aktif'");
            stmt.setInt(1, seciliMasaId);
            stmt.executeUpdate();
            stmt.close();
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'kirli' WHERE masa_id = ?");
            masaStmt.setInt(1, seciliMasaId);
            masaStmt.executeUpdate();
            masaStmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        loadMasalar();
        showAlert("Hesap Kesildi", String.format(
            "Toplam: %.2f TL\nHesap başarıyla kesildi.\nLütfen müşteriye fişini teslim edin.", hesapTutari));
        updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, "kirli", seciliMasaKonum));
    }

    private void temizleMasa() {
        if (seciliMasaId == -1) return;
        try (Connection conn = MySQLConnection.connect()) {
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'bos' WHERE masa_id = ?");
            masaStmt.setInt(1, seciliMasaId);
            masaStmt.executeUpdate();
            masaStmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        loadMasalar();
        updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, "bos", seciliMasaKonum));
    }

    private void masaDurumGuncelle() {
        if (seciliMasaId == -1) return;
        String yeniDurum = durumComboBox.getValue();
        if (yeniDurum == null || yeniDurum.isEmpty()) return;
        try (Connection conn = MySQLConnection.connect()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE masalar SET durum = ? WHERE masa_id = ?");
            stmt.setString(1, yeniDurum);
            stmt.setInt(2, seciliMasaId);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        loadMasalar();
        updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, yeniDurum, seciliMasaKonum));
    }

    private void siparisSil(SiparisRow row) {
        if (seciliMasaId == -1 || row == null) return;
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT s.siparis_id FROM siparisler s JOIN menu_urunler m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND m.ad = ? AND s.durum = 'aktif'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, seciliMasaId);
            stmt.setString(2, row.urunAdiProperty().get());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                sql = "SELECT s.siparis_id FROM siparisler s JOIN menu m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND m.urun_adi = ? AND s.durum = 'aktif'";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, seciliMasaId);
                stmt.setString(2, row.urunAdiProperty().get());
                rs = stmt.executeQuery();
            }
            if (rs.next()) {
                int siparisId = rs.getInt("siparis_id");
                PreparedStatement delStmt = conn.prepareStatement("DELETE FROM siparisler WHERE siparis_id = ?");
                delStmt.setInt(1, siparisId);
                delStmt.executeUpdate();
                delStmt.close();
            }
            rs.close(); stmt.close();
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM siparisler WHERE masa_id = ? AND durum = 'aktif'");
            checkStmt.setInt(1, seciliMasaId);
            ResultSet checkRS = checkStmt.executeQuery();
            boolean masaBos = false;
            if (checkRS.next() && checkRS.getInt(1) == 0) {
                masaBos = true;
                PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'bos' WHERE masa_id = ?");
                masaStmt.setInt(1, seciliMasaId);
                masaStmt.executeUpdate();
                masaStmt.close();
            }
            checkRS.close(); checkStmt.close();
            loadSiparisler();
            siparisTable.refresh();
            if (masaBos) {
                updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, "bos", seciliMasaKonum));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void tumSiparisleriSil() {
        if (seciliMasaId == -1) return;
        try (Connection conn = MySQLConnection.connect()) {
            PreparedStatement delStmt = conn.prepareStatement("DELETE FROM siparisler WHERE masa_id = ? AND durum = 'aktif'");
            delStmt.setInt(1, seciliMasaId);
            delStmt.executeUpdate();
            delStmt.close();
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'bos' WHERE masa_id = ?");
            masaStmt.setInt(1, seciliMasaId);
            masaStmt.executeUpdate();
            masaStmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        loadSiparisler();
        siparisTable.refresh();
        updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, "bos", seciliMasaKonum));
    }

    private void showAlert(String baslik, String mesaj) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    // Yardımcı veri modeli
    private static class MasaSecim {
        int masaId; String masaNo; int kapasite; String durum; String konum;
        MasaSecim(int id, String no, int kap, String dr, String kn) { masaId = id; masaNo = no; kapasite = kap; durum = dr; konum = kn; }
    }
    public static class SiparisRow {
        private final SimpleStringProperty urunAdi;
        private final SimpleIntegerProperty miktar;
        private final SimpleDoubleProperty tutar;
        public SiparisRow(String urunAdi, int miktar, double tutar) {
            this.urunAdi = new SimpleStringProperty(urunAdi);
            this.miktar = new SimpleIntegerProperty(miktar);
            this.tutar = new SimpleDoubleProperty(tutar);
        }
        public SimpleStringProperty urunAdiProperty() { return urunAdi; }
        public SimpleIntegerProperty miktarProperty() { return miktar; }
        public SimpleDoubleProperty tutarProperty() { return tutar; }
        public SimpleStringProperty tutarStrProperty() { return new SimpleStringProperty(String.format("%.2f TL", tutar.get())); }
    }
} 