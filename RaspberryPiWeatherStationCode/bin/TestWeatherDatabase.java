import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import rosas.lou.weatherclasses.*;

public class TestWeatherDatabase{
   public static void main(String [] args){
      new TestWeatherDatabase();
   }

   public TestWeatherDatabase(){
      WeatherDatabase mdb = MySQLWeatherDatabase.getInstance();
      int i = 0;
      while(i++ < 10){
         List<WeatherData> wd =
                             mdb.temperature("December","28","2018");
         Iterator<WeatherData> it = wd.iterator();
         while(it.hasNext()){
            System.out.println(it.next());
         }
         List<WeatherData> hd = mdb.humidity("December","28","2018");
         Iterator<WeatherData> hit = hd.iterator();
         while(hit.hasNext()){
            System.out.println(hit.next());
         }
         List<WeatherData> bd =
                      mdb.barometricPressure("December","28","2018");
         Iterator<WeatherData> bit = bd.iterator();
         while(bit.hasNext()){
            System.out.println(bit.next());
         }
         List<WeatherData> dd=mdb.dewpoint("December","31","2018");
         Iterator<WeatherData> dit = dd.iterator();
         while(dit.hasNext()){
            System.out.println(dit.next());
         }
         List<WeatherData> hid=mdb.heatIndex("December","31","2018");
         Iterator<WeatherData> hiit = hid.iterator();
         while(hiit.hasNext()){
            System.out.println(hiit.next());
         }
         try{
            Thread.sleep(600000);
         }
         catch(InterruptedException ie){}
         catch(NullPointerException npe){}
      }
      System.out.println("TestWeatherDatabase--GoodBye!");
   }
}
