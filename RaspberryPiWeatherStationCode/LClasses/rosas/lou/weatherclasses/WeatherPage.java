/*
Copyright 2020 Lou Rosas

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
import java.text.DateFormat;
import java.text.ParseException;
import rosas.lou.weatherclasses.*;
import com.sun.net.httpserver.*;

public class WeatherPage{
   private static final int PORT    = 8000;
   private static final int TIMEOUT = 20000;

   private byte[]            _addr;
   private int               _port;
   private String[]          _cal;
   private String            _rawData;
   private List<WeatherData> _temperatureData;
   private List<WeatherData> _humidityData;
   private List<WeatherData> _pressureData;
   private List<WeatherData> _dewpointData;
   private List<WeatherData> _heatIndexData;

   private List<WeatherDatabaseClientObserver> _observers;

   {
      _addr = new byte[]{(byte)68, (byte)110, (byte)91, (byte)225};
      _port            = PORT;
      _cal             = new String[]{"January", "01", "2017"};
      _rawData         = null;
      _temperatureData = null;
      _humidityData    = null;
      _pressureData    = null;
      _dewpointData    = null;
      _heatIndexData   = null;
      _observers       = null;
   };

   /////////////////////////Constructors//////////////////////////////
   /*
   */
   public WeatherPage(){}

   /////////////////////////Public Methods////////////////////////////
   /**/
   public void addObserver(WeatherDatabaseClientObserver observer){
      try{
         this._observers.add(observer);
      }
      catch(NullPointerException npe){
         this._observers =
                      new LinkedList<WeatherDatabaseClientObserver>();
         this._observers.add(observer);
      }
   }

   /**/
   public void grabTemperatureData(String month,String day,String year){
      String data = this.connectAndGrab(month,day,year);
      this.parseTemperature(data);
   }

   ////////////////////////Private Methods////////////////////////////
   /**/
   private String connectAndGrab(String month,String day,String year){
      StringBuffer send = new StringBuffer("http://");
      String addr = new String(""+this._addr[0]+"."+this._addr[1]);
      addr = addr.concat("."+this._addr[2]+"."+this._addr[3]);
      addr = addr.concat(":"+this._port);
      send.append(addr);
      System.out.println(send);
      return send.toString(); //TODO-->FIX!!!
   }

   /**/
   private void parseTemperature(String data){}
}
