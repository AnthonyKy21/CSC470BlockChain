/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_Hybrid;

import java.io.Serializable;

/**
 *
 * @author Won
 */
public class Message implements Serializable{
    
    String input;
    
    public Message(String input)
    {
        this.input = input;
    }
    
    public String toString()
    {
        return input;
    }
    
}
