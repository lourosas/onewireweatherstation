/*
Copyright 2022 Lou Rosas

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
import java.text.*;
import rosas.lou.weatherclasses.*;

public class WeatherDataStringToWeatherDataConverter{
   private static Calendar _cal = null;

   /////////////////////////Constructors//////////////////////////////
   /**/
   public WeatherDataStringToWeatherDataConverter(){}

   ////////////////////////Public Methods/////////////////////////////
   ////////////////////////Static Methods/////////////////////////////
   /**/
   public static WeatherData dewpoint(String rawData){
      WeatherData data = null;
      WeatherDataParser wdp = new WeatherDataParser();
      double dp;
      try{
         calendar(rawData);
         String message = wdp.parseDewpointMessage(rawData);
         if(message.toUpperCase().equals("GOOD")){
            dp = Double.parseDouble(wdp.parseDewpointMetric(rawData));
            data = new DewpointData(Units.METRIC,dp,"good",_cal);
         }
         else{
            throw new NumberFormatException("Not Good");
         }
      }
      catch(NumberFormatException nfe){
         dp = WeatherData.DEFAULTVALUE;
         data = new DewpointData(Units.METRIC,
                                 dp,
                                 nfe.getMessage(),
                                 _cal);
      }

      return data;
   }

   /**/
   public static WeatherData heatindex(String rawData){
      WeatherData data = null;
      WeatherDataParser wdp = new WeatherDataParser();
      double hi;
      try{
         calendar(rawData);
         String message = wdp.parseHeatIndexMessage(rawData);
         if(message.toUpperCase().equals("GOOD")){
            hi =Double.parseDouble(wdp.parseHeatIndexMetric(rawData));
            data = new HeatIndexData(Units.METRIC,
                                     hi,
                                     "good",
                                     _cal);
         }
         else{
            throw new NumberFormatException("Not Good");
         }
      }
      catch(NumberFormatException nfe){
         hi = WeatherData.DEFAULTVALUE;
         data = new HeatIndexData(Units.METRIC,
                                  hi,
                                  nfe.getMessage(),
                                  _cal);
      }
      return data;
   }

   /**/
   public static WeatherData humidity(String rawData){
      WeatherData data = null;
      WeatherDataParser wdp = new WeatherDataParser();
      double humidity;
      try{
         calendar(rawData);
         String message = wdp.parseHumidityMessage(rawData);
         if(message.toUpperCase().equals("GOOD")){
            humidity = Double.parseDouble(wdp.parseHumidity(rawData));
            data = new HumidityData(Units.PERCENTAGE,
                                    humidity,
                                    "good",
                                    _cal);
         }
         else{
            throw new NumberFormatException("Not Good");
         }
      }
      catch(NumberFormatException npe){
         humidity = WeatherData.DEFAULTHUMIDITY;
         data = new HumidityData(Units.PERCENTAGE,
                                 humidity,
                                 npe.getMessage(),
                                 _cal);
      }

      return data;
   }
   
   /*
   */
   public static WeatherData pressure(String rawData){
      WeatherData data = null;
      WeatherDataParser wdp = new WeatherDataParser();
      double pressure;
      try{
         calendar(rawData);
         String message = wdp.parsePressureMessage(rawData);
         if(message.toUpperCase().equals("GOOD")){
            pressure =
               Double.parseDouble(wdp.parsePressureAbsolute(rawData));
            data = new PressureData(Units.ABSOLUTE,
                                    pressure,
                                    "good",
                                    _cal);
         }
         else{
            throw new NumberFormatException("Not Good");
         }
      }
      catch(NumberFormatException npe){
         pressure = WeatherData.DEFAULTVALUE;
         data = new PressureData(Units.ABSOLUTE,
                                 pressure,
                                 npe.getMessage(),
                                 _cal);
      }
      return data;
   }

   /**/
   public static WeatherData temperature(String rawData){
      WeatherData data      = null;
      WeatherDataParser wdp = new WeatherDataParser();
      double temp;
      try{
         calendar(rawData);
         String message = wdp.parseTemperatureMessage(rawData);
         if(message.toUpperCase().equals("GOOD")){
            temp = 
              Double.parseDouble(wdp.parseTemperatureMetric(rawData));
            data = new TemperatureData(Units.METRIC,temp,"good",_cal);
         }
         else{
            throw new NumberFormatException("Not Good");
         }
      }
      catch(NumberFormatException nfe){
         temp = WeatherData.DEFAULTVALUE;
         data = new TemperatureData(Units.METRIC,
                                    temp,
                                    nfe.getMessage(),
                                    _cal);
      }

      return data;
   }

   ////////////////////////Private Methods////////////////////////////
   /**/
   private static void calendar(String rawData){
      if(_cal == null){
         _cal = Calendar.getInstance();
      }
      WeatherDataParser wdp = new WeatherDataParser();
      String date = wdp.parseCalendar(rawData);
      SimpleDateFormat sdf =
                   new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
      try{
         _cal.setTime(sdf.parse(date));
      }
      catch(ParseException pe){
         pe.printStackTrace();
      }
   }

}
