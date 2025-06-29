package org.example;

public class Main {
    public static void main(String[] args) {
        String IP = "127.0.0.1";
        int port = 8080;
        Server server = new Server(port, IP);
        server.start();
    }
}