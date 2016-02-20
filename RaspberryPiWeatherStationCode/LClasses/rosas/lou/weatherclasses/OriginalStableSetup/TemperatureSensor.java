/********************************************************************
The Temperature Sensor class for the Weather Station Project
This class is the software model for the Temperature Sensor
and interacts with the Temperature Sensor built by
Dallas Semi-Conductor
A Class By Lou Rosas
********************************************************************/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class TemperatureSensor extends Sensor{
   //Set up the different temperature constants as an indicator
   //how the temperature is to be returned
   public static final int CELSIUS    = 0;
   public static final int FAHRENHEIT = 1;
   public static final int KELVIN     = 2;

   private OneWireContainer10 tempDevice;
   private static final double DEFAULT_TEMP = -999.9;
   private int currentUnits;
   private double temperatureCelsius;
   private double temperatureFahrenheit;
   private double temperatureKelvin;

   //************************Public Methods**************************
   //
   //Constructor of no arguments
   //
   public TemperatureSensor(){
      super();
      this.tempDevice = null;
      this.convertToCelsius(DEFAULT_TEMP);
      this.convertToFahrenheit(DEFAULT_TEMP);
      this.convertToKelvin(DEFAULT_TEMP);
   }

   //
   //Constructor taking the Port Name, Sensor ID, Sensor Name
   //and type (Thermometer)
   //
   public TemperatureSensor
   (
      String portName,
      String id,
      String name,
      String type
   ){
      //First thing, call the super class
      super(portName, id, name, type);
      this.convertToCelsius(DEFAULT_TEMP);
      this.convertToFahrenheit(DEFAULT_TEMP);
      this.convertToKelvin(DEFAULT_TEMP);
      this.setTemperatureDevice();
      try{
         this.setUnits(CELSIUS);
      }
      catch(UnitsException e){
         System.out.println(e); //Nothing should be thrown here
      }
   }

   /*****************************************************************
   Constructor taking the Port Name, Sensor ID, Sensor Name, Type
   (Thermometer) and Adapter Name (DS9097U, etc....)
   *****************************************************************/
   public TemperatureSensor
   (
      String portName,
      String id,
      String name,
      String type,
      String adapterName
   ){
      //First, call the Super Class
      super(portName, id, name, type, adapterName);
      this.convertToCelsius(DEFAULT_TEMP);
      this.convertToFahrenheit(DEFAULT_TEMP);
      this.convertToKelvin(DEFAULT_TEMP);
      this.setTemperatureDevice();
      try{
         this.setUnits(CELSIUS);
      }
      catch(UnitsException e){
         System.out.println(e); //Nothing should be thrown here
      }
   }

   //
   //Get the temperature in the current units as set by the class
   //
   public double calcTemperature(){
      double temp = DEFAULT_TEMP;
      try{
         byte[] state = this.tempDevice.readDevice();
         this.tempDevice.doTemperatureConvert(state);
         state = this.tempDevice.readDevice();
         //By default, the temperature comes back in degrees Celsius
         temp = this.tempDevice.getTemperature(state);
      }
      catch(OneWireIOException ioe){
         temp = DEFAULT_TEMP;
      }
      catch(OneWireException e){
         temp = DEFAULT_TEMP;
      }
      finally{
         //Convert and save the temp data
         this.convertToCelsius(temp);
         this.convertToKelvin(temp);
         this.convertToFahrenheit(temp);
         int units = this.getUnits();
         if(units == CELSIUS){
            temp = this.getTemperatureCelsius();
         }
         else if(units == FAHRENHEIT){
            temp = this.getTemperatureFahrenheit();
         }
         else{
            temp = this.getTemperatureKelvin();
         }
      }
      return temp;
   }

   /******************************************************************
   ******************************************************************/
   public double calcTemperature(int units) throws UnitsException{
      double temp = this.getTemperatureCelsius();

      if(units >= CELSIUS && units <= KELVIN){
         this.calcTemperature();
         if(units == CELSIUS){
            temp = this.getTemperatureCelsius();
         }
         else if(units == FAHRENHEIT){
            temp = this.getTemperatureFahrenheit();
         }
         else{
            temp = this.getTemperatureKelvin();
         }
      }
      else{
         throw new UnitsException();
      }
      return temp;
   }

   //
   //Get the Celsius Temperature
   //
   public double getTemperatureCelsius(){
      return this.temperatureCelsius;
   }

   //
   //Get the Fahrenheit Temperature
   //
   public double getTemperatureFahrenheit(){
      return this.temperatureFahrenheit;
   }

   //
   //Get the Kelvin Temperature
   //
   public double getTemperatureKelvin(){
      return this.temperatureKelvin;
   }
   //
   //Get the current units
   //
   public int getUnits(){
      return this.currentUnits;
   }

   /*****************************************************************
   Overriding the toString() method as it pertains to this class.
   *****************************************************************/
   public String toString(){
      String string = new String(super.toString() + "\n");
      string =
         string.concat(this.getTemperatureCelsius() + " \u00B0C\n");
      string = string.concat(this.getTemperatureFahrenheit() +
                                                       " \u00B0F\n");
      string =
           string.concat(this.getTemperatureKelvin() + " \u00B0K\n");
      return string;
   }

   /****************************************************************
   Set up the current Temperature Units
   ****************************************************************/
   public void setUnits(int units) throws UnitsException{
      if(units >= CELSIUS && units <= KELVIN){
         this.currentUnits = units;
      }
      else{
         throw new UnitsException();
      }
   }

   //***********************Private Methods**************************
   //
   //No need to conver to Celsius, by default, the One-Wire sensor
   //returns the temperature in degrees Celsius, this is just for
   //archiving the data (althogh, I did keep the name the same
   //as the other unit conversion methods)
   //
   private void convertToCelsius(double temp){
      this.temperatureCelsius = temp;
   }

   //
   //Convert the temperature to Fahrenheit
   //
   private void convertToFahrenheit(double temp){
      double cv = 1.8;  //Conversion factor (9/5)
      double fp = 32.0; //Freezing point of water
      if(temp != DEFAULT_TEMP){
         this.temperatureFahrenheit = (cv*temp)+fp;
      }
      else{
         this.temperatureFahrenheit = temp;
      }
   }

   //
   //Convert the temperature to Kelvin (the Celsius scale based on
   //absolute 0)
   //
   private void convertToKelvin(double temp){
      double conversion = 273.15;
      if(temp != DEFAULT_TEMP){
         this.temperatureKelvin = temp + conversion;
      }
      else{
         this.temperatureKelvin = temp;
      }
   }


   //
   //Set the Temperature Device (The Physical One-Wire temperature
   //Sensor)
   //
   private void setTemperatureDevice(){
      DSPortAdapter adapter = this.getAdapter();
      String id = this.getID();
      this.tempDevice = new OneWireContainer10(adapter, id);
      //Now determine if this device has greater than .5 temperature
      //resolution
      try{
         if(this.tempDevice.hasSelectableTemperatureResolution()){
            //Set to max temperature resolution
            byte[] state = this.tempDevice.readDevice();
            this.tempDevice.setTemperatureResolution(
                    OneWireContainer10.RESOLUTION_MAXIMUM,
                    state);
            this.tempDevice.writeDevice(state);
         }
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
      }
      catch(OneWireException e){
         System.out.println("Error Setting Resolution:  " + e);
      }
   }
}

