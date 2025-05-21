package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("Form.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			
			Scene scene = new Scene(root,400,540);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Gelişim Kafe - Giriş");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Dosya yükleme hatası: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
 //.

