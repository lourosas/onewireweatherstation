import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestWeatherNetwork{
   private WeatherNetwork wn;
   public static void main(String [] args){
      new TestWeatherNetwork();
   }

   public TestWeatherNetwork(){
      this.wn = new WeatherNetwork();
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable returnHash = ps.findPorts();
      Enumeration<String> e = returnHash.keys();
      while(e.hasMoreElements()){
         String key = e.nextElement();
         Stack<String> current = (Stack)returnHash.get(key);
         this.findSensors(current);
      }
      //Go ahead and start collecting weather data
      this.wn.collectData();
   }

   public void findSensors(Stack<String> s){
      WeatherStation ws = new WeatherStation();
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         System.out.println(name);
         System.out.println(address);
         //More "direct initialization" to follow
         ws.initializeThermometer(name, address);
      }
      //Need to initialize the Hygrometer SEPARATELY!!!
      ws.initializeHygrometer("DS2438", "6C000000F1DFE526");
      //Need to initialize the Barometer SEPARATELY!!
      ws.initializeBarometer("DS2438", "92000000BCA3EF26");
      //Initialize the WeatherNetwork
      this.wn.initialize(ws);
   }
}
