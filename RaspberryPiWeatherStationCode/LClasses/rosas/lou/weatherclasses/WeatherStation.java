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
   
   /*
   */
   public void mesure(){
      System.out.println("measure");
      this.publishTemperature();
      this.publishHumidity();
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
                  Sensor hygrometer = Hygrometer.getInstance();
                  hygrometer.initialize(units,address,name,this.dspa);
                  System.out.println(hygrometer);
               }
            }
         }
      }
      catch(EmptyStackException ese){}
   }
   
   /**
   */
   private void publishHumidity(){
      Sensor hygrometer = Hygrometer.getInstance();
      System.out.println("" + hygrometer.measure());
   }
   
   /**
   */
   private void publishTemperature(){
      Sensor thermometer = Thermometer.getInstance();
      System.out.println("" + thermometer.measure());
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
