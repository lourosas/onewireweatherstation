/**/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import gnu.io.*;
import rosas.lou.weatherclasses.*;
import rosas.lou.clock.StopWatch;
import rosas.lou.clock.TimeFormater;
import rosas.lou.clock.TimeListener;
import rosas.lou.clock.ClockState;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;

//Database Stuff (TBD)
//import java.sql.*;

public class WeatherStation implements TimeListener{
   private Units units;
   private DSPortAdapter dspa                  = null;
   private StopWatch     stopWatch             = null;
   private String        currentDate           = null;
   //put into a list to handle multiple observers
   private List<TemperatureObserver> t_o_List  = null;
   private List<HumidityObserver>    h_o_List  = null;
   private List<BarometerObserver>   b_o_List  = null;
   private List<CalculatedObserver>  c_o_List  = null;
   private List<TimeObserver>        ti_o_List = null;

   //************************Constructors*****************************
   /*
   Constructor of no arguments
   */
   public WeatherStation(){
      this(Units.METRIC);
   }

   /*
   Constructor initializing the Units, et. al...
   */
   public WeatherStation(Units units){
      final int DEFAULTUPDATERATE = 5; //5 minutes
      this.currentDate = new String();
      this.setUpDSPA();
      this.setUpdateRate(DEFAULTUPDATERATE);
   }

   /***/
   public void addBarometerObserver(BarometerObserver bo){
      try{
         this.b_o_List.add(bo);
      }
      catch(NullPointerException npe){
         this.b_o_List = new Vector<BarometerObserver>();
         this.b_o_List.add(bo);
      }
   }   
   
   /***/
   public void addHumidityObserver(HumidityObserver ho){
      try{
         this.h_o_List.add(ho);
      }
      catch(NullPointerException npe){
         this.h_o_List = new Vector<HumidityObserver>();
         this.h_o_List.add(ho);
      }
   }
   
   /***/
   public void addTemperatureObserver(TemperatureObserver to){
      try{
         this.t_o_List.add(to);
      }
      catch(NullPointerException npe){
         this.t_o_List = new Vector<TemperatureObserver>();
         this.t_o_List.add(to);
      }
   }
   
   /***/
   public void addTimeObserver(TimeObserver to){
      try{
         this.ti_o_List.add(to);
      }
      catch(NullPointerException npe){
         this.ti_o_List = new Vector<TimeObserver>();
         this.ti_o_List.add(to);
      }
   }
   
   /*
   */
   public void mesure(){
      this.publishTimeEvent();
      this.publishTemperature();
      this.publishHumidity();
      this.publishBarometricPressure();
   }
   
   ////////////Implementation of the TimeListener Interface///////////
   public void update(ClockState cs){}
   
   public void update(Object o){}
   
   public void update(Object o, ClockState cs){}
   
   public void update(Stack<TimeFormater> tfs, ClockState cs){}
   
   public void update(TimeFormater tf){
      this.mesure();
   }
   
   public void update(TimeFormater tf, ClockState cs){}
   ///////////////////////////////////////////////////////////////////
   
   
   ///////////////////////////////////////////////////////////////////
   /**
   */
   private void findSensors(Stack<String> sensors){
      try{
         Units units = Units.METRIC;
         while(sensors.size() > 0){
            String address = sensors.pop();
            String name    = sensors.pop();
            if(!name.equals("DS1990A")){
               if(name.equals("DS1920") || name.equals("DS18S20")){
                  Sensor thermometer = Thermometer.getInstance();
                  thermometer.initialize(units,address,name,this.dspa);
                  System.out.println(thermometer);
               }
               //Eventually, will need to check the address, since
               //both the hygrometer and barometer have the same name,
               //BUT different addresses:  this IS the biggest PAIN in
               //the ASS with the OneWireSystem!!!
               else if(name.equals("DS2438")){
                  if(address.equals("92000000BCA3EF26")){
                     Sensor barometer = Barometer.getInstance();
                     barometer.initialize(units, address, name,
                                                           this.dspa);
                     System.out.println(barometer);
                  }
                  else{
                     Sensor hygrometer = Hygrometer.getInstance();
                     hygrometer.initialize(units, address, name,
                                                           this.dspa);
                     System.out.println(hygrometer);
                  }
               }
            }
         }
      }
      catch(EmptyStackException ese){}
      catch(Exception e){ e.printStackTrace(); }
   }
   
