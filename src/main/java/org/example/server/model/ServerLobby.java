package org.example.server.model;

import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Server-side Lobby implementation that maintains the Singleton pattern.
 */
@Component
public class ServerLobby {
    private static volatile ServerLobby instance;
    private final Map<String, String> playerIcons; // Maps player names to their selected icons
    private final Set<String> players;
    private final SimpMessagingTemplate messagingTemplate;
    
    private ServerLobby(SimpMessagingTemplate messagingTemplate) {
        this.players = new HashSet<>();
        this.playerIcons = new HashMap<>();
        this.messagingTemplate = messagingTemplate;
    }
    
    public static ServerLobby getInstance(SimpMessagingTemplate messagingTemplate) {
        if (instance == null) {
            synchronized (ServerLobby.class) {
                if (instance == null) {
                    instance = new ServerLobby(messagingTemplate);
                }
            }
        }
        return instance;
    }
    
    public void join(String playerName, String iconId) {
        players.add(playerName);
        
        // Store the player's icon
        playerIcons.put(playerName, iconId != null ? iconId : "default");
        
        // Create join message to notify all clients
        LobbyMessage message = new LobbyMessage(
            playerName + " joined the lobby", 
            "Server", 
            LobbyMessage.MessageType.JOIN,
            playerIcons.get(playerName)
        );
        
        // Broadcast the message to all connected clients
        messagingTemplate.convertAndSend("/topic/lobby", message);
        
        // Send updated player list to all clients
        sendPlayerList();
    }
    
    // For backward compatibility
    public void join(String playerName) {
        join(playerName, "default");
    }
    
    public void leave(String playerName) {
        players.remove(playerName);
        
        // Get player's icon before removing
        String iconId = playerIcons.getOrDefault(playerName, "default");
        
        // Remove from icon map
        playerIcons.remove(playerName);
        
        // Create leave message to notify all clients
        LobbyMessage message = new LobbyMessage(
            playerName + " left the lobby", 
            "Server", 
            LobbyMessage.MessageType.LEAVE,
            iconId
        );
        
        // Broadcast the message to all connected clients
        messagingTemplate.convertAndSend("/topic/lobby", message);
        
        // Send updated player list to all clients
        sendPlayerList();
    }
    
    public void sendMessage(String content, String sender) {
        // Create chat message
        LobbyMessage message = new LobbyMessage(
            content, 
            sender, 
            LobbyMessage.MessageType.CHAT,
            playerIcons.getOrDefault(sender, "default")
        );
        
        // Broadcast the message to all connected clients
        messagingTemplate.convertAndSend("/topic/lobby", message);
    }
    
    public Set<String> getPlayers() {
        return new HashSet<>(players);
    }
    
    public Map<String, String> getPlayerIcons() {
        return new HashMap<>(playerIcons);
    }
    
    // Send the current player list to all clients
    private void sendPlayerList() {
        StringBuilder playerListContent = new StringBuilder();
        
        // Format: playerName:iconId,playerName2:iconId2,...
        for (String player : players) {
            if (playerListContent.length() > 0) {
                playerListContent.append(",");
            }
            playerListContent.append(player).append(":").append(playerIcons.getOrDefault(player, "default"));
        }
        
        LobbyMessage message = new LobbyMessage(
            playerListContent.toString(),
            "Server",
            LobbyMessage.MessageType.PLAYER_LIST
        );
        messagingTemplate.convertAndSend("/topic/lobby", message);
    }
}
