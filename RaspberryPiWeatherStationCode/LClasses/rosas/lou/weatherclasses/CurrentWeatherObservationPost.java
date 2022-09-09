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

public class CurrentWeatherObservationPost extends
CurrentWeatherDataSubscriber implements WeatherClientDataSubscriber{
   //////////////////////////Constructors/////////////////////////////
   /*
   */
   public CurrentWeatherObservationPost(){}

   ////////////////////////Instance Methods///////////////////////////
   /////////////////////////Public Methods////////////////////////////
   ////////////////////////Protected Methods//////////////////////////
   /////////////////////////Private Methods///////////////////////////
   /*
   */
   private void publish(){
      this.publishTemperature();
      this.publishHumidity();
      this.publishPressure();
      this.publishDewpoint();
      this.publishHeatindex();
   }

   /*
   */
   private void publishDewpoint(){
      System.out.println("\n*************************************\n");
      System.out.println(this.dewpointData);
      System.out.println("\n*************************************\n"); 
   }

   /*
   */
   private void publishHeatindex(){
      System.out.println("\n*************************************\n");
      System.out.println(this.heatIndexData);
      System.out.println("\n*************************************\n"); 
   }

   /*
   */
   private void publishHumidity(){
      System.out.println("\n*************************************\n");
      System.out.println(this.humidityData);
      System.out.println("\n*************************************\n"); 
   }

   /*
   */
   private void publishPressure(){
      System.out.println("\n*************************************\n");
      System.out.println(this.pressureData);
      System.out.println("\n*************************************\n"); 
   }

   /*
   */
   private void publishTemperature(){
      System.out.println("\n*************************************\n");
      System.out.println(this.temperatureData);
      System.out.println("\n*************************************\n"); 
   }

   ///////////////////////Interface Implementation////////////////////
   /**/
   public void updateData(String data){
      this._data = data;
   }

   /**/
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
}
