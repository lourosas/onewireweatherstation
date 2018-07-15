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


public class WeatherData{
   public static final double DEFAULTMEASURE  = -999.9;
   public static final double DEFAULTHUMIDITY =  -99.9;

   private double _metric;
   private double _english;
   private double _absolute;
   private double _percent;
   private String _message;

   private WeatherDataType _type;

   {
      _metric   = DEFAULTMEASURE;
      _english  = DEFAULTMEASURE;
      _absolute = DEFAULTMEASURE;
      _percent  = DEFAULTHUMIDITY;
      _message  = null;
   };

   //***********************Constructors*****************************
   /**/
   public WeatherData(){}

   /**/
   public WeatherData(WeatherDataType type,Units units,double data){
      this(type, units, data, null);
   }

   /**/
   public WeatherData
   (
      WeatherDataType type,
      Units           units,
      double          data,
      String          message
   ){
      this.data(type, units, data, message);
   }

   //***********************Public Methods***************************
   /*
   */
   public double absoluteData(){
      return this._absolute;
   }
   
   /*
   */
   public double englishData(){
      return this._english;
   }
   
   /*
   */
   public double metricData(){
      return this._metric;
   }
   
   /*
   */
   public double percentageData(){
      return this._percent;
   }
   
   /**
   Set the current data with a given message that can be checked to
   see if further information related to the type and condition
   of the data stored in the instance.
   */
   public void data
   (
      WeatherDataType type,
      Units           units,
      double          data,
      String          message
   ){
      this._type  = type;
      if(type == WeatherDataType.TEMPERATURE){
         this.genericThermalData(units, data);
      }
      else if(type == WeatherDataType.HUMIDITY){
         this.humidity(data);
      }
      else if(type == WeatherDataType.PRESSURE){
         this.barometricPressure(units, data);
      }
      else if(type == WeatherDataType.DEWPOINT){
         this.genericThermalData(units, data);
      }
      else if(type == WeatherDataType.HEATINDEX){
         this.genericThermalData(units, data);
      }
      try{
         this._message = new String(message);
      }
      catch(NullPointerException npe){
         //report nothing if a NULL message comes in
         this._message = new String("");
      }
   }

   /**
   Set the current data
   */
   public void data(WeatherDataType type, Units units, double data){
      this.data(type, units, data, null);
   }
   
   /*
   */
   public void data(Units, units, double data, String message){
      this.data(WeatherDataType.TEMPERATURE, units, data, message);
   }
   
   /*
   */
   public void data(Units units, double data){
      this.data(WeatherDataType.TEMPERATURE, units, data, null);
   }
   
   /**
   Get the current data--in the respective units.
   There is no guarantee the requester will get the correct data
   (Since no type checking is performed).  In addition, if the
   the Units are non-existent or Units.NULL, this returns the
   humidity value (since percentage is not really a Unit).
   */
   public double data(Units units){
      double currentData = DEFAULTMEASURE;
      if(units == Units.METRIC){
         currentData = this._metric;
      }
      else if(units == Units.ENGLISH){
         currentData = this._english;
      }
      else if(units == Units.ABSOLUTE){
         currentData = this._absolute;
      }
      else if(units == Units.PERCENTAGE){
         currentData = this._percent;
      }
      else{
         currentData = this._percent;
      }
      return currentData;
   }
   
