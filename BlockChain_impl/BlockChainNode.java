/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import blockchain.P2PNode;
import java.util.LinkedList;

/**
 *
 * @author joseph
 */
public class BlockChainNode<T> extends Thread {
    
    private class data {
        
        data(BlockChain<T> ch, T dat) {
            chain = ch;
            data = dat; 
        }
        
        BlockChain<T> chain;
        T data; 
    }
    
    BlockChain<T> chain = new BlockChain<T>();
    P2PNode<data> node  = new P2PNode<data>(9091);
   
    
    P2PNode<data> selfServer() {
        return node;
    }
    
    @Override public void run() {
        node.run();
        while (true) {
            LinkedList<data> datalist = node.getDataClear();
            if (!datalist.isEmpty()) {
                for (data RCVD_DATA : datalist) {
                    System.out.println(RCVD_DATA.data);
                    chain.add(RCVD_DATA.data);
                }
                
                if (!chain.compareValid(datalist.getLast().chain)) {
                    System.out.println("discrpency detected");
                }
            }
        }
    }
    
    void send(T data) {
        node.send(new data(chain, data));
    }
} 
