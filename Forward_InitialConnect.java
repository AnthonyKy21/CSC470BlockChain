/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_Hybrid;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author joseph
 * 
 * Listens for a node to send a string with their own ip address.
 *      adds the ip address to the connection list 
 * @param <T>
 */
    public class Forward_InitialConnect<T extends Serializable> implements Serializable, Forwarder<String> {

        BlockChain<T> chain;
        
    Forward_InitialConnect(BlockChain<T> chain) {
        this.chain =  chain;
    }
    
    @Override
    public void forward(String IP) throws InterruptedException {
       //check if string is an ip address
        chain.addConnection(IP); 
    }
}