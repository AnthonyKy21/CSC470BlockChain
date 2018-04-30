/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockChain_Impl;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author Won
 * @param <T>
 */
public class Block<T> {
    
    private int prevHash;
    private T data;
    private int currHash;
    private final Date date = new Date();
    
    public Block(int prevHash, T data)
    {
        this.date.getTime();
        this.prevHash = prevHash;
        this.data = data;
        
         Object [] contain = {data, prevHash};
         
         this.currHash = Arrays.hashCode(contain);
    }

    public int getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(int prevHash) {
        this.prevHash = prevHash;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCurrHash() {
        return currHash;
    }

    public void setCurrHash(int currHash) {
        this.currHash = currHash;
    }
    
    public String toString()
    {
        String blockDetails = "timestamp: " + date + "\n" +
                              "data: " + data.toString() + "\n" +
                              "hash: " + currHash + "\n" +
                              "previousHash: " + prevHash + "\n" +
                              "--------------------------------";
        return blockDetails;
    }

    
    
    
}
