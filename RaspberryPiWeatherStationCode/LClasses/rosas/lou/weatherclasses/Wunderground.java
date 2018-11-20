//////////////////////////////////////////////////////////////////////
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
//////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.text.Format;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.*;
import java.io.*;
import rosas.lou.weatherclasses.*;

public class Wunderground implements TemperatureHumidityObserver,
BarometerObserver, CalculatedObserver, Runnable{
   //Timeout in 5 seconds
   private final long TIMEOUT = 5000;
   //
   private final String USERNAME = "KAZTUCSO647";
   //
   private final String PASSWORD = "7ku8hcyj";
   //
   private final String URL      = "weatherstation.wunderground.com";

   private WeatherData _temperature;
   private WeatherData _humidity;
   private WeatherData _pressure;
   private WeatherData _heatIndex;
   private WeatherData _dewPoint;

   private Socket _socket;

   {
      _temperature = null;
      _humidity    = null;
      _pressure    = null;
      _heatIndex   = null;
      _dewPoint    = null;
      _socket      = null;
   }

   //////////////////////////Constructor//////////////////////////////
   public Wunderground(){}

   ////////////////////////Public Methods/////////////////////////////

   /////////////////Interface Implementations/////////////////////////
   //
   //Implementation of the Runnable interface
   //
   public void run(){
      boolean toRun = true;
      //Thread t = Thread.currentThread();
      while(toRun){
         try{
            //Publish data to Wunderground
            StringBuffer message = this.setUpMessage();
            this.sendToWunderground(message.toString().trim());
            //Sleep for 10 mins, then publish to Wunderground again
            Thread.sleep(600000);
         }
         catch(InterruptedException ie){
            toRun = false;
            ie.printStackTrace();
         }
      }
   }

   ////Implementation of the TemperatureHumidityObserver Interface////
   /*
   */
   public void updateTemperature(WeatherData data){
      this._temperature = data;
   }

   /*
   */
   public void updateTemperatureMetric(double temp){}

   /*
   */
   public void updateTemperatureEnglish(double temp){}

   /*
   */
   public void updateTemperatureAbsolute(double temp){}

   /*
   */
   public void updateHumidity(WeatherData data){
      this._humidity = data;
   }

   /*
   */
   public void updateHumidity(double humidity){}

   ///////Implementation of the BarometerObserver Interface//////////
   /*
   */
   public void updatePressure(WeatherEvent event){}

   /*
   */
   public void updatePressure(WeatherStorage store){}

   /*
   */
   public void updatePressure(WeatherData data){
      this._pressure = data;
   }

   /*
   */
   public void updatePressureAbsolute(double data){}

   /*
   */
   public void updatePressureEnglish(double data){}

   /*
   */
   public void updatePressureMetric(double data){}

   ///////Implementation of the CalculatedObserver Interface/////////
   /*
   */
   public void updateDewpoint(WeatherEvent event){}

   /*
   */
   public void updateDewpoint(WeatherStorage storage){}

   /*
   */
   public void updateDewpoint(WeatherData data){
      this._dewPoint = data;
   }

   /*
   */
   public void updateDewpointAbsolute(double data){}

   /*
   */
   public void updateDewpointEnglish(double data){}

   /*
   */
   public void updateDewpointMetric(double data){}

   /*
   */
   public void updateHeatIndex(WeatherEvent event){}

   /*
   */
   public void updateHeatIndex(WeatherStorage store){}

   /*
   */
   public void updateHeatIndex(WeatherData data){
      this._heatIndex = data;
   }

   /*
   */
   public void updateHeatIndexAbsolute(double hi){}

   /*
   */
   public void updateHeatIndexEnglish(double hi){}

   /*
   */
   public void updateHeatIndexMetric(double hi){}

   /*
   */
   public void updateWindChill(WeatherEvent event){}


   //Private Methods
   //
   //
   //
   private String findUTC(){
      String UTC = new String();
      SimpleDateFormat formatter =
             new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
      formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
      try{
         if(this._temperature != null){
            Calendar cal = this._temperature.calendar();
            Date date    = cal.getTime();
            UTC          = formatter.format(date);
         }
         else if(this._humidity != null){
            Calendar cal = this._humidity.calendar();
            Date date    = cal.getTime();
            UTC          = formatter.format(date);
         }
         else if(this._pressure != null){
            Calendar cal = this._pressure.calendar();
            Date date    = cal.getTime();
            UTC          = formatter.format(date);
         }
         else{
            throw new NullPointerException();
         }
         UTC = UTC.replaceAll(":", "%3A");
      }
      catch(NullPointerException npe){
         UTC = new String("1001-01-01+00%3A00%3A00");
      }
      return UTC;
   }

   //
   //
   //
   private void sendToWunderground(String message){
      System.out.println(message);
      final int PORT = 443;
      final int TIMES = 100;
      try{
         URL url = new URL(message);
         URLConnection conn = url.openConnection();
         BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                          conn.getInputStream()));
         int i = 0;
         while(i < TIMES){
            if(in.ready()){
               System.out.println(in.readLine());
            }
            try{ Thread.sleep(100); }
            catch(InterruptedException ie){}
            ++i;
         }
      }
      catch (MalformedURLException me){
         System.out.println("Wunderground URL Exception:  " + me);
      }
      catch(IOException ioe){
         System.out.println("Wunderground I/O Error:  " + ioe);
      }
   }

   //
   //
   //
   private StringBuffer setUpMessage(){
      StringBuffer message = new StringBuffer();

      message.append("https://" + URL);
      message.append("/weatherstation/updateweatherstation.php?");
      message.append("ID=" + this.USERNAME);
      message.append("&PASSWORD=" + this.PASSWORD);
      message.append("&dateutc="+this.findUTC());
      try{
         message.append("&tempf=" + this._temperature.englishData());
      }
      catch(NullPointerException npe0){
         message.append("&tempf=" + Thermometer.DEFAULTTEMP);
      }
      try{
         message.append("&humidity="+this._humidity.percentageData());
      }
      catch(NullPointerException npe1){
         message.append("&humidity="+ Hygrometer.DEFAULTHUMIDITY);
      }
      try{
         message.append("&baromin=" + this._pressure.englishData());
      }
      catch(NullPointerException npe2){
         message.append("&baromin="+ Barometer.DEFAULTPRESSURE);
      }
      //if(this.heatIndexUpdated){
      //   message.append("&heatidxf=" + this.heatIndex.getValue());
      //}
      try{
         message.append("&dewptf=" + this._dewPoint.englishData());
      }
      catch(NullPointerException npe3){
         message.append("&dewptf=" + Thermometer.DEFAULTTEMP);
      }
      message.append("&softwaretype=tws&action=updateraw ");
      //message.append("HTTP//1.1\r\nConnection: keep-alive\r\n\r\n");
      //message.append("HTTP//1.1");
      return message;
   }
}
