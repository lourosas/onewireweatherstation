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

public class HumidityData implements WeatherData{
   private double          _humidity;
   private WeatherDataType _type;
   private Calendar        _cal;
   private String          _message;
   private String          _month;
   private String          _day;
   private String          _year;
   private String          _time;
   
   {
      _humidity = WeatherData.DEFAULTHUMIDITY;
      _type     = WeatherDataType.HUMIDITY;
      _cal      = null;
      _message  = null;
      _month    = null;
      _day      = null;
      _year     = null;
      _time     = null;
   };
   
   //************************Constructors*****************************
   /*
   */
   public HumidityData(){}
   
   /*
   */
   public HumidityData(Units units, double value){
      this(units, value, null);
   }
   
   /**/
   public HumidityData(Units units, double value, String message){
      this.data(units, value, message);
   }

   /**/
   public HumidityData
   (
      Units units,
      double value,
      String message,
      Calendar cal
   ){
      this.data(units, value, message, cal);
   }

   /**/
   public HumidityData
   (
      Units  units,
      double data,
      String message,
      String month,
      String day,
      String year,
      String time
   ){
      this.data(units, data, message, month, day, year, time);
   }
   
   //******************Interface Implementation***********************
   /**/
   public double absoluteData(){
      return WeatherData.DEFAULTHUMIDITY;
   }
   
   /**/
   public double englishData(){
      return this.absoluteData();
   }
   
   /**/
   public double metricData(){
      return this.absoluteData();
   }
   
   /**/
   public double percentageData(){ return this._humidity; }

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
      double humidity,
      String message,
      Calendar cal
   ){
      this.humidity(humidity);
      this.message(message);
      this.calendar(cal);
   }

   /**/
   public void data(Units units, double humidity, String message){
      this.data(units,humidity,message,null);
   }
   
   /**/
   public void data(Units units, double humidity){
      this.data(units, humidity, null, null);     
   }

   /**/
   public void data
   (
      Units  units,
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
   public String message(){
      return this._message;
   }
   
   /**/
   public WeatherDataType type(){
      return this._type;
   }
   
   /**/
   public String toStringAbsolute(){
      return null;
   }
   
   /**/
   public String toStringEnglish(){
      return null;
   }
   
   /**/
   public String toStringMetric(){
      return null;
   }
   
   /**/
   public String toStringPercentage(){
      String save = String.format("%.2f", this._humidity);
      save = save.concat(" %");
      return save;
   }
   
   //********************Pubilc Methods********************************
   public String toString(){
      String hString = new String(this.toStringPercentage() + ", ");
      hString = hString.concat(this.type() +", " + this.message());
      try{
         String cal = String.format("%tc", this.calendar());
         hString = hString.concat(":  " + cal);
      }
      catch(NullPointerException npe){}
      return hString;
      
   }
   
   //*********************Private Methods*****************************
   /*
   */
   private void calendar(Calendar cal){
      this._cal = cal;
   }

   /*
   */
   private void humidity(double value){
      if(value >= 0. && value <= 100.){
         this._humidity = value;
      }
      else{
         this._humidity = WeatherData.DEFAULTHUMIDITY;
      }
   }
   
   /*
   */
   private void message(String message){
      if(message != null){
         this._message = new String(message);
      }
   }
}
