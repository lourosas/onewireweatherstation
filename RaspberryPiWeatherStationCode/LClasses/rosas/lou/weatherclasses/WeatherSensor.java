/*
Copyright 2018 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public abstract class WeatherSensor{
   private String _address;
   private String _name;
   private String _type;
   private Units  _units;

   protected DSPortAdaptor _dspa;

   {
      _address     = null;
      _name        = null;
      _type        = null;
      _units       = Units.METRIC; //Initialize to Metric
      _dspa        = null;
   };

   //*************************Public Methods*************************
      /*
   Return the 16-bit address related to the given sensor
   */
   public String address(){
      return this._address;
   }
   
   /*
   Return the Name:  DS1920, DS18S10, DS2438, ...
   */
   public String name(){
      return this._name;
   }
   
   /*
   Return the Verbal Type of Sensor:  Thermometer, Hygrometer,
   Barometer...
   */
   public String type(){
      return this._type;
   }
   
   /**/
   public Units units(){
      return this._units;
   }
   
   /*
   Override the toString() method (for polymorphic purposes only)
   */
   public String toString(){
      return new String("This is an Unknown Type of Weather Sensor");
   }

   //***********************Protected Methods************************
   /*
   */
   protected void address(String currentAddress){
      this._address = new String(currentAddress);
   }

   /*
   */
   protected void name(String currentName){
      this._name = new String(currentName);
   }

   /*
   */
   protected void type(String currentType){
      this._type = new String(currentType);
   }

   /*
   */
   protected void usbAdapter(){
      try{
         PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
         Hashtable<Stack<String>,Stack<String>> hash = ps.findPorts();
         Enumeration<Stack<String>> e = hash.keys();
         while(e.hasMoreElements()){
            Stack<String> key = (Stack)e.nextElement();
            if(key.size() == 2){//Name, Port
                String name = key.pop();
                String port = key.pop();
                this._dspa = 
                         OneWireAccessProvider.getAdapter(name,port);
            }
         }
      }
      catch(OneWireIOException ioe){
         this._dspa = null;
         ioe.printStackTrace();
      }
      catch(OneWireException owe){
         this._dspa = null;
         owe.printStackTrace();
      }
   }


   //************************Abstract Methods************************
   /**/
   public abstract WeatherData measure();

   /**/
   protected abstract void findSensors();

   /**/
   protected abstract void initialize();
   //************************Private Methods*************************
}
