package csc470blockchain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import views.UserInterface;
import networking.P2PNode;

/**
 *
 * @author Anthony Ky <AK838275@wcupa.edu>
 */
public class CSC470BlockChain {

  private final UserInterface frame = new UserInterface();

  public CSC470BlockChain() {
    frame.setTitle(getClass().getSimpleName());
    frame.setLocationRelativeTo(null);

    JTextArea outputArea = frame.getOutputArea();
    JTextArea inputArea = frame.getInputArea();

    JTextField usernameField = frame.getUsernameField();
    JTextField hostField = frame.getHostField();
    JTextField portField = frame.getPortField();

    JButton connectButton = frame.getConnectButton();
    JButton disconnectButton = frame.getDisconnectButton();
    JButton sendButton = frame.getSendButton();

    P2PNode<String> self = new P2PNode<>(9091);

    connectButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        String host = frame.getHostField().getText();
        self.addConnection(host);
      }
    });

    disconnectButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    });

    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          self.send(usernameField + ": " + inputArea);
          outputArea.setText(usernameField + ": " + inputArea);
          inputArea.setText("");
        }
        catch (IOException ex) {
          Logger.getLogger(CSC470BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex) {
          Logger.getLogger(CSC470BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
  }

  public static void main(String[] args) {
    CSC470BlockChain app = new CSC470BlockChain();
    app.frame.setVisible(true);
  }
}
