/**/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class Barometer extends WeatherSensor{
   public static final double DEFAULTPRESSURE = -999.9;
   private static final String NAME    = "DS2438";
   private static final String ADDRESS = "92000000BCA3EF26";
   
   private ADContainer barometricSensor;
   private static Barometer instance;

   {
      barometricSensor = null;
      instance         = null;
   };
   
   //**********************Constructors*****************************
   /*
   Constructor of no attributes
   NOTE:  the constructor is protected
   */
   protected Barometer(){
      this.initialize();
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
   Override the abstract measure() from the WeatherSensor
   abstract class.  This method takes no attributes.   
   */
   @Override
   public WeatherData measure(){
      String bad = new String("No Barometric Pressure Data ");
      bad = bad.concat("Available:  default value returned");
      String good = new String("Good");
      WeatherData data = null;
      double pressureInches = WeatherData.DEFAULTVALUE;
      try{
         byte[] state = this.barometricSensor.readDevice();
         //Perform the AD output voltage measurement
         this.barometricSensor.doADConvert(
                                      OneWireContainer26.CHANNEL_VAD,
                                      state);
         //read the result of the conversion
         double vad = this.barometricSensor.getADVoltage(
                                      OneWireContainer26.CHANNEL_VAD,
                                      state);
         pressureInches=this.convertVoltageToPressure(vad);
         data = new PressureData(Units.ENGLISH,
                                 pressureInches,
                                 good,
                                 Calendar.getInstance());
      }
      catch(OneWireIOException ioe){
         System.out.println(ioe.getStackTrace()[0].getFileName());
         System.out.println(""+ioe.getStackTrace()[0].getLineNumber());
         pressureInches = WeatherData.DEFAULTVALUE;
         data = new PressureData(Units.ENGLISH,
                                 pressureInches,
                                 bad,
                                 Calendar.getInstance());
      }
      catch(OneWireException owe){
         System.out.println(owe.getStackTrace()[0].getFileName());
         System.out.println(""+owe.getStackTrace()[0].getLineNumber());
         pressureInches = WeatherData.DEFAULTVALUE;
         data = new PressureData(Units.ENGLISH,
                                 pressureInches,
                                 bad,
                                 Calendar.getInstance());
      }
      catch(NullPointerException npe){
         System.out.println(npe.getStackTrace()[0].getFileName());
         System.out.println(""+npe.getStackTrace()[0].getLineNumber());
         pressureInches = WeatherData.DEFAULTVALUE;
         data = new PressureData(Units.ENGLISH,
                                 pressureInches,
                                 bad,
                                 Calendar.getInstance());
      }
      return data;
   }

   /*
   Override the toString() method
   */
   @Override
   public String toString(){
      String returnString = new String();
      returnString = returnString.concat(this.type() + ", ");
      returnString = returnString.concat(this.name() + ", ");
      returnString = returnString.concat(this.address());
      return returnString;
   }

   //***********************Protected Methods************************
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
            if((o.getName().equals(NAME)) &&
               (o.getAddressAsString().equals(ADDRESS))){
               this.name(o.getName());
               this.address(o.getAddressAsString());
               found = true;
            }
         }
         if(!found){
            throw new NullPointerException("\n\nNo Barometer\n\n");
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
   Override the abstract initialize(...) method from the Sensor
   Abstract class
   */
   @Override
   protected void initialize(){
      try{
         this.type("Barometer");
         if(_dspa == null){
            this.usbAdapter();
         }
         if(_dspa == null){
            throw new NullPointerException("No DSP Adapter");
         }
         this.findSensors();
         //Next, go ahead and set up the Barometric Sensor (the
         //actual
         //Dallas Semiconductor hardware device communicating over
         //the network).
         String addr = this.address();
         this.barometricSensor =
                             new OneWireContainer26(this._dspa,addr);
      }
      catch(NullPointerException npe){
         this.barometricSensor = null;
         System.out.println(npe.getMessage());
      }
   }
   
   //********************Private Methods***************************
   /*
   Given the input (vdd) and output (vad) voltages, convert to 
   the current pressure (in inHg) by applying the appropriate
   formula
   */
   private double convertVoltageToPressure(double vad){
      final double PRESSUREGAIN   =  0.7352;
      final double PRESSUREOFFSET = 26.5296;
      double returnPressure       = DEFAULTPRESSURE;
      
      returnPressure = (vad * PRESSUREGAIN) + PRESSUREOFFSET;
      return returnPressure;
   }
   
}
