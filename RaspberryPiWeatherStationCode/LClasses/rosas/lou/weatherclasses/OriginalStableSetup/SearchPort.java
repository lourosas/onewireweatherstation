//*******************************************************************
//SearchPort Class
//A Class by Lou Rosas
//*******************************************************************
package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class SearchPort extends Observable implements Runnable{
   private static final String ADAPTER_STRING = "DS9097U";
   private String currentPort, searchResults;
   //*************************Public Methods*************************

   //
   //Constructor of no arguments
   //
   public SearchPort(){
      this.setCurrentPort(new String());
   }

   //
   //Constructor taking the current port name
   //
   public SearchPort(String port){
      this.setCurrentPort(port);
   }

   //
   //Get the current port name
   //
   public String getCurrentPort(){ return this.currentPort; }

   //
   //Get the One Wire Results of searching a particular report
   //
   public String getPortSearchResults(){
      return this.searchResults;
   }
   
   //
   //Grab the Port Data
   //
   public String grabPortData(){
      String threadString = new String();
      try{
         DSPortAdapter a = OneWireAccessProvider.getAdapter(
                                    ADAPTER_STRING,
                                    this.currentPort);

         String as = new String();
         a.beginExclusive(true);

         a.setSearchAllDevices();
         a.targetAllFamilies();
         a.setSpeed(DSPortAdapter.SPEED_REGULAR);

         Enumeration e = a.getAllDeviceContainers();
         while(e.hasMoreElements()){
            OneWireContainer owc = (OneWireContainer)e.nextElement();
            //The Address String for the current OneWireDevice
            as = as.concat(owc.getAddressAsString() + "\n");
            as = as.concat(owc.getName() + "\n");
         }

         a.endExclusive();
         a.freePort();
         threadString = new String(as);
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
         threadString = new String("Nothing good here");
      }
      catch(OneWireException we){
         threadString =
                      new String("Nothing One Wire related found\n");
      }
      finally{
         threadString = threadString;
      }
      return threadString;
   }

   //
   //Implementation of the run() method as part of the Runnable
   //interface
   //
   public void run(){
      String threadString = new String();
      try{
         DSPortAdapter a = OneWireAccessProvider.getAdapter(
                                    ADAPTER_STRING,
                                    this.currentPort);
         String an = new String("Adapter:  "); //Adapter name
         an = an.concat(a.getAdapterName() + "\n");
         String pn = new String("Port:  "); //Port name
         pn = pn.concat(a.getPortName() + "\n\n");
         String as = new String();

         a.beginExclusive(true);

         a.setSearchAllDevices();
         a.targetAllFamilies();
         a.setSpeed(DSPortAdapter.SPEED_REGULAR);

         Enumeration e = a.getAllDeviceContainers();
         while(e.hasMoreElements()){
            OneWireContainer owc = (OneWireContainer)e.nextElement();
            //The Address String for the current OneWireDevice
            as = as.concat(owc.getAddressAsString() + "\n");
            as = as.concat(owc.getName() + "\n");
         }

         a.endExclusive();
         a.freePort();
         threadString = new String(an + pn + as);
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
      }
      catch(OneWireException we){
         threadString =
                      new String("Nothing One Wire related found\n");
      }
      finally{
         this.setPortSearchResults(threadString + "\nDone\n\n");
         this.setChanged();
         this.notifyObservers();
         this.clearChanged();
      }
   }

   //
   //Set the port name to search
   //
   public void setCurrentPort(String port){
      this.currentPort = new String(port);
   }

   //************************Private Methods*************************

   //
   //Set up the Search Results
   //
   private void setPortSearchResults(String results){
      this.searchResults = new String(results);
   }
}
