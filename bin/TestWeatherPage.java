import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestWeatherPage{
   public static void main(String [] args){
      new TestWeatherPage();
   }

   public TestWeatherPage(){
      //WeatherPageController wpc = new WeatherPageController();
      GenericWeatherController wpc = new WeatherPageController();
      //WeatherDatabaseClientView wpv =
      WeatherView wpv = 
      //            new WeatherDatabaseClientView("Web Page View", wpc);
      new WeatherPageView("Web Page View", wpc);
      //WeatherPageView wpv = new WeatherPageView("Web Page View", wpc);
      WeatherPage wp = new WeatherPage();
      wpc.addModel(wp);
      wp.addObserver(wpv);
   }
}
