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
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    ///this is the listener method ------------ call run ----------------------------------
    @Override
    public void run() {

        ServerSocket listener = null;
        Socket socket = null;

        try {
            listener = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(P2PNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            try {

                //accept a connection 
                socket = listener.accept();
                ObjectInputStream obj_reader = new ObjectInputStream(socket.getInputStream()); //attempt to read / get data from it 

                locker.lock();
                data_received.add((T) obj_reader.readObject());
                locker.unlock();

                System.out.println(data_received.get(data_received.size() - 1));
            } catch (Exception e) {
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(P2PNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

//
//    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
//        P2PNode<String> self = new P2PNode<>(9091);
//        String joe = "104.201.202.232";
//        String won = "104.201.247.121";
//        String ky = "104.201.210.88";
//        self.addConnection(won);
//        self.addConnection(ky);
//        self.addConnection(joe);
//
//        self.start();
//
//        Scanner scan = new Scanner(System.in);
//        while (true) {
//            System.out.print(">>>Joseph: ");
//
//            String statement = "Joseph: " + scan.nextLine();
//            System.out.print(">>>Joseph: ");
//            self.send(1, statement);
//            self.send(0, statement);
//        }
//
//    }
