import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ASimpleClient2{
   private DatagramSocket socket;

   public static void main(String[] args){
      new ASimpleClient2();
   }

   public ASimpleClient2(){
      try{
         this.socket = new DatagramSocket();
         String message = new String("Temperature");
         byte data[] = message.getBytes();
         byte[] addr = new byte[]
                             {(byte)192,(byte)168,(byte)1,(byte)119};
         InetAddress iNetAddr = InetAddress.getByAddress(addr);
         DatagramPacket sendPacket = new DatagramPacket(data,
            data.length, iNetAddr, 9312);
         socket.send(sendPacket);
         System.out.println("Poop");
      }
      catch(Exception e){ e.printStackTrace(); }
   }
}