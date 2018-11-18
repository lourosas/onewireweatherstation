import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;
import com.sun.net.httpserver.*;
import java.net.*;
import java.io.IOException;


public class WeatherStationApp implements TemperatureHumidityObserver,
BarometerObserver, CalculatedObserver{

   public static void main(String [] args){
      new WeatherStationApp();
   }

   public WeatherStationApp(){
      WeatherStation station = new WeatherStation();
      station.addTemperatureHumidityObserver(this);
      station.addBarometerObserver(this);
      station.addCalculatedObserver(this);
      SimpleServer cwds = new CurrentWeatherDataServer();
      station.addTemperatureHumidityObserver(cwds);
      station.addBarometerObserver(cwds);
      station.addCalculatedObserver(cwds);
      Thread thread = new Thread(cwds);
      thread.start();
      BaseWeatherHandler cwh = new CurrentWeatherHandler();
      station.addTemperatureHumidityObserver(cwh);
      station.addBarometerObserver(cwh);
      station.addCalculatedObserver(cwh);
      HttpServer httpserver = null;
      try{
         httpserver =
                 HttpServer.create(new InetSocketAddress(8000), 100);
         httpserver.createContext("/", cwh);
         httpserver.setExecutor(null);
         httpserver.start();
      }
      catch(BindException be){ be.printStackTrace(); }
      catch(IOException ioe){ ioe.printStackTrace(); }
      int i = 0;
      boolean run = true;
      //while(i < 10000){
      while(run){
         Calendar cal = Calendar.getInstance();
         //station.temperature();
         //station.temperatureMetric();
         //station.temperatureEnglish();
         //station.temperatureAbsolute();
         //station.humidityValue();
         //station.humidity();
         //station.barometricPressureAbsolute();
         //station.barometricPressureMetric();
         //station.barometricPressureEnglish();
         //station.barometricPressure();
         //station.dewpoint();
         //station.heatIndex();
         //station.heatIndexEnglish();
         //station.heatIndexMetric();
         //station.heatIndexAbsolute();
         System.out.println("\n"+station.id()+"\n");
         station.measure();
         System.out.println("\n"+String.format("%tc",cal.getTime())+"\n");
         try{
            Thread.sleep(600000);
         }
         catch(InterruptedException ie){ run = false; }
         ++i;
      }
      System.out.println("Good Bye!!!");
   }

   public void updateDewpoint(WeatherEvent event){}
   public void updateDewpoint(WeatherStorage store){}
   public void updateDewpoint(WeatherData data){
      System.out.println(data);
   }
   public void updateDewpointAbsolute(double dp){
   }
   public void updateDewpointEnglish(double dp){
   }
   public void updateDewpointMetric(double dp){
   }
   public void updateHeatIndex(WeatherEvent event){}
   public void updateHeatIndex(WeatherStorage store){}
   public void updateHeatIndex(WeatherData data){
      System.out.println(data);
   }
   public void updateHeatIndexAbsolute(double hi){
   }
   public void updateHeatIndexEnglish(double hi){
   }
   public void updateHeatIndexMetric(double hi){
   }
   public void updateWindChill(WeatherEvent event){}
   public void updatePressure(WeatherEvent event){}
   public void updatePressure(WeatherStorage store){}
   public void updatePressure(WeatherData data){
      System.out.println(data);
   }
   public void updatePressureAbsolute(double pressure){
   }
   public void updatePressureEnglish(double pressure){
   }
   public void updatePressureMetric(double pressure){
   }
   public void updateHumidity(WeatherData data){
      //System.out.println(data.percentageData() + "%");
      System.out.println(data);
   }
   public void updateHumidity(double data){
   }
   public void updateTemperature(WeatherData data){
      System.out.println(data);
   }
   public void updateTemperatureMetric(double data){
   }
   public void updateTemperatureEnglish(double data){
   }
   public void updateTemperatureAbsolute(double data){
   }
}
