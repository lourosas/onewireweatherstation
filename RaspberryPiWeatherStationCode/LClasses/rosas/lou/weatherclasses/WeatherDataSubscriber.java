/////////////////////////////////////////////////////////////////////
/*
<GNU Stuff to go here>
*/
/////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class WeatherDataSubscriber implements TemperatureObserver,
HumidityObserver, BarometerObserver, CalculatedObserver,
ExtremeObserver{
   //By Convention, the Metric Data is stored in the first position
   //in the List
   private final int METRIC   = 0;
   //By Convention, the English Data is stored in the second
   //position in the List
   private final int ENGLISH  = 1;
   //By Convention, the Absolute Data is stored in the third
   //position in the List
   private final int ABSOLUTE = 2;
   private List<WeatherEvent> temperature;
   private List<WeatherEvent> temperatureMax;
   private List<WeatherEventL temperatureMin;
   private List<WeatherEvent> humidity;
   private List<WeatherEvent> humidityMax;
   private List<WeatherEvent> humidityMin;
   private List<WeatherEvent> pressure;
   private List<WeatherEvent> pressureMax;
   private List<WeatherEvent> pressureMin;
   private List<WeatherEvent> dewPoint;
   private List<WeatherEvent> dewPointMax;
   private List<WeatherEvent> dewPointMin;
   private List<WeatherEvent> heatIndex;
   private List<WeatherEvent> heatIndexMax;
   private List<WeatherEvent> heatIndexMin;
   
   {
      humidity       = null;
      humidityMax    = null;
      humidityMin    = null;
      pressure       = null;
      pressureMax    = null;
      pressureMin    = null;
      temperature    = null;
      temperatureMax = null;
      temperatureMin = null;
      dewPoint       = null;
      dewPointMax    = null;
      dewPointMin    = null;
      heatIndex      = null;
      heatIndexMax   = null;
      heatIndexMin   = null;   
   }
   
   //
   //Constructor of no arguments
   //
   public WeatherDataSubscriber(){}

   //
   //Grab the current dew point data
   //
   public WeatherEvent getDewpoint(String type){
      WeatherEvent event = null;
      try{
         int units = -1;
         if(type.toLowerCase().equals("english")){
            units = ENGLISH;
         }
         else if(type.toLowerCase().equals("metric")){
            units = METRIC;
         }
         else if(type.toLowerCase().equals("absolute")){
            units = ABSOLUTE;
         }
         else{
            throw new NullPointerException();
         }
         event = this.dewPoint.get(units);
      }
      catch(NullPointerException npe){}
      finally{
         return event;
      }
   }   
   
   //
   //Grab the current humidity data
   //
   public WeatherEvent getHumidity(){
      WeatherEvent event = null;
      try{
         event = this.humidity.get(METRIC);
      }
      catch(NullPointerException npe){}
      finally{
         return event;
      }
   }
   
   //
   //Grab the current pressure data
   //
   public WeatherEvent getPressure(String type){
      WeatherEvent event = null;
      try{
         int units = -1;
         if(type.toLowerCase().equals("english")){
            units = ENGLISH;
         }
         else if(type.toLowerCase().equals("metric")){
            units = METRIC;
         }
         else if(type.toLowerCase().equals("absolute")){
            units = ABSOLUTE;
         }
         else{
            throw new NullPointerException();
         }
         event = this.pressure.get(units);
      }
      catch(NullPointerException npe){}
      finally{
         return event;
      }
   }
   
   //
   //Grab the Current Temperature data
   //
   public WeatherEvent getTemperature(String type){
      WeatherEvent event = null;
      try{
         int units = -1;
         if(type.toLowerCase().equals("english")){
            units = ENGLISH;
         }
         else if(type.toLowerCase().equals("metric")){
            units = METRIC;
         }
         else if(type.toLowerCase().equals("absolute")){
            units = ABSOLUTE;
         }
         else{
            throw new NullPointerException();
         }
         event = this.temperature.get(units);
      }
      catch(NullPointerException npe){}
      finally{
         return event;
      }
   }
   
   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateDewpoint(WeatherEvent event){}

   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateDewpoint(WeatherStorage data){
      this.dewPoint = new LinkedList<WeatherEvent>();
      List<WeatherEvent> event = data.getLatestData("dewpoint");
      Iterator<WeatherEvent> it = event.iterator();
      try{
         while(it.hasNext()){
            this.dewPoint.add(it.next());
         }
      }
      catch(NullPointerException npe){
         this.dewPoint = null;
      }
   }

   //
   //Implementation of the Extreme Weather Interface
   //
   public void updateExtremes(WeatherEvent event){}

   //
   //Implementation of the Extreme Weather Interface
   //
   public void updateExtremes(WeatherStorage data){
      /*
      this.updateTemperatureExtremes(data);
      this.updateHumidityExtremes(data);
      this.updatePressureExtremes(data);
      this.updateDewPointExtremes(data);
      this.updateHeatIndexExtremes(data);
      */
   }
   
   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateHeatIndex(WeatherStorage data){
      this.heatIndex = new LinkedList<WeatherEvent>();
      List<WeatherEvent> event = data.getLatestData("heatindex");
      Iterator<WeatherEvent> it = event.iterator();
      try{
         while(it.hasNext()){
            this.heatIndex.add(it.next());
         }
      }
      catch(NullPointerException npe){
         this.heatIndex = null;
      }
   }

   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateWindChill(WeatherEvent event){}

   //
   //Implementatoin of the HumidityObserver Interface
   //
   public void updateHumidity(WeatherEvent event){}

   //
   //Implementatoin of the HumidityObserver Interface
   //
   public void updateHumidity(WeatherStorage data){
      this.humidity = new LinkedList<WeatherEvent>();
      List<WeatherEvent> event = data.getLatestData("humidity");
      try{
         Iterator<WeatherEvent> it = event.iterator();
         while(it.hasNext()){
            this.humidity.add(it.next());
         }
      }
      catch(NullPointerException npe){
         this.humidity = null;
      }
   }

   //
   //Implementation of the BarometerObserver Interface
   //
   public void updatePressure(WeatherEvent event){}

   //
   //Implementation of the BarometerObserver Interface
   //
   public void updatePressure(WeatherStorage data){
      this.pressure = new LinkedList<WeatherEvent>();
      List<WeatherEvent> event = data.getLatestData("pressure");
      try{
         Iterator<WeatherEvent> it = event.iterator();
         while(it.hasNext()){
            this.pressure.add(it.next());
         }
      }
      catch(NullPointerException npe){
         this.pressure = null;
      }
   }

   //
   //Implementation of the TemperatureObserver Interface
   //
   public void updateTemperature(WeatherEvent event){}

   //
   //Implementation of the TemperatureObserver Interface
   //
   public void updateTemperature(WeatherStorage data){
     /*
     List<WeatherEvent> event = data.getLatestData("temperature");
      try{
         this.temperature = event.get(ENGLISH);        
      }
      catch(NullPointerException npe){
         this.temperature = null;
      }
      */
      this.temperature = new LinkedList<WeatherEvent>();
      List<WeatherEvent> event = data.getLatestData("temperature");
      try{
         Iterator<WeatherEvent> it = event.iterator();
         while(it.hasNext()){
            this.temperature.add(it.next());
         }
      }
      catch(NullPointerException npe){
         this.temperature = null;
      }
   }   
}