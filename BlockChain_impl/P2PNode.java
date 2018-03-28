/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
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
    private static final String POISON_PILL = "POISON_PILL";

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
    
    public byte[] serialize(T data) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(data); 
        
        return out.toByteArray();
    }

    ///send to specific connection
    public boolean send(int i, T data) {
        
        try {
            String serverAddress = serverAddresses.get(i);
            SocketChannel sender = SocketChannel.open(new InetSocketAddress(serverAddress, port));
            ByteBuffer buffer = ByteBuffer.allocate(256);
            buffer = ByteBuffer.wrap(serialize(data));
            String msg = null;
            
            sender.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
            return false;
        }
        return true;  
    }

    ///this is the listener method ------------ call run ----------------------------------
    @Override
    public void run() {
        
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(256);
            
            while(true){
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    
                    if(key.isAcceptable())
                        register(selector, serverSocket);
                    if(key.isReadable())
                        readMsg(buffer, key);
                    
                    iter.remove();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(P2PNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {

        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
    
    public  void readMsg(ByteBuffer buffer, SelectionKey key) throws IOException {
        String msg = null;
        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        System.out.println(new String(buffer.array()).trim());
        buffer.clear();
        if (new String(buffer.array()).trim().equals(POISON_PILL)) {
            client.close();
            System.out.println("Not accepting client messages anymore");
        }
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        P2PNode<String> self = new P2PNode<>(9091);
        String won = "";
        String vm = "";
      
        self.addConnection(won);
        self.addConnection(vm);
        
        
        self.start();
        Scanner scan = new Scanner(System.in);
        while(true) {
            
        String statement = scan.nextLine();    
        self.send(1, statement);  

            
            
        }
    }

       
}