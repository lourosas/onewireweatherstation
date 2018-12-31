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

Here is how the current Raw Data is entered:
<Absolute>,<English>,<Metric>,<Type>,<Comment>:<Date (Calendar)>"\n"

Where:
<Absolute> is the Absolute type measurement (Kelvin for thermal
type data, Millibars for Barometric Pressure).
<English> is the English type measurements (Fahrenheit for thermal
type data, inches Mercury for Barometric Pressure).
<Metric> is the Metric (Or System International) type measurements
(Celsius for thermal type data, Millimeters Mercury for Barometric
Pressure).
<Type> is the Type of measurement:  Temperature, Humidity,
Barometric Pressure, Dewpoint, Heat Index
<Comment> indicates the "condition" of the data (good, bad, not
calculated)
<Date> is the Calendar date of the measurement.
IE:  "Sat Dec 01 15:04:16 MST 2018"

For Humidity measurements, there is NO different units (just
percentage, so the data is measured in percentage ONLY).
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import rosas.lou.weatherclasses.*;

public class WeatherDataParser{
   private static final String [] MEASUREMENTS = {"TEMPERATURE",
                                                  "HUMIDITY",
                                                  "PRESSURE",
                                                  "DEWPOINT",
                                                  "HEATINDEX"};

   private final String [] MONTHS = {"JAN","FEB","MAR",
                                     "APR", "MAY","JUN","JUL",
                                     "AUG", "SEP","OCT",
                                     "NOV","DEC"};
   ////////////////////////Constructors///////////////////////////////
   public WeatherDataParser(){}

   ///////////////////////Public Methods//////////////////////////////
   /*
   Here is how the current Raw Data is entered:
   <Absolute>,<English>,<Metric>,<Type>,<Comment>:<Date(Calendar)>"\n"

   Where:
   <Absolute> is the Absolute type measurement (Kelvin for thermal
   type data, Millibars for Barometric Pressure).
   <English> is the English type measurements (Fahrenheit for thermal
   type data, inches Mercury for Barometric Pressure).
   <Metric> is the Metric (Or System International) type measurements
   (Celsius for thermal type data, Millimeters Mercury for Barometric
   Pressure).
   <Type> is the Type of measurement:  Temperature, Humidity,
   Barometric Pressure, Dewpoint, Heat Index
   <Comment> indicates the "condition" of the data (good, bad, not
   calculated)
   <Date> is the Calendar date of the measurement.
   IE:  "Sat Dec 01 15:04:16 MST 2018"

   For Humidity measurements, there is NO different units (just
   percentage, so the data is measured in percentage ONLY).
   */
   public String parseCalendar(String data){
      String calendar = null;
      try{
         String ref = this.parseRawTemperature(data);
         ref = this.parseData(ref, ",", MONTHS);
         String [] parts = ref.split(": ");
         calendar = parts[1].trim();
      }
      catch(NullPointerException npe){
         calendar = null;
      }
      return calendar;
   }

   /*
   */
   public String parseDewpointMessage(String data){
      String rawDewpoint = this.parseRawDewpoint(data);
      return this.parseForTheMessage(rawDewpoint);
   }

   /*
   */
   public String parseHeatIndexMessage(String data){
      String rawHeatIndex = this.parseRawHeatIndex(data);
      return this.parseForTheMessage(rawHeatIndex);
   }

   /*
   */
   public String parseHumidityMessage(String data){
      return this.parseForTheMessage(this.parseRawHumidity(data));
   }

   /*
   */
   public String parsePressureMessage(String data){
      return this.parseForTheMessage(this.parseRawPressure(data));
   }

   /*
   */
   public String parseTemperatureMessage(String data){
      return this.parseForTheMessage(this.parseRawTemperature(data));
   }

   /*
   */
   public String parseRawTemperature(String data){
      return this.parseData(data,"\n",MEASUREMENTS[0]);
   }

   /*
   */
   public String parseRawHumidity(String data){
      return this.parseData(data,"\n",MEASUREMENTS[1]);
   }

   /*
   */
   public String parseRawPressure(String data){
      return this.parseData(data,"\n",MEASUREMENTS[2]);
   }

   /*
   */
   public String parseRawDewpoint(String data){
      return this.parseData(data,"\n",MEASUREMENTS[3]);
   }

   /*
   */
   public String parseRawHeatIndex(String data){
      return this.parseData(data,"\n",MEASUREMENTS[4]);
   }

   /*
   */
   public String parseDewpointAbsolute(String data){
      String rawDP = this.parseRawDewpoint(data);
      return this.parseStringForStringNumericalValue(rawDP,0,", ");
   }

   /*
   */
   public String parseDewpointEnglish(String data){
      String rawDP = this.parseRawDewpoint(data);
      return this.parseStringForStringNumericalValue(rawDP,1,", ");
   }

   /*
   */
   public String parseDewpointMetric(String data){
      String rawDP = this.parseRawDewpoint(data);
      return this.parseStringForStringNumericalValue(rawDP,2,", ");
   }

   /*
   */
   public String parseHeatIndexAbsolute(String data){
      String rawHI = this.parseRawHeatIndex(data);
      return this.parseStringForStringNumericalValue(rawHI,0,", ");
   }

   /*
   */
   public String parseHeatIndexEnglish(String data){
      String rawHI = this.parseRawHeatIndex(data);
      return this.parseStringForStringNumericalValue(rawHI,1,", ");
   }

   /*
   */
   public String parseHeatIndexMetric(String data){
      String rawHI = this.parseRawHeatIndex(data);
      return this.parseStringForStringNumericalValue(rawHI,2,", ");
   }

