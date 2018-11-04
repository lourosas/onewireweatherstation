import java.io.*;
import java.net.*;

import com.sun.net.httpserver.*;

public class TestWeatherWebServer{
   public static void main(String[] args){
      try{
         HttpServer server =
                   HttpServer.create(new InetSocketAddress(8000), 0);
         server.createContext("/test", new MyHandler());
         server.setExecutor(null);
         server.start();
      }
      catch(Exception e){ e.printStackTrace(); }
   }

   static class MyHandler implements HttpHandler{
      public void handle(HttpExchange t){
         try{
            String response = "<html><head>";
            response += "<title>This is a Test</title>";
            response += "</head><body><p>Nothing to see here...";
            response  += "</body></html>";
            //String response = "This is a response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
         }
         catch(IOException ioe){ ioe.printStackTrace();}
      }
   }
}

