package org.example.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Simple test application to verify JavaFX is working correctly
 */
public class TestJavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("JavaFX Test App");
        Button button = new Button("Click Me");
        button.setOnAction(e -> label.setText("Button clicked!"));
        
        VBox root = new VBox(10, label, button);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