   /**
   */
   private void publishBarometricPressure(){
      WeatherEvent evt1     = null;
      WeatherEvent evt2     = null;
      WeatherEvent evt3     = null;
      Units  units          = Units.METRIC;
      Sensor barometer      = Barometer.getInstance();
      String type           = barometer.getType();
      double data1          = barometer.measure();
      evt1 = new WeatherEvent(barometer, type, data1, units);

      units = Units.ENGLISH;
      data1 = ((Barometer)barometer).getBarometricPressure(units);
      evt2  = new WeatherEvent(barometer, type, data1, units);

      units = Units.ABSOLUTE;
      data1 = ((Barometer)barometer).getBarometricPressure(units);
      evt3  = new WeatherEvent(barometer, type, data1, units);
      
      try{
         Iterator<BarometerObserver> i = this.b_o_List.iterator();
         while(i.hasNext()){
            BarometerObserver bo = (BarometerObserver)i.next();
            bo.updatePressure(evt1);
            bo.updatePressure(evt2);
            bo.updatePressure(evt3);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /**
   */
   private void publishHumidity(){
      WeatherEvent evt1     = null;
      WeatherEvent evt2     = null;
      Sensor     hygrometer = Hygrometer.getInstance();
      Hygrometer hyg        = Hygrometer.getInstance();
      String     type       = hygrometer.getType();
      double     data1      = hygrometer.measure();
      double     data2      = hyg.getCalculatedHumidity();
      
      evt1 = new WeatherEvent(hygrometer, type, data1, Units.METRIC);
      evt2 = new WeatherEvent(hyg, type, data2, Units.METRIC);
      
      try{
         Iterator<HumidityObserver> i = this.h_o_List.iterator();
         while(i.hasNext()){
            HumidityObserver ho = (HumidityObserver)i.next();
            ho.updateHumidity(evt1);
            ho.updateHumidity(evt2);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /**
   */
   private void publishTemperature(){
      WeatherEvent evt1  = null;
      WeatherEvent evt2  = null;
      WeatherEvent evt3  = null;
      Units units        = Units.METRIC;
      Sensor thermometer = Thermometer.getInstance();
      String type        = thermometer.getType();
      double data        = thermometer.measure(units);
      evt1 = new WeatherEvent(thermometer, type, data, units);
      
      units = Units.ENGLISH;
      data  = ((Thermometer)thermometer).getTemperature(units);
      evt2  = new WeatherEvent(thermometer, type, data, units);
      
      units = Units.ABSOLUTE;
      data  = ((Thermometer)thermometer).getTemperature(units);
      evt3  = new WeatherEvent(thermometer, type, data, units);
      
      try{
         Iterator<TemperatureObserver> i = this.t_o_List.iterator();
         while(i.hasNext()){
            TemperatureObserver to = (TemperatureObserver)i.next();
            to.updateTemperature(evt1);
            to.updateTemperature(evt2);
            to.updateTemperature(evt3);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /**
   */
   private void publishTimeEvent(){
      Calendar cal = Calendar.getInstance();
      String time = String.format("%tc", cal.getTime());
      try{
         Iterator<TimeObserver> i = this.ti_o_List.iterator();
         while(i.hasNext()){
            (i.next()).updateTime(time);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /**
   */
   private void setUpDSPA(){
      final int ADAPTER_DATA_SIZE = 2;
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable hash = ps.findPorts();
      Enumeration<Stack> e = hash.keys();
      while(e.hasMoreElements()){
         Stack<String> key     = (Stack)e.nextElement();
         Stack<String> current = (Stack)hash.get(key);
         if(key.size() == ADAPTER_DATA_SIZE){
            String name = key.pop();
            String port = key.pop();
            try{
               this.dspa=OneWireAccessProvider.getAdapter(name,port);
               this.findSensors(current);
            }
            catch(OneWireIOException ioe){ ioe.printStackTrace(); }
            catch(OneWireException owe){ owe.printStackTrace(); }
         }
      }
   }
      
   /**
   */
   private void setUpdateRate(int mins){
      final int SECONDS      = 60;   //Seconds in a minutue
      final int MILLISECONDS = 1000; //Milli-Seconds in a second
      this.stopWatch = new StopWatch(mins*SECONDS*MILLISECONDS);
      this.stopWatch.addTimeListener(this);
      Thread t = new Thread(this.stopWatch);
      t.start();
      this.stopWatch.start();
   }
}
