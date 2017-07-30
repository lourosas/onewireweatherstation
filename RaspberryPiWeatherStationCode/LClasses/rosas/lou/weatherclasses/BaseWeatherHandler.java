/////////////////////////////////////////////////////////////////////
/*
<GNU Stuff to go here>
*/
/////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import rosas.lou.weatherclasses.*;

public class BaseWeatherHandler implements HttpHandler{
   private WeatherDataSubscriber wds;
   
   {
      wds = null;
   }
   
   //
   //
   //
   public BaseWeatherHandler(){
      this.wds = new WeatherDataSubscriber();
   }
   
   //
   //
   //
   public void handle(HttpExchange exchange){}
   
   //
   //
   //
   public WeatherDataSubscriber weatherDataSubscriber(){
      return this.wds;
   }
   
   //**************Protected Methods*********************************
   //
   //
   //
   protected double dewpoint(String type){
      double temp        = Thermometer.DEFAULTTEMP;
      WeatherEvent event = null;
      try{
         event = this.wds.getDewpoint(type);
         temp  = event.getValue();
      }
      catch(NullPointerException npe){}
      finally{
         return temp;
      }
   }
   
   //
   //
   //
   protected double heatindex(String type){
      double hi          = Thermometer.DEFAULTTEMP;
      WeatherEvent event = null;
      try{
         event = this.wds.getHeatIndex(type);
         hi    = event.getValue();
      }
      catch(NullPointerException npe){}
      finally{
         return hi;
      }
   }
   
   //
   //
   //
   protected double humidity(String type){
      double humid       = Hygrometer.DEFAULTHUMIDITY;
      WeatherEvent event = null;
      try{
         event = this.wds.getHumidity();
         humid = event.getValue();
      }
      catch(NullPointerException npe){}
      finally{
         return humid;
      }
   }
   
   //
   //
   //
   protected double pressure(String type){
      double barometricPressure = Temperature.DEFAULTTEMP;
      WeatherEvent event = null;
      try{
         event = this.wds.getPressure(type);
         barometricPressure = event.getValue();
      }
      catch(NullPointerException npe){}
      finally{
         return barometricPressure;
      }
   }
   
   //
   //
   //
   protected double temperature(String type){
      double temp = Thermometer.DEFAULTTEMP;
      WeatherEvent event = null;
      try{
         event = this.wds.getTemperature(type);
         temp = event.getValue();
      }
      catch(NullPointerException npe){}
      finally{
         return temp;
      }
   }
}