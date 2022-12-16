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

import java.lang.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;
import rosas.lou.weatherclasses.*;

public abstract class DatabaseSubscriber
extends CurrentWeatherDataSubscriber{
   private WeatherData _temperature;
   private WeatherData _humidity;
   private WeatherData _pressure;
   private WeatherData _heatIndex;
   private WeatherData _dewpoint;
   private int _year;
   private int _month;
   private int _date;
   private int _hour;
   private int _min;
   private int _sec;

   {
      _temperature = null;
      _humidity    = null;
      _pressure    = null;
      _heatIndex   = null;
      _dewpoint    = null;
      _year        = 0;
      _month       = 0;
      _date        = 0;
      _hour        = 0;
      _min         = 0;
      _sec         = 0;
   };

   ////////////////////////Public Methods/////////////////////////////
   /*
   Override the updateData(...) method
   */
   public void updateData(String data){
      super.updateData(data);
      //Do this at the Abstract Level, since every Child Class is
      //going to need it.
      this.setWeatherData();
   }

   /*
   */
   public WeatherData dewpoint(){
      return this._dewpoint;
   }

   /*
   */
   public WeatherData heatIndex(){
      return this._heatIndex;
   }

   /*
   */
   public WeatherData humidity(){
      return this._humidity;
   }

   /*
   */
   public WeatherData pressure(){
      return this._pressure;
   }

   /*
   */
   public WeatherData temperature(){
      return this._temperature;
   }

   //////////////////////Protected Methods////////////////////////////
   protected void setWeatherData(){
      this.setTemperatureData();
      this.setHumidityData();
      this.setPressureData();
      this.setDewpointData();
      this.setHeatIndexData();
   }

   //////////////////////Private Methods//////////////////////////////
   /*
   */
   private void parseNeededCalendarValues(String calendar){
      String [] values = calendar.split(" ");
      if(values[1].trim().equals("Jan")){ this._month = 0; }
      else if(values[1].trim().equals("Feb")){ this._month = 1; }
      else if(values[1].trim().equals("Mar")){ this._month = 2; }
      else if(values[1].trim().equals("Apr")){ this._month = 3; }
      else if(values[1].trim().equals("May")){ this._month = 4; }
      else if(values[1].trim().equals("Jun")){ this._month = 5; }
      else if(values[1].trim().equals("Jul")){ this._month = 6; }
      else if(values[1].trim().equals("Aug")){ this._month = 7; }
      else if(values[1].trim().equals("Sep")){ this._month = 8; }
      else if(values[1].trim().equals("Oct")){ this._month = 9; }
      else if(values[1].trim().equals("Nov")){ this._month = 10; }
      else if(values[1].trim().equals("Dec")){ this._month = 11; }
      this._year = Integer.parseInt(values[5].trim());
      this._date = Integer.parseInt(values[2].trim());
      String [] time = values[3].trim().split(":");
      this._hour = Integer.parseInt(time[0].trim());
      this._min  = Integer.parseInt(time[1].trim());
      this._sec  = Integer.parseInt(time[2].trim());
   }

   /*
   */
   private void setDewpointData(){
      double dewpoint = Thermometer.DEFAULTTEMP;
      String dp       = this._wdp.parseDewpointMetric(this._data);
      String message  = this._wdp.parseDewpointMessage(this._data);
      Calendar cal    = Calendar.getInstance();
      cal.set(_year,_month,_date,_hour,_min,_sec);
      try{
         dewpoint = Double.parseDouble(dp);
      }
      catch(NumberFormatException nfe){
         dewpoint = Thermometer.DEFAULTTEMP;
      }
      this._dewpoint =
                  new DewpointData(Units.METRIC,dewpoint,message,cal);
   }

   /*
   */
   private void setHeatIndexData(){
      double heatIndex = Thermometer.DEFAULTTEMP;
      String hi        = this._wdp.parseHeatIndexMetric(this._data);
      String message   = this._wdp.parseHeatIndexMessage(this._data);
      Calendar cal     = Calendar.getInstance();
      cal.set(_year,_month,_date,_hour,_min,_sec);
      try{
         heatIndex = Double.parseDouble(hi);
      }
      catch(NumberFormatException nfe){
         heatIndex = Thermometer.DEFAULTTEMP;
      }
      this._heatIndex =
                new HeatIndexData(Units.METRIC,heatIndex,message,cal);
   }

   /*
   */
   private void setHumidityData(){
      double humidity = Hygrometer.DEFAULTHUMIDITY;
      String hum      = this._wdp.parseHumidity(this._data);
      String message  = this._wdp.parseHumidityMessage(this._data);
      Calendar cal    = Calendar.getInstance();
      cal.set(_year,_month,_date,_hour,_min,_sec);
      try{
         humidity = Double.parseDouble(hum);
      }
      catch(NumberFormatException nfe){
         humidity = Hygrometer.DEFAULTHUMIDITY;
      }
      this._humidity=
              new HumidityData(Units.PERCENTAGE,humidity,message,cal);
   }

   /*
   */
   private void setPressureData(){
      double pressure = Barometer.DEFAULTPRESSURE;
      String pres     = this._wdp.parsePressureMetric(this._data);
      String message  = this._wdp.parsePressureMessage(this._data);
      Calendar cal    = Calendar.getInstance();
      cal.set(_year,_month,_date,_hour,_min,_sec);
      try{
         pressure = Double.parseDouble(pres);
      }
      catch(NumberFormatException nfe){
         pressure = Barometer.DEFAULTPRESSURE;
      }
      this._pressure =
                  new PressureData(Units.METRIC,pressure,message,cal);
   }

   /*
   */
   private void setTemperatureData(){
      double temp = Thermometer.DEFAULTTEMP;
      String calendar = this._wdp.parseCalendar(this._data);
      String temperature=this._wdp.parseTemperatureMetric(this._data);
      String message  = this._wdp.parseTemperatureMessage(this._data);
      this.parseNeededCalendarValues(calendar);
      Calendar cal = Calendar.getInstance();
      //Now, set the date
      cal.set(_year,_month,_date,_hour,_min,_sec);
      try{
         temp = Double.parseDouble(temperature);
      }
      catch(NumberFormatException npe){
         temp = Thermometer.DEFAULTTEMP;
      }
      this._temperature =
                   new TemperatureData(Units.METRIC,temp,message,cal);
   }
}
