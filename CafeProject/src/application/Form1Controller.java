package application;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Form1Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField KullaniciAdiGiris;

    @FXML
    private TextField SifreGiris;

    @FXML
    void initialize() {
        assert KullaniciAdiGiris != null : "fx:id=\"KullaniciAdiGiris\" was not injected: check your FXML file 'Form.fxml'.";
        assert SifreGiris != null : "fx:id=\"SifreGiris\" was not injected: check your FXML file 'Form.fxml'.";

    }

}