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
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      try{
         response.append(this.setUpHeader());
         response.append(this.setUpBody());
         response.append(this.setUpFooter());
         String send = response.toString();
         exchange.sendResponseHeaders(200, send.length());
         OutputStream os = exchange.getResponseBody();
         os.write(send.getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }
   
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
   protected String getDate(){
      String currentDate = new String();
      WeatherEvent event = null;
      try{
         event       = this.wds.getTemperature("metric");
         currentDate = new String(event.toStringDate());
      }
      catch(NullPointerException npe){}
      finally{
         return currentDate;
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
   protected double humidity(){
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
      double barometricPressure = Thermometer.DEFAULTTEMP;
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
   
   //*****************Private Methods********************************
   //
   //
   //
   private String setUpBody(){
      String value      = new String();
      StringBuffer body = new StringBuffer();
      body.append("<body>");
      try{
         value = String.format("%.2f", this.temperature("english"));
         body.append("<p>" + value + " &#176F");
         value = String.format("%.2f", this.humidity());
         body.append("<p>" + value + "%");
         value = String.format("%.2f", this.pressure("english"));
         body.append("<p>" + value + " in");
         value = String.format("%.2f", this.heatindex("english"));
         body.append("<p>" + value + " &#176F");
         value = String.format("%.2f", this.dewpoint("english"));
         body.append("<p>" + value + " &#176F");
         body.append("<p>" + this.getDate());
      }
      catch(NullPointerException npe){
         body.append("<p>N/A");
      }
      return body.toString();
   }
   
   //
   //
   //
   private String setUpFooter(){
      StringBuffer end = new StringBuffer();
      end.append("<p><p><center>&#169 2017 Lou Rosas");
      end.append("<br>Do not use for personal safety</p>");
      end.append("</center></body></html>");
      return end.toString();
   }
   
   //
   //
   //
   private String setUpHeader(){
      StringBuffer header  = new StringBuffer();
      header.append("<html><head>");
      header.append("<title>Lou Rosas' ");
      header.append("Basic Weather Data Empty Web Page</title>");
      header.append("</head>");    
      return header.toString();
   }
}