   /*
   */
   public String parseHumidity(String data){
      String rawH = this.parseRawHumidity(data);
      return this.parseStringForStringNumericalValue(rawH,0,", ");
   }

   /*
   */
   public String parsePressureAbsolute(String data){
      String rawPres = this.parseRawPressure(data);
      return this.parseStringForStringNumericalValue(rawPres,0,", ");
   }

   /*
   */
   public String parsePressureEnglish(String data){
      String rawPres = this.parseRawPressure(data);
      return this.parseStringForStringNumericalValue(rawPres,1,", ");
   }

   /*
   */
   public String parsePressureMetric(String data){
      String rawPres = this.parseRawPressure(data);
      return this.parseStringForStringNumericalValue(rawPres,2,", ");
   }

   /*
   The weatherdata database sends back the data in the following form:
   <Month>, <Day>, <Year>, <Hour>:<Min>:<Sec> <Time Zone>
   as a String.
   This method will parse that string into siz separate integer values
   to set a Calendar instance for the purpose of storage.
   */
   public Calendar parseStringIntoCalendar(String data){
      Calendar cal = null;
      //String dateTime = this.parseData(data, ", ", MONTHS);
      String month=this.parseData(data,", ",MONTHS);
      String dS=this.parseStringForStringNumericalValue(data,1,", ");
      String yS=this.parseStringForStringNumericalValue(data,2,", ");
      String tS=this.parseStringForStringNumericalValue(data,3,", ");
      String hS=this.parseStringForStringNumericalValue(tS,0,":");
      String mS=this.parseStringForStringNumericalValue(tS,1,":");
      String sS=this.parseStringForStringNumericalValue(tS,2,":");
      sS=this.parseStringForStringNumericalValue(sS.trim(),0," ");
      try{
         int mon  =this.parseStringMonthForNumerical(month.trim());
         int date = Integer.parseInt(dS);
         int year = Integer.parseInt(yS);
         int hour = Integer.parseInt(hS);
         int min  = Integer.parseInt(mS);
         int sec  = Integer.parseInt(sS);
         cal = Calendar.getInstance();
         cal.set(year,mon,date,hour,min,sec);
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         cal = null;
      }
      return cal;
   }

   /*
   */
   public String parseTemperatureAbsolute(String data){
      String rawTemperature = this.parseRawTemperature(data);
      return this.parseStringForStringNumericalValue(rawTemperature,
                                                     0,
                                                     ", ");
   }

   /*
   public double parseTemperatureAbsolute(String data){
      String rawTemperature = this.parseRawTemperature(data);
      return 0.0;
   }
   */

   /*
   */
   public String parseTemperatureEnglish(String data){
      String rawTemp = this.parseRawTemperature(data);
      return this.parseStringForStringNumericalValue(rawTemp,1,", ");
   }

   /*
   */
   public String parseTemperatureMetric(String data){
      String rawTemp = this.parseRawTemperature(data);
      return this.parseStringForStringNumericalValue(rawTemp,2,", ");
   }

   ///////////////////////Private Methods/////////////////////////////
   /*
   */
   private String parseData(String data, String delim, String key){
      String returnString = null;
      boolean found       = false;
      int     i           = 0;
      try{
         String [] strings    = data.split(delim);
         do{
            String compare = strings[i];
            compare = compare.toUpperCase().trim();
            if(compare.contains(key)){
               found        = true;
               returnString = strings[i].trim();
            }
         }while((++i < strings.length) && !found);
      }
      catch(NullPointerException npe){
         returnString = null;
      }
      return returnString;
   }

   /*
   */
   private String parseData(String data,String delim,String [] keys){
      String returnString = null;
      boolean found       = false;
      int i               = 0;
      try{
         do{
            returnString = this.parseData(data, delim, keys[i]);
            if(returnString != null){
               found = true;
            }
         }while((++i < keys.length) && !found);
      }
      catch(NullPointerException npe){
         returnString = null;
      }
      return returnString;
   }

   /*
   The format for the raw data goes like the following (as from the
   Header notes)-<Message>: <Full Date>.  This means I can parse the
   data based on the month (it will be one of them), just like
   parsing for the Calendar, except get the first part of the string
   that was split up! Not the Second
   */
   private String parseForTheMessage(String rawData){
      String message = null;
      try{
         String ref = this.parseData(rawData, ",", MONTHS);
         String [] parts = ref.split(": ");
         message = parts[0].trim();
      }
      catch(NullPointerException npe){
         message = null;
      }
      return message;
   }

   /*
   */
   private int parseStringMonthForNumerical(String monthString){
      int month     = -1;
      boolean found = false;
      int i         = 0;

      String compare = monthString.toUpperCase();

      do{
         if(compare.contains(MONTHS[i])){
            found = true;
            month = i;
         }
      }while((++i < MONTHS.length) && !found);

      return month;
   }

   /*
   */
   private String parseStringForStringNumericalValue
   (
      String data,
      int index,
      String delim
   ){
      String returnString = null;
      String [] values = data.split(delim);
      returnString     = values[index].trim();
      double value     = Double.NaN;
      //NOTE:  THIS IS CRAP!!  Should have a '$' at the end!!!
      //But for some reason it won't work!!!!!
      Pattern pattern = Pattern.compile("^[-]?\\d*[.?]\\d*");
      Matcher matcher = pattern.matcher(returnString);
      try{
         if(matcher.find()){
            returnString = matcher.group();
            value = Double.parseDouble(returnString);
            returnString = String.format("%.2f", value);
         }
      }
      catch(NumberFormatException nfe){
         returnString = null;
      }
      return returnString;
   }
}
