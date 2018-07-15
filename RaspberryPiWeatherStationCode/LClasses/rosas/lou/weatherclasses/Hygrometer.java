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

public class Hygrometer extends WeatherSensor{
   public static final double DEFAULTHUMIDITY = -99.9;
   private static final String NAME           = "DS2438";

   private static Hygrometer instance;
   //Because I am calling functionality OUTSIDE OF the
   //HumidityContainer, I need to declare the hygrometerSensor
   //as OneWireContainer26 (Unless I want to mess with casting all
   //over the place)!  Essentially, this just seems easier.
   private OneWireContainer26 hygrometerSensor;

   {
      instance         = null;
      hygrometerSensor = null;
   };

   //************************Constructors***************************
   /*
   Constructor of no attributes
   NOTE:  Constructor is protected
   */
   protected Hygrometer(){
      this.initialize();
   }

   //**********************Public Methods***************************
   /*
   Return the Singleton Instance
   */
   public static Hygrometer getInstance(){
      if(instance == null){
         instance = new Hygrometer();
      }
      return instance;
   }



   /*
   Override the abstract measure(...) method from the WeatherSensor
   Abstract class.  This method takes no attributes, which is
   irrelevant for this sensor, since the value is measured in
   percentage.
   */
   @Override
   public WeatherData measure(){
      String bad = new String("No Humidity Data Available:  ");
      bad = bad.concat("default value returned");
      WeatherData data = null;
      double rh = this.measureCalculatedHumidity();
      if(rh > WeatherData.DEFAULTHUMIDITY){ //Relative Humidity Good
         data = new WeatherData(WeatherDataType.HUMIDITY,
                                Units.PERCENTAGE,
                                rh,
                                "Calculated Relative Humidity");
      }
      else{
         data = new WeatherData(WeatherDataType.HUMIDITY,
                                Units.PERCENTAGE,
                                rh,
                                bad);
      }
      return data;
   }

   /*
   Override the toString() method
   */
   public String toString(){
      String returnString = new String();
      returnString = returnString.concat(this.type() + ", ");
      returnString = returnString.concat(this.name() + ", ");
      returnString = returnString.concat(this.address());
      return returnString;
   }

