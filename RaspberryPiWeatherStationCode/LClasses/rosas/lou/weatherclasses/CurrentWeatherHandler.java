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
         response.append(this.setUpTemperature());
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
      body.append("<body>");
      body.append("<div id=\"temp_div\", ");
      body.append("style=\"width: 240px; height: 240px;\"</div>");
      body.append("</body></html>");
      return body.toString();
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
      header.append("google.charts.setOnLoadCallback(drawTemp);");
      return header.toString();
   }
   
   //
   //
   //
   private String setUpTemperature(){
      StringBuffer buffer = new StringBuffer();
      double temp = this.temperature("english");
      buffer.append("function drawTemp() { ");
      buffer.append("var tempdata = google.visualization.arrayToDataTable([ ");
      buffer.append("['Label', 'Value'], ");
      buffer.append("['Temperature', 0]");
      buffer.append(" ]);");
      buffer.append("var tempoptions = { ");
      buffer.append("max: 130, minorTicks: 5, ");
      buffer.append("width: 240, height: 240, ");
      buffer.append("redFrom: 90, redTo: 130, ");
      buffer.append("yellowFrom: 75, yellowTo: 90,");
      buffer.append("greenFrom: 30, greenTo: 75 }; ");
      buffer.append("var tempchart = new google.visualization.Gauge(document.getElementById('temp_div'));");
      buffer.append("tempchart.draw(tempdata, tempoptions);");
      //buffer.append("tempdata.setValue(0, 1, Math.round(temp)); ");
      buffer.append("tempdata.setValue(0,1,Math.round("+temp+")); ");
      buffer.append("tempchart.draw(tempdata, tempoptions); }");
      //LINE BELOW:THIS WILL NEED TO BE REMOVED FOR LATER DATA DISPLAYS
      buffer.append("</script></head>");
      return buffer.toString();
      
   }
}