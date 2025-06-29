package org.example;

import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client extends Thread {
    private final int serverPort;
    private final String IPAddress;
    private Scanner sc = new Scanner(System.in);

    public Client(String IP, int serverPort) {
        this.IPAddress = IP;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            socket = new Socket(IPAddress,serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Connected to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            BufferedReader finalIn = in;
            Thread receiver = new Thread(() -> {
                String line;
                try {
                    while ((line = finalIn.readLine()) != null) {
                        System.out.println("<< " + line);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            receiver.start();

            Gson gson = new Gson();


            while (true) {
                System.out.print("enter a command: ");
                String Command = sc.nextLine();
                System.out.println("enter Extra:");
                String extra = sc.nextLine();


                if ("exit".equalsIgnoreCase(Command)) {
                    break;
                }

                Command CMD = new Command(Command,extra);
                String Json = gson.toJson(CMD);
                out.write(Json);
                out.newLine();
                out.flush();
            }

            socket.close();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 8080);
        client.start();
    }
}
