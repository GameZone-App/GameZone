package org.example;
    public class Player implements LobbyObserver {
        private final String name;

        public Player(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void update(String message) {
            System.out.println(name + " received: " + message);
        }
    }
    


