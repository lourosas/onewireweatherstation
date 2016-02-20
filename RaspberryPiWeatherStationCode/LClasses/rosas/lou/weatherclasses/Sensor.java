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

public abstract class Sensor{
   private String name;
   private String address;
   private String type; //Thermometer, Hygrometer, Barometer...
   private Units  units;
   
   protected DSPortAdapter dspa;
   
   //**********************Public Methods***************************
   /*
   Return the 16-bit address related to the given sensor
   */
   public String getAddress(){
      return this.address;
   }
   
   /*
   Return the Name:  DS1920, DS18S10, DS2438, ...
   */
   public String getName(){
      return this.name;
   }
   
   /*
   Return the Verbal Type of Sensor:  Thermometer, Hygrometer,
   Barometer...
   */
   public String getType(){
      return this.type;
   }
   
   /**/
   public Units getUnits(){
      return this.units;
   }
   
   /*
   Override the toString() method (for polymorphic purposes only)
   */
   public String toString(){
      return new String("This is a Sensor");
   }
   
   //**********************Protected Methods************************
   /*
   */
   protected void setAddress(String address){
      this.address = new String(address);
   }
   
   /*
   */
   protected void setName(String name){
      this.name = new String(name);
   }
   
   /*
   */
   protected void setType(String type){
      this.type = new String(type);
   }
   
   /*
   */
   protected void setUnits(Units units){
      switch(units){
         case METRIC  :
         case ENGLISH :
         case ABSOLUTE:
            this.units = units;
            break;
         default:
            this.units = Units.METRIC;
      }
   }
   
   /*
   */
   protected void setUSBAdapter(){
      try{
         PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
         Hashtable hash = ps.findPorts();
         Enumeration<Stack> e = hash.keys();
         while(e.hasMoreElements()){
            Stack<String> key = (Stack)e.nextElement();
            if(key.size() == 2){//Should have TWO peices of data
               String name = key.pop();
               String port = key.pop();
               this.dspa=OneWireAccessProvider.getAdapter(name,port);
            }
         }
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
      }
      catch(OneWireException we){
         we.printStackTrace();
      }
   }

   /*
   */
   protected void setUSBAdapter(DSPortAdapter adapter){
      this.dspa = adapter;
   }
   
   //*************************Abstract Methods**********************
   /**/
   public abstract double measure();
   
   /**/
   public abstract double measure(Units units);
   
   /**/
   public abstract void initialize
   (
      Units units,
      String address,
      String name,
      DSPortAdapter dspa
   );
}
