/**
A Generic Search Port Class:  for searching for ports that have a
Dallas Semiconductor 1-Wire Adapter--Either of the Serial Port
(DS9097U) or of the USB (DS9094R) type
A Class By Lou Rosas
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class PortSniffer{
   public static final short PORT_USB                = 0;
   public static final short PORT_SERIAL             = 1;
   private static final String ADAPTER_STRING_USB    = "DS9094R";
   private static final String ADAPTER_STRING_SERIAL = "DS9097U";

   private short type;

   /**************************Public Methods************************/
   /**
   Constructor of no arguments
   */
   public PortSniffer(){
      this(PORT_SERIAL);
   }

   /**
   Constructor taking the the type of adapter to use (USB or Serial)
   */
   public PortSniffer(short type){
      this.portType(type);
   }

   /**
   Find all the ports related to a specific type of Dallas
   Semiconductor Adapter (either USB or Serial)
   */
   public Hashtable findPorts(){
      Hashtable returnHash = new Hashtable();
      Stack portStack = this.grabPortData();
      Stack<String> adapterStack = new Stack<String>();
      while(!portStack.empty()){
         String portData = (String)portStack.pop();
         if(!portData.startsWith("Nothing")){
            String [] data = portData.split("\\n");
            Stack stringStack = new Stack();
            for(int i = data.length - 1; i > 2; i--){
               try{
                  stringStack.push(data[i]);
               }
               catch(NullPointerException npe){
                  npe.printStackTrace();
               }
            }
            adapterStack.push(data[2]); //Adapter Port
            adapterStack.push(data[1]); //Adapter Name
            //Need the key to be unique:  which is more likely to
            //be the Port Name
            //returnHash.put(data[2], stringStack);
            returnHash.put(adapterStack, stringStack);
         }
      }
      return returnHash;
   }

   /**
   Set the type of Adapter that connects to sensors.
   By default, set the type of adapter to type Serial
   */
   public void portType(short porttype){
      switch(porttype){
         case PORT_USB:
         case PORT_SERIAL:
            this.type = porttype;
            break;
         //If the porttype is not set properly, set it by default
         //to the serial port
         default: this.type = PORT_SERIAL;
      }
   }
   
   /**
   Because of issues I am currently having, go ahead and print all
   the ports that are available. 
   */
   public void printAllPorts(){
      DSPortAdapter dspa;
      Enumeration e = OneWireAccessProvider.enumerateAllAdapters();
      while(e.hasMoreElements()){
         dspa = (DSPortAdapter)e.nextElement();
         Enumeration p = dspa.getPortNames();
         while(p.hasMoreElements()){
            try{
               String port = (String)p.nextElement();
               dspa.selectPort(port);
               if(dspa.adapterDetected()){
                  System.out.println("Adapter: " +
                                             dspa.getAdapterName());
                  System.out.println("Port: " + port);
                  System.out.println("Adapter Detected");
                  dspa.beginExclusive(true);
                  dspa.setSearchAllDevices();
                  dspa.targetAllFamilies();
                  dspa.setSpeed(DSPortAdapter.SPEED_REGULAR);
                  Enumeration c = dspa.getAllDeviceContainers();
                  while(c.hasMoreElements()){
                     OneWireContainer owc =
                                  (OneWireContainer)c.nextElement();
                     System.out.println(owc.getAddressAsString());
                     System.out.println(owc.getName());
                     
                  }
                  dspa.endExclusive();
               }
               dspa.freePort();
            }
            catch(OneWireIOException ioe){}
            catch(OneWireException owe){}
         }
      }
      System.out.println(
      OneWireAccessProvider.getProperty("onewire.adapter.default"));
      System.out.println(
      OneWireAccessProvider.getProperty("onewire.port.default"));
      
   }   

   /**
   Get the type of port used to connect to the Sensors
   */
   public short portType(){
      return this.type;
   }

   /**
   Overriding the toString method for this specific object
   */
   public String toString(){
      String rs = new String();
      Stack printStack = this.grabPortData();
      Enumeration e = printStack.elements();
      while(e.hasMoreElements()){
         rs = rs.concat((String)e.nextElement());
      }
      return rs;
   }

   /*************************Private Methods************************/
   /**
   Grab the appropriate Port Data based on if the port is USB or
   Serial
   */
   private Stack grabPortData(){
      Stack returnStack = new Stack();
      short porttype = this.portType(); //Get the port type
      if(porttype == PORT_USB){
         returnStack = this.grabUSBDefaultPortData();
      }
      else{
         returnStack = this.grabSerialPortData();
      }
      return returnStack;
   }

   /**
   Grab the default USB Port Data:  For a while, until more and
   different adapters are added, this will be the "Status Quo" for
   USB ports
   */
   private Stack grabUSBDefaultPortData(){
      Stack returnStack = new Stack();
      String pd = new String("USB\n"); //pd = Port Data
      try{
         DSPortAdapter dspa;
         dspa = OneWireAccessProvider.getDefaultAdapter();
         pd = pd.concat(dspa.getAdapterName() + "\n");
         pd = pd.concat(dspa.getPortName() + "\n");
         dspa.selectPort(dspa.getPortName());
         dspa.beginExclusive(true);
         dspa.setSearchAllDevices();
         dspa.targetAllFamilies();
         dspa.setSpeed(DSPortAdapter.SPEED_REGULAR);
         Enumeration e = dspa.getAllDeviceContainers();
         while(e.hasMoreElements()){
            OneWireContainer owc = (OneWireContainer)e.nextElement();
            //Get the address of the One Wire Container
            pd = pd.concat(owc.getAddressAsString() + "\n");
            //Get the Name of the One Wire Container
            //(Very Important for this stage of developement)
            pd = pd.concat(owc.getName() + "\n");
         }
         dspa.endExclusive();
         dspa.freePort();
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
         pd = new String("Nothing good here");
      }
      catch(OneWireException we){
         we.printStackTrace();
         pd = new String("Nothing One-Wire related found");
      }
      finally{ returnStack.push(pd); }
      return returnStack;
   }

   /**
   Grab the Serial Port Data:  Default or not.
   */
   private Stack grabSerialPortData(){
      Stack returnStack = new Stack();
      //Enumerate through all the possible adapters connected
      Enumeration e = OneWireAccessProvider.enumerateAllAdapters();
      while(e.hasMoreElements()){
        String pd = null;
         try{
            DSPortAdapter dspa = (DSPortAdapter)e.nextElement();
            if(dspa.getAdapterName().equals(ADAPTER_STRING_SERIAL)){
               pd = new String("Serial\n"); //pd = Port Data
               pd = pd.concat(dspa.getAdapterName() + "\n");
               Enumeration f = dspa.getPortNames();
               while(f.hasMoreElements()){
                  String port = (String)f.nextElement();
                  pd = pd.concat(port + "\n");
                  dspa.selectPort(port);
                  dspa.beginExclusive(true);
                  dspa.setSearchAllDevices();
                  dspa.targetAllFamilies();
                  dspa.setSpeed(DSPortAdapter.SPEED_REGULAR);
                  Enumeration g = dspa.getAllDeviceContainers();
                  while(g.hasMoreElements()){
                     OneWireContainer o =
                                   (OneWireContainer)g.nextElement();
                     pd = pd.concat(o.getAddressAsString() + "\n");
                     pd = pd.concat(o.getName() + "\n");
                 }
                 dspa.endExclusive();
                 dspa.freePort();
               }
            }
         }
         catch(OneWireIOException ioe){
            ioe.printStackTrace();
            pd = new String("Nothing good here\n");
         }
         catch(OneWireException we){
            we.printStackTrace();
            pd = new String("Nothing One-Wire related found\n");
         }
         finally{ 
            if(pd != null){
               returnStack.push(pd);}
         }
      }
      return returnStack;
   }
}
