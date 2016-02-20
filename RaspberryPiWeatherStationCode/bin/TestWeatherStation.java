import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestWeatherStation{
   public static void main(String [] args){
      new TestWeatherStation();
   }
   
   public TestWeatherStation(){
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable returnHash = ps.findPorts();
      Enumeration<Stack> e = returnHash.keys();
      while(e.hasMoreElements()){
         Stack<String> key     = (Stack)e.nextElement();
         Stack<String> current = (Stack)returnHash.get(key);
         this.findSensors(current);
      }
   }

   public void findSensors(Stack<String> s){
      //WeatherNetwork wn = new WeatherNetwork();
      WeatherStation ws = new WeatherStation();
      //ws.initialize(s);
      /*
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         if(!name.equals("DS1990A")){
            System.out.println(name);
            System.out.println(address);
            System.out.println();
         }
      }
      */
   }
   
   public void printStack(Stack<String> s){
      WeatherStation ws = new WeatherStation();
      //ws.initialize(s);
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         //ws.initializeThermometer(name, address);
      }
      //System.out.println(ws);
   }
}
