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
import gnu.io.*;
import com.dalsemi.onewire.utils.Convert;

public class HumidityData implements WeatherData{
   private double _humidity;
   private double _type;
   private String _message;
   
   {
      _humidity = WeatherData.DEFAULTHUMIDITY;
      _type     = WeatherDataType.HUMIDITY;
      _message  = null;
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
   public HumdityData(Units units, double value, String message){
      this.data(units, value, message);
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
   public data(Units units, double humidity, String message){
      this.humidity(humidity);
      this.message(message);
   }
   
   /**/
   public data(Units units, double humidity){
      this.data(units, humidity, null);     
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
      return new String(this._percentage + "%");
   }
   
   //*********************Private Methods*****************************
   /*
   */
   private void humidity(double value){
      if(value >= 0. && value <= 100.){
         this._humdity = value;
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