package P2P_Hybrid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Won
 */
public class Node<T> extends Thread
{
    List<T> data_received = new ArrayList<>();
    int portNumber;
    List<String> serverAddresses = new ArrayList<>();
    
    public Node(int portNumber)
    {
        this.portNumber = portNumber;
    }
    
    public void addConnection(String address)
    {
        serverAddresses.add(address);
    }
    
    public void fileData(T data)
    {
        data_received.add(data);
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
        
        // TODO code application logic here
        String serverAddress = serverAddresses.get(i);
        try(Socket socket = new Socket(serverAddress, portNumber))
        {
            //socket.setSoTimeout(5000);
            ObjectOutputStream obj_writer = new ObjectOutputStream(socket.getOutputStream());
            
            Scanner scanner = new Scanner(System.in);
            String echoString;
            String response;
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
            data_received.add((T) obj_reader.readObject());
                           
            System.out.println("Recieved Message: " + data_received.get(data_received.size()-1));
            
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
    
    public static void main(String[] args)
    {
        
        Node<Message> node = new Node(9091);        
        
        node.addConnection("192.168.1.4");
        node.start();
        
        Scanner kb = new Scanner(System.in);
        String input = "hello";
        Message msg = new Message(input);
        while(true)
        {
            input = kb.nextLine();
            node.send(msg);
        }
        
        
        
    }
   
        
}
    
    
   

