/********************************************************************
Copyright 2019 Lou Rosas

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
********************************************************************/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import rosas.lou.weatherclasses.*;

public class WeatherStationReader extends WeatherDataPublisher{
   private static int _id = -1;

   //Properties to go here
   private String    _currentFile;
   private Calendar  _cal;

   private List<TemperatureHumidityObserver> _t_h_List;
   private List<BarometerObserver>           _b_o_List;
   private List<CalculatedObserver>          _c_o_List;

   {
      _currentFile="/home/pi/weather-station/current_weather_data.dat";
      _cal      = null;
      _t_h_List = null;
      _b_o_List = null;
      _c_o_List = null;
   };

   //***********************Constructors******************************
   /*
   Constructor of no arguments
   */
   public WeatherStationReader(){
      ++_id;
   }

   /*
   Constructor setting weather data file to be read
   */
   public WeatherStationReader(String file){
      ++_id;
      this.setCurrentFile(file);
   }

   //*************WeatherDataPublisher Methods************************
   /*
   */
   public void addBarometerObserver(BarometerObserver bo){
      try{
         this._b_o_List.add(bo);
      }
      catch(NullPointerException npe){
         this._b_o_List = new Vector<BarometerObserver>();
         this._b_o_List.add(bo);
      }
   }

   /*
   */
   public void removeBarometerObserver(BarometerObserver bo){}

   /*
   */
   public void addCalculatedObserver(CalculatedObserver co){
      try{
         this._c_o_List.add(co);
      }
      catch(NullPointerException npe){
         this._c_o_List = new Vector<CalculatedObserver>();
         this._c_o_List.add(co);
      }
   }

   /*
   */
   public void removeCalculatedObserver(CalculatedObserver co){}

   /*
   */
   public void addTemperatureHumidityObserver
   (
      TemperatureHumidityObserver tho
   ){
      try{
         this._t_h_List.add(tho);
      }
      catch(NullPointerException npe){
         this._t_h_List = new Vector<TemperatureHumidityObserver>();
         this._t_h_List.add(tho);
      }
   }

   /*
   */
   public void removeTemperatureHumidityObserver
   (
      TemperatureHumidityObserver tho
   ){}

