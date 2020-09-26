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
   private int               _port;
   private String[]           _dates;
   private String            _rawData;
   private List<WeatherData> _temperatureData;
   private List<WeatherData> _humidityData;
   private List<WeatherData> _pressureData;
   private List<WeatherData> _dewpointData;
   private List<WeatherData> _heatIndexData;

   {
      //Open it "up" later when running...
      //_addr = new byte[]{(byte)192, (byte)168, (byte)1, (byte)114};
      _addr = new byte[]{(byte)68, (byte)98, (byte)39, (byte)39};
      _port            = PORT;
      _dates           = new String[]{"January", "01", "2017"};
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
   public WeatherDatabaseClient(){}

   /**/
   public void requestData(String measurement){
      this.requestData(measurement, this._dates);
   }

   /*
   */
   public void requestData(String measurement, String [] values){
      if((measurement.toUpperCase()).contains("TEMP")){
         this.requestTemperatureData(values);
      }
      else if((measurement.toUpperCase()).contains("HUMI")){
         this.requestHumidityData(values);
      }
      else if((measurement.toUpperCase()).contains("PRES")){
         this.requestPressureData(values);
      }
      else if((measurement.toUpperCase()).contains("DEWP")){
         this.requestDewpointData(values);
      }
      else if((measurement.toUpperCase()).contains("HEAT")){
         this.requestHeatIndexData(values);
      }
   }

   /**/
   public void setDay(String day){
      this._dates[1] = day;
   }

   /**/
   public void setMonth(String month){
      this._dates[0] = month;
   }

   public void setYear(String year){
      this._dates[2] = year;
   }

   /**/
   public void setServerAddress(String address){
      int [] addr = new int[4];
      String [] values = address.split("\\.");
      try{
         for(int i = 0; i < values.length; i++){
            addr[i] = Integer.parseInt(values[i]);
         }
         this._addr = new byte[]{(byte)addr[0],
                                 (byte)addr[1],
                                 (byte)addr[2],
                                 (byte)addr[3]};
      }
      //Elevate this at some point
      catch(NumberFormatException nfe){}
   }

   /**/
   public void setServerPort(String port){
      try{
         this._port = Integer.parseInt(port.trim());
      }
      //Elevate this at some point
      catch(NumberFormatException nfe){}
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
         byte [] receiveData  = new byte[16384];

         System.out.println("HostName:  " + iNetAddr.getHostName());
         System.out.println("HostAddr:  "+iNetAddr.getHostAddress());

         sendPacket = new DatagramPacket(temp,
                                         temp.length,
                                         iNetAddr,
                                         this._port);

         this._socket.send(sendPacket);
         receivePacket =
                  new DatagramPacket(receiveData, receiveData.length);
         this._socket.receive(receivePacket);
         this._rawData = new String(receivePacket.getData());

         System.out.println("Return Data:\n" + this._rawData);
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
   private void requestTemperatureData(String [] values){
      String requestString = new String("TEMPERATURE " + values[0]);
      requestString += " " + values[1] + " " + values[2];
      this.request(requestString);
   }

   /*
   */
   private void requestHumidityData(String [] values){
      String requestString = new String("HUMIDITY " + values[0]);
      requestString += " " + values[1] + " " + values[2];
      this.request(requestString);
   }

   /*
   */
   private void requestPressureData(String [] values){
      String requestString = new String("PRESSURE " + values[0]);
      requestString += " " + values[1] + " " + values[2];
      this.request(requestString);
   }

   /*
   */
   private void requestDewpointData(String [] values){
      String requestString = new String("DEWPOINT " + values[0]);
      requestString += " " + values[1] + " " + values[2];
      this.request(requestString);
   }

   /*
   */
   private void requestHeatIndexData(String [] values){
      String requestString = new String("HEATINDEX " + values[0]);
      requestString += " " + values[1] + " " + values[2];
      this.request(requestString);
   }
}
