import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import rosas.lou.weatherclasses.*;
import com.sun.net.httpserver.*;

public class TestCurrentWeatherClient2{
   /*
   */
   public static void main(String [] args){
      new TestCurrentWeatherClient2();
   }

   /*
   */
   public TestCurrentWeatherClient2(){
      CurrentWeatherClient cwc = new CurrentWeatherClient();
      CurrentWeatherDataSubscriber chttph = new CurrentHttpHandler();
      cwc.addSubscriber(chttph);

      Thread thread = new Thread(cwc);
      thread.start();

      HttpServer httpserver = null;
      try{
         httpserver =
                  HttpServer.create(new InetSocketAddress(10000),100);
         httpserver.createContext("/", (HttpHandler)chttph);
         httpserver.setExecutor(null);
         httpserver.start();
      }
      catch(BindException be){  be.printStackTrace(); }
      catch(IOException  ioe){ ioe.printStackTrace(); }
      catch(Exception      e){   e.printStackTrace(); }
   }
}
