/**/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

//Database Stuff (TBD)
//import java.sql.*;

public class WeatherStation implements Runnable{
   private int updateRate;
   //Does not need to be public!
   private static short NumberOfStations = 0;
   private short stationNumber; //Initialize to 0!
   private Units units;
   private String currentDate                 = null;
   private DSPortAdapter dspa                 = null;
   //put into a list to handle multiple observers
   private List<TemperatureObserver> t_o_List = null;
   private List<HumidityObserver> h_o_List    = null;
   private List<BarometerObserver> b_o_List   = null;
   private List<CalculatedObserver> c_o_List  = null;
   private List<TimeObserver> ti_o_List       = null;
   private List<Sensor> sensorList            = null;
   
   //************************Constructor****************************
   /*
   Constructor of no arguments
   */
   public WeatherStation(){
      this(Units.METRIC);
   }
   
   /*
   Constructor initializing the Units
   */
   public WeatherStation(Units units){
      //final int DEFAULTUPDATERATE = 10; //10 minutes
      final int DEFAULTUPDATERATE = 5; // 5 minutes
      //Initialize the currentDate string to no value
      this.currentDate = new String();
      //Set the current Station Number
      this.setStationNumber(NumberOfStations);
      //Set up all the Observers
      this.setUpObservers();
      //Set up the Units (by default, Metric)
      this.setUnits(units);
      this.setUpdateRate(DEFAULTUPDATERATE);
      //Now, increment the number of stations by 1
      ++NumberOfStations;     
   }
   
   //**********************Public Methods***************************
   /*
   Register a BarometerObserver
   */
   public void addBarometerObserver(BarometerObserver bo){
      try{
         this.b_o_List.add(bo);
      }
      catch(NullPointerException npe){
         this.b_o_List = new Vector<BarometerObserver>();
         this.b_o_List.add(bo);
      }
   }
   
   /*
   Register a CalculatedObserver
   */
   public void addCalculatedObserver(CalculatedObserver co){
      try{
         this.c_o_List.add(co);
      }
      catch(NullPointerException npe){
         this.c_o_List = new Vector<CalculatedObserver>();
         this.c_o_List.add(co);
      }
   }
   
   /*
   Register a HumidityObserver
   */
   public void addHumidityObserver(HumidityObserver ho){
      try{
         this.h_o_List.add(ho);
      }
      catch(NullPointerException npe){
         this.h_o_List = new Vector<HumidityObserver>();
         this.h_o_List.add(ho);
      }
   }
   
   /*
   Register a ThermometerObserver
   */
   public void addTemperatureObserver(TemperatureObserver to){
      try{
         this.t_o_List.add(to);
      }
      catch(NullPointerException npe){
         this.t_o_List = new Vector<TemperatureObserver>();
         this.t_o_List.add(to);
      }
   }
   
