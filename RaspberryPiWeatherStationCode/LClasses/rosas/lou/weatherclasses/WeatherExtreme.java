/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import java.text.DateFormat;
import java.text.ParseException;

public class WeatherExtreme{
   private Calendar currentDate;
   private double   maxTempC;
   private double   maxTempF;
   private double   maxTempK;
   private double   minTempC;
   private double   minTempF;
   private double   minTempK;
   private String   maxTempDate;
   private String   minTempDate;
   private double   maxHumidity;
   private double   minHumidity;
   private String   maxHumidityDate;
   private String   minHumidityDate;
   private double   maxPresM;
   private double   maxPresE;
   private double   maxPresA;
   private double   minPresM;
   private double   minPresE;
   private double   minPresA;
   private Date     maxPressureDate;
   private Date     minPressureDate;
   private double   maxDPC;
   private double   maxDPF;
   private double   maxDPK;
   private double   minDPC;
   private double   minDPF;
   private double   minDPK;
   private Date     maxDPDate;
   private Date     minDPDate;
   private double   maxHIC;
   private double   maxHIF;
   private double   maxHIK;
   private double   minHIC;
   private double   minHIF;
   private double   minHIK;
   private Date     maxHIDate;
   private Date     minHIDate;
   private static   WeatherExtreme instance;
   
   {
      currentDate = null;
      
      maxTempC    =  Thermometer.DEFAULTTEMP;
      maxTempF    =  Thermometer.DEFAULTTEMP;
      maxTempK    =  Thermometer.DEFAULTTEMP;
      minTempC    = -Thermometer.DEFAULTTEMP;
      minTempF    = -Thermometer.DEFAULTTEMP;
      minTempK    = -Thermometer.DEFAULTTEMP;
      maxTempDate = null;
      minTempDate = null;
      
      maxHumidity     =  Hygrometer.DEFAULTHUMIDITY;
      minHumidity     = -Hygrometer.DEFAULTHUMIDITY;
      maxHumidityDate = null;
      minHumidityDate = null;
      
      maxPresM        =  Barometer.DEFAULTPRESSURE;
      maxPresE        =  Barometer.DEFAULTPRESSURE;
      maxPresA        =  Barometer.DEFAULTPRESSURE;
      minPresM        = -Barometer.DEFAULTPRESSURE;
      minPresE        = -Barometer.DEFAULTPRESSURE;
      minPresA        = -Barometer.DEFAULTPRESSURE;
      maxPressureDate = null;
      minPressureDate = null;
      
      maxDPC    =  Thermometer.DEFAULTTEMP;
      maxDPF    =  Thermometer.DEFAULTTEMP;
      maxDPK    =  Thermometer.DEFAULTTEMP;
      minDPC    = -Thermometer.DEFAULTTEMP;
      minDPF    = -Thermometer.DEFAULTTEMP;
      minDPK    = -Thermometer.DEFAULTTEMP;
      maxDPDate = null;
      minDPDate = null;
      
      maxHIC    =  Thermometer.DEFAULTTEMP;
      maxHIF    =  Thermometer.DEFAULTTEMP;
      maxHIK    =  Thermometer.DEFAULTTEMP;
      minHIC    = -Thermometer.DEFAULTTEMP;
      minHIF    = -Thermometer.DEFAULTTEMP;
      minHIK    = -Thermometer.DEFAULTTEMP;
      maxHIDate = null;
      minHIDate = null;
      
      instance  = null;
   }
   
   //*************************Constructors****************************
   /*
   Constructor of no attributes
   NOTE:  the constructor is protected, since implementing a
   Singleton Pattern
   */
   protected WeatherExtreme(){}
   
   //**********************Public Methods*****************************
   /*
   */
   public void checkSetDate(){
      this.checkSetData(Calendar.getInstance());
   }
   
   /*
   */
   public void monitorBarometerExtremes(){
      this.currentDate = Calendar.getInstance();
      this.monitorBarometerExtremes(this.currentDate);
   }
   
   /*
   */
   public void monitorBarometerExtremes(Calendar date){
      this.monitorBarometerMax(date);
      this.monitorBarometerMin(date);
   }
 
   /*
   */ 
   public void monitorDewpointExtremes(double dewpoint){
      this.currentDate = Calendar.getInstance();
      this.monitorDewpointExtremes(this.currentDate, dewpoint);
   }
   
