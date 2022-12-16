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

public class HeatIndexData extends ThermalData implements WeatherData{
   {
      _type = WeatherDataType.HEATINDEX;
   };
   
   //***********************Constructors******************************
   /**/
   public HeatIndexData(){}

   /**/
   public HeatIndexData(Units units, double data){
      this(units, data, null);
   }

   /**/
   public HeatIndexData(Units units, double data, String message){
      this.data(units, data, message);
   }

   /**/
   public HeatIndexData
   (
      Units units,
      double data,
      String message,
      Calendar cal
   ){
      this.data(units, data, message, cal);
   }

   /**/
   public HeatIndexData
   (
      Units  units,
      double data,
      String message,
      String month,
      String day,
      String year,
      String time
   ){
      this.data(units, data, message, month,day, year, time);
   }
   
   //******************Interface Implementation***********************
   /**/
   public Calendar calendar(){
      return super.calendar();
   }

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
      String hiString = new String();
      try{
         hiString = hiString.concat(this.toStringAbsolute() + ", ");
         hiString = hiString.concat(this.toStringEnglish() + ", ");
         hiString = hiString.concat(this.toStringMetric() + ", ");
         hiString = hiString.concat(this.type() + ", ");
         hiString = hiString.concat(this.message());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         hiString = hiString.concat(" Not all Data Received, ");
         hiString = hiString.concat("data may be corrupted");
      }
      try{
         String cal = String.format("%tc", this.calendar());
         hiString = hiString.concat(":  " + cal);
      }
      catch(NullPointerException npe){}
      return hiString;
   }
}
