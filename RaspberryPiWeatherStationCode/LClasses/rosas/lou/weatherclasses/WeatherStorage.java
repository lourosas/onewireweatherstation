/********************************************************************
<GNU Stuff to go here>

The pupose of the WeatherStorage Class is to store all WeatherEvent
data for a given date.  

To appropriately store all the weather data for a given date.

In addition, the purpose of the
WeatherStorage class is to save all that WeatherEvent data for a
given date in a given file.
*********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.*;
import rosas.lou.weatherclasses.*;

/*********************************************************************
*********************************************************************/
public class WeatherStorage{
   static WeatherStorage instance;
   private Hashtable<Calendar, List> temperatureHash;
   private List<Calendar>            temperatureDates;
   private Hashtable<Calendar, List> humidityHash;
   private List<Calendar>            humidityDates;
   private Hashtable<Calendar, List> dewpointHash;
   private List<Calendar>            dewpointDates;
   private Hashtable<Calendar, List> heatIndexHash;
   private List<Calendar>            heatIndexDates;
   private Hashtable<Calendar, List> pressureHash;
   private List<Calendar>            pressureDates;
   private WeatherExtreme            extremeData;
   private Calendar                  currentDate;

   {
      instance        = null;
      temperatureHash = null;
      temperatureDates= null;
      humidityHash    = null;
      humidityDates   = null;
      dewpointHash    = null;
      dewpointDates   = null;
      heatIndexHash   = null;
      heatIndexDates  = null;
      pressureHash    = null;
      pressureDates   = null;
      extremeData     = null;
      currentDate     = null;
   }

   //**********************Constructors*******************************
   /**
   Constructor of no arguments
   NOTE:  The constructor is protected since implementing a Singleton
   */
   protected WeatherStorage(){}

   //***********************Public Methods****************************
   /**
   Return the Singleton Value
   */
   public static WeatherStorage getInstance(){
      if(instance == null){
         instance = new WeatherStorage();
      }
      return instance;
   }