   /*
   Register a Time Observer
   */
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
   public void initialize(Stack<String> s){
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         if(!name.equals("DS1990A")){
            this.initializeThermometer(name, address);
            this.initializeHygrometer(name, address);
         }
      }
   }
   
   /*
   For so many reasons, the generic initialize(...) method will NOT
   work for the Hygrometer AND Barometer!  This is due to the fact
   that BOTH have the name Name (are the same type).  As a result,
   the value MUST BE resolved based on the Address!  If not, there
   will be problems.  This is due to the fact the Barometer is 
   essentially a "cluge" of the DS2438 sensor, and hence, the
   Barometer and Hygrometer ARE NOT interchangeable.  Thus, it is
   up to the Weather Station OR the Weather Network to DETERMINE
   WHICH Sensor is the Hygrometer and WHICH Sensor is the Barometer.
   These are based on the ADDRESS of the sensor which are VERY
   Specific!  As a result, the system MUST call this directly.
   */
   public void initializeBarometer(String name, String address){
      if(name.equals("DS2438")){
         Units units = this.getUnits();
         //Get the Singleton
         Sensor sensor = Barometer.getInstance();
         sensor.initialize(units, address, name);
         this.addToSensorList(sensor);
      }
   }
   
   /*
   For so many reasons, the generic initialize(...) method will NOT
   work for the Hygrometer AND Barometer!  This is due to the fact
   that BOTH have the name Name (are the same type).  As a result,
   the value MUST BE resolved based on the Address!  If not, there
   will be problems.  This is due to the fact the Barometer is 
   essentially a "cluge" of the DS2438 sensor, and hence, the
   Barometer and Hygrometer ARE NOT interchangeable.  Thus, it is
   up to the Weather Station OR the Weather Network to DETERMINE
   WHICH Sensor is the Hygrometer and WHICH Sensor is the Barometer.
   These are based on the ADDRESS of the sensor which are VERY
   Specific!  As a result, the system MUST call this directly.
   */
   public void initializeHygrometer(String name, String address){
      if(name.equals("DS2438")){
         Units units = this.getUnits();
         //Get the Singleton
         Sensor sensor = Hygrometer.getInstance();
         sensor.initialize(units, address, name);
         this.addToSensorList(sensor);
      }
   }
   
   /*
   */
   public void initializeThermometer(String name, String address){
      if(name.equals("DS1920") || name.equals("DS18S20")){
         //Grab the Thermometer Singleton and initialize it
         Units units   = this.getUnits();
         Sensor sensor = Thermometer.getInstance();
         sensor.initialize(units, address, name);
         this.addToSensorList(sensor);
      }
   }

   /*
   */
   public Units getUnits(){
      return this.units;
   }
   
   /*
   Implementing the run method from as part of implementing the
   Runnable interface
   */
   public void run(){
      Calendar cal = Calendar.getInstance();
      int currentMinute;
      int lastMinute = -1;
      int count      =  0;
      //for now, go ahead and run continuosly
      while(true){
         try{
            Thread.sleep(1000); //Sleep for one second
         }
         catch(InterruptedException ie){}
         cal.setTimeInMillis(System.currentTimeMillis());
         currentMinute = cal.get(Calendar.MINUTE);
         //Measure the weather data at a requested rate
         if(currentMinute != lastMinute){
            if((count % this.getUpdateRate()) == 0){
               if(this.setCurrentDate()){
                  //For Future Use--go ahead and set up for finding
                  //extremes
               }
               //Publish the time event
               this.publishTimeEvent();
               //Go Ahead and publish the WeatherEvents
               //From all Sensors (this seems the easiest)
               this.publishWeatherEvents();
            }
            lastMinute = currentMinute;
            ++count;
         }
      }
   }
   
   /*
   */
   public void setUpdateRate(int rate){
      this.updateRate = rate;
   }

   /*
   Override the toString() method
   */
   public String toString(){
      String returnString = new String("");
      return returnString;
   }
   
   //**********************Private Methods**************************
   /*
   */
   private void addToSensorList(Sensor sensor){
      try{
         this.sensorList.add(sensor);
      }
      catch(NullPointerException npe){
         this.sensorList = new Vector<Sensor>();
         this.sensorList.add(sensor);
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
   private double calculateDewpoint(){
      final double l = 243.12;  //lambda
      final double b =  17.62;  //Beta
      
      double dewpoint = Thermometer.DEFAULTTEMP;
      
      Thermometer t = Thermometer.getInstance();
      Hygrometer  h = Hygrometer.getInstance();
      
      //Now, for the calculation, get the appropriate temperature
      //and relative humidity
      double celsius  = t.getTemperature(Units.METRIC);
      double humidity = h.getHumidity();
      
      //Only calculate the dewpoint if the Temperature and Relative
      //Humidity are not the default values (This would indicate
      //an issue related to the sensors or network)
      if(celsius  > Thermometer.DEFAULTTEMP &&
         humidity > Hygrometer.DEFAULTHUMIDITY){
         double alpha = 
                ((b*celsius)/(l+celsius)) + Math.log(humidity*0.01);
         //This will now give us the dewpiont in degrees celsius.
         //Now, need to go ahead and figure out the units
         //needed.
         dewpoint = (l * alpha)/(b - alpha);
         Units units = this.getUnits();
         if(units == Units.ENGLISH){
            double f = WeatherConvert.celsiusToFahrenheit(dewpoint);
            dewpoint = f;
         }
         else if(units == Units.ABSOLUTE){
            double k = WeatherConvert.celsiusToKelvin(dewpoint);
            dewpoint = k;
         }
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
   private double calculateHeatIndex(){
      final double MINIMUMTEMP = 70.;
      double heatIndex = Thermometer.DEFAULTTEMP;
      
      Thermometer thermometer = Thermometer.getInstance();
      Hygrometer  hygrometer  = Hygrometer.getInstance();
      
      //For this calculation, the temperature needs to be in 
      //English Units (degrees Fahernheit)
      double tf = thermometer.getTemperature(Units.ENGLISH);
      double rh = hygrometer.getHumidity();
      
      /*
      Only consider to calculate the HeatIndex if the Temperature
      and Relative Humidity are not the default values (This would
      indicate an issue related to the sensors or network)
      */
      if(tf > Thermometer.DEFAULTTEMP &&
         rh > Hygrometer.DEFAULTHUMIDITY){
         /*If the Temperature and Humidity are valid (at least
         seem valid), go ahead and determine if the temperature
         is high enough for a valid heat index calculation (> 70).
         The Humidity is assumed to be >= 0. by default/expectation
         of the sensor working correctly.
         */
         if(tf >= MINIMUMTEMP){
            heatIndex =  16.923;
            heatIndex += (.185212 * tf);
            heatIndex += (5.37941 * rh);
            heatIndex -= ((.100254) * tf * rh);
            heatIndex += ((9.41685 * .001) * tf * tf);
            heatIndex += ((7.28898 * .001) * rh * rh);
            heatIndex += ((3.45372 * .0001) * tf * tf * rh);
            heatIndex -= ((8.14971 * .0001) * tf * rh * rh);
            heatIndex += ((1.02102 * .00001) * tf * tf * rh * rh);
            heatIndex -= ((3.8646  * .00001) * tf * tf * tf);
            heatIndex += ((2.91583 * .00001) * rh * rh * rh);
            heatIndex += ((1.42721 * .000001)* tf * tf * tf * rh);
            heatIndex += ((1.97483 * .0000001) * tf * rh * rh * rh);
            heatIndex -= ((2.18429 * .00000001) *tf*tf*tf* rh * rh);
            heatIndex += ((8.43196 * .0000000001) *tf*tf*rh*rh* rh);
            heatIndex-=((4.81975 * .00000000001)*tf*tf*tf*rh*rh*rh);
            Units units = this.getUnits();
            if(units == Units.METRIC){
               double c = 
                      WeatherConvert.fahrenheitToCelsius(heatIndex);
               heatIndex = c;
            }
            else if(units == Units.ABSOLUTE){
               double k =
                       WeatherConvert.fahrenheitToKelvin(heatIndex);
               heatIndex = k;
            }
         }
      }
      
      return heatIndex;
   }
   
   /*
   */
   private int getUpdateRate(){
      return this.updateRate;
   }
   
   /*
   Publish every and all Weather Events after the lastest measured
   data for each Sensor
   */
   private void publishWeatherEvents(){
      this.publishTemperatureEvent();
      this.publishHumidityEvent();
      this.publishBarometerEvent();
      this.publishCalculatedEvents();
   }

   /*
   Publish the Barometer Event for the given sensor.
   */
   private void publishBarometerEvent(){
      Sensor s    = Barometer.getInstance();
      double data = s.measure();
      String type = s.getType();
      Units units = this.getUnits();
      WeatherEvent evt = new WeatherEvent(s, type, data, units);
      try{
         Iterator<BarometerObserver> i = this.b_o_List.iterator();
         while(i.hasNext()){
            (i.next()).updatePressure(evt);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   Publish the Calculated Weather Events:  Dewpoint, HeatIndex,
   WindChill
   */
   public void publishCalculatedEvents(){
      //For the Heat Index, go ahead and get the Thermometer to
      //help if the heat index returns a defautl temperature
      //(-999.9), which could indicate the sensors went out, or the
      //temperature is too low for accurate HeatIndex calculations.
      Thermometer t = Thermometer.getInstance();
      WeatherEvent dpevt, hievt;
      Units units = this.getUnits();
      double dewpoint  = this.calculateDewpoint();
      double heatindex = this.calculateHeatIndex();
      dpevt = new WeatherEvent(null, "Dewpoint", dewpoint, units);
      hievt = new WeatherEvent(t, "HeatIndex", heatindex, units);
      try{
         Iterator<CalculatedObserver> i = this.c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = i.next();
            co.updateDewpoint(dpevt);
            co.updateHeatIndex(hievt);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   Publish the Humidity Event for the given sensor.
   */
   private void publishHumidityEvent(){
      Sensor s    = Hygrometer.getInstance();
      double data = s.measure();
      String type = s.getType();
      Units units = this.getUnits();
      WeatherEvent evt = new WeatherEvent(s, type, data, units);
      try{
         Iterator<HumidityObserver> i = this.h_o_List.iterator();
         while(i.hasNext()){
            (i.next()).updateHumidity(evt);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   Publish the Temperature Event for the given sensor.
   */
   private void publishTemperatureEvent(){
      Sensor s    = Thermometer.getInstance();
      double data = s.measure();
      String type = s.getType();
      Units units = this.getUnits();
      WeatherEvent evt = new WeatherEvent(s, type, data, units);
      try{
         Iterator<TemperatureObserver> i = this.t_o_List.iterator();
         while(i.hasNext()){
           (i.next()).updateTemperature(evt);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   Publish the time event
   */
   private void publishTimeEvent(){
      Calendar cal = Calendar.getInstance();
      String s = String.format("%tT", cal.getTime());
      try{
         Iterator<TimeObserver> i = this.ti_o_List.iterator();
         while(i.hasNext()){
            (i.next()).updateTime(s);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   private boolean setCurrentDate(){
      boolean isNewDate = false;
      Calendar cal      = Calendar.getInstance();
      //Now, set the date and compare
      String date = String.format("%tm", cal.getTime());
      date = date.concat("_");
      date = date.concat(String.format("%td", cal.getTime()));
      date = date.concat("_");
      date = date.concat(String.format("%tY", cal.getTime()));
      isNewDate = !(date.equals(this.currentDate));
      //Set the current date as appropriate
      if(isNewDate){
         this.currentDate = new String(date);
      }
      return isNewDate;
   }
   
   /*
   Set the Current Station Number
   */
   private void setStationNumber(short currentNumber){
      this.stationNumber = currentNumber;
   }
   
   /*
   Set up the Units for the system.  If the units are NOT one of the
   three described, go ahead and default to metric.
   */
   private void setUnits(Units currentUnits){
      switch(currentUnits){
         case METRIC  :
         case ENGLISH :
         case ABSOLUTE:
            this.units = currentUnits;
            break;
         default:
            this.units = Units.METRIC;
      }
   }
   
   /*
   Set up ALL the observers to the Weather Station
   */
   private void setUpObservers(){
      this.setUpTemperatureObserver();
   }
   
   /*
   Instantiate the ThermometerObserver Vector
   */
   private void setUpTemperatureObserver(){
      this.t_o_List = new Vector<TemperatureObserver>();
   }
}
