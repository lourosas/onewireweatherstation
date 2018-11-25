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

public class WeatherDataParser{
   private static final String [] MEASUREMENTS = {"TEMPERATURE",
                                                  "HUMIDITY",
                                                  "PRESSURE",
                                                  "DEWPOINT",
                                                  "HEATINDEX"};

   ////////////////////////Constructors///////////////////////////////
   public WeatherDataParser(){}

   ///////////////////////Public Methods//////////////////////////////
   /*
   */
   public String parseRawTemperature(String data){
      return this.parseRaw(data, "TEMPERATURE");
   }

   /*
   */
   public String parseRawHumidity(String data){
      return this.parseRaw(data, "HUMIDITY");
   }

   /*
   */
   public String parseRawPressure(String data){
      return this.parseRaw(data, "PRESSURE");
   }

   /*
   */
   public String parseRawDewpoint(String data){
      return this.parseRaw(data, "DEWPOINT");
   }

   /*
   */
   public String parseRawHeatIndex(String data){
      return this.parseRaw(data, "HEATINDEX");
   }

   ///////////////////////Private Methods/////////////////////////////

   /*
   */
   private String parseRaw(String data, String key){
      String returnString = null;
      boolean found       = false;
      int i = 0;
      String [] strings = data.split("\n");
      do{
         String compare = strings[i];
         compare = compare.toUpperCase().trim();
         if(compare.contains(key)){
            found        = true;
            returnString = strings[i];
         }
      }while((++i < MEASUREMENTS.length) && !found);
      return returnString;
   }
}