   /*
   */
   public double dewpointDataAbsolute(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.DEWPOINT){
         data = this._absolute;
      }
      return data;
   }
   
   /*
   */
   public double dewpointDataEnglish(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.DEWPOINT){
         data = this._english;
      }
      return data;
   }
   
   /*
   */
   public double dewpointDataMetric(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.DEWPOINT){
         data = this._metric;
      }
      return data;
   }
   
   /*
   */
   public double heatIndexDataAbsolute(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.HEATINDEX){
         data = this._absolute;
      }
      return data;
   }
   
   /*
   */
   public double heatIndexDataEnglish(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.HEATINDEX){
         data = this._english;
      }
      return data;
   }
   
   /*
   */
   public double heatIndexDataMetric(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.HEATINDEX){
         data = this._metric;
      }
      return data;
   }
   
   /*
   */
   public double pressureDataAbsolute(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.PRESSURE){
         data = this._absolute;
      }
      return data;
   }

   /*
   */
   public double pressureDataEnglish(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.PRESSURE){
         data = this._english;
      }
      return data;
   }

   /*
   */
   public double pressureDataMetric(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.PRESSURE){
         data = this._metric;
      }
      return data;
   }

   /**
   */
   public double humidityDataPercentage(){
      double data = DEFAULTHUMIDITY;
      if(this.weatherType() == WeatherDataType.HUMIDITY){
         data = this._percent;
      }
      return data;
   }

   /**
   */
   public String message(){
      return this._message;
   }

   /**
   */
   public double temperatureDataMetric(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.TEMPERATURE){
         data = this._metric;
      }
      return data;
   }

   /**
   */
   public double temperatureDataEnglish(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.TEMPERATURE){
         data = this._english;
      }
      return data;
   }

   /**
   */
   public double temperatureDataAbsolute(){
      double data = DEFAULTMEASURE;
      if(this.weatherType() == WeatherDataType.TEMPERATURE){
         data = this._absolute;
      }
      return data;
   }

   /**
   */
   public String toString(){
      String returnString = new String();

      returnString = returnString.concat(this.weatherType() + "\n");
      if(this.weatherType() == WeatherDataType.HUMIDITY){
         returnString = returnString.concat(this._percent + "%\n");
      }
      else{
         returnString =
                  returnString.concat(this._metric+"\u00B0"+"C\n");
         returnString =
                 returnString.concat(this._english+"\u00B0"+"F\n");
         returnString = returnString.concat(this._absolute+"K\n");
      }
      returnString = returnString.concat(this.message());

      return returnString;
   }

   /**
   Return the current Storage type
   */
   public WeatherDataType weatherType(){
      return this._type;
   }

   //***********************Private Methods**************************
   /**
   */
   private void barometricPressure(Units units, double data){
      if(data > DEFAULTMEASURE){
         if(units == Units.METRIC){
            this._metric = data;
            this._english = WeatherConvert.millimetersToInches(data);
            this._absolute =
                     WeatherConvert.inchesToMillibars(this._english);
         }
         else if(units == Units.ENGLISH){
            this._english = data;
            this._metric = WeatherConvert.inchesToMillimeters(data);
            this._absolute = WeatherConvert.inchesToMillibars(data);
         }
         else if(units == Units.ABSOLUTE){
            this._absolute = data;
            this._english = WeatherConvert.millibarsToInches(data);
            this._metric=WeatherConvert.millibarsToMillimeters(data);
         }
         else{
            this._metric = data;
            this._english = WeatherConvert.millimetersToInches(data);
            this._absolute =
                     WeatherConvert.inchesToMillibars(this._english);
         }
      }
   }
   
   /**
   */
   private void genericThermalData(Units units, double data){
      if(data > DEFAULTMEASURE){
         if(units == Units.METRIC){
            this._metric = data;
            this._english = WeatherConvert.celsiusToFahrenheit(data);
            this._absolute = WeatherConvert.celsiusToKelvin(data);
         }
         else if(units == Units.ENGLISH){
            this._english = data;
            this._metric  = WeatherConvert.fahrenheitToCelsius(data);
            this._absolute = WeatherConvert.fahrenheitToKelvin(data);
         }
         else if(units == Units.ABSOLUTE){
            this._absolute = data;
            this._metric = WeatherConvert.kelvinToCelsius(data);
            this._english = WeatherConvert.kelvinToFahrenheit(data);
         }
         else{
            this._metric = data;
            this._english = WeatherConvert.celsiusToFahrenheit(data);
            this._absolute = WeatherConvert.celsiusToKelvin(data);
         }
      }      
   }

   /**
   */
   private void humidity(double data){
      if(data >= 0.0 && data <= 100.0){
         this._percent = data;
      }
   }
}
