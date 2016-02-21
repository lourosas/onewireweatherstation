import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestWeatherStation implements TemperatureObserver,
HumidityObserver, TimeObserver, BarometerObserver{
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
      WeatherStation ws = new WeatherStation();
      ws.addTimeObserver(this);
      ws.addHumidityObserver(this);
      ws.addTemperatureObserver(this);
      ws.addBarometerObserver(this);
   }
   
   public void printStack(Stack<String> s){
      WeatherStation ws = new WeatherStation();
      //ws.initialize(s);
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
      }
   }
   
   //Implementation of the HumidityObserver Interface
   public void updateHumidity(WeatherEvent evt){
      System.out.println(String.format("%.2f%s", evt.getValue(),"%"));
   }
   
   //Implementation of the BarometerObserver Interface
   public void updatePressure(WeatherEvent evt){
      System.out.print(String.format("%.2f  ", evt.getValue()));
      System.out.println(evt.getUnits());
   }
   
   //Implementation of the TemperatureObserver Interface
   public void updateTemperature(WeatherEvent evt){
      System.out.println(evt.getValue() + " " + evt.getUnits());
   }
   
   //Implementation of the TimeObserver Interface
   public void updateTime(){}
   
   public void updateTime(String formattedTime){
      System.out.println(formattedTime);
   }
   
   public void updateTime(String mo, String day, String yr){}
   
   public void updateTime(String yr, String mo, String day,
                          String hr, String min, String sec){}
}
