package org.example.server.controller;

import org.example.server.model.LobbyMessage;
import org.example.server.model.ServerLobby;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for lobby operations
 */
@Controller
public class LobbyController {
    private final ServerLobby serverLobby;
    
    @Autowired
    public LobbyController(SimpMessagingTemplate messagingTemplate) {
        this.serverLobby = ServerLobby.getInstance(messagingTemplate);
    }
    
    @MessageMapping("/join")
    public void joinLobby(@Payload LobbyMessage message) {
        String playerName = message.getSender();
        String iconId = message.getIconId();
        serverLobby.join(playerName, iconId);
    }
    
    @MessageMapping("/leave")
    public void leaveLobby(@Payload LobbyMessage message) {
        String playerName = message.getSender();
        serverLobby.leave(playerName);
    }
    
    @MessageMapping("/chat")
    public void sendMessage(@Payload LobbyMessage message) {
        String content = message.getContent();
        String sender = message.getSender();
        serverLobby.sendMessage(content, sender);
    }
}
