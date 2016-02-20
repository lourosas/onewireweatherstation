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
      //System.out.println(ps);
      //System.out.println(returnHash);
      Enumeration<String> e = returnHash.keys();
      while(e.hasMoreElements()){
         String key = (String)e.nextElement();
         Stack<String> current = (Stack)returnHash.get(key);
         //System.out.println(current);
         //this.printStack(current);
         this.findSensors(current);
      }
   }

   public void findSensors(Stack<String> s){
      WeatherNetwork wn = new WeatherNetwork();
      WeatherStation ws = new WeatherStation();
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         System.out.println(name);
         System.out.println(address);
         System.out.println();
      }
   }
   
   public void printStack(Stack<String> s){
      WeatherStation ws = new WeatherStation();
      //ws.initialize(s);
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         ws.initializeThermometer(name, address);
      }
      System.out.println(ws);
   }
}
