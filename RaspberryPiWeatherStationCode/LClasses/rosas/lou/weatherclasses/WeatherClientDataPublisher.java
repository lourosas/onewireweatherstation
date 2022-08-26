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
import rosas.lou.weatherclasses.WeatherClientDataSubscriber;

public abstract class WeatherClientDataPublisher{
   protected List<WeatherClientDataSubscriber> _subscribers;
   {
      _subscribers = null;
   };

   //////////////////////Public Methods///////////////////////////////
   /*
   */
   public void addSubscriber(WeatherClientDataSubscriber sub){
      try{
         this._subscribers.add(sub);
      }
      catch(NullPointerException npe){
         this._subscribers =
                        new LinkedList<WeatherClientDataSubscriber>();
         this._subscribers.add(sub);
      }
   }

   
   ///////////////////Protected Methods///////////////////////////////
   /*
   */
   protected void publishData(String data){
      try{
         Iterator<WeatherClientDataSubscriber> it =
                                         this._subscribers.iterator();
         while(it.hasNext()){
            //WeatherClientDataSubscriber sub = it.next();
            //sub.updateData(data);
            it.next().updateData(data);
         }
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /*
   */
   protected void publishData(List<WeatherData> data){
      try{
         Iterator<WeatherClientDataSubscriber> it =
                                         this._subscribers.iterator();
         while(it.hasNext()){
            it.next().updateData(data);
         }
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }
}
