package client;

import server.Server;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class StartScreen extends JPanel{
    private final JButton sendButton;
    private final JLabel askForCode;
    private final JTextField codeField;
    private final JLabel askForNickname;
    private final JTextField nicknameField;
    private final ClientGUI parentGUI;

    public StartScreen(ClientGUI parentGUI){
        this.parentGUI = parentGUI;
        sendButton = new JButton("Отправить");
        sendButton.setBounds(200, 500, 100, 50);

        askForCode = new JLabel("Введите код комнаты (4 знака)");
        askForCode.setBounds(100, 100, 250, 50);

        codeField = new JTextField(4);
        codeField.setBounds(100, 160, 150, 50);

        askForNickname = new JLabel("Введите ваш никнейм");
        askForNickname.setBounds(100, 260, 200, 50);

        nicknameField = new JTextField(16);
        nicknameField.setBounds(100, 320, 100, 50);

        addFieldsListener(nicknameField);
        addFieldsListener(codeField);

        sendButton.addActionListener(e -> sendButtonListener());

        add(askForCode);
        add(codeField);
        add(askForNickname);
        add(nicknameField);
        add(sendButton);

        setVisible(true);
    }
    private void addFieldsListener(JTextField field){
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isFieldsCorrect();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                isFieldsCorrect();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                isFieldsCorrect();
            }
        });
    }
    private boolean isFieldsCorrect(){
        String nickname = nicknameField.getText().trim();
        String code = codeField.getText().trim();
        boolean isCorrect = !nickname.isEmpty() && code.length() == 4;
        sendButton.setEnabled(isCorrect);
        return isCorrect;
    }
    private void sendButtonListener(){
        if (isFieldsCorrect()){
            ClientWorker worker = new ClientWorker(
                    Server.HOST,
                    Server.PORT,
                    codeField.getText().trim(),
                    nicknameField.getText().trim(),
                    parentGUI
            );
            parentGUI.worker = worker;
            worker.execute();
            sendButton.setEnabled(true);
        }
    }
}
