import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestStationReader{
   public static void main(String [] args){
      new TestStationReader();
   }

   public TestStationReader(){
      WeatherStationReader station = new WeatherStationReader();
      SimpleServer cwds = new CurrentWeatherDataServer();
      station.addTemperatureHumidityObserver(cwds);
      station.addBarometerObserver(cwds);
      station.addCalculatedObserver(cwds);
      Thread thread = new Thread(cwds);
      thread.start();
      boolean run = true;
      System.out.println(station.id());
      while(run){
         try{
            station.measure();
            Thread.sleep(60000);
         }
         catch(InterruptedException ie){
            ie.printStackTrace();
            run = false;
         }
      }
   }
}
