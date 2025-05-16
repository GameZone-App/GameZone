package org.example.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.client.model.ClientLobby;

/**
 * A simplified JavaFX client for the Game Lobby that doesn't use FXML
 * but still provides the same functionality.
 */
public class SimpleGameLobbyClient extends Application {

    private Stage primaryStage;
    private ClientLobby clientLobby;
    private String playerName = "";
    private ListView<String> chatListView;
    private TextField messageField;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        clientLobby = ClientLobby.getInstance();
        showLoginScreen();
    }
    
    private void showLoginScreen() {
        // Create a simple login screen
        Label titleLabel = new Label("Game Lobby");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label nameLabel = new Label("Enter your player name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Your Name");
        nameField.setPrefWidth(300);
        
        Button joinButton = new Button("Join Lobby");
        joinButton.setOnAction(e -> {
            playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                showLobbyScreen();
            } else {
                showAlert("Error", "Please enter a player name.");
            }
        });
        
        VBox root = new VBox(20, titleLabel, nameLabel, nameField, joinButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center; -fx-background-color: #2C3E50;");
        titleLabel.setStyle(titleLabel.getStyle() + "; -fx-text-fill: white;");
        nameLabel.setStyle("-fx-text-fill: white;");
        joinButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Game Lobby");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void showLobbyScreen() {
        // Connect to the server
        try {
            clientLobby.connect("ws://localhost:8080/lobby-websocket", playerName, message -> {
                Platform.runLater(() -> chatListView.getItems().add(message));
            });
            
            // Join the lobby
            clientLobby.join();
        } catch (Exception e) {
            showAlert("Connection Error", "Failed to connect to the lobby server: " + e.getMessage());
            return;
        }
        
        // Create the lobby chat UI
        Label titleLabel = new Label("Lobby Chat");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        chatListView = new ListView<>();
        chatListView.setPrefHeight(400);
        chatListView.setStyle("-fx-background-color: #34495E; -fx-control-inner-background: #34495E;");
        
        messageField = new TextField();
        messageField.setPromptText("Type your message...");
        messageField.setPrefHeight(40);
        messageField.setOnAction(e -> sendMessage());
        
        Button sendButton = new Button("Send");
        sendButton.setPrefHeight(40);
        sendButton.setPrefWidth(100);
        sendButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        sendButton.setOnAction(e -> sendMessage());
        
        HBox messageBox = new HBox(10, messageField, sendButton);
        messageBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button leaveButton = new Button("Leave Lobby");
        leaveButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        leaveButton.setOnAction(e -> {
            clientLobby.leave();
            showLoginScreen();
        });
        
        VBox centerBox = new VBox(10, chatListView, messageBox);
        centerBox.setPadding(new Insets(10));
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2C3E50;");
        
        HBox topBox = new HBox(titleLabel);
        topBox.setPadding(new Insets(15));
        topBox.setStyle("-fx-background-color: #34495E;");
        
        HBox bottomBox = new HBox(leaveButton);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setStyle("-fx-background-color: #34495E; -fx-alignment: center-right;");
        
        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setBottom(bottomBox);
        
        // Add a welcome message
        chatListView.getItems().add("Welcome to the lobby, " + playerName + "!");
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Game Lobby - " + playerName);
        primaryStage.setScene(scene);
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            clientLobby.sendMessage(message);
            messageField.clear();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        // Clean up when the application is closed
        if (clientLobby != null) {
            clientLobby.leave();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
