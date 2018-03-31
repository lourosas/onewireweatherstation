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

public class GetHandler extends BaseWeatherHandler implements
HttpHandler{

   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      try{
         URI uri = exchange.getRequestURI();
         String query = uri.getRawQuery();
         Map<String, Object> parameters=new HashMap<String,Object>();
         String pairs[] = query.split("[&]");
         for(int i = 0; i < pairs.length; i++){
            System.out.println(pairs[i].split("[=]"));
         }
         String response = "<html><head><title>Get Querry</title>";
         response += "</head><body><p>" + query + "</p></body></html>";
         exchange.sendResponseHeaders(200, response.length());
         OutputStream os = exchange.getResponseBody();
         os.write(response.toString().getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }
}
