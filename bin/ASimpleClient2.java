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
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      try{
         this.socket = new DatagramSocket();
         //String message = new String("Temperature");
         //String message = new String("Temperature");
         String message = new String("Select * from  heatindexdata ");
         message += "WHERE month = 'October' AND day = '30'";
         byte data[] = message.getBytes();
         byte[] addr = new byte[]
                             {(byte)192,(byte)168,(byte)1,(byte)112};
         InetAddress iNetAddr = InetAddress.getByAddress(addr);
         while(true){
            byte[] receiveData = new byte[64];
            sendPacket = new DatagramPacket(data,
                                            data.length,
                                            iNetAddr,
                                            19000);
            this.socket.send(sendPacket);
            System.out.println("Poop");
            receivePacket =
                 new DatagramPacket(receiveData, receiveData.length);
            this.socket.receive(receivePacket);
            String output = new String(receivePacket.getData());
            int size = Integer.parseInt(output.trim());
            int i = 0;
            while(i < size){
               this.socket.receive(receivePacket);
               System.out.println(i+ ": "+receivePacket.getAddress()+
                                   ", " + receivePacket.getLength());
               output = new String(receivePacket.getData());
               System.out.println(output);
               ++i;
            }
            System.out.println(size);
            try{ Thread.sleep(300000); }
            catch(InterruptedException ie){}
         }
      }
      catch(Exception e){ e.printStackTrace(); }
   }
}
