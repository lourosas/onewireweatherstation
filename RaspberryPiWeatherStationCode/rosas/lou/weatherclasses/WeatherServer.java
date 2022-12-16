/*
Copyright 2018 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import rosas.lou.weatherclasses.*;

public class WeatherServer implements TemperatureObserver,
BarometerObserver, HumidityObserver, CalculatedObserver, TimeObserver,
Runnable{
   //Do not open up a port lower than this!!!
   private final int PORT_LIMIT = 9000;

   private int            port;
   private DatagramSocket socket;
   private WeatherStation station;
   private WeatherStorage weatherStorage;
   private String         currentTime;

   {
      currentTime    = null;
      socket         = null;
      port           = -1;
      station        = null;
      weatherStorage = null;
   }

   //Constructor
   public WeatherServer(int port){
      try{
         this.setPort(port);
         this.setUpTheServer();
      }
      catch(Exception e){
         System.out.println("Server NOT Set up:  EXITING");
         System.exit(1);
      }
   }

   /////////////////////////Public Methods///////////////////////////
   /**
   **/
   public int getPort(){
      return this.port;
   }

   /**
   **/
   public void registerWithAWeatherStation(WeatherStation station){
      station.addBarometerObserver(this);
      station.addCalculatedObserver(this);
      station.addHumidityObserver(this);
      station.addTemperatureObserver(this);
      station.addTimeObserver(this);
      //It totally sucks I have to do this like this...
      this.station = station;
      
   }

   //////////////////////Interface Implementations///////////////////
   /**
   Implementation of the Runnable Interface
   **/
   public void run(){
      while(true){
         try{
            //wait for a client to request data
            this.waitForPackets();
            Thread.sleep(1000);
         }
         catch(InterruptedException ie){}
      }
   }
   /**
   Implementation of the Calculated Observer Interface
   **/
   public void updateDewpoint(WeatherEvent event){}

   /**
   Implementation of the Calculated Observer Interface
   **/
   public void updateDewpoint(WeatherStorage data){
   }

   /**
   Implementation of the Calculated Observer Interface
   **/
   public void updateHeatIndex(WeatherEvent event){}

   /**
   Implementation of the Calculated Observer Interface
   **/
   public void updateHeatIndex(WeatherStorage data){
   }

   /**
   Implementation of the Humidity Observer Interface
   **/
   public void updateHumidity(WeatherEvent event){}

   /**
   **/
   public void updateHumidity(WeatherStorage data){
   }

   /**
   Implementation of the Barometer Observer Interface
   **/
   public void updatePressure(WeatherEvent event){}

   /**
   Implementation of the Barometer Observer Interface
   **/
   public void updatePressure(WeatherStorage data){
   }

   /**
   Implementation of the Temperature Observer Interface
   **/
   public void updateTemperature(WeatherEvent event){}

   /**
   Implementation of the Temperature Observer Interface
   **/
   public void updateTemperature(WeatherStorage data){
      this.weatherStorage = data;
   }

   /**
   Implementation of the Time Observer Interface
   **/
   public void updateTime(){}

   /**
   Implementation of the Time Observer Interface
   **/
   public void updateTime(String formatedTime){
      this.currentTime = new String(formatedTime);
   }

   /**
   Implementation of the Time Observer Interface
   **/
   public void updateTime(String mo, String day, String yr){}

   /**
   Implementation of the Time Observer Interface
   **/
   public void updateTime
   (
      String yr,
      String mo,
      String day,
      String hr,
      String min,
      String sec){}

   /**
   Implementation of the Calculated Observer Interface
   **/
   public void updateWindChill(WeatherEvent event){}
   

   ////////////////////////Private Methods///////////////////////////
   //
   private void setPort(int port){
      this.port = port;
   }

   //
   private void setUpTheServer() throws Exception{
      try{
         if(this.getPort() >= this.PORT_LIMIT){
            this.socket = new DatagramSocket(this.getPort());
         }
         else{
            throw new Exception("Port "+this.getPort()+" too low");
         }
      }
      catch(Exception e){
         e.printStackTrace();
         throw e;
      }
   }

   //
   //
   //
   private void waitForPackets(){
      DatagramPacket receivePacket = null;
      List<String>   receiveData   = null;
      while(true){
         try{
            byte data[]   = new byte[500];
            receivePacket = new DatagramPacket(data, data.length);
            this.socket.receive(receivePacket);
            InetAddress addr = receivePacket.getAddress();
            int port         = receivePacket.getPort();
            String received  = new String(receivePacket.getData(), 0,
                                          receivePacket.getLength());
            receiveData = this.station.requestData(received);
            String dataSize = "" + receiveData.size();
            data = dataSize.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(
                                                          data,
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
               this.socket.send(sendPacket);
            }
         }
         catch(IOException ioe){
            ioe.printStackTrace();
         }
      }
   }
}
