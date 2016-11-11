/********************************************************************
<GNU Stuff to go here>
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import rosas.lou.weatherclasses.*;

public class WeatherClient{
   private int localPort;
   private DatagramSocket socket;
   private List<WeatherClientObserver> observers;
   private byte[] addr;

   {
      socket    = null;
      observers = null;
      addr      = new byte[]{(byte)192,(byte)168,(byte)1,(byte)112};
   }

   private static int port;
   /**
   **/
   public WeatherClient(){
      this(null);
   }

   /**
   **/
   public WeatherClient(WeatherClientObserver wco){
      try{
         this.observers.add(wco);
      }
      catch(NullPointerException npe){
         this.observers = new LinkedList<WeatherClientObserver>();
         this.observers.add(wco);
      }
      finally{
         setPortNumber();
      }
   }
   
   /**
   **/
   public void addObserver(WeatherClientObserver wco){
      try{
         this.observers.add(wco);
      }
      catch(NullPointerException npe){
         this.observers = new LinkedList<WeatherClientObserver>();
         this.observers.add(wco);
      }
   }

   //////////////////////Private Methods/////////////////////////////
   /**
   **/
   private static void setPortNumber(){
      ++WeatherClient.port;
      System.out.println(WeatherClient.port);
   }
}
