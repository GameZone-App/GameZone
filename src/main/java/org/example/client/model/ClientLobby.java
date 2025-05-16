package org.example.client.model;

import org.example.server.model.LobbyMessage;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ClientLobby {
    private static ClientLobby instance;
    private List<MessageListener> messageListeners = new ArrayList<>();
    private List<PlayerListListener> playerListListeners = new ArrayList<>();
    private String playerName;
    private String playerIcon = "default";
    private StompSession stompSession;
    private Map<String, String> playerIcons = new HashMap<>(); // Maps player names to their icons

    private ClientLobby() {
        // Intentionally private for Singleton pattern
    }

    public static ClientLobby getInstance() {
        if (instance == null) {
            instance = new ClientLobby();
        }
        return instance;
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    public interface PlayerListListener {
        void onPlayerListUpdated(List<String> players, Map<String, String> playerIcons);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public void addPlayerListListener(PlayerListListener listener) {
        playerListListeners.add(listener);
    }

    public void removePlayerListListener(PlayerListListener listener) {
        playerListListeners.remove(listener);
    }

    public void connect(String serverUrl, String playerName, String iconId) {
        this.playerName = playerName;
        this.playerIcon = iconId != null ? iconId : "default";
        
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            stompSession = stompClient.connectAsync(serverUrl, new StompSessionHandlerAdapter() {}).get();
            stompSession.subscribe("/topic/lobby", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return LobbyMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    LobbyMessage message = (LobbyMessage) payload;
                    handleIncomingMessage(message);
                }
            });

            // Send join message
            LobbyMessage joinMessage = new LobbyMessage(
                    "joined the lobby",
                    playerName,
                    LobbyMessage.MessageType.JOIN,
                    playerIcon
            );
            stompSession.send("/app/join", joinMessage);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    // For backward compatibility
    public void connect(String serverUrl, String playerName) {
        connect(serverUrl, playerName, "default");
    }

    private void handleIncomingMessage(LobbyMessage message) {
        if (message.getType() == LobbyMessage.MessageType.PLAYER_LIST) {
            handlePlayerListUpdate(message.getContent());
            return;
        }

        // Format message based on type
        String formattedMessage;
        switch (message.getType()) {
            case JOIN:
                formattedMessage = "📥 " + message.getContent();
                break;
            case LEAVE:
                formattedMessage = "📤 " + message.getContent();
                break;
            case CHAT:
                formattedMessage = message.getSender() + ": " + message.getContent();
                break;
            default:
                formattedMessage = message.getContent();
        }

        // Notify all registered listeners
        for (MessageListener listener : messageListeners) {
            listener.onMessageReceived(formattedMessage);
        }
    }

    private void handlePlayerListUpdate(String playerListContent) {
        List<String> playerList = new ArrayList<>();
        Map<String, String> updatedIcons = new HashMap<>();
        
        if (!playerListContent.isEmpty()) {
            // Format: playerName:iconId,playerName2:iconId2,...
            String[] playerEntries = playerListContent.split(",");
            
            for (String entry : playerEntries) {
                String[] parts = entry.split(":");
                String name = parts[0];
                String icon = parts.length > 1 ? parts[1] : "default";
                
                playerList.add(name);
                updatedIcons.put(name, icon);
            }
        }
        
        // Update our cached player icons
        this.playerIcons = updatedIcons;
        
        // Notify all registered listeners
        for (PlayerListListener listener : playerListListeners) {
            listener.onPlayerListUpdated(playerList, updatedIcons);
        }
    }

    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            // Send leave message
            LobbyMessage leaveMessage = new LobbyMessage(
                    "left the lobby",
                    playerName,
                    LobbyMessage.MessageType.LEAVE,
                    playerIcon
            );
            stompSession.send("/app/leave", leaveMessage);
            
            stompSession.disconnect();
        }
    }

    public void sendMessage(String content) {
        if (stompSession != null && stompSession.isConnected()) {
            LobbyMessage chatMessage = new LobbyMessage(
                    content,
                    playerName,
                    LobbyMessage.MessageType.CHAT,
                    playerIcon
            );
            stompSession.send("/app/chat", chatMessage);
        }
    }

    public String getPlayerName() {
        return playerName;
    }
    
    public String getPlayerIcon() {
        return playerIcon;
    }
    
    public Map<String, String> getPlayerIcons() {
        return new HashMap<>(playerIcons);
    }
}
