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
      //Wunderground   wg      = new Wunderground();
      WeatherStation station = new WeatherStation();
      //server.registerWithAWeatherStation(new WeatherStation());
      //station.addBarometerObserver(wg);
      //station.addCalculatedObserver(wg);
      //station.addHumidityObserver(wg);
      //station.addTemperatureObserver(wg);
      //station.addExtremeObserver(wg);
      //Thread nextThread = new Thread(wg);
      //nextThread.start();
      try{
         WeatherServer  server  = new WeatherServer(19000);
         server.registerWithAWeatherStation(station);
         Thread thread = new Thread(server);
         thread.start();
         /*
         WeatherDataHandler wdh = new WeatherDataHandler();
         station.addTemperatureObserver(wdh);
         station.addHumidityObserver(wdh);
         station.addBarometerObserver(wdh);
         station.addCalculatedObserver(wdh);
         station.addExtremeObserver(wdh);
         */
         //BaseWeatherHandler bwh = new BaseWeatherHandler();
         //Declared vs Runtime
         BaseWeatherHandler bwh = new CurrentWeatherHandler();
         BaseWeatherHandler dwh = new DailyWeatherHandler();
         station.addTemperatureObserver(bwh.weatherDataSubscriber());
         station.addHumidityObserver(bwh.weatherDataSubscriber());
         station.addBarometerObserver(bwh.weatherDataSubscriber());
         station.addCalculatedObserver(bwh.weatherDataSubscriber());
         station.addExtremeObserver(bwh.weatherDataSubscriber());
         station.addTemperatureObserver(dwh.weatherDataSubscriber());
         station.addHumidityObserver(dwh.weatherDataSubscriber());
         station.addBarometerObserver(dwh.weatherDataSubscriber());
         station.addCalculatedObserver(dwh.weatherDataSubscriber());
         station.addExtremeObserver(dwh.weatherDataSubscriber());
         //Set up the HTTP Server
         HttpServer httpserver  =
                  HttpServer.create(new InetSocketAddress(8000),100);
         //httpserver.createContext("/",wdh);
         httpserver.createContext("/", bwh);
         httpserver.createContext("/daily", dwh);
         httpserver.setExecutor(null);
         httpserver.start();
      }
      catch(Exception e){ e.printStackTrace(); }
   }
}