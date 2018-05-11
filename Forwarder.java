/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_Hybrid;

import java.io.Serializable;

/**
 *
 * @author joseph
 * @param <T>
 * 
 * -- A lamda class
 * 
 * Trivial interface for Nodes. The forward method effecively works on controlling what a Node does 
 * after it successfully listens and pickups any data sent to it. Various implementations of this 
 * work to send the data to the apropriate structures/methods of the block chain.
 */
public interface Forwarder<T extends Serializable> {    
    void forward(T data)  throws InterruptedException;
}
