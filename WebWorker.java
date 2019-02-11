/*
   Yarely Ogaz
   CS 371: Program 1
   WebWorker.java
*/
import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;

public class WebWorker implements Runnable{
private Socket socket;

//Constructor
public WebWorker(Socket s){
   socket = s;
}


public void run(){
	
   System.err.println("Handling connection...");
   
   try {
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();

      String filePath = readHTTPRequest(is);		//turns the file path into a string
      writeHTTPHeader(os,"text/html",filePath);		//prints out filePath	
      writeContent(os,"text/html",filePath);			//writes out filePath

      os.flush();
      socket.close();

   } catch (Exception e) {
      System.err.println("Output error: "+e);
   }
   
   System.err.println("Done handling connection.");
   return;
   
}//end run


private String readHTTPRequest(InputStream is){

   String line;
   String path = "";
   int count = 0;
   BufferedReader r = new BufferedReader(new InputStreamReader(is));
   
   while(true){
   
      try {
      
         while (!r.ready()) 
	      	Thread.sleep(1);
            
         line = r.readLine();
         System.err.println("Request line: ("+line+")");
         
         if(line.contains("GET ")){
            path = line.substring(4);
            while(!(path.charAt(count) == ' '))
               count++;
            path = path.substring(0,count);
         }
         
         if (line.length()==0) 
            break;
            
      } catch (Exception e) {
         System.err.println("Request error: "+e);
         break;
      }
	 
   }//end while
   
   return path;
   
}//end readHTTPRequest


private void writeHTTPHeader(OutputStream os, String contentType, String filePath) throws Exception{

   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("MST7MDT"));
   
   String pathCopy = "." + filePath.substring(0,filePath.length());
   
   try{
      
      FileReader inputFile = new FileReader( pathCopy );
      BufferedReader bufferFile = new BufferedReader( inputFile );
      os.write("HTTP/1.1 200 OK\n".getBytes());
      
   } catch( FileNotFoundException e){
      
      os.write( "HTTP/1.1 404 Not Found\n".getBytes() );
      System.err.println("File not found: " + pathCopy);
      
   } 
   
   os.write("Date: ".getBytes());
   os.write((df.format(d)).getBytes());
   os.write("\n".getBytes());
   os.write("Server: Yare's very own server\n".getBytes());
   
   //os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
   //os.write("Content-Length: 438\n".getBytes()); 
   
   os.write("Connection: close\n".getBytes());
   os.write("Content-Type: ".getBytes());
   os.write(contentType.getBytes());
   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   
   return;
   
}//end writeHTTPHeader


private void writeContent(OutputStream os, String contentType, String filePath) throws Exception{
	
      String str = "";
      String pathCopy = "." + filePath.substring(0,filePath.length());
      
      Date d = new Date();
      DateFormat df = DateFormat.getDateTimeInstance();
      df.setTimeZone(TimeZone.getTimeZone("MST/MDT"));
      
      try{
      
          File inputFile = new File( pathCopy );
          FileReader readerFile = new FileReader( inputFile);
          BufferedReader bufferFile = new BufferedReader( readerFile );
   
          while((str = bufferFile.readLine()) != null){	//reads through all lines and changes tags if found
	   
	          if(str.contains("cs371date"))
		         str = str.replace("cs371date", df.format(d));  
        	   
	          if(str.contains("cs371server"))
		         str = str.replace("cs371server", "Server: Yare's very own server\n"); 
               
	          os.write( str.getBytes() );
             os.write("\n".getBytes() );
             
	      }//end while
	   
	   }catch(FileNotFoundException e){
	
         System.err.println("File not found: " + filePath);
      
      }        
                   
}//end writeContent

} // end class
