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
 * Listens for an arraylist of ip-addresses and adds all unknown ip address to the to connection list 
 */
//Accepts an arraylist of ip addresses and adds them to their list 
public class Forward_IP<T extends Serializable> implements Serializable, Forwarder<ArrayList<String>> {

    BlockChain<T> chain;
    
    Forward_IP( BlockChain<T> chain) {
        this.chain = chain;
    }
    
    @Override
    public void forward(ArrayList<String> IPs) throws InterruptedException {
        for (String ip : IPs) 
            if (!chain.addresslist.contains(ip))
                chain.safe_addConnection(ip);
    }    
}
