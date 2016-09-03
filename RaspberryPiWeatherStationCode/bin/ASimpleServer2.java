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
         this.socket = new DatagramSocket(9312);//Set the port high
         this.waitForPackets();
      }
      catch(Exception e){
         e.printStackTrace();
         System.exit(1);
      }
   }

   public void waitForPackets(){
      while(true){
         try{
            byte data[] = new byte[500];
            DatagramPacket receivePacket =
                                new DatagramPacket(data, data.length);
            this.socket.receive(receivePacket);
            System.out.println(receivePacket.getAddress());
            System.out.println(receivePacket.getPort());
            System.out.println(receivePacket.getLength());
            String received = new String(receivePacket.getData(), 
                                         0,
                                         receivePacket.getLength());
            System.out.println(received);
            if(received.toUpperCase().equals("TEMPERATURE")){
               this.findTemperatureData();
            }
         }
         catch(IOException ioe){ ioe.printStackTrace(); }
      }
   }

   public void findTemperatureData(){
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER = "root";
      final String PASS = "password";
      String select = new String("SELECT * from temperaturedata;");
      System.out.println(select);
      Database database = Database.getInstance();
      List<String> data = database.requestData("temperature");
      Iterator<String> it = data.iterator();
      while(it.hasNext()){
         System.out.println(it.next());
      }
      System.out.println("Size:  " + data.size());
      System.out.println("database:  " + database);
   }
}