   /*
   MUST Be messured in METRIC!!!
   */
   public void monitorDewpointExtremes(Calendar date,double dewpoint){
      if(dewpoint > Thermometer.DEFAULTTEMP){
         if(dewpoint >= this.maxDPC){
            this.maxDPC = dewpoint;
            this.maxDPF =WeatherConvert.celsiusToFahrenheit(dewpoint);
            this.maxDPK =WeatherConvert.celsiusToKelvin(dewpoint);
            this.maxDPDate = date.getTime();
         }
         else if(dewpoint <= this.minDPC){
            this.minDPC = dewpoint;
            this.minDPF =WeatherConvert.celsiusToFahrenheit(dewpoint);
            this.minDPK =WeatherConvert.celsiusToKelvin(dewpoint);
            this.minDPDate = date.getTime();
         }
      }
   }
   
   /*
   */
   public void monitorHeatIndexExtremes(double heatIndex){
      this.currentDate = Calendar.getInstance();
      this.monitorHeatIndexExtremes(this.currentDate, heatIndex);
   }
   
   /*
   Must Be messured in ENGLISH!!!
   */
   public void monitorHeatIndexExtremes(Calendar date, double hi){
      if(hi > Thermometer.DEFAULTTEMP){
         if(hi >= this.maxHIF){
            this.maxHIF = hi;
            this.maxHIC = WeatherConvert.fahrenheitToCelsius(hi);
            this.maxHIK = WeatherConvert.fahrenheitToKelvin(hi);
            this.maxHIDate = new String(date);
         }
         else if(hi <= this.minHIF){
            this.minHIF = hi;
            this.minHIC = WeatherConvert.fahrenheitToCelsius(hi);
            this.minHIK = WeatherConvert.fahrenheitToKelvin(hi);
            this.minHIDate = new String(date);
         }
      }
   }
   
   /*
   */
   public void monitorHumidityExtremes(){
      Calendar cal = Calendar.getInstance();
      String date = String.format("%tc", cal.getTime());
      this.monitorHumidityExtremes(date);
   }
   
   /*
   */
   public void monitorHumidityExtremes(Date date){
      this.monitorHumidityMax(date);
      this.monitorHumidityMin(date);
   }
   
   /*
   */
   public void monitorTemperatureExtremes(){
      Calendar cal = Calendar.getInstance();
      String date = String.format("%tc", cal.getTime());
      this.monitorTemperatureExtremes(date);
   }
   
   /*
   */
   public void monitorTemperatureExtremes(Date date){
      this.monitorTemperatureMax(date);
      this.monitorTemperatureMin(date);
   }
   
   /*
   Return the Singleton Value
   */
   public static WeatherExtreme getInstance(){
      if(instance == null){
         instance = new WeatherExtreme();
      }
      instance.checkSetDate();
      return instance;
   }
   
   //*********************Private Methods*****************************   
   /*
   */
   private void checkSetDate(Calendar date){
      try{
         int currentDay = this.currentDate.get(Calendar.DAY_OF_MONTH);
         int currentMonth = this.currentDate.get(Calendar.MONTH);
         if((currentMonth != date.get(Calendar.MONTH)) ||
            (currentDay != date.get(Calendar.DAY_OF_MONTH))){
            this.currentDate = date;
            this.initializeExtremeData();
         }
      }
      catch(NullPointerException npe){
         this.currentDate = Calendar.getInstance();
         this.initializeExtremeData();
      }
   }
   
