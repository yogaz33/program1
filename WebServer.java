/*
   Yarely Ogaz
   CS 371: Program 1
   WebServer.java
*/
import java.net.*;

public class WebServer{

private ServerSocket socket;
private boolean running;


//Constructor
private WebServer(){
   running = false;
}


private boolean start(int port){

   Socket workerSocket;
   WebWorker worker;
   
   try{
      socket = new ServerSocket(port);
   }catch (Exception e){
      System.err.println("Error binding to port "+port+": "+e);
      return false;
   }
   
   while(true){
      try{
         // wait and listen for new client connection
         workerSocket = socket.accept();
      }catch (Exception e){
         System.err.println("No longer accepting: "+e);
         break;
      }
	  
      // have new client connection, so fire off a worker on it
      worker = new WebWorker(workerSocket);
      new Thread(worker).start();
	  
   }//end while
   
   return true;
} // end start


private boolean stop(){
   return true;
}


public static void main(String args[]){

   int port = 8080;
   
   if (args.length > 1){
      System.err.println("Usage: java Webserver <portNumber>");
      return;
   } 
   else if (args.length == 1){
      try {
         port = Integer.parseInt(args[0]);
      } catch (Exception e) {
         System.err.println("Argument must be an int ("+e+")");
         return;
      }
   }
   
   WebServer server = new WebServer();
   if (!server.start(port)) {
      System.err.println("Execution failed!");
   }
   
} // end main

} // end class

