/*
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class Thermometer extends Sensor{
   private static String sensortype1 = new String("DS18S20");
   private static String sensortype2 = new String("DS1920");

   private static final double DEFAULT_TEMP = -999.9;

   private DSPortAdapter      adapter;
   private OneWireContainer10 tempDevice;

   private double temperature;

   //*********************Constructors*******************************
   /*
   Constructor of taking in the port data so as to find the
   appropriate sensor and id
   */
   public Thermometer(Stack sensorStack,Ports port,String portName){
      //Go ahead an nullify device, name and id upon instantiation
      this.adapter    = null;
      this.tempDevice = null;
      this.setTemperature(DEFAULT_TEMP);
      this.setUnits(Units.METRIC); //Default the Units to metric
      this.setPort(port);
      this.setPortName(portName);
      this.setType("Thermometer");
      this.setUpSensorDevice(sensorStack);
      this.setUpAdapter();
      this.setUpTemperatureDevice();
   }

   //********************Public Methods******************************
   /*
   Upon an "amicable" System Exit, free the AdapterPort
   */
   public void freePort(){
      try{
         this.getAdapter().freePort();
      }
      catch(OneWireException owe){
         owe.printStackTrace();
      }
   }
   
   /*
   */
   public double getTemperature(){
      return this.temperature;
   }

   /*
   From the OneWireContainer10 instance, read the sensor hardware
   for the thermometer.  Set the appropriate units, and return the
   measurement data in those units
   */
   public double measure(Units units){
      this.setUnits(units);
      switch(units){
         case METRIC:
            this.setTemperature(this.getTemperatureCelsius());
            break;
         case ENGLISH:
            this.setTemperature(this.getTemperatureFahrenheit());
            break;
         case ABSOLUTE:
            this.setTemperature(this.getTemperatureKelvin());
            break; 
         default:
            this.setTemperature(this.getTemperatureCelsius());
      }
      return this.getTemperature();
   }

   /*
   Measure the temperature and return in the current units
   */
   public double measure(){
      return this.measure(this.getUnits());
   }
   
   //********************Protected Methods**************************
   /**/
   protected void setUpAdapter(){
      String aName = null;
      String pName = this.getPortName();
      Ports port   = this.getPort();
      if(port == Ports.USB){
         aName = new String("{DS9490}");
      }
      else{
         aName = new String("DS9097U");
      }
      try{
         this.adapter=OneWireAccessProvider.getAdapter(aName,pName);
         this.adapter.selectPort(pName);
         System.out.println("Found Adapter:  " +
                             this.adapter.getAdapterName() +
                             " on port " +
                             this.adapter.getPortName());
         this.adapter.reset(); //Reset the bus
      }
      catch(OneWireIOException owioe){
         owioe.printStackTrace();
      }
      catch(OneWireException owe){
         owe.printStackTrace();
      }
   }

   /*
   Because the data is originally retrieved from a PortSniffer
   instance, the elements in the Stack come in the following order:
   Sensor Name, Sensor ID, <repeating however many times>.  Since,
   from the documentation on Temperature Sensors, the name is either
   1)DS18S20 or
   2)DS1920,  I know the next element in the Stack will be the
   ID (the 8-byte hex value in String form).  If this device is on
   the network
   */
   protected void setUpSensorDevice(Stack sensorStack){
      Enumeration elements = sensorStack.elements();
      while(elements.hasMoreElements()){
         String name = (String)elements.nextElement();
         if(name.equals(sensortype1) || name.equals(sensortype2)){
            this.setName(name);
            String id = (String)elements.nextElement();
            this.setID(id);
         }
      }
   }

   //***********************Private Methods*************************
   /*
   Return the temperature from the OneWireContainer10 device.
   by default, the temperature is returned in degree Celsius.
   If either a OneWireIOException or OweWireException occurs, the
   Default Temperature of -999.9 is returned
   */
   private double calculateTemperature(){
      double temperature;
      OneWireContainer10 device = this.getDevice();
      try{
         byte[] state = device.readDevice();
         device.doTemperatureConvert(state);
         state = device.readDevice();
         //by default, the device returns the temperature in Celsius
         temperature = device.getTemperature(state);
      }
      catch(OneWireIOException owioe){
         temperature = DEFAULT_TEMP;
      }
      catch(OneWireException owe){
         temperature = DEFAULT_TEMP;
      }
      return temperature;
   }

   /**/
   private DSPortAdapter getAdapter(){
      return this.adapter;
   }

   /**/
   private OneWireContainer10 getDevice(){
      return this.tempDevice;
   }

   /**/
   private double getTemperatureCelsius(){
      return this.calculateTemperature();
   }

   /**/
   private double getTemperatureFahrenheit(){
      double conversionFactor = 1.8;
      double freezingPoint    = 32.0;
      double temperature = this.calculateTemperature();
      //If the Temperature is legitimate, convert to Fahrenheit
      if(temperature != DEFAULT_TEMP){
         temperature *= conversionFactor;
         temperature += freezingPoint;
      }
      return temperature;
   }

   /**/
   private double getTemperatureKelvin(){
      double conversion = 273.15;
      double temperature = this.calculateTemperature();
      //If the Temperature is legitimate, convert to Kelvin
      if(temperature != DEFAULT_TEMP){
         temperature += conversion;
      }
      return temperature;
   }

   /**/
   private void setTemperature(double temperature){
      this.temperature = temperature;
   }

   /**/
   private void setUpTemperatureDevice(){
      DSPortAdapter adapter = this.getAdapter();
      String id             = this.getID();
      this.tempDevice = new OneWireContainer10(adapter, id);
      //Now determine if the device has greater than .5 deg C
      //temperature resolution
      try{
         if(this.tempDevice.hasSelectableTemperatureResolution()){
            //Set to max temperature resolution
            byte[] state = this.tempDevice.readDevice();
            this.tempDevice.setTemperatureResolution(
                        tempDevice.RESOLUTION_MAXIMUM,state);
            this.tempDevice.writeDevice(state);
         }
      }
      catch(OneWireIOException owioe){
         owioe.printStackTrace();
      }
      catch(OneWireException owe){
         System.out.println("Error Setting Temperature Resolution");
         System.out.println(owe);
      }
   }
}
