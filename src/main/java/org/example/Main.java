package org.example;

public class Main {
    public static void main(String[] args) {
        Lobby lobby = Lobby.getInstance();

        Player alice = new Player("Alice");
        Player bob = new Player("Bob");

        lobby.join(alice);
        lobby.join(bob);

        lobby.sendMessage("Hey Bob!", alice);
        lobby.sendMessage("Hi Alice!", bob);

        lobby.leave(alice);
    }
}
