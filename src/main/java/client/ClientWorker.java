package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientWorker extends SwingWorker<Void, String> {
    public Socket socket;
    public BufferedReader reader;
    public BufferedWriter writer;
    private ClientGUI parentGUI;

    public ClientWorker(String host, int port, String code, String nickname, ClientGUI parentGUI) {
        this.parentGUI = parentGUI;
        try {
            this.socket = new Socket(host, port);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            sendMessage("/nickname " + nickname);
            sendMessage("/code " + code);
        } catch (IOException e) {
            publish("Произошла ошибка подключения");
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        publish("Мы теперь подключены к серверу");

        String message;
        while ((message = reader.readLine()) != null) {
            if (message.trim().equals("/accept")){
                parentGUI.remove(parentGUI.startScreen);
                sendMessage("/ready");
            }
            if (message.trim().startsWith("/first_question ")){
                parentGUI.add(parentGUI.firstRoundScreen);
                String question = message.trim().substring(16);
                parentGUI.firstRoundScreen.question.setText(question);
            }
            if (message.trim().startsWith("/second_question ")){
                String question = message.trim().substring(17);
                parentGUI.secondRoundScreen.question.setText(question);
            }
            if (message.trim().startsWith("/second_words ")){
                parentGUI.add(parentGUI.secondRoundScreen);
                parentGUI.remove(parentGUI.firstRoundScreen);
                String[] words = message.trim().substring(14).split(" ");
                parentGUI.secondRoundScreen.addWords(words);
            }
            if (message.trim().startsWith("/start_voting ")){
                parentGUI.remove(parentGUI.secondRoundScreen);
                parentGUI.add(parentGUI.votingScreen);
                parentGUI.votingScreen.setVisible(true);
                String[] row = message.trim().substring(14).split(" ");
                int playersCount = Integer.parseInt(row[0]);
                int selfNumber = Integer.parseInt(row[1]);
                System.out.println("started vote...");
                parentGUI.votingScreen.addButtons(playersCount, selfNumber);
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {

            System.out.println(message);
        }
    }

    @Override
    protected void done() {
        try {
            reader.close();
            writer.close();
            socket.close();
            super.done();
        } catch (IOException e) {
            publish("Ошибка отключения");
        }
    }

    public void sendMessage(String message) {
        SwingWorker<Void, Void> sender = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                writer.write(message + "\n");
                writer.flush();
                System.out.println(message + " sent");
                return null;
            }
        };
        sender.execute();
    }
}
