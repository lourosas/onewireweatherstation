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
      CurrentWeatherClient cwc  = new CurrentWeatherClient();
      WeatherClientDataSubscriber wds =
                               new DailyWeatherDataSubscriber();
      cwc.addSubscriber(wds);

      Thread thread = new Thread(cwc);
      thread.start();
   }
}
//////////////////////////////////////////////////////////////////////
