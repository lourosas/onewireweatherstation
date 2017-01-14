import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import rosas.lou.weatherclasses.*;

public class TestWeatherServer{
   public static void main(String[] args){
      new TestWeatherServer();
   }

   public TestWeatherServer(){
      Wunderground   wg      = new Wunderground();
      WeatherStation station = new WeatherStation();
      WeatherServer  server  = new WeatherServer(19000);
      //server.registerWithAWeatherStation(new WeatherStation());
      server.registerWithAWeatherStation(station);
      station.addBarometerObserver(wg);
      station.addCalculatedObserver(wg);
      station.addHumidityObserver(wg);
      station.addTemperatureObserver(wg);
      station.addExtremeObserver(wg);
      Thread thread = new Thread(server);
      thread.start();
      Thread nextThread = new Thread(wg);
      nextThread.start();
      try{
         WeatherDataHandler wdh = new WeatherDataHandler();
         station.addTemperatureObserver(wdh);
         station.addHumidityObserver(wdh);
         station.addBarometerObserver(wdh);
         station.addCalculatedObserver(wdh);
         station.addExtremeObserver(wdh);
         //Set up the HTTP Server
         HttpServer httpserver  =
                  HttpServer.create(new InetSocketAddress(8000),100);
         httpserver.createContext("/weather",wdh);
         httpserver.setExecutor(null);
         httpserver.start();
      }
      catch(Exception e){ e.printStackTrace(); }
   }
}
