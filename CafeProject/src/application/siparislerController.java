package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

public class siparislerController implements Initializable {
    @FXML private GridPane masaGrid;
    @FXML private Pane icerikPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        masaGrid.getChildren().clear();
        if (icerikPanel != null) {
            masaGrid.setPrefWidth(icerikPanel.getWidth());
        }
        masaGrid.setHgap(30);
        masaGrid.setVgap(30);
        try (Connection conn = MySQLConnection.connect()) {
            String sql = "SELECT * FROM masalar";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int col = 0, row = 0;
            while (rs.next()) {
                final int masaId = rs.getInt("masa_id");
                final String masaAdi = rs.getString("masa_no");
                final String durum = rs.getString("durum");
                String arkaRenk;
                switch (durum.toLowerCase()) {
                    case "dolu": arkaRenk = "#4CAF50"; break;
                    case "kirli": arkaRenk = "#FF6347"; break;
                    default: arkaRenk = "#444"; break;
                }
                VBox kart = new VBox(12);
                kart.setAlignment(Pos.CENTER);
                kart.setPrefWidth(120);
                kart.setPrefHeight(120);
                kart.setStyle("-fx-background-color: " + arkaRenk + "; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, #00000055, 8,0,0,2);");
                Text masaText = new Text(masaAdi);
                masaText.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
                kart.getChildren().addAll(masaText);
                kart.setOnMouseClicked(ev -> {
                    if ("bos".equals(durum.toLowerCase())) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("siparisAcPopup.fxml"));
                            Parent root = loader.load();
                            siparisAcPopupController popupController = loader.getController();
                            popupController.setMasaId(masaId);
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setTitle("Sipariş Aç");
                            stage.setScene(new Scene(root));
                            stage.showAndWait();
                            initialize(null, null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if ("dolu".equals(durum.toLowerCase())) {
                        System.out.println("Sipariş detayı popup: " + masaAdi);
                        // siparisDetayPopup(masaId, masaAdi);
                    } else if ("kirli".equals(durum.toLowerCase())) {
                        System.out.println("Masayı temizle popup: " + masaAdi);
                        // masaTemizlePopup(masaId, masaAdi);
                    }
                });
                masaGrid.add(kart, col, row);
                col++;
                if (col > 5) { col = 0; row++; }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSiparislerButton() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("siparislerAdmin.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) masaGrid.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 