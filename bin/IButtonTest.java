import java.lang.*;
import java.util.*;
import java.io.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class IButtonTest implements MemoryListener, MissionListener,
LogListener{
   List<Object> humidityData     = null;
   List<Object> temperatureData  = null;
   List<Object> tempTimeData     = null;
   List<Object> humidityTimeData = null;
   List<Object> heatIndexData    = null;
   List<Object> dewpointData     = null;
   Units        units;

   public static void main(String [] args){
      new IButtonTest();
   }
   
   public IButtonTest(){
      boolean isTemperature = true;
      boolean isHumidity    = true;
      IButton ib = new IButton();
      ib.addMemoryListener(this);
      ib.addMissionListener(this);
      ib.addLogListener(this);
      ib.requestMissionData(isTemperature,isHumidity,Units.ENGLISH);
      //ib.clearMemory();
      //ib.stopMission();
      //Start the mission with a sample rate of 10 min
      //ib.startMission();
      //Start the mission with a sample rate of 5 min (300 sec)
      //ib.startMission(300);
      if(isTemperature){
         this.printTemperatureLog();
      }
      if(isHumidity){
         this.printHumidityLog();
      }
      //this.saveData(true, true);
      ib.requestHeatIndexData();
      ib.requestDewpointData();
      System.out.println(ib);
   }
   
   public void onDewpointLogEvent(LogEvent le){
      System.out.println(le.getMessage());
      this.dewpointData = new LinkedList(le.getDataList());
      this.units = le.getUnits();
      try{
         Iterator<Object> dpd = this.dewpointData.iterator();
         Iterator<Object> ttd = this.tempTimeData.iterator();
         System.out.println("Dewpoint Data");
         while(dpd.hasNext()){
            String s = String.format("%.3f", (Double)dpd.next());
            System.out.print((Date)ttd.next() + ", " + s + " ");
            System.out.print(this.units + "\n");
         }
      }
      catch(NullPointerException npe){
         System.out.println("Dewpoint Log Data");
      }
   }
   
   public void onHeatIndexLogEvent(LogEvent le){
      System.out.println(le.getMessage());
      this.heatIndexData = new LinkedList(le.getDataList());
      this.units = le.getUnits();
      try{
         Iterator<Object> hid  = this.heatIndexData.iterator();
         Iterator<Object> ttd = this.tempTimeData.iterator();
         System.out.println("Heat Index Log Data");
         while(hid.hasNext()){
            String s = String.format("%.3f", (Double)hid.next());
            System.out.print((Date)ttd.next() + ", " + s + "  ");
            System.out.print(this.units + "\n");
         }
      }
      catch(NullPointerException npe){
         System.out.println("Heat Index Data Not Available");
      }
      catch(NoSuchElementException nsee){}      
   }

   
   public void onHumidityLogEvent(LogEvent le){
      System.out.println(le.getMessage());
      this.humidityData = new LinkedList(le.getDataList());
   }
   
   public void onHumidityTimeLogEvent(LogEvent le){
      System.out.println(le.getMessage());
      this.humidityTimeData = new LinkedList(le.getDataList());
   }
   
   public void onTemperatureLogEvent(LogEvent le){
      System.out.println(le.getMessage());
      this.temperatureData = new LinkedList(le.getDataList());
      this.units = le.getUnits();
   }
   
   public void onTemperatureTimeLogEvent(LogEvent le){
      System.out.println(le.getMessage());
      this.tempTimeData = new LinkedList(le.getDataList());
   }
   
   public void onMemoryEvent(MemoryEvent me){
      System.out.println(me.getMessage());
   }
   
   public void onMissionEvent(MissionEvent me){
      System.out.println(me.getMessage());
   }
   
   private void printHumidityLog(){
      try{
         Iterator<Object> hd  = this.humidityData.iterator();
         Iterator<Object> htd = this.humidityTimeData.iterator();
         System.out.println("Humidity Log Data");
         while(hd.hasNext()){
            String h = String.format("%.2f", (Double)hd.next());
            System.out.println((Date)htd.next() + ", " + h + "%");
         }
      }
      catch(NullPointerException npe){
         this.printHumidityLogData();
         this.printHumidityTimeLogData();
      }
   }
   
   private void printHumidityLogData(){
      try{
         Iterator<Object> hd = this.humidityData.iterator();
         while(hd.hasNext()){
            String h = String.format("%.2f", (Double)hd.next());
            System.out.println(h + "%");
         }
      }
      catch(NullPointerException npe){
         System.out.println("No Humidity Log Data");
      }
      catch(NoSuchElementException nsee){}
   }
   
   private void printHumidityTimeLogData(){
      try{
         Iterator<Object> htd = this.humidityTimeData.iterator();
         while(htd.hasNext()){
            System.out.println((Double)htd.next());
         }
      }
      catch(NullPointerException npe){
         System.out.println("No Humidity Time Log Data");
      }
      catch(NoSuchElementException nsee){}
   }
   
   private void printTemperatureLog(){
      try{
         Iterator<Object> td  = this.temperatureData.iterator();
         Iterator<Object> ttd = this.tempTimeData.iterator();
         System.out.println("Temperature Log Data");
         while(td.hasNext()){
            String s = String.format("%.3f", (Double)td.next());
            System.out.print((Date)ttd.next() + ", " + s + "  ");
            System.out.print(this.units + "\n");
         }
      }
      catch(NullPointerException npe){
         this.printTemperatureLogData();
         this.printTemperatureTimeLogData();
      }
      catch(NoSuchElementException nsee){}
   }
   
   private void printTemperatureLogData(){
      try{
         Iterator<Object> td  = this.temperatureData.iterator();
         while(td.hasNext()){
            String s = String.format("%.3f", (Double)td.next());
            System.out.println(s + " " + this.units);
         }
      }
      catch(NullPointerException npe){
         System.out.println("No Temperature Log Data");
      }
      catch(NoSuchElementException nsee){}
   }
   
   private void printTemperatureTimeLogData(){
      try{
         Iterator<Object> ttd = this.tempTimeData.iterator();
         while(ttd.hasNext()){
            System.out.println((Date)ttd.next());
         }
      }
      catch(NullPointerException npe){
         System.out.println("No Temperature Time Log Data");
      }
   }
   
   private void saveData(boolean isTemperature, boolean isHumidity){
      FileWriter fw  = null;
      PrintWriter pw = null;
      try{
         Iterator<Object> td  = null;
         Iterator<Object> ttd = null;
         Iterator<Object> hd  = null;
         Iterator<Object> htd = null;
         //ps = Print String
         String ps;
         fw = new FileWriter("out.txt", false);
         pw = new PrintWriter(fw, true);
         pw.println("Environmental Data");
         if(isTemperature && isHumidity){
            pw.print("Date\t\t\tTemperature, Humidity\n");
            pw.println("-----------------------------------------");
            td  = this.temperatureData.iterator();
            ttd = this.tempTimeData.iterator();
            hd  = this.humidityData.iterator();
            while(ttd.hasNext()){
               String s = String.format("%.3f", (Double)td.next());
               String h = String.format("%.2f", (Double)hd.next());
               pw.print((Date)ttd.next() + ", ");
               pw.print(s + "  " + this.units + ", " + h + "%\n");
            }
         }
         else if(isTemperature){
            pw.print("Date\t\t\t\tTemperature\n");
            pw.println("-----------------------------------------");
            td  = this.temperatureData.iterator();
            ttd = this.tempTimeData.iterator();
            while(ttd.hasNext()){
               String s = String.format("%.3f", (Double)td.next());
               pw.print((Date)ttd.next() + ", " + s + "  ");
               pw.print(this.units + "\n");
            }
         }
         else if(isHumidity){
            pw.print("Date\t\t\t\tHumidity\n");
            pw.println("----------------------------------------");
            htd = this.humidityTimeData.iterator();
            hd  = this.humidityData.iterator();
            while(htd.hasNext()){
               String h = String.format("%.2f", (Double)hd.next());
               pw.print((Date)htd.next() + ", " + h + "%\n");
            }
         }
         fw.close();
         pw.close();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
}