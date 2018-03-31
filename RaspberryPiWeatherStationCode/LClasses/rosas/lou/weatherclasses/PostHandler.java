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

public class PostHandler extends BaseWeatherHandler implements
HttpHandler{

   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      try{
         InputStreamReader isr = 
            new InputStreamReader(exchange.getRequestBody(),"utf-8");
         BufferedReader  br = new BufferedReader(isr);
         String query = br.readLine();
         String response = new String("<html><head><title> Post");
         response += "</title></head><body><p>"+query+"</p>";
         response += "</body></html>";
         exchange.sendResponseHeaders(200, response.length());
         OutputStream os = exchange.getResponseBody();
         os.write(response.toString().getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }
}
