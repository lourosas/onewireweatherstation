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

public class ArchiveDisplayHandler extends BaseWeatherHandler
implements HttpHandler{

   private static String[] months = {"January", "February",
   "March", "April", "May", "June", "July", "August", "September",
   "October", "November", "December"};

   private static String[] years = {"2017", "2018"};

   //
   //Implemtation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();

      try{
         response.append(this.setUpHeader());
         response.append(this.setUpBody(exchange)); 
         exchange.sendResponseHeaders(200, response.length());
         OutputStream os = exchange.getResponseBody();
         os.write(response.toString().getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }

   //*****************Private Methods********************************
   //
   //
   //
   private String setUpBody(HttpExchange exchange){
      Calendar now = Calendar.getInstance();
      int month    = now.get(Calendar.MONTH);
      int day      = now.get(Calendar.DATE);
      int year     = now.get(Calendar.YEAR);
      String theMonth = months[month];
      StringBuffer body = new StringBuffer();
      body.append("<body><h1>Try This Archiving</h1>");
      body.append("\n<form action=");
      body.append("\"http://68.98.39.148:8000/archive\" ");
      body.append("method=\"GET\" ");
      body.append("enctype=\"multipart/form-data\">");
      //Month
      body.append("\n<select value = \"month\">");
      for(int i = 0; i < months.length; i++){
         body.append("\n<option value = \""+months[i] +"\"");
         if(i == month){
            body.append(" selected");
         }
         body.append(">" + months[i] + "</option>");
      }
      body.append("\n</select>");
      //Day
      body.append("\n<select value = \"day\">");
      for(int i = 0; i < 31; i++){
         body.append("\n<option value = \"" + (i+1) + "\"");
         if((i+1) == day){
            body.append(" selected");
         }
         body.append(">" + (i+1) + "</option>");
      }
      body.append("\n</select>");
      //Year
      body.append("\n<select value = \"year\">");
      for(int i = 0; i < years.length; i++){
         body.append("\n<option value = \""+years[i]+"\"");
         String value = "" + year;
         if(value.equals(years[i])){
            body.append(" selected");
         }
         body.append(">" + years[i] + "</option>");
      }
      body.append("\n<select>&nbsp;&nbsp;&nbsp;");
      //Select Button
      body.append("<input type=\"submit\" value=\"View\"/>");
      body.append("\n</form>");
      body.append(this.processRequestData(exchange));
      body.append("</body></html>");
      return body.toString();
   }

   //
   //
   //
   private String setUpHeader(){
      StringBuffer header = new StringBuffer();
      header.append("<html><head><title>Archived Inquiry");
      header.append("</title></head>");
      return header.toString();
   }

   //
   //
   //
   private String processRequestData(HttpExchange exchange){
      StringBuffer returnBuffer = new StringBuffer();
      try{
         returnBuffer.append("\n<p>Will this work? <br />");
         /*
         InputStream in = exchange.getRequestBody();
         InputStreamReader reader = new InputStreamReader(in);
         BufferedReader bufferedReader = new BufferedReader(reader);
         String line = null;
         while((line = bufferedReader.readLine()) != null){
            returnBuffer.append(line + "<br />");
         }
         */
         URI requestedUri = exchange.getRequestURI();
         String query = requestedUri.getRawQuery();
         returnBuffer.append(query + "<br />");
         String request = exchange.getRequestMethod();
         returnBuffer.append(request + "<br />");
         returnBuffer.append("\n</p>");
      }
      catch(Exception ioe){
         returnBuffer = new StringBuffer();
      }
      finally{
         return returnBuffer.toString();
      }
   }
}
