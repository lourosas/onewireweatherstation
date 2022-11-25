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

public class RawDataToWeatherDataConverter{
   private static Calendar _cal = null;

   /////////////////////Constructors//////////////////////////////////
   /**/
   public RawDataToWeatherDataConverter(){}

   ///////////////////////Public Methods//////////////////////////////
   ///////////////////////Static Methods//////////////////////////////
   /*
   */
   public static WeatherData barometricPressure(String rawData){
      WeatherData data = null;
      calendar(rawData);
      return data;
   }

   /*
   */
   public static WeatherData dewpoint(String rawData){
      WeatherData data = null;
      calendar(rawData);
      return data;
   }

   /*
   */
   public static WeatherData heatindex(String rawData){
      WeatherData data = null;
      calendar(rawData);
      return data;
   }

   /*
   */
   public static WeatherData humidity(String rawData){
      WeatherData data = null;
      calendar(rawData);
      return data;
   }

   /*
   */
   public static WeatherData temperature(String rawData)
   throws NullPointerException{
      WeatherData data = null;
      calendar(rawData);
      if(rawData == null){
         throw new NullPointerException();
      }
      return data;
   }

   //////////////////////Private Methods//////////////////////////////
   /**/
   private static void calendar(String rawData)
   throws NullPointerException{
      _cal = Calendar.getInstance();
      try{
         String mdy = rawData.split(" ")[0];
         String hms = rawData.split(" ")[1];
         String [] yearmonthday = mdy.split("-");
         String yr              = yearmonthday[0];
         String mt              = yearmonthday[1];
         String dy              = yearmonthday[2];
         int year               = Integer.parseInt(yr);
         int month              = Integer.parseInt(mt);
         int day                = Integer.parseInt(dy);
         String [] hourminsec   = hms.split(":");
         int hour               = Integer.parseInt(hourminsec[0]);
         int min                = Integer.parseInt(hourminsec[1]);
         int sec = Integer.parseInt(hourminsec[2].split("\\.")[0]);
         _cal.set(year, month - 1, day, hour, min, sec);
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      catch(ArrayIndexOutOfBoundsException e){
         WeatherDataParser wdp = new WeatherDataParser();
         String date = wdp.parseCalendar(rawData);
         System.out.println(date);
         DateFormat df = DateFormat.getDateInstance();
         try{
            _cal.setTime(df.parse(date));
         }
         catch(ParseException pe){
            pe.printStackTrace();
         }
      }
   }
}
