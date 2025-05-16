package org.example.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private TextField playerNameField;

    @FXML
    private Button joinLobbyButton;

    @FXML
    void onJoinLobbyClicked(ActionEvent event) {
        String playerName = playerNameField.getText().trim();
        
        if (playerName.isEmpty()) {
            showAlert("Error", "Please enter a player name.");
            return;
        }
        
        try {
            // Load the lobby chat view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LobbyChat.fxml"));
            Parent lobbyView = loader.load();
            
            // Get the controller and initialize it with the player name
            LobbyChatController lobbyChatController = loader.getController();
            lobbyChatController.initializeWithPlayerName(playerName);
            
            // Create a new scene and set it on the current stage
            Scene scene = new Scene(lobbyView);
            Stage stage = (Stage) joinLobbyButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Game Lobby - " + playerName);
            
        } catch (IOException e) {
            showAlert("Error", "Failed to load lobby: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
