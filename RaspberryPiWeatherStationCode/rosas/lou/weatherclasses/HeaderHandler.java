/*
*/
/////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import rosas.lou.weatherclasses.*;

public class HeaderHandler extends BaseWeatherHandler implements
HttpHandler{

   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      try{
         Hashtable<String, String> inputs = new Hashtable();
         Headers headers = exchange.getRequestHeaders();
         Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
         String response = "<html><head><title>Headers</title><p>";
         URI requestedUri = exchange.getRequestURI();
         String query = requestedUri.getRawQuery();
         this.parseQuery(query, inputs);
         for(Map.Entry<String, List<String>> entry : entries)
            response += entry.toString() + "<br />";
         response += exchange.getRequestMethod() + "<br />";
         response += query + "<br />";
         Enumeration<String> e = inputs.keys();
         while(e.hasMoreElements()){
            String key   = e.nextElement();
            String value = inputs.get(key);
            response += key + " : " + value + "<br />";
         }
         response +=  "</p></body></html>";
         exchange.sendResponseHeaders(200, response.length());
         OutputStream os = exchange.getResponseBody();
         os.write(response.toString().getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }

   //
   //Parse the query for the inputs
   //
   public void parseQuery
   (
      String query,
      Hashtable<String, String> inputs
   ){
      try{
         String data[] = query.split("&");
         for(int i = 0; i < data.length; i++){
            String parts[] = data[i].split("=");
            String key   = null;
            String value = null;
            if(parts.length > 0){
               key = URLDecoder.decode(parts[0], 
                                System.getProperty("file.encoding"));
            }
            if(parts.length > 1){
               value = URLDecoder.decode(parts[1], 
                                System.getProperty("file.encoding"));
            }
            inputs.put(key, value);
         }
      }
      catch(UnsupportedEncodingException uee){
         uee.printStackTrace();
      }
      catch(NullPointerException npe){}
   }
}
