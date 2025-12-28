package client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class FirstRoundScreen extends JPanel {
    JLabel question;
    JTextField answer;
    JButton sendButton;
    ClientGUI parentGUI;

    public FirstRoundScreen(ClientGUI parentGUI){
        setLayout(null);
        this.parentGUI = parentGUI;
        question = new JLabel("Здесь будет вопрос");
        question.setBounds(20, 0, 760, 100);

        answer = new JTextField();
        answer.setBounds(0, 110, 800, 200);

        sendButton = new JButton("Отправить");
        sendButton .setBounds(350, 500, 100, 60);

        sendButton.setEnabled(false);

        answer.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sendButton.setEnabled(enoughWords());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendButton.setEnabled(enoughWords());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendButton.setEnabled(enoughWords());
            }
        });

        sendButton.addActionListener(l -> {
            if (enoughWords()){
                String formatedAnswer = String.join(" ", answer.getText().trim().split("\n"));
                parentGUI.worker.sendMessage("/answer1 " + formatedAnswer);
            }
        });

        add(question);
        add(answer);
        add(sendButton);
    }
    private boolean enoughWords(){
        return answer.getText().trim().split(" ").length >= 5;
    }
}
