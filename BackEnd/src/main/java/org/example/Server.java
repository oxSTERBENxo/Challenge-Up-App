package org.example;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Server extends Thread {

    private Integer port;
    private String IP;
    private List<ClientData> clients;
    private List<PlayerStatData> PlayerStats;
    private Lock ClientLock = new ReentrantLock();
    private Lock PlayerLock = new ReentrantLock();

    public Server(Integer port, String IP) {
        this.port = port;
        this.IP = IP;
        clients = new ArrayList<>();
        PlayerStats = new ArrayList<>();
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port, 50, InetAddress.getByName(this.IP));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File ClientsFile = null;
        File GroupsFile = null;
        File PlayerStatsFile = null;


//      check if the clients file exists
        try {
            PlayerStatsFile = new File("src/main/java/org/example/Files/PlayerStats.json");
            if (PlayerStatsFile.createNewFile()) {
                System.out.println("File created");
                FileWriter PlayerStatsFileWriter = new FileWriter(PlayerStatsFile);
                PlayerStatsFileWriter.write("[]");
                PlayerStatsFileWriter.close();
            }
            else {
                System.out.println("File already exists");
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }

//      check if the PlayerStats file exists
        try {
            ClientsFile = new File("src/main/java/org/example/Files/Clients.json");
            if (ClientsFile.createNewFile()) {
                System.out.println("File created");
                FileWriter ClientsWriter = new FileWriter(ClientsFile);
                ClientsWriter.write("[]");
                ClientsWriter.close();
            }
            else{
                System.out.println("File already exists");
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }

        Gson gson = new Gson();
//        Load Clients
        if (ClientsFile.length() > 2){
            try(Reader UserReader = new FileReader("src/main/java/org/example/Files/Clients.json")) {
                Type clientListType = new TypeToken<List<ClientData>>(){}.getType();
                List<ClientData> CDList = gson.fromJson(UserReader, clientListType);
                if (CDList != null) clients.addAll(CDList);
            }catch (Exception e) {
                System.err.println(e);
            }
        }else{
            System.out.println("No Clients found");
        }
//        Load PlayerStat
        if (PlayerStatsFile.length() > 2){
            try(Reader PlayerStatsReader = new FileReader("src/main/java/org/example/Files/PlayerStats.json")) {
                Type PlayerStatsType = new TypeToken<List<PlayerStatData>>(){}.getType();
                List<PlayerStatData> CDList = gson.fromJson(PlayerStatsReader, PlayerStatsType);
                if (CDList != null) PlayerStats.addAll(CDList);
            }catch (Exception e) {
                System.err.println(e);
            }
        }else{
            System.out.println("No PlayerStats found");
        }


        String Task = "Placeholder text to fill out current";

        System.out.println("Server started");
        System.out.println(clients.size() + " clients");
        System.out.println(PlayerStats.size() + " PlayerStats");

        DailyMidnightTask taskRunner = new DailyMidnightTask();
        taskRunner.startDailyMidnightTask(() -> {
            PlayerLock.lock();
            System.out.println("Midnight reset running...");
            for (PlayerStatData stat : PlayerStats) {
                stat.UpdateValue();
            }
            PlayerLock.unlock();
        });

        while (true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Worker W = new Worker(clientSocket,this.clients,this.PlayerStats,Task,this.ClientLock,this.PlayerLock);
            W.start();
            System.out.println("Client connected");

        }
    }

    public static void main(String[] args) {
        String IP = "127.0.0.1";
        int port = 8080;
        Server server = new Server(port, IP);
        server.start();
    }
}
