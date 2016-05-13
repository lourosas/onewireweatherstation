import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestWeatherStation implements TemperatureObserver,
HumidityObserver, TimeObserver, BarometerObserver, CalculatedObserver,
ExtremeObserver{
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
      ws.addCalculatedObserver(this);
      ws.addExtremeObserver(this);
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

   //Implementation of the Caclulated Observer Interface
   public void updateDewpoint(WeatherEvent evt){
      System.out.print(String.format("DP:  %.2f ", evt.getValue()));
      System.out.println(evt.getUnits());
   }

   //Implementation of the Calculated Observer Interface
   public void updateHeatIndex(WeatherEvent evt){
      System.out.print(String.format("HI:  %.2f ", evt.getValue()));
      System.out.println(evt.getUnits());
   }
   
   //Implementation of the Extreme Observer Interface
   public void updateExtremes(WeatherEvent evt){
      System.out.println(evt.getSource());
      System.out.println(evt.getPropertyName());
      this.updateTemperatureMax(evt);
      this.updateTemperatureMin(evt);
      this.updateHumidityMax(evt);
      this.updateHumidityMin(evt);
   }
   
   //Implementation of the HumidityObserver Interface
   public void updateHumidity(WeatherEvent evt){
      System.out.println(String.format("%.2f%s", evt.getValue(),"%"));
   }
   
   //Implementation of the BarometerObserver Interface
   public void updatePressure(WeatherEvent evt){
      System.out.print(String.format("BP:  %.2f  ", evt.getValue()));
      System.out.println(evt.getUnits());
   }
   
   //Implementation of the TemperatureObserver Interface
   public void updateTemperature(WeatherEvent evt){
      System.out.print(String.format("Temp: %.2f ", evt.getValue()));
      System.out.println(evt.getUnits());
   }
   
   //Implementation of the TimeObserver Interface
   public void updateTime(){}
   
   public void updateTime(String formattedTime){
      System.out.println(formattedTime);
   }
   
   public void updateTime(String mo, String day, String yr){}
   
   public void updateTime(String yr, String mo, String day,
                          String hr, String min, String sec){}

   //Implementation of the Calculated Observer Interface
   public void updateWindChill(WeatherEvent event){}

   //*******************Private Methods*******************************
   /*
   */
   private void updateHumidityMax(WeatherEvent evt){
      System.out.println("Humidity Maximum");
      WeatherExtreme we = (WeatherExtreme)evt.getSource();
      double maxHumidity = we.requestHumidityMax();
      System.out.println(String.format("%.2f%s", maxHumidity, "%"));
      System.out.println(we.requestHumidityMaxDate());
   }

   /*
   */
   private void updateHumidityMin(WeatherEvent evt){
      System.out.println("Humdity Minimum");
      WeatherExtreme we = (WeatherExtreme)evt.getSource();
      double minHumidity = we.requestHumidityMin();
      System.out.println(String.format("%.2f%s", minHumidity, "%"));
      System.out.println(we.requestHumidityMinDate());
   }

   /*
   */
   private void updateTemperatureMax(WeatherEvent evt){
      System.out.println("Temperature Maximum");
      WeatherExtreme we = (WeatherExtreme)evt.getSource();
      double maxMetric = we.requestTemperatureMax(Units.METRIC);
      double maxEnglis = we.requestTemperatureMax(Units.ENGLISH);
      double maxAbsolu = we.requestTemperatureMax(Units.ABSOLUTE);
      System.out.print(String.format("%.2f  ", maxMetric));
      System.out.println(Units.METRIC);
      System.out.print(String.format("%.2f  ", maxEnglis));
      System.out.println(Units.ENGLISH);
      System.out.print(String.format("%.2f  ", maxAbsolu));
      System.out.println(Units.ABSOLUTE);
      System.out.println(we.requestTemperatureMaxDate());
   }

   /*
   */
   private void updateTemperatureMin(WeatherEvent evt){
      System.out.println("Temperature Minimum");
      WeatherExtreme we = (WeatherExtreme)evt.getSource();
      double minMetric = we.requestTemperatureMin(Units.METRIC);
      double minEnglis = we.requestTemperatureMin(Units.ENGLISH);
      double minAbsolu = we.requestTemperatureMin(Units.ABSOLUTE);
      System.out.print(String.format("%.2f  ", minMetric));
      System.out.println(Units.METRIC);
      System.out.print(String.format("%.2f  ", minEnglis));
      System.out.println(Units.ENGLISH);
      System.out.print(String.format("%.2f  ", minAbsolu));
      System.out.println(Units.ABSOLUTE);
      System.out.println(we.requestTemperatureMinDate());
   }
}
