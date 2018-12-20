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
import rosas.lou.weatherclasses.*;

public class MySQLSubscriber extends DatabaseSubscriber{
   int _hour;
   int _min;
   int _sec;
   {
      _hour = -1;
      _min  = -1;
      _sec  = -1;
   };

   ////////////////////////Constructor////////////////////////////////
   /*
   */
   public MySQLSubscriber(){}

   ///////////////////////Public Methods//////////////////////////////
   /*
      override the updateData(...) method, which is part of the
      WeatherClientDataSubscriber interface.
   */
   public void updateData(String data){
      super.updateData(data);
      System.out.println("MySQLSubscriber");
      System.out.println(this.temperature());
      System.out.println(this.humidity());
      System.out.println(this.pressure());
      System.out.println(this.dewpoint());
      System.out.println(this.heatIndex());
      this.archive();
   }

   ///////////////////////Private Methods/////////////////////////////
   /*
   */
   public void archive(){
      Calendar cal = this.temperature().calendar();
      int hour = cal.get(Calendar.HOUR_OF_DAY);
      int min  = cal.get(Calendar.MINUTE);
      int sec  = cal.get(Calendar.SECOND);
      //Only Archive when the time changes!
      if((this._hour!=hour)||(this._min!=min)||(this._sec!=sec)){
         System.out.println("Archive");
         WeatherDatabase db = MySQLWeatherDatabase.getInstance();
         db.temperature(this.temperature());
         db.humidity(this.humidity());
         db.barometricPressure(this.pressure());
         db.dewpoint(this.dewpoint());
         db.heatIndex(this.heatIndex());
         this._hour = hour;
         this._min  = min;
         this._sec  = sec;
      }
   }
}
