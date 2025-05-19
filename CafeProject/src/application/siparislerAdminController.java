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
        addSilButtonToTable();
        updatePanel(null);
    }

    private void loadUrunler() {
        urunComboBox.getItems().clear();
        try (Connection conn = MySQLConnection.connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT urun_adi FROM menu");
            while (rs.next()) {
                urunComboBox.getItems().add(rs.getString("urun_adi"));
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
            String sql = "SELECT s.siparis_id, m.urun_adi, s.miktar, s.toplam_tutar FROM siparisler s JOIN menu m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND s.durum = 'aktif'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, seciliMasaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String urunAdi = rs.getString("urun_adi");
                int miktar = rs.getInt("miktar");
                double tutar = rs.getDouble("toplam_tutar");
                toplamTutar += tutar;
                siparisList.add(new SiparisRow(urunAdi, miktar, tutar));
            }
            rs.close(); stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        toplamTutarText.setText(String.format("%.2f TL", toplamTutar));
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
        int miktar;
        try { miktar = Integer.parseInt(miktarStr); } catch (NumberFormatException ex) { showAlert("Hata", "Miktar sayısal olmalı!"); return; }
        if (miktar <= 0) { showAlert("Hata", "Miktar sıfırdan büyük olmalı!"); return; }
        try (Connection conn = MySQLConnection.connect()) {
            PreparedStatement checkStmt = conn.prepareStatement("SELECT siparis_id, miktar, toplam_tutar FROM siparisler s JOIN menu m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND m.urun_adi = ? AND s.durum = 'aktif'");
            checkStmt.setInt(1, seciliMasaId);
            checkStmt.setString(2, urun);
            ResultSet checkRS = checkStmt.executeQuery();
            if (checkRS.next()) {
                int siparisId = checkRS.getInt("siparis_id");
                int eskiMiktar = checkRS.getInt("miktar");
                double eskiTutar = checkRS.getDouble("toplam_tutar");
                PreparedStatement urunStmt = conn.prepareStatement("SELECT fiyat FROM menu WHERE urun_adi = ?");
                urunStmt.setString(1, urun);
                ResultSet urunRS = urunStmt.executeQuery();
                if (!urunRS.next()) return;
                double fiyat = urunRS.getDouble("fiyat");
                urunRS.close(); urunStmt.close();
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE siparisler SET miktar = ?, toplam_tutar = ? WHERE siparis_id = ?");
                updateStmt.setInt(1, eskiMiktar + miktar);
                updateStmt.setDouble(2, fiyat * (eskiMiktar + miktar));
                updateStmt.setInt(3, siparisId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                PreparedStatement urunStmt = conn.prepareStatement("SELECT urun_id, fiyat FROM menu WHERE urun_adi = ?");
                urunStmt.setString(1, urun);
                ResultSet urunRS = urunStmt.executeQuery();
                if (!urunRS.next()) return;
                int urunId = urunRS.getInt("urun_id");
                double fiyat = urunRS.getDouble("fiyat");
                urunRS.close(); urunStmt.close();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO siparisler (masa_id, urun_id, miktar, toplam_tutar, durum) VALUES (?, ?, ?, ?, 'aktif')");
                stmt.setInt(1, seciliMasaId);
                stmt.setInt(2, urunId);
                stmt.setInt(3, miktar);
                stmt.setDouble(4, fiyat * miktar);
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
        try (Connection conn = MySQLConnection.connect()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE siparisler SET durum = 'kapali' WHERE masa_id = ? AND durum = 'aktif'");
            stmt.setInt(1, seciliMasaId);
            stmt.executeUpdate();
            stmt.close();
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'kirli' WHERE masa_id = ?");
            masaStmt.setInt(1, seciliMasaId);
            masaStmt.executeUpdate();
            masaStmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        loadMasalar();
        updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, "kirli", seciliMasaKonum));
    }

    private void temizleMasa() {
        if (seciliMasaId == -1) return;
        try (Connection conn = MySQLConnection.connect()) {
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'bos' WHERE masa_id = ?");
            masaStmt.setInt(1, seciliMasaId);
            masaStmt.executeUpdate();
            masaStmt.close();
            PreparedStatement silStmt = conn.prepareStatement("DELETE FROM siparisler WHERE masa_id = ? AND durum = 'kapali'");
            silStmt.setInt(1, seciliMasaId);
            silStmt.executeUpdate();
            silStmt.close();
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
            // Sipariş id'sini bul
            String sql = "SELECT s.siparis_id FROM siparisler s JOIN menu m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND m.urun_adi = ? AND s.durum = 'aktif'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, seciliMasaId);
            stmt.setString(2, row.urunAdiProperty().get());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int siparisId = rs.getInt("siparis_id");
                PreparedStatement delStmt = conn.prepareStatement("DELETE FROM siparisler WHERE siparis_id = ?");
                delStmt.setInt(1, siparisId);
                delStmt.executeUpdate();
                delStmt.close();
            }
            rs.close(); stmt.close();
            // Masada başka aktif sipariş var mı kontrol et
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM siparisler WHERE masa_id = ? AND durum = 'aktif'");
            checkStmt.setInt(1, seciliMasaId);
            ResultSet checkRS = checkStmt.executeQuery();
            if (checkRS.next() && checkRS.getInt(1) == 0) {
                PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'bos' WHERE masa_id = ?");
                masaStmt.setInt(1, seciliMasaId);
                masaStmt.executeUpdate();
                masaStmt.close();
            }
            checkRS.close(); checkStmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        loadMasalar();
        updatePanel(new MasaSecim(seciliMasaId, seciliMasaNo, seciliMasaKapasite, "bos", seciliMasaKonum));
    }

    private void showAlert(String baslik, String mesaj) {
        Alert alert = new Alert(AlertType.ERROR);
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