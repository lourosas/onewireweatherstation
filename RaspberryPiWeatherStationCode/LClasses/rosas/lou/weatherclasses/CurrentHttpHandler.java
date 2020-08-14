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
import java.text.*;
import com.sun.net.httpserver.*;
import rosas.lou.weatherclasses.*;

public class CurrentHttpHandler 
extends CurrentWeatherDataSubscriber implements HttpHandler{
   private Calendar start;
   private long     startMillis;
   private int      currentDate;
   private String   sunrise;
   private String   sunset;

   {
      start       = null;
      startMillis = -1;
      currentDate = -1;
      sunrise     = "";
      sunset      = "";
   };

   ////////////////////////Constructors///////////////////////////////
   /*
   */
   public CurrentHttpHandler(){
      super();
      this.start        = Calendar.getInstance();
      this.startMillis  = this.start.getTimeInMillis();
      this.currentDate  = start.get(Calendar.DATE);
      this.setSunriseSunset();
   }

   ///////////////////Interface Implementations///////////////////////
   /*
   Implementation of the HttpHandler Interface
   */
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      final int OK          = 200;
      OutputStream os       = null;
      try{
         response.append(this.setUpHeader());
         response.append(this.setUpTemperature());
         response.append(this.setUpHumidity());
         response.append(this.setUpPressure());
         response.append(this.setUpDewpoint());
         response.append(this.setUpHeatIndex());
         response.append(this.closeOutHeader());
         response.append(this.setUpBody());
         response.append(this.setUpFooter());
         String send = response.toString();
         exchange.sendResponseHeaders(OK, send.length());
         os = exchange.getResponseBody();
         os.write(send.getBytes());
         os.close();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
      finally{
         try{
            os.close();
         }
         catch(IOException ioe){}
      }
   }

   ////////////////////Protected Methods//////////////////////////////
   /*
   */
   protected String closeOutHeader(){
      StringBuffer close = new StringBuffer();
      close.append("\n</script></head>\n");
      return close.toString();
   }

   /*
   */
   protected String setUpBody(){
      StringBuffer body = new StringBuffer();
      String date = this._wdp.parseCalendar(this._data);
      if(!this.checkDate()){
         this.setSunriseSunset();
      }
      body.append("\n<body>\n");
      body.append("<table width = \"740\">");
      body.append("<tr align = \"center\"><td><h2>Tucson, AZ<br>");
      body.append("Rita Ranch Neighborhood<br>Weather Conditions");
      body.append("</h2></td>");
      body.append("<td><h2>As Of: "+ date + "</h2></td>");
      body.append("</tr></table>");
      body.append("<table width = \"740\">");
      body.append("<tr align = \"center\">");
      body.append("<td><h3>Sunrise:  "+this.sunrise+"</h3></td>");
      body.append("<td><h3>Sunset:   "+this.sunset+"</h3></td>");
      body.append("</tr>");
      body.append("</table>");
      body.append("\n<table class=\"rows\">\n<tr>");
      body.append("\n<td><div id=\"temp_div\", ");
      body.append("style=\"width: 300px;height: 240px;\"</div></td>");
      body.append("\n<td><div id=\"dp_div\", ");
      body.append("style=\"width: 300px;height: 240px;\"</div></td>");
      body.append("</tr>");
      body.append("\n<tr><td><div id=\"humid_div\", ");
      body.append("style=\"width: 300px;height: 240px;\"</div></td>");
      body.append("\n<td><div id=\"hi_div\", ");
      body.append("style=\"width: 300px;height: 240px;\"</div></td>");
      body.append("</tr>");
      body.append("\n<tr><td><div id=\"press_div\", ");
      body.append("style=\"width: 300px;height: 240px;\"</div></td>");
      body.append("\n</tr>\n</table>\n");
      body.append("<h3 style=\"color:blue;text-align:center;\">");
      body.append("This website has been up for:  ");
      body.append(this.calculateUpTime() + "</h3>\n");
      body.append("</body>\n");
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
   private boolean checkDate(){
      boolean isTheSame = true;
      Calendar cal = Calendar.getInstance();
      if(this.currentDate != cal.get(Calendar.DATE)){
         isTheSame = false;
         this.currentDate = cal.get(Calendar.DATE);
      }
      return isTheSame;
   }

   /*
   */
   private void setSunriseSunset(){
      Calendar cal = Calendar.getInstance();
      int month    = cal.get(Calendar.MONTH) + 1;
      int day      = cal.get(Calendar.DATE);
      int year     = cal.get(Calendar.YEAR);

      StringBuffer date = new StringBuffer();
      date.append(year + "-");
      date.append(String.format("%02d", month) + "-");
      date.append(String.format("%02d", day));
      StringBuffer send = new StringBuffer("https://");
      send.append("api.met.no/weatherapi/sunrise/2.0/?");
      send.append("lat=32.1017&lon=-110.7895&");
      send.append("date=" + date.toString().trim());
      send.append("&offset=-07:00");
      try{
         System.out.println(send);
         URL url = new URL(send.toString().trim());
         URLConnection conn = url.openConnection();
         BufferedReader in = new BufferedReader(
                       new InputStreamReader(conn.getInputStream()));
         String line;
         while((line = in.readLine()) != null){
            if(line.contains("sunrise time")){
               String currentTime = line.split("T")[1];
               this.sunrise = currentTime.split("-")[0];
               System.out.println(this.sunrise);
            }
            else if(line.contains("sunset time")){
               String currentTime = line.split("T")[1];
               this.sunset = currentTime.split("-")[0];
               System.out.println(this.sunset);
            }
         }
      }
      catch(MalformedURLException me){
         System.out.println("URL Exception " + me);
      }
      catch(IOException ioe){
         System.out.println("IO Exception " + ioe);
      }
   }

   /*
   */
   private String setUpHeatIndex(){
      StringBuffer buffer = new StringBuffer();
      String hi = this._wdp.parseHeatIndexEnglish(this._data);
      double hid = Double.parseDouble(hi);
      if(hid > Thermometer.DEFAULTTEMP){
         buffer.append("\nfunction drawHeatIndex() { \n");
         buffer.append("var hidata = google.visualization.arrayToDataTable([ ");
         buffer.append("\n['Label', 'Value'], ");
         buffer.append("\n['Heat Index', 0], ");
         buffer.append("\n ]);");
         buffer.append("\nvar hioptions = { ");
         buffer.append("\nmin: 70, ");
         buffer.append("\nmax: 130, minorTicks: 3, ");
         buffer.append("\nwidth: 240, height: 240, ");
         buffer.append("\nredFrom: 100, redTo: 130, ");
         buffer.append("\nyellowFrom: 85, yellowTo: 100,");
         buffer.append("\ngreenFrom: 70, greenTo: 85 \n}; ");
         buffer.append("\nvar hichart = new google.visualization.Gauge(document.getElementById('hi_div'));");
         buffer.append("\nhichart.draw(hidata, hioptions);");
         buffer.append("\nhidata.setValue(0,1,Math.round("+hi+")); ");
         buffer.append("\nhichart.draw(hidata,hioptions);\n}");
      }
      return buffer.toString();
   }

   /*
   */
   protected String setUpHeader(){
      StringBuffer header = new StringBuffer();
      header.append("<html><head>\n");
      header.append("<title>A One Wire Weather Station</title>\n");
      header.append("<meta http-equiv=\"refresh\" content=\"600\">");
      header.append("<script type=\"text/javascript\" ");
      header.append("src=\"https://www.gstatic.com/charts/loader.js\"></script>");
      header.append("<script type=\"text/javascript\">");
      header.append("google.charts.load('current', {'packages':['gauge']});");
      header.append("\ngoogle.charts.setOnLoadCallback(drawTemp);\n");
      header.append("google.charts.setOnLoadCallback(drawHumidity);\n");
      header.append("google.charts.setOnLoadCallback(drawPressure);\n");
      header.append("google.charts.setOnLoadCallback(drawDewpoint);\n");
      header.append("google.charts.setOnLoadCallback(drawHeatIndex);\n");
      header.append("\n");
      return header.toString();
   }

   ///////////////////////Private Methods/////////////////////////////
   /*
   */
   private String calculateUpTime(){
      StringBuffer upTime = null;
      int DAY_DIFF        = 1;
      int EPOCH           = 1970;
      Calendar current    = Calendar.getInstance();

      current.setTimeZone(TimeZone.getTimeZone("UTC"));
      long currentMillis = current.getTimeInMillis();
      current.setTimeInMillis(currentMillis - this.startMillis);

      int secs = current.get(Calendar.SECOND);
      int mins = current.get(Calendar.MINUTE);
      int hrs  = current.get(Calendar.HOUR_OF_DAY);
      int days = current.get(Calendar.DAY_OF_YEAR) - DAY_DIFF;
      int yrs  = current.get(Calendar.YEAR) - EPOCH;

      upTime = new StringBuffer(yrs + " Years ");
      upTime.append(days + " days ");
      upTime.append(String.format("%02d", hrs ) + " Hours ");
      upTime.append(String.format("%02d", mins) +  " Mins ");
      upTime.append(String.format("%02d", secs) +  " Secs ");

      return upTime.toString();
   }

   /*
   */
   private String setUpDewpoint(){
      StringBuffer buffer = new StringBuffer();
      String dewpoint = this._wdp.parseDewpointEnglish(this._data);
      buffer.append("\nfunction drawDewpoint() { \n");
      buffer.append("var dpdata = google.visualization.arrayToDataTable([ ");
      buffer.append("\n['Label', 'Value'], ");
      buffer.append("\n['Dewpoint', 0], ");
      buffer.append("\n ]);");
      buffer.append("\nvar dpoptions = { ");
      buffer.append("\nminorTicks: 5, ");
      buffer.append("\nwidth: 240, height: 240\n};");
      buffer.append("\nvar dpchart = new google.visualization.Gauge(document.getElementById('dp_div'));");
      buffer.append("\ndpchart.draw(dpdata, dpoptions);");
      buffer.append("\ndpdata.setValue(0,1,Math.round("+dewpoint+"));");
      buffer.append("\ndpchart.draw(dpdata,dpoptions);");
      buffer.append("\n}");
      return buffer.toString();
   }

   /*
   */
   private String setUpHumidity(){
      StringBuffer buffer = new StringBuffer();
      String humidity = this._wdp.parseHumidity(this._data);
      buffer.append("\n\nfunction drawHumidity() { \n");
      buffer.append("var humiddata = google.visualization.arrayToDataTable([ ");
      buffer.append("\n['Label', 'Value'], ");
      buffer.append("\n['Humidity', 0]");
      buffer.append(" ]);");
      buffer.append("\nvar humidoptions = { ");
      buffer.append("\nmax: 100, minorTicks: 5, ");
      buffer.append("\nwidth: 240, height: 240 \n}; ");
      buffer.append("\nvar humidchart = new google.visualization.Gauge(document.getElementById('humid_div'));");
      buffer.append("\nhumidchart.draw(humiddata, humidoptions);");
      buffer.append("\nhumiddata.setValue(0,1,Math.round("+humidity+")); ");
      buffer.append("\nhumidchart.draw(humiddata, humidoptions); \n}");
      return buffer.toString();
   }

   /*
   */
   private String setUpPressure(){
      StringBuffer buffer = new StringBuffer();
      String pressure    = this._wdp.parsePressureEnglish(this._data);
      buffer.append("\n\nfunction drawPressure() { \n");
      buffer.append("var presdata = google.visualization.arrayToDataTable([ ");
      buffer.append("\n['Label', 'Value'], ");
      buffer.append("\n['in Hg', 27]");
      buffer.append("\n ]);");
      buffer.append("\nvar presoptions = { ");
      buffer.append("\nmin:27, max: 32,");
      buffer.append("\nminorTicks: 12,");
      buffer.append("\nwidth: 240, height: 240\n};");
      buffer.append("\nvar preschart = new google.visualization.Gauge(document.getElementById('press_div'));");
      buffer.append("\npreschart.draw(presdata,presoptions);");
      buffer.append("\npresdata.setValue(0,1,"+pressure+");");
      buffer.append("\npreschart.draw(presdata, presoptions); \n}");
      return buffer.toString();
   }

   /*
   */
   private String setUpTemperature(){
      StringBuffer buffer = new StringBuffer();
      String temp = this._wdp.parseTemperatureEnglish(this._data);
      buffer.append("\nfunction drawTemp() { \n");
      buffer.append("var tempdata = google.visualization.arrayToDataTable([ ");
      buffer.append("\n['Label', 'Value'], ");
      buffer.append("\n['Temperature', 0]");
      buffer.append("\n ]);");
      buffer.append("\nvar tempoptions = { ");
      buffer.append("\nmax: 130, minorTicks: 6, ");
      buffer.append("\nwidth: 240, height: 240, ");
      buffer.append("\nredFrom: 90, redTo: 130, ");
      buffer.append("\nyellowFrom: 75, yellowTo: 90,");
      buffer.append("\ngreenFrom: 30, greenTo: 75 \n}; ");
      buffer.append("\nvar tempchart = new google.visualization.Gauge(document.getElementById('temp_div'));");
      buffer.append("\ntempchart.draw(tempdata, tempoptions);");
      buffer.append("\ntempdata.setValue(0,1,Math.round("+temp+")); ");
      buffer.append("\ntempchart.draw(tempdata, tempoptions); }");
      return buffer.toString();
   }
}
