package client;


import javax.swing.*;


public class ClientGUI extends JFrame {
    public StartScreen startScreen;
    public FirstRoundScreen firstRoundScreen;
    public SecondRoundScreen secondRoundScreen;
    public VotingScreen votingScreen;
    public ClientWorker worker;

    public ClientGUI() {
        setSize(800, 600);

        startScreen = new StartScreen(this);
        firstRoundScreen = new FirstRoundScreen(this);
        secondRoundScreen = new SecondRoundScreen(this);
        votingScreen = new VotingScreen(this);

        add(startScreen);
        setVisible(true);

    }
    @Override
    public void dispose() {
        if (worker != null){
            this.worker.done();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}