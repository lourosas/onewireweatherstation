//******************************************************************
//Weather Extreeme Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************

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
   private Date     maxTempDate;
   private Date     minTempDate;
   private double   maxHumidity;
   private double   minHumidity;
   private Date     maxHumidityDate;
   private Date     minHumidityDate;
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
      this.checkSetDate(Calendar.getInstance());
   }
   
   /*
   */
   public void monitorBarometerExtremes(double pressure, Units units){
      Calendar cal = Calendar.getInstance();
      this.monitorBarometerExtremes(cal, pressure, units);
   }
   
   /*
   */
   public void monitorBarometerExtremes
   (
      Calendar cal,
      double   pressure,
      Units    units
   ){
      this.monitorBarometerMax(cal, pressure, units);
      this.monitorBarometerMin(cal, pressure, units);
   }
   
   /*
   */
   public void monitorDewpointExtremes(double dewpoint, Units units){
      Calendar cal = Calendar.getInstance();
      this.monitorDewpointExtremes(cal, dewpoint, units);
   }
   
   /*
   */
   public void monitorDewpointExtremes
   (
      Calendar cal,
      double   dewpoint,
      Units    units
   ){
      this.monitorDewpointMax(cal, dewpoint, units);
      this.monitorDewpointMin(cal, dewpoint, units);
   }
   
   /*
   */
   public void monitorHeatIndexExtremes(double heatIndex,Units units){
      Calendar cal = Calendar.getInstance();
      this.monitorHeatIndexExtremes(cal, heatIndex, units);
   }
   
   /*
   */
   public void monitorHeatIndexExtremes
   (
      Calendar cal,
      double   heatIndex,
      Units    units
   ){
      this.monitorHeatIndexMax(cal, heatIndex, units);
      this.monitorHeatIndexMin(cal, heatIndex, units);
   }
   
   /*
   */
   public void monitorHumidityExtremes(double humidity, Units units){
      Calendar cal = Calendar.getInstance();
      this.monitorHumidityExtremes(cal, humidity, units);
   }
   
   /*
   */
   public void monitorHumidityExtremes
   (
      Calendar cal,
      double   humidity,
      Units    units
   ){
      this.monitorHumidityMax(cal, humidity, units);
      this.monitorHumidityMin(cal, humidity, units);
   }
   
   /*
   */
   public void monitorTemperatureExtremes(double temp, Units units){
      Calendar cal = Calendar.getInstance();
      String  date = String.format("%tc", cal.getTime());
      this.monitorTemperatureExtremes(cal, temp, units);
   }
   
   /*
   */
   public void monitorTemperatureExtremes
   (
      Calendar cal,
      double   temp,
      Units    units
   ){
      this.monitorTemperatureMax(cal, temp, units);
      this.monitorTemperatureMin(cal, temp, units);
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

   /*
   */
   public double requestDewpointMax(Units units){
      double max = Thermometer.DEFAULTTEMP;
      switch(units){
         case METRIC:
            max = this.maxDPC;
            break;
         case ENGLISH:
            max = this.maxDPF;
            break;
         case ABSOLUTE:
            max = this.maxDPK;
            break;
         default:  max = this.maxDPC;
      }
      return max;
   }

   /*
   */
   public String requestDewpointMaxDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.maxDPDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Max Dewpoint Date");
      }
      finally{
         return dateString;
      }
   }

   /*
   */
   public double requestDewpointMin(Units units){
      double min = -Thermometer.DEFAULTTEMP;
      switch(units){
         case METRIC:
            min = this.minDPC;
            break;
         case ENGLISH:
            min = this.minDPF;
            break;
         case ABSOLUTE:
            min = this.minDPK;
            break;
         default:  min = this.minDPC;
      }
      return min;
   }

   /*
   */
   public String requestDewpointMinDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.minDPDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Min Dewpoint Date");
      }
      finally{
         return dateString;
      }
   }

   /*
   */
   public double requestHeatIndexMax(Units units){
      double max = Thermometer.DEFAULTTEMP;
      switch(units){
         case METRIC:
            max = this.maxHIC;
            break;
         case ENGLISH:
            max = this.maxHIF;
            break;
         case ABSOLUTE:
            max = this.maxHIK;
            break;
         default:  max = this.maxHIC;
      }
      return max;
   }

   /*
   */
   public String requestHeatIndexMaxDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.maxHIDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Max Heat Index Date");
      }
      finally{
         return dateString;
      }
   }
   
   /*
   */
   public double requestHeatIndexMin(Units units){
      double min = -Thermometer.DEFAULTTEMP;
      switch(units){
         case METRIC:
            min = this.minHIC;
            break;
         case ENGLISH:
            min = this.minHIF;
            break;
         case ABSOLUTE:
            min = this.minHIK;
            break;
         default:  min = this.minHIC;
      }
      return min;
   }
   
   /*
   */
   public String requestHeatIndexMinDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.minHIDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Min Heat Index Date");
      }
      finally{
         return dateString;
      }
   }

   /*
   */
   public double requestHumidityMax(){
      double max = Hygrometer.DEFAULTHUMIDITY;
      max = this.maxHumidity;
      return max;
   }

   /*
   */
   public String requestHumidityMaxDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.maxHumidityDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Max Humidity Date");
      }
      finally{
         return dateString;
      }
   }

   /*
   */
   public double requestHumidityMin(){
      double min = -Hygrometer.DEFAULTHUMIDITY;
      min = this.minHumidity;
      return min;
   }

   /*
   */
   public String requestHumidityMinDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.minHumidityDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Min Humidity Date");
      }
      finally{
         return dateString;
      }
   }

   /*
   */
   public double requestTemperatureMax(Units units){
      double max = Thermometer.DEFAULTTEMP;
      switch(units){
         case METRIC:
            max = this.maxTempC;
            break;
         case ENGLISH:
            max = this.maxTempF;
            break;
         case ABSOLUTE:
            max = this.maxTempK;
            break;
         default:
            max = this.maxTempC;
      }
      return max;
   }

   /*
   */
   public String requestTemperatureMaxDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.maxTempDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Max Temp Date");
      }
      finally{
         return dateString;
      }
   }

   /*
   */
   public double requestTemperatureMin(Units units){
      double min = -Thermometer.DEFAULTTEMP;
      switch(units){
         case METRIC:
            min = this.minTempC;
            break;
         case ENGLISH:
            min = this.minTempF;
            break;
         case ABSOLUTE:
            min = this.minTempK;
            break;
         default:
            min = this.minTempC;
      }
      return min;
   }

   /*
   */
   public String requestTemperatureMinDate(){
      String dateString = null;
      try{
         dateString = String.format("%tc", this.minTempDate);
      }
      catch(NullPointerException npe){
         dateString = new String("No Min Temp Date");
      }
      finally{
         return dateString;
      }
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
   private void monitorBarometerMax
   (
      Calendar cal,
      double   pressure,
      Units    units
   ){
      if(pressure > Barometer.DEFAULTPRESSURE){
         double pressureM = Barometer.DEFAULTPRESSURE;
         double pressureE = Barometer.DEFAULTPRESSURE;
         double pressureA = Barometer.DEFAULTPRESSURE;
         switch(units){
            case METRIC:
            case NULL:
               pressureM = pressure;
               pressureE = 
                        WeatherConvert.millimetersToInches(pressureM);
               pressureA =WeatherConvert.inchesToMillibars(pressureE);
               break;
            case ENGLISH:
               pressureE = pressure;
               pressureM =
                        WeatherConvert.inchesToMillimeters(pressureE);
               pressureA =WeatherConvert.inchesToMillibars(pressureE);
               break;
            case ABSOLUTE:
               pressureA = pressure;
               pressureM =
                     WeatherConvert.millibarsToMillimeters(pressureA);
               pressureE =
                          WeatherConvert.millibarsToInches(pressureA);
               break;
            default:
         }
         if(pressureM >= this.maxPresM){
            this.maxPresM        = pressureM;
            this.maxPresE        = pressureE;
            this.maxPresA        = pressureA;
            this.maxPressureDate = cal.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorBarometerMin
   (
      Calendar cal,
      double   pressure,
      Units    units
   ){
      if(pressure > Barometer.DEFAULTPRESSURE){
         double pressureM = Barometer.DEFAULTPRESSURE;
         double pressureE = Barometer.DEFAULTPRESSURE;
         double pressureA = Barometer.DEFAULTPRESSURE;
         switch(units){
            case METRIC:
            case NULL:
               pressureM = pressure;
               pressureE = 
                        WeatherConvert.millimetersToInches(pressureM);
               pressureA =WeatherConvert.inchesToMillibars(pressureE);
               break;
            case ENGLISH:
               pressureE = pressure;
               pressureM =
                        WeatherConvert.inchesToMillimeters(pressureE);
               pressureA =WeatherConvert.inchesToMillibars(pressureE);
               break;
            case ABSOLUTE:
               pressureA = pressure;
               pressureM =
                     WeatherConvert.millibarsToMillimeters(pressureA);
               pressureE =
                          WeatherConvert.millibarsToInches(pressureA);
               break;
            default:
         }
         if(pressureM <= this.minPresM){
            this.minPresM        = pressureM;
            this.minPresE        = pressureE;
            this.minPresA        = pressureA;
            this.minPressureDate = cal.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorDewpointMax
   (
      Calendar cal,
      double   dewpoint,
      Units    units
   ){
      if(dewpoint > Thermometer.DEFAULTTEMP){
         double dpC = Thermometer.DEFAULTTEMP;
         double dpF = Thermometer.DEFAULTTEMP;
         double dpK = Thermometer.DEFAULTTEMP;
         switch(units){
            case METRIC:
            case   NULL:
               dpC = dewpoint;
               dpF = WeatherConvert.celsiusToFahrenheit(dpC);
               dpK = WeatherConvert.celsiusToKelvin(dpC);
               break;
            case ENGLISH:
               dpF = dewpoint;
               dpC = WeatherConvert.fahrenheitToCelsius(dpF);
               dpK = WeatherConvert.fahrenheitToKelvin(dpF);
               break;
            case ABSOLUTE:
               dpK = dewpoint;
               dpC = WeatherConvert.kelvinToCelsius(dpK);
               dpF = WeatherConvert.kelvinToFahrenheit(dpK);
               break;
            default:
         }
         if(dpC >= this.maxDPC){
            this.maxDPC    = dpC;
            this.maxDPF    = dpF;
            this.maxDPK    = dpK;
            this.maxDPDate = cal.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorDewpointMin
   (
      Calendar cal,
      double   dewpoint,
      Units    units
   ){
      if(dewpoint > Thermometer.DEFAULTTEMP){
         double dpC = Thermometer.DEFAULTTEMP;
         double dpF = Thermometer.DEFAULTTEMP;
         double dpK = Thermometer.DEFAULTTEMP;
         switch(units){
            case METRIC:
            case   NULL:
               dpC = dewpoint;
               dpF = WeatherConvert.celsiusToFahrenheit(dpC);
               dpK = WeatherConvert.celsiusToKelvin(dpC);
               break;
            case ENGLISH:
               dpF = dewpoint;
               dpC = WeatherConvert.fahrenheitToCelsius(dpF);
               dpK = WeatherConvert.fahrenheitToKelvin(dpF);
               break;
            case ABSOLUTE:
               dpK = dewpoint;
               dpC = WeatherConvert.kelvinToCelsius(dpK);
               dpF = WeatherConvert.kelvinToFahrenheit(dpK);
               break;
            default:
         }
         if(dpC <= this.minDPC){
            this.minDPC    = dpC;
            this.minDPF    = dpF;
            this.minDPK    = dpK;
            this.minDPDate = cal.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorHeatIndexMax
   (
      Calendar cal,
      double   heatIndex,
      Units    units
   ){
      if(heatIndex > Thermometer.DEFAULTTEMP){
         double hiC = Thermometer.DEFAULTTEMP;
         double hiF = Thermometer.DEFAULTTEMP;
         double hiA = Thermometer.DEFAULTTEMP;
         switch(units){
            case METRIC:
            case   NULL:
               hiC = heatIndex;
               hiF = WeatherConvert.celsiusToFahrenheit(hiC);
               hiA = WeatherConvert.celsiusToKelvin(hiC);
               break;
            case ENGLISH:
               hiF = heatIndex;
               hiC = WeatherConvert.fahrenheitToCelsius(hiF);
               hiA = WeatherConvert.fahrenheitToKelvin(hiA);
               break;
            case ABSOLUTE:
               hiA = heatIndex;
               hiF = WeatherConvert.kelvinToFahrenheit(hiA);
               hiC = WeatherConvert.kelvinToCelsius(hiA);
               break;
            default:
         }
         if(hiC >= this.maxHIC){
            this.maxHIC    = hiC;
            this.maxHIF    = hiF;
            this.maxHIK    = hiA;
            this.maxHIDate = cal.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorHeatIndexMin
   (
      Calendar cal,
      double   heatIndex,
      Units    units
   ){
      if(heatIndex > Thermometer.DEFAULTTEMP){
         double hiC = Thermometer.DEFAULTTEMP;
         double hiF = Thermometer.DEFAULTTEMP;
         double hiA = Thermometer.DEFAULTTEMP;
         switch(units){
            case METRIC:
            case   NULL:
               hiC = heatIndex;
               hiF = WeatherConvert.celsiusToFahrenheit(hiC);
               hiA = WeatherConvert.celsiusToKelvin(hiC);
               break;
            case ENGLISH:
               hiF = heatIndex;
               hiC = WeatherConvert.fahrenheitToCelsius(hiF);
               hiA = WeatherConvert.fahrenheitToKelvin(hiA);
               break;
            case ABSOLUTE:
               hiA = heatIndex;
               hiF = WeatherConvert.kelvinToFahrenheit(hiA);
               hiC = WeatherConvert.kelvinToCelsius(hiA);
               break;
            default:
         }
         if(hiC <= this.minHIC){
            this.minHIC    = hiC;
            this.minHIF    = hiF;
            this.minHIK    = hiA;
            this.minHIDate = cal.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorHumidityMax
   (
      Calendar cal,
      double   humidity,
      Units    units
   ){
      if(humidity > Hygrometer.DEFAULTHUMIDITY &&
         humidity >= this.maxHumidity){
         this.maxHumidity     = humidity;
         this.maxHumidityDate = cal.getTime();
      }
   }
   
   /*
   */
   private void monitorHumidityMin
   (
      Calendar cal,
      double   humidity,
      Units    units
   ){
      if(humidity > Hygrometer.DEFAULTHUMIDITY &&
         humidity <= this.minHumidity){
         this.minHumidity     = humidity;
         this.minHumidityDate = cal.getTime();
      }
   }
   
   /*
   */
   private void monitorTemperatureMax
   (
      Calendar cal,
      double   temp,
      Units    units
   ){
      if(temp > Thermometer.DEFAULTTEMP){
         double tempC = Thermometer.DEFAULTTEMP;
         double tempF = Thermometer.DEFAULTTEMP;
         double tempK = Thermometer.DEFAULTTEMP;
         if(units == Units.METRIC || units == Units.NULL){
            tempC = temp;
            tempF = WeatherConvert.celsiusToFahrenheit(tempC);
            tempK = WeatherConvert.celsiusToKelvin(tempC);
         }
         else if(units == Units.ENGLISH){
            tempF = temp;
            tempC = WeatherConvert.fahrenheitToCelsius(tempF);
            tempK = WeatherConvert.fahrenheitToKelvin(tempF);
         }
         else if(units == Units.ABSOLUTE){
            tempK = temp;
            tempF = WeatherConvert.kelvinToFahrenheit(tempK);
            tempC = WeatherConvert.kelvinToCelsius(tempK);
         }
         if(tempC >= this.maxTempC){
            this.maxTempC    = tempC;
            this.maxTempF    = tempF;
            this.maxTempK    = tempK;
            this.maxTempDate = cal.getTime();
         }
      }
   }
   
   /*
   */
   private void monitorTemperatureMin
   (
      Calendar cal,
      double   temp,
      Units    units
   ){
      if(temp > Thermometer.DEFAULTTEMP){
         double tempC = Thermometer.DEFAULTTEMP;
         double tempF = Thermometer.DEFAULTTEMP;
         double tempK = Thermometer.DEFAULTTEMP;
         if(units == Units.METRIC || units == Units.NULL){
            tempC = temp;
            tempF = WeatherConvert.celsiusToFahrenheit(tempC);
            tempK = WeatherConvert.celsiusToKelvin(tempC);
         }
         else if(units == Units.ENGLISH){
            tempF = temp;
            tempC = WeatherConvert.fahrenheitToCelsius(tempF);
            tempK = WeatherConvert.fahrenheitToKelvin(tempF);         
         }
         else if(units == Units.ABSOLUTE){
            tempK = temp;
            tempF = WeatherConvert.kelvinToFahrenheit(tempK);
            tempC = WeatherConvert.kelvinToCelsius(tempK);        
         }
         if(tempC <= this.minTempC){
            this.minTempC    = tempC;
            this.minTempF    = tempF;
            this.minTempK    = tempK;
            this.minTempDate = cal.getTime();
         }
      }
   }
}
