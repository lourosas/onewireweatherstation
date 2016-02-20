/********************************************************************
The first attempt to do this weather thing correctly:  by setting up
a WeatherTest Class

By:  Lou Rosas
********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import java.io.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class WeatherTest{
   //Public data types (This is stupid and needs redoing)
   private static final String VERSION = "WeatherTest 0.1";
   private static final String ONE_WIRE_SERIAL_PORT = "COM4";
   //This one might be the "Fall Back" Adapter Type
   //public static final String ADAPTER_TYPE = "DS9097";
   //I think this will work just fine:  might need to adjust
   private static final String ADAPTER_TYPE = "DS9097U";
   
   private DSPortAdapter adapter;
   
   //*************************Public Methods*************************
   //****************************************************************
   //Constructor of no arguments
   //****************************************************************
   public WeatherTest(){
      Stack adapterStack = new Stack();
      Enumeration adapters =
                        OneWireAccessProvider.enumerateAllAdapters();
      while(adapters.hasMoreElements()){
         //Adapter first initial
         String afi = new String("java sucks");
         DSPortAdapter a = (DSPortAdapter)adapters.nextElement();
         String currentAdapter = a.getAdapterName();
         try{ afi = currentAdapter.substring(0,1); }
         catch(IndexOutOfBoundsException e){e.printStackTrace();}
         if(afi.equals("D")){
            //This is the one to use, for now, print out info
            adapterStack.push(a);
         }
      }
      for(int i = 0; i < adapterStack.size(); i++){
         try{
            DSPortAdapter a = (DSPortAdapter)adapterStack.pop();
            Enumeration p = a.getPortNames();
            String adapterName = a.getAdapterName();
            System.out.println("\nAdapter Name:  " + adapterName);
            System.out.println("==================================");
            while(p.hasMoreElements()){
               String pn = (String)p.nextElement();
               System.out.println("Port Name: " + pn);
               try{
                  DSPortAdapter adapter = 
                     OneWireAccessProvider.getAdapter(adapterName,
                                                                 pn);
                  //this.resetBus(adapter);
                  this.cycleSystem(adapter);
               }
               catch(OneWireIOException ioe){ ioe.printStackTrace();}
               catch(OneWireException we){
                  System.out.println("OneWireException");
               }
               finally{ System.out.println(); }
            }
         }
         catch(EmptyStackException e){e.printStackTrace();}
      }
   }
   
   //****************************************************************
   //Get the current version
   //****************************************************************
   public static String getVersion(){
      return VERSION;
   }
   
   //*****************************Private Methods********************
   //****************************************************************
   //Cycle through the entire system:  mainly just sleeping, and then
   //reset the bus every second
   //****************************************************************
   private void cycleSystem(DSPortAdapter adapter){
      boolean quit = false;
      Calendar cal = Calendar.getInstance();
      int minute, lastMinute = -99; //Initialize
      int hour, seconds;
      long currentMillis;
      long initialMillis = System.currentTimeMillis();
      cal.setTimeInMillis(initialMillis);
      while(!quit){
         try{
            this.resetBus(adapter);
            Thread.sleep(10000);  //Sleep for 10-seconds
            currentMillis = System.currentTimeMillis();
            cal.setTimeInMillis(currentMillis);
            minute = cal.get(Calendar.MINUTE);
            if(minute != lastMinute){
               hour = cal.get(Calendar.HOUR_OF_DAY);
               seconds = cal.get(Calendar.SECOND);
               System.out.println(hour + ":" + minute + ":" + seconds);
               lastMinute = minute;
            }
            //Automate:  let run for 1 hour
            if(currentMillis - initialMillis >= 3600000 ){
               minute = cal.get(Calendar.MINUTE);
               hour = cal.get(Calendar.HOUR_OF_DAY);
               seconds = cal.get(Calendar.SECOND);
               System.out.println(hour + ":" + minute + ":" + seconds);
               quit = true;
            }
         }
         catch(InterruptedException e){
            e.printStackTrace();
            System.exit(0);
         }
      }
      System.out.println("\n\nQuiting after 1 hour\n\n");
      try{
         adapter.freePort();
      }
      catch(OneWireException e){e.printStackTrace();}
   }
   
   //****************************************************************
   // Reset the bus
   //****************************************************************
   private void resetBus(DSPortAdapter adapter){
      try{
         //int value = this.adapter.reset();
         int value = adapter.reset();
         System.out.println("Reset Value:  " + value);
      }
      catch(OneWireIOException ioe){
         System.out.println("One Wire IO Exeption Occured");     
      }
      catch(OneWireException e){
         System.out.println("One Wire Exception Occured");      
      }
   }
}
