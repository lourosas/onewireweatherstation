/*
The Barometric Sensor class for the Weather Station Project.
This class is the software model for the Barometer and interacts
with the 1-Wire DS2438 Barometer hardware.
NOTE:  The Barometric Pressure calculation is ALTITUDE DEPENDENT 
and currently, the 1-Wire Barometer was Calibrated at the factory
for a given altitude of 2950 ft.
A Class By Lou Rosas
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class BarometricSensor extends Sensor{
   //The default pressure measurement
   public static final int IN_HG = 0;
   //Measure pressure in mm of Hg
   public static final int MM_HG = 1;
   //Measure pressure in millbars
   public static final int MB    = 2;

   //Slope used for determining the Barometric Pressure
   //(at the current altitude)
   private static final double PRESSURE_GAIN   = 0.7352;
   //Offset used for determining the Barometric Pressure
   //(at the current altitude)
   private static final double PRESSURE_OFFSET = 26.5296;
   //The voltage that needs to be set when calibrating the barometer
   private static final double OFFSET_VOLTAGE  = 3.14;
   //The default pressure (indicates the sensor is malfunctionning)
   private static final double DEFAULT_PRESSURE = -99.99;

   //The input voltage
   private double vdd;
   //The output voltage
   private double vad;
   //The calculated Barometric Pressure
   private double barometricPressure;
   //Current units for measurement
   private int units;

   //The 1-Wire Containter (the API interacting with the 1-Wire
   //Barometric Hardware)
   private OneWireContainer26 barometer;

   /****************************Public Methods**********************/
   /**
   Constructor of No Arguments
   */
   public BarometricSensor(){
      super();
      this.setBarometer();
      this.setInputVoltage(0.);
      this.setOutputVoltage(0.);
      this.setPressure(DEFAULT_PRESSURE);
      //Set the units to English (by default) Which is inches Hg
      try{
         this.setUnits(IN_HG);
      }
      catch(UnitsException ue){
         System.out.println(ue); //Nothing should be caught here
      }
   }

   /**
   Constructor taking the:
   1. Port Name
   2. Sensor ID
   3. Sensor Name (DS2438)
   4. Type (Barometer)
   */
   public BarometricSensor
   (
      String portName,
      String id,
      String name,
      String type
   ){
      this(portName, id, name, type, "DS9097U");
   }

   /**
   Constructor taking the:
   1. Port Name
   2. Sensor ID
   3. Sensor Name (DS2438)
   4. Type (Barometer)
   5. Adapter Name (DS9097U, DS9490R)
   */
   public BarometricSensor
   (
      String portName,
      String id,
      String name,
      String type,
      String adapterName
   ){
      super(portName, id, name, type, adapterName);
      this.setBarometer();
      this.setInputVoltage(0.);
      this.setOutputVoltage(0.);
      this.setPressure(DEFAULT_PRESSURE);
      try{
         this.setUnits(IN_HG);
      }
      catch(UnitsException ue){
         System.out.println(ue);
      }
   }

   /**
   Calculate the Barometric Pressure.  This will calculate the 
   Barometric Pressure (by default) in in Hg.  The defualt pressure
   is not necissarily the presure returned:  which is based on the
   state of the pressure messurement (the units).  The Pressure by
   default is calculated in in. Hg.  But, are converted to the 
   appropriate units (mm in Hg or millibars) as needed based on the
   units set.
   */
   public double calculateBarometricPressure(){
      double pressure = DEFAULT_PRESSURE;
      //First, get the pressure, then convert
      try{
         this.calculatePressure();
         int units = this.getUnits();
         //Convert to the appropriate units as needed
         if(units == MM_HG){
            //Convert to mm Hg
            pressure = this.getPressure_mm();
         }
         else if(units == MB){
            //Convert to millibars
            pressure = this.getPressure_mb();
         }
         else{
            //Just get the default pressure
            pressure = this.getPressure();
         }
      }
      //If the units are not set properly, indicate that and return
      //the default pressure
      catch(UnitsException ue){
         ue.printStackTrace();
         pressure = DEFAULT_PRESSURE;
      }
      return pressure;
   }

   /**
   Calculate the default barometric pressure (in. Hg)
   */
   public double calculateBarometricPressure_in(){
      this.calculatePressure();
      return this.getPressure();
   }

   /**
   Calculate the Barometric Pressure in mm Hg
   This is done by calculating the Pressure by default (in in. Hg)
   and then CONVERTING it to mm
   */
   public double calculateBarometricPressure_mm(){
      this.calculatePressure();
      return this.getPressure_mm();
   }

   /**
   Calculate the Barometric Pressure in millbars
   This is done by calculating the Pressure by default (in in. Hg)
   and then CONVERTING to millibars
   */
   public double calculateBarometricPressure_mb(){
      this.calculatePressure();
      return this.getPressure_mb();
   }

   /**
   Return the barometric pressure measured in the default units
   (in. Hg).
   */
   public double getPressure(){
      return this.barometricPressure;
   }

   /**
   Return the barometric pressure measured in mm Hg.  This is done
   by grabbing the default pressure (which is in in Hg) and 
   converting it to millimeters by multiplying it by 25.4.
   If there is an error (an exception of some kind), the default
   pressure value will be returned.  This is a way of letting the
   system or user know there is something wrong with the barometer
   */
   public double getPressure_mm(){
      double inches_to_millimeters = 25.4;
      double pressure = this.getPressure();
      //The pressure should NOT be less than 0! in other words,
      //if a negative pressure is received then:
      //1.  There is something wrong with the barometer
      //2.  The Earth's atmosphere has dwindled away and we are
      //    all dead.
      //I am betting on the first one (since I cannot fix the
      //second issue)
      if(pressure > 0.){
         //Convert the value from inches to millimeters
         pressure *= inches_to_millimeters;
      }
      else{
         //Return the default pressure (do not bother to convert)
         pressure = DEFAULT_PRESSURE;
      }
      return pressure;
   }

   /**
   Return the barometric pressure measured in millibars.  This is
   done by grabbing the default pressure (which is in in Hg) and
   converting it to millibars by multiplying it by 33.864
   */
   public double getPressure_mb(){
      double inches_to_millibars = 33.864;
      double pressure = this.getPressure();
      //The pressure should NOT be less than 0! in other words,
      //if a negative pressure is received then:
      //1.  There is something wrong with the barometer
      //2.  The Earth's atmosphere has dwindled away and we are
      //    all dead.
      //I am betting on the first one (since I cannot fix the
      //second issue)
      if(pressure > 0.){
         //Convert the value from inches to millibars
         pressure *= inches_to_millibars;
      }
      else{
         pressure = DEFAULT_PRESSURE;
      }
      return pressure;
   }

   /**
   Return the calibrated slope (gain) for the current barometric 
   calibration (this is subject to change)
   */
   public double getCalibratedSlope(){
      //Return the current Gain value (subject to change)
      return PRESSURE_GAIN;
   }

   /**
   Return the calibrated offest (intercept) for the current
   barometric calibration (this is subject to change)
   */
   public double getCalibratedOffset(){
      //Return the current Slope value (subject to change)
      return PRESSURE_OFFSET;
   }

   /**
   Return the Input Voltage (vdd).  This is previously calculated.
   */
   public double getInputVoltage(){
      return this.vdd;
   }

   /**
   Return the voltage needing to be set when calibrating the
   barometer.
   */
   public double getOffsetVoltage(){
      return OFFSET_VOLTAGE;
   }

   /**
   Return the Output Voltage (vad).  This is previously calculated.
   */
   public double getOutputVoltage(){
      return this.vad;
   }

   /**
   Return the current units (in. Hg, mm Hg, millibars)
   */
   public int getUnits(){
      return this.units;
   }

   /**
   Set up the Current Measurement Units (in Hg, mm Hg or millibars)
   @throws UnitsException
   */
   public void setUnits(int units) throws UnitsException{
      if(units >= IN_HG && units <= MB){
         this.units = units;
      }
      else{
         this.units = IN_HG;
         throw new UnitsException();
      }
   }

   /**
   Overriding the toString() method pertaining to this class
   */
   public String toString(){
      String s = new String("\n" + super.toString() + "\n");
      s = s.concat(this.getPressure() + " in Hg\n");
      s = s.concat(this.getPressure_mm() + " mm Hg\n");
      s = s.concat(this.getPressure_mb() + " mb\n");
      s = s.concat("Input Voltage:  " + this.getInputVoltage());
      s = s.concat("\nOutput Voltage: " + this.getOutputVoltage());
      s = s.concat("\nGain:  " + this.getCalibratedSlope());
      s = s.concat("\nOffset:  " + this.getCalibratedOffset());
      s = s.concat("\nOffset Voltage:  " + this.getOffsetVoltage());
      s = s.concat("\n");
      return s;
   }

   /*************************Private Methods************************/
   /*
   Calculate the Barometric Pressure for the given altitude based
   on the formula related to the DS2438 Barometric Sensor:
   Pressure = Output_Voltage * Pressure Slope + Pressure_Offset
   Since the Pressure_Slope and Pressure_Offset are calibrated,
   these constants are subject to change (need calibration for the
   long term, as well as the short) and are based purely on
   mathematical and then adjusted based on measured results.
   */
   private void calculatePressure(){
      int vddChannel = OneWireContainer26.CHANNEL_VDD;
      int vadChannel = OneWireContainer26.CHANNEL_VAD;
      double pressure = this.getPressure();
      try{
         byte[] state = this.barometer.readDevice();

         //Read A to D output voltage
         this.barometer.doADConvert(vadChannel, state);
         double output =
                      this.barometer.getADVoltage(vadChannel, state);
         this.setOutputVoltage(output);

         //Read A to D input voltage (for calibration only)
         this.barometer.doADConvert(vddChannel, state);
         double input = 
                       this.barometer.getADVoltage(vddChannel,state);
         this.setInputVoltage(input);

         //Calulate the pressure (in Hg) and set
         pressure = output * PRESSURE_GAIN + PRESSURE_OFFSET;         
      }
      catch(OneWireIOException ioe){
         System.out.println("Barometer Error, " + ioe);
         pressure = DEFAULT_PRESSURE;
      }
      catch(OneWireException we){
         System.out.println("Barometer Error, " + we);
         pressure = DEFAULT_PRESSURE;
      }
      finally{
         this.setPressure(pressure);
      }
   }

   /*
   Set the 1-Wire Container 26 (DS2438) device software object
   (the OneWireContainer26 object)
   */
   private void setBarometer(){
      DSPortAdapter adapter = this.getAdapter();
      String id = this.getID();
      this.barometer = new OneWireContainer26(adapter, id);
   }

   /*
   Set the input voltage
   */
   private void setInputVoltage(double inputVoltage){
      this.vdd = inputVoltage;
   }

   /*
   Set the output voltage
   */
   private void setOutputVoltage(double outputVoltage){
      this.vad = outputVoltage;
   }

   /*
      Set the Barometric Pressure Value
   */
   private void setPressure(double pressure){
      this.barometricPressure = pressure;
   }
}
