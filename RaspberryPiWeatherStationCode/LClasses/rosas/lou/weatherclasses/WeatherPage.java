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

   //private byte[]            _addr;
   //private int               _port;
   private String            _addr;
   private String[]          _cal;
   private String            _rawData;
   private List<WeatherData> _temperatureData;
   private List<WeatherData> _temperatureMinMaxAvg;
   private List<WeatherData> _humidityData;
   private List<WeatherData> _humidityMinMaxAvg;
   private List<WeatherData> _pressureData;
   private List<WeatherData> _dewpointData;
   private List<WeatherData> _dewpointMinMaxAvg;
   private List<WeatherData> _heatIndexData;

   private List<WeatherDatabaseClientObserver> _observers;

   {
      //_addr = new byte[]{(byte)68,(byte)230,(byte)27,(byte)225};
      //_port            = PORT;
      _addr            = new String("68.230.27.225:8500");
      _cal             = new String[]{"January", "01", "2017"};
      _rawData         = null;
      _temperatureData = null;
      _temperatureMinMaxAvg = null;
      _humidityData    = null;
      _humidityMinMaxAvg = null;
      _pressureData    = null;
      _dewpointData    = null;
      _dewpointMinMaxAvg = null;
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
      this.publishAddress(this._addr);
   }

   /**/
   public void grabDewpointData(String month,String day,String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         data        = this.parseTheData(data);
         wl          = this.parseDewpoint(data);
         this.publishDewpoint(wl);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
         this.publishDewpoint(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
         this.publishDewpoint(e);
      }
      finally{
         this._dewpointData = wl;
      }
   }

   /**/
   public void grabDewpointMinMaxAvg(String mo,String dy,String yr){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(mo,dy,yr);
         String data = this.connectAndGrab(mo,dy,yr,"metric");
         data        = this.parseTheMinMaxAvg(data);
         wl          = this.parseDewpointMinMaxAvg(data);
         this.publishMinMaxAvgDewpoint(wl);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
         this.publishMinMaxAvgDewpoint(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
         this.publishMinMaxAvgDewpoint(e);
      }
      finally{
         this._dewpointMinMaxAvg = wl;
      }
   }

   /**/
   public void grabHeatIndexData(String month,String day, String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         data        = this.parseTheData(data);
         wl          = this.parseHeatIndex(data);
         this.publishHeatIndex(wl);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
         this.publishHeatIndex(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
         this.publishHeatIndex(e);
      }
      finally{
         this._heatIndexData = wl;
      }
   }

   /**/
   public void grabHumidityData(String month,String day,String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         data        = this.parseTheData(data);
         wl          = this.parseHumidity(data);
         this.publishHumidity(wl);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
         this.publishHumidity(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
         this.publishHumidity(e);
      }
      finally{
         this._humidityData = wl;
      }
   }

   /**/
   public void grabHumidityMinMaxAvg(String mo,String dy,String yr){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(mo,dy,yr);
         String data = this.connectAndGrab(mo,dy,yr,"metric");
         data        = this.parseTheMinMaxAvg(data);
         wl          = this.parseHumidityMinMaxAvg(data);
         this.publishMinMaxAvgHumidity(wl);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
         this.publishMinMaxAvgHumidity(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
         this.publishMinMaxAvgHumidity(e);
      }
      finally{
         this._humidityMinMaxAvg = wl;
      }
   }

   /**/
   public void grabPressureData(String month,String day,String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"absolute");
         data        = this.parseTheData(data);
         wl          = this.parsePressure(data);
         this.publishPressure(wl);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
         this.publishPressure(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
         this.publishPressure(e);
      }
      finally{
         this._pressureData = wl;
      }
   }

   /**/
   public void grabTemperatureData(String month,String day,String year){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(month,day,year);
         String data = this.connectAndGrab(month,day,year,"metric");
         data        = this.parseTheData(data);
         wl          = this.parseTemperature(data);
         this.publishTemperature(wl);
      }
      catch(NullPointerException npe){
         //npe.printStackTrace();
         //Need to figure out a better way to aleart the view!!!
         //Let the View figure out what to do!!
         wl = new LinkedList<WeatherData>();
	      this.publishTemperature(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
	      this.publishTemperature(e);
      }
      finally{
         this._temperatureData = wl;
      }
   }

   /**/
   public void grabTemperatureMinMaxAvg(String mo,String dy,String yr){
      List<WeatherData> wl = null;
      try{
         this.setCalendar(mo,dy,yr);
         String data = this.connectAndGrab(mo,dy,yr,"metric");
         data        = this.parseTheMinMaxAvg(data);
         wl          = this.parseTempMinMaxAvg(data);
         this.publishMinMaxAvgTemp(wl);
      }
      catch(NullPointerException npe){
         wl = new LinkedList<WeatherData>();
         this.publishMinMaxAvgTemp(npe);
      }
      catch(Exception e){
         wl = new LinkedList<WeatherData>();
         this.publishMinMaxAvgTemp(e);
      }
      finally{
         this._temperatureMinMaxAvg = wl;
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
      this._addr = new String(address);
      this.publishAddress(this._addr);
   }

   ////////////////////////Private Methods////////////////////////////
   /**/
   private String connectAndGrab
   (
      String month,
      String day,
      String year,
      String units
   ) throws Exception{
      String returnLine = null;
      StringBuffer send = new StringBuffer("http://");
      send.append(this._addr+"/daily?month="+month+"&date="+day);
      send.append("&year="+year+"&units="+units);
      try{
         URL url = new URL(send.toString().trim());
         URLConnection conn = url.openConnection();
         conn.setConnectTimeout(TIMEOUT);
         conn.connect();
         BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
         String line = null;
         while((line = in.readLine()) != null){
            if(returnLine == null){
               returnLine = new String();
            }
            returnLine = returnLine.concat(line);
         }
	      return returnLine;
      }
      catch(SocketTimeoutException ste){
         ste.printStackTrace();
         throw new SocketTimeoutException(ste.getMessage());
      }
      catch(MalformedURLException mle){
         mle.printStackTrace();
         throw new MalformedURLException(mle.getMessage());
      }
      catch(IOException ioe){
         ioe.printStackTrace();
         throw new IOException(ioe.getMessage());
      }
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
   private List<WeatherData> parseDewpointMinMaxAvg(String data){
      String [] arrayData = data.split("],");
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; ++i){
         if(arrayData[i].contains("Dew Point")){
            String [] dpArray = arrayData[i].split(",");
            double min = WeatherData.DEFAULTVALUE;
            double max = WeatherData.DEFAULTVALUE;
            double avg = WeatherData.DEFAULTVALUE;
            String dMeas = dpArray[1].trim();
            dMeas = dMeas.substring(1,dMeas.length()-1);
            try{
               min = Double.parseDouble(dMeas);
            }
            catch(NumberFormatException nfe){
               min = WeatherData.DEFAULTVALUE;
            }
            dMeas = dpArray[2].trim();
            dMeas = dMeas.substring(1,dMeas.length()-1);
            try{
               max = Double.parseDouble(dMeas);
            }
            catch(NumberFormatException nfe){
               max = WeatherData.DEFAULTVALUE;
            }
            dMeas = dpArray[3].trim();
            dMeas = dMeas.substring(1,dMeas.length()-1);
            try{
               avg = Double.parseDouble(dMeas);
            }
            catch(NumberFormatException nfe){
               avg = WeatherData.DEFAULTVALUE;
            }
            catch(NullPointerException npe){}
            finally{
               wdList = new LinkedList<WeatherData>();
               wdList.add(new DewpointData(Units.METRIC, min,
                                           "DEWPOINT MIN"));
               wdList.add(new DewpointData(Units.METRIC, max,
                                          "DEWPOINT MAX"));
               wdList.add(new DewpointData(Units.METRIC, avg,
                                           "DEWPOINT AVG"));
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

   /**/
   private List<WeatherData> parseHumidityMinMaxAvg(String data){
      String [] arrayData = data.split("],");
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; ++i){
         if(arrayData[i].contains("Humidity")){
            String [] humidityArray = arrayData[i].split(",");
            double min = WeatherData.DEFAULTHUMIDITY;
            double max = WeatherData.DEFAULTHUMIDITY;
            double avg = WeatherData.DEFAULTHUMIDITY;
            String hMeas = humidityArray[1].trim();
            hMeas = hMeas.substring(1,hMeas.length()-1);
            try{
               min = Double.parseDouble(hMeas);
            }
            catch(NumberFormatException nfe){
               min = WeatherData.DEFAULTHUMIDITY;
            }
            hMeas = humidityArray[2].trim();
            hMeas = hMeas.substring(1,hMeas.length()-1);
            try{
               max = Double.parseDouble(hMeas);
            }
            catch(NumberFormatException nfe){
               max = WeatherData.DEFAULTHUMIDITY;
            }
            hMeas = humidityArray[3].trim();
            hMeas = hMeas.substring(1,hMeas.length()-1);
            try{
               avg = Double.parseDouble(hMeas);
            }
            catch(NumberFormatException nfe){
               avg = WeatherData.DEFAULTHUMIDITY;
            }
            catch(NullPointerException npe){}
            finally{
               wdList = new LinkedList<WeatherData>();
               wdList.add(new HumidityData(Units.PERCENTAGE, min,
                                           "HUMIDITY MIN"));
               wdList.add(new HumidityData(Units.PERCENTAGE, max,
                                           "HUMIDITY MAX"));
               wdList.add(new HumidityData(Units.PERCENTAGE, avg,
                                           "HUMIDITY AVG"));
            }
         }
      }
      return wdList;
   }

   /**/
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
   private List<WeatherData> parseTempMinMaxAvg(String data){
      String [] arrayData     = data.split("],");
      List<WeatherData> wdList = null;
      for(int i = 0; i < arrayData.length; ++i){
         if(arrayData[i].contains("Temperature")){
            String [] tempArray = arrayData[i].split(",");
            double min = WeatherData.DEFAULTVALUE;
            double max = WeatherData.DEFAULTVALUE;
            double avg = WeatherData.DEFAULTVALUE;
            String sMeas = tempArray[1].trim();
            sMeas = sMeas.substring(1,sMeas.length()-1);
            try{
               min = Double.parseDouble(sMeas);
            }
            catch(NumberFormatException nfe){
               min = WeatherData.DEFAULTVALUE;
            }
            sMeas      = tempArray[2].trim();
            sMeas      = sMeas.substring(1,sMeas.length()-1);
            try{
               max = Double.parseDouble(sMeas);
            }
            catch(NumberFormatException nfe){
               max = WeatherData.DEFAULTVALUE;
            }
            sMeas      = tempArray[3].trim();
            sMeas      = sMeas.substring(1,sMeas.length()-1);
            try{
               avg = Double.parseDouble(sMeas);
            }
            catch(NumberFormatException nfe){
               avg = WeatherData.DEFAULTVALUE;
            }
            catch(NullPointerException npe){}
            finally{
               wdList = new LinkedList<WeatherData>();
               wdList.add(new TemperatureData(Units.METRIC, min,
                                              "TEMPERATURE MIN"));
               wdList.add(new TemperatureData(Units.METRIC, max,
                                              "TEMPERATURE MAX"));
               wdList.add(new TemperatureData(Units.METRIC, avg,
                                              "TEMPERATURE AVG"));
            }
         }
      }
      return wdList;
   }

   /**/
   private String parseTheData(String data){
      String theData = null;
      if(data.contains("theData.addRows")){
         int index = data.indexOf("theData.addRows");
         String temp = data.substring(index);
         String begin = new String("([");
         int beginidx = temp.indexOf(begin);
         String end = new String("]);");
         int endindex = temp.indexOf(end);
         temp = temp.substring(beginidx+begin.length(),endindex);
         theData = temp;
      }
      return theData;
   }

   /**/
   private String parseTheMinMaxAvg(String data){
      String minMaxAvg = null;
      if(data.contains("hldata.addRows")){
         int index    = data.indexOf("hldata.addRows");
         String mma   = data.substring(index);
         String begin = new String("([");
         int beginidx = mma.indexOf(begin);
         String end   = new String("]);");
         int endidx   = mma.indexOf(end);
         mma = mma.substring(beginidx+begin.length(),endidx);
         minMaxAvg = mma;
      }
      return minMaxAvg;
   }

   /**/
   private void publishAddress(String address){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            (it.next()).updateAddress(address);
         }
         catch(NullPointerException npe){}
      }
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
               //(it.next()).alertNoDewpointData();
               throw new Exception("No Dewpoint Data");
            }
         }
         catch(NullPointerException npe){
            //(it.next()).alertNoDewpointData();
            (it.next()).alertNoDewpointData(npe);
            //this.publishDewpoint(npe);
         }
         catch(Exception e){
            (it.next()).alertNoDewpointData(e);
            //this.publishDewpoint(e);
         }
      }
   }

   /**/
   private void publishDewpoint(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         //observer.alertNoDewpointData();
         observer.alertNoDewpointData(e);
      }
   }

   /**/
   private void publishMinMaxAvgDewpoint(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         (it.next()).alertNoDewpointMinMaxAvg(e);
      }
   }

   /**/
   private void publishMinMaxAvgDewpoint(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updateDewpointMinMaxAvg(list);
            }
            else{
               throw new Exception("No Temperature Min/Max/Avg Data");
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoDewpointMinMaxAvg(npe);
         }
         catch(Exception e){
            (it.next()).alertNoDewpointMinMaxAvg(e);
         }
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
               //(it.next()).alertNoHeatIndexData();
               throw new Exception("No Heat Index Data");
            }
         }
         catch(NullPointerException npe){
            //(it.next()).alertNoHeatIndexData();
            (it.next()).alertNoHeatIndexData(npe);
         }
         catch(Exception e){
            (it.next()).alertNoHeatIndexData(e);
         }
      }
   }

   /**/
   private void publishHeatIndex(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         //observer.alertNoHeatIndexData();
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
               //(it.next()).alertNoHumidityData();
               throw new Exception("No Humidity Data");
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoHumidityData(npe);
         }
         catch(Exception e){
            (it.next()).alertNoHumidityData(e);
         }
      }
   }

   /**/
   private void publishHumidity(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         //observer.alertNoHumidityData();
         observer.alertNoHumidityData(e);
      }
   }

   /**/
   private void publishMinMaxAvgHumidity(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updateHumidityMinMaxAvg(list);
            }
            else{
               throw new Exception("No Humidity Min/Max/Avg Data");
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoHumidityMinMaxAvg(npe);
         }
         catch(Exception e){
            (it.next()).alertNoHumidityMinMaxAvg(e);
         }
      }
   }

   /**/
   private void publishMinMaxAvgHumidity(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         //observer.alertNoHumidityData();
         observer.alertNoHumidityMinMaxAvg(e);
      }
   }

   /**/
   private void publishPressure(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         //observer.alertNoPressureData();
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
               //(it.next()).alertNoPressureData();
               throw new Exception("No Pressure Data");
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoPressureData(npe);
         }
         catch(Exception e){
            (it.next()).alertNoPressureData(e);
         }
      }
   }

   /**/
   private void publishTemperature(Exception re){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         WeatherDatabaseClientObserver observer = it.next();
         //observer.alertNoTemperatureData();
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
               throw new Exception("No Temperature Data");
               //(it.next()).alertNoTemperatureData();
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoTemperatureData(npe);
         }
         catch(Exception e){
            (it.next()).alertNoTemperatureData(e);
         }
      }
   }

   /**/
   private void publishMinMaxAvgTemp(Exception e){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         (it.next()).alertNoTemperatureMinMaxAvgData(e);
      }
   }

   /**/
   private void publishMinMaxAvgTemp(List<WeatherData> list){
      Iterator<WeatherDatabaseClientObserver> it =
                                           this._observers.iterator();
      while(it.hasNext()){
         try{
            if(list.size() > 0){
               (it.next()).updateTemperatureMinMaxAvg(list);
            }
            else{
               throw new Exception("No Temperature Min/Max/Avg Data");
            }
         }
         catch(NullPointerException npe){
            (it.next()).alertNoTemperatureMinMaxAvgData(npe);
         }
         catch(Exception e){
            (it.next()).alertNoTemperatureMinMaxAvgData(e);
         }
      }
   }
}
