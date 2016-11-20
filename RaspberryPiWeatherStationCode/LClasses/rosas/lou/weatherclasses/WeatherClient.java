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
   private final static int PORT = 19000;

   private DatagramSocket socket;
   private List<WeatherClientObserver> observers;
   private byte[] addr;

   {
      socket    = null;
      observers = null;
      addr      = new byte[]{(byte)192,(byte)168,(byte)1,(byte)112};
   }

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

   /**
   **/
   public void requestMissionData(){
      String message = new String("SELECT * FROM missiondata");
      this.requestMissionData(message);
   }

   /**
   **/
   public void requestMissionData(String message){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> missionData     = new LinkedList();
      try{
         this.socket = new DatagramSocket();
         byte data[] = message.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this.addr);
         byte[] receiveData = new byte[64];
         sendPacket = new DatagramPacket(data,
                                         data.length,
                                         iNetAddr,
                                         PORT);
         this.socket.send(sendPacket);
         receivePacket =
                 new DatagramPacket(receiveData, receiveData.length);
         this.socket.receive(receivePacket);
         String output = new String(receivePacket.getData());
         int size = Integer.parseInt(output.trim());
         for(int i = 0; i < size; i++){
            this.socket.receive(receivePacket);
            output = new String(receivePacket.getData());
            System.out.println(output);
            missionData.add(output);
            receivePacket.setData(new byte[64]);
         }
      }
      catch(Exception e){
         //TBD...but for now, print the Exception!!!
         e.printStackTrace();
         missionData = null;
      }
      this.publishMissionData(missionData);
   }

   /**
   **/
   public void requestTemperatureData(ViewState state){
      String command = "SELECT ";
      String where   = new String();
      if(!state.month.isEmpty()){
         command += "month, ";
         where += "month = '" + state.month + "'";
      }
      if(!state.day.isEmpty()){
         command += "day, ";
         if(!state.month.isEmpty()){ where += " AND "; }
         where += "day = '" + state.day + "'";
      }
      if(!state.year.isEmpty()){
         command += "year, ";
         if(!state.month.isEmpty() || !state.day.isEmpty()){
            where += " AND ";
         }
         where += "year = '" + state.year + "'";
      }
      command += "time, ";
      if(state.units == Units.METRIC){ command += "tempc "; }
      if(state.units == Units.ENGLISH){ command += "tempf "; }
      if(state.units == Units.ABSOLUTE){ command += "tempk "; }
      command += "FROM temperaturedata WHERE " + where;
      this.requestTemperatureData(command);
   }

   /**
   **/
   public void requestTemperatureData(String message){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> temperatureData = new LinkedList();
      try{
         this.socket = new DatagramSocket();
         byte data[] = message.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this.addr);
         byte[] receiveData = new byte[64];
         sendPacket = new DatagramPacket(data,
                                         data.length,
                                         iNetAddr,
                                         PORT);
         this.socket.send(sendPacket);
         receivePacket =
                 new DatagramPacket(receiveData, receiveData.length);
         this.socket.receive(receivePacket);
         String output = new String(receivePacket.getData());
         int size = Integer.parseInt(output.trim());
         for(int i = 0; i < size; i++){
            this.socket.receive(receivePacket);
            output = new String(receivePacket.getData());
            System.out.println(output);
            temperatureData.add(output);
            receivePacket.setData(new byte[64]);
         }
      }
      catch(Exception e){
         //TBD...but for now, print the Exception
         e.printStackTrace();
         temperatureData = null;
      }
      //this.publishTemperatureData(temperatureData);
   }

   //////////////////////Private Methods/////////////////////////////
   /**
   **/
   private void publishMissionData(List<String> missionData){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco = it.next();
         wco.updateMissionData(missionData);
      }
   }
}
