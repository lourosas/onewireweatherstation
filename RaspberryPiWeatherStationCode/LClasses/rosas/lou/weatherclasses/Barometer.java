/**/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class Barometer extends Sensor{
   public static final double DEFAULTPRESSURE = -99.9;
   
   private double inHg;
   private double mmHg;
   private double millibars;
   
   private ADContainer barometricSensor = null;
   
   private static Barometer instance = null;
   
   //**********************Constructors*****************************
   /*
   Constructor of no attributes
   NOTE:  the constructor is protected
   */
   protected Barometer(){
      this.setBarometricPressureinHg(DEFAULTPRESSURE);
      this.setBarometricPressuremmHg(DEFAULTPRESSURE);
      this.setBarometricPressuremillibars(DEFAULTPRESSURE);
      this.barometricSensor = null;
   }
   
   //************************Public Methods*************************
   /*
   Return the Singleton value
   */
   public static Barometer getInstance(){
      if(instance == null){
         instance = new Barometer();
      }
      return instance;
   }
   
   /*
   */
   public double getBarometricPressure(){
      return this.getBarometricPressure(this.getUnits());
   }
   
   /*
   */
   public double getBarometricPressure(Units units){
      double returnPressure = DEFAULTPRESSURE;
      switch(units){
         case METRIC:
            returnPressure = this.getBarometricPressuremmHg();
            break;
         case ENGLISH:
            returnPressure = this.getBarometricPressureinHg();
            break;
         case ABSOLUTE:
            returnPressure = this.getBarometricPressuremillibars();
            break;
         default:
            returnPressure = this.getBarometricPressuremmHg();
      }
      return returnPressure;
   }
   
   /*
   Override the abstract initialize(...) method from the Sensor
   Abstract class
   */
   public void initialize
   (
      Units  units,      //English, Metric, Absolute
      String address,    //64-bit Address as a String
      String name,       //DS2438, DS
      DSPortAdapter dspa //The DSPortAdpater...
   ){
      //First, save off the attributes
      this.setUnits(units);
      this.setAddress(address);
      this.setName(name);
      this.setType("Barometer");
      this.setUSBAdapter(dspa);
      
      //Next, go ahead and set up the Barometric Sensor (the actual
      //Dallas Semiconductor hardware device communicating over the
      //network).
      String addr          = this.getAddress();
      this.barometricSensor=new OneWireContainer26(this.dspa,addr);
   }
   
   /*
   Override the abstract measure(...) method from the Sensor
   Abstract Class.  This method takes no attributes, which means
   to measure the temperature in the Units set in the intialize
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
         byte [] state = this.barometricSensor.readDevice();
         //perform the A to D output for the output voltage
         this.barometricSensor.doADConvert(
                                     OneWireContainer26.CHANNEL_VAD,
                                     state);
         //read the result of the conversion
         double vad = this.barometricSensor.getADVoltage(
                                     OneWireContainer26.CHANNEL_VAD,
                                     state);
         
         //perform the A to D output for the Supply Voltage
         //(for reference only)
         this.barometricSensor.doADConvert(
                                     OneWireContainer26.CHANNEL_VDD,
                                     state);
         //read the result of the conversion
         double vdd = this.barometricSensor.getADVoltage(
                                     OneWireContainer26.CHANNEL_VDD,
                                     state);
         //Now Convert to Barometric Pressure in inHg
         double inchesHg = this.convertVoltageToPressure(vad, vdd);
         this.setBarometricPressureinHg(inchesHg);
         //Convert the pressure to mmHg and millibars
         double mmhg = WeatherConvert.inchesToMillimeters(inchesHg);
         double millibars = 
                         WeatherConvert.inchesToMillibars(inchesHg);
         this.setBarometricPressuremmHg(mmhg);
         this.setBarometricPressuremillibars(millibars);
      }
      catch(OneWireIOException owe){
         this.setBarometricPressureinHg(DEFAULTPRESSURE);
         this.setBarometricPressuremmHg(DEFAULTPRESSURE);
         this.setBarometricPressuremillibars(DEFAULTPRESSURE);
      }
      catch(OneWireException we){
         this.setBarometricPressureinHg(DEFAULTPRESSURE);
         this.setBarometricPressuremmHg(DEFAULTPRESSURE);
         this.setBarometricPressuremillibars(DEFAULTPRESSURE);
      }
      return this.getBarometricPressure();
   }
   
   /*
   Override the toString() method
   */
   public String toString(){
      double pressure = this.getBarometricPressure();
      String returnString = new String();
      returnString = returnString.concat(this.getType() + ", ");
      returnString = returnString.concat(this.getName() + ", ");
      returnString = returnString.concat(this.getAddress() + ", ");
      String s = String.format("%.3f", pressure);
      returnString = returnString.concat(s +", ");
      returnString = returnString.concat("" + this.getUnits());
      return returnString;
   }
   
   //********************Private Methods***************************
   /*
   Given the input (vdd) and output (vad) voltages, convert to 
   the current pressure (in inHg) by applying the appropriate
   formula
   */
   private double convertVoltageToPressure(double vad, double vdd){
      final double PRESSUREGAIN   =  0.7352;
      final double PRESSUREOFFSET = 26.5296;
      double returnPressure       = DEFAULTPRESSURE;
      
      returnPressure = (vad * PRESSUREGAIN) + PRESSUREOFFSET;
      return returnPressure;
   }
   
   /*
   */
   private double getBarometricPressureinHg(){
      return this.inHg;
   }
   
   /*
   */
   private double getBarometricPressuremmHg(){
      return this.mmHg;
   }
   
   /*
   */
   private double getBarometricPressuremillibars(){
      return this.millibars;
   }
   
   /*
   Set the ENGLISH Units of Barometric Pressure
   */
   private void setBarometricPressureinHg(double inchesHg){
      this.inHg = inchesHg;
   }
   
   /*
   Set the METRIC Units of Barometric Pressure
   */
   private void setBarometricPressuremmHg(double millimetersHg){
      this.mmHg = millimetersHg;
   }
   
   /*
   Set the (What I am calling) ABSOLUTE Units of Barometric Pressure
   */
   private void setBarometricPressuremillibars(double mb){
      this.millibars = mb;
   }
}
