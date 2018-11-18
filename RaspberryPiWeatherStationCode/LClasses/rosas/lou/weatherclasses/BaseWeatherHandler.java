/////////////////////////////////////////////////////////////////////
/*
Copyright 2018 Lou Rosas
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import rosas.lou.weatherclasses.*;

public abstract class BaseWeatherHandler implements HttpHandler,
TemperatureHumidityObserver, BarometerObserver, CalculatedObserver{

   protected WeatherData _temperature;
   protected WeatherData _humidity;
   protected WeatherData _pressure;
   protected WeatherData _heatIndex;
   protected WeatherData _dewPoint;

   {
      _temperature = null;
      _humidity    = null;
      _pressure    = null;
      _heatIndex   = null;
      _dewPoint    = null;
   }

   /////////Implementation of the HttpHandler Interface//////////////
   /**/
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      final int OK          = 200;
      OutputStream os       = null;
      InputStream  is       = null;
      try{
         System.out.println(exchange.getRequestMethod());
         //System.out.println(exchange.getRequestHeaders());
         /*
         Headers headers = exchange.getRequestHeaders();
         System.out.println(headers.isEmpty());
         Set<String> keys = headers.keySet();
         Iterator<String> it = keys.iterator();
         while(it.hasNext()){
            String key = it.next();
            System.out.println(key);
            System.out.println(headers.get(key));
         }
         InputStream is = exchange.getRequestBody();
         System.out.println(is.available());
         */
         /*
         BufferedReader br =
                        new BufferedReader(new InputStreamReader(is));
         String whatIsRead = null;
         while((whatIsRead = br.readLine()) != null){
            System.out.println(whatIsRead);
         }
         */
         response.append(this.setUpHeader());
         response.append(this.setUpBody());
         response.append(this.setUpFooter());
         String send = response.toString();
         exchange.sendResponseHeaders(OK, send.length());
         System.out.println(send.length());
         System.out.println(send);
         os = exchange.getResponseBody();
         os.write(send.getBytes());
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
      finally{
         try{
            os.close();
         }
         catch(IOException e){}
      }
   }

   ///Implementation of the TemperatureHumidityObserver Interface////
   /*
   */
   public void updateTemperature(WeatherData data){
      this._temperature = data;
   }

   /*
   */
   public void updateTemperatureMetric(double temp){}

   /*
   */
   public void updateTemperatureEnglish(double temp){}

   /*
   */
   public void updateTemperatureAbsolute(double temp){}

   /*
   */
   public void updateHumidity(WeatherData data){
      this._humidity = data;
   }

   /*
   */
   public void updateHumidity(double humidity){}

   ///////Implementation of the BarometerObserver Interface//////////
   /*
   */
   public void updatePressure(WeatherEvent event){}

   /*
   */
   public void updatePressure(WeatherStorage store){}

   /*
   */
   public void updatePressure(WeatherData data){
      this._pressure = data;
   }

   /*
   */
   public void updatePressureAbsolute(double data){}

   /*
   */
   public void updatePressureEnglish(double data){}

   /*
   */
   public void updatePressureMetric(double data){}

   ///////Implementation of the CalculatedObserver Interface/////////
   /*
   */
   public void updateDewpoint(WeatherEvent event){}

   /*
   */
   public void updateDewpoint(WeatherStorage storage){}

   /*
   */
   public void updateDewpoint(WeatherData data){
      this._dewPoint = data;
   }

   /*
   */
   public void updateDewpointAbsolute(double data){}

   /*
   */
   public void updateDewpointEnglish(double data){}

   /*
   */
   public void updateDewpointMetric(double data){}

   /*
   */
   public void updateHeatIndex(WeatherEvent event){}

   /*
   */
   public void updateHeatIndex(WeatherStorage store){}

   /*
   */
   public void updateHeatIndex(WeatherData data){
      this._heatIndex = data;
   }

   /*
   */
   public void updateHeatIndexAbsolute(double hi){}

   /*
   */
   public void updateHeatIndexEnglish(double hi){}

   /*
   */
   public void updateHeatIndexMetric(double hi){}

   /*
   */
   public void updateWindChill(WeatherEvent event){}


   ///////////////////Protected Methods//////////////////////////////
   /*
   */
   protected String setUpBody(){
      StringBuffer body = new StringBuffer();
      StringBuffer data = new StringBuffer();
      String value      = new String();
      body.append("\n<body>");
      try{
         value=String.format("%tc",this._temperature.calendar());
         body.append("\n<h2>" + value + "</h2>");
         value=String.format("Temperature:  %.2f",
                              this._temperature.englishData());
         body.append("\n<p>" + value + " &#176F");
      }
      catch(NullPointerException npe){
         body.append("\n<p>N/A");
      }
      try{
         value=String.format("Humidity:  %.2f",
                              this._humidity.percentageData());
         body.append("\n<p>" + value + "%");
      }
      catch(NullPointerException npe){
         body.append("\n<p>N/A");
      }
      try{
         value = String.format("Pressure:  %.2f",
                                this._pressure.englishData());
         body.append("\n<p>" + value + " in Hg");
      }
      catch(NullPointerException npe){
         body.append("\n<p>N/A");
      }
      try{
         value = String.format("Dewpoint:  %.2f",
                                this._dewPoint.englishData());
         body.append("\n<p>" + value + " &#176F");
      }
      catch(NullPointerException npe){
         body.append("\n<p>N/A");
      }
      try{
         value = String.format("Heat Index:  %.2f",
                                this._heatIndex.englishData());
         body.append("\n<p>" + value + " &#176F");
      }
      catch(NullPointerException npe){
         body.append("\n<p>N/A");
      }
      body.append("\n</body>");
      return body.toString();
   }

   /*
   */
   protected String setUpFooter(){
      StringBuffer footer = new StringBuffer();
      footer.append("\n</html>");
      return footer.toString();
   }

   /*
   */
   protected String setUpHeader(){
      StringBuffer header = new StringBuffer();
      header.append("<html>");
      header.append("\n<head>\n<title>Base Weather Handler</title>");
      header.append("\n</head>");
      return header.toString();
   }
}
