/********************************************************************
********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class PortFinder extends Observable implements Runnable{
   private static final String ADAPTER_STRING = "DS9094R";
   private String adapterName, portName;
   private DSPortAdapter currentAdapter;
   private Hashtable usbHash;
   
   /**************************Public Methods************************/
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public PortFinder(){
      this.findDefaultAdapter();
   }


   /*****************************************************************
   Get the current adapter
   *****************************************************************/
   public DSPortAdapter getAdapter(){
      return this.currentAdapter;
   }

   /*****************************************************************
   Get the current adapter name
   *****************************************************************/
   public String getAdapterName(){
      return this.adapterName;
   }

   /*****************************************************************
   Get the current port name
   *****************************************************************/
   public String getAdapterPortName(){
      return this.portName;
   }

   /*****************************************************************
   *****************************************************************/
   public Hashtable getAdaptersWithUSBPorts(){
      return this.usbHash;
   }

   /*****************************************************************
   Grab the port data for ONLY the default port
   *****************************************************************/
   public String grabDefaultPortData(){
      String portString = new String();
      try{
         String _portName = this.getAdapterPortName();
         String as = new String();
	 this.currentAdapter.selectPort(_portName);
         this.currentAdapter.beginExclusive(true);
         this.currentAdapter.setSearchAllDevices();
         this.currentAdapter.targetAllFamilies();
         this.currentAdapter.setSpeed(DSPortAdapter.SPEED_REGULAR);

         as = as.concat(this.currentAdapter.getAdapterName() + "\n");
	 as = as.concat(_portName + "\n");
         Enumeration e=this.currentAdapter.getAllDeviceContainers();
         while(e.hasMoreElements()){
            OneWireContainer owc = (OneWireContainer)e.nextElement();
            //The Address String for the current OneWireDevice
            as = as.concat(owc.getAddressAsString() + "\n");
            as = as.concat(owc.getName() + "\n");
         }
         this.currentAdapter.endExclusive();
         this.currentAdapter.freePort();
         portString = as;
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
	 portString = new String("Nothing good here");
      }
      catch(OneWireException we){
         we.printStackTrace();
         portString = new String("Nothing One-Wire related found");
      }
      finally{portString = portString;}
      return portString;
   }

   /*****************************************************************
   *****************************************************************/
   public Stack grabUSBPortData(){
      Stack portStack   = new Stack();
      String portString = new String();
      this.findAdaptersWithUSBPorts();
      Hashtable hash = this.getAdaptersWithUSBPorts();
      if(!hash.isEmpty()){
         Enumeration keys = hash.keys();
         while(keys.hasMoreElements()){
            String as = (String)keys.nextElement(); //Adapter String
            //Get the Stack Value in the associated with the
            //Current Key
            Stack usbs = (Stack)hash.get(as);
            while(!usbs.empty()){
               portString = new String();
               String port = new String((String)usbs.pop());
               try{
                  DSPortAdapter a =
                           OneWireAccessProvider.getAdapter(as,port);
                  a.beginExclusive(true);
                  a.setSearchAllDevices();
                  a.targetAllFamilies();
                  a.setSpeed(DSPortAdapter.SPEED_REGULAR);
                  portString = portString.concat(as + "\n");
                  portString = portString.concat(port + "\n");
                  Enumeration e = a.getAllDeviceContainers();
                  while(e.hasMoreElements()){
                     OneWireContainer owc = 
                                   (OneWireContainer)e.nextElement();
                     portString = portString.concat(
                                    owc.getAddressAsString() + "\n");
                     portString = portString.concat(
                                               owc.getName() + "\n");
                  }
                  a.endExclusive();
                  a.freePort();
                  portStack.push(portString);
               }
               catch(OneWireIOException ioe){
                  portString = new String("No IO Connection\n");
               }
               catch(OneWireException we){
                  portString=new String("Nothing One Wire found\n");
               }
            }
         }
      }
      return portStack;
   }

   /*****************************************************************
   Print all the possible adapters connected to the computer
   *****************************************************************/
   public void printAllAdapters(){
      //Enumerate through all possible adapters and print out
      //information
      Enumeration e = OneWireAccessProvider.enumerateAllAdapters();
      while(e.hasMoreElements()){
         DSPortAdapter dspa = (DSPortAdapter)e.nextElement();
         System.out.println(dspa.getAdapterName());
	 Enumeration p = dspa.getPortNames();
	 while(p.hasMoreElements()){
            System.out.println((String)p.nextElement());
	 }
      }
   }

   /*****************************************************************
   Implementation of the run() method as part of the Runnable
   interface
   *****************************************************************/
   public void run(){}

   /*****************************************************************
   Implementation of the toString() method for this class
   *****************************************************************/
   public String toString(){
      DSPortAdapter dspa = this.getAdapter();
      String rs = new String();
      rs = rs.concat(this.getAdapterName() + "\n");
      rs = rs.concat(this.getAdapterPortName() + "\n");
      rs = rs.concat(dspa.getPortTypeDescription() + "\n");
      rs = rs.concat(dspa.getClassVersion());
      return rs;
   }

   /*************************Private Methods************************/
   /*****************************************************************
   Find the current Default Adapter:  this is to see if the current
   DS9094R adapter can be found if connected to the USB port, since
   the RXTX Comm Port Package API does not have support for the USB
   *****************************************************************/
   private void findDefaultAdapter(){
      try{
         this.currentAdapter = 
                           OneWireAccessProvider.getDefaultAdapter();
         this.adapterName = this.currentAdapter.getAdapterName();
         this.portName = this.currentAdapter.getPortName();
	 this.currentAdapter.freePort();
      }
      catch(OneWireIOException ioe){ioe.printStackTrace();}
      catch(OneWireException we){we.printStackTrace();}
   }

   /*****************************************************************
   Find all the adapters containing USB ports, put them in the
   appropriate Hashtable for use by other files/applications.
   *****************************************************************/
   private void findAdaptersWithUSBPorts(){
      this.usbHash = new Hashtable();

      //Enumerate through all the possible adapters connected
      Enumeration e = OneWireAccessProvider.enumerateAllAdapters();
      while(e.hasMoreElements()){
         Stack portStack = new Stack();
         DSPortAdapter dspa = (DSPortAdapter)e.nextElement();
         Enumeration p = dspa.getPortNames();
         while(p.hasMoreElements()){
            String portName = (String)p.nextElement();
            if(portName.startsWith("USB")){
               portStack.push(portName);
            }
         }
         this.usbHash.put(dspa.getAdapterName(), portStack);
      }
   }
}
