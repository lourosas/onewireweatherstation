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
import java.io.*;
import java.net.*;
import rosas.lou.weatherclasses.*;

public abstract class SimpleServer implements Runnable,
TemperatureHumidityObserver, BarometerObserver, CalculatedObserver{
   protected int    port;
   protected String address;
   protected byte[] addr;

   public int port(){ return this.port; }
   public String address(){ return this.address; }
   public byte[] addressAsByte(){ return this.addr; }
   //////////////////Protected Methods////////////////////////////////
   /*
   */
   protected void setUpAddress(int usePort){
      this.port = usePort;

      try{
         InetAddress host         = InetAddress.getLocalHost();
         InetAddress loopBackHost = InetAddress.getLoopbackAddress();
         this.address = host.getHostAddress();
         //Trim out the White Space
         this.address = this.address.trim();
         //Now, time to get the address in bytes
         this.addr    = host.getAddress();
      }
      catch(UnknownHostException ste){
         ste.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   ////////////////Runnable Interface Implementation//////////////////
   public void run(){}

   /////TemperatureHumidityObserver Interface Implementation//////////
   public void updateTemperature(WeatherData data){}
   public void updateTemperatureMetric(double metric){}
   public void updateTemperatureEnglish(double english){}
   public void updateTemperatureAbsolute(double absolute){}
   public void updateHumidity(WeatherData data){}

   //////////BarometerObserver Interface Implementation////////////////
   public void updatePressure(WeatherEvent event){}
   public void updatePressure(WeatherStorage store){}
   public void updatePressure(WeatherData data){}
   public void updatePressureAbsolute(double absolute){}
   public void updatePressureEnglish(double english){}
   public void updatePressureMetric(double metric){}

   /////////CalculatedObserver Interface Implementation///////////////
   public void updateDewpoint(WeatherEvent event){}
   public void updateDewpoint(WeatherStorage store){}
   public void updateDewpoint(WeatherData data){}
   public void updateDewpointAbsolute(double absolute){}
   public void updateDewpointEnglish(double english){}
   public void updateDewpointMetric(double metric){}
   public void updateHeatIndex(WeatherEvent event){}
   public void updateHeatIndex(WeatherStorage store){}
   public void updateHeatIndex(WeatherData data){}
   public void updateHeatIndexAbsolute(double absolute){}
   public void updateHeatIndexEnglish(double english){}
   public void updateHeatIndexMetric(double metric){}
   public void updateWindChill(WeatherEvent event){}
}
