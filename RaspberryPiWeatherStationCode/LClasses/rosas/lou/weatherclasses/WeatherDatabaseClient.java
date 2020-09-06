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
import java.text.DateFormat;
import java.text.ParseException;
import rosas.lou.weatherclasses.*;

public class WeatherDatabaseClient{
   private static final int PORT     = 20001;
   private static final int TIMEOUT  = 20000;

   private DatagramSockect   _socket;
   private byte[]            _addr;
   private String            _rawData;
   private List<WeatherData> _temperatureData;
   private List<WeatherData> _humidityData;
   private List<WeatherData> _pressureData;
   private List<WeatherData> _dewpointData;
   private List<WeatherData> _heatIndexData;

   {
      //Open it "up" later when running...
      _addr = new byte[]{(byte)192, (byte)168, (byte)1, (byte)114};
      _socket          = null;
      _rawData         = null;
      _temperatureData = null;
      _humidityData    = null;
      _pressureData    = null;
      _dewpointData    = null;
      _heatIndexData   = null;
   };

   /////////////////////////Public Methods////////////////////////////
   /////////////////////////Constructors//////////////////////////////
   /*
   */
   public WeatherDatabaseClient(){
      this.requestData();
   }

   /**/
   public void requestData(){
      //request the data
      this.requestTemperatureData();
      this.requestHumidityData();
      this.requestPressureData();
      this.requestDewpointData();
      this.requestHeatIndexData();
   }

   //////////////////////Private Methods//////////////////////////////
   /*
   */
   public void requestTemepratureData(){
      DatagramPacket  sendPacket    = null;
      DatagramPacket  receivePacket = null;
      List<String>    data          = new LinkedList();
      try{
         this._socket = new DatagramSocket();
         this._socket.setSoTimeout(TIMEOUT);
         String tempData = new String("Request Temperature Data");

         byte temp[]          = tempData.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this._addr);
         byte [] receiveData  = new byte[8192];

         System.out.println(iNetAddr.getHostName());
         System.out.println(iNetAddr.getHostAddress());

         sendPacket = new DatagramPacket(temp,
                                         temp.length,
                                         iNetAddr,
                                         PORT);
          
         this._socket.send(sentPacket);
         receivePacket =
                  new DatagramPacket(receiveData, receiveData.length);
         this._socket.receive(receivePacket);
         this._rawData = new String(receivePacket.getData());

         System.out.println(this._rawData);
         System.out.println(receivePacket.getAddress());
         System.out.println(receivePacket.getPort());
         System.out.println(receivePacket.getLength());
      }
      catch(SocketTimeoutException ste){
         ste.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   /*
   */
   public void requestHumidityData(){}

   /*
   */
   public void requestPressureData(){}

   /*
   */
   public void requestDewpointData(){}

   /*
   */
   public void requestHeatIndexData(){}
}
