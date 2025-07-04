package org.example;

//Error Codes Chart
// 1 - Error: Client already exists
// 2 - Error: Username already used
// 3 - Error: Password not strong
// 4 - Error: Invalid email
// 5 - Error: Account does not exist
// 6 - Error: Not enough gems to restore
// 7 - Error: Incorrect username
// 8 - Error: Unknown command

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.io.OutputStream;

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
        if(password.length() < 6 || password.equals(name)){
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
        OutputStream os = null;


        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            os = this.socket.getOutputStream();

            String line = in.readLine();

            System.out.println("Received line: " + line);

            Map<String, String> urlParams = parseUrl(line);
            String Com = urlParams.getOrDefault("Command", "default");
            String EX_Encoded = urlParams.getOrDefault("Extra", "");
            String EX = java.net.URLDecoder.decode(EX_Encoded, StandardCharsets.UTF_8.name());

            System.out.println(line);
            System.out.println("Command: " + Com);
            System.out.println("Extra: " + EX_Encoded);
            System.out.println("Decoded Extra: " + EX);

            switch (Com) {
                //AccountManagement
                //Important: EXTRA(USERNAME;PASSWORD)
                case "CreateAccount":{
                        CLientLock.lock();
                        String[] Extra = EX.split(";");

                        ClientData CD = new ClientData(Extra[0], Extra[1]);
                        PlayerStatData PSD = new PlayerStatData(Extra[0],0,0);
                        if(Clients.contains(CD)) {
                            sendStringResponse(os, "Error:1");
                            System.out.println("Error");
                        } else if (IsUsernameUsed(Extra[0])) {
                            sendStringResponse(os, "Error:2");
                            System.out.println("Error");
                        } else if (!IsPasswordStrong(Extra[1], Extra[0])) {
                            sendStringResponse(os, "Error:3");
                            System.out.println("Error");
//                        } else if (!IsEmailRight(Extra[1])) {
//                            out.write("Error: Invalid email");
//                            out.newLine();
//                            out.flush();
                        } else {
                            Clients.add(CD);
                            PlayerStats.add(PSD);
                            sendStringResponse(os, "Successfully created account");
                            DataManagment.saveList(PlayerStats, "src/main/java/org/example/Files/PlayerStats.json");
                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        }
                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;PASSWORD)
                case "RemoveAccount":{
                        CLientLock.lock();
                        String[] Extra = EX.split(";");

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                PlayerStats.remove(psd);
                                break;
                            }
                        }

                        ClientData CD = new ClientData(Extra[0], Extra[1]);
                        if(Clients.contains(CD)) {
                            Clients.remove(CD);
                            PlayerStats.remove(PSD);
                            sendStringResponse(os, "Successfully removed account");
                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                            DataManagment.saveList(PlayerStats, "\"src/main/java/org/example/Files/PlayerStats.json");
                        }else{
                            sendStringResponse(os, "Error:5");
                        }
                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;PASSWORD)
                case "Login":{
                    CLientLock.lock();
                    String[] Extra = EX.split(";");

                    ClientData CD = new ClientData(Extra[0], Extra[1]);
                    if(Clients.contains(CD)) {
                        sendStringResponse(os, "Successfully logged in");
                    }else {
                        sendStringResponse(os,"Error:5");
                        System.out.println("Error");
                    }
                    CLientLock.unlock();
                    break;
                }
                    //Important: EXTRA(USERNAME;PASSWORD;NEW USERNAME)
                case "EditAccountName":{
                        CLientLock.lock();
                        String[] Extra = EX.split(";");
                        ClientData CD_old = new ClientData(Extra[0], Extra[1]);
                        ClientData CD_new = new ClientData(Extra[2], Extra[1]);

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                PlayerStats.remove(psd);
                                break;
                            }
                        }

                        if (PSD != null) {
                            PlayerStatData PSD_new = new PlayerStatData(Extra[2],PSD.getPoints(),PSD.getDays());
                            if(Clients.contains(CD_old)) {
                                Clients.remove(CD_old);
                                Clients.add(CD_new);
                                PlayerStats.add(PSD_new);
                                sendStringResponse(os, "Successfully edited account");
                                DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                                DataManagment.saveList(PlayerStats, "\"src/main/java/org/example/Files/PlayerStats.json");
                            }else {
                                sendStringResponse(os, "Error:5");
                            }
                        }else{
                            out.write("Error:7");
                            out.newLine();
                            out.flush();
                        }


                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;PASSWORD;NEW PASSWORD)
                case "EditAccountPassword":{
                        CLientLock.lock();
                        String[] Extra = EX.split(";");

                        ClientData CD_old = new ClientData(Extra[0], Extra[1]);
                        ClientData CD_new = new ClientData(Extra[0], Extra[2]);
                        if(Clients.contains(CD_old)) {
                            Clients.remove(CD_old);
                            Clients.add(CD_new);
                            sendStringResponse(os, "Successfully edited account");
                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        }else {
                            sendStringResponse(os, "Error:5");
                        }
                        DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
                        CLientLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;EMAIL;PASSWORD;NEW EMAIL)
