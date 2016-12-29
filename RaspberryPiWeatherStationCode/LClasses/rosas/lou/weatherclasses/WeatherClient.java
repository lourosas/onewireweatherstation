/********************************************************************
<GNU Stuff to go here>
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import rosas.lou.IOObserver;
import rosas.lou.weatherclasses.*;

public class WeatherClient{
   private final static int PORT    = 19000;
   private final static int TIMEOUT = 20000;

   private DatagramSocket socket;
   private List<String>   currentTemperatureData;
   private List<WeatherClientObserver> observers;
   private List<IOObserver> ioobservers;
   private byte[] addr;
   private int month;
   private int day;
   private int year;

   {
      socket                 = null;
      currentTemperatureData = null;
      observers              = null;
      ioobservers            = null;
      addr      = new byte[]{(byte)192,(byte)168,(byte)1,(byte)115};
      month                  = 0;
      day                    = 0;
      year                   = 0;
   }

   /**
   **/
   public WeatherClient(){
      this(null, null);
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
         Calendar cal = Calendar.getInstance();
         this.month = cal.get(Calendar.MONTH);
         this.day   = cal.get(Calendar.DAY_OF_MONTH);
         this.year  = cal.get(Calendar.YEAR);
      }
   }

   /**
   **/
   public WeatherClient(WeatherClientObserver wco, IOObserver ioo){
      this(wco);
      try{
         this.ioobservers.add(ioo);
      }
      catch(NullPointerException npe){
         this.ioobservers = new LinkedList<IOObserver>();
         this.ioobservers.add(ioo);
      }
   }

   /**
   **/
   public void addIOObserver(IOObserver ioo){
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
   public void requestDewpointData(ViewState state){
      this.updateMissionData();
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
      if(state.units == Units.METRIC){ command += "dewptc "; }
      if(state.units == Units.ENGLISH){ command += "dewptf "; }
      if(state.units == Units.ABSOLUTE){ command += "dewptk "; }
      command += "FROM dewpointdata WHERE " + where;
      this.requestDewpointData(command);
   }

   /**
   **/
   public void requestDewpointData(String message){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> dewpointData = new LinkedList();
      try{
         this.socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!! 
         this.socket.setSoTimeout(TIMEOUT);
         byte data[] = message.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this.addr);
         byte[] receiveData = new byte[128];
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
            dewpointData.add(output.trim());
            receivePacket.setData(new byte[128]);
         }
      }
      catch(SocketTimeoutException ste){
         this.publishDewpointRequestTimeout();
      }
      catch(Exception e){
         //TBD...but for now, print the Exception
         e.printStackTrace();
         dewpointData = null;
      }
      finally{
         this.publishDewpointData(dewpointData);
      }
   }

   /**
   **/
   public void requestHeatIndexData(ViewState state){
      this.updateMissionData();
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
      if(state.units == Units.METRIC){
         command += "heatindexc ";
      }
      else if(state.units == Units.ENGLISH){
         command += "heatindexf ";
      }
      else if(state.units == Units.ABSOLUTE){
         command += "heatindexk ";
      }
      command += "FROM heatindexdata WHERE " + where;
      this.requestHeatIndexData(command);
   }

   /**
   **/
   public void requestHeatIndexData(String message){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> heatIndexData = new LinkedList();
      System.out.println(message);
      try{
         this.socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!! 
         this.socket.setSoTimeout(TIMEOUT);
         byte data[] = message.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this.addr);
         byte[] receiveData = new byte[128];
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
            System.out.println(output.trim());
            heatIndexData.add(output.trim());
            receivePacket.setData(new byte[128]);
         }
      }
      catch(SocketTimeoutException ste){
         this.publishHeatIndexRequestTimeout();
      }
      catch(Exception e){
         //TBD...but for now, print the Exception
         e.printStackTrace();
         heatIndexData = null;
      }
      finally{
         this.publishHeatIndexData(heatIndexData);
      }
   }

   /**
   **/
   public void requestHumidityData(ViewState state){
      this.updateMissionData();
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
      command += "time, humidity FROM humiditydata WHERE " + where;
      this.requestHumidityData(command);
   }

   /**
   **/
   public void requestHumidityData(String message){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> humidityData     = new LinkedList();
      System.out.println(message);
      try{
         this.socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!! 
         this.socket.setSoTimeout(TIMEOUT);
         byte data[] = message.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this.addr);
         byte[] receiveData = new byte[128];
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
            System.out.println(output.trim());
            humidityData.add(output.trim());
            receivePacket.setData(new byte[128]);
         }
      }
      catch(SocketTimeoutException ste){
         this.publishHumidityRequestTimeout();
      }
      catch(Exception e){
         //TBD...but for now, print the Exception
         e.printStackTrace();
         humidityData = null;
      }
      finally{
         this.publishHumidityData(humidityData);
      }      
   }

   /**
   **/
   public void requestMissionData(String message){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> missionData     = new LinkedList();
      try{
         this.socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!! 
         this.socket.setSoTimeout(TIMEOUT);
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
            //System.out.println(output);
            missionData.add(output);
            receivePacket.setData(new byte[64]);
         }
      }
      catch(SocketTimeoutException ste){
         this.publishMissionTimeout();
      }
      catch(Exception e){
         //TBD...but for now, print the Exception!!!
         e.printStackTrace();
         missionData = null;
      }
      finally{
         this.publishMissionData(missionData);
      }
   }

   /**
   **/
   public void requestPressureData(ViewState state){
      this.updateMissionData();
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
      if(state.units ==      Units.METRIC){ command += "mmHg "; }
      else if(state.units == Units.ENGLISH){ command += "inHg "; }
      else if(state.units == Units.ABSOLUTE){command +="mB ";}
      command += "FROM pressuredata WHERE " + where;
      this.requestPressureData(command);
   }

   /**
   **/
   public void requestPressureData(String message){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> pressureData = new LinkedList();
      System.out.println(message);
      try{
         this.socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!! 
         this.socket.setSoTimeout(TIMEOUT);
         byte data[] = message.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this.addr);
         byte[] receiveData = new byte[128];
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
            pressureData.add(output.trim());
            receivePacket.setData(new byte[128]);
         }
      }
      catch(SocketTimeoutException ste){
         this.publishPressureRequestTimeout();
      }
      catch(Exception e){
         //TBD...see all the OTHER notes
         e.printStackTrace();
         pressureData = null;
      }
      finally{
         this.publishPressureData(pressureData);
      }
   }

   /**
   **/
   public void requestTemperatureData(ViewState state){
      this.updateMissionData();
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
      System.out.println(message);
      try{
         this.socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!! 
         this.socket.setSoTimeout(TIMEOUT);
         byte data[] = message.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this.addr);
         byte[] receiveData = new byte[128];
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
            System.out.println(output.trim());
            temperatureData.add(output.trim());
            receivePacket.setData(new byte[128]);
         }
      }
      catch(SocketTimeoutException ste){
         this.publishTemperatureRequestTimeout();
      }
      catch(Exception e){
         //TBD...but for now, print the Exception
         e.printStackTrace();
         temperatureData = null;
      }
      finally{
         this.publishTemperatureData(temperatureData);
      }
   }

   /**
   Going to make this simple:  nothing real complex about this part
   **/
   public void saveTemperatureData(File file){
      FileWriter  fileWriter  = null;
      PrintWriter printWriter = null;
      try{
         fileWriter  = new FileWriter(file, true);
         printWriter = new PrintWriter(fileWriter, true);
         Iterator<String> it=this.currentTemperatureData.iterator();
         while(it.hasNext()){
            String line = it.next();
            printWriter.println(line);
         }
      }
      //somehow, need to alert the clients of "bad things"...
      catch(NullPointerException npe){
         this.publishTemperatureSaveException(file, npe);
      }
      catch(IOException ioe){
         this.publishTemperatureSaveException(file, ioe);
      }
      finally{
         try{
            fileWriter.close();
            printWriter.close();
         }
         catch(IOException ioe){} //nothing to really do
      }
   }

   //////////////////////Private Methods/////////////////////////////
   /**
   **/
   private void publishDewpointData(List<String> dewpointData){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco = it.next();
         wco.updateDewpointData(dewpointData);
      }
   }

   /**
   **/
   private void publishDewpointRequestTimeout(){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco=(WeatherClientObserver)it.next();
         wco.alertDewpointTimeout();
      }
   }

   /**
   **/
   private void publishHeatIndexData(List<String> heatIndexData){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco = it.next();
         wco.updateHeatIndexData(heatIndexData);
      }
   }

   /**
   **/
   private void publishHeatIndexRequestTimeout(){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco=(WeatherClientObserver)it.next();
         wco.alertHeatIndexTimeout();
      }
   }

   /**
   **/
   private void publishHumidityData(List<String> humidityData){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco = it.next();
         wco.updateHumidityData(humidityData);
      }
   }

   /**
   **/
   private void publishHumidityRequestTimeout(){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco=(WeatherClientObserver)it.next();
         wco.alertHumidityTimeout();
      }
   }

   /**
   **/
   private void publishPressureData(List<String> pressureData){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco = it.next();
         wco.updatePressureData(pressureData);
      }
   }

   /**
   **/
   private void publishPressureRequestTimeout(){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco=(WeatherClientObserver)it.next();
         wco.alertPressureTimeout();
      }
   }


   /**
   **/
   private void publishMissionData(List<String> missionData){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco = it.next();
         wco.updateMissionData(missionData);
      }
   }

   /**
   **/
   private void publishMissionTimeout(){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco=(WeatherClientObserver)it.next();
         wco.alertMissionTimeout();
      }
   }

   /**
   **/
   private void publishTemperatureData(List<String> tempData){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      this.currentTemperatureData        = tempData;
      while(it.hasNext()){
         WeatherClientObserver wco=(WeatherClientObserver)it.next();
         wco.updateTemperatureData(tempData);
      }
   }

   /**
   **/
   private void publishTemperatureRequestTimeout(){
      Iterator<WeatherClientObserver> it = this.observers.iterator();
      while(it.hasNext()){
         WeatherClientObserver wco=(WeatherClientObserver)it.next();
         wco.alertTemperatureTimeout();
      }
   }

   /**
   **/
   private void publishTemperatureSaveException(File f, Exception e){
      Iterator<IOObserver> it = this.ioobservers.iterator();
      while(it.hasNext()){
         IOObserver ioo = (IOObserver)it.next();
         if(e instanceof NullPointerException){
            ioo.alertNoDataError(f);
         }
         else if(e instanceof IOException){
            ioo.alertIOExceptionError(f);
         }
      }
   }

   /**
   **/
   private void updateMissionData(){
      Calendar current = Calendar.getInstance();
      int currentMonth = current.get(Calendar.MONTH);
      int currentDay   = current.get(Calendar.DATE);
      int currentYear  = current.get(Calendar.YEAR);
      if(currentMonth != this.month ||
         currentDay   != this.day   ||
         currentYear  != this.year){
         //Update the mission data for all the Observers
         //(Subscribers)
         this.requestMissionData();
         this.month = currentMonth;
         this.day   = currentDay;
         this.year  = currentYear;
      }
   }
}
