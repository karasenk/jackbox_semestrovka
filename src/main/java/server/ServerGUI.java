package server;

import javax.swing.*;

public class ServerGUI extends JFrame {
    Server server;
    JButton playButton;
    JTextArea textArea;

    public ServerGUI(){
        setSize(800, 600);
        setLayout(null);

        textArea = new JTextArea();
        playButton = new JButton("Играть");

        textArea.setBounds(0, 100, 800, 500);
        playButton.setBounds(350, 10, 100, 50);

        add(textArea);
        add(playButton);
        setVisible(true);
        server = new Server(Server.PORT, textArea, playButton);
    }
    @Override
    public void dispose(){
        server.close();
        super.dispose();
    }

    public static void main(String[] args){
        new ServerGUI();
    }
}
