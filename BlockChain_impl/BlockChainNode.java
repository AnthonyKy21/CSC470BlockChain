/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import blockchain.P2PNode;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joseph
 */
public class BlockChainNode<T extends Object> extends Thread {
    
    
    BlockChain<T> chain = new BlockChain<T>();                      //Data list sender
    P2PNode<Integer[]> p2p_chain  = new P2PNode<Integer[]>(9091);        //Hash list sender 
    P2PNode<T> node  = new P2PNode<T>(9091);        //Hash list sender 
   

    @Override public void run() {
        node.start();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BlockChainNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            LinkedList<T> datalist = node.getDataClear();
            System.out.println("accepted data");
            if (!datalist.isEmpty()) {
                 System.out.println("is not empty");

                for (T RCVD_DATA : datalist) {
                    System.out.println(RCVD_DATA);
                    chain.add(RCVD_DATA);
                }
            }
         System.out.println("data is empty");

        }
    }
    
    void send(T data) {
        node.send(data);
    }
} 
