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

public class CurrentWeatherHandler extends BaseWeatherHandler
implements HttpHandler{
   
   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      try{
         response.append(this.setUpHeader());
         //response.append(this.setUpData());
         response.append(this.setUpTemperature());
         response.append(this.setUpHumidity());
         response.append(this.setUpPressure());
         response.append(this.setUpDewpoint());
         response.append(this.setUpHeatIndex());
         response.append("\n</script></head>\n");
         response.append(this.setUpBody());
         String send = response.toString();
         exchange.sendResponseHeaders(200, send.length());
         OutputStream os = exchange.getResponseBody();
         os.write(send.getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }
   
   //******************Private Methods*******************************
   //
   //
   //
   private String setUpBody(){
      StringBuffer body = new StringBuffer();
      body.append("\n<body>");
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
      body.append("\n</tr>\n</table>\n</body>\n</html>");
      return body.toString();
   }
   
   //
   //
   //
   private String setUpDewpoint(){
      StringBuffer buffer = new StringBuffer();
      double dewpoint = this.dewpoint("english");
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
      double hi = this.heatindex("english");
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
      return buffer.toString();
   }
   
   //
   //
   //
   private String setUpPressure(){
      StringBuffer buffer = new StringBuffer();
      double pressure = this.pressure("english");
      String presString = String.format("%.2f", pressure);
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
      //buffer.append("\n</script></head>");
      return buffer.toString();
   }
   
   //
   //Do NOT Use!!!
   //
   private String setUpData(){
      StringBuffer buffer = new StringBuffer();
      double temp     = this.temperature("english");
      double humidity = this.humidity();
      buffer.append("function drawChart() { ");
      buffer.append("var data = google.visualization.arrayToDataTable([ ");
      buffer.append("['Label', 'Value'], ");
      buffer.append("['Temperature', 0], ");
      buffer.append("['Humidity', 0] ");
      buffer.append("]); ");
      buffer.append("var tempoptions = { ");
      buffer.append("max: 130, minorTicks: 5, ");
      buffer.append("width: 240, height: 240, ");
      buffer.append("redFrom: 90, redTo: 130, ");
      buffer.append("yellowFrom: 75, yellowTo: 90,");
      buffer.append("greenFrom: 30, greenTo: 75 }; ");
      buffer.append("var humidoptions = { ");
      buffer.append("max: 100, minorTicks: 5, ");
      buffer.append("width: 240, height: 240 }; ");
      buffer.append("var chart = new google.visualization.Gauge(document.getElementById('chart_div')); ");
      buffer.append("chart.draw(data, tempoptions); ");
      buffer.append("data.setValue(0, 1, 60); ");
      buffer.append("chart.draw(data, tempoptions); ");
      buffer.append("</script></head>");
      return buffer.toString();
   }
   
   //
   //
   //
   private String setUpHeader(){
      StringBuffer header = new StringBuffer();
      header.append("<html><head>");
      header.append("<title>Lou Rosas' ");
      header.append("One Wire Weather Station</title>");
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
   
   //
   //
   //
   private String setUpHumidity(){
      StringBuffer buffer = new StringBuffer();
      double humidity = this.humidity();
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
      //LINE BELOW WILL NEED TO BE REMOVED FOR LATER DATA DISPLAYS
      //buffer.append("\n</script></head>");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpTemperature(){
      StringBuffer buffer = new StringBuffer();
      double temp = this.temperature("english");
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
      //buffer.append("tempdata.setValue(0, 1, Math.round(temp)); ");
      buffer.append("\ntempdata.setValue(0,1,Math.round("+temp+")); ");
      buffer.append("\ntempchart.draw(tempdata, tempoptions); }");
      //LINE BELOW:THIS WILL NEED TO BE REMOVED FOR LATER DATA DISPLAYS
      //buffer.append("</script></head>");
      return buffer.toString();
      
   }
}