/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcp2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Trivial client for the date server.
 */
public class P2PNode<T> extends Thread {

    int port;                                   //
    ArrayList<String> serverAddresses;                       //ip_address
    ArrayList<T> data_received = new ArrayList<>();
    
    P2PNode(int port_) {
        port = port_;
    }
    void addConecction(String address) {
        serverAddresses.add(address);
    }

    public void send(T data) throws IOException, ClassNotFoundException {
        for (int i = 0; i < serverAddresses.size(); ++i) {
            try {
            
            String serverAddress = serverAddresses.get(i);
        
        Socket s = new Socket(serverAddress, 9090);
        try {

            //BufferedReader input = new BufferedReader();
            ObjectOutputStream obj_writer = new ObjectOutputStream(s.getOutputStream());
            obj_writer.writeObject(data);

        } catch (Exception e) {
            System.out.println(e);
        }

        }
         catch (Exception e) {
                System.out.println("send failed ");
              }
        }
    }
    
    
    ///this is the listener method ------------ call run ----------------------------------
    @Override
    public void run() {
        ServerSocket listener;
        try {
            listener = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(P2PNode.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Socket socket = null;
        
        //do this forever 
        try {
            while (true) {
                try {
                    
                   //accept a connection 
                socket = listener.accept();
                ObjectInputStream obj_reader = new ObjectInputStream(socket.getInputStream()); //attempt to read / get data from it 
                data_received.add((T) obj_reader.readObject());
                } catch (Exception e) {
                } finally { socket.close(); }
            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (socket != null)
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(P2PNode.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
    }

}
