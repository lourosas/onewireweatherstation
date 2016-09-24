import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import rosas.lou.weatherclasses.Database;

public class ASimpleServer2{
   private DatagramSocket socket;

   public static void main(String[] args){
      new ASimpleServer2();
   }

   public ASimpleServer2(){
      try{
         this.socket = new DatagramSocket(9307);//Set the port high
         this.waitForPackets();
      }
      catch(Exception e){
         e.printStackTrace();
         System.exit(1);
      }
   }

   public void waitForPackets(){
      List<String> receiveData = null;
      while(true){
         try{
            byte data[] = new byte[500];
            DatagramPacket receivePacket =
                                new DatagramPacket(data, data.length);
            this.socket.receive(receivePacket);
            InetAddress addr = receivePacket.getAddress();
            int port         = receivePacket.getPort();
            System.out.println(receivePacket.getAddress());
            System.out.println(receivePacket.getPort());
            System.out.println(receivePacket.getLength());
            String received = new String(receivePacket.getData(), 
                                         0,
                                         receivePacket.getLength());
            System.out.println(received);
            if(received.toUpperCase().equals("TEMPERATURE")){
               receiveData = this.findTemperatureData();
               String dataSize = "" + receiveData.size();
               data = dataSize.getBytes();
               DatagramPacket sendPacket =
                                   new DatagramPacket(data,
                                                     data.length,
                                                     addr,
                                                     port);
               this.socket.send(sendPacket);
               Iterator<String> it = receiveData.iterator();
               while(it.hasNext()){
                  data = it.next().getBytes();
                  sendPacket.setData(data, 0, data.length);
                  sendPacket.setAddress(addr);
                  sendPacket.setPort(port);
                  //System.out.println(it.next());
                  this.socket.send(sendPacket);
               }
            }
            //this.printASystemCommand();
         }
         catch(IOException ioe){ ioe.printStackTrace(); }
      }
   }

   public List<String> findTemperatureData(){
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER = "root";
      final String PASS = "password";
      Database database = Database.getInstance();
      List<String> data = database.requestData("temperature");
      System.out.println("Size:  " + data.size());
      System.out.println("database:  " + database);
      return data;
   }

   public void printASystemCommand(){
      try{
         System.out.println("This should print");
         Process p = 
            Runtime.getRuntime().exec("ls /home/pi/bin/");
         BufferedReader input = new BufferedReader(
                          new InputStreamReader(p.getInputStream()));
         String s = input.readLine();
         while(s != null){
            if(s.contains(".csv")){
               System.out.println(s);
            }
            s = input.readLine();
         }
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }
}
