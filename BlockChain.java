/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_Hybrid;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
/**
 *
 * @author joseph
 * @param <T>
 */
public class BlockChain<T extends Serializable> implements Serializable {
    
    final int SENDER_PORT = 9092;
    final int VERIFIER_PORT = 9091;
    

    protected final class data implements Serializable {
        
        int hash = 0;
        long time_stamp = new Date().getTime();
        T info; 
        
        data(T info_) {
            info = info_;
            hash = info.hashCode();
        }
        data(T info_, int prev_hash) {
            info = info_;
            hash = do_hash(prev_hash);
        }
        
        int do_hash(int prev) {
            return info.hashCode() + prev;
        }
        
        boolean equals(data var) {
            return hash == var.hash && info == var.info;
        }
        
        @Override
        public String toString() {
            return "info: " + info + "\n time: " + time_stamp + "\n";
        }
    }
    
    LinkedList<data> list = new LinkedList<>();
    
    Node<T> node;
    Node<LinkedList<data>> verifier; 
    
    BlockChain() {
        node = new Node<>(SENDER_PORT, new Forward_Adder<>(this)); 
        verifier = new Node<>(VERIFIER_PORT, new Forward_Setter<>(this));
    }
    
    public void init() {
        node.start();
        verifier.start();
    }
    
    
    
    public void addConnection(String ip) {
        node.addConnection(ip);
        verifier.addConnection(ip);
    }
    
    int size() {
        return list.size();
    }
    
    void add(T info) throws InterruptedException {        
        list.add(new data(info));        
        node.send(info);
        verifier.send(list);
    }
    void safe_add(T info) {
        list.add(new data(info)); 
    }
    
    boolean equal(BlockChain bc) {
        return list.equals(bc.list);
    }
    
    @Override
    public String toString() {
        String str = "\n";
        int i = 1;
        for (data x : list) {
            str += i + " " + x.toString();
            ++i;
        }
        return str;
    }
    
    
    public boolean validate(LinkedList<data> list) {
        
        int hash = 0;
        for (data e : list) {
            if (e.hash != e.do_hash(hash)) {
                return false;
            }
        }
        return true; 
    }

}