   /*
   */
   public List<String> dewpointFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> dpData = null;
      try{
         //Here is the tricky part:  somehow, need to figure out
         //WHICH Database Instance to access.  There are (going to
         //be) several databases to access.  It all depends the
         //Database instantiated.
         Database database = MySQLWeatherDatabase.getInstance();
         dpData = database.dewpointFromDatabase(month, day, year);
      }
      catch(NullPointerException npe){}
      finally{
         return dpData;
      }
   }

   /**
   */
   public List<WeatherEvent> getLatestData(String type){
      List<WeatherEvent> event = null;
      if(type.toLowerCase().equals("pressure")){
         int size     = this.pressureDates.size();
         Calendar cal = this.pressureDates.get(size - 1);
         event        = this.pressureHash.get(cal);
      }
      else if(type.toLowerCase().equals("humidity")){
         int size     = this.humidityDates.size();
         Calendar cal = this.humidityDates.get(size - 1);
         event        = this.humidityHash.get(cal);
      }
      else if(type.toLowerCase().equals("temperature")){
         int size     = this.temperatureDates.size();
         Calendar cal = this.temperatureDates.get(size - 1);
         event        = this.temperatureHash.get(cal);
      }
      else if(type.toLowerCase().equals("dewpoint")){
         int size     = this.dewpointDates.size();
         Calendar cal = this.dewpointDates.get(size - 1);
         event        = this.dewpointHash.get(cal);
      }
      else if(type.toLowerCase().equals("heatindex")){
         int size     = this.heatIndexDates.size();
         Calendar cal = this.heatIndexDates.get(size - 1);
         event        = this.heatIndexHash.get(cal);
      }
      return event;
   }

   /**
   **/
   public List<WeatherEvent> getMax(String type){
      List<WeatherEvent> maxList = null;
      WeatherEvent maxEvent      = null;

      if(type.toLowerCase().equals("temperature")){
         maxList = this.getMaxTemperatureFromDatabase();
      }
      else if(type.toLowerCase().equals("humidity")){
         maxList = this.getMaxHumidityFromDatabase();
      }
      else if(type.toLowerCase().equals("dewpoint")){
         maxList = this.getMaxDewpointFromDatabase();
      }
      else if(type.toLowerCase().equals("heatindex")){
         maxList = this.getMaxHeatIndexFromDatabase();
      }
      
      else if(type.toLowerCase().equals("pressure")){
         maxList = this.getMaxPressureFromDatabase();
      }
      return maxList;
   }

   /**
   */
   public List<WeatherEvent> getMin(String type){
      List<WeatherEvent> minList = null;
      WeatherEvent      minEvent = null;
      Enumeration<List>        e = null;

      if(type.toLowerCase().equals("temperature")){
         e = this.temperatureHash.elements();
         minList = this.getMinTemperatureFromDatabase();
      }
      else if(type.toLowerCase().equals("humidity")){
         e = this.humidityHash.elements();
         minList = this.getMinHumidityFromDatabase();
      }
      else if(type.toLowerCase().equals("dewpoint")){
         e = this.dewpointHash.elements();
         minList = this.getMinDewpointFromDatabase();
      }
      else if(type.toLowerCase().equals("heatindex")){
         e = this.heatIndexHash.elements();
         minList = this.getMinHeatIndexFromDatabase();
      }
      else if(type.toLowerCase().equals("pressure")){
         e = this.pressureHash.elements();
         minList = this.getMinPressureFromDatabase();
      }
      return minList;
   }

   /*
   */
   public List<String> heatIndexFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> hiData = null;
      try{
         Database database = MySQLWeatherDatabase.getInstance();
         hiData = database.heatIndexFromDatabase(month,day,year);
      }
      catch(NullPointerException npe){}
      finally{
         return hiData;
      }
   }

   /*
   */
   public List<String> humidityFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> hData = null;
      try{
         Database database = MySQLWeatherDatabase.getInstance();
         hData = database.humidityFromDatabase(month,day,year);
      }
      catch(NullPointerException npe){}
      finally{
         return hData;
      }
   }

   /*
   */
   public List<String> pressureFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> pData = null;
      try{
         Database database = MySQLWeatherDatabase.getInstance();
         pData = database.pressureFromDatabase(month,day,year);
      }
      catch(NullPointerException npe){}
      finally{
         return pData;
      }
   }

   /*
   */
   public List<String> temperatureFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> tData = null;
      try{
         Database database = MySQLWeatherDatabase.getInstance();
         tData = database.temperatureFromDatabase(month,day,year);
      }
      catch(NullPointerException npe){}
      finally{
         return tData;
      }
   }

   /////////////////////////Private Methods//////////////////////////
   /**
   **/
   private List<WeatherEvent> getMaxDewpointFromDatabase(){
      List<String>     returnData = null;
      List<WeatherEvent>    dpMax = null;
      WeatherEvent          event = null;
      try{
         String request = new String("SELECT month, day, year, ");
         request += "max(dewptc), max(dewptf), max(dewptk) FROM ";
         request += "dewpointdata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         dpMax = new LinkedList<WeatherEvent>();
         //Create Three different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Dewpoint",
                                  value,
                                  Units.METRIC,
                                  cal);
         dpMax.add(event);
         value = Double.parseDouble(values[4].trim());
         event = new WeatherEvent(null,
                                  "Dewpoint",
                                  value,
                                  Units.ENGLISH,
                                  cal);
         dpMax.add(event);
         value = Double.parseDouble(values[5].trim());
         event = new WeatherEvent(null,
                                  "Dewpoint",
                                  value,
                                  Units.ABSOLUTE,
                                  cal);
         dpMax.add(event);
      }
      catch(IndexOutOfBoundsException oobe){
        oobe.printStackTrace();
      }
      catch(NullPointerException npe){
        npe.printStackTrace();
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      finally{
         return dpMax;
      }            
   }
   
   /**
   **/
   private List<WeatherEvent> getMaxHeatIndexFromDatabase(){
      List<String>     returnData = null;
	   List<WeatherEvent> hiMax    = null;
	   WeatherEvent      event     = null;
	   try{
         String request = new String("SELECT month, day, year, ");
         request += "max(heatindexc), max(heatindexf), ";
         request += "max(heatindexk) FROM ";
         request += "heatindexdata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         hiMax = new LinkedList<WeatherEvent>();
         //Create Three different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Heatindex",
                                  value,
                                  Units.METRIC,
                                  cal);
         hiMax.add(event);
         value = Double.parseDouble(values[4].trim());
         event = new WeatherEvent(null,
                                  "Heatindex",
                                  value,
                                  Units.ENGLISH,
                                  cal);
         hiMax.add(event);
         value = Double.parseDouble(values[5].trim());
         event = new WeatherEvent(null,
                                  "Heatindex",
                                  value,
                                  Units.ABSOLUTE,
                                  cal);
         hiMax.add(event);
	  }
	  catch(IndexOutOfBoundsException oobe){
        oobe.printStackTrace();
     }
	  catch(NullPointerException npe){
        npe.printStackTrace();
     }
	  catch(NumberFormatException nfe){
        nfe.printStackTrace();
     }
	  finally{
		  return hiMax;
	  }
   }
   
   /**
   **/
   private List<WeatherEvent> getMaxHumidityFromDatabase(){
      List<String>       returnData  = null;
      List<WeatherEvent> humidityMax = null;
      WeatherEvent       event       = null;
      try{
         String request = new String("SELECT month, day, year, ");
         request += "max(humidity) FROM ";
         request += "humiditydata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         humidityMax = new LinkedList<WeatherEvent>();
         event = new WeatherEvent(null,
                                  "Hygrometer",
                                  value,
                                  Units.METRIC,
                                  cal);
         humidityMax.add(event);
      }
      catch(IndexOutOfBoundsException oobe){
         oobe.printStackTrace();
      }
      catch(NullPointerException npe){
         System.out.println("Humidity:  that did not work!!!");
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      finally{
         return humidityMax;
      }
   }
   
   /**
   **/
   private List<WeatherEvent> getMaxTemperatureFromDatabase(){
      List<String>    returnData = null;
      List<WeatherEvent> tempMin = null;
      WeatherEvent         event = null;
      try{
         String request = new String("SELECT month, day, year, ");
         request += "max(tempc), max(tempf), max(tempk) FROM ";
         request += "temperaturedata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         tempMin = new LinkedList<WeatherEvent>();
         //Create Three Different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Thermometer",
                                  value,
                                  Units.METRIC,
                                  cal);
        tempMin.add(event);
        value = Double.parseDouble(values[4].trim());
        event = new WeatherEvent(null,
                                 "Thermometer",
                                 value,
                                 Units.ENGLISH,
                                 cal);
        tempMin.add(event);
        value = Double.parseDouble(values[5].trim());
        event = new WeatherEvent(null,
                                 "Thermometer",
                                 value,
                                 Units.ABSOLUTE,
                                 cal);
        tempMin.add(event);      
      }
      catch(IndexOutOfBoundsException oobe){
         oobe.printStackTrace();
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      finally{
         return tempMin;
      }
   }
   
   /**
   **/
   private List<WeatherEvent> getMinDewpointFromDatabase(){
      List<String>     returnData = null;
	   List<WeatherEvent> dpMin    = null;
	   WeatherEvent      event     = null;
	   try{
         String request = new String("SELECT month, day, year, ");
         request += "min(dewptc), min(dewptf), min(dewptk) FROM ";
         request += "dewpointdata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         dpMin = new LinkedList<WeatherEvent>();
         //Create Three different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Dewpoint",
                                  value,
                                  Units.METRIC,
                                  cal);
         dpMin.add(event);
         value = Double.parseDouble(values[4].trim());
         event = new WeatherEvent(null,
                                  "Dewpoint",
                                  value,
                                  Units.ENGLISH,
                                  cal);
         dpMin.add(event);
         value = Double.parseDouble(values[5].trim());
         event = new WeatherEvent(null,
                                  "Dewpoint",
                                  value,
                                  Units.ABSOLUTE,
                                  cal);
         dpMin.add(event);
	  }
	  catch(IndexOutOfBoundsException oobe){
        oobe.printStackTrace();
     }
	  catch(NullPointerException npe){
        npe.printStackTrace();
     }
	  catch(NumberFormatException nfe){
        nfe.printStackTrace();
     }
	  finally{
		  return dpMin;
	  }
   }
   
   /**
   **/
   private List<WeatherEvent> getMinHeatIndexFromDatabase(){
      List<String>     returnData = null;
	   List<WeatherEvent> hiMin    = null;
	   WeatherEvent      event     = null;
	   try{
         String request = new String("SELECT month, day, year, ");
         request += "min(heatindexc), min(heatindexf), ";
         request += "min(heatindexk) FROM ";
         request += "heatindexdata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         hiMin = new LinkedList<WeatherEvent>();
         //Create Three different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Heatindex",
                                  value,
                                  Units.METRIC,
                                  cal);
         hiMin.add(event);
         value = Double.parseDouble(values[4].trim());
         event = new WeatherEvent(null,
                                  "Heatindex",
                                  value,
                                  Units.ENGLISH,
                                  cal);
         hiMin.add(event);
         value = Double.parseDouble(values[5].trim());
         event = new WeatherEvent(null,
                                  "Heatindex",
                                  value,
                                  Units.ABSOLUTE,
                                  cal);
         hiMin.add(event);
	  }
	  catch(IndexOutOfBoundsException oobe){
        oobe.printStackTrace();
     }
	  catch(NullPointerException npe){
        npe.printStackTrace();
     }
	  catch(NumberFormatException nfe){
        nfe.printStackTrace();
     }
	  finally{
		  return hiMin;
	  }      
   }

   /**
   **/
   private List<WeatherEvent> getMinHumidityFromDatabase(){
      List<String>       returnData  = null;
      List<WeatherEvent> humidityMin = null;
      WeatherEvent       event       = null;
      try{
         String request = new String("SELECT month, day, year, ");
         request += "min(humidity) FROM ";
         request += "humiditydata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         humidityMin = new LinkedList<WeatherEvent>();
         event = new WeatherEvent(null,
                                  "Hygrometer",
                                  value,
                                  Units.METRIC,
                                  cal);
         humidityMin.add(event);
      }
      catch(IndexOutOfBoundsException oobe){
         oobe.printStackTrace();
      }
      catch(NullPointerException npe){
         System.out.println("Humidity:  that did not work!!!");
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      finally{
         return humidityMin;
      }
   }
    
   /**
   **/
   private List<WeatherEvent> getMaxPressureFromDatabase(){
      List<String>       returnData  = null;
      List<WeatherEvent> pressureMax = null;
      WeatherEvent       event       = null;
      try{
         String request = new String("SELECT month, day, year, ");
         request += "max(mmHg), max(inHg), max(mB) FROM ";
         request += "pressuredata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         pressureMax = new LinkedList<WeatherEvent>();
         //Create Three Different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Barometer",
                                  value,
                                  Units.METRIC,
                                  cal);
        pressureMax.add(event);
        value = Double.parseDouble(values[4].trim());
        event = new WeatherEvent(null,
                                 "Barometer",
                                 value,
                                 Units.ENGLISH,
                                 cal);
        pressureMax.add(event);
        value = Double.parseDouble(values[5].trim());
        event = new WeatherEvent(null,
                                 "Barometer",
                                 value,
                                 Units.ABSOLUTE,
                                 cal);
        pressureMax.add(event);
      }
      catch(IndexOutOfBoundsException oobe){
         oobe.printStackTrace(); 
      }
      catch(NullPointerException npe){
         System.out.println("That did not work!!!");
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      finally{
         return pressureMax;
      }   
   }
   
   /**
   **/
   private List<WeatherEvent> getMinPressureFromDatabase(){
      List<String>       returnData = null;
      List<WeatherEvent> pressureMin    = null;
      WeatherEvent       event      = null;
      try{
         String request = new String("SELECT month, day, year, ");
         request += "min(mmHg), min(inHg), min(mB) FROM ";
         request += "pressuredata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         pressureMin = new LinkedList<WeatherEvent>();
         //Create Three Different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Barometer",
                                  value,
                                  Units.METRIC,
                                  cal);
        pressureMin.add(event);
        value = Double.parseDouble(values[4].trim());
        event = new WeatherEvent(null,
                                 "Barometer",
                                 value,
                                 Units.ENGLISH,
                                 cal);
        pressureMin.add(event);
        value = Double.parseDouble(values[5].trim());
        event = new WeatherEvent(null,
                                 "Barometer",
                                 value,
                                 Units.ABSOLUTE,
                                 cal);
        pressureMin.add(event);
      }
      catch(IndexOutOfBoundsException oobe){
         oobe.printStackTrace(); 
      }
      catch(NullPointerException npe){
         System.out.println("That did not work!!!");
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      finally{
         return pressureMin;
      }      
   }


   /**
   **/
   private List<WeatherEvent> getMinTemperatureFromDatabase(){
      List<String>       returnData = null;
      List<WeatherEvent> tempMin    = null;
      WeatherEvent       event      = null;
      try{
         String request = new String("SELECT month, day, year, ");
         request += "min(tempc), min(tempf), min(tempk) FROM ";
         request += "temperaturedata where ";
         String month=String.format("%tB",this.currentDate.getTime());
         String day = String.format("%td",this.currentDate.getTime());
         request += "month = '" + month + "' and day = '" + day +"'";
         request += " GROUP BY month, day";
         returnData = this.requestData(request);
         Calendar cal = Calendar.getInstance();
         String data = returnData.get(0);
         String values[] = data.split(",");
         double value = Double.parseDouble(values[3].trim());
         tempMin = new LinkedList<WeatherEvent>();
         //Create Three Different Weather Events for storage
         event = new WeatherEvent(null,
                                  "Thermometer",
                                  value,
                                  Units.METRIC,
                                  cal);
        tempMin.add(event);
        value = Double.parseDouble(values[4].trim());
        event = new WeatherEvent(null,
                                 "Thermometer",
                                 value,
                                 Units.ENGLISH,
                                 cal);
        tempMin.add(event);
        value = Double.parseDouble(values[5].trim());
        event = new WeatherEvent(null,
                                 "Thermometer",
                                 value,
                                 Units.ABSOLUTE,
                                 cal);
        tempMin.add(event);
      }
      catch(IndexOutOfBoundsException oobe){
         oobe.printStackTrace(); 
      }
      catch(NullPointerException npe){
         System.out.println("That did not work!!!");
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      finally{
         return tempMin;
      }
   }

   /**
   **/
   public List<String> requestData(String request){
      List<String> returnData = null;
      try{
         //Database database = Database.getInstance();
         Database database = MySQLWeatherDatabase.getInstance();

         returnData = database.requestData(request);
      }
      catch(NullPointerException npe){}
      finally{
         return returnData;
      }
   }

   /*****************************************************************
   Store the Current Weather Data
   *****************************************************************/
   public void store(WeatherEvent event){
      String type = event.getPropertyName();
      if(this.isTimeToSaveToFile(event.getCalendar())){
         this.saveData();
         this.initialize();
         this.currentDate = event.getCalendar();
      }
      if(type.equals("Thermometer")){
         this.storeTemperatureData(event);
      }
      else if(type.equals("Hygrometer")){
         this.storeHumidityData(event);
      }
      else if(type.equals("Dewpoint")){
         this.storeDewpointData(event);
      }
      else if(type.equals("Heat Index")){
         this.storeHeatIndexData(event);
      }
      else if(type.equals("Barometer")){
         this.storePressureData(event);
      }
   }
   
   /**
   Set the current Weather Extreme data
   */
   public void setWeatherExtreme(WeatherExtreme currentExtreme){
      this.extremeData = currentExtreme;
   }
   
   //************************Private Methods**************************
   /*
   */
   private Iterator<Calendar> getDatesIterator(String type){
      Iterator<Calendar> datesIterator = null;
      if(type.toLowerCase().equals("dewpoint")){
         datesIterator = this.dewpointDates.iterator();
      }
      else if(type.toLowerCase().equals("heatindex")){
         datesIterator = this.heatIndexDates.iterator();
      }
      else if(type.toLowerCase().equals("humidity")){
         datesIterator = this.humidityDates.iterator();
      }
      else if(type.toLowerCase().equals("pressure")){
         datesIterator = this.pressureDates.iterator();
      }
      else if(type.toLowerCase().equals("temperature")){
         this.temperatureDates.iterator();
      }
      return datesIterator;
   }

   /*
   */
   private Hashtable<Calendar, List> getHash(String type){
      Hashtable<Calendar, List> hash = null;
      if(type.toLowerCase().equals("dewpoint")){
         hash = this.dewpointHash;
      }
      else if(type.toLowerCase().equals("heatindex")){
         hash = this.heatIndexHash;
      }
      else if(type.toLowerCase().equals("humidity")){
         hash = this.humidityHash;
      }
      else if(type.toLowerCase().equals("pressure")){
         hash = this.pressureHash;
      }
      else if(type.toLowerCase().equals("temperture")){
         hash = this.temperatureHash;
      }
      return hash;
   }

   /*
   */
   private void initialize(){
      try{
         this.temperatureHash.clear();
         this.temperatureDates.clear();
         this.humidityHash.clear();
         this.humidityDates.clear();
         this.dewpointHash.clear();
         this.dewpointDates.clear();
         this.heatIndexHash.clear();
         this.heatIndexDates.clear();
         this.pressureHash.clear();
         this.pressureDates.clear();
         this.extremeData = null;
         this.currentDate = null;
      }
      catch(NullPointerException npe){}
   }

   /*
   */
   private boolean isTimeToSaveToFile(Calendar date){
      boolean isTime = false;
      int day = Calendar.DAY_OF_YEAR;
      try{
         if(this.currentDate.get(day) != date.get(day)){
            isTime = true;
         }
      }
      catch(NullPointerException npe){
         this.currentDate = date;
      }
      return isTime;
   }
   
   /**
   */
   private void saveData(){
      this.saveTemperatureData();
      this.saveHumidityData();
      this.saveData("dewpoint");
      this.saveData("heatindex");
      this.saveData("pressure");
   }

   /**
   */
   private void saveData(String type){
      FileWriter   fileWriter = null;
      PrintWriter printWriter = null;
      String fileName = new String(this.setDateString(type));
      fileName = fileName.concat(".csv");
      try{
         Hashtable<Calendar, List> hash  = this.getHash(type);
         Enumeration<Calendar>     keys  = hash.keys();
         Iterator<Calendar>        dates=this.getDatesIterator(type);
         List<WeatherEvent>        event = null;
         Iterator<WeatherEvent>    data  = null;

         fileWriter    = new FileWriter(fileName,    true);
         printWriter   = new PrintWriter(fileWriter, true);
         String header = null;
         if((type.toLowerCase().equals("humidty"))){
            header = new String("Time, Humidity");
         }
         else{
            header = new String("Time, Metric, English, Absolute");
         }
         printWriter.println(header);
         while(dates.hasNext()){
            Calendar cal = (Calendar)dates.next();
            event        = hash.get(cal);
            //Get the time; Get the first WeatherEvent in the List
            WeatherEvent first = event.get(0);
            //Print the Date
            printWriter.print(first.toStringDate() + ", ");
            data = event.iterator();
            //Get all the WeatherEvents
            while(data.hasNext()){
               WeatherEvent currentEvent = data.next();
               printWriter.print(currentEvent.toStringValue()+", ");
            }
            printWriter.println();
         }
         event = this.getMax(type);
         data  = event.iterator();
         printWriter.print("Max:  ");
         printWriter.print((event.get(0)).toStringDate() + ", ");
         while(data.hasNext()){
            printWriter.print((data.next()).toStringValue() + ", ");
         }
         printWriter.println();
         event = this.getMin(type);
         data  = event.iterator();
         printWriter.print("Min:  ");
         printWriter.print((event.get(0)).toStringDate() + ", ");
         while(data.hasNext()){
            printWriter.print((data.next()).toStringValue() + ", ");
         }
         fileWriter.close();
         printWriter.close();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /**
   */
   private void saveHumidityData(){
      FileWriter  fileWriter  = null;
      PrintWriter printWriter = null;
      String fileName = new String(this.setDateString("Humidity"));
      fileName = fileName.concat(".csv");
      try{
         List<WeatherEvent> humids   = null;
         Iterator<WeatherEvent> data = null;
         fileWriter  = new FileWriter(fileName, true);
         printWriter = new PrintWriter(fileWriter, true);
         Enumeration<Calendar> keys = this.humidityHash.keys();
         String header = new String("Time, Humidity");
         printWriter.println(header);
         Iterator<Calendar> dates = this.humidityDates.iterator();
         while(dates.hasNext()){
            Calendar cal = (Calendar)dates.next();
            humids       = this.humidityHash.get(cal);
            //Get the time, and the first WeatherEvent in the List
            WeatherEvent first = humids.get(0);
            //Print the date
            printWriter.print(first.toStringDate() + ", ");
            data = humids.iterator();
            //Get all the Humidity Data
            while(data.hasNext()){
               WeatherEvent event = data.next();
               printWriter.print(event.toStringValue() + ", ");
            }
            printWriter.println();
         }
         humids = this.getMax("humidity");
         data   = humids.iterator();
         printWriter.print("Max:  ");
         printWriter.print((humids.get(0)).toStringDate() + ", ");
         while(data.hasNext()){
            printWriter.print((data.next()).toStringValue() + ", ");
         }
         printWriter.println();
         humids = this.getMin("humidity");
         data   = humids.iterator();
         printWriter.print("Min:  ");
         printWriter.print(humids.get(0).toStringDate() + ", ");
         while(data.hasNext()){
            printWriter.print((data.next()).toStringValue() + ", ");
         }
         fileWriter.close();
         printWriter.close();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /**
   */
   private void saveTemperatureData(){
      FileWriter  fileWriter  = null;
      PrintWriter printWriter = null;
      String fileName=new String(this.setDateString("Temperature"));
      fileName = fileName.concat(".csv");
      try{
         List<WeatherEvent> temps    = null;
         Iterator<WeatherEvent> data = null;
         fileWriter = new FileWriter(fileName,    true);
         printWriter= new PrintWriter(fileWriter, true);
         Enumeration<Calendar> keys = this.temperatureHash.keys();
         String header = new String("Time,Temp Metric,");
         header = header.concat("Temp English, ");
         header = header.concat("Temp Absolute");
         printWriter.println(header);
         Iterator<Calendar> dates = this.temperatureDates.iterator();
         while(dates.hasNext()){
            Calendar cal = (Calendar)dates.next();
            temps        = this.temperatureHash.get(cal);
            //Get the Time
            //Get the first WeatherEvent in the List
            WeatherEvent first = temps.get(0);
            //Print the Date
            printWriter.print(first.toStringDate() + ", ");
            data = temps.iterator();
            //Get all the temps
            while(data.hasNext()){
               WeatherEvent event = data.next();
               printWriter.print(event.toStringValue() + ",");
            }
            printWriter.println();
         }
         temps = this.getMax("temperature");
         data = temps.iterator();
         printWriter.print("Max: ");
         printWriter.print((temps.get(0)).toStringDate() + ", ");
         while(data.hasNext()){
            printWriter.print((data.next()).toStringValue() + ", ");
         }
         printWriter.println();
         temps = this.getMin("temperature");
         data  = temps.iterator();
         printWriter.print("Min:  ");
         printWriter.print((temps.get(0)).toStringDate() + ", ");
         while(data.hasNext()){
            printWriter.print((data.next()).toStringValue() + ", ");
         }
         fileWriter.close();
         printWriter.close();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }

   /*
   */
   private String setDateString(String type){
      int month = Calendar.MONTH;
      int day   = Calendar.DAY_OF_MONTH;
      int year  = Calendar.YEAR;

      int currentMonth = this.currentDate.get(month) + 1;
      String dateString = new String(currentMonth + "_");
      dateString = dateString.concat(this.currentDate.get(day)+"_");
      dateString = dateString.concat(""+this.currentDate.get(year));
      dateString = dateString.concat("_" + type);
      return dateString;
   }

   /*
   */
   private void storeDewpointData(WeatherEvent event){
      Calendar cal          = event.getCalendar();
      List<WeatherEvent> dp = null;
      //Go ahead and get the Singleton and populate the latest
      //temperature data
      //Database database = Database.getInstance();
      Database database = MySQLWeatherDatabase.getInstance();
      database.store(event);
      try{
         if(this.dewpointHash.containsKey(cal)){
            dp = this.dewpointHash.get(cal);
         }
         else{
            this.dewpointDates.add(cal);
             dp = new LinkedList<WeatherEvent>();
         }
      }
      catch(NullPointerException npe){
         this.dewpointHash = new Hashtable<Calendar, List>();
         dp = new LinkedList<WeatherEvent>();
         this.dewpointDates = new LinkedList<Calendar>();
         this.dewpointDates.add(cal);
      }
      finally{
         dp.add(event);
         this.dewpointHash.put(cal, dp);
      }
   }

   /*
   */
   private void storeHeatIndexData(WeatherEvent event){
      Calendar cal = event.getCalendar();
      List<WeatherEvent> heatIndices = null;
      //Go ahead and get the Singleton and populate the latest
      //temperature data
      //Database database = Database.getInstance();
      Database database = MySQLWeatherDatabase.getInstance();
      database.store(event);
      try{
         if(this.heatIndexHash.containsKey(cal)){
            heatIndices = this.heatIndexHash.get(cal);
         }
         else{
            heatIndices = new LinkedList<WeatherEvent>();
            this.heatIndexDates.add(cal);
         }
      }
      catch(NullPointerException npe){
         this.heatIndexHash = new Hashtable<Calendar, List>();
         heatIndices = new LinkedList<WeatherEvent>();
         this.heatIndexDates = new LinkedList<Calendar>();
         this.heatIndexDates.add(cal);
      }
      finally{
         heatIndices.add(event);
         this.heatIndexHash.put(cal, heatIndices);
      }
   }

   /*
   */
   private void storeHumidityData(WeatherEvent event){
      Calendar cal = event.getCalendar();
      List<WeatherEvent> humids = null;
      //Go ahead and get the Singleton and populate the latest
      //temperature data
      //Database database = Database.getInstance();
      Database database = MySQLWeatherDatabase.getInstance();
      database.store(event);
      try{
         if(this.humidityHash.containsKey(cal)){
            humids = this.humidityHash.get(cal);
         }
         else{
            this.humidityDates.add(cal);
            humids = new LinkedList<WeatherEvent>();
         }
      }
      catch(NullPointerException npe){
         this.humidityHash  = new Hashtable<Calendar, List>();
         humids = new LinkedList<WeatherEvent>();
         this.humidityDates = new LinkedList<Calendar>();
         this.humidityDates.add(cal);
      }
      finally{
         humids.add(event);
         this.humidityHash.put(cal, humids);
      }
   }

   /*
   */
   private void storePressureData(WeatherEvent event){
      Calendar cal                = null;
      List<WeatherEvent> pressure = null;
      //Go ahead and get the Singleton and populate the latest
      //temperature data
      //Database database = Database.getInstance();
      Database database = MySQLWeatherDatabase.getInstance();
      database.store(event);
      try{
         cal = event.getCalendar();
         if(this.pressureHash.containsKey(cal)){
            pressure = this.pressureHash.get(cal);
         }
         else{
            pressure = new LinkedList<WeatherEvent>();
            this.pressureDates.add(cal);
         }
      }
      catch(NullPointerException npe){
         this.pressureHash = new Hashtable<Calendar, List>();
         pressure = new LinkedList<WeatherEvent>();
         this.pressureDates = new LinkedList<Calendar>();
         this.pressureDates.add(cal);
      }
      finally{
         pressure.add(event);
         this.pressureHash.put(cal, pressure);
      }
   }

   /*
   */
   private void storeTemperatureData(WeatherEvent event){
      Calendar cal = event.getCalendar();
      List<WeatherEvent> temps = null;
      //Go ahead and get the Singleton and populate the latest
      //temperature data
      //Database database = Database.getInstance();
      Database database = MySQLWeatherDatabase.getInstance();
      database.store(event);
      try{
         if(this.temperatureHash.containsKey(cal)){
            temps = this.temperatureHash.get(cal);
         }
         else{
            temps = new LinkedList<WeatherEvent>();
            this.temperatureDates.add(cal);
         }
      }
      catch(NullPointerException npe){
         this.temperatureHash = new Hashtable<Calendar, List>();
         temps = new LinkedList<WeatherEvent>();
         this.temperatureDates = new LinkedList<Calendar>();
         this.temperatureDates.add(cal);
      }
      finally{
         temps.add(event);
         this.temperatureHash.put(cal, temps);
      }
   }
}
/////////////////////////////////////////////////////////////////////
