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
import rosas.lou.weatherclasses.*;

public class CurrentWeatherDataSubscriber
implements WeatherClientDataSubscriber, Runnable{
   protected String            _data;
   protected int               _sleepTime;
   protected WeatherDataParser _wdp;
   protected WeatherData       temperatureData;
   protected WeatherData       humidityData;
   protected WeatherData       pressureData;
   protected WeatherData       dewpointData;
   protected WeatherData       heatIndexData;

   {
      _data      = null;
      _wdp       = null;
      _sleepTime = -1;
   };
   ///////////////////////Constructors////////////////////////////////
   /*
   */
   public CurrentWeatherDataSubscriber(){
      this._wdp = new WeatherDataParser();
   }

   ///////////////////Interface Implementations///////////////////////
   /*
   */
   public void run(){}

   /*
   */
   public void updateData(String data){
      this._data = new String(data);
      //Test Print
      System.out.println("WeatherClientSubscriber");
      System.out.println(this._wdp.parseCalendar(this._data));
      System.out.println(this._wdp.parseTemperatureAbsolute(this._data));
      System.out.println(this._wdp.parseTemperatureEnglish(this._data));
      System.out.println(this._wdp.parseTemperatureMetric(this._data));
      System.out.println(this._wdp.parseHumidity(this._data));
      System.out.println(this._wdp.parsePressureAbsolute(this._data));
      System.out.println(this._wdp.parsePressureEnglish(this._data));
      System.out.println(this._wdp.parsePressureMetric(this._data));
      System.out.println(this._wdp.parseDewpointAbsolute(this._data));
      System.out.println(this._wdp.parseDewpointEnglish(this._data));
      System.out.println(this._wdp.parseDewpointMetric(this._data));
      System.out.println(this._wdp.parseHeatIndexAbsolute(this._data));
      System.out.println(this._wdp.parseHeatIndexEnglish(this._data));
      System.out.println(this._wdp.parseHeatIndexMetric(this._data));
   }

   /*
   I am sure there is a better way to do this than to check the type!
   */
   public void updateData(List<WeatherData> data){
      System.out.println("More WeatherClientSubscriber");
      Iterator<WeatherData> it = data.iterator();
      while(it.hasNext()){
         WeatherData currentData = it.next();
         try{
            TemperatureData td = (TemperatureData)currentData;
            this.temperatureData = td;
         }
         catch(ClassCastException cce){}
         try{
            HumidityData hd = (HumidityData)currentData;
            this.humidityData = hd;
         }
         catch(ClassCastException cce){}
         try{
            PressureData pd = (PressureData)currentData;
            this.pressureData = pd;
         }
         catch(ClassCastException cce){}
         try{
            DewpointData dd = (DewpointData)currentData;
            this.dewpointData = dd;
         }
         catch(ClassCastException cce){}
         try{
            HeatIndexData hid = (HeatIndexData)currentData;
            this.heatIndexData = hid;
         }
         catch(ClassCastException cce){}
      }
   }

   /**/
   public void addPublisher(WeatherClientDataPublisher publisher){}


   ///////////////////////Instance Methods////////////////////////////
   ////////////////////////Public Methods/////////////////////////////
   /**/
   public void changeSleepTime(){}

   public boolean confirmSuperUserCredentials
   (
      String name,
      char[] password
   ){
      return false;
   }

   /**/
   public void requestUpdateFromPublisher(){}

   /**/
   public void setSleepTime(int seconds){}

   /**/
   public void setSleepTime(int hours,int minutes,int seconds){}

   /*
   */
   public double temperature(Units units){
      return Thermometer.DEFAULTTEMP;
   }

   public String temperatureString(Units units){
      return new String("Poop");
   }

   ////////////////////////Protected Methods//////////////////////////
}
