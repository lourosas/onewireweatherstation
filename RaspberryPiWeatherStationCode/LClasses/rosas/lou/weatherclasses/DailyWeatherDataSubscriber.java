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

public class DailyWeatherDataSubscriber extends
CurrentWeatherDataSubscriber implements WeatherClientDataSubscriber{
   ///////////////////////Consttuctors////////////////////////////////
   /*
   */
   public DailyWeatherDataSubscriber(){}

   /////////////////////////Interface Implementation//////////////////
   /*
   */
   public void updateData(String data){
      this._data = new String(data);
   }

   /*
   */
   public void updateData(List<WeatherData> data){
      try{
         Iterator<WeatherData> it = data.iterator();
         while(it.hasNext()){
            WeatherData currentData = it.next();
            try{
               this.temperatureData = (TemperatureData)currentData;
            }
            catch(ClassCastException cce){}
            try{
               this.humidityData = (HumidityData)currentData;
            }
            catch(ClassCastException cce){}
            try{
               this.pressureData = (PressureData)currentData;
            }
            catch(ClassCastException cce){}
            try{
               this.dewpointData = (DewpointData)currentData;
            }
            catch(ClassCastException cce){}
            try{
               this.heatIndexData = (HeatIndexData)currentData;
            }
            catch(ClassCastException cce){}
         }
         this.publish();
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   //////////////////////////Instance Methods/////////////////////////
}
