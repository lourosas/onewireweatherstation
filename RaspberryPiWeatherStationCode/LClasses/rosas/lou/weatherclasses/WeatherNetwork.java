//******************************************************************
//Weather Network Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class WeatherNetwork implements TemperatureObserver,
HumidityObserver, BarometerObserver, CalculatedObserver,
TimeObserver{
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
      ws.addHumidityObserver(this);
      ws.addBarometerObserver(this);
      ws.addCalculatedObserver(this);
      ws.addTimeObserver(this);
      this.addWeatherStation(ws);
   }
   
   /*
   Implementation of the updateDewpoint() from the
   CalculatedObserver interface
   */
   public void updateDewpoint(WeatherEvent event){
      if(event.getPropertyName().equals("Dewpoint")){
         System.out.print("Dewpoint:  ");
         if(event.getValue() > Thermometer.DEFAULTTEMP){
            String s = String.format("%.3f", event.getValue());
            System.out.print(s + ", " + event.getUnits() + "\n");
         }
         else{
            System.out.print("cannot be calculated at this time\n");
         }
      }
   }
   
   /*
   Implementation of the updateHeatIndex() from the
   CalculatedObserver interface
   */
   public void updateHeatIndex(WeatherEvent event){
     if(event.getPropertyName().equals("HeatIndex")){
        System.out.print("Heat Index:  ");
        if(event.getValue() > Thermometer.DEFAULTTEMP){
           String s = String.format("%.3f", event.getValue());
           System.out.print(s + ", " + event.getUnits() + "\n");
        }
        else{
           Thermometer t = (Thermometer)event.getSource();
           double temp = t.getTemperature();
           if(temp > Thermometer.DEFAULTTEMP){
              System.out.print(""+temp +", "+event.getUnits()+"\n");
              System.out.print("Currently not hot enough to ");
              System.out.print("accurately calculate ");
              System.out.print("the Heat Index at this time\n");
           }
           else{
              System.out.print("cannot be calculated at this time");
              System.out.println();
           }
        }
     }
   }
   
   /*
   Implementation of the updateHumidity() from the
   HumidityObserver interface
   */
   public void updateHumidity(WeatherEvent event){
      if(event.getPropertyName().equals("Hygrometer")){
         System.out.println((Hygrometer)event.getSource());
      }
   }
   
   /*
   Implementation of the updatePressure() from the
   BarometerObserver interface
   */
   public void updatePressure(WeatherEvent event){
      if(event.getPropertyName().equals("Barometer")){
         System.out.println(event.getSource());
      }
   }
   
   /*
   Implementation of the updateTemperature() from the
   TemperatureObserver interface
   */
   public void updateTemperature(WeatherEvent event){
      if(event.getPropertyName().equals("Thermometer")){
         System.out.println((Thermometer)event.getSource());
      }
   }
   
   /*
   Implementation of the updataTime() from the TimeObserver
   interface
   */
   public void updateTime(){}

   /*
   Implementation of the updataTime() from the TimeObserver
   interface
   */
   public void updateTime(String formatedTime){
      System.out.println(formatedTime);
   }
   
   /*
   Implementation of the updataTime() from the TimeObserver
   interface
   */
   public void updateTime(String mo, String day, String year){}

   /*
   Implementation of the updataTime() from the TimeObserver
   interface
   */
   public void updateTime
   (
      String year,
      String month,
      String day,
      String hour,
      String min,
      String sec
   ){}
   
   /*
   Implementation of the updateWindChill() from the 
   CalculatedObserver interface
   */
   public void updateWindChill(WeatherEvent event){}


}
