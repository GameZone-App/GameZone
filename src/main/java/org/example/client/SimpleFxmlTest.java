package org.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * A simple test application that first tries to load a basic UI,
 * then attempts to load the MainMenu.fxml if available
 */
public class SimpleFxmlTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // First try with a simple UI to ensure JavaFX is working
        try {
            // Attempt to load the FXML file
            URL fxmlUrl = getClass().getResource("/fxml/MainMenu.fxml");
            
            if (fxmlUrl != null) {
                // FXML file found, try to load it
                System.out.println("Found FXML file, attempting to load: " + fxmlUrl);
                
                Parent root = FXMLLoader.load(fxmlUrl);
                Scene scene = new Scene(root, 800, 600);
                primaryStage.setTitle("Game Lobby - FXML Loaded");
                primaryStage.setScene(scene);
            } else {
                // FXML file not found, create a simple UI
                System.out.println("FXML file not found, creating basic UI");
                
                Label label = new Label("JavaFX is working, but FXML file not found");
                Button button = new Button("Click Me");
                button.setOnAction(e -> label.setText("Button clicked!"));
                
                VBox root = new VBox(10, label, button);
                root.setStyle("-fx-padding: 20; -fx-alignment: center;");
                
                Scene scene = new Scene(root, 300, 200);
                primaryStage.setTitle("JavaFX Test - No FXML");
                primaryStage.setScene(scene);
            }
            
            primaryStage.show();
            
        } catch (Exception e) {
            // If any exception occurs, show the error in a simple UI
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            
            Label errorLabel = new Label("Error: " + e.getMessage());
            VBox errorRoot = new VBox(10, errorLabel);
            errorRoot.setStyle("-fx-padding: 20; -fx-alignment: center;");
            
            Scene errorScene = new Scene(errorRoot, 500, 200);
            primaryStage.setTitle("JavaFX Error");
            primaryStage.setScene(errorScene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
