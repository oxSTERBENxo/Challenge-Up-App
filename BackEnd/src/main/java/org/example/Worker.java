package org.example;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class Worker extends Thread {
    private Socket socket;

    private List<ClientData> Clients;
    private List<PlayerStatData> PlayerStats;
    private String Task;
    private Lock CLientLock;
    private Lock PlayerLock;

    public Worker(Socket socket, List<ClientData> Clients, List<PlayerStatData> PlayerStats, String Task, Lock ClientLock, Lock PlayerLock) {
        this.socket = socket;

        this.Clients = Clients;
        this.PlayerStats = PlayerStats;
        this.Task = Task;

        this.PlayerLock = PlayerLock;
        this.CLientLock = ClientLock;
    }

    private boolean IsUsernameUsed(String name){
        for(ClientData cd : Clients){
            if(cd.getUsername().equals(name)){
                return true;
            }
        }
        return false;
    }

    private boolean IsPasswordStrong(String password, String name){
        if(password.length() < 6 || !password.contains(".*[!@#$%^&*].*") || password.equals(name)){
            return false;
        }
        return true;
    }

    private boolean IsEmailRight(String Email){
        if(Email.length() < 6 || !Email.contains("@")){
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        BufferedWriter out = null;


        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

            String line;
            Gson gson = new Gson();

            while ((line = in.readLine()) != null) {
                Command cmd = gson.fromJson(line, Command.class);
                System.out.println(cmd.getCommand());

                switch (cmd.getCommand()) {


//                  AccountManagement
                    //Important: EXTRA(USERNAME;EMAIL;PASSWORD)
                    case "CreateAccount":{
                        CLientLock.lock();
                        String[] Extra = cmd.getExtra().split(";");

                        ClientData CD = new ClientData(Extra[0], Extra[1], Extra[2]);
                        PlayerStatData PSD = new PlayerStatData(Extra[0],0,0);
                        if(Clients.contains(CD)) {
                            out.write("Error: Client already exists");
                            out.newLine();
                            out.flush();
                        } else if (IsUsernameUsed(Extra[0])) {
                            out.write("Error: Username is already taken");
                            out.newLine();
                            out.flush();
                        } else if (IsPasswordStrong(Extra[2], Extra[0])) {
                            out.write("Error: Invalid password");
                            out.newLine();
                            out.flush();
                        } else if (IsEmailRight(Extra[1])) {
                            out.write("Error: Invalid email");
                            out.newLine();
                            out.flush();
                        } else {
                            Clients.add(CD);
                            PlayerStats.add(PSD);
                            out.write("Client created account");
                            out.newLine();
                            out.flush();
                            DataManagment.saveList(PlayerStats, "\"src/main/java/org/example/Files/PlayerStats.json");
                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        }
                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;EMAIL;PASSWORD)
                    case "RemoveAccount":{
                        CLientLock.lock();
                        String[] Extra = cmd.getExtra().split(";");

                        ClientData CD = new ClientData(Extra[0], Extra[1], Extra[2]);
                        PlayerStatData PSD = new PlayerStatData(Extra[0],0,0);
                        if(Clients.contains(CD)) {
                            Clients.remove(CD);
                            PlayerStats.remove(PSD);
                            out.write("Successfully removed account");
                            out.newLine();
                            out.flush();
                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                            DataManagment.saveList(PlayerStats, "\"src/main/java/org/example/Files/PlayerStats.json");
                        }else{
                            out.write("Error: Client does not exist");
                            out.newLine();
                            out.flush();
                        }
                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;EMAIL;PASSWORD)
                    case "Login":{
                        CLientLock.lock();
                        String[] Extra = cmd.getExtra().split(";");

                        ClientData CD = new ClientData(Extra[0], Extra[1], Extra[2]);
                        if(Clients.contains(CD)) {
                            out.write("successfully logged in");
                            out.newLine();
                            out.flush();
                        }else {
                            out.write("Error: Incorrect username or password");
                            out.newLine();
                            out.flush();
                        }
                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;EMAIL;PASSWORD;NEW USERNAME)
                    case "EditAccountName":{
                        CLientLock.lock();
                        String[] Extra = cmd.getExtra().split(";");
                        ClientData CD_old = new ClientData(Extra[0], Extra[1], Extra[2]);
                        ClientData CD_new = new ClientData(Extra[3], Extra[1], Extra[2]);

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                PlayerStats.remove(psd);
                                break;
                            }
                        }

                        if (PSD != null) {
                            PlayerStatData PSD_new = new PlayerStatData(Extra[3],PSD.getPoints(),PSD.getDays());
                            if(Clients.contains(CD_old)) {
                                Clients.remove(CD_old);
                                Clients.add(CD_new);
                                PlayerStats.add(PSD_new);
                                out.write("Successfully edited account");
                                out.newLine();
                                out.flush();
                                DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                                DataManagment.saveList(PlayerStats, "\"src/main/java/org/example/Files/PlayerStats.json");
                            }else {
                                out.write("Error: Account does not exist");
                            }
                        }else{
                            out.write("Error: Incorrect username");
                            out.newLine();
                            out.flush();
                        }


                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;EMAIL;PASSWORD;NEW PASSWORD)
                    case "EditAccountPassword":{
                        CLientLock.lock();
                        String[] Extra = cmd.getExtra().split(";");

                        String NewPassword = cmd.getExtra();
                        ClientData CD_old = new ClientData(Extra[0], Extra[1], Extra[2]);
                        ClientData CD_new = new ClientData(Extra[0], Extra[1], Extra[3]);
                        if(Clients.contains(CD_old)) {
                            Clients.remove(CD_old);
                            Clients.add(CD_new);
                            out.write("Successfully edited account");
                            out.newLine();
                            out.flush();
                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        }else {
                            out.write("Error: Account does not exist");
                        }
                        DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;EMAIL;PASSWORD;NEW EMAIL)
                    case "EditAccountEmail":{
                        CLientLock.lock();
                        String[] Extra = cmd.getExtra().split(";");

                        String NewEmail = cmd.getExtra();
                        ClientData CD_old = new ClientData(Extra[0], Extra[1], Extra[2]);
                        ClientData CD_new = new ClientData(Extra[0], Extra[3], Extra[2]);
                        if(Clients.contains(CD_old)) {
                            Clients.remove(CD_old);
                            Clients.add(CD_new);
                            out.write("Successfully edited account");
                            out.newLine();
                            out.flush();
                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        }else {
                            out.write("Error: Account does not exist");
                        }
                        DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        CLientLock.unlock();
                        break;
                    }


//                  StatsManagement
                    //Important: NONE
                    case "GetTop10":{
                        PlayerLock.lock();
                        List<PlayerStatData> temp = PlayerStats;
                        temp.sort((a,b) -> Integer.compare(b.getPoints(), a.getPoints()));
                        List<PlayerStatData> top10 = new ArrayList<>(temp.subList(0, Math.min(PlayerStats.size(), 10)));
                        out.write(gson.toJson(top10));
                        out.newLine();
                        out.flush();
                        PlayerLock.unlock();
                        break;
                    }


//                  Points Managment
                    //Important: EXTRA(USERNAME)
                    case "CompletedTask":{
                        PlayerLock.lock();
                        String[] Extra = cmd.getExtra().split(";");

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                PlayerStats.remove(psd);
                                break;
                            }
                        }

                        if(PSD != null) {
                            PSD.setCompleted(true);
                            PlayerStats.add(PSD);
                            out.write("Task successfully marked completed");
                            out.newLine();
                            out.flush();
                        }else {
                            out.write("Error: Account does not exist");
                            out.newLine();
                            out.flush();
                        }
                        PlayerLock.unlock();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
