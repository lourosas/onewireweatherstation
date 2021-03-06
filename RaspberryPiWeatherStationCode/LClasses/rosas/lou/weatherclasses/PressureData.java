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

public class PressureData implements WeatherData{
   private double _absolutePressure;
   private double _englishPressure;
   private double _metricPressure;
   private Calendar _cal;
   private WeatherDataType _type;
   private String _message;
   private String _month;
   private String _day;
   private String _year;
   private String _time;
   
   {
      _absolutePressure = WeatherData.DEFAULTVALUE;
      _englishPressure  = WeatherData.DEFAULTVALUE;
      _metricPressure   = WeatherData.DEFAULTVALUE;
      _type             = WeatherDataType.PRESSURE;
      _cal              = null;
      _message          = null;
      _month            = null;
      _day              = null;
      _year             = null;
      _time             = null;
   };
   
   //************************Constructors*****************************
   /*
   */
   public PressureData(){}
   
   /**/
   public PressureData(Units units, double value){
      this(units, value, null);
   }

   /**/   
   public PressureData(Units units, double value, String message){
      this.data(units, value, message);
   }

   /**/
   public PressureData
   (
      Units units,
      double value,
      String message,
      Calendar cal
   ){
      this.data(units, value, message, cal);
   }

   /**/
   public PressureData
   (
      Units units,
      double value,
      String message,
      String month,
      String day,
      String year,
      String time
   ){
      this.data(units,value,message,month,day,year,time);
   }

   //******************Interface Implementation***********************
   /**/
   public double absoluteData(){
      return this._absolutePressure;
   }
   
   /**/
   public double englishData(){
      return this._englishPressure;
   }
   
   /**/
   public double metricData(){
      return this._metricPressure;
   }
   
   /**/
   public double percentageData(){
      return WeatherData.DEFAULTHUMIDITY;
   }

   /**/
   public Calendar calendar(){
      return this._cal;
   }

   /**/
   public void data
   (
      Units  units,
      double value,
      String message,
      String month,
      String day,
      String year,
      String time
   ){
      this.data(units, value, message);
      this._month = new String(month);
      this._day   = new String(day);
      this._year  = new String(year);
      this._time  = new String(time);
   }
   
   /**/
   public void data
   (
      Units units,
      double data,
      String message,
      Calendar cal
   ){
      this.pressure(units, data);
      this.message(message);
      this.calendar(cal);
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
   public WeatherDataType type(){
      return this._type;
   }
   
   /**/
   public String toStringAbsolute(){
      String save = String.format("%.1f", this._absolutePressure);
      save = save.concat(" mb");
      return save;
   }
   
   /**/
   public String toStringEnglish(){
      String save = String.format("%.2f", this._englishPressure);
      save = save.concat(" inHg");
      return save;
   }
   
   /**/
   public String toStringMetric(){
      String save = String.format("%.2f", this._metricPressure);
      save = save.concat(" mmHg");
      return save;
   }
   
   /**/
   public String toStringPercentage(){
      return null;
   }
   
   /*
   */
   public String toString(){
      String pString = new String(this.toStringAbsolute() + ", ");
      pString = pString.concat(this.toStringEnglish() + ", ");
      pString = pString.concat(this.toStringMetric() + ", ");
      pString = pString.concat(this.type() + ", ");
      pString = pString.concat(this.message());
      try{
         String cal = String.format("%tc", this.calendar());
         pString = pString.concat(":  " + cal);
      }
      catch(NullPointerException npe){}
      return pString;
   }
   
   //*********************Private Methods*****************************
   /**/
   private void calendar(Calendar cal){
      this._cal = cal;
   }

   /**/
   private void pressure(Units units, double data){
      if(data > WeatherData.DEFAULTVALUE){
         if(units == Units.ABSOLUTE){
            this._absolutePressure = data;
            this._englishPressure=WeatherConvert.millibarsToInches(data);
            this._metricPressure =
                          WeatherConvert.millibarsToMillimeters(data);
         }
         else if(units == Units.ENGLISH){
            this._englishPressure = data;
            this._absolutePressure = 
                               WeatherConvert.inchesToMillibars(data);
            this._metricPressure =
                             WeatherConvert.inchesToMillimeters(data);
         }
         else if(units == Units.METRIC){
            this._metricPressure = data;
            this._englishPressure =
                             WeatherConvert.millimetersToInches(data);
            this._absolutePressure =
              WeatherConvert.inchesToMillibars(this._englishPressure);
         }
      }
   }
   
   /**/
   private void message(String message){
      if(message != null){
         this._message = new String(message);
      }
   }
}
