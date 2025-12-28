package client;

import javax.swing.*;
import java.awt.*;

public class VotingScreen extends JPanel {
    private final JPanel buttonsPanel;
    private final ClientGUI parentGUI;

    public VotingScreen(ClientGUI parentGUI){
        this.parentGUI = parentGUI;
        setLayout(null);
        JLabel label = new JLabel("Голосуйте за лучший ответ");
        label.setBounds(20, 0, 600, 50);
        add(label);

        buttonsPanel = new JPanel();
        buttonsPanel.setBounds(0, 60, 300, 300);
    }
    public void addButtons(int playersCount, int selfNumber){
        buttonsPanel.setLayout(new GridLayout(2, playersCount / 2 + 1));
        System.out.println(playersCount + " " + selfNumber);
        for (int i = 0; i < playersCount; i++){
            if (i != selfNumber){
                JButton btn = new JButton("" + (i + 1));
                btn.addActionListener(l -> {
                    parentGUI.worker.sendMessage("/vote " + btn.getText());
                    buttonsPanel.setEnabled(false);
                });
                buttonsPanel.add(btn);
            }
        }
        add(buttonsPanel);
        setEnabled(true);
    }
}
