//////////////////////////////////////////////////////////////////////
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import rosas.lou.weatherclasses.*;
import com.sun.net.httpserver.*;

public class TestCurrentWeatherClient3{
   public static void main(String [] args){
      new TestCurrentWeatherClient3();
   }

   public TestCurrentWeatherClient3(){
      System.out.println("Hello World");
      CurrentWeatherClient cwc        = new CurrentWeatherClient();
      CurrentWeatherController cwcontroller =
                        new CurrentWeatherObservationPostController();
      WeatherClientDataSubscriber wds =
                               //new DailyWeatherDataSubscriber();
                                  new CurrentWeatherObservationPost();
      cwc.addSubscriber(wds);
      cwcontroller.addModel((CurrentWeatherDataSubscriber)wds);

      CurrentWeatherView wv = new CurrentWeatherObservationPostView(
                                            "Weather View",
                                            cwcontroller);
      ((CurrentWeatherDataSubscriber)wds).addObserver(wv);

      Thread thread = new Thread(cwc);
      thread.start();
   }
}
//////////////////////////////////////////////////////////////////////
