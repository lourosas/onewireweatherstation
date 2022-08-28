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
import java.text.DateFormat;
import java.text.ParseException;
import rosas.lou.weatherclasses.*;

public class CurrentWeatherClient extends WeatherClientDataPublisher
implements Runnable{
   /*TODO--make a static method in CurrentWeatherServer to get
     this value*/
   private static final int PORT    = 19000;
   private static final int TIMEOUT = 20000;
   
   private DatagramSocket _socket;
   private byte[] _addr;
   private String _rawData;
   private List<WeatherData> _weatherData;
   {
      //_addr = new byte[]{(byte)192, (byte)168, (byte)1, (byte)145};
      //_addr = new byte[]{(byte)192, (byte)168, (byte)0, (byte)173};
      _addr = new byte[]{(byte)70, (byte)162, (byte)74, (byte)239};
      _socket      = null;
      _rawData     = null;
      _weatherData = null;
   };

   ///////////////////////Constructors///////////////////////////////
   /*
   TODO--make a wholebunch more constructors that that take observers
   */
   public CurrentWeatherClient(){}

   //////////////////Runnable Interface Implementation///////////////
   public void run(){
      while(true){
         try{
            this.requestWeatherDataFromServer();
            this.convertToWeatherData();
            this.publishData(this._rawData);
            this.publishData(this._weatherData);
            Thread.sleep(60000);
         }
         catch(InterruptedException ie){}
      }
   }

   //////////////////Private Methods/////////////////////////////////
   /*
   */
   private void convertToWeatherData(){
      try{
         try{
            this._weatherData.clear();
         }
         catch(NullPointerException e){
            this._weatherData = new LinkedList<WeatherData>();
         }
         WeatherDataParser wdp = new WeatherDataParser();
         String cal = wdp.parseTemperatureCalendar(this._rawData);
         Calendar calendar =
                           wdp.parseRawDataCalStringIntoCalendar(cal);
         String message = wdp.parseTemperatureMessage(this._rawData);
         String dataS = wdp.parseTemperatureMetric(this._rawData);
         double temp = (double)Thermometer.DEFAULTTEMP;
         try{
            temp = Double.parseDouble(dataS);
         }
         catch(NumberFormatException npe){
            npe.printStackTrace();
            temp = (double)Thermometer.DEFAULTTEMP;
         }
         WeatherData tempData = new TemperatureData(Units.METRIC,
                                                    temp, message,
                                                    calendar);
         this._weatherData.add(tempData);

         cal         = wdp.parseHumidityCalendar(this._rawData);
         calendar    = wdp.parseRawDataCalStringIntoCalendar(cal);
         message     = wdp.parseHumidityMessage(this._rawData);
         dataS       = wdp.parseHumidity(this._rawData);
         double humi = Hygrometer.DEFAULTHUMIDITY;
         try{
             humi = Double.parseDouble(dataS);
         }
         catch(NumberFormatException nfe){
            nfe.printStackTrace();
            humi = Hygrometer.DEFAULTHUMIDITY;
         }
         WeatherData humiData = new HumidityData(Units.PERCENTAGE,
                                                 humi, message,
                                                 calendar);
         this._weatherData.add(humiData);

         cal         = wdp.parsePressureCalendar(this._rawData);
         calendar    = wdp.parseRawDataCalStringIntoCalendar(cal);
         message     = wdp.parsePressureMessage(this._rawData);
         dataS       = wdp.parsePressureMetric(this._rawData);
         double pres = Barometer.DEFAULTPRESSURE;
         try{
            pres = Double.parseDouble(dataS);
         }
         catch(NumberFormatException nfe){
            nfe.printStackTrace();
            pres = Barometer.DEFAULTPRESSURE;
         }
         WeatherData presData = new PressureData(Units.METRIC,
                                                 pres,
                                                 message,
                                                 calendar);
         this._weatherData.add(presData);

         cal       = wdp.parseDewpointCalendar(this._rawData);
         calendar  = wdp.parseRawDataCalStringIntoCalendar(cal);
         message   = wdp.parseDewpointMessage(this._rawData);
         dataS     = wdp.parseDewpointMetric(this._rawData);
         double dp = (double)Thermometer.DEFAULTTEMP;
         try{
            dp = Double.parseDouble(dataS);
         }
         catch(NumberFormatException nfe){
            nfe.printStackTrace();
            dp = (double)Thermometer.DEFAULTTEMP;
         }
         WeatherData dpData = new DewpointData(Units.METRIC, dp,
                                               message, calendar);
         this._weatherData.add(dpData);

         cal       = wdp.parseHeatIndexCalendar(this._rawData);
         calendar  = wdp.parseRawDataCalStringIntoCalendar(cal);
         message   = wdp.parseHeatIndexMessage(this._rawData);
         dataS     = wdp.parseHeatIndexMetric(this._rawData);
         double hi = (double)Thermometer.DEFAULTTEMP;
         try{
            hi = Double.parseDouble(dataS);
         }
         catch(NumberFormatException nfe){
            nfe.printStackTrace();
            hi = (double)Thermometer.DEFAULTTEMP;
         }
         WeatherData hiData = new HeatIndexData(Units.METRIC, hi,
                                                message, calendar);
         this._weatherData.add(hiData);
         
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      System.out.println(this._weatherData.get(0));
      System.out.println(this._weatherData.get(1));
      System.out.println(this._weatherData.get(2));
      System.out.println(this._weatherData.get(3));
      System.out.println(this._weatherData.get(4));
   }

   /*
   */
   private void requestWeatherDataFromServer(){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> data            = new LinkedList();
      try{
         this._socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!!
         this._socket.setSoTimeout(TIMEOUT);
         String testData = new String("poop");
         byte test[] = testData.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this._addr);
         byte[] receiveData = new byte[1024];
         System.out.println(iNetAddr.getHostName());
         System.out.println(iNetAddr.getHostAddress());
         sendPacket = new DatagramPacket(test,
                                         test.length,
                                         iNetAddr,
                                         PORT);
         this._socket.send(sendPacket);
         receivePacket =
                 new DatagramPacket(receiveData, receiveData.length);
         this._socket.receive(receivePacket);
         this._rawData = new String(receivePacket.getData());
         System.out.println(receivePacket.getAddress());
         System.out.println(receivePacket.getPort());
         System.out.println(receivePacket.getLength());
         System.out.println(this._rawData);
      }
      catch(SocketTimeoutException ste){
         ste.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }
}
