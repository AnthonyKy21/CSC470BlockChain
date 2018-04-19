/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcp2p;

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
    P2PNode<data> node  = new P2PNode<data>();
   
    
    P2PNode<data> selfServer() {
        return node;
    }
    
    @Override public void run() {
        while (true) {
            LinkedList<data> datalist = node.getDataClear();
            if (!datalist.isEmpty()) {
                for (data RCVD_DATA : datalist)
                    chain.add(RCVD_DATA.data);
                
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
