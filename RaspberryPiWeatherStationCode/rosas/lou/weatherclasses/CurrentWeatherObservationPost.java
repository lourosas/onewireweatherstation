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
import java.io.*;
import rosas.lou.weatherclasses.*;

public class CurrentWeatherObservationPost extends
CurrentWeatherDataSubscriber 
implements WeatherClientDataSubscriber, Runnable{
   private List<CurrentWeatherDataObserver> _observers = null;
   private WeatherClientDataPublisher wcdp             = null;

   //////////////////////////Constructors/////////////////////////////
   /*
   */
   public CurrentWeatherObservationPost(){}

   ////////////////////////Instance Methods///////////////////////////
   /////////////////////////Public Methods////////////////////////////
   /*
   */
   public void addObserver(CurrentWeatherDataObserver ob){
      try{
         this._observers.add(ob);
      }
      catch(NullPointerException npe){
         this._observers =
                         new LinkedList<CurrentWeatherDataObserver>();
         this._observers.add(ob);
      }
   }

   /**/
   public void requestUpdateFromPublisher(){
      this.wcdp.request();
   }

   /**/
   public void save(File file){
      PrintWriter outs = null;
      try{
         FileWriter fw = new FileWriter(file,true);
         outs          = new PrintWriter(fw,true);
         outs.println(this.temperatureData);
         outs.println(this.humidityData);
         outs.println(this.pressureData);
         outs.println(this.dewpointData);
         outs.println(this.heatIndexData);
      }
      catch(IOException ioe){
         this.publishError(ioe.getMessage());
      }
      finally{
         outs.close();
      }
   }

   /**/
   public void save(String fileName){}

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
   private void publishError(String error){
      Iterator<CurrentWeatherDataObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         it.next().receiveError(error);
      }
   }

   /*
   */
   private void publishDewpoint(){
      Iterator<CurrentWeatherDataObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         it.next().updateDewpoint(this.dewpointData);
      }
   }

   /*
   */
   private void publishHeatindex(){
      Iterator<CurrentWeatherDataObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         it.next().updateHeatindex(this.heatIndexData);
      }
   }

   /*
   */
   private void publishHumidity(){
      Iterator<CurrentWeatherDataObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         it.next().updateHumidity(this.humidityData);
      }
   }

   /*
   */
   private void publishPressure(){
      Iterator<CurrentWeatherDataObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         it.next().updatePressure(this.pressureData);
      }
   }

   /*
   */
   private void publishTemperature(){
      Iterator<CurrentWeatherDataObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         it.next().updateTemperature(this.temperatureData);
      }
   }

   ///////////////////////Interface Implementation////////////////////
   /**/
   public void run(){}

   /**/
   public void updateData(String data){
      this._data = data;
      String[] test = this._data.split("\\n");
      try{
         this.temperatureData =
                RawDataToWeatherDataConverter.temperature(this._data);
         this.humidityData =
                   RawDataToWeatherDataConverter.humidity(this._data);
         this.pressureData =
          RawDataToWeatherDataConverter.barometricPressure(this._data);
         this.dewpointData =
                   RawDataToWeatherDataConverter.dewpoint(this._data);
         this.heatIndexData =
                  RawDataToWeatherDataConverter.heatindex(this._data);
         this.publish();
      }
      catch(RuntimeException e){
         this.temperatureData =
            WeatherDataStringToWeatherDataConverter.temperature(
                                                          this._data);
         this.humidityData =
            WeatherDataStringToWeatherDataConverter.humidity(
                                                          this._data);
         this.pressureData =
            WeatherDataStringToWeatherDataConverter.pressure(
                                                          this._data);
         this.dewpointData =
            WeatherDataStringToWeatherDataConverter.dewpoint(                 
                                                          this._data);
         this.heatIndexData =
            WeatherDataStringToWeatherDataConverter.heatindex(
                                                          this._data);
         this.publish();
      }
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

   /**/
   public void addPublisher(WeatherClientDataPublisher publisher){
      wcdp = publisher;
   }
}