   //********************Protected Methods***************************
   /*
   Override the abstract findSensors() method from the WeatherSensor
   abstract class
   */
   @Override
   protected void findSensors() throws NullPointerException{
      try{
         boolean found = false;
         Enumeration<OneWireContainer> e =
                                 this._dspa.getAllDeviceContainers();
         while(e.hasMoreElements() && !found){
            OneWireContainer o = (OneWireContainer)e.nextElement();
            if(o.getName().equals(NAME) &&
              (!(o.getAddressAsString().equals("92000000BCA3EF26")))){
               this.name(o.getName());
               this.address(o.getAddressAsString());
               found = true;//Get the First Hygrometer
            }
         }
         if(!found){
            throw new NullPointerException("\n\nNo Hygrometer\n\n");
         }
      }
      catch(OneWireIOException ioe){
         this._address = null;
         this._name    = null;
         ioe.printStackTrace();
      }
      catch(OneWireException owe){
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

   /*
   Override the abstract initialize(...) method from the
   WeatherSensor Abstract Class
   */
   @Override
   protected void initialize(){

      try{
         //0.  Set the Type (in this case, it is a Hygrometer)
         //Very prudent in determining the WeatherSensor
         this.type("Hygrometer");
         //1.Set the USB Adapter (this is actually not needed, per
         //se, since it should already be set in the Thermometer--the
         //Thermometer is the prime <first> sensor initialized,
         //usually)
         if(this._dspa == null){
            this.usbAdapter();
         }
         if(this._dspa == null){
            throw new NullPointerException("No DSP Adapter!");
         }
         this.findSensors();
         //Next, go ahead and set up the Humidity Sensor (hygrometer)
         //The actual Dallas Semiconductor hardware device
         //communicating over the network.
         this.hygrometerSensor =
                   new OneWireContainer26(this._dspa,this.address());
         //Will need the temperature, so go ahead and set up the
         //Temperature Resolution to the highest possible resolution
         byte [] state = this.hygrometerSensor.readDevice();
         if(
          this.hygrometerSensor.hasSelectableTemperatureResolution()
         ){
            double [] resolution =
                  this.hygrometerSensor.getTemperatureResolutions();
            this.hygrometerSensor.setTemperatureResolution(
                                  resolution[resolution.length - 1],
                                  state);
            this.hygrometerSensor.writeDevice(state);
         }
         state = this.hygrometerSensor.readDevice();
         if(this.hygrometerSensor.hasSelectableHumidityResolution())
         {
            double [] resolution =
                     this.hygrometerSensor.getHumidityResolutions();
            this.hygrometerSensor.setHumidityResolution(
                                  resolution[resolution.length - 1],
                                  state);
            this.hygrometerSensor.writeDevice(state);
         }
      }
      catch(OneWireIOException ioe){
         this.hygrometerSensor = null;
         ioe.printStackTrace();
         System.out.println("Error Setting up Humidity Resolutions");
      }
      catch(OneWireException owe){
         this.hygrometerSensor = null;
         owe.printStackTrace();
         System.out.println("Error Setting up Humidity Resolutions");
      }
      catch(NullPointerException npe){
         this.hygrometerSensor = null;
         System.out.println(npe.getMessage());
      }
   }

   //***********************Private Methods*************************
   /*
   */
   private double convertVoltageToRelativeHumidity
   (
      double voltageAD,
      double voltageDD,
      double temp
   ){
      final double CONST_1         = 0.16;
      final double CONST_2         = 0.0062;
      final double CONST_3         = 1.0546;
      final double CONST_4         = 0.00216;
      final double HUMIDITY_GAIN   = 1.0244;
      final double HUMIDITY_OFFSET = -0.016;

      double rh = ((voltageAD/voltageDD) - CONST_1)/CONST_2;
      double calcHumidity = (rh/(CONST_3 - CONST_4 * temp));

      return (calcHumidity * HUMIDITY_GAIN + HUMIDITY_OFFSET);
   }

   /*
   Measure the Calculated Humidity as originally specified.  Use
   the constants as specified in previous builds.
   */
   private double measureCalculatedHumidity(){
      double calcHum = WeatherData.DEFAULTHUMIDITY;
      try{
         double temp, vad, vdd;
         //Read the temperature sensor
         byte [] state = this.hygrometerSensor.readDevice();
         this.hygrometerSensor.doTemperatureConvert(state);
         temp = this.hygrometerSensor.getTemperature(state);
         //Read the humidity sensor's output voltage
         this.hygrometerSensor.doADConvert(
                                     OneWireContainer26.CHANNEL_VAD,
                                     state);
         vad = this.hygrometerSensor.getADVoltage(
                                     OneWireContainer26.CHANNEL_VAD,
                                     state);
         //Read the humidity sensor's power supply voltage
         this.hygrometerSensor.doADConvert(
                                     OneWireContainer26.CHANNEL_VDD,
                                     state);
         vdd = this.hygrometerSensor.getADVoltage(
                                     OneWireContainer26.CHANNEL_VDD,
                                     state);
         calcHum=this.convertVoltageToRelativeHumidity(vad,vdd,temp);
      }
      catch(OneWireIOException owe){
         System.out.println(owe.getStackTrace()[0].getFileName());
         System.out.println(""+owe.getStackTrace()[0].getLineNumber());
         calcHum = WeatherData.DEFAULTHUMIDITY;
      }
      catch(OneWireException we){
         System.out.println(we.getStackTrace()[0].getFileName());
         System.out.println(""+we.getStackTrace()[0].getLineNumber());
         calcHum = WeatherData.DEFAULTHUMIDITY;
      }
      catch(NullPointerException npe){
         System.out.println(npe.getStackTrace()[0].getFileName());
         System.out.println(""+npe.getStackTrace()[0].getLineNumber());
         calcHum = WeatherData.DEFAULTHUMIDITY;
      }
      return calcHum;
   }

   /*
   Get the humidity as measured by the OneWireSensor26.
   */
   private double measureHumidity(){
      double rh = WeatherData.DEFAULTHUMIDITY;
      try{
         byte [] state = this.hygrometerSensor.readDevice();
         this.hygrometerSensor.doHumidityConvert(state);
         rh = this.hygrometerSensor.getHumidity(state);
      }
      catch(OneWireIOException ioe){
         rh = WeatherData.DEFAULTHUMIDITY;
      }
      catch(OneWireException we){
         rh = WeatherData.DEFAULTHUMIDITY;
      }
      catch(NullPointerException npe){
         rh = WeatherData.DEFAULTHUMIDITY;
      }
      return rh;
   }
}
