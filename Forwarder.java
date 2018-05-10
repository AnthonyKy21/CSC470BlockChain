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
 */
public interface Forwarder<T extends Serializable> {    
    void forward(T data)  throws InterruptedException;
}