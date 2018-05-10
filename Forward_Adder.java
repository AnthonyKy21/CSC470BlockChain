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
public class Forward_Adder<T extends Serializable> implements Forwarder<T>, Serializable {
    
    BlockChain<T> bc_ptr;
//    Mutex m = new Mutex();
    
    Forward_Adder(BlockChain<T> bc_ptr) {
        this.bc_ptr = bc_ptr; 
    }
    
    @Override
    public void forward(T data) throws InterruptedException {
        bc_ptr.safe_add(data);
    }
    
}