//                    case "EditAccountEmail":{
//                        CLientLock.lock();
//                        String[] Extra = cmd.getExtra().split(";");
//
//                        String NewEmail = cmd.getExtra();
//                        ClientData CD_old = new ClientData(Extra[0], Extra[1], Extra[2]);
//                        ClientData CD_new = new ClientData(Extra[0], Extra[3], Extra[2]);
//                        if(Clients.contains(CD_old)) {
//                            Clients.remove(CD_old);
//                            Clients.add(CD_new);
//                            out.write("Successfully edited account");
//                            out.newLine();
//                            out.flush();
//                            DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
//                        }else {
//                            out.write("Error: Account does not exist");
//                        }
//                        DataManagment.saveList(Clients, "src/main/java/org/example/Files/Clients.json");
//                        CLientLock.unlock();
//                        break;
//                    }

                    //Points Management


                    //Important: EXTRA(USERNAME)

                case "CompletedTask":{
                        PlayerLock.lock();
                        String[] Extra = EX.split(";");

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
                            sendStringResponse(os, "Successfully marked task completed");
                            DataManagment.saveList(PlayerStats, "src/main/java/org/example/Files/PlayerStats.json");
                        }else {
                            sendStringResponse(os, "Error:5");
                        }
                        PlayerLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME)
                case "NotCompletedTask":{
                            PlayerLock.lock();
                            String[] Extra = EX.split(";");

                            PlayerStatData PSD= null;

                            for(PlayerStatData psd : PlayerStats) {
                                if(psd.getUsername().equals(Extra[0])) {
                                    PSD = psd;
                                    PlayerStats.remove(psd);
                                    break;
                                }
                            }

                            if(PSD != null) {
                                PSD.setCompleted(false);
                                PlayerStats.add(PSD);
                                sendStringResponse(os, "Successfully marked task not completed");
                                DataManagment.saveList(PlayerStats, "src/main/java/org/example/Files/PlayerStats.json");
                            }else {
                                sendStringResponse(os, "Error:5");
                            }
                            PlayerLock.unlock();
                            break;
                        }
                    //Important: EXTRA(USERNAME)
                case "GetPlayerStats":{
                        PlayerLock.lock();
                        String[] Extra = EX.split(";");

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                break;
                            }
                        }

                        if(PSD != null) {
                            sendJsonResponse(PSD, os);
                        }else {
                            sendStringResponse(os, "Error:5");
                        }
                        PlayerLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME)
                case "PayToReset":{
                        PlayerLock.lock();
                        String[] Extra = EX.split(";");

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                PlayerStats.remove(psd);
                                break;
                            }
                        }

                        if(PSD != null) {
                            Integer Value = PSD.RestoreTemp();
                            PlayerStats.add(PSD);
                            if (Value > 0) {
                                sendStringResponse(os, "Successfully marked task restored");
                            }else {
                                sendStringResponse(os, "Error:6");
                            }
                        }else {
                            sendStringResponse(os, "Error:5");
                        }
                        PlayerLock.unlock();
                        break;
                    }
                    //Important: EXTRA(USERNAME;GEMSAMMOUNT)
                case "AddGems":{
                        PlayerLock.lock();
                        String[] Extra = EX.split(";");

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                PlayerStats.remove(psd);
                                break;
                            }
                        }

                        if(PSD != null) {
                            PSD.AddGems(Integer.parseInt(Extra[1]));
                            PlayerStats.add(PSD);
                            sendStringResponse(os, "Successfully added gems");
                        }else {
                            sendStringResponse(os, "Error:5");
                        }
                        PlayerLock.unlock();
                        break;
                    }
                    //Important: NONE
