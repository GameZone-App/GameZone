package org.example.client.config;

/**
 * Shared configuration for the client application
 */
public class ApplicationConfig {
    // WebSocket server URL
    public static final String SERVER_URL = "ws://localhost:8080/lobby-websocket";
    
    // Server connection timeout in milliseconds
    public static final int CONNECTION_TIMEOUT = 5000;
}
