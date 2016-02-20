/********************************************************************
The Humidity Sensor Class for the Weather Station Project.
This class is the software model for the Humidity Sensor
and interacts with the Humidity Sensor (OneWireContainer26) built
by Dallas Semi-Conductor
********************************************************************/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class HumiditySensor extends Sensor{
   //Set up the different constants
   private static final double HUMIDITY_GAIN      = 1.0244;
   private static final double HUMIDITY_OFFSET    = -0.016;
   private static final double DEFAULT_HUMIDITY   = -99.9;

   public static final int    DEVICE   = 0;
   public static final int    ADJUSTED = 1;

   //Set up the different data types and instances
   private OneWireContainer26 humidityDevice;
   //Original Humidity (Should be the "raw" humidity)
   //As measured by the system or originally calibrated
   private double relativeHumidity;
   //Adjusted Relative Humidity based on the originally measured
   //Relative Humidity (as per calibration on pg. 167 of
   //"Weather Toys", Tim Bitson, copyright 2006)
   private double adjustedRelativeHumidity;
   //The Humidity as measure by the device
   private double deviceHumidity;

   //*************************Public Methods*************************
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public HumiditySensor(){
      super();
      this.humidityDevice           = null;
      this.relativeHumidity         = DEFAULT_HUMIDITY;
      this.adjustedRelativeHumidity = DEFAULT_HUMIDITY;
      this.deviceHumidity           = DEFAULT_HUMIDITY;
   }

   /*****************************************************************
   Constructor taking the:
   1.  Port Name
   2.  Sensor ID
   3.  Sensor Name
   4.  Type (Hydrometer)
   *****************************************************************/
   public HumiditySensor
   (
      String portName,
      String id,
      String name,
      String type
   ){
      //First thing:  Call the super class
      super(portName, id, name, type);
      //Set up the device
      this.setHumidityDevice();
      this.relativeHumidity         = DEFAULT_HUMIDITY;
      this.adjustedRelativeHumidity = DEFAULT_HUMIDITY;
      this.deviceHumidity           = DEFAULT_HUMIDITY;
   }

   /*****************************************************************
   Constructor taking the:
   1.  Port Name
   2.  Sensor ID
   3.  Sensor Name
   4.  Type (Hydrometer)
   5.  Adapter Name (DS9097U, etc...)
   *****************************************************************/
   public HumiditySensor
   (
      String portName,
      String id,
      String name,
      String type,
      String adapterName
   ){
      //First thing:  Call the Super Class
      super(portName, id, name, type, adapterName);
      //Set up the device
      this.setHumidityDevice();
      this.relativeHumidity         = DEFAULT_HUMIDITY;
      this.adjustedRelativeHumidity = DEFAULT_HUMIDITY;
      this.deviceHumidity           = DEFAULT_HUMIDITY;
   }
   
   /*****************************************************************
   Calculate the:
   1) Adjusted Relative Humidity
   2) Relative Humidity (as a consequence of calculating the
      adjusted relative humidity)
   3) Device humidity as calculated by the One-Wire Sensor
   This will just calculate the humidities.
   *****************************************************************/
   public void calculateHumidity(){
      //Calculate the Adjusted Relative Humidity (this will by
      //Default calculate the relative humidity)
      this.calculateAdjustedRelativeHumidity();
      //Calculate the Device Humidity (what the One-Wire Sensor
      //Calculated on its own)
      this.calculateDeviceHumidity();
   }

   /*****************************************************************
   Calculate the:
   1) Adjusted Relative Humidity
   2) Relative Humidity (as a consequence of calculating the
      adjusted relative humidity)
   3) Device humidity as calculated by the One-Wire Sensor
   This will just calculate the humidities.  Use the appropriate
   method to grab the calculated data.
   To Calculate the Adjusted Relative and (by default) the Relative
   humidity, message the method using the HumiditySensor.ADJUSTED
   constant.
   To Calculate the Device Measured Humidity, message the method
   using the HumiditySensor.DEVICE constant.
   *****************************************************************/
   public void calculateHumidity(int which){
      if(which == ADJUSTED){
         //Calculate the Adjusted Relative Humidity (this will by
         //Default calculate the relative humidity)
         this.calculateAdjustedRelativeHumidity();
      }
      else if(which == DEVICE){
         //Calculate the Device Humidity (what the One-Wire Sensor
         //Calculated on its own)
         this.calculateDeviceHumidity();
      }
   }

   /*****************************************************************
   Return the Calculated Humidity.  This returns the ADJUSTED
   relative humidity.  The adjusted relative humidity is calibrated
   based on empirical evidence.  This measurement is calculated by
   using adjusting the relative humidity calculation and adjusting
   it by the HUMIDITY_GAIN and HUMIDITY_OFFSET values.  The
   HUMIDITY_GAIN and HUMIDITY_OFFSET values are determined via
   empirical measurement.  Hence this is the calibrated humidity
   calculation.
   *****************************************************************/
   public double getHumidity(){
      //First, Calculate the Adjusted Relative Humidity
      this.calculateAdjustedRelativeHumidity();
      //Second, get the Adjusted Relative Humidity
      return this.getAdjustedRelativeHumidity();
   }

   /*****************************************************************
   Return the Adjusted (Compensated, calibrated) relative humidity
   This is based on calibration as performed post humidity sensor
   (OneWireContainer26) setup.
   The Calibration method is described in great detail on pages
   167-175 of "WeatherToys", Tim Bitson, copyright 2006
   *****************************************************************/
   public double getAdjustedRelativeHumidity(){
      return this.adjustedRelativeHumidity;
   }

   /*****************************************************************
   Return the Humidity (this is unique, the is the humidity
   ACTUALLY calculated by the One Wire Device: not set up via a
   calculation by this class.
   This is done for calibration purposes.
   *****************************************************************/
   public double getDeviceHumidity(){
      return this.deviceHumidity;
   }

   /*****************************************************************
   Return the Calculated Humidity based on the calculation of the
   Sensor input and out voltages (as applied via the formula from
   the hardware manufacturer)
   *****************************************************************/
   public double getRelativeHumidity(){
      return this.relativeHumidity;
   }

   /*****************************************************************
   Return the Input Voltage of the Sensor:  CHANNEL_VDD
   This is the Power Supply voltage
   *****************************************************************/
   public double getInputVoltage(byte[] state){
      int ddChannel = OneWireContainer26.CHANNEL_VDD;
      double vdd = 0.;  //Default to this
      try{
         this.humidityDevice.doADConvert(ddChannel, state);
         vdd = this.humidityDevice.getADVoltage(ddChannel, state);
      }
      catch(OneWireIOException ioe){
         vdd = 0.;
      }
      catch(OneWireException e){
         vdd = 0.;
      }
      return vdd;
   }

   /*****************************************************************
   Return the Output Voltage of the Sensor:  CHANNEL_VAD
   *****************************************************************/
   public double getOutputVoltage(byte[] state){
      int adChannel = OneWireContainer26.CHANNEL_VAD;
      double vad = 0.; //Default to this value
      try{
         this.humidityDevice.doADConvert(adChannel, state);
         vad = this.humidityDevice.getADVoltage(adChannel, state);
      }
      catch(OneWireIOException ioe){
         vad = 0.;
      }
      catch(OneWireException e){
         vad = 0.;
      }
      return vad;
   }

   /*****************************************************************
   Return the HUMIDITY_OFFSET calibration value
   *****************************************************************/
   public double getHumidityOffset(){
      return HUMIDITY_OFFSET;
   }

   /*****************************************************************
   Return the HUMIDITY_GAIN calibration value
   *****************************************************************/
   public double getHumidityGain(){
      return HUMIDITY_GAIN;
   }

   /*****************************************************************
   Overriding the toString() method as it pertains to this class.
   To Print out the Adjusted Relative and (by default) the Relative
   Humidity, message this method using the HumiditySensor.ADJUSTED
   constant.
   To Print out the Device Measured Humidity, message this method
   using the HumiditySensor.DEVICE constant.
   *****************************************************************/
   public String toString(){
      String string = new String(super.toString());
      string = string.concat("\nAdjusted Relative Humidity:  ");
      string = string.concat("" + this.getAdjustedRelativeHumidity());
      string = string.concat("\nRelative Humidity:  ");
      string = string.concat("" + this.getRelativeHumidity());
      string = string.concat("\nDevice Humidity:  ");
      string = string.concat("" + this.getDeviceHumidity());
      string = string.concat("\n");
      return string;
   }

   //************************Private Methods*************************
   /*****************************************************************
   Calculate the humidity as determined by the One-Wire Device.
   This is NOT a calculation actually set up by this class, but
   rather dependant on the device itself.
   *****************************************************************/
   private void  calculateDeviceHumidity(){
      byte[] state;
      double humidity = DEFAULT_HUMIDITY;
      try{
         //Read the device (the sensor state)
         state = this.humidityDevice.readDevice();
         //Take the state information and perform a humidity
         //conversion, to read the humidity data from the device
         this.humidityDevice.doHumidityConvert(state);
         //Read the humidity from the device
         humidity = this.humidityDevice.getHumidity(state);
      }
      catch(OneWireIOException ioe){
         humidity = DEFAULT_HUMIDITY;      
      }
      catch(OneWireException e){
         humidity = DEFAULT_HUMIDITY;
      }
      this.deviceHumidity = humidity;
   }

   /*****************************************************************
   Calculate the Relative Humidity based on the:
   1.  Input Voltage
   2.  Output Voltage
   3.  Temperature
   4.  Formula applied
   Relative humidity calculation based on the book "Weather Toys,
   Tim Bitson, copyright 2006 (pg. 167)
   *****************************************************************/
   private void calculateRelativeHumidity(){
      final double CONST_1 = 0.16;
      final double CONST_2 = 0.0062;
      final double CONST_3 = 1.0546;
      final double CONST_4 = 0.00216;

      byte[] state;
      double temp, vout, vin, rh;
      double humidity = DEFAULT_HUMIDITY;

      try{
         state = this.humidityDevice.readDevice();
         this.humidityDevice.doTemperatureConvert(state);
         temp = this.humidityDevice.getTemperature(state);
         vout = this.getOutputVoltage(state);  //VAD
         vin  = this.getInputVoltage(state);   //VDD
         if(vin <= 0.){ //DO SOMETHING HERE
            //This logic kill two birds with one stone:
            //1. Don't divide over 0
            //2. Should not have a Negative humidity
            humidity = DEFAULT_HUMIDITY;
         }
         else{  //Procede Normally
            rh = ((vout/vin) - CONST_1)/CONST_2;
            humidity = (rh/(CONST_3 - CONST_4 * temp));
         }
      }
      catch(OneWireIOException ioe){
         humidity = DEFAULT_HUMIDITY;
      }
      catch(OneWireException e){
         humidity = DEFAULT_HUMIDITY;
      }
      this.relativeHumidity = humidity;
   }

   /*****************************************************************
   Calculate the Adjusted Relative Humidity based on the:
   1.  Relative Humidity
   2.  Humidity Offset (Empirical Calculation)
   3.  Humidity Gain (Empirical Calculation)
   Relative humidity calculation based on the book "Weather Toys,
   Tim Bitson, copyright 2006 (pg. 167)
   *****************************************************************/
   private void calculateAdjustedRelativeHumidity(){
      double humidity = DEFAULT_HUMIDITY;

      this.calculateRelativeHumidity();
      humidity = this.getRelativeHumidity();
      //Adjust from here
      //If the humidity is measurable AND no device issues
      //(the device is not malfunctioning), adjust the humidity
      if(humidity >= 0.){
         humidity = (humidity * HUMIDITY_GAIN) + HUMIDITY_OFFSET;
      }
      //Set the adjusted relative humidity
      this.adjustedRelativeHumidity = humidity;
   }

   /*****************************************************************
   Set up the Humidity Device.  This means setting up the
   OneWireContainter26 instance held by this Class.
   *****************************************************************/
   private void setHumidityDevice(){
      DSPortAdapter adapter = this.getAdapter();
      String id = this.getID();
      //Instantiate the Humidity Sensor
      this.humidityDevice = new OneWireContainer26(adapter, id);
   }
}
