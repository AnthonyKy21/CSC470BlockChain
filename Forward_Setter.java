/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_Hybrid;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author joseph
 * @param <T>
 */
public class Forward_Setter<T extends Serializable>  implements Forwarder<LinkedList<BlockChain<T>.data>>, Serializable{
    
    BlockChain bc_ptr;
//    Mutex m = new Mutex();
    
    Forward_Setter(BlockChain<T> bc_ptr) {
        this.bc_ptr = bc_ptr; 
    }
    
    @Override
    public void forward(LinkedList<BlockChain<T>.data> list) throws InterruptedException {
        if (bc_ptr.validate(list))
            if (bc_ptr.size() > list.size()) {
                bc_ptr.list = list;
            }
    }
    
}
