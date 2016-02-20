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
   private Units units;
   private String name;  //DS1820, DS2438,...
   private String id;    //The 16-bit Hex Number (as a String)
   private String type;  //"Thermometer", "Barometer", ...
   private DSPortAdapter adapter;

   //**********************Public Methods****************************
   /*
   */
   public void freePort(){
      try{
         this.getAdapter().freePort();
      }
      catch(OneWireException e){
         e.printStackTrace();
      }
   }

   /*
   */
   public String getID(){
      return this.id;
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
   protected DSPortAdapter getAdapter(){
      return this.adapter;
   }

   /*
   */
   protected void resetBus(){
      try{
         int reset = this.getAdapter().reset();
         System.out.println("One-Wire Bus Reset Value:  " + reset);
      }
      catch(OneWireIOException owioe){
         owioe.printStackTrace();
      }
      catch(OneWireException owe){
         owe.printStackTrace();
      }
   }

   /*
   */
   protected void setAdapter(DSPortAdapter dspa){
      this.adapter = dspa;
   }

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
   protected void setUnits(Units units){
      this.units = units;
   }

   /*
   */
   protected void setType(String type){
      this.type = new String(type);
   }

   //************************Abstract Methods************************
   public abstract double measure(Units units);
   public abstract double measure();
   protected abstract void  setUpDevice();
   protected abstract void  setUpSensorDevice(Stack sensorStack);
}
