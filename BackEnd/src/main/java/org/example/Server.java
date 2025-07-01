package org.example;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Server extends Thread {

    private Integer port;
    private String IP;
    private List<ClientData> clients;
    private List<PlayerStatData> PlayerStats;
    private List<Task> Tasks;
    private Lock ClientLock = new ReentrantLock();
    private Lock PlayerLock = new ReentrantLock();
    private Task TT = new Task("No Task Assigned");

    public Server(Integer port, String IP) {
        this.port = port;
        this.IP = IP;
        clients = new ArrayList<>();
        PlayerStats = new ArrayList<>();
        Tasks = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println("Server started at:" + LocalDateTime.now());

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port, 50, InetAddress.getByName(this.IP));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File ClientsFile = null;
        File PlayerStatsFile = null;
        File TasksFile = new File("src/main/java/org/example/Files/Tasks.json");
        File TaskFile = null;
        File TimeFile = null;


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

//      Check if TimeFile Exists
        try {
            TimeFile = new File("src/main/java/org/example/Files/TimeFile.txt");
            if (TimeFile.createNewFile()) {
                System.out.println("File created");
                FileWriter TimeFIleWriter = new FileWriter(TimeFile);
                TimeFIleWriter.write(String.valueOf(LocalDate.now()));
                TimeFIleWriter.close();
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
//        Load Tasks
        if (TasksFile.length() > 2){
            try(Reader TasksReader = new FileReader("src/main/java/org/example/Files/Tasks.json")) {
                Type TasksType = new TypeToken<List<Task>>(){}.getType();
                List<Task> CDList = gson.fromJson(TasksReader, TasksType);
                if (CDList != null) Tasks.addAll(CDList);
            }catch (Exception e) {
                System.err.println(e);
            }
        }else{
            System.out.println("No Tasks found");
        }

        System.out.println(clients.size() + " clients");
        System.out.println(PlayerStats.size() + " PlayerStats");

//      Check if TaskFile Exists
        try {
            TaskFile = new File("src/main/java/org/example/Files/TaskFile.json");
            if (TaskFile.createNewFile()) {
                System.out.println("File created");
                Task T = new Task("Prepare to start your improvement journey!");
                T.setCompletedAt(LocalDate.now().toString());
                T.setPointsAmount(0);
                DataManagment.saveObject(T, "src/main/java/org/example/Files/TaskFile.json");
            }
            else{
                System.out.println("File already exists");
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }

        // Initialize TT with a default task
        if (Tasks.size() > 0) {
            TT = new Task();
            TT.setToDo(Tasks.get(0).getToDo());  // Set an initial value
        }

        File finalTimeFile = TimeFile;

        Thread TimeCheck = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep( 30* 1000); // Check every 30 minutes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    // Step 1: Read last written date
                    LocalDate lastDate;
                    try (BufferedReader br = new BufferedReader(new FileReader(finalTimeFile))) {
                        String line = br.readLine();

                        if (line == null || line.isBlank()) {
                            // File is empty — assume today as default
                            lastDate = LocalDate.now();
                            try (FileWriter writer = new FileWriter(finalTimeFile)) {
                                writer.write(lastDate.toString());
                            }
                        } else {
                            lastDate = LocalDate.parse(line, DateTimeFormatter.ISO_LOCAL_DATE);
                        }
                    }


                    // Step 2: Check current date
                    LocalDate currentDate = LocalDate.now();
//                    currentDate.isAfter(lastDate) replace in the if condition when ready to launch
                    if (true) {
                        // Step 3.1: Update time file
                        try (FileWriter writer = new FileWriter(finalTimeFile)) {
                            writer.write(currentDate.toString());
                        }

                        // Step 3.2: Pick random task and update
                        Random rand = new Random();
                        if (!Tasks.isEmpty()) {
                            int RandomIndex = rand.nextInt(Tasks.size());
                            Task randomTask = Tasks.get(RandomIndex);
                            System.out.println(randomTask.getToDo());
                            if (randomTask != null && randomTask.getToDo() != null) {
                                TT = randomTask;
                                DataManagment.saveObject(randomTask,"src/main/java/org/example/Files/TaskFile.json");
                            } else {
                                System.err.println("Random task or task content was null.");
                            }
                        } else {
                            System.err.println("No tasks available in the list.");
                        }

                        //Step 4: Update PlayerStats
                        for (PlayerStatData playerStat : PlayerStats) {
                            playerStat.UpdateValue(TT);
                        }
                        DataManagment.saveList(PlayerStats, "src/main/java/org/example/Files/PlayerStats.json");

                    } else {
                        System.out.println("Date unchanged. Still " + lastDate);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TimeCheck.start();


        while (true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Worker W = new Worker(clientSocket,this.clients,this.PlayerStats,TT.getToDo(),this.ClientLock,this.PlayerLock);
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
