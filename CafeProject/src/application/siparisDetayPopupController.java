package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;

public class siparisDetayPopupController implements Initializable {
    @FXML private TableView<SiparisRow> siparisTable;
    @FXML private TableColumn<SiparisRow, String> urunCol;
    @FXML private TableColumn<SiparisRow, Integer> miktarCol;
    @FXML private TableColumn<SiparisRow, String> tutarCol;
    @FXML private Text toplamTutarText;
    @FXML private Button hesapKesButton;
    @FXML private Button temizleButton;
    @FXML private Button kapatButton;

    private int masaId;
    private ObservableList<SiparisRow> siparisList = FXCollections.observableArrayList();
    private double toplamTutar = 0;

    public void setMasaId(int masaId) {
        this.masaId = masaId;
        siparisleriYukle();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        urunCol.setCellValueFactory(data -> data.getValue().urunAdiProperty());
        miktarCol.setCellValueFactory(data -> data.getValue().miktarProperty().asObject());
        tutarCol.setCellValueFactory(data -> data.getValue().tutarStrProperty());
        siparisTable.setItems(siparisList);
        hesapKesButton.setOnAction(e -> hesapKes());
        temizleButton.setOnAction(e -> temizleMasa());
        kapatButton.setOnAction(e -> ((Stage) kapatButton.getScene().getWindow()).close());
    }

    private void siparisleriYukle() {
        siparisList.clear();
        toplamTutar = 0;
        try {
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT s.siparis_id, m.urun_adi, s.miktar, s.toplam_tutar FROM siparisler s JOIN menu m ON s.urun_id = m.urun_id WHERE s.masa_id = ? AND s.durum = 'aktif'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, masaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String urunAdi = rs.getString("urun_adi");
                int miktar = rs.getInt("miktar");
                double tutar = rs.getDouble("toplam_tutar");
                toplamTutar += tutar;
                siparisList.add(new SiparisRow(urunAdi, miktar, tutar));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        toplamTutarText.setText(String.format("%.2f TL", toplamTutar));
    }

    private void hesapKes() {
        try {
            Connection conn = MySQLConnection.connect();
            // Siparişleri kapalı yap ve uygula
            PreparedStatement stmt = conn.prepareStatement("UPDATE siparisler SET durum = 'kapali' WHERE masa_id = ? AND durum = 'aktif'");
            stmt.setInt(1, masaId);
            stmt.executeUpdate();
            stmt.close();
            // Masayı kirli yap ve uygula
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'kirli' WHERE masa_id = ?");
            masaStmt.setInt(1, masaId);
            masaStmt.executeUpdate();
            masaStmt.close();
            conn.close();
            siparisleriYukle();
            toplamTutarText.setText("Hesap Kesildi: " + String.format("%.2f TL", toplamTutar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void temizleMasa() {
        try {
            Connection conn = MySQLConnection.connect();
            // Masayı boş yap
            PreparedStatement masaStmt = conn.prepareStatement("UPDATE masalar SET durum = 'bos' WHERE masa_id = ?");
            masaStmt.setInt(1, masaId);
            masaStmt.executeUpdate();
            masaStmt.close();
            // Kapalı siparişleri sil
            PreparedStatement silStmt = conn.prepareStatement("DELETE FROM siparisler WHERE masa_id = ? AND durum = 'kapali'");
            silStmt.setInt(1, masaId);
            silStmt.executeUpdate();
            silStmt.close();
            conn.close();
            siparisleriYukle();
        } catch (Exception e) {
            e.printStackTrace();
        }
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