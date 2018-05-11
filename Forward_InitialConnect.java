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
 * @param <T>
 * 
 * -- A lamda class
 * 
 * Used by pseudo-hosts only. Listens for a String (sent by the initialConnect method) and automatically
 * adds the IP to the adresslist of the pseudo-host who will than ping the rest of the users on the network to
 * add the connection. 
 *
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
