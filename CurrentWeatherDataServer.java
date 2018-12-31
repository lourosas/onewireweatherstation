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
import rosas.lou.weatherclasses.*;

//////////////////////////////////////////////////////////////////////
public class CurrentWeatherDataServer extends SimpleServer
implements Runnable, TemperatureHumidityObserver, BarometerObserver,
CalculatedObserver{
   private final int PORT = 19000; //The port for the server
   private WeatherData    _temperature;
   private WeatherData    _humidity;
   private WeatherData    _pressure;
   private WeatherData    _dewpoint;
   private WeatherData    _heatIndex;
   private DatagramSocket _socket;

   {
      address      = null;
      addr         = null;
      _temperature = null;
      _humidity    = null;
      _pressure    = null;
      _dewpoint    = null;
      _heatIndex   = null;
      _socket      = null;      
   }

   ////////////////////////Constructor////////////////////////////////
   public CurrentWeatherDataServer(){
      this.setUpAddress(this.PORT);
      this.setUpTheServer();
   }
   
   /////////////////Interface Implementations/////////////////////////
   /////////////////Runnable Interface Implementation/////////////////
   public void run(){
      while(true){
         try{
            this.waitForRequests();
            //Sleep for a quarter of a second
            Thread.sleep(250);
         }
         catch(InterruptedException ie){}
      }
   }
   /////TemperatureHumidityObserver Interface Implementation//////////
   /**/
   public void updateTemperature(WeatherData data){
      this._temperature = data;
   }

   /**/
   public void updateTemperatureMetric(double metric){}

   /**/
   public void updateTemperatureEnglish(double english){}

   /**/
   public void updateTemperatureAbsolute(double absolute){}

   /**/
   public void updateHumidity(WeatherData data){
      this._humidity = data;
   }

   /**/
   public void updateHumidity(double percentage){}

   //////////BarometerObserver Interface Implementation////////////////
   /**/
   public void updatePressure(WeatherEvent event){}

   /**/
   public void updatePressure(WeatherStorage store){}

   /**/
   public void updatePressure(WeatherData data){
      this._pressure = data;
   }

   /**/
   public void updatePressureAbsolute(double absolute){}

   /**/
   public void updatePressureEnglish(double english){}

   /**/
   public void updatePressureMetric(double metric){}

   /////////CalculatedObserver Interface Implementation///////////////
   /**/
   public void updateDewpoint(WeatherEvent event){}

   /**/
   public void updateDewpoint(WeatherStorage store){}

   /**/
   public void updateDewpoint(WeatherData data){
      this._dewpoint = data;
   }

   /**/
   public void updateDewpointAbsolute(double absolute){}

   /**/
   public void updateDewpointEnglish(double english){}

   /**/
   public void updateDewpointMetric(double metric){}

   /**/
   public void updateHeatIndex(WeatherEvent event){}

   /**/
   public void updateHeatIndex(WeatherStorage store){}

   /**/
   public void updateHeatIndex(WeatherData data){
      this._heatIndex = data;
   }

   /**/
   public void updateHeatIndexAbsolute(double absolute){}

   /**/
   public void updateHeatIndexEnglish(double english){}

   /**/
   public void updateHeatIndexMetric(double metric){}

   /**/
   public void updateWindChill(WeatherEvent event){}

   /////////////////////Private Methods///////////////////////////////
   /*
   */
   private void setUpTheServer(){
      try{
         this._socket = new DatagramSocket(this.PORT);
      }
      catch(Exception e){ e.printStackTrace(); }
   }
   /*
   */
   private void waitForRequests(){
      DatagramPacket receivePacket = null;
      List<String>   receiveData   = null;
      while(true){
         try{
            byte data[]   = new byte[1024];
            receivePacket = new DatagramPacket(data, data.length);
            this._socket.receive(receivePacket);
            InetAddress addr = receivePacket.getAddress();
            int port         = receivePacket.getPort();
            String received  = new String(receivePacket.getData(), 0,
                                          receivePacket.getLength());
            System.out.println("Received:  " + received);
            String temp = this._temperature.toString();
            temp += "\n" + this._humidity.toString();
            temp += "\n" + this._pressure.toString();
            temp += "\n" + this._dewpoint.toString();
            temp += "\n" + this._heatIndex.toString();
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
