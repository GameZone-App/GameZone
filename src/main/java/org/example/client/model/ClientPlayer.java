package org.example.client.model;

import org.example.LobbyObserver;

/**
 * Client-side player model that implements the LobbyObserver interface
 * to maintain compatibility with the Observer pattern.
 */
public class ClientPlayer implements LobbyObserver {
    private final String name;
    private final ClientLobbyCallback callback;
    
    public ClientPlayer(String name, ClientLobbyCallback callback) {
        this.name = name;
        this.callback = callback;
    }
    
    // Explicit getter to avoid Lombok issues
    public String getName() {
        return name;
    }
    
    public ClientLobbyCallback getCallback() {
        return callback;
    }
    
    @Override
    public void update(String message) {
        // Forward the message to the UI callback
        if (callback != null) {
            callback.onMessageReceived(message);
        }
    }
    
    /**
     * Interface for UI callbacks to handle lobby messages
     */
    public interface ClientLobbyCallback {
        void onMessageReceived(String message);
    }
}
