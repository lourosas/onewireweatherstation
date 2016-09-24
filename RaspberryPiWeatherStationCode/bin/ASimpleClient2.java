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
         byte[] receiveData = new byte[64];
         byte[] addr = new byte[]
                             {(byte)192,(byte)168,(byte)1,(byte)130};
         InetAddress iNetAddr = InetAddress.getByAddress(addr);
         DatagramPacket sendPacket = new DatagramPacket(data,
            data.length, iNetAddr, 9301);
         this.socket.send(sendPacket);
         System.out.println("Poop");
         DatagramPacket receivePacket =
                 new DatagramPacket(receiveData, receiveData.length);
         this.socket.receive(receivePacket);
         String output = new String(receivePacket.getData());
         int size = Integer.parseInt(output.trim());
         System.out.println(size);
         boolean toPrint = true;
         int i = 0;
         do{
            this.socket.receive(receivePacket);
            System.out.println(i + ": "+receivePacket.getAddress()+
             ", " + receivePacket.getLength());
            output = new String(receivePacket.getData());
            System.out.println(output);
            ++i;
            toPrint = (i < size);
         }while(true);
      }
      catch(Exception e){ e.printStackTrace(); }
   }
}
