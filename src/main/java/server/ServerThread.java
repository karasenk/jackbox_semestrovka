package server;

import game.Player;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private final Server server;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientName;
    public final Player player;

    public ServerThread(Socket clientSocket, Server server) throws IOException {
        this.player = new Player();
        this.server = server;
        this.socket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        String message;
        try {
            while (true) {
                message = reader.readLine();
                if (message == null || message.equalsIgnoreCase("/exit")) {
                    break;
                }
                if (message.equals("/ready")){
                    server.publish(clientName + " в игре!");
                }
                if (message.startsWith("/nickname ")) {
                    clientName = message.substring(10).trim();
                    continue;
                }
                if (message.startsWith("/code ")){
                    String code = message.substring(6).trim();
                    if (code.equals(server.CODE)){
                        send("/accept");
                        continue;
                    }
                    break;
                }
                if (message.startsWith("/answer1 ")){
                    String answer = message.substring(9).trim();
                    player.addAnswer(0, answer);
                }
                if (message.startsWith("/answer2 ")){
                    String answer = message.substring(9).trim();
                    player.addAnswer(1, answer);
                }
                if (message.startsWith("/vote ")){
                    int n = Integer.parseInt(message.substring(6).trim()) - 1;
                    server.CLIENT_LIST.get(n).player.increaseScore();
                    player.vote();
                }
            }
        } catch (IOException e) {
            System.out.println("Клиент " + clientName + " отключился неожиданно");
        } finally {
            try {
                server.removeClient(this);
                socket.close();
                System.out.println("Клиент " + clientName + " отключен");
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }

    public boolean send(String message) {
        try {
            writer.write(message + "\n");
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public String getClientName() {
        return clientName;
    }
    public boolean isAnswered(int num){
        if (0 <= num && num <= 1){
            return player.answers[num] != null;
        }
        System.err.println("Вопроса " + num + "кажется и не должно быть..");
        return false;
    }
}