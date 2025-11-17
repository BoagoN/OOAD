package app;

import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the LoginView.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            VBox root = loader.load();

            // Get the LoginController and set the stage
            LoginController controller = loader.getController();
            controller.setStage(primaryStage);

            // Apply your styling (from your original layout)
            Scene scene = new Scene(root, 900, 550);
            primaryStage.setTitle("🏦 Banking System");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
