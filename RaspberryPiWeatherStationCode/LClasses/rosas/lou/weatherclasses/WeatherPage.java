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
      _addr = new byte[]{(byte)68,(byte)110,(byte)91,(byte)225};
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
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         wl          = this.parseTemperature(data);
         //this.publishTemperature(wl);
      }
      catch(NullPointerException npe){
         //npe.printStackTrace();
         //Need to figure out a better way to aleart the view!!!
         //Let the View figure out what to do!!
         wl = new LinkedList<WeatherData>();
      }
      finally{
         this.publishTemperature(wl);
      }
   }

   public void setCalendar(String month, String day, String year){
      if(month != null && month != ""){
         this._cal[0] = month;
      }
      if(day != null && day != ""){
         this._cal[1] = day;
      }
      if(year != null && year != ""){
         this._cal[2] = year;
      }
   }

   ////////////////////////Private Methods////////////////////////////
   /**/
   private String connectAndGrab
   (
      String month,
      String day,
      String year,
      String units
   ){
      int first = new Byte(this._addr[0]).intValue();
      first = first < 0 ? first + 256 : first;
      int sec   = new Byte(this._addr[1]).intValue();
      sec = sec < 0 ? sec + 256 : sec;
      int third = new Byte(this._addr[2]).intValue();
      third = third < 0 ? third + 256 : third;
      int fourth= new Byte(this._addr[3]).intValue();
      fourth=fourth < 0 ? fourth + 256 : fourth;
      String returnLine = null;
      StringBuffer send = new StringBuffer("http://");
      String addr = new String(""+first+"."+sec+"."+third+"."+fourth);
      addr = addr.concat(":"+this._port);
      send.append(addr+"/daily?month="+month+"&date="+day);
      send.append("&year="+year+"&units="+units);
      try{
         URL url = new URL(send.toString().trim());
         URLConnection conn = url.openConnection();
         conn.connect();
         BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
         String line = null;
         while((line = in.readLine()) != null){
            if(line.contains("theData.addRows")){
               while(!(line = in.readLine()).contains("]);")){
                  if(line.length() > 0){
                     if(returnLine == null){
                        returnLine = new String();
                     }
                     returnLine = returnLine.concat(line);
                  }
               }
            }
         }
      }
      catch(MalformedURLException mle){ mle.printStackTrace();}
      catch(IOException ioe){ioe.printStackTrace();}
      return returnLine;
   }

   /**/
   private List<WeatherData> parseTemperature(String data){
      String [] arrayData      = data.split("],");
      String time              = null;
      String temp              = null;
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; i += 2){
         double tempd   = WeatherData.DEFAULTVALUE;
         WeatherData td = null;
         if(arrayData[i].contains("[")){
            String [] timeArray = arrayData[i].substring(2,
                                    arrayData[i].length()).split(",");
            time = new String(timeArray[0].trim() + ":");
            time = time.concat(timeArray[1].trim() + ":");
            time = time.concat(timeArray[2].trim());
            temp = arrayData[i + 1].trim().split(",")[0];
            temp = temp.substring(1,temp.length()-1);
            try{
               tempd = Double.parseDouble(temp);
            }
            catch(NumberFormatException nfe){
               tempd = WeatherData.DEFAULTVALUE;
            }
            catch(NullPointerException npe){
               tempd = WeatherData.DEFAULTVALUE;
            }
            finally{
               td = new TemperatureData(Units.METRIC, tempd,
                                        "Temperature", this._cal[0],
                                        this._cal[1], this._cal[2],
                                        time);
            }
            try{
               wdList.add(td);
            }
            catch(NullPointerException npe){
               wdList = new LinkedList<WeatherData>();
               wdList.add(td);
            }
         }
      }
      return wdList;
   }

   /**/
   private void publishTemperature(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         if(list.size() > 0){
            (it.next()).updateTemperatureData(list);
         }
         else{
            (it.next()).alertNoTemperatureData();
         }
      }
   }
}
