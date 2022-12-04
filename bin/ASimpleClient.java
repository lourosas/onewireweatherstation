import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ASimpleClient{
   public static void main(String[] args){
      new ASimpleClient();
   }

   public ASimpleClient(){
      try{
         String address = "192.168.1.119";
         Socket s = new Socket(address, 9203);
         //BufferedReader input = new BufferedReader(
         //                new InputStreamReader(s.getInputStream()));
         //String value = input.readLine();
         System.out.println("Client:  " + s.getRemoteSocketAddress());
         BufferedReader input = new BufferedReader(
                           new InputStreamReader(s.getInputStream()));
         while(true){
            System.out.println(input.readLine());
            try{ Thread.sleep(1000); }
            catch(InterruptedException ie){}
            input = new BufferedReader(
                           new InputStreamReader(s.getInputStream()));
            
         }
         //System.exit(0);
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }
}