/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcp2p;
import java.util.Arrays;

/**
 *
 * @author joseph
 */
public class BlockChain<T> {
    
    private class Node {
        Node next;
        Node prev; 

        T data; 
        int hash;
        Node(T data, Node prev) {
            this.data = data;
            this.hash = hashTool.hashIt(data); 
        }
    }
    private class Hasher<T> {
    
    	private int prevHash = 0;
    	private T data;
    	private int currHash;
    	
    	public int hashIt(T data){

       		this.data = data;

       		Object [] contain = {data, prevHash};
       		
       		this.currHash = Arrays.hashCode(contain);

       		this.prevHash = currHash;

       		return currHash;
    	}
	}

    
    Node head;
    Node tail; 
    Node last_valid;

    Hasher hashTool = new Hasher();

    int sz;
    int valid_sz;
    
    
    void add(T data) {
        
        if (sz == 0)
            head = new Node(data, null);
        else {

            tail.next = new Node(data, tail);
            tail = tail.next; 
        }
        ++sz;
    }
    private Node compareValidImpl(Node n1, Node n2) { 
        //iterates through every node and compares the two chains hashes, 
        //if the networks are same (success) returns null else retursn pointer to Node where the disjunction occurs 
        if (n1 == null || n2 == null) {
            return n1; 
        }
        
       if (n1.hash != n2.hash) {
           return n1; 
       }
       else 
           return compareValidImpl(n1.next, n2.next); 
    }
    
    public boolean compareValid(BlockChain<T> fellow_chain) { 
        //THESE WILL ALWAYS BE THE SAME "CORRECT NODE"
        Node initial_ref_self  =              last_valid != null ?              last_valid :              head; 
        Node initial_ref_param = fellow_chain.last_valid != null ? fellow_chain.last_valid : fellow_chain.head;   
        
        return compareValidImpl(initial_ref_self, initial_ref_param) == null;          
    }
    private boolean isCleanImpl(Node n) {
        if (n == null) 
            return true;
        if (n.hash == n.prev.hash + n.hash)
            return isCleanImpl(n.next);
        else
            return false;
    }
    public boolean isClean() {
        return isCleanImpl(head);
    }

}