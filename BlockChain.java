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
 * @author joseph
 * @param <T>
 */
public class BlockChain<T extends Serializable> implements Serializable {
    
    final int SENDER_PORT   = 9092;
    final int VERIFIER_PORT = 9091;
    final int IP_PORT       = 9090;
    final int HOST_IP_SNDR  = 9089;
    final boolean HOST;

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
    
    LinkedList<data> list = new LinkedList<>();
    
    Node<T> node;
    Node<LinkedList<data>> verifier;
    Node<ArrayList<String>> ip_receiver;
    Node<String> host_linker; 
    ArrayList<String> addresslist;
    
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
    //initializes the nodes 
    public void init() {
        node.start();
        verifier.start();
        ip_receiver.start();
        
        if (HOST)
            host_linker.start();
    }   
    //Add an ip address to the nodes
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
    public void safe_addConnection(String ip) {
        node.addConnection(ip);
        verifier.addConnection(ip);
        ip_receiver.addConnection(ip);
        addresslist.add(ip);        

        if (HOST)
            host_linker.addConnection(ip);  
    }
    
    
    //Attempts to connect to a pseudo-host -- the host will add the ip to its config and ping back the iplist to the client 
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
    
    //add data to the blockchain and to everyone on the net 
    void add(T info) throws InterruptedException {        
        list.add(new data(info));        
        node.send(info);
        verifier.send(list);
    }
    //add data to the blockchain without pinging (used for updating during collisions via Forward_Setter)
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
    
    //checks if a block is correct
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
