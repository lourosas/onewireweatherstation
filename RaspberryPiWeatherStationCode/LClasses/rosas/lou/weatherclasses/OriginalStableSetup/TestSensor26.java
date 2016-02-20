/**
TestSensor26 class.  This Tests for DS2438 Sensors:  that end is 0x26
These curently indicate 1) Humidity Sensor 2) Barometer.  This class
is to determine the output voltage of either to see if there is a 
difference in output voltage between the Humidity Sensor and the
Barometer
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

public class TestSensor26{
   private Hashtable sensorHash;
   private Hashtable sensor26Hash;
   private Stack    sensor26Stack;
   
   public TestSensor26(){
      this.sensorHash    = new Hashtable();
      this.sensor26Hash  = new Hashtable();
      this.sensor26Stack = new Stack();
      this.findSensorInformation();
      this.findSensor26Information();
      this.setUpSensor26Sensors();
      //this.printOutData();
      this.freeAllSensorPorts();
   }

   private void findSensorInformation(){
      Hashtable typeHash = new Hashtable();
      SearchPort searchPort = new SearchPort();
      Stack portStack = new Stack();
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      while(ports.hasMoreElements()){
         CommPortIdentifier cpi = (CommPortIdentifier)ports.nextElement();
         if(cpi.getPortType() == CommPortIdentifier.PORT_SERIAL){
            portStack.push(new String(cpi.getName()));
         }
      }
      while(!portStack.empty()){
         String portString = (String)portStack.pop();
         searchPort.setCurrentPort(portString);
         String hashString = searchPort.grabPortData();
         System.out.println(hashString);
         if(!hashString.startsWith("Nothing")){
            String[] data = hashString.split("\\n");
            for(int i = 0; i < data.length; i += 2){
               try{
                  typeHash.put(data[i], data[i + 1]);
               }
               catch(NullPointerException npe){
                  npe.printStackTrace();
               }
            }
            this.sensorHash.put(portString, typeHash);
         }
      }
   }

   private void findSensor26Information(){
      Enumeration e = this.sensorHash.keys();
      while(e.hasMoreElements()){
         String key = (String)e.nextElement();
         try{
            Hashtable typeHash = (Hashtable)this.sensorHash.get(key);
            Enumeration ids = typeHash.keys();
            while(ids.hasMoreElements()){
               String id    = (String)ids.nextElement();
               String name  = (String)typeHash.get(id);
               if(name.equals("DS2438")){
                  Stack tempStack = new Stack();
                  tempStack.push("Unknown");
                  tempStack.push(name);
                  tempStack.push(id);
                  this.sensor26Hash.put(key, tempStack);
               }
            }
         }
         catch(NullPointerException npe){npe.printStackTrace();}
         System.out.println(sensor26Hash);
      }
   }

   private void freeAllSensorPorts(){
      Enumeration sensors = this.sensor26Stack.elements();
      while(sensors.hasMoreElements()){
         Sensor s = (Sensor)sensors.nextElement();
         s.freePort();
      }
   }

   private void printOutData(){
      Enumeration s = this.sensor26Stack.elements();
      while(s.hasMoreElements()){
         BarometricSensor bs = (BarometricSensor)s.nextElement();
         bs.calculateBarometricPressure();
         System.out.println("\nBarometer (possibly): ");
         System.out.println("\n-----------------------");
         System.out.println(bs + "\n");
      }
   }

   private void setUpSensor26Sensors(){
      BarometricSensor bs;
      Enumeration e = this.sensor26Hash.keys();
      while(e.hasMoreElements()){
         String portName = (String)e.nextElement();
         try{
            Stack data  = (Stack)this.sensor26Hash.get(portName);
            String id   = (String)data.pop();
            String name = (String)data.pop();
            String type = (String)data.pop();
            bs = new BarometricSensor(portName, id, name, type);
            this.sensor26Stack.push(bs);
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
            this.freeAllSensorPorts();
            System.exit(0);
         }
      }
   }
}

