package client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SecondRoundScreen extends JPanel {
    public JButton sendButton2;
    public JLabel question;
    public JTextField answerField;
    private JPanel buttonsPanel;
    private final List<JButton> pressedButtons = new ArrayList<>();

    public SecondRoundScreen(ClientGUI parentGUI){
        setLayout(null);
        question = new JLabel();
        question.setBounds(20, 0, 760, 40);

        sendButton2 = new JButton("Отправить");
        sendButton2.setBounds(350, 500, 100, 60);

        answerField = new JTextField();
        answerField.setBounds(10, 50, 780, 40);
        answerField.setEnabled(false);
        answerField.setDisabledTextColor(Color.black);
        answerField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sendButton2.setEnabled(answerField.getText().split(" ").length >= 3);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendButton2.setEnabled(answerField.getText().split(" ").length >= 3);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendButton2.setEnabled(answerField.getText().split(" ").length >= 3);
            }
        });

        sendButton2.addActionListener(l -> {
            String answer = answerField.getText();
            if (answer.split(" ").length >= 3){
                parentGUI.worker.sendMessage("/answer2 " + answer);
            }
        });

        buttonsPanel = new JPanel();
        buttonsPanel.setBounds(0, 100, 800, 390);

        add(question);
        add(answerField);
        add(buttonsPanel);
        add(sendButton2);
    }
    public void addWords(String[] words){
        buttonsPanel.setLayout(new GridLayout(3, words.length / 3 + 1));
        for (String word : words){
            JButton btn = new JButton(word);
            btn.addActionListener(l -> {
                if (pressedButtons.contains(btn)){
                    pressedButtons.remove(btn);
                }
                else {
                    pressedButtons.add(btn);
                }
                List<String> text = new ArrayList<>();
                for (JButton b : pressedButtons){
                    text.add(b.getText());
                }
                answerField.setText(String.join(" ", text));

            });
            buttonsPanel.add(btn);
        }
    }
}
