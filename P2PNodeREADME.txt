
Utilizes multi-threading for reading writing.

//Initialize 

P2PNode<DataType> self = new P2PNode<DataType>(int port_number_to_interact_on);
self.addConnection(String server_addresses) //add a list of initial IPAddresses to begin the network 

self.start();                     // a secondary thread starts running in the background and picks up any data sent on the network 
self.send(DataType data_to_send); //sends data to all the known connections 


----
Need to add code to automatically add/remove users that connect to the network 
