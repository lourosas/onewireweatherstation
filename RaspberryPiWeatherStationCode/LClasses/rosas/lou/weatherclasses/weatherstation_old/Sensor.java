/********************************************************************
Sensor class, needed for the Weather Station Project
This class is the Super Class for any and all One-Wire Sensors
as built by Dallas Semi-Conductor
A class by Lou Rosas
********************************************************************/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class Sensor{
   //In the current incarnation
   private static final String ADAPTER_STRING = "DS9097U";
   private String portName;
   private String id;   //The 16-digit id number from each sensor
   private String name; //The Sensor Name (DS1920)
   private String type; //The Type of Sensor (Temp, Humidity...)
   private String adapterString;
   private DSPortAdapter adapter;
   
   //***********************Public Methods***************************
   
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public Sensor(){
      this.setPortName("");
      this.setID("");
      this.setName("");
      this.setType("");
      this.setAdapterName(ADAPTER_STRING);//Set to DS9097U by Default
      this.adapter = null;
   }
   
   /*****************************************************************
   Constructor taking the Port Name Sensor ID, the Sensor Name,
   and Type
   *****************************************************************/
   public Sensor
   (
      String portName,
      String id,
      String name,
      String type
   ){
      this.setPortName(portName);
      this.setID(id);
      this.setName(name);
      this.setType(type);
      this.setAdapterName(ADAPTER_STRING);//Set to DS9097U by Default
      this.setAdapter();
   }

   /*****************************************************************
   Constructor taking everything:  Port Name, Sensor ID, Sensor
   Name, Type and Adapter Name
   *****************************************************************/
   public Sensor
   (
      String portName,
      String id,
      String name,
      String type,
      String adapterName
   ){
      this.setPortName(portName);
      this.setID(id);
      this.setName(name);
      this.setType(type);
      this.setAdapterName(adapterName);
      this.setAdapter();
   }

   /*****************************************************************
   Free the port owned by the adapter
   *****************************************************************/
   public void freePort(){
      try{
         this.adapter.freePort();
      }
      catch(OneWireException e){
         System.out.println("Exception Occured");
         System.out.println("Freeing the adapter port:  " + e);
      }
   }

   /*****************************************************************
   Get the Adapter
   *****************************************************************/
   public DSPortAdapter getAdapter(){
      return this.adapter;
   }
   
   /*****************************************************************
   Get the current adapter name (by default it is:  DS9097U)
   *****************************************************************/
   public String getAdapterName(){
      return this.adapterString;
   }
   
   /*****************************************************************
   Get the current ID (the 16-digit id number from the sensor)
   *****************************************************************/
   public String getID(){
      return this.id;
   }
   
   /*****************************************************************
   Get the current Sensor Name (DS1920, or something similar)
   *****************************************************************/
   public String getName(){
      return this.name;
   }
   
   /*****************************************************************
   Get the current Port Name
   *****************************************************************/
   public String getPortName(){
      return this.portName;
   }
   
   /*****************************************************************
   Get the current Sensor type
   (temperature, humidity, barometer, etc...)
   *****************************************************************/
   public String getType(){
      return this.type;
   }

   /*****************************************************************
   Reset the 1-wire bus (only needs to be done once)
   *****************************************************************/
   public void resetBus(){
      try{
         int reset = this.adapter.reset();
         System.out.println("One-Wire Bus Reset Value:  " + reset);
      }
      catch(OneWireException e){
         System.out.println("Exception in reseting One-Wire Bus");
         System.out.println(e);
      }
   }

   /*****************************************************************
   Set up the One Wire Adapter based on the type and current port
   *****************************************************************/
   public void setAdapter(){
      String name = this.getAdapterName();
      String port = this.getPortName();
      try{
         this.adapter = OneWireAccessProvider.getAdapter(name, port);
         if(adapter == null){
            System.out.println("Unable to find One-Wire Adapter!");
         }
         else{
            System.out.println("Found Adapter:  " +
                               adapter.getAdapterName() +
                               " on port " +
                               adapter.getPortName());
         }
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
      }
      catch(OneWireException e){
         System.out.println("Error finding Adapter:  " + e);
      }
   }
   
   /*****************************************************************
   *****************************************************************/
   public void setAdapterName(String ad){
      this.adapterString = new String(ad);
   }

   /*****************************************************************
   Set the Sensor ID (The 16-digit id number unique to the sensor)
   *****************************************************************/
   public void setID(String ID){
      this.id = new String(ID);
   }
   
   /*****************************************************************
   Set the Name (DS1920 or something of the sort)
   *****************************************************************/
   public void setName(String name){
      this.name = new String(name);
   }
   
   /*****************************************************************
   Set the Current Port name
   *****************************************************************/
   public void setPortName(String portName){
      this.portName = new String(portName);
   }
   
   /*****************************************************************
   Set the current sesor type (temperature, humidity, etc...)
   *****************************************************************/
   public void setType(String type){
      this.type = new String(type);
   }
   
   /*****************************************************************
   The typical toString method overriden from the Object class
   *****************************************************************/
   public String toString(){
      String returnString =
                       new String("Adapter: "+this.getAdapterName());
      returnString =
            returnString.concat("\nPort Name:  "+this.getPortName());
      returnString = returnString.concat("\nID:  "+this.getID());
      returnString = returnString.concat("\nName:  "+this.getName());
      returnString = returnString.concat("\nType:  "+this.getType());
      return returnString;
   }
   
   //**********************Private Methods***************************
}

