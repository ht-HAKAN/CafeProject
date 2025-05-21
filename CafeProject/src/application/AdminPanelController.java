package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminPanelController {
    @FXML private Button menuYonetimiBtn;
    @FXML private Button rezervasyonYonetimiBtn;
    @FXML private Button siparisYonetimiBtn;
    @FXML private Button personelYonetimiBtn;
    @FXML private AnchorPane icerikAnchor;

    @FXML
    private void initialize() {
        menuYonetimiBtn.setOnAction(e -> loadPanel("menuYonet.fxml"));
        rezervasyonYonetimiBtn.setOnAction(e -> loadPanel("rezervasyonAdminSistemi.fxml"));
        siparisYonetimiBtn.setOnAction(e -> loadPanel("siparislerAdmin.fxml"));
        personelYonetimiBtn.setOnAction(e -> loadPanel("personelYonetimi.fxml"));
    }

    private void loadPanel(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(fxml.replace(".fxml", ""));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setAdmin(boolean isAdmin) {
    }
    
    public void setAdminName(String name) {
    	
    }
}