   /*
   */
   private void initializeExtremeData(){
      this.maxTempC    =  Thermometer.DEFAULTTEMP;
      this.maxTempF    =  Thermometer.DEFAULTTEMP;
      this.maxTempK    =  Thermometer.DEFAULTTEMP;
      this.minTempC    = -Thermometer.DEFAULTTEMP;
      this.minTempF    = -Thermometer.DEFAULTTEMP;
      this.minTempK    = -Thermometer.DEFAULTTEMP;
      maxTempDate = null;
      minTempDate = null;
      
      maxHumidity     =  Hygrometer.DEFAULTHUMIDITY;
      minHumidity     = -Hygrometer.DEFAULTHUMIDITY;
      maxHumidityDate = null;
      minHumidityDate = null;
      
      maxPresM        =  Barometer.DEFAULTPRESSURE;
      maxPresE        =  Barometer.DEFAULTPRESSURE;
      maxPresA        =  Barometer.DEFAULTPRESSURE;
      minPresM        = -Barometer.DEFAULTPRESSURE;
      minPresE        = -Barometer.DEFAULTPRESSURE;
      minPresA        = -Barometer.DEFAULTPRESSURE;
      maxPressureDate = null;
      minPressureDate = null;
      
      maxDPC    =  Thermometer.DEFAULTTEMP;
      maxDPF    =  Thermometer.DEFAULTTEMP;
      maxDPK    =  Thermometer.DEFAULTTEMP;
      minDPC    = -Thermometer.DEFAULTTEMP;
      minDPF    = -Thermometer.DEFAULTTEMP;
      minDPK    = -Thermometer.DEFAULTTEMP;
      maxDPDate = null;
      minDPDate = null;
      
      maxHIC    =  Thermometer.DEFAULTTEMP;
      maxHIF    =  Thermometer.DEFAULTTEMP;
      maxHIK    =  Thermometer.DEFAULTTEMP;
      minHIC    = -Thermometer.DEFAULTTEMP;
      minHIF    = -Thermometer.DEFAULTTEMP;
      minHIK    = -Thermometer.DEFAULTTEMP;
      maxHIDate = null;
      minHIDate = null;
   }
   /*
   */
   private void monitorBarometerMax(Calendar date){
      Barometer bar = Barometer.getInstance();
      double pressure = bar.getBarometricPressure(Units.METRIC);
      if(pressure > Barometer.DEFAULTPRESSURE){
         if(pressure >= this.maxPresM){
            this.maxPresM = pressure;
            this.maxPresE = bar.getBarometricPressure(Units.ENGLISH);
            this.maxPresA = bar.getBarometricPressure(Units.ABSOLUTE);
            this.maxPressureDate = date.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorBarometerMin(Calendar date){
      Barometer bar = Barometer.getInstance();
      double pressure = bar.getBarometricPressure(Units.METRIC);
      if(pressure > Barometer.DEFAULTPRESSURE){
         if(pressure <= this.minPresM){
            this.minPresM = pressure;
            this.minPresE = bar.getBarometricPressure(Units.ENGLISH);
            this.minPresA = bar.getBarometricPressure(Units.ABSOLUTE);
            this.minPressureDate = date.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorHumidityMax(String date){
      Hygrometer hygrometer = Hygrometer.getInstance();
      double humidity = hygrometer.getHumidity();
      if(humidity > Hygrometer.DEFAULTHUMIDITY){
         if(humidity >= this.maxHumidity){
            this.maxHumidity = humidity;
            this.maxHumidityDate = new String(date);
         }
      }
   }
   
   /*
   */
   private void monitorHumidityMin(String date){
      Hygrometer hygrometer = Hygrometer.getInstance();
      double humidity = hygrometer.getHumidity();
      if(humidity > Hygrometer.DEFAULTHUMIDITY){
         if(humidity <= this.minHumidity){
            this.minHumidity = humidity;
            this.minHumidityDate = new String(date);
         }
      }
   }
   
   /*
   */
   private void monitorTemperatureMax(String date){
      Thermometer thermometer = Thermometer.getInstance();
      double temp = thermometer.getTemperature(Units.METRIC);
      if(temp > Thermometer.DEFAULTTEMP){
         if(temp >= this.maxTempC){
            this.maxTempC = temp;
            this.maxTempF = thermometer.getTemperature(Units.ENGLISH);
            this.maxTempK =thermometer.getTemperature(Units.ABSOLUTE);
            this.maxTempDate = new String(date);
         }
      }
   }
   
   /*
   */
   private void monitorTemperatureMin(String date){
      Thermometer thermometer = Thermometer.getInstance();
      double temp = thermometer.getTemperature(Units.METRIC);
      if(temp > Thermometer.DEFAULTTEMP){
         if(temp <= this.minTempC){
            this.minTempC = temp;
            this.minTempF = thermometer.getTemperature(Units.ENGLISH);
            this.minTempK =thermometer.getTemperature(Units.ABSOLUTE);
            this.minTempDate = new String(date);
         }
      }
   }
}
