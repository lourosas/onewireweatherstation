/*
Copyright 2019 Lou Rosas

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
//////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class WundergroundSubscriber extends
CurrentWeatherDataSubscriber{

   private List<TemperatureHumidityObserver> _thList;
   private List<BarometerObserver>           _boList;
   private List<CalculatedObserver>          _coList;

   {
      _thList = null;  
      _boList = null;
      _coList = null;
   };

   /*
   */
   public WundergroundSubscriber(){}

   /*
   */
   public WundergroundSubscriber
   (
      TemperatureHumidityObserver thObserver,
      BarometerObserver           bObserver,
      CalculatedObserver          cObserver
   ){
      this.addTemperatureHumidityObserver(thObserver);
      this.addBarometerObserver(bObserver);
      this.addCalculatedObserver(cObserver);
   }

   //////////////////////////Public Methods///////////////////////////
   /*
   */
   public void addBarometerObserver(BarometerObserver bo){
      try{
         this._boList.add(bo);
      }
      catch(NullPointerException npe){
         this._boList = new LinkedList<BarometerObserver>();
         this._boList.add(bo);
      }
   }

   /*
   */
   public void addCalculatedObserver(CalculatedObserver co){
      try{
         this._coList.add(co);
      }
      catch(NullPointerException npe){
         this._coList = new LinkedList<CalculatedObserver>();
         this._coList.add(co);
      }
   }

   /*
   */
   public void addTemperatureHumidityObserver
   (
      TemperatureHumidityObserver tho
   ){
      try{
         this._thList.add(tho);
      }
      catch(NullPointerException npe){
         this._thList = new LinkedList<TemperatureHumidityObserver>();
         this._thList.add(tho);
      }
   }

   /*
   Override the updateData(...) method, which is part of the
   WeatherClientDataSubscriber Interface, and the
   CurrentWeatherDataSubscriber class (which is the Super Class to
   this class).
   */
   public void updateData(List<WeatherData> data){
      super.updateData(data);
      try{
         Iterator<TemperatureHumidityObserver> thoit =
                                             this._thList.iterator();
         while(thoit.hasNext()){
            TemperatureHumidityObserver temp = thoit.next();
            temp.updateTemperature(this.temperatureData);
            temp.updateHumidity(this.humidityData);
         }

         Iterator<BarometerObserver> boit = this._boList.iterator();
         while(boit.hasNext()){
            BarometerObserver temp = boit.next();
            temp.updatePressure(this.pressureData);
         }

         Iterator<CalculatedObserver> coit = this._coList.iterator();
         while(coit.hasNext()){
            CalculatedObserver temp = coit.next();
            temp.updateDewpoint(this.dewpointData);
            temp.updateHeatIndex(this.heatIndexData);
         }
      }
      catch(NullPointerException npe){}
      System.out.println("WundergroundSubscriber");
      System.out.println(this.temperatureData);
      System.out.println(this.humidityData);
      System.out.println(this.pressureData);
      System.out.println(this.dewpointData);
      System.out.println(this.heatIndexData);
   }
}

//////////////////////////////////////////////////////////////////////
