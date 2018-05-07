/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.util.Date;

/**
 *
 * @author joseph
 */
public class BlockChain<T> {
    
    private class Node {
        Node next;
        Node prev;  
        long time_stamp = new Date().getTime();

        T data; 
        
        int hash = 1;
        Node(T data, Node prev) {
            this.data = data;
            
            if (prev != null) 
                this.hash = prev.hash + data.hashCode(); //our crappy hash function 
        }
    }
    
    Node head;
    Node tail; 
    Node last_valid;
    int sz;
    int valid_sz;
    
    
    void add(T data) {
        if (sz == 0) {
            head = new Node(data, null);
            tail = head;
        }
        else {
            tail.next = new Node(data, tail);
            tail = tail.next; 
        }
        ++sz;
    }
    private Node equal_impl(Node n1, Node n2) { 
        //iterates through every node and compares the two chains hashes, 
        //if the networks are same (success) returns null else retursn pointer to Node where the disjunction occurs 
        if (n1 == null || n2 == null) {
            return n1; 
        }
        
       if (n1.hash != n2.hash) {
           return n1; 
       }
       else 
           return equal_impl(n1.next, n2.next); 
    }
  
    
    public boolean equal(BlockChain<T> fellow_chain) { 
        //THESE WILL ALWAYS BE THE SAME "CORRECT NODE"
        Node initial_ref_self  =              last_valid != null ?              last_valid :              head; 
        Node initial_ref_param = fellow_chain.last_valid != null ? fellow_chain.last_valid : fellow_chain.head;   
        
        return equal_impl(initial_ref_self, initial_ref_param) == null;          
    }
    private boolean valid_impl(Node n) {
        if (n == null) 
            return true;
        if (n.hash == n.prev.hash + n.data.hashCode())
            return valid_impl(n.next);
        else
            return false;
    }
    public boolean valid() {
        return valid_impl(head);
    }
    public void print() {
        Node ref = head;
        while (ref != null) {
            System.out.println(ref.data);
            ref = ref.next;
        }
    }
    
    @Override
    public String toString() {
        String str = "";
        Node ref = head;
        while (ref != null) {
            str += "{ \n";
            str += "data" + ref.data + "\n";
            str += "hash" + ref.hash + "\n";
            str += "time" + ref.time_stamp + "\n";
                       
            str += "    }";
        }
        return str;
    }
}
