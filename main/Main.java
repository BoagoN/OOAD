package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setTitle("Banking System - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Could not load LoginView.fxml. Check file path.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
