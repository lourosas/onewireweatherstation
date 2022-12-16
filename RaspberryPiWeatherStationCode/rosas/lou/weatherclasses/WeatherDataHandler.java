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

public class WeatherDataHandler implements HttpHandler, 
TemperatureObserver, HumidityObserver, BarometerObserver,
CalculatedObserver, ExtremeObserver{
   //By Convention, the Metric Data is stored in the first position
   //in the List
   private final int METRIC   = 0;
   //By Convention, the English Data is stored in the second
   //position in the List
   private final int ENGLISH  = 1;
   //By Convention, the Absolute Data is stored in the third
   //position in the List
   private final int ABSOLUTE = 2;
   private StringBuffer response;
   private WeatherEvent temperature;
   private WeatherEvent temperatureMax;
   private WeatherEvent temperatureMin;
   private WeatherEvent humidity;
   private WeatherEvent humidityMax;
   private WeatherEvent humidityMin;
   private WeatherEvent pressure;
   private WeatherEvent pressureMax;
   private WeatherEvent pressureMin;
   private WeatherEvent dewPoint;
   private WeatherEvent dewPointMax;
   private WeatherEvent dewPointMin;
   private WeatherEvent heatIndex;
   private WeatherEvent heatIndexMax;
   private WeatherEvent heatIndexMin;

   {
      response       = null;
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
   //
   //
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      try{
          response.append(this.setUpHeader());
          response.append(this.setUpTemperature());
          response.append(this.setUpHumidity());
          response.append(this.setUpPressure());
          response.append(this.setUpDewpoint());
          response.append(this.setUpHeatIndex());
          response.append(this.setUpEnding());
          String send = response.toString();
          exchange.sendResponseHeaders(200, send.length());
          OutputStream os = exchange.getResponseBody();
          os.write(send.getBytes());
          os.close();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
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
      List<WeatherEvent> event = data.getLatestData("dewpoint");
      try{
         this.dewPoint = event.get(ENGLISH);
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
      this.updateTemperatureExtremes(data);
      this.updateHumidityExtremes(data);
      this.updatePressureExtremes(data);
      this.updateDewPointExtremes(data);
      this.updateHeatIndexExtremes(data);
   }

   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateHeatIndex(WeatherEvent event){}

   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateHeatIndex(WeatherStorage data){
      List<WeatherEvent> event = data.getLatestData("heatindex");
      try{
         this.heatIndex = event.get(ENGLISH);
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
      List<WeatherEvent> event = data.getLatestData("humidity");
      try{
         Iterator<WeatherEvent> it = event.iterator();
         while(it.hasNext()){
            this.humidity = it.next();
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
      List<WeatherEvent> event = data.getLatestData("pressure");
      try{
         this.pressure = event.get(ENGLISH);
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
     List<WeatherEvent> event = data.getLatestData("temperature");
      try{
         this.temperature = event.get(ENGLISH);        
      }
      catch(NullPointerException npe){
         this.temperature = null;
      }
   }

   //Private Methods
   //
   //
   //
   private String setUpDewpoint(){
      StringBuffer data = new StringBuffer();
      data.append("<tr><th colspan = \"3\" bgcolor = \"#DAF7A6\">");
      data.append("<font size = \"4\"><b>Dew Point</b></th></tr>");
      data.append("<tr align = \"center\">");
      data.append("<th>Current</th><th>Maximum</th><th>Minimum</th>");
      data.append("</tr>");
      data.append("<tr align = \"center\">");
      try{
         String value = 
                     String.format("%.2f", this.dewPoint.getValue());
         double dp = Double.parseDouble(value);
         if(dp <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + value + " &#176F</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/R</td>");
      }
      try{
         String maxDewPoint =
                  String.format("%.2f", this.dewPointMax.getValue());
         double maxdp = Double.parseDouble(maxDewPoint);
         if(maxdp <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + maxDewPoint + " &#176F</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/R</td>");
      }
      try{
         String minDewPoint =
                  String.format("%.2f", this.dewPointMin.getValue());
         double mindp = Double.parseDouble(minDewPoint);
         if(mindp <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + minDewPoint + " &#176F</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/R</td>");
      }
      finally{
         data.append("</tr>");
         return data.toString();
      }
   }

   //
   //
   //
   private String setUpEnding(){
      StringBuffer theEnd = new StringBuffer();
      theEnd.append("</table>");
      theEnd.append("<p>&#169 2017 Lou Rosas");
      theEnd.append("<br>Do Not Use for Personal Safety</p>");
      theEnd.append("</center></body></html>");
      return theEnd.toString();
   }

   //
   //
   //
   private String setUpHeader(){
      StringBuffer header       = new StringBuffer();
      StringBuffer currentTime  = new StringBuffer();
      try{
         Calendar cal = this.temperature.getCalendar();
         String dateString = String.format("%tc", cal.getTime());
         currentTime.append(dateString);
      }
      catch(NullPointerException npe){
         currentTime.append("N/A");
      }
      header.append("<html><head>");
      header.append("<title>Lou Rosas' ");
      header.append("One Wire Weather Station</title>");
      header.append("<meta http-equiv=\"refresh\" content=\"600\">");
      header.append("</head>");
      header.append("<body bgcolor = \"#C0C0C0\"><center>");
      header.append("<table width = \"640\" align = \"center\">");
      header.append("<tr align = \"center\"><td><h2> Tucson, AZ<br>");
      header.append("Rita Ranch Neighboorhood<br>Weather Conditions");
      header.append("</h2></td>");
      header.append("<td><h2>As Of: " + currentTime + "</h2></td>");
      header.append("</tr></table>");
      header.append("<font size = \"3\">");
      header.append("<table border = \"5\" width = \"640\"");
      header.append("align = \"center\" bgcolor = \"#5DADE2\"");
      return header.toString(); 
   }

   //
   //
   //
   private String setUpHeatIndex(){
      StringBuffer data = new StringBuffer();
      data.append("<tr><th colspan = \"3\" bgcolor = \"#DAF7A6\">");
      data.append("<font size = \"4\"><b>Heat Index</b></th></tr>");
      data.append("<tr align = \"center\">");
      data.append("<th>Current</th><th>Maximum</th><th>Minimum</th>");
      data.append("</tr>");
      data.append("<tr align = \"center\">");
      try{
         String value =
                    String.format("%.2f", this.heatIndex.getValue());
         double hi = Double.parseDouble(value);
         if(hi <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + value + " &#176F</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/A</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/A</td>");
      }
      try{
         String maxHeatIndex =
                  String.format("%.2f", this.heatIndexMax.getValue());
         double hiMax = Double.parseDouble(maxHeatIndex);
         if(hiMax <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + maxHeatIndex + " &#176F</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/A</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/A</td>");
      }
      try{
         String minHeatIndex =
                  String.format("%.2f", this.heatIndexMin.getValue());
         double hiMin = Double.parseDouble(minHeatIndex);
         if(hiMin <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + minHeatIndex + " &#176F</td>");
         }
      }
      catch(NumberFormatException npe){
         data.append("<td>N/A</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/A</td>");
      }
      finally{
         data.append("</tr>");
         return data.toString();
      }
   }

   //
   //
   //
   private String setUpHumidity(){
      StringBuffer data = new StringBuffer();
      data.append("<tr><th colspan = \"3\" bgcolor = \"#DAF7A6\">");
      data.append("<font size = \"4\"><b>Humidity</b></th></tr>");
      data.append("<tr align = \"center\">");
      data.append("<th>Current</th><th>Maximum</th><th>Minimum</th>");
      data.append("</tr>");
      data.append("<tr align = \"center\">");
      try{
         String value =
                     String.format("%.2f", this.humidity.getValue());
         double humidity = Double.parseDouble(value);
         if(humidity <= Hygrometer.DEFAULTHUMIDITY){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + value + "%</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/R</td>");
      }
      try{
         String maxHumidity =
                  String.format("%.2f", this.humidityMax.getValue());
         double max = Double.parseDouble(maxHumidity);
         if(max <= Hygrometer.DEFAULTHUMIDITY){
            throw new NullPointerException();
         }
         else{    
            data.append("<td>" + maxHumidity + "%</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe1){
         data.append("<td>N/R</td>");
      }
      try{
         String minHumidity = 
                  String.format("%.2f", this.humidityMin.getValue());
         double min = Double.parseDouble(minHumidity);
         if(min <= Hygrometer.DEFAULTHUMIDITY){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + minHumidity + "%</td>");
         }
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe2){
         data.append("<td>N/R</td>");
      }
      finally{
         data.append("</tr>");
         return data.toString();
      }
   }

   //
   //
   //
   private String setUpPressure(){
      StringBuffer data = new StringBuffer();
      data.append("<tr><th colspan = \"3\" bgcolor = \"#DAF7A6\">");
      data.append("<font size = \"4\"><b>Barometric Pressure</b></th></tr>");
      data.append("<tr align = \"center\">");
      data.append("<th>Current</th><th>Maximum</th><th>Minimum</th>");
      data.append("</tr>");
      data.append("<tr align = \"center\">");
      try{
         String value =
                     String.format("%.2f", this.pressure.getValue());
         double pressure = Double.parseDouble(value);
         if(pressure <= Barometer.DEFAULTPRESSURE){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + value + " in Hg</td>");
         }
      }
      catch(NumberFormatException npe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/R</td>");
      }
      try{
         String maxPressure =
                  String.format("%.2f", this.pressureMax.getValue());
         double max = Double.parseDouble(maxPressure);
         if(max <= Barometer.DEFAULTPRESSURE){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + maxPressure + "in Hg</td>");
         }
      }
      catch(NumberFormatException npe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe1){
         data.append("<td>N/R</td>");
      }
      try{
         String minPressure =
                  String.format("%.2f", this.pressureMin.getValue());
         double min = Double.parseDouble(minPressure);
         if(min <= Barometer.DEFAULTPRESSURE){
            throw new NullPointerException();
         }
         else{
            data.append("<td>" + minPressure + "in Hg</td>");
         }
      }
      catch(NumberFormatException npe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe2){
         data.append("<td>N/R</td>");
      }
      finally{
         data.append("</tr>");
         return data.toString();
      }
   }

   //
   //
   //
   private String setUpTemperature(){
      StringBuffer data = new StringBuffer();
      data.append("<tr><th colspan = \"3\" bgcolor = \"#DAF7A6\">");
      data.append("<font size = \"4\"><b>Temperature</b></th></tr>");
      data.append("<tr align = \"center\">");
      data.append("<th>Current</th><th>Maximum</th><th>Minimum</th>");
      data.append("</tr>");
      data.append("<tr align = \"center\">");
      try{
         String temp = 
                   String.format("%.2f",this.temperature.getValue());
         double value = Double.parseDouble(temp);
         if(value <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         data.append("<td>"+ temp +" &#176F</td>");
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe){
         data.append("<td>N/R</td>");
      }
      try{
         String tempMax =
               String.format("%.2f", this.temperatureMax.getValue());
         double max = Double.parseDouble(tempMax);
         if(max <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         data.append("<td>" + tempMax + " &#176F</td>");
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe2){
         data.append("<td>N/R</td>");
      }
      try{
         String tempMin =
               String.format("%.2f", this.temperatureMin.getValue());
         double min = Double.parseDouble(tempMin);
         if(min <= Thermometer.DEFAULTTEMP){
            throw new NullPointerException();
         }
         data.append("<td>" + tempMin + " &#176F</td>");
      }
      catch(NumberFormatException nfe){
         data.append("<td>N/R</td>");
      }
      catch(NullPointerException npe3){
         data.append("<td>N/R</td>");
      }
      finally{
         data.append("</tr>");
         return data.toString();
      }
   }

   //
   //
   //
   private void updateDewPointExtremes(WeatherStorage data){
      List<WeatherEvent> max = data.getMax("dewpoint");
      List<WeatherEvent> min = data.getMin("dewpoint");
      try{
         Iterator<WeatherEvent> it = max.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.dewPointMax = we;
            }
         }
      }
      catch(NullPointerException npe1){
         this.dewPointMax = null;
      }
      try{
         Iterator<WeatherEvent> it = min.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.dewPointMin = we;
            }
         }
      }
      catch(NullPointerException npe2){
         this.dewPointMin = null;
      }
   }

   //
   //
   //
   private void updateHeatIndexExtremes(WeatherStorage data){
      List<WeatherEvent> max = data.getMax("heatindex");
      List<WeatherEvent> min = data.getMin("heatindex");
      try{
         Iterator<WeatherEvent> it = max.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.heatIndexMax = we;
            }
         }
      }
      catch(NullPointerException npe1){
         this.heatIndexMax = null;
      }
      try{
         Iterator<WeatherEvent> it = min.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.heatIndexMin = we;
            }
         }
      }
      catch(NullPointerException npe2){
         this.heatIndexMin = null;
      }
   }

   //
   //
   //
   private void updateHumidityExtremes(WeatherStorage data){
      List<WeatherEvent> max = data.getMax("humidity");
      List<WeatherEvent> min = data.getMin("humidity");
      try{
         Iterator<WeatherEvent> it = max.iterator();
         this.humidityMax = it.next();
      }
      catch(NullPointerException npe1){
         this.humidityMax = null;
      }
      try{
         Iterator<WeatherEvent> it = min.iterator();
         this.humidityMin = it.next();
      }
      catch(NullPointerException npe2){
         this.humidityMin = null;
      }
   }

   //
   //
   //
   private void updatePressureExtremes(WeatherStorage data){
      List<WeatherEvent> max = data.getMax("pressure");
      List<WeatherEvent> min = data.getMin("pressure");
      try{
         Iterator<WeatherEvent> it = max.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.pressureMax = we;
            }
         }
      }
      catch(NullPointerException npe1){
         this.pressureMax = null;
      }
      try{
         Iterator<WeatherEvent> it = min.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.pressureMin = we;
            }
         }
      }
      catch(NullPointerException npe2){
         this.pressureMin = null;
      }
   }

   //
   //
   //
   private void updateTemperatureExtremes(WeatherStorage data){
      List<WeatherEvent> max = data.getMax("temperature");
      List<WeatherEvent> min = data.getMin("temperature");
      try{
         Iterator<WeatherEvent> it = max.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.temperatureMax = we;
            }
         }
      }
      catch(NullPointerException npe1){
         this.temperatureMax = null;
      }
      try{
         Iterator<WeatherEvent> it = min.iterator();
         while(it.hasNext()){
            WeatherEvent we = it.next();
            if(we.getUnits() == Units.ENGLISH){
               this.temperatureMin = we;
            }
         }
      }
      catch(NullPointerException npe2){
         this.temperatureMin = null;
      }
   }
}
