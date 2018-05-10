package P2P_Hybrid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

/**
 *
 * @author Won
 * @param <T>
 */
public class Node<T extends Serializable> extends Thread implements Serializable
{
    List<String> serverAddresses = new ArrayList<>();
    int portNumber;    
    Forwarder<T> sender; 
    Mutex locker = new Mutex();
 
    
    public Node(int portNumber, Forwarder sender)
    {
        this.portNumber = portNumber;
        this.sender = sender;
    }
    
    public void addConnection(String address)
    {
        serverAddresses.add(address);
    }
    
    public void send(T data)
    {
        try 
        {
            for (int i = 0; i < serverAddresses.size(); i++) 
                send(i, data);
        } 
        catch (Exception e) 
        {
            System.out.println(e);
        }
    }
    
    public void send(int i, T data) 
    {
        
        String serverAddress = serverAddresses.get(i);
        try(Socket socket = new Socket(serverAddress, portNumber))
        {
            //socket.setSoTimeout(5000);
            ObjectOutputStream obj_writer = new ObjectOutputStream(socket.getOutputStream());
            obj_writer.writeObject(data);          
        }
        catch(SocketTimeoutException e)
        {
            System.out.println("The socket timed out");
        }
        catch(IOException e)
        {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
    
    public void run() 
    {
        // TODO code application logic here
        try(ServerSocket serverSocket = new ServerSocket(portNumber)) //socket number may already be reserved for other applications. So check to see if socket intance is created
        {
            while(true)
            {
                Socket socket = serverSocket.accept();
                try
        {
            ObjectInputStream obj_reader = new ObjectInputStream(socket.getInputStream());
            T data =  (T) obj_reader.readObject();
                    try {
                        locker.acquire();
                        sender.forward(data);
                        locker.release();
                    } catch (InterruptedException ex) {
                        System.out.println("FORWARDER FAILED");
                        Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                    }
            
            try
            {
                Thread.sleep(3000);
            }
            catch(InterruptedException e)
            {
                System.out.println("Thread interrupted");
            }  
        }
        catch(IOException e)
        {
            System.out.println("Oops: " + e.getMessage());
        }       catch (ClassNotFoundException ex) { 
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                } 
        
        finally
        {
            try
            {
                socket.close();
            }
            catch(IOException e)
            {
                //oh, well
            }
        }
            }
        }
        catch(IOException e)
        {
            System.out.println("Server exception " + e.getMessage());
        }
    }
    
       public static void main(String[] args) throws InterruptedException
    {
        BlockChain<String> node = new BlockChain<>();
        node.addConnection("104.201.246.65");
        node.init();
        
        Scanner kb = new Scanner(System.in);
        String input;
        while(true)
        {       
            input = kb.nextLine();
            node.add(input);
            
            
            System.out.println(node);
        }          
    }
}