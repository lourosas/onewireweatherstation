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

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.*;
//import gnu.io.*;
//import com.dalsemi.onewire.utils.Convert;

public class ThermalData implements WeatherData{
   protected double          _absolute;
   protected double          _english;
   protected double          _metric;
   protected Calendar        _cal;
   protected WeatherDataType _type;
   protected String          _message;
   protected String          _month;
   protected String          _day;
   protected String          _year;
   protected String          _time;
   
   {
      _absolute = WeatherData.DEFAULTVALUE;
      _english  = WeatherData.DEFAULTVALUE;
      _metric   = WeatherData.DEFAULTVALUE;
      _type     = WeatherDataType.TEMPERATURE;
      _cal      = null;
      _message  = null;
      _month    = null;
      _day      = null;
      _year     = null;
      _time     = null;
   };
   
   //******************Interface Implementation***********************
   /**/
   public double absoluteData(){
      return this._absolute;
   }
   
   /**/
   public double englishData(){
      return this._english;
   }
   
   /**/
   public double metricData(){
      return this._metric;
   }
   
   /**/
   public double percentageData(){
      return WeatherData.DEFAULTVALUE;
   }

   /**/
   public Calendar calendar(){
      return this._cal;
   }

   /**/
   public String month(){
      return this._month;
   }

   /**/
   public String day(){
      return this._day;
   }

   /**/
   public String year(){
      return this._year;
   }

   /**/
   public String time(){
      return this._time;
   }
   
   /**/
   public void data
   (
      Units units,
      double data,
      String message,
      Calendar cal
   ){
      if(data > WeatherData.DEFAULTVALUE){
         if(units == Units.ABSOLUTE){
            this._absolute = data;
            this._english  = WeatherConvert.kelvinToFahrenheit(data);
            this._metric   = WeatherConvert.kelvinToCelsius(data);
         }
         else if(units == Units.ENGLISH){
            this._english  = data;
            this._absolute = WeatherConvert.fahrenheitToKelvin(data);
            this._metric   = WeatherConvert.fahrenheitToCelsius(data);
         }
         else if(units == Units.METRIC){
            this._metric   = data;
            this._absolute = WeatherConvert.celsiusToKelvin(data);
            this._english  = WeatherConvert.celsiusToFahrenheit(data);
         }
      }
      //Need to be able to handle properly when bad data comes in
      else{
         this._metric   = data;
         this._absolute = data;
         this._english  = data;
      }
      //Hopefully, there is a message attached with the data, for the
      //purpose of communication
      if(message != null){
         this._message = new String(message);
      }
      this._cal = cal;
   }

   /**/
   public void data
   (
      Units units,
      double data,
      String message,
      String month,
      String day,
      String year,
      String time
   ){
      this.data(units, data, message);
      this._month = new String(month);
      this._day   = new String(day);
      this._year  = new String(year);
      this._time  = new String(time);
   }

   /**/
   public void data(Units units, double data, String message){
      this.data(units, data, message, null);
   }
   
   
   /**/
   public void data(Units units, double data){
      this.data(units, data, null, null);     
   }
   
   /**/
   public String message(){
      return this._message;
   }
   
   /**/
   public WeatherDataType type(){
      return this._type;
   }
   
   /**/
   public String toStringAbsolute(){
      String save = String.format("%.2f", this._absolute);
      save = save.concat(" K");
      return save;
   }
   
   /**/
   public String toStringEnglish(){
      String save = String.format("%.2f", this._english);
      save = save.concat(" \u00B0"+"F");
      return save;
   }
   
   /**/
   public String toStringMetric(){
      String save = String.format("%.2f", this._metric);
      save = save.concat(" \u00B0"+"C");
      return save;
   }
   
   /**/
   public String toStringPercentage(){
      return null;
   }   
}
