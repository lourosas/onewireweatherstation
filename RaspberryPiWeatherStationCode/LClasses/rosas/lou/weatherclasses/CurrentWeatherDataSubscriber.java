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

public class CurrentWeatherDataSubscriber
implements WeatherClientDataSubscriber{
   private String _data;
   {
      _data = null;
   }
   ///////////////////////Constructors////////////////////////////////
   /*
   */
   public CurrentWeatherDataSubscriber(){}

   ///////////////////Interface Implementations///////////////////////
   /*
   */
   public void updateData(String data){
      this._data = new String(data);
      //Test Print
      System.out.println("WeatherClientSubscriber");
      System.out.println(this._data);
      //TODO--put together a WeatherDataParser, and WeatherClientData
      //to implement how the data is to be "displayed", et al
   }
}
