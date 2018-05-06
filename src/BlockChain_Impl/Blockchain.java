/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockChain_Impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Won
 */
public class Blockchain {
    
    public static void main(String[] args)
    {
        List<Block> blockChain = new ArrayList<>();
        
        Scanner kb = new Scanner(System.in);
        
        boolean buildingChain = true;
        
        //Data data = null;
        Block block = null;
        String data;
        
        String firstName, lastName, input;
        int age;
        int prevHash;
        
        while(buildingChain)
        {
            System.out.println("Enter information:"); //i.e. First Name, Last Name, and Age
            
            
           /* firstName = kb.nextLine();
            lastName = kb.nextLine();
            age = kb.nextInt();
            kb.nextLine(); 
            
            data = new Data(firstName, lastName, age);*/
            System.out.println("Enter message");
            data = kb.nextLine();
            prevHash = (blockChain.isEmpty()) ? 0 : blockChain.get(blockChain.size()-1).getCurrHash();
            
            System.out.println(prevHash);
                    
            block = new Block(prevHash,data);
                    
            blockChain.add(block);
            
            System.out.println("Would you like to continue?");
            input = kb.nextLine();
            
            if(!(input.equalsIgnoreCase("Yes")))
                buildingChain = false;
            
        }
        
        for(Block b: blockChain)
        {
            System.out.println("--------------------------------\nindex: "+ blockChain.indexOf(b) + "\n"+ b.toString());
        }

    }
    
}
