/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcp2p;

import java.io.IOException;
import static java.lang.reflect.Array.set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import sun.awt.Mutex;

/**
 *
 * @author joseph
 */
public class BlockChainNode<T> {

    
    //Small k/v pair class 
    private class pair<K, V> {

        K key;
        V value;

        pair(K k, V v) {
            key = k;
            value = v;
        }
    }
   
    private class verifyListener<K> extends Thread {        
        /*
            verifyListener is a small thread that runs in the background
            it checks verify communicator for new data 
            and constantly updates       
        */
        boolean running = true; 
        boolean waiting = false; 
        Mutex locker = new Mutex();
        HashMap<K, Boolean> dataMap = new HashMap<>();
        P2PNode<pair<K, Boolean>> node; 
    
        verifyListener(P2PNode<pair<K, Boolean>> node_) {
            node = node_;
        }
        
        //returns if the state of the network is verified 
        boolean isVerified() {
            locker.lock();
           
            for (Boolean b : dataMap.values()) {
                if (!b) {
                    locker.unlock();
                    return false;
                } 
            }
            locker.unlock();
            return true;
        }
        
        @Override
        public void run() {
            while (running) {
                while(waiting) {} //"Sleep"                
                //While running continously add the newest data the map
                LinkedList<pair<K, Boolean>> dat_ = node.getDataClear();
                locker.lock();
                for (pair<K,Boolean> data : dat_) 
                    dataMap.put(data.key, data.value);
                locker.unlock(); 
            }
        }
    }

    String myAddress;               //"My Address"//
    BlockChain<T> chain;            //"My Block Chain
    ArrayList<String> serverList;   //Server list (shared among all P2P Nodes)

    //the data you send to the block_chain_verifier --> syntactic sugar  
    pair<String, BlockChain<T>> addressChain = new pair<>(myAddress, chain);
    
    //Key Strings == IPAdress || Value == data_type  
    P2PNode<pair<String, BlockChain<T>>> block_chain_verifier;
    P2PNode<pair<String, Boolean>> verify_communicator;
    
    //Constantly listens to verify_communicator and updates the current data 
    verifyListener<String> verifier = new verifyListener<>(verify_communicator); 
    
    //The actual data_transmitter that passes the data back and forth 
    P2PNode<T> data_transmitter;

    BlockChainNode(int port1, int port2, int port3) {
        block_chain_verifier = new P2PNode<>(port1);
        verify_communicator = new P2PNode<>(port2);
        data_transmitter = new P2PNode<>(port3);

        //Ensures they use the same list of servers 
        block_chain_verifier.assignServerList(serverList);
        block_chain_verifier.shareServerList(verify_communicator);
        block_chain_verifier.shareServerList(data_transmitter);
    }

    //initializes the threads
    void init() {
        block_chain_verifier.start();
        verify_communicator.start();
        data_transmitter.start();
    }

    //send a data 
    boolean send(T data) {
        chain.add(data);
        return data_transmitter.send(data);
    }
    
    boolean isVerified() {
        return verifier.isVerified(); 
    }
    
    LinkedList<T> getData() {
        return data_transmitter.getData();
    }
    void clearData() {
        data_transmitter.clearData(); 
    }
    
    //THIS IS NOT THE SAME AS CALLING getData() and the clearData() 
    //The mutex will unlock inbetween each call which means data could be added
    //in between callingboth, call getDataClear() for thread safety 
    LinkedList<T> getDataClear() {
       return data_transmitter.getDataClear();
    }

        
}
