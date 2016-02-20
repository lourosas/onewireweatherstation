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
   //put into a list to handle multiple observers
   private List<TemperatureObserver> t_o_List = null;
   private List<Sensor> sensorList            = null;
   private Sensor thermometer;
   
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
      final int DEFAULTUPDATERATE = 1; //Set to 1 minute for test!
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
   */
   public void initialize(Stack<String> s){
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         this.initializeThermometer(name, address);
      }
   }
   
   /*
   */
   public void initializeThermometer(String name, String address){
      if(name.equals("DS1920") || name.equals("DS18S20")){
         //Grab the Thermometer Singleton and initialize it
         Units units      = this.getUnits();
         this.thermometer = Thermometer.getInstance();
         this.thermometer.initialize(units, address, name);
         this.addToSensorList(this.thermometer);
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
      returnString=returnString.concat(this.thermometer.toString());
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
   */
   private int getUpdateRate(){
      return this.updateRate;
   }
   
   /*
   Publish every and all Weather Events after the lastest measured
   data for each Sensor
   */
   private void publishWeatherEvents(){
      try{
         Iterator<Sensor> i = this.sensorList.iterator();
         while(i.hasNext()){
            Sensor s = i.next();
            this.publishTemperatureEvent(s);
            this.publishHumidityEvent(s);
            this.publishBarometerEvent(s);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   private void publishBarometerEvent(Sensor currentSensor){}

   /*
   */
   private void publishHumidityEvent(Sensor currentSensor){}

   /*
   Publish the TemperatureEvent for the given sensor.
   */
   private void publishTemperatureEvent(Sensor currentSensor){
      if(currentSensor instanceof Thermometer){
         double temp = currentSensor.measure();
         WeatherEvent evt = new WeatherEvent(currentSensor,
                                             "Temperature_Event",
                                             temp,
                                             this.getUnits());
         try{
            Iterator<TemperatureObserver> i = t_o_List.iterator();
            while(i.hasNext()){
               (i.next()).updateTemperature(evt);
            }
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
         }
      }
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
