/*
An abstract class that every type Sensor (Thermometer, Barometer,
etc...) should inherit from:  since all of these are Sensors (is-a)
As a result, this class is set up as abstract:  so that it cannot
be instatiated and just thought of as a super-class
A Class By Lou Rosas
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public abstract class Sensor{
   private String portName;
   private Ports  port;
   private Units  units;
   private String name;  //DS1820, DS2438,...
   private String id;    //The 16-bit Hex Number (as a String)
   private String type;  //"Thermometer", "Barometer", ...

   //**********************Public Methods****************************
   /*
   */
   public String getID(){
      return this.id;
   }

   /*
   */
   public Ports getPort(){
      return this.port;
   }

   /*
   */
   public String getPortName(){
      return this.portName;
   }

   /*
   */
   public String getName(){
      return this.name;
   }

   /*
   */
   public String getType(){
      return this.type;
   }

   /*
   */
   public Units getUnits(){
      return this.units;
   }

   //***********************Protected Methods************************
   /*
   */
   protected void setID(String id){
      this.id = new String(id);
   }

   /*
   */
   protected void setName(String name){
      this.name = new String(name);
   }

   /*
   */
   protected void setPort(Ports port){
      this.port = port;
   }

   /*
   */
   protected void setPortName(String portName){
      this.portName = portName;
   }

   /*
   */
   protected void setUnits(Units units){
      this.units = units;
   }

   /*
   */
   protected void setType(String type){
      this.type = new String(type);
   }

   //************************Abstract Methods************************
   public abstract void  freePort();
   public abstract double measure(Units units);
   public abstract double measure();
   protected abstract void  setUpAdapter();
   protected abstract void  setUpSensorDevice(Stack sensorStack);
}
