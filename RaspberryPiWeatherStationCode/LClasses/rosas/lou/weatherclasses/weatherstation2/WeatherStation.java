/*
The WeatherStation Class.  This is the "new and improved" version of
the WeatherStation Class.  This means that each WeatherStation
instance is only responsible for one set of sensors.
Since these sensors are from Dallas Semi-Conductor, that means that
each and every WeatherStation instance is interacting with a select
port--to pass this information on to the Sensor objects so as they
know the appropriate port with which to find the 1-Wire sensor in
which to interact
A Class By Lou Rosas
*/

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

public class WeatherStation{
   public static short NumberOfStations = 0;
   
   private short stationNumber;
   private String portName;
   private Units  units;
   private Ports  port;
   private ThermometerListener thermometerListener;
   private Sensor thermometer;

   //*******************Constructors*********************************
   /*
   Constructor of no arguments
   */
   public WeatherStation(){
      this.thermometer = null;
      //Set the Current Station Number to the Current Number of
      //Stations
      this.setStationNumber(NumberOfStations);
      //Increment the total number of stations in the Grouping
      //(Related to the WeatherStationGroup)
      ++NumberOfStations;
   }

   //********************Public Methods******************************
   /*
   Register the ThermometerListener that will be "notified" upon
   a change in state of the Thermometer
   */
   public void addThermometerListener(ThermometerListener tl){
      this.thermometerListener = tl;
   }

   /*
   */
   public int getStationNumber(){
      return stationNumber;
   }

   /*
   */
   public Ports getPorts(){
      return this.port;
   }

   /*
   */
   public String getPortName(){
      return this.portName;
   }

   /*
   */
   public Units getUnits(){
      return this.units;
   }

   /*
   Initialize the current WeatherStation instance:  this means
   passing in the Measurement Units, the Port to find the adapter
   and the Current Port Name for find the Sensors to read
   */
   public void initialize(Units units, Ports portType, String port){
      this.setUnits(units);
      this.setPort(portType);
      this.setPortName(port);
      this.setUpSensors();
   }

   /*
   Stop all measurements
   */
   public void stop(){
      this.thermometer.freePort();
      //add more sensors as they "come online"
   }

   /*
   For the given sensors take the appropriate measurements
   */
   public void takeMeasurements(){
      try{
         double temp = this.thermometer.measure(this.getUnits());
         this.thermometerListener.updateTemperature(
            this,
            this.thermometer.measure(this.getUnits()),
            this.getUnits());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         this.stop();
      }
   }

   //********************Private Methods*****************************
   /*
   Find all possible ports for which ada
   */
   private Stack findPorts(PortSniffer ps){
      Hashtable portHash = ps.findPorts();
      Stack returnStack  = null;
      String portName = this.getPortName();
      Enumeration keys = portHash.keys();
      while(keys.hasMoreElements()){
         String currentPort = (String)keys.nextElement();
         if(currentPort.equals(portName)){
            returnStack = (Stack)portHash.get(currentPort);
         }
      }
      return returnStack;
   }

   /*
   Get the elapsed time (in minutes) from the current time and
   the previous time.  If the current time - previous time >=
   incremental measurement (as predefined), set the previous time
   to the current time
   */
   private int getElapsedTime(){
      return -1;
   }

   /*
   Sets the Current Station number for the WeatherStation instance
   */
   private void setStationNumber(short station_number){
      this.stationNumber = station_number;
   }

   /*
   Set the port name for which to find the sensors
   */
   private void setPortName(String currentPort){
      this.portName = new String(currentPort);
   }

   /*
   Set the Port Type for which the DS Port Adapter Interacts with
   the Sensors (as well as where to search for the sensors)
   */
   private void setPort(Ports currentPort){
      this.port = currentPort;
   }

   /*
   Sets the measurement units used when measuring the meteorological
   data:
   Units.METRIC:   For measuring in Metric Units
   Units.ENGLISH:  For measuring in English Units
   Units.ABSOLUTE: For measuring in Absolute Units (if available--
                   mostly for temperature)
   */
   private void setUnits(Units currentUnit){
      this.units = currentUnit;
   }

   /*
   Set up all the sensors for a given WeatherStation instance
   This will be accomplished by finding the devices via a
   PortSnifer instance
   */
   private void setUpSensors(){
      PortSniffer ps   = null;
      Ports port       = this.getPorts();
      String portName = this.getPortName();
      if(port == Ports.USB){
         ps = new PortSniffer(PortSniffer.PORT_USB);
      }
      else{
         ps = new PortSniffer();
      }
      //adapter = this.setUpAdapter();
      Stack sensorStack = this.findPorts(ps);
      //Now, go ahead and set up the appropriate sensors
      thermometer = new Thermometer(sensorStack, port, portName);
      //Add more sensors as they "come online"
   }
}
