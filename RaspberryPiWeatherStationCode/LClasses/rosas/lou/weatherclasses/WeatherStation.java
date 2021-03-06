/********************************************************************
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
********************************************************************/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import gnu.io.*;
import rosas.lou.weatherclasses.*;
import rosas.lou.clock.StopWatch;
import rosas.lou.clock.TimeFormater;
import rosas.lou.clock.TimeListener;
import rosas.lou.clock.ClockState;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;

//Database Stuff (TBD)
//import java.sql.*;

public class WeatherStation extends WeatherDataPublisher 
implements TimeListener{
   private static int _id                    = -1;

   //put into a list to handle multiple observers
   private List<TemperatureObserver> t_o_List  = null;
   private List<HumidityObserver>    h_o_List  = null;
   private List<BarometerObserver>   b_o_List  = null;
   private List<CalculatedObserver>  c_o_List  = null;
   private List<TimeObserver>        ti_o_List = null;
   private List<ExtremeObserver>     ex_o_List = null;
   private List<TemperatureHumidityObserver> t_h_List = null;
   private WeatherData               _dewPoint = null;
   private WeatherData              _heatIndex = null;
   private WeatherData               _humidity = null;
   private WeatherData               _pressure = null;
   private WeatherData            _temperature = null;
   private long            _previousTimeMillis =   -1;
   //Save to the database (by default) every 10 minutes
   private long             _dataBaseSaveTime = 600000;

   //************************Constructors*****************************
   /*
   Constructor of no arguments
   */
   public WeatherStation(){
      ++_id;
   }

   /*
   Constructor initializing the Units, et. al...
   */
   public WeatherStation(Units units){
      ++_id;
   }

   /**/
   public void addBarometerObserver(BarometerObserver bo){
      try{
         this.b_o_List.add(bo);
      }
      catch(NullPointerException npe){
         this.b_o_List = new Vector<BarometerObserver>();
         this.b_o_List.add(bo);
      }
   }   

   /***/
   public void addCalculatedObserver(CalculatedObserver co){
      try{
         this.c_o_List.add(co);
      }
      catch(NullPointerException npe){
         this.c_o_List = new Vector<CalculatedObserver>();
         this.c_o_List.add(co);
      }
   }
   
   /***/
   public void addExtremeObserver(ExtremeObserver eo){
      try{
         this.ex_o_List.add(eo);
      }
      catch(NullPointerException npe){
         this.ex_o_List = new Vector<ExtremeObserver>();
         this.ex_o_List.add(eo);
      }
   }
   
   /***/
   public void addHumidityObserver(HumidityObserver ho){
      try{
         this.h_o_List.add(ho);
      }
      catch(NullPointerException npe){
         this.h_o_List = new Vector<HumidityObserver>();
         this.h_o_List.add(ho);
      }
   }

   public void addTemperatureHumidityObserver
   (
      TemperatureHumidityObserver tho
   ){
      try{
         this.t_h_List.add(tho);
      }
      catch(NullPointerException npe){
         this.t_h_List = new Vector<TemperatureHumidityObserver>();
         this.t_h_List.add(tho);
      }
   }
   
   /***/
   public void addTemperatureObserver(TemperatureObserver to){
      try{
         this.t_o_List.add(to);
      }
      catch(NullPointerException npe){
         this.t_o_List = new Vector<TemperatureObserver>();
         this.t_o_List.add(to);
      }
   }
   
   /***/
   public void addTimeObserver(TimeObserver to){
      try{
         this.ti_o_List.add(to);
      }
      catch(NullPointerException npe){
         this.ti_o_List = new Vector<TimeObserver>();
         this.ti_o_List.add(to);
      }
   }

   /*
   */
   public void barometricPressure(){
      this.setPressure();
      this.publishBarometricPressure(this._pressure);
   }

   /*
   */
   public void barometricPressureAbsolute(){
      this.setPressure();
      double pressure = this._pressure.absoluteData();
      this.publishBarometricPressure(pressure, Units.ABSOLUTE);
   }

   /*
   */
   public void barometricPressureEnglish(){
      this.setPressure();
      double pressure = this._pressure.englishData();
      this.publishBarometricPressure(pressure, Units.ENGLISH);

   }

   /*
   */
   public void barometricPressureMetric(){
      this.setPressure();
      double pressure = this._pressure.metricData();
      this.publishBarometricPressure(pressure, Units.METRIC);
   }

   /*
   Pick the time (in minutes) to save off the database.
   The default is 10 minutes
   */
   public void dataBaseSaveTime(int minutes){
      final int SECONDS      = 60;   //Seconds in a minutue
      final int MILLISECONDS = 1000; //Milli-Seconds in a second
      this._dataBaseSaveTime = minutes*SECONDS*MILLISECONDS;
   }
   
   /*
   */
   public void dewpoint(){
      this.setDewpoint();
      this.publishDewpoint(this._dewPoint);
   }
   
   /*
   */
   public void dewpointAbsolute(){
      this.setDewpoint();
      double dewpoint  = this._dewPoint.absoluteData();
      this.publishDewpoint(dewpoint, Units.ABSOLUTE);
   }
   
   /*
   */
   public void dewpointEnglish(){
      this.setDewpoint();
      double dewpoint  = this._dewPoint.englishData();
      this.publishDewpoint(dewpoint, Units.ENGLISH);
   }
   
   /*
   */
   public void dewpointMetric(){
      this.setDewpoint();
      double dewpoint  = this._dewPoint.metricData();
      this.publishDewpoint(dewpoint, Units.METRIC);
   }
   
   /*
   */
   public void heatIndex(){
      this.setHeatIndex();
      this.publishHeatIndex(this._heatIndex);
      
   }
   
   /*
   */
   public void heatIndexAbsolute(){
      this.setHeatIndex();
      double heatIndex = this._heatIndex.absoluteData();
      this.publishHeatIndex(heatIndex, Units.ABSOLUTE);
   }
   
   /*
   */
   public void heatIndexEnglish(){
      this.setHeatIndex();
      double heatIndex = this._heatIndex.englishData();
      this.publishHeatIndex(heatIndex, Units.ENGLISH);
   }
   
   /*
   */
   public void heatIndexMetric(){
      this.setHeatIndex();
      double heatIndex = this._heatIndex.metricData();
      this.publishHeatIndex(heatIndex, Units.METRIC);
   }

   /*
   */
   public void humidity(){
      this.setHumidity();
      this.publishHumidity(this._humidity);
   }

   /*
   */
   public void humidityValue(){
      this.setHumidity();
      this.publishHumidity(this._humidity.percentageData());
   }

   /*
   */
   public int id(){ return _id; }

   /*
   Now make this real easy
   */
   public void measure(){
      this.temperature();
      this.humidity();
      this.barometricPressure();
      this.dewpoint();
      this.heatIndex();
      //The User needs to know if this was possible
      this.archiveToDatabase();
   }

   /*
   */
   public void removeBarometerObserver(BarometerObserver bo){}
   
   /*
   */
   public void removeCalculatedObserver(CalculatedObserver co){}

   /*
   */
   public void removeTemperatureHumidityObserver
   (
      TemperatureHumidityObserver tho
   ){}

   /*
   */
   public void setUpdateRate(int mins){
      final int SECONDS      = 60;   //Seconds in a minutue
      final int MILLISECONDS = 1000; //Milli-Seconds in a second
      boolean run = true;
      while(run){
         try{
            this.measure();
            Thread.sleep(mins*SECONDS*MILLISECONDS);
         }
         catch(InterruptedException ie){
            run = false;
         }
      }
   }

   /*
   */
   public void temperature(){
      this.setTemperature();
      this.publishTemperature(this._temperature);
   }

   /**
   */
   public void temperatureAbsolute(){
      this.setTemperature();
      double temp = this._temperature.absoluteData();
      this.publishTemperature(temp, Units.ABSOLUTE);
   }

   /**
   */
   public void temperatureEnglish(){
      this.setTemperature();
      double temp = this._temperature.englishData();
      this.publishTemperature(temp, Units.ENGLISH);
   }

   /**
   */
   public void temperatureMetric(){
      this.setTemperature();
      double temp = this._temperature.metricData();
      this.publishTemperature(temp, Units.METRIC);
   }
   
   ////////////Implementation of the TimeListener Interface///////////
   public void update(ClockState cs){}
   
   public void update(Object o){}
   
   public void update(Object o, ClockState cs){}
   
   public void update(Stack<TimeFormater> tfs, ClockState cs){}
   
   public void update(TimeFormater tf){
      this.measure();
   }
   
   public void update(TimeFormater tf, ClockState cs){}
   ///////////////////////////////////////////////////////////////////

   ////////////////////////////Private Methods////////////////////////
   /*
   */
   private void archiveToDatabase(){
      Calendar cal            = Calendar.getInstance();
      WeatherDatabase db      = MySQLWeatherDatabase.getInstance();
      long currentTimeMillis  = cal.getTimeInMillis();
      if((currentTimeMillis - this._previousTimeMillis) >= 
                                             this._dataBaseSaveTime){
         System.out.println("Archive!\n");
         System.out.println(currentTimeMillis + "\n");
         System.out.println(this._previousTimeMillis + "\n");
         this._previousTimeMillis = currentTimeMillis;
         db.temperature(this._temperature);
         db.humidity(this._humidity);
         db.barometricPressure(this._pressure);
         db.dewpoint(this._dewPoint);
         db.heatIndex(this._heatIndex);
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
   private WeatherData calculateDewpoint(){
      /*
      final double l = 243.12;  //lambda constant
      final double b =  17.62;  //Beta constant

      double dewpoint = Thermometer.DEFAULTTEMP;

      Thermometer t = Thermometer.getInstance();
      Hygrometer  h = Hygrometer.getInstance();

      //Temperature in Metric Units
      double temp     = t.getTemperature(Units.METRIC);
      double humidity = h.getHumidity();

      if(temp     > Thermometer.DEFAULTTEMP &&
         humidity > Hygrometer.DEFAULTHUMIDITY){
         double alpha = ((b*temp)/(l+temp))+Math.log(humidity*0.01);
         dewpoint = (l * alpha)/(b - alpha);
      }

      return dewpoint;
      */
      final double l  = 243.12;  //lambda constant
      final double b  =  17.62;  //Beta constant
      double temp     = this._temperature.metricData();
      double humidity = this._humidity.percentageData();
      
      WeatherData dewpoint = null;
      
      if(temp     > WeatherData.DEFAULTVALUE &&
         humidity > WeatherData.DEFAULTHUMIDITY){
         double alpha = ((b*temp)/(l+temp))+Math.log(humidity*0.01);
         double dp    = (l * alpha)/(b - alpha);
         String message = new String("Good Dewpoint Data");
         dewpoint = new DewpointData(Units.METRIC,
                                     dp,
                                     message,
                                     Calendar.getInstance());
      }
      else{
         //If temp OR humidity did not give a good reading, the
         //dewpoint CANNOT be calculated!
         String message = new String("No Dewpoint Data");
         dewpoint = new DewpointData(Units.METRIC,
                                     WeatherData.DEFAULTVALUE,
                                     message,
                                     Calendar.getInstance());
      }
      return dewpoint;
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
   private WeatherData calculateHeatIndex(){
      //final double MINIMUMTEMP = 70.;
      //double heatIndex = Thermometer.DEFAULTTEMP;

      //Thermometer t = Thermometer.getInstance();
      //Hygrometer  h = Hygrometer.getInstance();

      //For this calculation, the tempertaure needs to be in English
      //units (degrees Fahrenheit)
      //double tf = t.getTemperature(Units.ENGLISH);
      //double rh = h.getHumidity();

      /*
      Only consider to calculate the HeatIndex if the Temperature
      and Relative Humidity are not the default values (This would
      indicate an issue related to the sensors or network)
      */
      //if(tf > Thermometer.DEFAULTTEMP &&
      //   rh > Hygrometer.DEFAULTHUMIDITY){
         /*If the Temperature and Humidity are valid (at least
         seem valid), go ahead and determine if the temperature
         is high enough for a valid heat index calculation (> 70).
         The Humidity is assumed to be >= 0. by default/expectation
         of the sensor working correctly.
         */
      //   if(tf >= MINIMUMTEMP){
      //      heatIndex  = 16.923;
      //      heatIndex += (0.185212 * tf);
      //      heatIndex += (5.37941  * rh);
      //      heatIndex -= ((0.100254) * tf * rh);
      //      heatIndex += ((9.41685 * 0.001) * tf * tf);
      //      heatIndex += ((7.28898 * 0.001) * rh * rh);
      //      heatIndex += ((3.45372 * 0.0001) * tf * tf * rh);
      //      heatIndex -= ((8.14971 * 0.0001) * tf * rh * rh);
      //      heatIndex += ((1.02102 * 0.00001) * tf * tf * rh * rh);
      //      heatIndex -= ((3.8646  * 0.00001) * tf * tf * tf);
      //      heatIndex += ((2.91583 * 0.00001) * rh * rh * rh);
      //      heatIndex += ((1.42721 * .000001)* tf * tf * tf *rh);
      //      heatIndex += ((1.97483 * .0000001) * tf * rh * rh * rh);
      //      heatIndex -= ((2.18429 * .00000001) *tf*tf*tf* rh * rh);
      //      heatIndex += ((8.43196 * .0000000001)*tf*tf*rh*rh*rh);
      //      heatIndex-=((4.81975 * .00000000001)*tf*tf*tf*rh*rh*rh);
      //   }
      //}
      //return heatIndex;
      final double MINIMUMTEMP = 70.;
      double tf = this._temperature.englishData();
      double rh = this._humidity.percentageData();
      
      WeatherData heatIndex = null;
      
      if(tf > WeatherData.DEFAULTVALUE &&
         rh > WeatherData.DEFAULTHUMIDITY){
         if(tf >= MINIMUMTEMP){
            double heatI  = 16.923;
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
            String message = new String("Good HeatIndex Data");
            heatIndex = new HeatIndexData(Units.ENGLISH,
                                          heatI,
                                          message,
                                          Calendar.getInstance());
         }
         else{
            //Temperature is too low for accurate calculation
            String message = new String("Temperature Too Low");
            heatIndex = new HeatIndexData(Units.METRIC,
                                          WeatherData.DEFAULTVALUE,
                                          message,
                                          Calendar.getInstance());
         }
      }
      else{
         //if temp or humidity did not give a good reading, the
         //Heat Index CANNOT be calculated!
         String message = new String("No Heat Index Data");
         heatIndex = new HeatIndexData(Units.METRIC,
                                       WeatherData.DEFAULTVALUE,
                                       message,
                                       Calendar.getInstance());
      }
      return heatIndex;
   }

   /**
   */
   protected void publishBarometricPressure(WeatherData data){
      try{
         Iterator<BarometerObserver> i = this.b_o_List.iterator();
         while(i.hasNext()){
            BarometerObserver bo = i.next();
            bo.updatePressure(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      /*
      WeatherEvent evt1     = null;
      WeatherEvent evt2     = null;
      WeatherEvent evt3     = null;
      try{
         Units  units          = Units.METRIC;
         Sensor barometer      = Barometer.getInstance();
         String type           = barometer.getType();
         double data1          = barometer.measure();
         evt1 = new WeatherEvent(barometer, type, data1, units,
                                                   this.currentDate);

         units = Units.ENGLISH;
         data1 = ((Barometer)barometer).getBarometricPressure(units);
         evt2  = new WeatherEvent(barometer, type, data1, units,
                                                   this.currentDate);
         units = Units.ABSOLUTE;
         data1 = ((Barometer)barometer).getBarometricPressure(units);
         evt3  = new WeatherEvent(barometer, type, data1, units,
                                                   this.currentDate);
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         evt1 = new WeatherEvent(null, "Barometer", evt1.getValue(),
                                             null, this.currentDate);
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<BarometerObserver> i = this.b_o_List.iterator();
         while(i.hasNext()){
            BarometerObserver bo = (BarometerObserver)i.next();
            bo.updatePressure(ws);
         }
      
      }
      */
   }

   /*
   */
   protected void publishBarometricPressure(double data, Units units){
      try{
         Iterator<BarometerObserver> i = this.b_o_List.iterator();
         while(i.hasNext()){
            BarometerObserver bo = i.next();
            if(units == Units.METRIC){
               bo.updatePressureMetric(data);
            }
            else if(units == Units.ENGLISH){
               bo.updatePressureEnglish(data);
            }
            else if(units == Units.ABSOLUTE){
               bo.updatePressureAbsolute(data);
            }
	 }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   protected void publishDewpoint(WeatherData data){
      /*
      WeatherEvent evt1 = null;
      WeatherEvent evt2 = null;
      WeatherEvent evt3 = null;
      try{
         Units units       = Units.METRIC;
         //Dewpoint calculation will return in Metric Units, will
         //need to
         //convert to other units.
         double dewpoint   = this.calculateDewpoint();
         double dewpointF  = Thermometer.DEFAULTTEMP;
         double dewpointK  = Thermometer.DEFAULTTEMP;
         evt1 = new WeatherEvent(null, "Dewpoint", dewpoint,
                                            units, this.currentDate);

         if(dewpoint > Thermometer.DEFAULTTEMP){
            dewpointF = WeatherConvert.celsiusToFahrenheit(dewpoint);
            dewpointK = WeatherConvert.celsiusToKelvin(dewpoint);
         }

         units = Units.ENGLISH;
         evt2 = new WeatherEvent(null, "Dewpoint", dewpointF, 
                                            units, this.currentDate);

         units = Units.ABSOLUTE;
         evt3 = new WeatherEvent(null, "Dewpoint", dewpointK, 
                                            units, this.currentDate);

         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         WeatherStorage ws = WeatherStorage.getInstance();
         //evt1 = new WeatherEvent(null,"Dewpoint",-999.9,
         //                                    null, this.currentDate);
         evt1 = new WeatherEvent(null,"Dewpoint",
                                             Thermometer.DEFAULTTEMP,
                                             null, this.currentDate);
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<CalculatedObserver> i = this.c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = (CalculatedObserver)i.next();
            co.updateDewpoint(ws);
         }
      }
      */
      try{
         Iterator<CalculatedObserver> i = c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = i.next();
            co.updateDewpoint(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /**/
   protected void publishDewpoint(double data, Units units){
      try{
         Iterator<CalculatedObserver> i = c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = i.next();
            if(units == Units.METRIC){
               co.updateDewpointMetric(data);
            }
            else if(units == Units.ENGLISH){
               co.updateDewpointEnglish(data);
            }
            else if(units == Units.ABSOLUTE){
               co.updateDewpointAbsolute(data);
            }
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   private void publishExtremes(){
      /*
      try{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<ExtremeObserver> i = this.ex_o_List.iterator();
         while(i.hasNext()){
            ExtremeObserver eo = (ExtremeObserver)i.next();
            eo.updateExtremes(ws);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      */
   }

   /**
   */
   protected void publishHeatIndex(WeatherData data){
      /*
      WeatherEvent evt1 = null;
      WeatherEvent evt2 = null;
      WeatherEvent evt3 = null;
      try{
         Units units       = Units.ENGLISH;
         //Heat Index calculation will return in English Units,
         //will need
         //to convert to other units
         double heatIndexF = this.calculateHeatIndex();
         double heatIndex  = Thermometer.DEFAULTTEMP;
         double heatIndexK = Thermometer.DEFAULTTEMP;
         evt2 = new WeatherEvent(null,"Heat Index",heatIndexF,units,
                                                   this.currentDate);

         if(heatIndexF > Thermometer.DEFAULTTEMP){
            heatIndex=WeatherConvert.fahrenheitToCelsius(heatIndexF);
            heatIndexK=WeatherConvert.fahrenheitToKelvin(heatIndexF);
         }

         units = Units.METRIC;
         evt1 = new WeatherEvent(null,"Heat Index",heatIndex,units,
                                                  this.currentDate);

         units = Units.ABSOLUTE;
         evt3 = new WeatherEvent(null,"Heat Index",heatIndexK,units,
                                                   this.currentDate);

         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         WeatherStorage ws = WeatherStorage.getInstance();
         evt1 = new WeatherEvent(null, "Heat Index",
                                            Thermometer.DEFAULTTEMP,
                                             null, this.currentDate);
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<CalculatedObserver> i = this.c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = (CalculatedObserver)i.next();
            co.updateHeatIndex(ws);
         }
      }
      */
      try{
         Iterator<CalculatedObserver> i = this.c_o_List.iterator();
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
   protected void publishHeatIndex(double data, Units units){
      try{
         Iterator<CalculatedObserver> i = this.c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = i.next();
            if(units == Units.METRIC){
               co.updateHeatIndexMetric(data);
            }
            else if(units == Units.ENGLISH){
               co.updateHeatIndexEnglish(data);
            }
            else if(units == Units.ABSOLUTE){
               co.updateHeatIndexAbsolute(data);
            }
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /**
   */
   protected void publishHumidity(WeatherData data){
      try{
         Iterator<TemperatureHumidityObserver> i =
                                            this.t_h_List.iterator();
         while(i.hasNext()){
            TemperatureHumidityObserver tho = i.next();
            tho.updateHumidity(data);
	 }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      /*
      WeatherEvent evt1     = null;
      WeatherEvent evt2     = null;
      Sensor     hygrometer = Hygrometer.getInstance();
      Hygrometer hyg        = Hygrometer.getInstance();
      String     type       = hygrometer.getType();
      double     data1      = hygrometer.measure();
      double     data2      = hyg.getCalculatedHumidity();
      
      evt1 = new WeatherEvent(hygrometer, type, data1, Units.METRIC,
                                                   this.currentDate);
      evt2 = new WeatherEvent(hyg, type, data2, Units.METRIC,
                                                   this.currentDate);
      
      try{
         WeatherExtreme we = WeatherExtreme.getInstance();
         data1  = evt1.getValue();
         units  = evt1.getUnits();
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         WeatherStorage ws = WeatherStorage.getInstance();
         evt1 = new WeatherEvent(null, "Hygrometer", evt1.getValue(),
                                             null, this.currentDate);
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<HumidityObserver> i = this.h_o_List.iterator();
         while(i.hasNext()){
            HumidityObserver ho = (HumidityObserver)i.next();
            ho.updateHumidity(ws);
         }
      }
      */
   }

   /**
   */
   protected void publishHumidity(double humidity){
      try{
         Iterator<TemperatureHumidityObserver> i =
                                            this.t_h_List.iterator();
         while(i.hasNext()){
            TemperatureHumidityObserver tho = i.next();
            tho.updateHumidity(humidity);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      
   }



   /**
   */
   protected void publishTemperature(WeatherData data){
      try{
         Iterator<TemperatureHumidityObserver> i =
                                            this.t_h_List.iterator();
         while(i.hasNext()){
            TemperatureHumidityObserver tho = i.next();
            tho.updateTemperature(data);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      /*
      WeatherEvent evt1  = null;
      WeatherEvent evt2  = null;
      WeatherEvent evt3  = null;
      Units units        = Units.METRIC;
      Sensor thermometer = Thermometer.getInstance();
      String type        = thermometer.getType();
      double data        = thermometer.measure(units);
      evt1 = new WeatherEvent(thermometer, type, data, units,
                                                   this.currentDate);
      
      units = Units.ENGLISH;
      data  = ((Thermometer)thermometer).getTemperature(units);
      evt2  = new WeatherEvent(thermometer, type, data, units,
                                                   this.currentDate);
      
      units = Units.ABSOLUTE;
      data  = ((Thermometer)thermometer).getTemperature(units);
      evt3  = new WeatherEvent(thermometer, type, data, units,
                                                   this.currentDate);
      
      try{
         Iterator<TemperatureObserver> i = this.t_o_List.iterator();
         //data  = evt1.getValue();
         //units = evt1.getUnits();
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
         while(i.hasNext()){
            TemperatureObserver to = (TemperatureObserver)i.next();
            to.updateTemperature(ws);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      */
   }

   /*
   */
   protected void publishTemperature(double temp, Units units){
      try{
         Iterator<TemperatureHumidityObserver> i =
                                            this.t_h_List.iterator();
         while(i.hasNext()){
            TemperatureHumidityObserver tho = i.next();
            if(units == Units.METRIC){
               tho.updateTemperatureMetric(temp);
            }
            else if(units == Units.ENGLISH){
               tho.updateTemperatureEnglish(temp);
            }
            else if(units == Units.ABSOLUTE){
               tho.updateTemperatureAbsolute(temp);
            }
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   } 

   /**
   */
   private void publishTimeEvent(){
      /*
      this.currentDate = Calendar.getInstance();
      String date = String.format("%tc", this.currentDate.getTime());
      try{
         Iterator<TimeObserver> i = this.ti_o_List.iterator();
         while(i.hasNext()){
            (i.next()).updateTime(date);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      */
   }
   
   /*
   private void setUpDSPA(){
      final int ADAPTER_DATA_SIZE = 2;
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable hash = ps.findPorts();
      Enumeration<Stack> e = hash.keys();
      while(e.hasMoreElements()){
         Stack<String> key     = (Stack)e.nextElement();
         Stack<String> current = (Stack)hash.get(key);
         if(key.size() == ADAPTER_DATA_SIZE){
            String name = key.pop();
            String port = key.pop();
            try{
               this.dspa=OneWireAccessProvider.getAdapter(name,port);
               this.findSensors(current);
            }
            catch(OneWireIOException ioe){ ioe.printStackTrace(); }
            catch(OneWireException owe){ owe.printStackTrace(); }
         }
      }
   }
   */

   /*
   */
   private void setDewpoint(){
      this._dewPoint = this.calculateDewpoint();
   }

   /*
   */
   private void setHeatIndex(){
      this._heatIndex = this.calculateHeatIndex();
   }

   /*
   Set the _humidity WeatherData by messaging the Hygrometer for
   data
   */
   private void setHumidity(){
      this._humidity = Hygrometer.getInstance().measure();
   }

   /*
   */
   private void setPressure(){
      this._pressure = Barometer.getInstance().measure();
   }
   /*
   Set the _temperature WeatherData by messaging the Thermometer for
   data
   */
   private void setTemperature(){
      this._temperature = Thermometer.getInstance().measure();
   }
}
