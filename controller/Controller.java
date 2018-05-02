package controller;

import P2P_Hybrid.Message;
import P2P_Hybrid.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import views.Frame;

/**
 *
 * @author Ky
 */
public class Controller {

    private final Frame frame = new Frame();

    public Controller() {
        Node<Message> node = new Node(9091);

        JTextField username = frame.getUsernameField();
        JTextField host = frame.getHostField();
        JTextField port = frame.getPortField();
        JTextArea outputArea = frame.getOutputArea();
        JTextArea inputArea = frame.getInputArea();
        JButton connectButton = frame.getConnectButton();
        JButton disconnectButton = frame.getDisconnectButton();
        JButton sendButton = frame.getSendButton();

        frame.setTitle(getClass().getSimpleName());
        frame.setLocationRelativeTo(null);
        frame.getHostField().setText("192.168.1.4");

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username.setEditable(false);
                host.setEditable(false);
                port.setEditable(false);
                node.addConnection(host.getText().trim());
                node.start();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputArea.getText();
                Message msg = new Message(input);
                node.send(msg);
                outputArea.setText(username.getText() + ": " + msg);
                inputArea.setText("");
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username.setEditable(true);
                host.setEditable(true);
                port.setEditable(true);
            }
        });
    }

    public static void main(String[] args) {
        Controller app = new Controller();
        app.frame.setVisible(true);
    }
}
