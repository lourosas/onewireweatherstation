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

public class CurrentWeatherHandler extends BaseWeatherHandler
implements HttpHandler{
   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      //super.handle(exchange);
      //Going to "play" with the HttpExchange class to understand it
      //better.
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
      catch(IOException ioe){ ioe.printStackTrace(); }
      finally{
         try{
            os.close();
         }
         catch(IOException ioe){}
      }
   }
   
   //******************Overriden Methods*****************************
   //
   //
   //
   protected String setUpBody(){
      StringBuffer body = new StringBuffer();
      String date = new String();
      date = String.format("%tc", this._temperature.calendar());
      body.append("\n<body>");
      body.append("<table width = \"740\">");
      body.append("<tr align = \"center\"><td><h2>Tucson, AZ<br>");
      body.append("Rita Ranch Neighborhood<br>Weather Conditions");
      body.append("</h2></td>");
      body.append("<td><h2>As Of: "+ date + "</h2></td>");
      body.append("</tr></table>");
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
      body.append("\n</tr>\n</table>");
      body.append("\n</body>");
      return body.toString();
   }

   //
   //
   //
   protected String setUpFooter(){
      StringBuffer footer = new StringBuffer();
      footer.append("\n</html>");
      return footer.toString();
   }

   //
   //
   //
   protected String setUpHeader(){
      StringBuffer header = new StringBuffer();
      header.append("<html><head>");
      header.append("<title> A One Wire Weather Station</title>");
      header.append("<meta http-equiv=\"refresh\" content=\"600\">");
      header.append("<script type=\"text/javascript\" ");
      header.append("src=\"https://www.gstatic.com/charts/loader.js\"></script>");
      header.append("<script type=\"text/javascript\">");
      header.append("google.charts.load('current', {'packages':['gauge']});");
      header.append("google.charts.setOnLoadCallback(drawTemp);\n");
      header.append("google.charts.setOnLoadCallback(drawHumidity);\n");
      header.append("google.charts.setOnLoadCallback(drawPressure);\n");
      header.append("google.charts.setOnLoadCallback(drawDewpoint);\n");
      header.append("google.charts.setOnLoadCallback(drawHeatIndex);\n");
      return header.toString();
   }

   //******************Private Methods*******************************
   //
   //
   //
   private String closeOutHeader(){
      StringBuffer close = new StringBuffer();
      close.append("\n</script></head>\n");
      return close.toString();
   }

   //
   //
   //
   private String setUpDewpoint(){
      StringBuffer buffer = new StringBuffer();
      double dewpoint = this._dewPoint.englishData();;
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

   //
   //
   //
   private String setUpHeatIndex(){
      StringBuffer buffer = new StringBuffer();
      double hi = this._heatIndex.englishData();
      if(hi > Thermometer.DEFAULTTEMP){
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

   //
   //
   //
   private String setUpHumidity(){
      StringBuffer buffer = new StringBuffer();
      double humidity = this._humidity.percentageData();
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

   //
   //
   //
   private String setUpPressure(){
      StringBuffer buffer = new StringBuffer();
      double pressure     = this._pressure.englishData();
      String presString   = String.format("%.2f", pressure);
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
      buffer.append("\npresdata.setValue(0,1,"+presString+");");
      buffer.append("\npreschart.draw(presdata, presoptions); \n}");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpTemperature(){
      StringBuffer buffer = new StringBuffer();
      double temp = this._temperature.englishData();
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
