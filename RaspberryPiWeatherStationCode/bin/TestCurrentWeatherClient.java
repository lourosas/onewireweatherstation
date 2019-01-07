import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import rosas.lou.weatherclasses.*;
import com.sun.net.httpserver.*;

public class TestCurrentWeatherClient{
   public static void main(String [] args){
      new TestCurrentWeatherClient();
   }

   public TestCurrentWeatherClient(){
      CurrentWeatherClient cwc = new CurrentWeatherClient();
      CurrentWeatherDataSubscriber cwds =
                                  new CurrentWeatherDataSubscriber();
      CurrentWeatherDataSubscriber chttph = new CurrentHttpHandler();
      CurrentWeatherDataSubscriber dwh    = new DailyWeatherHandler();
      CurrentWeatherDataSubscriber msqls  = new MySQLSubscriber();
      CurrentWeatherDataSubscriber wndrgs=new WundergroundSubscriber();
      cwc.addSubscriber(cwds);
      cwc.addSubscriber(chttph);
      cwc.addSubscriber(dwh);
      cwc.addSubscriber(msqls);
      cwc.addSubscriber(wndrgs);

      Thread thread = new Thread(cwc);
      thread.start();

      Wunderground wg = new Wunderground();
      ((WundergroundSubscriber)wndrgs).addTemperatureHumidityObserver(wg);
      ((WundergroundSubscriber)wndrgs).addBarometerObserver(wg);
      ((WundergroundSubscriber)wndrgs).addCalculatedObserver(wg);
      Thread wgThread = new Thread(wg);
      wgThread.start();

      HttpServer httpserver = null;
      try{
         httpserver =
                  HttpServer.create(new InetSocketAddress(8000),100);
         httpserver.createContext("/", (HttpHandler)chttph);
         httpserver.createContext("/daily", (HttpHandler)dwh);
         httpserver.setExecutor(null);
         httpserver.start();
      }
      catch(BindException be){ be.printStackTrace(); }
      catch(IOException ioe){ ioe.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
   }
}
