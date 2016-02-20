/**/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;

public class Thermometer extends Sensor{
   private static final double DEFAULTTEMP = -999.9;
   
   TemperatureContainer thermalSensor;
   
   double celsius;
   double fahrenheit;
   double kelvin ;
   
   static Thermometer instance = null;
   
   //********************Constructors*******************************
   /*
   Constructor of no attributes
   NOTE:  the constructor is protected
   */
   protected Thermometer(){
      this.setTemperatureCelsius(DEFAULTTEMP);
      this.setTemperatureFahrenheit(DEFAULTTEMP);
      this.setTemperatureKelvin(DEFAULTTEMP);
      this.thermalSensor = null;
   }
   
   //**********************Public Methods***************************
   /*
   Return the Singleton value
   */
   public static Thermometer getInstance(){
      if(instance == null){
         instance = new Thermometer();
      }
      return instance;
   }
   
   /*
   */
   public double getTemperature(){
      return this.getTemperature(this.getUnits());
   }
   
   /*
   */
   public double getTemperature(Units units){
      double returnTemp = DEFAULTTEMP;
      switch(units){
         case METRIC:
            returnTemp = this.getTemperatureCelsius();
            break;
         case ENGLISH:
            returnTemp = this.getTemperatureFahrenheit();
            break;
         case ABSOLUTE:
            returnTemp = this.getTemperatureKelvin();
            break;
         default:
            returnTemp = this.getTemperatureCelsius();
      }
      return returnTemp;
   }
   
   /*
   Override the abstract initialize(...) method from the Sensor
   Abstract class
   */
   public void initialize
   (
      Units units,    //English, Metric, Absolute
      String address, //16-Bit Address as a String
      String name     //DS1920, DS19S20, DS2438...
   ){
      //First, save off the attributes
      this.setUnits(units);
      this.setAddress(address);
      this.setName(name);
      this.setType("Thermometer");
      this.setUSBAdapter();
      
      //Next, go ahead and set up the Thermal Sensor (the actual
      //Dallas Semiconductor harware device communicating over the
      //network).
      String addr        = this.getAddress();
      this.thermalSensor = new OneWireContainer10(this.dspa, addr);
      //Now, go ahead and set the temperature resolution to the
      //highest possible resolution
      try{
         byte [] state = this.thermalSensor.readDevice();
         if(this.thermalSensor.hasSelectableTemperatureResolution()){
            double[] resolution =
                     this.thermalSensor.getTemperatureResolutions();
            this.thermalSensor.setTemperatureResolution(
                                  resolution[resolution.length - 1],
                                  state);
            this.thermalSensor.writeDevice(state);
         }
      }
      catch(OneWireIOException ioe){
         System.out.println("Error Setting Temperature Resolution");
      }
      catch(OneWireException owe){
         System.out.println("Error Setting Temperature Resolution");
      }
   }
   
   /*
   Override the abstract measure(...) method from the Sensor
   Abstract class.  This method takes no attributes, which means
   to measure the temperature in the Units set in the initialize
   method.
   */
   public double measure(){
      return this.measure(this.getUnits());
   }
   
   /*
   Override the abstract measure(...) method from the Sensor
   Abstract class.  This method takes the units attributes, which
   means to measure the temperature data in the requested units:
   Metric, English or Absolute.  If the Units attribute is NOT one
   of the three, this method will by default return the measured
   temperature in Metric
   */
   public double measure(Units units){
      this.setUnits(units);
      try{
         double currentTemp;
         byte [] state = this.thermalSensor.readDevice();
         //perform the temperature conversion
         this.thermalSensor.doTemperatureConvert(state);
         //read the result of the converstion
         state = this.thermalSensor.readDevice();
         //get the temperature from the state data
         //value returned is in celsius
         currentTemp = this.thermalSensor.getTemperature(state);
         //Since the temperature value returned is in celsius, go
         //ahead and set the celsius temperature
         this.setTemperatureCelsius(currentTemp);
         //Convert the celsius temperature into Fahrenheit and
         //Kelvin
         double f = this.convertCelsiusToFahrenheit(currentTemp);
         double k = this.convertCelsiusToKelvin(currentTemp);
         this.setTemperatureFahrenheit(f);
         this.setTemperatureKelvin(k);
      }
      catch(OneWireIOException owe){
         this.setTemperatureCelsius(DEFAULTTEMP);
         this.setTemperatureFahrenheit(DEFAULTTEMP);
         this.setTemperatureKelvin(DEFAULTTEMP);
      }
      catch(OneWireException we){
         this.setTemperatureCelsius(DEFAULTTEMP);
         this.setTemperatureFahrenheit(DEFAULTTEMP);
         this.setTemperatureKelvin(DEFAULTTEMP);
      }
      return this.getTemperature();
   }
   
   /*
   Override the toString() method
   */
   public String toString(){
      String returnString = new String();
      returnString = returnString.concat(this.getType() + ", ");
      returnString = returnString.concat(this.getName() + ", ");
      returnString = returnString.concat(this.getAddress() + ", ");
      returnString = returnString.concat(this.getTemperature()+", ");
      returnString = returnString.concat("" + this.getUnits());
      return returnString;
   }
   
   //***********************Private Methods*************************
   /*
   */
   private double convertCelsiusToFahrenheit(double celsius){
      return Convert.toFahrenheit(celsius);
   }
   
   /*
   */
   private double convertCelsiusToKelvin(double celsius){
      final double KELVIN_CONVERSION = 273.15;
      return celsius + KELVIN_CONVERSION;
   }
   
   /*
   */
   private double getTemperatureCelsius(){
      return this.celsius;
   }
   
   /*
   */
   private double getTemperatureFahrenheit(){
      return this.fahrenheit;
   }
   
   /*
   */
   private double getTemperatureKelvin(){
      return this.kelvin;
   }
   
   /*
   */
   private void setTemperatureCelsius(double temp){
      this.celsius = temp;
   }
   
   /*
   */
   private void setTemperatureFahrenheit(double temp){
      this.fahrenheit = temp;
   }
   
   /*
   */
   private void setTemperatureKelvin(double temp){
      this.kelvin = temp;
   }
}
