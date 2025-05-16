package org.example.server.model;

/**
 * Message model for lobby communications
 */
public class LobbyMessage {
    private String content;
    private String sender;
    private MessageType type;
    private String iconId;
    
    // Default constructor
    public LobbyMessage() {
    }
    
    // All-args constructor (to replace Lombok annotation)
    public LobbyMessage(String content, String sender, MessageType type) {
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.iconId = "default"; // Default icon
    }
    
    // Constructor with icon
    public LobbyMessage(String content, String sender, MessageType type, String iconId) {
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.iconId = iconId;
    }
    
    // Getters and setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public String getIconId() {
        return iconId;
    }
    
    public void setIconId(String iconId) {
        this.iconId = iconId;
    }
    
    public enum MessageType {
        JOIN, LEAVE, CHAT, PLAYER_LIST
    }
}
