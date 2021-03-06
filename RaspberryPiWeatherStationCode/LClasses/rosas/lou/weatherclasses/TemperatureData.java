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

public class TemperatureData extends ThermalData
implements WeatherData{
   {
      _type = WeatherDataType.TEMPERATURE;
   };
   
   //***********************Constructors******************************
   /*
   */
   public TemperatureData(){}
   
   /*
   */
   public TemperatureData(Units units, double data){
      this(units, data, null);
   }
   
   /*
   */
   public TemperatureData(Units units, double data, String message){
      this.data(units, data, message);
   }

   /*
   */
   public TemperatureData
   (
      Units units,
      double data,
      String message,
      Calendar cal
   ){
      this.data(units, data, message, cal);
   }

   /*
   */
   public TemperatureData
   (
      Units units,
      double data,
      String message,
      String month,
      String day,
      String year,
      String time
   ){
      this.data(units,data,message,month,day,year,time);
   }
   
   //******************Interface Implementation***********************
   /**/
   public double absoluteData(){
      return super.absoluteData();
   }
   
   /**/
   public double englishData(){
      return super.englishData();
   }
   
   /**/
   public double metricData(){
      return super.metricData();
   }
   
   /**/
   public double percentageData(){
      return super.percentageData();
   }

   public Calendar calendar(){
      return super.calendar();
   }

   /**/
   public void data
   (
      Units units,
      double data,
      String message,
      Calendar cal
   ){
      super.data(units, data, message, cal);
   }
   
   /**/
   public void data(Units units, double data, String message){
      super.data(units, data, message, null);
   }
   
   /**/
   public void data(Units units, double data){
      super.data(units, data, null, null);     
   }
   
   /**/
   public String message(){
      return super.message();
   }
   
   /**/
   public WeatherDataType type(){
      return this._type;
   }
   
   /**/
   public String toStringAbsolute(){
      return super.toStringAbsolute();
   }
   
   /**/
   public String toStringEnglish(){
      return super.toStringEnglish();
   }
   
   /**/
   public String toStringMetric(){
      return super.toStringMetric();
   }
   
   /**/
   public String toStringPercentage(){
      return super.toStringPercentage();
   } 
   
   //******************Public Methods*********************************
   public String toString(){
      String tString = new String(this.toStringAbsolute() + ", ");
      tString = tString.concat(this.toStringEnglish() + ", ");
      tString = tString.concat(this.toStringMetric() + ", ");
      tString = tString.concat(this.type() + ", ");
      tString = tString.concat(this.message());
      try{
         String cal = String.format("%tc", this.calendar());
         tString = tString.concat(":  " + cal);
      }
      catch(NullPointerException npe){}
      return tString;
   }
}
