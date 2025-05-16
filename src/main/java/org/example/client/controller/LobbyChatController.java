package org.example.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.client.model.ClientLobby;
import org.example.client.model.ClientPlayer;
import org.example.client.config.ApplicationConfig;

import java.io.IOException;

public class LobbyChatController implements ClientLobby.MessageListener, ClientPlayer.ClientLobbyCallback {

    @FXML
    private ListView<String> chatListView;

    @FXML
    private TextField messageField;
    
    private ClientLobby clientLobby;
    private final ObservableList<String> chatMessages = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        chatListView.setItems(chatMessages);
        clientLobby = ClientLobby.getInstance();
        
        // Register as a message listener
        clientLobby.addMessageListener(this);
    }
    
    public void initializeWithPlayerName(String playerName) {
        // Connect to the WebSocket server using config
        clientLobby.connect(ApplicationConfig.SERVER_URL, playerName, this);
        
        // Join the lobby
        clientLobby.join();
        
        // Add a welcome message
        addMessage("Welcome to the lobby, " + playerName + "!");
    }

    @FXML
    void onSendMessage(ActionEvent event) {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            // Send the message to the server
            clientLobby.sendMessage(message);
            
            // Clear the message field
            messageField.clear();
        }
    }

    @FXML
    void onLeaveLobby(ActionEvent event) {
        // Leave the lobby
        clientLobby.leave();
        
        // Remove this controller as a message listener
        clientLobby.removeMessageListener(this);
        
        try {
            // Load the main menu view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent mainMenuView = loader.load();
            
            // Create a new scene and set it on the current stage
            Scene scene = new Scene(mainMenuView);
            Stage stage = (Stage) messageField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Game Lobby");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onMessageReceived(String message) {
        // Run on JavaFX thread to update UI
        Platform.runLater(() -> addMessage(message));
    }
    
    private void addMessage(String message) {
        chatMessages.add(message);
        
        // Scroll to the bottom of the list view
        chatListView.scrollTo(chatMessages.size() - 1);
    }
}
