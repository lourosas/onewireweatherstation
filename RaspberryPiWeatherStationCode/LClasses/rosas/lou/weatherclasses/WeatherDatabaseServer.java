/*
Copyright 2020 Lou Rosas

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
import rosas.lou.weatherclasses.*;

//////////////////////////////////////////////////////////////////////
public class WeatherDatabaseServer extends SimpleServer
implements Runnable{
   private final int PORT = 20001;

   private List<WeatherData> _temperature;
   private List<WeatherData> _humidity;
   private List<WeatherData> _pressure;
   private List<WeatherData> _dewpoint;
   private List<WeatherData> _heatIndex;
   private DatagramSocket    _socket;

   {
      address       = null;
      addr          = null;
      _temperature  = null;
      _humidity     = null;
      _pressure     = null;
      _dewpoint     = null;
      _heatIndex    = null;
      _socket       = null;
   };

   ///////////////////////Public Methods//////////////////////////////
   ////////////////////////Constructor////////////////////////////////
   /*
   */
   public WeatherDatabaseServer(){
      this.setUpAddress(this.PORT);
      this.setUpTheServer();
   }

   /////////////////////Interface Implementations/////////////////////
   public void run(){
      while(true){
         try{
            this.waitForRequests();
            Thread.sleep(100);
         }
         catch(InterruptedException ie){}
      }
   }

   ////TemperatureHumidityObserver Interface Implementation///////////
   public void updateTemperature(WeatherData data){}
   public void updateTemperatureMetric(double metric){}
   public void updateTemperatureEnglish(double english){}
   public void updateTemperatureAbsolute(double absolute){}
   public void updateHumidity(WeatherData data){}
   public void updateHumidity(double percentage){}

   //////////BarometerObserver Interface Implementation///////////////
   public void updatePressure(WeatherEvent event){}
   public void updatePressure(WeatherStorage store){}
   public void updatePressure(WeatherData data){}
   public void updatePressureAbsolute(double absolute){}
   public void updatePressureEnglish(double english){}
   public void updatePressureMetric(double metric){}

   /////////CalculatorObserver Interface Implementation///////////////
   public void updateDewpoint(WeatherEvent event){}
   public void updateDewpoint(WeatherStorage store){}
   public void updateDewpoint(WeatherData data){}
   public void updateDewpointAbsolute(double absolute){}
   public void updateDewpointEnglish(double english){}
   public void updateDewpointMetric(double metric){}
   public void updateHeatIndex(WeatherEvent event){}
   public void updateHeatIndex(WeatherStorage store){}
   public void updateHeatIndex(WeatherData data){}
   public void updateHeatIndexAbsolute(double absolute){}
   public void updateHeatIndexEnglish(double english){}
   public void updateHeatIndexMetric(double metric){}
   public void updateWindChill(WeatherEvent event){}

   ////////////////////////Private Methods////////////////////////////
   /*
   */
   private void setUpTheServer(){
      try{
         this._socket = new DatagramSocket(this.PORT);
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   /*
   */
   private void waitForRequests(){
      DatagramPacket receivePacket  = null;
      List<String>   receiveData    = null;
      while(true){
         try{
            byte data[]    = new byte[8192];
            receivePacket  = new DatagramPacket(data, data.length);
            this._socket.receive(receivePacket);
            InetAddress addr = receivePacket.getAddress();
            int port         = receivePacket.getPort();
            String received  = new String(receivePacket.getData(), 0,
                                         receivePacket.getLength());
            System.out.println(addr);
            System.out.println(port);
            System.out.println(received);
            String temp = "Send back something";
            data = temp.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data,
                                                           data.length,
                                                           addr,
                                                           port);
            this._socket.send(sendPacket);
         }
         catch(IOException ioe){
            ioe.printStackTrace();
         }
      }
   }
}
//////////////////////////////////////////////////////////////////////
