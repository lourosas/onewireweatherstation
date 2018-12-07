//////////////////////////////////////////////////////////////////////
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
//////////////////////////////////////////////////////////////////////

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class CurrentWeatherDataParser extends WeatherDataParser{
   /////////////////////Constructors/////////////////////////////////
   /*
   */
   public CurrentWeatherDataParser(){
      //WeatherClientData<String> data = new WeatherClientString<>();
      //this._data = data;
   }

   /*
   */
   public void parseRaw(String data){
      super.parseRaw(data);
   }

   /////////////////////Public Methods///////////////////////////////
   /////////////////////Protected Methods////////////////////////////
   //////////////////////Private Methods/////////////////////////////
   /*
   */
   private void parseTemperatureAbsolute(String data){
   }

   /*
   */
   private void parseTemperatureEnglish(String data){
   }

   /*
   */
   private void parseTemperatureMetric(String data){
   }

   /*
   */
   private void parseHumidity(String data){
   }

   /*
   */
   private void parsePressureAbsolute(String data){
   }

   /*
   */
   private void parsePressureEnglish(String data){
   }

   /*
   */
   private void parsePressureMetric(String data){
   }

   /*
   */
   private void parseDewpointAbsolute(String data){
   }

   /*
   */
   private void parseDewpointEnglish(String data){
   }

   /*
   */
   private void parseDewpointMetric(String data){
   }

   /*
   */
   private void parseHeatIndexAbsolute(String data){
   }

   /*
   */
   private void parseHeatIndexEnglish(String data){
   }

   /*
   */
   private void parseHeatIndexMetric(String data){
   }
}
