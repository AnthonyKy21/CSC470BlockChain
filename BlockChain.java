/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_Hybrid;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
/**
 *
 * @author joseph jaspers, won murdoc, anthony ky
 * documentation: Joseph Jaspers       
 * 
 *  Block chain implementation P2P Network
 * 
 */
public class BlockChain<T extends Serializable> implements Serializable {
    
    final int SENDER_PORT   = 9092;
    final int VERIFIER_PORT = 9091;
    final int IP_PORT       = 9090;
    final int HOST_IP_SNDR  = 9089;
    final boolean HOST;

    //A data type containing individual elements of type T and relevant information
    protected final class data implements Serializable {
        
        int hash = 0;
        long time_stamp = new Date().getTime();
        T info; 
        
        data(T info_) {
            info = info_;
            hash = info.hashCode();
        }
        data(T info_, int prev_hash) {
            info = info_;
            hash = do_hash(prev_hash);
        }
        
        int do_hash(int prev) {
            return info.hashCode() + prev;
        }
        
        boolean equals(data var) {
            return hash == var.hash && info == var.info;
        }
        
        @Override
        public String toString() {
            return "info: " + info + "\n time: " + time_stamp + "\n";
        }
    }
    
    LinkedList<data> list = new LinkedList<>();//The actual blockchain
    
    Node<T> node;                              //Listener/Sender for the Generic T type
    Node<LinkedList<data>> verifier;           //Listever/Sender for sending/receiving the actual chain, handles discrepencies between 2 or more blockchains.
    Node<ArrayList<String>> ip_receiver;       //IP address listener used for listening for when a pseudo-host adds a new connection to the network
    Node<String> host_linker;                  //Initialzied by Pseudo-Host-Nodes only,listens for an IP-address string, and pings all other nodes, informing them to add the IP to list.
    ArrayList<String> addresslist;             //List of known IP-addresses on the network 
    
    BlockChain() {
        HOST = false;      
        node = new Node<>(SENDER_PORT, new Forward_Adder<>(this));
        verifier = new Node<>(VERIFIER_PORT, new Forward_Setter<>(this));
        ip_receiver = new Node<>(IP_PORT, new Forward_IP(this));
    }
    BlockChain(boolean isHost) {
       HOST = isHost;
        node = new Node<>(SENDER_PORT, new Forward_Adder<>(this));
       verifier = new Node<>(VERIFIER_PORT, new Forward_Setter<>(this));
       ip_receiver = new Node<>(IP_PORT, new Forward_IP(this));
       
       if (isHost)
           host_linker = new Node<>(HOST_IP_SNDR, new Forward_InitialConnect(this));
    }
    
    //initializes the nodes to start listening to the network
    public void init() {
        node.start();
        verifier.start();
        ip_receiver.start();
        
        if (HOST)
            host_linker.start();
    }   
    //Add an ip address to the nodes, pings any added connection to the rest of the network
    public void addConnection(String ip) {
        node.addConnection(ip);
        verifier.addConnection(ip);
        ip_receiver.addConnection(ip);
        addresslist.add(ip);
                
        if (HOST)
            host_linker.addConnection(ip);
        
        ip_receiver.send(addresslist);
    }
    
    //adds a connection without pinging the connection to everyone
    //this is utilized for when another node pings an IP to another node (as to avoid pinging the same IP indefinately)
    public void safe_addConnection(String ip) {
        node.addConnection(ip);
        verifier.addConnection(ip);
        ip_receiver.addConnection(ip);
        addresslist.add(ip);        

        if (HOST)
            host_linker.addConnection(ip);  
    }
    
    
    //Attempts to connect to a pseudo-host -- the host will add the ip to its config and ping back the iplist to the client 
    //Connect to a pseudo-host to have the host ping your IP to the rest of the network and add yourself to the network
    void initialConnect(String serverAddress, String ip_self) {
        try(Socket socket = new Socket(serverAddress, HOST_IP_SNDR))
        {
            ObjectOutputStream obj_writer = new ObjectOutputStream(socket.getOutputStream());
            obj_writer.writeObject(ip_self);          
        }
        catch(SocketTimeoutException e)
        {
            System.out.println("The socket timed out");
        }
        catch(IOException e)
        {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
    
    //get the size of the chain
    int size() {
        return list.size();
    }
    
    //add data to the blockchain and to everyone on the net (automatically sends to the network)
    void add(T info) throws InterruptedException {        
        list.add(new data(info));        
        node.send(info);
        verifier.send(list);
    }
    //add data to the blockchain without pinging (used for updating during collisions via Forward_Setter)
    //Similair to safe_addConnection()
    void safe_add(T info) {
        list.add(new data(info)); 
    }
    
    //compares 
    boolean equal(BlockChain bc) {
        return list.equals(bc.list);
    }
    
    @Override
    public String toString() {
        String str = "\n";
        int i = 1;
        for (data x : list) {
            str += i + " " + x.toString();
            ++i;
        }
        return str;
    }
    
    //checks if a blockchain has correct hashes / is a valid blockchain
    public boolean validate(LinkedList<data> list) {
        
        int hash = 0;
        for (data e : list) {
            if (e.hash != e.do_hash(hash)) {
                return false;
            }
        }
        return true; 
    }

}
