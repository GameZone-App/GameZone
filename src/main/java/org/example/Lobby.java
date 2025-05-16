package org.example;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private static Lobby instance;
    private final List<LobbyObserver> players;

    private Lobby() {
        players = new ArrayList<>();
    }

    public static Lobby getInstance() {
        if (instance == null) {
            instance = new Lobby();
        }
        return instance;
    }

    public void join(Player player) {
        players.add(player);
        notifyAll(player.getName() + " joined the lobby.");
    }

    public void leave(Player player) {
        players.remove(player);
        notifyAll(player.getName() + " left the lobby.");
    }

    public void sendMessage(String message, Player sender) {
        notifyAll(sender.getName() + ": " + message);
    }

    private void notifyAll(String message) {
        for (LobbyObserver observer : players) {
            observer.update(message);
        }
    }
}
