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
      _addr = new byte[]{(byte)68,(byte)230,(byte)27,(byte)225};
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
   public void grabDewpointData(String month,String day,String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         wl          = this.parseDewpoint(data);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
      }
      finally{
         this._dewpointData = wl;
         this.publishDewpoint(wl);
      }
   }

   /**/
   public void grabHeatIndexData(String month,String day, String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         wl          = this.parseHeatIndex(data);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
      }
      finally{
         this._heatIndexData = wl;
         this.publishHeatIndex(wl);
      }
   }

   /**/
   public void grabHumidityData(String month,String day,String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         wl          = this.parseHumidity(data);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
      }
      finally{
         this._humidityData = wl;
         this.publishHumidity(wl);
      }
   }

   /**/
   public void grabPressureData(String month,String day,String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"absolute");
         wl          = this.parsePressure(data);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
      }
      finally{
         this._pressureData = wl;
         this.publishPressure(wl);
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
         this._temperatureData = wl;
         this.publishTemperature(wl);
      }
   }

   /**/
   public void saveDewpoint(File file, Units units){
      PrintWriter outs = null;
      try{
         String save = this.grabMeasureString(this._dewpointData,
                                              units);
         if(save.length() <= 0){
            String error = "No Dewpoint data to Save";
            throw new NullPointerException(error);
         }
         outs = new PrintWriter(new FileWriter(file));
         outs.print(save);
         outs.close();
      }
      catch(NullPointerException npe){
         this.publishDewpoint(npe);
      }
      catch(IOException ioe){
         //Will need to alert the observers or somthing like that
         this.publishDewpoint(ioe);
      }
   }

   public void saveHeatIndex(File file, Units units){
      PrintWriter outs = null;
      try{
         String save = this.grabMeasureString(this._heatIndexData,
                                              units);
         if(save.length() <= 0){
            String error = "No Heat Index data to Save";
            throw new NullPointerException(error);
         }
         outs = new PrintWriter(new FileWriter(file));
         outs.print(save);
         outs.close();
      }
      catch(NullPointerException npe){
         this.publishHeatIndex(npe);
      }
      catch(IOException ioe){
         //Will need to alert the observers or somthing like that
         this.publishHeatIndex(ioe);
      }
   }

   /**/
   public void saveHumidity(File file){
      PrintWriter outs = null;
      try{
         String save = this.grabMeasureString(this._humidityData,
                                              Units.PERCENTAGE);
         if(save.length() <= 0){
            String error = "No Humdity data to Save";
            throw new NullPointerException(error);
         }
         outs = new PrintWriter(new FileWriter(file));
         outs.print(save);
         outs.close();
      }
      catch(NullPointerException npe){
         this.publishHumidity(npe);
      }
      catch(IOException ioe){
         //Will need to alert the observers or somthing like that
         this.publishHumidity(ioe);
      }
   }

   /**/
   public void savePressure(File file, Units units){
      PrintWriter outs = null;
      try{
         String save = this.grabMeasureString(this._pressureData,
                                              units);
         if(save.length() <= 0){
            String error = "No Pressure Data to Save";
            throw new NullPointerException(error);
         }
         outs = new PrintWriter(new FileWriter(file));
         outs.print(save);
         outs.close();
      }
      catch(NullPointerException npe){
         this.publishPressure(npe);
      }
      catch(IOException ioe){
         //Will need to alert the observers or somthing like that
         this.publishPressure(ioe);
      }
   }

   /**/
   public void saveTemperature(File file, Units units){
      PrintWriter outs = null;
      try{
         String save = this.grabMeasureString(this._temperatureData,
                                              units);
         if(save.length() <= 0){
            String error = "No Temperature Data to Save";
            throw new NullPointerException(error);
         }
         outs = new PrintWriter(new FileWriter(file));
         outs.print(save);
         outs.close();
      }
      catch(NullPointerException npe){
         this.publishTemperature(npe);
      }
      catch(IOException ioe){
         this.publishTemperature(ioe);
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

   /**/
   public void setServerAddress(String address){
      int [] addr = new int[4];
      String [] values = address.split("\\.");
      try{
         for(int i = 0; i < values.length; i++){
            addr[i] = Integer.parseInt(values[i]);
         }
         this._addr = new byte[]{(byte)addr[0],
                                 (byte)addr[1],
                                 (byte)addr[2],
                                 (byte)addr[3]};
      }
      catch(NumberFormatException nfe){}
   }

   /**/
   public void setServerPort(String port){
      try{
         this._port = Integer.parseInt(port.trim());
      }
      catch(NumberFormatException nfe){}
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
   private String grabMeasureString
   (
      List<WeatherData> data,
      Units units
   ){
      String value = null;
      try{
         Iterator<WeatherData> it = data.iterator();
         value = new String();
         while(it.hasNext()){
            WeatherData wd = it.next();
            value = value.concat(wd.month() + " " + wd.day() + " ");
            value = value.concat(wd.year() + " " +wd.time() + " ");
            if(units == Units.ABSOLUTE){
               value = value.concat(wd.toStringAbsolute());
            }
            else if(units == Units.ENGLISH){
               value = value.concat(wd.toStringEnglish());
            }
            else if(units == Units.METRIC){
               value = value.concat(wd.toStringMetric());
            }
            else if(units == Units.PERCENTAGE){
               value = value.concat(wd.toStringPercentage());
            }
            value = value.concat("\n");
         }
      }
      catch(NullPointerException npe){}
      return value;
   }

   /**/
   private List<WeatherData> parseDewpoint(String data){
      String [] arrayData      = data.split("],");
      String time              = null;
      String dewpoint          = null;
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; i += 2){
         double dp      = WeatherData.DEFAULTVALUE;
         WeatherData dd = null;
         if(arrayData[i].contains("[")){
            String [] timeArray = arrayData[i].substring(2,
                                    arrayData[i].length()).split(",");
            time = new String(timeArray[0].trim() + ":");
            time = time.concat(timeArray[1].trim() + ":");
            time = time.concat(timeArray[2].trim());
            dewpoint = arrayData[i+1].trim().split(",")[3];
            dewpoint = dewpoint.substring(1,dewpoint.length()-1);
            try{
               dp = Double.parseDouble(dewpoint);
            }
            catch(NumberFormatException nfe){
               dp = WeatherData.DEFAULTVALUE;
            }
            catch(NullPointerException npe){
               dp = WeatherData.DEFAULTVALUE;
            }
            finally{
               dd = new DewpointData(Units.METRIC, dp,
                                     "Dewpoint", this._cal[0],
                                     this._cal[1],this._cal[2],
                                     time);
            }
            try{
               wdList.add(dd);
            }
            catch(NullPointerException npe){
               wdList = new LinkedList<WeatherData>();
               wdList.add(dd);
            }
         }
      }
      return wdList;
   }

   /**/
   private List<WeatherData> parseHeatIndex(String data){
      String [] arrayData      = data.split("],");
      String time              = null;
      String heatIndex         = null;
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; i += 2){
         double hi      = WeatherData.DEFAULTVALUE;
         WeatherData hd = null;
         if(arrayData[i].contains("[")){
            String [] timeArray = arrayData[i].substring(2,
                                    arrayData[i].length()).split(",");
            time = new String(timeArray[0].trim() + ":");
            time = time.concat(timeArray[1].trim() + ":");
            time = time.concat(timeArray[2].trim());
            heatIndex = arrayData[i+1].trim().split(",")[4];
            heatIndex = heatIndex.substring(1,heatIndex.length()-1);
            try{
               hi = Double.parseDouble(heatIndex);
            }
            catch(NumberFormatException nfe){
               hi = WeatherData.DEFAULTVALUE;
            }
            catch(NullPointerException npe){
               hi = WeatherData.DEFAULTVALUE;
            }
            finally{
               hd = new HeatIndexData(Units.METRIC, hi,
                                     "HeatIndex", this._cal[0],
                                     this._cal[1],this._cal[2],
                                     time);
            }
            try{
               wdList.add(hd);
            }
            catch(NullPointerException npe){
               wdList = new LinkedList<WeatherData>();
               wdList.add(hd);
            }
         }
      }
      return wdList;
   }

   /**/
   private List<WeatherData> parseHumidity(String data){
      String [] arrayData      = data.split("],");
      String time              = null;
      String humidity          = null;
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; i += 2){
         double hum     = WeatherData.DEFAULTVALUE;
         WeatherData hd = null;
         if(arrayData[i].contains("[")){
            String [] timeArray = arrayData[i].substring(2,
                                    arrayData[i].length()).split(",");
            time = new String(timeArray[0].trim() + ":");
            time = time.concat(timeArray[1].trim() + ":");
            time = time.concat(timeArray[2].trim());
            humidity = arrayData[i+1].trim().split(",")[1];
            humidity = humidity.substring(1,humidity.length()-1);
            try{
               hum = Double.parseDouble(humidity);
            }
            catch(NumberFormatException nfe){
               hum = WeatherData.DEFAULTHUMIDITY;
            }
            catch(NullPointerException npe){
               hum = WeatherData.DEFAULTHUMIDITY;
            }
            finally{
               hd = new HumidityData(Units.PERCENTAGE, hum,
                                     "Humidity", this._cal[0],
                                     this._cal[1],this._cal[2],
                                     time);
            }
            try{
               wdList.add(hd);
            }
            catch(NullPointerException npe){
               wdList = new LinkedList<WeatherData>();
               wdList.add(hd);
            }
         }
      }
      return wdList;
   }

   private List<WeatherData> parsePressure(String data){
      String [] arrayData      = data.split("],");
      String time              = null;
      String pressure          = null;
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; i += 2){
         double press   = WeatherData.DEFAULTVALUE;
         WeatherData pd = null;
         if(arrayData[i].contains("[")){
            String [] timeArray = arrayData[i].substring(2,
                                    arrayData[i].length()).split(",");
            time = new String(timeArray[0].trim() + ":");
            time = time.concat(timeArray[1].trim() + ":");
            time = time.concat(timeArray[2].trim());
            pressure = arrayData[i+1].trim().split(",")[2];
            pressure = pressure.substring(1,pressure.length()-1);
            try{
               press = Double.parseDouble(pressure);
            }
            catch(NumberFormatException nfe){
               press = WeatherData.DEFAULTVALUE;
            }
            catch(NullPointerException npe){
               press = WeatherData.DEFAULTVALUE;
            }
            finally{
               pd = new PressureData(Units.ABSOLUTE, press,
                                     "Pressure", this._cal[0],
                                     this._cal[1],this._cal[2],
                                     time);
            }
            try{
               wdList.add(pd);
            }
            catch(NullPointerException npe){
               wdList = new LinkedList<WeatherData>();
               wdList.add(pd);
            }
         }
      }
      return wdList;
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
   private void publishDewpoint(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updateDewpointData(list);
            }
            else{
               (it.next()).alertNoDewpointData();
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoDewpointData();
         }
      }
   }

   /**/
   private void publishDewpoint(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         observer.alertNoDewpointData();
         observer.alertNoDewpointData(e);
      }
   }

   /**/
   private void publishHeatIndex(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updateHeatIndexData(list);
            }
            else{
               (it.next()).alertNoHeatIndexData();
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoHeatIndexData();
         }
      }
   }

   /**/
   private void publishHeatIndex(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         observer.alertNoHeatIndexData();
         observer.alertNoHeatIndexData(e);
      }
   }

   /**/
   private void publishHumidity(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updateHumidityData(list);
            }
            else{
               (it.next()).alertNoHumidityData();
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoHumidityData();
         }
      }
   }

   /**/
   private void publishHumidity(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         observer.alertNoHumidityData();
         observer.alertNoHumidityData(e);
      }
   }

   /**/
   private void publishPressure(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         observer.alertNoPressureData();
         observer.alertNoPressureData(e);
      }
   }

   /**/
   private void publishPressure(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updatePressureData(list);
            }
            else{
               (it.next()).alertNoPressureData();
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoPressureData();
         }
      }
   }

   /**/
   private void publishTemperature(Exception re){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         observer.alertNoTemperatureData();
         observer.alertNoTemperatureData(re);
      }
   }

   /**/
   private void publishTemperature(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updateTemperatureData(list);
            }
            else{
               (it.next()).alertNoTemperatureData();
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoTemperatureData();
         }
      }
   }
}
