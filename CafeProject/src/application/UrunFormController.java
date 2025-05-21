package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UrunFormController implements Initializable {
    @FXML private TextField adField;
    @FXML private TextField fiyatField;
    @FXML private TextField aciklamaField;
    @FXML private ImageView urunImageView;
    @FXML private Button resimSecButton;
    @FXML private Button kaydetButton;
    @FXML private Button iptalButton;

    private String seciliResimYolu = null;
    private boolean guncellemeModu = false;
    private int guncellenenUrunId = -1;

    public static interface UrunFormListener {
        void onKaydet(String ad, double fiyat, String resimYolu, String aciklama, Integer urunId);
        void onIptal();
    }
    private UrunFormListener listener;
    public void setListener(UrunFormListener listener) { this.listener = listener; }

    public void setFormData(String ad, double fiyat, String resimYolu, String aciklama, Integer urunId) {
        adField.setText(ad);
        fiyatField.setText(String.valueOf(fiyat));
        aciklamaField.setText(aciklama != null ? aciklama : "");
        if (resimYolu != null && !resimYolu.isEmpty()) {
            urunImageView.setImage(new Image("file:" + resimYolu));
            seciliResimYolu = resimYolu;
        }
        if (urunId != null) {
            guncellemeModu = true;
            guncellenenUrunId = urunId;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resimSecButton.setOnAction(e -> resimSec());
        kaydetButton.setOnAction(e -> kaydet());
        iptalButton.setOnAction(e -> iptal());
    }

    // .
    private void resimSec() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Resim Seç");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg")
        );
        File secilen = fileChooser.showOpenDialog(adField.getScene().getWindow());
        if (secilen != null) {
            try {
                File imagesDir = new File("images");
                if (!imagesDir.exists()) imagesDir.mkdir();
                String hedefYol = "images/" + secilen.getName();
                File hedef = new File(hedefYol);
                try (FileInputStream in = new FileInputStream(secilen); FileOutputStream out = new FileOutputStream(hedef)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                seciliResimYolu = hedefYol;
                urunImageView.setImage(new Image("file:" + seciliResimYolu));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void kaydet() {
        String ad = adField.getText();
        double fiyat = 0.0;
        try { fiyat = Double.parseDouble(fiyatField.getText()); } catch (Exception ignored) {}
        String aciklama = aciklamaField.getText();
        if (listener != null) {
            listener.onKaydet(ad, fiyat, seciliResimYolu, aciklama, guncellemeModu ? guncellenenUrunId : null);
        }
        pencereyiKapat();
    }

    private void iptal() {
        if (listener != null) listener.onIptal();
        pencereyiKapat();
    }

    private void pencereyiKapat() {
        Stage stage = (Stage) adField.getScene().getWindow();
        stage.close();
    }
} 