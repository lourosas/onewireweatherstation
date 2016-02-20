/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class WeatherNetwork implements TemperatureObserver{
   private List<WeatherStation> w_s_List = null;

   //***************************Constructors************************
   /*
   Constructor of no arguments
   */
   public WeatherNetwork(){}

   //************************Public Methods*************************
   /*
   */
   public void addWeatherStation(WeatherStation ws){
      try{
         w_s_List.add(ws);
      }
      catch(NullPointerException npe){
         w_s_List = new Vector<WeatherStation>();
         w_s_List.add(ws);
      }
   }

   /*
   Just start running the WeatherStations
   */
   public void collectData(){
      try{
         Iterator<WeatherStation> i = w_s_List.iterator();
         while(i.hasNext()){
            WeatherStation ws = i.next();
            //Start the Weather Station and start collecting data
            new Thread(ws).start();
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   public void initialize(String type){
      if(type.equals("USB")){
         PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
         Hashtable hash = ps.findPorts();
         Enumeration<String> e = hash.keys();
         while(e.hasMoreElements()){
            String key = e.nextElement();
            Stack<String> s = (Stack)hash.get(key);
            Enumeration<String> sensors = s.elements();
            WeatherStation ws = new WeatherStation();
            while(sensors.hasMoreElements()){
               String name    = sensors.nextElement();
               String address = sensors.nextElement();
               ws.initializeThermometer(name, address);
            }
            ws.addTemperatureObserver(this);
            this.addWeatherStation(ws);
         }
      }
   }

   /*
   */
   public void initialize(WeatherStation ws){
      ws.addTemperatureObserver(this);
      this.addWeatherStation(ws);
   }

   /*
   Implementation of the updateTemperature() from the
   TemperatureObserver interface
   */
   public void updateTemperature(WeatherEvent event){
      if(event.getSource() instanceof Thermometer){
         //Just do this, for now
         System.out.println((Thermometer)event.getSource());
      }
   }
}
