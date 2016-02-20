/**/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class Hygrometer extends Sensor{
   public static final double DEFAULTHUMIDITY = -99.9;
   
   private static Hygrometer instance          = null;

   private double calculatedHumidity;
   private double relativeHumidity;
   //Because I am calling functionality OUTSIDE OF the
   //HumidityContainer, I need to declare the hygrometerSensor
   //as OneWireContainer26 (Unless I want to mess with casting all
   //over the place)!  Essentially, this just seems easier.
   private OneWireContainer26 hygrometerSensor;

   //************************Constructors***************************
   /*
   Constructor of no attributes
   NOTE:  Constructor is protected
   */
   protected Hygrometer(){
      this.setCalculatedHumidity(DEFAULTHUMIDITY);
      this.setHumidity(DEFAULTHUMIDITY);
      this.hygrometerSensor = null;
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
   */
   public double getCalculatedHumidity(){
      return this.calculatedHumidity;
   }

   /*
   */
   public double getHumidity(){
      return this.relativeHumidity;
   }

   /*
   Override the abstract initialize(...) method from the Sensor
   Abstract Class
   */
   public void initialize
   (
      Units         units,    //N/A for a Hygrometer
      String        address, //16-Bit Address as a String
      String        name,     //DS1920, DS18S20, DS2438...
      DSPortAdapter dspa
   ){
      //First, save off the attributes
      this.setUnits(units);
      this.setAddress(address);
      this.setName(name);
      this.setType("Hygrometer");
      this.setUSBAdapter(dspa);

      //Next, go ahead and set up the Humidity Sensor (hygrometer)
      //The actual Dallas Semiconductor hardware device 
      //communicating over the network.
      String addr          = this.getAddress();
      this.hygrometerSensor= new OneWireContainer26(this.dspa,addr);
      //Will need the temperature, so go ahead and set up the
      //Temperature Resolution to the highest possible resolution
      try{
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
         System.out.println("Error Setting up Humidity Resolutions");
      }
      catch(OneWireException owe){
         System.out.println("Error Setting up Humidity Resolutions");
      }
   }

   /*
   Override the abstract measure(...) method from the Sensor
   Abstract class.  This method takes no attributes, which is
   irrelevant for this sensor, since the value is measured in
   percentage.
   */
   public double measure(){
      return this.measure(this.getUnits());
   }

   /*
   Override the abstract measure(...) method from the Sensor
   Abstract class.  This method takes the units attribute, which
   is irrelevant in the measurement of the humidity data, since the
   humidity data is in terms of percentage
   */
   public double measure(Units units){
      this.setUnits(units);
      this.measureHumidity();
      this.measureCalculatedHumidity();
      return this.getHumidity();
   }

   /*
   Override the toString() method
   */
   public String toString(){
      String returnString = new String();
      returnString = returnString.concat(this.getType() + ", ");
      returnString = returnString.concat(this.getName() + ", ");
      returnString = returnString.concat(this.getAddress() + ", ");
      String s = String.format("%.3f", this.getHumidity());
      //returnString = returnString.concat(this.getHumidity()+", ");
      returnString = returnString.concat(s + ", ");
      s = String.format("%.3f", this.getCalculatedHumidity());
      returnString = returnString.concat(s + ", ");
           //returnString.concat(this.getCalculatedHumidity() + ", ");
      returnString = returnString.concat("" + this.getUnits());
      return returnString;
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
   private void measureCalculatedHumidity(){
      try{
         double calcHum, temp, vad, vdd;
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
         this.setCalculatedHumidity(calcHum);
      }
      catch(OneWireIOException owe){
         this.setCalculatedHumidity(DEFAULTHUMIDITY);
      }
      catch(OneWireException we){
         this.setCalculatedHumidity(DEFAULTHUMIDITY);
      }
   }

   /*
   Get the humidity as measured by the OneWireSensor26.
   */
   private void measureHumidity(){
      try{
         double rh;
         byte [] state = this.hygrometerSensor.readDevice();
         this.hygrometerSensor.doHumidityConvert(state);
         rh = this.hygrometerSensor.getHumidity(state);
         this.setHumidity(rh);
      }
      catch(OneWireIOException ioe){
         this.setHumidity(DEFAULTHUMIDITY);
      }
      catch(OneWireException we){
         this.setHumidity(DEFAULTHUMIDITY);
      }
   }

   /*
   */
   private void setCalculatedHumidity(double calcHumidity){
      this.calculatedHumidity = calcHumidity;
   }

   /*
   */
   private void setHumidity(double humidity){
      this.relativeHumidity = humidity;
   }
}
