/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcp2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.awt.Mutex;

/**
 * Trivial client for the date server.
 */
public class P2PNode<T> extends Thread {

    int port;
    ArrayList<String> serverAddresses = new ArrayList<>();
    LinkedList<T> data_received = new LinkedList<>();

    Mutex locker = new Mutex();

    P2PNode(int port_) {
        port = port_;
    }

    void addConnection(String address) {
        serverAddresses.add(address);
    }
    
    // Instantiates your connection to the group chat
    void addSelf() throws UnknownHostException, IOException
    {
        
        String hostName;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter host name: ");
        hostName = input.readLine();
        
        try
        {
            InetAddress.getByName(hostName);
            addConnection(InetAddress.getLocalHost().getHostAddress());
            System.out.println("Connection successful.");
        }
        catch (UnknownHostException e)
        {
            System.out.println("Could not find " + hostName + "\nProcess aborted.") ;
        }

    }

    void shareServerList(P2PNode<?> node) {
        locker.lock();
        node.serverAddresses = serverAddresses;
        locker.unlock();
    }

    void assignServerList(ArrayList<String> lst) {
        locker.lock();
        serverAddresses = lst;
        locker.unlock();
    }

    //returns deep copy of the all the data 

    LinkedList<T> getData() {
        locker.lock();  //ensure nothing is being written to 
        LinkedList<T> deepCopy = new LinkedList<T>(data_received);
        locker.unlock();
        return deepCopy;
    }

    //returns all the data and then clears it 

    LinkedList<T> getDataClear() {
        locker.lock();  //ensure nothing is being written to 
        LinkedList<T> deepCopy = new LinkedList<T>(data_received);
        data_received.clear();
        locker.unlock();
        return deepCopy;
    }

    void clearData() {
        locker.lock();  //ensure nothing is being written to
        data_received.clear();
        locker.unlock();
    }

    ///send to everyone
    public boolean send(T data) {
        try {
            for (int i = 0; i < serverAddresses.size(); ++i) {
                send(i, data);
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;

    }

    ///send to specific connection
    public boolean send(int i, T data) {
        try {
            String serverAddress = serverAddresses.get(i);
            Socket s = new Socket(serverAddress, port);
            ObjectOutputStream obj_writer = new ObjectOutputStream(s.getOutputStream());
            obj_writer.writeObject(data);
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    ///this is the listener method ------------ call run ----------------------------------
    @Override
    public void run() {

        Socket socket = null;
        try(ServerSocket listener = new ServerSocket(port)){
            
            while (true) {
                try {

                    //accept a connection 
                    socket = listener.accept();
                    ObjectInputStream obj_reader = new ObjectInputStream(socket.getInputStream()); //attempt to read / get data from it 

                    locker.lock();
                    data_received.add((T) obj_reader.readObject());
                    locker.unlock();

                    System.out.println(data_received.get(data_received.size() - 1));
                    
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Oops: " + e.getMessage());
                } 
                finally {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(P2PNode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(P2PNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
