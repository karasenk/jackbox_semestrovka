package server;

import game.Questions;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server{
    public static final String HOST = "localhost";
    public static final int PORT = 8098;
    public final int MAX_CLIENTS_COUNT = 10;
    public final int MIN_CLIENTS_COUNT = 3;
    public final String CODE = "MEOW"; // надо потом сделать чтобы он генерировался
    public final List<ServerThread> CLIENT_LIST = new ArrayList<>();
    private JTextArea textArea;
    private boolean isGameStarted;
    private ServerSocket serverSocket;

    public Server (int port, JTextArea textArea, JButton playButton){
        playButton.addActionListener(l -> {
            if (this.CLIENT_LIST.size() >= this.MIN_CLIENTS_COUNT){
                this.gameStarted();
                playButton.setEnabled(false);
                playButton.setVisible(false);

            }
        });
        isGameStarted = false;
        this.textArea = textArea;
        try {
            serverSocket = new ServerSocket(port);
            publish("Код комнаты: " + CODE);
            waitingCycle();
            roundCycle(0);
            askQuestions(1);
            roundCycle(1);
            votingCycle();
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }

    }
    public void waitingCycle(){
        try{
            while (!isGameStarted) {
                Socket client = serverSocket.accept();
                serverSocket.setSoTimeout(5000);
                if (CLIENT_LIST.size() >= MAX_CLIENTS_COUNT){
                    gameStarted();
                    break;
                }
                ServerThread clientThread = new ServerThread(client, this);
                CLIENT_LIST.add(clientThread);
            }
        } catch (SocketTimeoutException e){
            waitingCycle();
        } catch (IOException e){
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }
    public void roundCycle(int num){
        if (num == 0){
            textArea.setText("За работу!\nРаунд первый: разогрев!\n");
        }
        else {
            textArea.setText("Раунд второй!\n");
        }
        boolean roundFinished = false;
        List<ServerThread> answeredClients = new ArrayList<>();
        while (!roundFinished){
            roundFinished = true;
            for (ServerThread client : CLIENT_LIST){
                if (!client.isAnswered(num)){
                    roundFinished = false;
                }
                else {
                    if(!answeredClients.contains(client)){
                        answeredClients.add(client);
                        publish(client.getClientName() + " уже отправил(а) ответ");
                    }
                }
            }
        }
    }
    private void votingCycle(){
        List<String> text = new ArrayList<>();
        text.add("Вот все ответы от вас и ваших коллег\nПроголосуйте за лучший ответ!\n");
        for (int i = 0; i < CLIENT_LIST.size(); i++){
            ServerThread client = CLIENT_LIST.get(i);
            text.add((i + 1) + " - " + client.player.getQuestion2());
            client.send("/start_voting " + CLIENT_LIST.size() + " " + i);
            text.add(client.getClientName() + ": " + client.player.answers[1] + "\n");
        }
        textArea.setText(String.join("\n", text));

        boolean allVoted = false;
        while (!allVoted){
            allVoted = true;
            for (ServerThread client : CLIENT_LIST){
                if (!client.player.isVoted()){
                    allVoted = false;
                    break;
                }
            }
        }
        int maxScore = 0;
        for (ServerThread client : CLIENT_LIST){
            maxScore = Math.max(maxScore, client.player.getScore());
        }
        List<String> winners = new ArrayList<>();
        for (ServerThread client : CLIENT_LIST){
            if (client.player.getScore() == maxScore){
                winners.add(client.getClientName());
            }
        }
        textArea.setText("В этой игре побеждают: " + String.join(" ", winners));
        close();
    }
    public void gameStarted(){
        isGameStarted = true;
        askQuestions(0);
    }

    public void publish(String message) {
        SwingWorker<Void, Void> sender = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                textArea.append(message + "\n");
                return null;
            }
        };
        sender.execute();
    }

    public synchronized void askQuestions(int num) {
        Random random = new Random();
        for (int i = 0; i < CLIENT_LIST.size(); i++) {
            ServerThread client = CLIENT_LIST.get(i);

            int n = random.nextInt(Questions.questions.size());
            String randomQuestion = Questions.questions.get(n);
            System.out.println(randomQuestion);
            String question = "/question " + randomQuestion;
            if (num == 0){
                question = "/first_question " + randomQuestion;
            }
            if (num == 1){
                question = "/second_question " + randomQuestion;
                client.player.setQuestion2(randomQuestion);
                int x = random.nextInt(CLIENT_LIST.size());
                while (x == i){
                    x = random.nextInt(CLIENT_LIST.size());
                }
                int y = random.nextInt(CLIENT_LIST.size());
                while (x == y || y == i){
                    y = random.nextInt(CLIENT_LIST.size());
                }
                String randomAnswer1 = CLIENT_LIST.get(x).player.getShuffledAnswer();
                String randomAnswer2 = CLIENT_LIST.get(y).player.getShuffledAnswer();
                client.send("/second_words " + randomAnswer1 + " " + randomAnswer2);
            }
            if (!client.send(question)) {
                CLIENT_LIST.remove(i);
                i--;
            }
        }
    }

    public synchronized void removeClient(ServerThread client) {
        CLIENT_LIST.remove(client);
    }

    public void close(){
        for (ServerThread client : CLIENT_LIST){
            removeClient(client);
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }
}
