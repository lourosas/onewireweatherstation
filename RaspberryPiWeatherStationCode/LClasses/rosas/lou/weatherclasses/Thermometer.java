/*
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
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;

public class Thermometer extends WeatherSensor{
   public static final int DEFAULTTEMP = -999;
   private TemperatureContainer thermalSensor;
   private static Thermometer instance;
   private static final String MAIN_NAME      = "DS1920";
   private static final String SECONDARY_NAME = "DS18S20";

   {
      thermalSensor = null;
      instance      = null;
   };
   
   //********************Constructors*******************************
   /*
   Constructor of no attributes
   NOTE:  the constructor is protected
   */
   protected Thermometer(){
      this.initialize();
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
   Override the abstract measure() method from the WeatherSensor
   Abstract class.  This method takes no attributes, which means
   to measure the temperature in the Units set in the initialize
   method.
   */
   @Override
   public WeatherData measure(){
      String bad = new String("No Temperature Data Available:  ");
      bad = bad.concat("default value returned");
      WeatherData currentData;
      //Default the data in case something bad happens, have
      //Something to return
      double currentTemp = WeatherData.DEFAULTMEASURE;
      try{
         byte [] state = this.thermalSensor.readDevice();
         //perform the temperature conversion
         this.thermalSensor.doTemperatureConvert(state);
         //read the results of the conversion
         this.thermalSensor.readDevice();
         //set the current temp (which is returned by default in
         //Celsius)
         currentTemp = this.thermalSensor.getTemperature(state);
         currentData = new WeatherData();
         currentData.data(WeatherDataType.TEMPERATURE,
                          Units.METRIC,
                          currentTemp,
                          "Good Temperature Data");
      }
      catch(OneWireIOException ioe){
         currentTemp = WeatherData.DEFAULTMEASURE;
         System.out.println(ioe.getStackTrace()[0].getFileName());
         System.out.println(""+ioe.getStackTrace()[0].getLineNumber());
         currentData = new WeatherData(WeatherDataType.TEMPERATURE,
                                       Units.METRIC,
                                       currentTemp,
                                       bad);
      }
      catch(OneWireException   owe){
         currentTemp = WeatherData.DEFAULTMEASURE;
         System.out.println(owe.getStackTrace()[0].getFileName());
         System.out.println(""+owe.getStackTrace()[0].getLineNumber());
         currentData = new WeatherData(WeatherDataType.TEMPERATURE,
                                       Units.METRIC,
                                       currentTemp,
                                       bad);
      }
      catch(NullPointerException npe){
         currentTemp = WeatherData.DEFAULTMEASURE;
         System.out.println(npe.getStackTrace()[0].getFileName());
         System.out.println(""+npe.getStackTrace()[0].getLineNumber());
         currentData = new WeatherData(WeatherDataType.TEMPERATURE,
                                       Units.METRIC,
                                       currentTemp,
                                       bad);
      }
      return currentData;
   }
   
   /*
   Override the toString() method
   */
   public String toString(){
      String returnString = new String();
      returnString = returnString.concat(this.type() + ", ");
      returnString = returnString.concat(this.name() + ", ");
      returnString = returnString.concat(this.address() + ", ");
      return returnString;
   }
   
   //**********************Protected Methods************************
   /*
   Override the abstract initialize() method from the WeatherSensor
   Abstract class
   */
   @Override
   protected void initialize(){
      try{
         //0.  Set the Type (in this case, it is a Thermometer)
         //Very prudent in determining the WeatherSensor
         this.type("Thermometer");
         //1.  Set up the USB Adapter (since we are using a USB
         //Adapter at the moment).
         this.usbAdapter();
         if(this._dspa == null){ 
            throw new NullPointerException("No DSPA Adapter!");
         }
         //3.  Have the WeatherSensor it self determine its own
         //Address and Name.  The Address follows the Name, so the
         //Name of a given sensor is set up.
         this.findSensors();
         //4.  If everything goes well, Set up the
         //OneWireContainer10
         this.thermalSensor =
                   new OneWireContainer10(this._dspa,this.address());
         byte [] state = this.thermalSensor.readDevice();
         if(this.thermalSensor.hasSelectableTemperatureResolution()){
            double [] resolution =
                      this.thermalSensor.getTemperatureResolutions();
            this.thermalSensor.setTemperatureResolution(
                           resolution[resolution.length - 1], state);
            this.thermalSensor.writeDevice(state);
         }
         
      }
      catch(OneWireIOException ioe){
         this.thermalSensor = null;
         System.out.println("Error Setting Temperature Resolution");
         ioe.printStackTrace();
      }
      catch(OneWireException owe){
         this.thermalSensor = null;
         System.out.println("Error Setting Temperature Resolution");
         ioe.printStackTrace();
      }
      catch(NullPointerException npe){
         this.thermalSensor = null;
         System.out.println(npe.getMessage());
      }
   }

   /*
   Override the abstract findSensors() method from the WeatherSensor
   Abstract class.
   */
   @Override
   protected void findSensors() throws NullPointerException{
      try{
         boolean found = false;
         Enumeration<OneWireContainer> e =
                                 this._dspa.getAllDeviceContainers();
         while(e.hasMoreElements() && !found){
            OneWireContainer o = (OneWireContainer)e.nextElement();
            if(o.getName().equals(MAIN_NAME) ||
               o.getName().equals(SECONDARY_NAME)){
               this.name(o.getName());
               this.address(o.getAddressAsString());
               found = true; //Just get the first Thermometer
            }
         }
         if(!found){
            throw new NullPointerException("\n\nNo Thermometer\n");
         }
      }
      catch(OneWireIOException ioe){
         this._address = null;
         this._name    = null;
         ioe.printStackTrace();
      }
      catch(OneWireException   owe){
         this._address = null;
         this._name    = null;
         owe.printStackTrace();
      }
      catch(NullPointerException npe){
         this._address = null;
         this._name    = null;
         npe.printStackTrace();
         throw npe;
      }
   }

   //***********************Private Methods*************************
}