//                    case "GetTop10":{
//                        PlayerLock.lock();
//                        List<PlayerStatData> temp = PlayerStats;
//                        temp.sort((a,b) -> Integer.compare(b.getPoints(), a.getPoints()));
//                        List<PlayerStatData> top10 = new ArrayList<>(temp.subList(0, Math.min(PlayerStats.size(), 10)));
//                        out.write(gson.toJson(top10));
//                        out.newLine();
//                        out.flush();
//                        PlayerLock.unlock();
//                        break;
//                    }

                    //Task Management
                    //Important: EXTRA(USERNAME)
                case "GetTask":{
                        PlayerLock.lock();
                        String[] Extra = EX.split(";");

                        PlayerStatData PSD= null;

                        for(PlayerStatData psd : PlayerStats) {
                            if(psd.getUsername().equals(Extra[0])) {
                                PSD = psd;
                                break;
                            }
                        }

                        if(PSD != null) {
                            Task T = DataManagment.loadObject("src/main/java/org/example/Files/TaskFile.json", Task.class);
                            T.setPointsAmount(PSD.CalulatePointsToBeEarned(T));
                            System.out.println(T.getPointsAmount());
                            sendJsonResponse(T, os);
                        }else {
                            sendStringResponse(os, "Error:5");
                        }
                        PlayerLock.unlock();
                        break;
                    }

                    //Default command to handle unknown commands
                default:{
                    sendStringResponse(os, "Error:8");
                    break;
                }
            }

            System.out.println("Ended command: " + Com);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendStringResponse(OutputStream outputStream, String message) throws IOException {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);

        // Build HTTP response headers
        String httpResponse =
                "HTTP/1.1 200 OK\r\n" +
                        "Access-Control-Allow-Origin: http://localhost:5005\r\n" +
                        "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        // Send headers
        outputStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));

        // Send body (your string message)
        outputStream.write(body);

        outputStream.flush();
    }

    public void sendJsonResponse(Object obj, OutputStream outputStream) throws IOException {
        Gson gson = new Gson();

        // Convert object to JSON string
        String json = gson.toJson(obj);

        // Convert JSON string to bytes
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

        // If you are implementing your own HTTP server or protocol,
        // you must send HTTP headers before JSON body, e.g.:
        String httpResponse =
                "HTTP/1.1 200 OK\r\n" +
                        "Access-Control-Allow-Origin: http://localhost:5005\r\n" +
                        "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + jsonBytes.length + "\r\n" +
                        "\r\n";

        // Send headers
        outputStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));

        // Send JSON body
        System.out.println(json);
        outputStream.write(jsonBytes);

        outputStream.flush();
    }

    private Map<String, String> parseUrl(String url) {
        Map<String, String> map = new HashMap<>();
        if (!url.contains("?")) return map;

        String[] parts = url.split("\\s+");
        String query = parts[1].split("\\?")[1];
        String[] params = query.split("&");

        for (String param : params) {
            String[] keyValue = param.split("=");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }

}
