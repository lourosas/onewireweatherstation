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
   private DSPortAdapter dspa        = null;
   private StopWatch     stopWatch   = null;
   private Calendar      currentDate = null;
   //put into a list to handle multiple observers
   private List<TemperatureObserver> t_o_List  = null;
   private List<HumidityObserver>    h_o_List  = null;
   private List<BarometerObserver>   b_o_List  = null;
   private List<CalculatedObserver>  c_o_List  = null;
   private List<TimeObserver>        ti_o_List = null;
   private List<ExtremeObserver>     ex_o_List = null;

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
      this.currentDate = Calendar.getInstance();
      this.setUpDSPA();
      this.setUpdateRate(DEFAULTUPDATERATE);
   }

   /**/
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
   public void addCalculatedObserver(CalculatedObserver co){
      try{
         this.c_o_List.add(co);
      }
      catch(NullPointerException npe){
         this.c_o_List = new Vector<CalculatedObserver>();
         this.c_o_List.add(co);
      }
   }
   
   /***/
   public void addExtremeObserver(ExtremeObserver eo){
      try{
         this.ex_o_List.add(eo);
      }
      catch(NullPointerException npe){
         this.ex_o_List = new Vector<ExtremeObserver>();
         this.ex_o_List.add(eo);
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
      this.publishDewpoint();
      this.publishHeatIndex();
      //Until I can handle null exceptions better
      this.publishBarometricPressure();
      this.publishExtremes();
   }

   /**
   **/
   public List<String> requestData(String request){
      List<String> returnList = null;
      try{
        WeatherStorage ws = WeatherStorage.getInstance();
        returnList        = ws.requestData(request); 
        System.out.println(request);
        System.out.println(returnList);
      }
      catch(NullPointerException npe){}
      finally{
         return returnList;
      }
   }
   
   /**
   */
   public void setUpdateRate(int mins){
      final int SECONDS      = 60;   //Seconds in a minutue
      final int MILLISECONDS = 1000; //Milli-Seconds in a second
      try{
         this.stopWatch.setQuerryTime(mins*SECONDS*MILLISECONDS);
      }
      catch(NullPointerException npe){
         this.stopWatch = new StopWatch(mins*SECONDS*MILLISECONDS);
         this.stopWatch.addTimeListener(this);
         Thread t = new Thread(this.stopWatch);
         t.start();
         this.stopWatch.start();
      }
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
   /*
   Very Simple:
   Calculate the dewpoint for a given pair of humidity and
   temperature sensors.  This is based on the temperature and
   humidty sensor stacks and is based on the assumption that
   That each temp sensor in the temperature stack corresponds 
   to an exact humidity sensor at the same position in the
   humidity stack.  The dewpoint is a formulaic calculation
   based on temperature and relative humidity and is an
   approximation.  The actual calculation depends upon wetbulb
   and dry bulb measurements.  This is an approximation based on
   the Manus-Tetens formula.
   Td = (243.12 * alpha[t,RH])/(17.62 - alpha[t, RH])
   where alpha[t,RH] = (17.62*t/(243.12 + t)) + ln(RH/100)
   and 0.0 < RH < 100.0.
   Temperature -45 to 60 degrees celsius
   */
   private double calculateDewpoint(){
      final double l = 243.12;  //lambda constant
      final double b =  17.62;  //Beta constant

      double dewpoint = Thermometer.DEFAULTTEMP;

      Thermometer t = Thermometer.getInstance();
      Hygrometer  h = Hygrometer.getInstance();

      //Temperature in Metric Units
      double temp     = t.getTemperature(Units.METRIC);
      double humidity = h.getHumidity();

      if(temp     > Thermometer.DEFAULTTEMP &&
         humidity > Hygrometer.DEFAULTHUMIDITY){
         double alpha = ((b*temp)/(l+temp))+Math.log(humidity*0.01);
         dewpoint = (l * alpha)/(b - alpha);
      }

      return dewpoint;
   }

   /*
   Calculate the heat index for a given pair of humidity and
   temperature sensors.  This is based on the temperature and
   humidity sensor stacks and is based on the assumption that
   each temperature sensor in the temperature stack corresponds
   to an excact humidity sensor at the same position in the
   humidity stack.  The heat index is a formulaic calculation
   based on temprature and relative humidity and is an
   approximation (although pretty good) which is based on sixteen
   calculations.
   heatindex = 16.923                            +
               (.185212*tf)                      +
               (5.37941 * rh)                    -
               (.100254*tf*rh)                   +
               ((9.41685 * 10^-3) * tf^2)        +
               ((7.28898 * 10^-3) * rh^2)        +
               ((3.45372*10^-4) * tf^2*rh)       -
               ((8.14971*10^-4) * tf *rh^2)      +
               ((1.02102*10^-5) * tf^2 * rh^2)   -
               ((3.8646*10^-5) * tf^3)           +
               ((2.91583*10^-5) * rh^3)          +
               ((1.42721*10^-6) * tf^3 * rh)     +
               ((1.97483*10^-7) * tf * rh^3)     -
               ((2.18429*10^-8) * tf^3 * rh^2)   +
               ((8.43296*10^-10) * tf^2 * rh^3)  -
               ((4.81975*10^-11)*t^3*rh^3)
   
   NOTE:  The Heat Index Calculation becomes inaccurate at a Dry Bulb
          less than 70 F.  If that is the case, the default value
          is set.  For those values, the System will have to determine
          The reason for the default Heat Index.  It is up to the
          System to figure out the appropriateness of the Heat Index
          data for display.
   */
   private double calculateHeatIndex(){
      final double MINIMUMTEMP = 70.;
      double heatIndex = Thermometer.DEFAULTTEMP;

      Thermometer t = Thermometer.getInstance();
      Hygrometer  h = Hygrometer.getInstance();

      //For this calculation, the tempertaure needs to be in English
      //units (degrees Fahrenheit)
      double tf = t.getTemperature(Units.ENGLISH);
      double rh = h.getHumidity();

      /*
      Only consider to calculate the HeatIndex if the Temperature
      and Relative Humidity are not the default values (This would
      indicate an issue related to the sensors or network)
      */
      if(tf > Thermometer.DEFAULTTEMP &&
         rh > Hygrometer.DEFAULTHUMIDITY){
         /*If the Temperature and Humidity are valid (at least
         seem valid), go ahead and determine if the temperature
         is high enough for a valid heat index calculation (> 70).
         The Humidity is assumed to be >= 0. by default/expectation
         of the sensor working correctly.
         */
         if(tf >= MINIMUMTEMP){
            heatIndex  = 16.923;
            heatIndex += (0.185212 * tf);
            heatIndex += (5.37941  * rh);
            heatIndex -= ((0.100254) * tf * rh);
            heatIndex += ((9.41685 * 0.001) * tf * tf);
            heatIndex += ((7.28898 * 0.001) * rh * rh);
            heatIndex += ((3.45372 * 0.0001) * tf * tf * rh);
            heatIndex -= ((8.14971 * 0.0001) * tf * rh * rh);
            heatIndex += ((1.02102 * 0.00001) * tf * tf * rh * rh);
            heatIndex -= ((3.8646  * 0.00001) * tf * tf * tf);
            heatIndex += ((2.91583 * 0.00001) * rh * rh * rh);
            heatIndex += ((1.42721 * .000001)* tf * tf * tf *rh);
            heatIndex += ((1.97483 * .0000001) * tf * rh * rh * rh);
            heatIndex -= ((2.18429 * .00000001) *tf*tf*tf* rh * rh);
            heatIndex += ((8.43196 * .0000000001)*tf*tf*rh*rh*rh);
            heatIndex-=((4.81975 * .00000000001)*tf*tf*tf*rh*rh*rh);
         }
      }
      return heatIndex;
   }

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
      try{
         Units  units          = Units.METRIC;
         Sensor barometer      = Barometer.getInstance();
         String type           = barometer.getType();
         double data1          = barometer.measure();
         evt1 = new WeatherEvent(barometer, type, data1, units,
                                                   this.currentDate);

         units = Units.ENGLISH;
         data1 = ((Barometer)barometer).getBarometricPressure(units);
         evt2  = new WeatherEvent(barometer, type, data1, units,
                                                   this.currentDate);
         units = Units.ABSOLUTE;
         data1 = ((Barometer)barometer).getBarometricPressure(units);
         evt3  = new WeatherEvent(barometer, type, data1, units,
                                                   this.currentDate);
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         evt1 = new WeatherEvent(null, "Barometer", evt1.getValue(),
                                             null, this.currentDate);
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<BarometerObserver> i = this.b_o_List.iterator();
         while(i.hasNext()){
            BarometerObserver bo = (BarometerObserver)i.next();
            bo.updatePressure(ws);
         }
      
      }
   }

   /**
   */
   private void publishDewpoint(){
      WeatherEvent evt1 = null;
      WeatherEvent evt2 = null;
      WeatherEvent evt3 = null;
      try{
         Units units       = Units.METRIC;
         //Dewpoint calculation will return in Metric Units, will
         //need to
         //convert to other units.
         double dewpoint   = this.calculateDewpoint();
         double dewpointF  = Thermometer.DEFAULTTEMP;
         double dewpointK  = Thermometer.DEFAULTTEMP;
         evt1 = new WeatherEvent(null, "Dewpoint", dewpoint,
                                            units, this.currentDate);

         if(dewpoint > Thermometer.DEFAULTTEMP){
            dewpointF = WeatherConvert.celsiusToFahrenheit(dewpoint);
            dewpointK = WeatherConvert.celsiusToKelvin(dewpoint);
         }

         units = Units.ENGLISH;
         evt2 = new WeatherEvent(null, "Dewpoint", dewpointF, 
                                            units, this.currentDate);

         units = Units.ABSOLUTE;
         evt3 = new WeatherEvent(null, "Dewpoint", dewpointK, 
                                            units, this.currentDate);

         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         WeatherStorage ws = WeatherStorage.getInstance();
         evt1 = new WeatherEvent(null,"Dewpoint",-999.9,
                                             null, this.currentDate);
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<CalculatedObserver> i = this.c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = (CalculatedObserver)i.next();
            co.updateDewpoint(ws);
         }
      }
   }

   /*
   */
   private void publishExtremes(){
      try{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<ExtremeObserver> i = this.ex_o_List.iterator();
         while(i.hasNext()){
            ExtremeObserver eo = (ExtremeObserver)i.next();
            eo.updateExtremes(ws);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   /**
   */
   private void publishHeatIndex(){
      WeatherEvent evt1 = null;
      WeatherEvent evt2 = null;
      WeatherEvent evt3 = null;
      try{
         Units units       = Units.ENGLISH;
         //Heat Index calculation will return in English Units,
         //will need
         //to convert to other units
         double heatIndexF = this.calculateHeatIndex();
         double heatIndex  = Thermometer.DEFAULTTEMP;
         double heatIndexK = Thermometer.DEFAULTTEMP;
         evt2 = new WeatherEvent(null,"Heat Index",heatIndexF,units,
                                                   this.currentDate);

         if(heatIndexF > Thermometer.DEFAULTTEMP){
            heatIndex=WeatherConvert.fahrenheitToCelsius(heatIndexF);
            heatIndexK=WeatherConvert.fahrenheitToKelvin(heatIndexF);
         }

         units = Units.METRIC;
         evt1 = new WeatherEvent(null,"Heat Index",heatIndex,units,
                                                  this.currentDate);

         units = Units.ABSOLUTE;
         evt3 = new WeatherEvent(null,"Heat Index",heatIndexK,units,
                                                   this.currentDate);

         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         WeatherStorage ws = WeatherStorage.getInstance();
         evt1 = new WeatherEvent(null, "Heat Index", -999.9,
                                             null, this.currentDate);
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<CalculatedObserver> i = this.c_o_List.iterator();
         while(i.hasNext()){
            CalculatedObserver co = (CalculatedObserver)i.next();
            co.updateHeatIndex(ws);
         }
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
      
      evt1 = new WeatherEvent(hygrometer, type, data1, Units.METRIC,
                                                   this.currentDate);
      evt2 = new WeatherEvent(hyg, type, data2, Units.METRIC,
                                                   this.currentDate);
      
      try{
         WeatherExtreme we = WeatherExtreme.getInstance();
         data1  = evt1.getValue();
         units  = evt1.getUnits();
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         WeatherStorage ws = WeatherStorage.getInstance();
         evt1 = new WeatherEvent(null, "Hygrometer", evt1.getValue(),
                                             null, this.currentDate);
         ws.store(evt1);
      }
      finally{
         WeatherStorage ws = WeatherStorage.getInstance();
         Iterator<HumidityObserver> i = this.h_o_List.iterator();
         while(i.hasNext()){
            HumidityObserver ho = (HumidityObserver)i.next();
            ho.updateHumidity(ws);
         }
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
      evt1 = new WeatherEvent(thermometer, type, data, units,
                                                   this.currentDate);
      
      units = Units.ENGLISH;
      data  = ((Thermometer)thermometer).getTemperature(units);
      evt2  = new WeatherEvent(thermometer, type, data, units,
                                                   this.currentDate);
      
      units = Units.ABSOLUTE;
      data  = ((Thermometer)thermometer).getTemperature(units);
      evt3  = new WeatherEvent(thermometer, type, data, units,
                                                   this.currentDate);
      
      try{
         Iterator<TemperatureObserver> i = this.t_o_List.iterator();
         //data  = evt1.getValue();
         //units = evt1.getUnits();
         WeatherStorage ws = WeatherStorage.getInstance();
         ws.store(evt1);
         ws.store(evt2);
         ws.store(evt3);
         while(i.hasNext()){
            TemperatureObserver to = (TemperatureObserver)i.next();
            to.updateTemperature(ws);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /**
   */
   private void publishTimeEvent(){
      this.currentDate = Calendar.getInstance();
      String date = String.format("%tc", this.currentDate.getTime());
      try{
         Iterator<TimeObserver> i = this.ti_o_List.iterator();
         while(i.hasNext()){
            (i.next()).updateTime(date);
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
}