   /*
   */
   protected void publishBarometricPressure(WeatherData data){
      try{
         Iterator<BarometerObserver> i = this._b_o_List.iterator();
         while(i.hasNext()){
            BarometerObserver bo = i.next();
            bo.updatePressure(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   protected void publishBarometricPressure(double p, Units u){}

   /*
   */
   protected void publishDewpoint(WeatherData data){
      try{
         Iterator<CalculatedObserver> i = this._c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = i.next();
            co.updateDewpoint(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   protected void publishDewpoint(double dp, Units units){}

   /*
   */
   protected void publishHeatIndex(WeatherData data){
      try{
         Iterator<CalculatedObserver> i = this._c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = i.next();
            co.updateHeatIndex(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   protected void publishHeatIndex(double hi, Units units){}

   /*
   */
   protected void publishHumidity(WeatherData data){
      try{
         Iterator<TemperatureHumidityObserver> i =
                                            this._t_h_List.iterator();
         while(i.hasNext()){
            TemperatureHumidityObserver tho = i.next();
            tho.updateHumidity(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   protected void publishHumidity(double humidity){}

   /*
   */
   protected void publishTemperature(WeatherData data){
      try{
         Iterator<TemperatureHumidityObserver> i =
                                            this._t_h_List.iterator();
         while(i.hasNext()){
            TemperatureHumidityObserver tho = i.next();
            tho.updateTemperature(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   protected void publishTemperature(double temp,Units units){}

   //*******************Public Methods********************************
   /*
   */
   public String getCurrentFile(){
      return this._currentFile;
   }

   /*
   */
   public int id(){
      return this._id;
   }

   /*
   */
   public void measure(){
      BufferedReader reader = null;
      String data           = null;
      try{
         String file = this.getCurrentFile();
         reader = new BufferedReader(new FileReader(file));
         String temp = null;
         while((temp = reader.readLine()) != null){
            data = temp;
         }
         System.out.println(data);
         this.calendar(data);
         this.temperature(data);
         this.humidity(data);
         this.barometricPressure(data);
         this.dewpoint(data);
         this.heatIndex(data);
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      finally{
         try{
            reader.close();
         }
         catch(IOException ioe){}
      }
   }

   //*****************Private Methods*********************************
   /*
   */
   private void calendar(String data){
      this._cal = Calendar.getInstance();
      String mdy = data.split(" ")[0];
      String hms = data.split(" ")[1];
      try{
         String [] yearmonthday = mdy.split("-");
         String yr = yearmonthday[0];
         String mt = yearmonthday[1];
         String dy = yearmonthday[2];
         int year  = Integer.parseInt(yr);
         int month = Integer.parseInt(mt) - 1;
         int day   = Integer.parseInt(dy);
         String [] hourminsec = hms.split(":");
         int hour = Integer.parseInt(hourminsec[0]);
         int min  = Integer.parseInt(hourminsec[1]);
         int sec  = Integer.parseInt(hourminsec[2].split("\\.")[0]);
         this._cal.set(year, month, day, hour, min, sec);
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
   }

   /*
   */
   private void setCurrentFile(String file){
      this._currentFile = new String(file);
   }

   /*
   */
   private void barometricPressure(String data){
      WeatherData weatherData = null;
      double pressure = WeatherData.DEFAULTVALUE;
      try{
         pressure    = Double.parseDouble(data.split(" ")[4]);
         pressure   += 103.5; //Altitude Adjustment
         weatherData = new PressureData(Units.ABSOLUTE,
                                        pressure,
                                        "good",
                                        this._cal);
      }
      catch(NumberFormatException nfe){
         pressure = WeatherData.DEFAULTVALUE;
         nfe.printStackTrace();
         weatherData = new PressureData(Units.ABSOLUTE,
                                        pressure,
                                        nfe.getMessage(),
                                        this._cal);
      }
      finally{
         this.publishBarometricPressure(weatherData);
      }
   }

   /*
   Very Simple:
   Calculate the dewpoint for a given pair of humidity and
   temperature sensors.  This is based on the temperature and
   humidty sensor stacks and is based on the assumption that
   That each temp sensor in the temperature stack corresponds
   to an exact humidity sensor at the same position in the
   humidity stack.  The dewpoint is a formulaic calculation
   based on temperature and relative humidity and is an
   approximation.  The actual calculation depends upon wetbulb
   and dry bulb measurements.  This is an approximation based on
   the Manus-Tetens formula.
   Td = (243.12 * alpha[t,RH])/(17.62 - alpha[t, RH])
   where alpha[t,RH] = (17.62*t/(243.12 + t)) + ln(RH/100)
   and 0.0 < RH < 100.0.
   Temperature -45 to 60 degrees celsius
   */
   private void dewpoint(String data){
      WeatherData weatherData = null;
      double dewpoint = WeatherData.DEFAULTVALUE;
      try{
         final double l = 243.12; //lambda constant
         final double b =  17.62; //beta constant
         double temp     = Double.parseDouble(data.split(" ")[2]);
         double humidity = Double.parseDouble(data.split(" ")[3]);
         double alpha = ((b*temp)/(l+temp))+Math.log(humidity*0.01);
         double dp    = (l * alpha)/(b - alpha);
         weatherData = new DewpointData(Units.METRIC,
                                     dp,
                                     "good",
                                     this._cal);
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         weatherData = new DewpointData(Units.METRIC,
                                     dewpoint,
                                     nfe.getMessage(),
                                     this._cal);
      }
      finally{
         this.publishDewpoint(weatherData);
      }
   }

   /*
   Calculate the heat index for a given pair of humidity and
   temperature sensors.  This is based on the temperature and
   humidity sensor stacks and is based on the assumption that
   each temperature sensor in the temperature stack corresponds
   to an excact humidity sensor at the same position in the
   humidity stack.  The heat index is a formulaic calculation
   based on temprature and relative humidity and is an
   approximation (although pretty good) which is based on sixteen
   calculations.
   heatindex = 16.923                            +
               (.185212*tf)                      +
               (5.37941 * rh)                    -
               (.100254*tf*rh)                   +
               ((9.41685 * 10^-3) * tf^2)        +
               ((7.28898 * 10^-3) * rh^2)        +
               ((3.45372*10^-4) * tf^2*rh)       -
               ((8.14971*10^-4) * tf *rh^2)      +
               ((1.02102*10^-5) * tf^2 * rh^2)   -
               ((3.8646*10^-5) * tf^3)           +
               ((2.91583*10^-5) * rh^3)          +
               ((1.42721*10^-6) * tf^3 * rh)     +
               ((1.97483*10^-7) * tf * rh^3)     -
               ((2.18429*10^-8) * tf^3 * rh^2)   +
               ((8.43296*10^-10) * tf^2 * rh^3)  -
               ((4.81975*10^-11)*t^3*rh^3)

   NOTE:  The Heat Index Calculation becomes inaccurate at a Dry Bulb
          less than 70 F.  If that is the case, the default value
          is set.  For those values, the System will have to determine
          The reason for the default Heat Index.  It is up to the
          System to figure out the appropriateness of the Heat Index
          data for display.
   */
   private void heatIndex(String data){
      final double MINIMUMTEMP = 70.;
      WeatherData weatherData  = null;
      double heatI             = WeatherData.DEFAULTVALUE;
      try{
         double tf = Double.parseDouble(data.split(" ")[2]);
         double rh = Double.parseDouble(data.split(" ")[3]);
         WeatherData tempData = new TemperatureData(Units.METRIC,tf);
         tf = tempData.englishData();
         if(tf < MINIMUMTEMP){
            throw new RuntimeException("Temperature too low");
         }
         heatI  = 16.923;
         heatI += (0.185212 * tf);
         heatI += (5.37941  * rh);
         heatI -= ((0.100254) * tf * rh);
         heatI += ((9.41685 * 0.001) * tf * tf);
         heatI += ((7.28898 * 0.001) * rh * rh);
         heatI += ((3.45372 * 0.0001) * tf * tf * rh);
         heatI -= ((8.14971 * 0.0001) * tf * rh * rh);
         heatI += ((1.02102 * 0.00001) * tf * tf * rh * rh);
         heatI -= ((3.8646  * 0.00001) * tf * tf * tf);
         heatI += ((2.91583 * 0.00001) * rh * rh * rh);
         heatI += ((1.42721 * .000001)* tf * tf * tf *rh);
         heatI += ((1.97483 * .0000001) * tf * rh * rh * rh);
         heatI -= ((2.18429 * .00000001) *tf*tf*tf* rh * rh);
         heatI += ((8.43196 * .0000000001)*tf*tf*rh*rh*rh);
         heatI -= ((4.81975 * .00000000001)*tf*tf*tf*rh*rh*rh);
         weatherData = new HeatIndexData(Units.ENGLISH,
                                         heatI,
                                         "good",
                                         this._cal);
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         weatherData = new HeatIndexData(Units.ENGLISH,
                                         heatI,
                                         nfe.getMessage(),
                                         this._cal);
      }
      catch(RuntimeException re){
         weatherData = new HeatIndexData(Units.ENGLISH,
                                         heatI,
                                         re.getMessage(),
                                         this._cal);
      }
      finally{
         this.publishHeatIndex(weatherData);
      }
   }

   /*
   */
   private void humidity(String data){
      WeatherData weatherData = null;
      double humidity = WeatherData.DEFAULTHUMIDITY;
      try{
         humidity    = Double.parseDouble(data.split(" ")[3]);
         weatherData = new HumidityData(Units.PERCENTAGE,
                                        humidity,
                                        "good",
                                        this._cal);
      }
      catch(NumberFormatException nfe){
         humidity = WeatherData.DEFAULTHUMIDITY;
         nfe.printStackTrace();
         weatherData = new HumidityData(Units.PERCENTAGE,
                                        humidity,
                                        nfe.getMessage(),
                                        this._cal);
      }
      finally{
         this.publishHumidity(weatherData);
      }
   }

   /*
   */
   private void temperature(String data){
      WeatherData weatherData = null;
      double temp = WeatherData.DEFAULTVALUE;
      try{
         temp = Double.parseDouble(data.split(" ")[2]);
         weatherData =
              new TemperatureData(Units.METRIC,temp,"good",this._cal);
      }
      catch(NumberFormatException nfe){
         temp = WeatherData.DEFAULTVALUE;
         nfe.printStackTrace();
         weatherData = new TemperatureData(Units.METRIC,
                                           temp,
                                           nfe.getMessage(),
                                           this._cal);
      }
      finally{
         this.publishTemperature(weatherData);
      }
   }
}
