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

   private DatagramSocket    _socket;
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
   private void request(String command){
      DatagramPacket  sendPacket    = null;
      DatagramPacket  receivePacket = null;
      List<String>    data          = new LinkedList();
      try{
         this._socket = new DatagramSocket();
         this._socket.setSoTimeout(TIMEOUT);
         String sendData = new String(command);

         byte temp[]          = sendData.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this._addr);
         byte [] receiveData  = new byte[8192];

         System.out.println("HostName:  " + iNetAddr.getHostName());
         System.out.println("HostAddr:  "+iNetAddr.getHostAddress());

         sendPacket = new DatagramPacket(temp,
                                         temp.length,
                                         iNetAddr,
                                         PORT);

         this._socket.send(sendPacket);
         receivePacket =
                  new DatagramPacket(receiveData, receiveData.length);
         this._socket.receive(receivePacket);
         this._rawData = new String(receivePacket.getData());

         System.out.println("Return Data:  " + this._rawData);
         System.out.println("addr: " + receivePacket.getAddress());
         System.out.println("port:  " + receivePacket.getPort());
         System.out.println("length:  " + receivePacket.getLength());
         System.out.println();
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
   private void requestTemperatureData(){
      this.request("Request Temperature Data");

   }

   /*
   */
   private void requestHumidityData(){
      this.request("Request Humidity Data on any date");
   }

   /*
   */
   private void requestPressureData(){
      String theRequest = new String("Request Pressure data");
      theRequest += " on a given date";
      this.request(theRequest);
   }

   /*
   */
   private void requestDewpointData(){
      String theRequest = new String("Request Dewpoint data");
      theRequest += " on a given date; entering in the date";
      this.request(theRequest);
   }

   /*
   */
   private void requestHeatIndexData(){
      String theRequest = new String("Request Heatindex data");
      theRequest += " on a given date; entering in the date; ";
      theRequest += " and the Units.";
      this.request(theRequest);   }
}
