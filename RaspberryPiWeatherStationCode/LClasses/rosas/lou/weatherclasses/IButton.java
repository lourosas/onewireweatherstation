/*
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class IButton{
   //Default Logging Rate set to 600 sec (10 min.)
   public static final int DEFAULT_LOGGING_RATE =
                                    MissionLog.DEFAULT_LOGGING_RATE;
   public static final int DEFAULT_DELAY =
                                     MissionLog.DEFAULT_START_DELAY;
   private Units units;
   private MissionLog missionLog;
   private String name;
   private String address;
   private double heatIndex = Double.NaN;
   private double dewPoint  = Double.NaN;
   //
   private List<Double> dewpointList      = null;
   private List<Double> heatIndexList     = null;
   private List<Double> humidityList      = null;
   private List<Date>   humidityTimeList  = null;   
   private List<Double> temperatureList   = null;
   private List<Date>   tempTimeList      = null;

   //Put listeners in a list to handle multiple listeners
   private List<MemoryListener>  ml_List  = null;
   private List<MissionListener> mis_List = null;
   private List<LogListener>     log_List = null;
   
   //State Data
   //New Mission Settings Data
   private int     sampleRate;
   private int     startDelay;
   private boolean isRolloverEnabled;
   private boolean isClockSynchronized;
   private boolean isHumidityChannelEnabled;
   private boolean isTemperatureChannelEnabled;
   private Units   newMissionTempAlarmUnits;
   private double  lowTemperatureAlarm;
   private double  highTemperatureAlarm;
   private double  lowHumidityAlarm;
   private double  highHumidityAlarm;
     
   //*********************Public Methods***************************
   /*
   */
   public IButton(){
      this.findSensors();
      this.setUnits(Units.METRIC);
      this.setRolloverEnabled(false);
      this.setSynchronizedClock(false);
      this.setNewMissionTempAlarmUnits(Units.METRIC);
      this.setLowTemperatureAlarm(Double.NaN);
      this.setLowHumidityAlarm(Double.NaN);
      this.setHighTemperatureAlarm(Double.NaN);
      this.setHighHumidityAlarm(Double.NaN);
      this.setHumidityChannelEnabled(true);
      this.setTemperatureChannelEnabled(true);
      this.setSampleRate(DEFAULT_LOGGING_RATE);
      this.setStartDelay(DEFAULT_DELAY);
   }
   
   /*
   */
   public IButton
   (
      String name,
      String address,
      String adapterName,
      String adapterPort
   ){
      this.setName(name);
      this.setAddress(address);
      this.setUpMissionLog(adapterName, adapterPort);
      this.setUnits(Units.METRIC);
      this.setRolloverEnabled(false);
      this.setSynchronizedClock(false);
      this.setNewMissionTempAlarmUnits(Units.METRIC);
      this.setLowTemperatureAlarm(Double.NaN);
      this.setLowHumidityAlarm(Double.NaN);
      this.setHighTemperatureAlarm(Double.NaN);
      this.setHighHumidityAlarm(Double.NaN);
      this.setHumidityChannelEnabled(true);
      this.setTemperatureChannelEnabled(true);
      this.setSampleRate(DEFAULT_LOGGING_RATE);
      this.setStartDelay(DEFAULT_DELAY);
   }
   
   /*
   Register a Log Listener
   */
   public void addLogListener(LogListener ll){
      try{
         this.log_List.add(ll);
      }
      catch(NullPointerException npe){
         //Save the data off in a Vector
         this.log_List = new Vector<LogListener>();
         this.log_List.add(ll);
      }
   }
   
   /*
   Register a Memory Listener
   */
   public void addMemoryListener(MemoryListener ml){
      try{
         this.ml_List.add(ml);
      }
      catch(NullPointerException npe){
         //Save the data off in a Vector
         this.ml_List = new Vector<MemoryListener>();
         this.ml_List.add(ml);
      }
   }
   
   /*
   Register a Mission Listener
   */
   public void addMissionListener(MissionListener ml){
      try{
         this.mis_List.add(ml);
      }
      catch(NullPointerException npe){
         this.mis_List = new Vector<MissionListener>();
         this.mis_List.add(ml);
      }
   }
   
   /*
   */
   public void clearMemory(){
      String memoryEventString = new String();
      try{
         this.missionLog.clearLog();
         memoryEventString = new String("Memory Cleared ");
         memoryEventString = memoryEventString.concat("on iButton");
         memoryEventString = memoryEventString.concat(" device");
      }
      catch(MemoryException me){
         memoryEventString = new String(me.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         memoryEventString = new String(npe.getMessage());
      }
      finally{
         this.publishMemoryEvent(memoryEventString);
      }
   }
   
   /*
   */
   public String getAddress(){
      return this.address;
   }
   
   /*
   */
   public MissionLog getMissionLog(){
      return this.missionLog;
   }
   
   /*
   */
   public String getName(){
      return this.name;
   }
   
   /*
   */
   public Units getUnits(){
      return this.units;
   }
   
   /**
   Need both the Temperature and Humidity for this to work
   */
   public void requestDewpointData(){
      List<Double> dewpointList   = new LinkedList<Double>();
      List<Double> kDewpointList  = new LinkedList<Double>();
      List<Double> fDewpointList  = new LinkedList<Double>();
      double minDP  = Double.NaN; double maxDP  = Double.NaN;
      double minDPk = Double.NaN; double maxDPk = Double.NaN;
      double minDPf = Double.NaN; double maxDPf = Double.NaN;
      //Log Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled() &&
            this.getHumidityChannelEnabled()){
            Iterator t = this.temperatureList.iterator();
            Iterator h = this.humidityList.iterator();
            while(t.hasNext()){
               Double temperature = (Double)t.next();
               Double humidity    = (Double)h.next();
               Double dp = 
                      this.calculateDewpoint(temperature, humidity);
               dewpointList.add(dp);
            }
            minDP = this.findMin(dewpointList);
            maxDP = this.findMax(dewpointList);
            kDewpointList = new LinkedList<Double>(dewpointList);
            fDewpointList = new LinkedList<Double>(dewpointList);
            this.convertCelsiusToKelvin(kDewpointList);
            this.convertCelsiusToFahrenheit(fDewpointList);
            minDPk = WeatherConvert.celsiusToKelvin(minDP);
            maxDPk = WeatherConvert.celsiusToKelvin(maxDP);
            minDPf = WeatherConvert.celsiusToFahrenheit(minDP);
            maxDPf = WeatherConvert.celsiusToFahrenheit(maxDP);
            les = new String("Dew Point Calculated and sent to");
            les = les.concat(" listeners");
         }
         else{
            les = new String("Dew Point not currently able to be ");
            les = les.concat("calculated");
         }
      }
      catch(IndexOutOfBoundsException ibe){
         ibe.printStackTrace();
         les = new String(ibe.getMessage());
         dewpointList.add(new Double(Double.NaN));
         kDewpointList.add(new Double(Double.NaN));
         fDewpointList.add(new Double(Double.NaN));
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
         dewpointList.add(new Double(Double.NaN));
         kDewpointList.add(new Double(Double.NaN));
         fDewpointList.add(new Double(Double.NaN));
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         les = new String(nfe.getMessage());
         dewpointList.add(new Double(Double.NaN));
         kDewpointList.add(new Double(Double.NaN));
         fDewpointList.add(new Double(Double.NaN));
      }
      finally{
         this.publishDewpointEvent(les, dewpointList, minDP, maxDP,
                                                      Units.METRIC);
         this.publishDewpointEvent(les, kDewpointList, minDPk,
                                            maxDPk, Units.ABSOLUTE);
         this.publishDewpointEvent(les, fDewpointList, minDPf,
                                             maxDPf, Units.ENGLISH);
      }      
   }
   
   /**
   */
   public void requestHeatIndexData(){
      List<Double> heatIndexList  = new LinkedList<Double>();
      List<Double> kheatIndexList = new LinkedList<Double>();
      List<Double> fheatIndexList = new LinkedList<Double>();
      double minHI  = Double.NaN; double maxHI  = Double.NaN;
      double minHIk = Double.NaN; double maxHIk = Double.NaN;
      double minHIf = Double.NaN; double maxHIf = Double.NaN;
      //Log Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled() &&
            this.getHumidityChannelEnabled()){
            Iterator t = this.temperatureList.iterator();
            Iterator h = this.humidityList.iterator();
            while(t.hasNext()){
               Double temp = (Double)t.next();
               Double humi = (Double)h.next();
               Double hi = this.calculateHeatIndex(temp, humi);
               heatIndexList.add(hi);
            }
            kheatIndexList = new LinkedList<Double>(heatIndexList);
            fheatIndexList = new LinkedList<Double>(heatIndexList);
            this.convertCelsiusToKelvin(kheatIndexList);
            this.convertCelsiusToFahrenheit(fheatIndexList);
            minHI  = this.findMin(heatIndexList);
            maxHI  = this.findMax(heatIndexList);
            minHIk = WeatherConvert.celsiusToKelvin(minHI);
            maxHIk = WeatherConvert.celsiusToKelvin(maxHI);
            minHIf = WeatherConvert.celsiusToFahrenheit(minHI);
            maxHIf = WeatherConvert.celsiusToFahrenheit(maxHI);
            les = new String("Heat Index Calculated and sent ");
            les = les.concat("to listeners");
         }
         else{
            les = new String("Heat Index not currently able to be");
            les = les.concat("calculated");
         }
      }
      catch(IndexOutOfBoundsException ibe){
         ibe.printStackTrace();
         les = new String(ibe.getMessage());
         heatIndexList.add(new Double(Double.NaN));
         kheatIndexList.add(new Double(Double.NaN));
         fheatIndexList.add(new Double(Double.NaN));
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
         heatIndexList.add(new Double(Double.NaN));
         kheatIndexList.add(new Double(Double.NaN));
         fheatIndexList.add(new Double(Double.NaN));
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         les = new String(nfe.getMessage());
         heatIndexList.add(new Double(Double.NaN));
         kheatIndexList.add(new Double(Double.NaN));
         fheatIndexList.add(new Double(Double.NaN));
      }
      finally{
         this.publishHeatIndexEvent(les, heatIndexList, minHI,
                                               maxHI, Units.METRIC);
         this.publishHeatIndexEvent(les, kheatIndexList, minHIk,
                                            maxHIk, Units.ABSOLUTE);
         this.publishHeatIndexEvent(les, fheatIndexList, minHIf,
                                             maxHIf, Units.ENGLISH);
      }
   }
   
   /**
   Get mission data (Temperature data in the current Units that are
   set)
   */
   public void requestMissionData
   (
      boolean forTemperature,
      boolean forHumidity
   ){
      if(forTemperature){
         this.requestTemperatureTimeData();
         this.requestTemperatureData();
      }
      if(forHumidity){
         this.requestHumidityTimeData();
         this.requestHumidityData();
      }
   }
   
   /**
   */
   public void requestMissionData
   (
      boolean forTemperature,
      boolean forHumidity,
      Units   units
   ){
      this.setUnits(units);
      if(forTemperature){
         this.requestTemperatureTimeData();
         this.requestTemperatureData(this.getUnits());
      }
      if(forHumidity){
         this.requestHumidityTimeData();
         this.requestHumidityData();
      }
   }

   /*
   */
   public void requestTemperatureData(){
      List<WeatherData> metricTemp = new LinkedList<WeatherData>();
      List<WeatherData> englishTemp= new LinkedList<WeatherData>();
      List<WeatherData> absoluteTemp=new LinkedList<WeatherData>();
      //Log Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled()){
            MissionLog ml = this.getMissionLog();
            metricTemp   = ml.requestTemperatureLog(Units.METRIC);
            englishTemp  = ml.requestTemperatureLog(Units.ENGLISH);
            absoluteTemp = ml.requestTemperatureLog(Units.ABSOLUTE);
         }
         else{
            les = new String("Temperature Not Currently Enabled");
         }
      }
      catch(MissionException me){}
      catch(NullPointerException npe){}
      finally{}
   /*
      this.temperatureList    = new LinkedList<Double>();
      List<Double> kelvinList = new LinkedList<Double>();
      List<Double> fahrenList = new LinkedList<Double>();
      double maxTemp  = Double.NaN; double minTemp  = Double.NaN;
      double maxTempK = Double.NaN; double minTempK = Double.NaN;
      double maxTempF = Double.NaN; double minTempF = Double.NaN;
      //Log Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled()){
            MissionLog ml = this.getMissionLog();
            this.temperatureList = ml.requestTemperatureLog();
            maxTemp = this.findMax(this.temperatureList);
            minTemp = this.findMin(this.temperatureList);
            kelvinList=new LinkedList<Double>(this.temperatureList);
            fahrenList=new LinkedList<Double>(this.temperatureList);
            this.convertCelsiusToKelvin(kelvinList);
            this.convertCelsiusToFahrenheit(fahrenList);
            maxTempK = WeatherConvert.celsiusToKelvin(maxTemp);
            maxTempF = WeatherConvert.celsiusToFahrenheit(maxTemp);
            minTempK = WeatherConvert.celsiusToKelvin(minTemp);
            minTempF = WeatherConvert.celsiusToFahrenheit(minTemp);
            les = new String("Temperature Log Data Received");
            les = les.concat(" and sent to the listeners");
         }
         else{
            les = new String("Temperature Not Currently Enabled");
         }
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String(me.getMessage());
         //To indicate there was a problem
         this.temperatureList.add(new Double(Double.NaN));
         kelvinList.add(new Double(Double.NaN));
         fahrenList.add(new Double(Double.NaN));
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
         //To indicate there was a problem
         this.temperatureList.add(new Double(Double.NaN));
         kelvinList.add(new Double(Double.NaN));
         fahrenList.add(new Double(Double.NaN));
      }
      finally{
         this.publishTemperatureLogEvent(les, this.temperatureList,
                                         minTemp, maxTemp,
                                         Units.METRIC);
         this.publishTemperatureLogEvent(les, kelvinList, minTempK,
                                         maxTempK, Units.ABSOLUTE);
         this.publishTemperatureLogEvent(les, fahrenList, minTempF,
                                         maxTempF, Units.ENGLISH);
      }
      */
   }

   /*
   */
   private void requestTemperatureData(Units units){
      double maxTemp = Double.NaN; double minTemp = Double.NaN;
      this.temperatureList = new LinkedList<Double>();
      //Log Event String
      String les           = new String();
      try{
         MissionLog ml   = this.getMissionLog();
         this.temperatureList = ml.requestTemperatureLog();
         if(units == Units.ABSOLUTE){
            this.convertCelsiusToKelvin(this.temperatureList);
         }
         else if(units == Units.ENGLISH){
            this.convertCelsiusToFahrenheit(this.temperatureList);
         }
         maxTemp = this.findMax(this.temperatureList);
         minTemp = this.findMin(this.temperatureList);
         les = new String("Temperature Log Data Received");
         les = les.concat(" and sent to the listeners");
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String(me.getMessage());
         //To indicate there was a problem
         this.temperatureList.add(new Double(Double.NaN));
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
         //To indicate there was a problem
         this.temperatureList.add(new Double(Double.NaN));
      }
      finally{
         this.publishTemperatureLogEvent(les,
                                         this.temperatureList,
                                         minTemp,
                                         maxTemp);
      }
   }
   
   /**
   */
   public void requestTemperatureMaxData(){
      String les = new String("Max Temperature/Date Data");
      LinkedList<String> days = this.getTheDays(this.tempTimeList);
      LinkedList<Vector> max  =
        this.getTheMax(days,this.tempTimeList,this.temperatureList);
      List<Date>   maxDates  = new LinkedList<Date>();
      List<Double> cMaxTemps = new LinkedList<Double>();
      List<Double> fMaxTemps = new LinkedList<Double>();
      List<Double> kMaxTemps = new LinkedList<Double>();
      try{
         Iterator i = max.iterator();
         while(i.hasNext()){
            Vector v = (Vector)i.next();
            //Add the data
            maxDates.add((Date)v.firstElement());
            cMaxTemps.add((Double)v.lastElement());
            System.out.println((Date)v.firstElement());
            System.out.println((Double)v.lastElement());
         }
         fMaxTemps = new LinkedList<Double>(cMaxTemps);
         kMaxTemps = new LinkedList<Double>(cMaxTemps);
         this.convertCelsiusToKelvin(kMaxTemps);
         this.covnertCelsiusToFahrenheit(fMaxTemps);
      }
      catch(NoSuchElementException nsee){
         nsee.printStackTrace();
         les = new String(nsee.getMessage());
         cMaxTemps.add(new Double(Double.NaN));
         fMaxTemps.add(new Double(Double.NaN));
         kMaxTemps.add(new Double(Double.NaN));
      }
      catch(NullPointerException npe){}
   }
   
   /**
   */
   public void requestTemperatureMaxData(Units units){}
   
   /**
   */
   public void requestTemperatureMinData(){}
   
   /**
   */
   public void requestTemperatureMinData(Units units){}
   
   /*
   */
   public void setHighHumidityAlarm(double alarm){
      this.highHumidityAlarm = alarm;
   }
   
   /*
   */
   public void setHighTemperatureAlarm(double alarm){
      this.highTemperatureAlarm = alarm;
   }
   
   /*
   */
   public void setHumidityChannelEnabled(boolean isEnabled){
      this.isHumidityChannelEnabled = isEnabled;
   }
   
   /*
   */
   public void setLowHumidityAlarm(double alarm){
      this.lowHumidityAlarm = alarm;
   }
   
   /*
   */
   public void setLowTemperatureAlarm(double alarm){
      this.lowTemperatureAlarm = alarm;
   }
   
   /*
   */
   public void setNewMissionTempAlarmUnits(Units units){
      this.newMissionTempAlarmUnits = units;
   }

   /*
   */
   public void setRolloverEnabled(boolean isEnabled){
      this.isRolloverEnabled = isEnabled;
   }
   
   /*
   */
   public void setSampleRate(int rate){ this.sampleRate = rate; }
   
   /*
   */
   public void setStartDelay(int delay){ this.startDelay = delay; }
   
   /*
   */
   public void setSynchronizedClock(boolean isSynchronized){
      this.isClockSynchronized = isSynchronized;
   }
   
   /*
   */
   public void setTemperatureChannelEnabled(boolean isEnabled){
      this.isTemperatureChannelEnabled = isEnabled;
   }

   /*
   Start a mission with the default sample rate set:  600 sec
   (10 min)
   */
   public void startMission(){
      //Go ahead and change this to the sample rate and start
      //delay set as part of setting the New Mission Data
      this.startMission(DEFAULT_LOGGING_RATE, DEFAULT_DELAY);
   }
   
   /*
   Start a mission with a selected sample rate:  entered in seconds.
   This might mean the sample time needs to be translated from hours
   or minutes to seconds.
   */
   public void startMission(int sampleRate){
      this.startMission(sampleRate, DEFAULT_DELAY);
   }
   
   /*
   Start a mission with a selected sample rate:  entered in seconds,
   and the selected start delay:  entered in seconds, as well
   This might mean the sample time needs to be translated from hours
   or minutes to seconds.
   */
   public void startMission(int sampleRate, int startDelay){
      //Most of this needs to be changed!!!
      //Go ahead and comment out for now, use the NewMissionData
      //Class, instead
      String missionEventString = new String();
      //Go ahead and set the sample rate and start delay here.
      this.setSampleRate(sampleRate);
      this.setStartDelay(startDelay);
      try{
         //Get the Mission Log (better way of doing this, I feel)
         MissionLog ml = this.getMissionLog();
         
         missionEventString = new String("Mission Started on ");
         missionEventString = missionEventString.concat("iButton");
         missionEventString = missionEventString.concat(" device");
         missionEventString = missionEventString.concat(" with a ");
         missionEventString = missionEventString.concat("sample ");
         missionEventString = missionEventString.concat("time of:");
         missionEventString = missionEventString.concat(" " + sampleRate);
         missionEventString = missionEventString.concat(" seconds");
         missionEventString = missionEventString.concat(" and a ");
         missionEventString = missionEventString.concat("delay of ");
         missionEventString = missionEventString.concat("" + startDelay);
         missionEventString = missionEventString.concat(" seconds");
         //Set up the NewMissionData instance, except for the
         //Sample Rate and Start Delay
         NewMissionData nmd = this.setUpNewMissionData();
         //needs to be redone, accepting a NewMissionData object
         //Essentially, start the logging with a NewMissionData
         //object:  since essentially, that is what you are doing,
         //regardless.
         ml.startLogging(nmd);
         //Test Print...
         System.out.println(nmd);
      }
      catch(MissionException me){
         missionEventString = new String(me.getMessage());
      }
      catch(NullPointerException npe){
         missionEventString = new String(npe.getMessage());
      }
      finally{
         this.publishMissionEvent(missionEventString);
      }
   }
   
   /*
   Stop the current mission
   */
   public void stopMission(){
      String missionEventString = new String();
      try{
         MissionLog ml = this.getMissionLog();
         ml.stopLogging();
         missionEventString = new String("Mission Stopped on ");
         missionEventString = missionEventString.concat("iButton");
         missionEventString = missionEventString.concat(" device");
      }
      catch(MissionException me){
         missionEventString = new String(me.getMessage());
      }
      catch(NullPointerException npe){
         missionEventString = new String(npe.getMessage());
      }
      finally{
         this.publishMissionEvent(missionEventString);
      }
   }
   
   /*
   Override the toString() method from the Object Class
   */
   public String toString(){
      return new String(this.getName()+", "+this.getAddress());
   }
   
   //**********************Private Methods*************************   
   /*
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
   private Double calculateDewpoint(Double temp, Double humi){
      final double l = 243.12;  //lambda
      final double b =  17.62;  //Beta
      //This is needed since the humidity recorded on the iButton
      //CAN be below 0.
      final double MINIMUMHUMIDITY = 0.;
      //Default Dewpoint Value
      Double dewpoint = new Double(Double.NaN);
      if(!(temp.isNaN() || humi.isNaN())){
         double tc = temp.doubleValue();
         double rh = humi.doubleValue();
         if(rh > MINIMUMHUMIDITY){
            double alpha = ((b*tc)/(l+tc)) + Math.log(rh * 0.01);
            //This will now give us the dewpiont in degrees
            //celsius. Now, need to go ahead and figure out
            //the units needed.
            double dp = (l * alpha)/(b - alpha);
            dewpoint = new Double(dp);
         }
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
   private Double calculateHeatIndex(Double temp, Double humi){
      final double MINIMUMTEMP = 70.;
      final double MINIMUMHUMI = 0.;
      Double heatIndex         = new Double(Double.NaN);
      if(!(temp.isNaN() || humi.isNaN())){
         double tf = temp.doubleValue();
         double rh = humi.doubleValue();
         //Convert to Fahrenheit first
         tf = WeatherConvert.celsiusToFahrenheit(tf);
         if(tf > MINIMUMTEMP && rh > MINIMUMHUMI){
            double hi =  16.923;
            hi += (.185212 * tf);
            hi += (5.37941 * rh);
            hi -= ((.100254) * tf * rh);
            hi += ((9.41685 * .001) * tf * tf);
            hi += ((7.28898 * .001) * rh * rh);
            hi += ((3.45372 * .0001) * tf * tf * rh);
            hi -= ((8.14971 * .0001) * tf * rh * rh);
            hi += ((1.02102 * .00001) * tf * tf * rh * rh);
            hi -= ((3.8646  * .00001) * tf * tf * tf);
            hi += ((2.91583 * .00001) * rh * rh * rh);
            hi += ((1.42721 * .000001)* tf * tf * tf * rh);
            hi += ((1.97483 * .0000001) * tf * rh * rh * rh);
            hi -= ((2.18429 * .00000001) *tf*tf*tf* rh * rh);
            hi += ((8.43196 * .0000000001) *tf*tf*rh*rh* rh);
            hi -=((4.81975 * .00000000001)*tf*tf*tf*rh*rh*rh);
            //Need to convert back to Celsius
            hi = WeatherConvert.fahrenheitToCelsius(hi);
            heatIndex = new Double(hi);
         }
      }
      return heatIndex;
   }
   
   /*
   */
   private void convertCelsiusToFahrenheit(List tempList){
      //First, check to see if the list is empty
      if(!tempList.isEmpty()){
         for(int i = 0; i < tempList.size(); i++){
            try{
               Double tempd = (Double)tempList.get(i);
               if(!tempd.isNaN()){
                  double temp  = tempd.doubleValue();
                  temp = WeatherConvert.celsiusToFahrenheit(temp);
                  //Replace the element in the List as needed
                  tempList.set(i, new Double(temp));
               }
            }
            //This should never happen
            catch(IndexOutOfBoundsException ibe){
               ibe.printStackTrace();
            }
         }
      }
   }
   
   /*
   */
   private void convertCelsiusToKelvin(List tempList){
      //First, check to see if the list is empty
      if(!tempList.isEmpty()){
         for(int i = 0; i < tempList.size(); i++){
            try{
               Double tempd = (Double)tempList.get(i);
               if(!tempd.isNaN()){
                  double temp  = tempd.doubleValue();
                  temp = WeatherConvert.celsiusToKelvin(temp);
                  //Replace the element in the List as needed
                  tempList.set(i, new Double(temp));
               }
            }
            //This should never happen
            catch(IndexOutOfBoundsException ibe){
               ibe.printStackTrace();
            }
         }
      }      
   }
 
   /*
   */
   private void convertFahrenheitToCelsius(List tempList){
      //First, check to see if the list is empty
      if(!tempList.isEmpty()){
         for(int i = 0; i < tempList.size(); i++){
            try{
               Double tempd = (Double)tempList.get(i);
               if(!tempd.isNaN()){
                  double temp  = tempd.doubleValue();
                  temp = WeatherConvert.fahrenheitToCelsius(temp);
                  //Replace the element in the List as needed
                  tempList.set(i, new Double(temp));
               }
            }
            //This should never happen
            catch(IndexOutOfBoundsException ibe){
               ibe.printStackTrace();
            }
         }
      }
   }
   
   /*
   */
   private void convertFahrenheitToKelvin(List tempList){
      //First, check to see if the list is empty
      if(!tempList.isEmpty()){
         for(int i = 0; i < tempList.size(); i++){
            try{
               Double tempd = (Double)tempList.get(i);
               if(!tempd.isNaN()){
                  double temp  = tempd.doubleValue();
                  temp = WeatherConvert.fahrenheitToKelvin(temp);
                  //Replace the element in the List as needed
                  tempList.set(i, new Double(temp));
               }
            }
            //This should never happen
            catch(IndexOutOfBoundsException ibe){
               ibe.printStackTrace();
            }
         }
      }
   }
   
   /*
   */
   private void convertKelvinToCelsius(List tempList){
      //First, check to see if the list is empty
      if(!tempList.isEmpty()){
         for(int i = 0; i < tempList.size(); i++){
            try{
               Double tempd = (Double)tempList.get(i);
               if(!tempd.isNaN()){
                  double temp = tempd.doubleValue();
                  temp = WeatherConvert.kelvinToCelsius(temp);
                  //Replace the element in the List as needed
                  tempList.set(i, new Double(temp));
               }
            }
            //This should never happen
            catch(IndexOutOfBoundsException ibe){
               ibe.printStackTrace();
            }
         }
      }
   }
   
   /*
   */
   private void convertKelvinToFahrenheit(List tempList){
      //First, check to see if the list is empty
      if(!tempList.isEmpty()){
         for(int i = 0; i < tempList.size(); i++){
            try{
               Double tempd = (Double)tempList.get(i);
               if(!tempd.isNaN()){
                  double temp  = tempd.doubleValue();
                  temp = WeatherConvert.kelvinToFahrenheit(temp);
                  //Replace the element in the List as needed
                  tempList.set(i, new Double(temp));
               }
            }
            //This should never happen
            catch(IndexOutOfBoundsException ibe){
               ibe.printStackTrace();
            }
         }
      }
   }

   /*
   */
   private double findMax(List list){
      double max = Double.MIN_VALUE;
      Iterator i = list.iterator();
      while(i.hasNext()){
         Double value = (Double)i.next();
         if(value.doubleValue() > max){
            max = value.doubleValue();
         }
      }
      return max;
   }
   
   /*
   */
   private Double findMin(List list){
      double min = Double.MAX_VALUE;
      Iterator i = list.iterator();
      while(i.hasNext()){
         Double value = (Double)i.next();
         if(value.doubleValue() < min){
            min = value.doubleValue();
         }
      }
      return min;
   }
   
   /*
   */
   private void findSensors(){
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable hash = ps.findPorts();
      Enumeration ports = hash.keys();
      String adapterName = new String();
      String adapterPort = new String();
      /*Finding this sensor is more "specialized" than finding
      the weather sensors-->because there truthfully ONLY
      one Sensor:  the iButton, which I can get BOTH the
      stored temperature and humidity data.*/
      while(ports.hasMoreElements()){
         //Get the next Key in the Hashtable
         //String port = (String)ports.nextElement();
         Stack port = (Stack)ports.nextElement();
         //Now, get the name and address of the sensor:
         //port is the key into the Hashtable
         Stack sensorData = (Stack)hash.get(port);
         Enumeration element = sensorData.elements();
         while(element.hasMoreElements()){
            String name    = (String)element.nextElement();
            String address = (String)element.nextElement();
            if(!name.equals("DS1990A")){
               /*Since this is a Thermochron iButton, I should only
               have one name and address (asside from the adapter
               address*/
               this.setName(name);
               this.setAddress(address);
            }
         }
         adapterName = (String)port.pop();//Adapter Name
         adapterPort = (String)port.pop();//Adapter Port
      }
      this.setUpMissionLog(adapterName, adapterPort);
   }
   
   /*
   */
   private double getHighHumidityAlarm(){
      return this.highHumidityAlarm;
   }
   
   /*
   */
   private double getHighTemperatureAlarm(){
      return this.highTemperatureAlarm;
   }
   
   /*
   */
   private boolean getHumidityChannelEnabled(){
      return this.isHumidityChannelEnabled;
   }
   
   /*
   */
   private double getLowHumidityAlarm(){
      return this.lowHumidityAlarm;
   }
   
   /*
   */
   private double getLowTemperatureAlarm(){
      return this.lowTemperatureAlarm;
   }
   
   /*
   */
   private Units getNewMissionTempAlarmUnits(){
      return this.newMissionTempAlarmUnits;
   }
   
   /*
   */
   private boolean getRolloverEnabled(){
      return this.isRolloverEnabled;
   }
   
   /*
   */
   private int getSampleRate(){
      return this.sampleRate;
   }
   
   /*
   */
   private int getStartDelay(){
      return this.startDelay;
   }
   
   /*
   */
   private boolean getSynchronizedClock(){
      return this.isClockSynchronized;
   }
   
   /*
   */
   private boolean getTemperatureChannelEnabled(){
      return this.isTemperatureChannelEnabled;
   }
   
   /*
   */
   private LinkedList<String> getTheDays(List<Date> timeLog){
      LinkedList<String> days = new LinkedList<String>();
      try{
         Calendar cal = Calendar.getInstance();
         Iterator i = timeLog.iterator();
         String currentDate = new String("Current");
         String newDate     = new String("Date");
         while(i.hasNext()){
            Date date = (Date)i.next();
            cal.setTime(date);
            newDate = DateFormat.getDateInstance().format(date);
            if(!newDate.equals(currentDate)){
               currentDate = new String(newDate);
               days.add(currentDate);
            }
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      finally{
         return days;
      }
   }
   
   /*
   */
   private LinkedList<Vector> getTheMax
   (
      List<String> days,
      List<Date>   timeLog,
      List<Double> data
   ){
      LinkedList<Vector> max = new LinkedList<Vector>();
      try{
         Iterator i = days.iterator();
         while(i.hasNext()){
            int toIndex   = -1;
            int fromIndex = -1;
            Iterator j = timeLog.iterator();
            String day = (String)i.next();
            System.out.println(day);
            while(j.hasNext()){
               Date date = (Date)j.next();
               String currentDate =
                          DateFormat.getDateInstance().format(date);
               if(currentDate.equals(day)){
                  if(toIndex == -1){
                     toIndex = timeLog.indexOf(date);
                  }
                  fromIndex = timeLog.indexOf(date);
               }
            }
            List<Date> currentDate =
                            timeLog.subList(toIndex, fromIndex + 1);
            List<Double> currentData =
                               data.subList(toIndex, fromIndex + 1);
            Vector<Object> v = new Vector();
            double currentMax = this.findMax(currentData);
            int maxIndex = currentData.indexOf(currentMax);
            Date maxDate = (Date)currentDate.get(maxIndex);
            v.addElement(maxDate);
            v.addElement(new Double(currentMax));
            max.add(v);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      catch(IndexOutOfBoundsException ioe){}
      finally{
         return max;
      }
   }
   
   /*
   */
   private void publishDewpointEvent
   (
      String eventString,
      List   list,
      double min,
      double max,
      Units  units
   ){
      LogEvent evt = new LogEvent(this, eventString, list,
                                                   min, max, units);
      try{
         Iterator<LogListener> i = this.log_List.iterator();
         while(i.hasNext()){
            (i.next()).onDewpointLogEvent(evt);
         }
      }
      //If this exception occurs, there are no Log Listeners,
      //regardless, so the only thing to do is alert through the
      //"typical channels"
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   private void publishHeatIndexEvent
   (
      String eventString,
      List   list,
      double min,
      double max,
      Units  units
   ){
      LogEvent evt = new LogEvent(this, eventString, list, min,
                                                        max, units);
      try{
         Iterator<LogListener> i = this.log_List.iterator();
         while(i.hasNext()){
            (i.next()).onHeatIndexLogEvent(evt);
         }
      }
      //If this exception occurs, there are no Log Listeners,
      //regardless, so the only thing to do is alert through the
      //"typical channels"
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   private void publishHumidityLogEvent
   (
      String eventString,
      List   list,
      double min,
      double max
   ){
      LogEvent evt = new LogEvent(this,eventString,list,min,max);
      try{
         Iterator<LogListener> i = this.log_List.iterator();
         while(i.hasNext()){
            (i.next()).onHumidityLogEvent(evt);
         }
      }
      //If this exception occurs, there are no Log Listeners,
      //regardless, so the only thing to do is alert through the
      //"typical channels"
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   private void publishHumidityTimeLogEvent
   (
      String eventString,
      List   list
   ){
      LogEvent evt = new LogEvent(this, eventString, list);
      try{
         Iterator<LogListener> i = this.log_List.iterator();
         while(i.hasNext()){
            (i.next()).onHumidityTimeLogEvent(evt);
         }
      }
      //If this exception occurs, there are no Log Listeners,
      //regardless, so the only thing to do is alert through the
      //"typical channels"
      catch(NullPointerException npe){
         npe.printStackTrace();
      }      
   }

   /*
   */
   private void publishMemoryEvent(String eventString){
      MemoryEvent evt = new MemoryEvent(this, eventString);
      try{
         Iterator<MemoryListener> i = this.ml_List.iterator();
         while(i.hasNext()){
            (i.next()).onMemoryEvent(evt);
         }
      }
      //If the exception occurs, there are no memory listeners,
      //regardless, so the only thing to do is aleart through
      //the "typical channels"
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   private void publishMissionEvent(String eventString){
      MissionEvent evt = new MissionEvent(this, eventString);
      try{
         Iterator<MissionListener> i = this.mis_List.iterator();
         while(i.hasNext()){
            (i.next()).onMissionEvent(evt);
         }
      }
      //If the exception occurs, there are no Mission listeners,
      //regardless, so the only thing to do is aleart through
      //the "typical channels"      
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   private void publishTemperatureLogEvent
   (
      String eventString,
      List   list,
      double min,
      double max
   ){
      this.publishTemperatureLogEvent(eventString, list, min, max,
                                                   this.getUnits());
   }
   
   /*
   */
   private void publishTemperatureLogEvent
   (
      String eventString,
      List   list,
      double min,
      double max,
      Units  units
   ){
      LogEvent evt = new LogEvent(this, eventString, list, min,
                                  max, units);
      try{
         Iterator<LogListener> i = this.log_List.iterator();
         while(i.hasNext()){
            (i.next()).onTemperatureLogEvent(evt);
         }
      }
      //If this exception occurs, there are no Log Listeners,
      //regardless, so the only thing to do is alert through the
      //"typical channels"
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   private void publishTemperatureTimeLogEvent
   (
      String eventString,
      List   list
   ){
      LogEvent evt = new LogEvent(this, eventString, list);
      try{
         Iterator<LogListener> i = this.log_List.iterator();
         while(i.hasNext()){
            (i.next()).onTemperatureTimeLogEvent(evt);
         }
      }
      //If this exception occurs, there are no Log Listeners,
      //regardless, so the only thing to do is alert through the
      //"typical channels"
      catch(NullPointerException npe){
         npe.printStackTrace();
      }      
   }
   
   /*
   */
   private void requestHumidityData(){
      this.humidityList = new LinkedList<Double>();
      String les        = new String();
      double min        = Double.NaN;
      double max        = Double.NaN;
      try{
         if(this.getHumidityChannelEnabled()){
            MissionLog ml  = this.getMissionLog();
            humidityList   = ml.requestHumidityLog();
            les = new String("Humidity Log Data Received");
            les = les.concat(" and sent to the listeners");
            min = this.findMin(this.humidityList);
            max = this.findMax(this.humidityList);
         }
         else
            les = new String("Humidity Not Currently Enabled");
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String(me.getMessage());
         //Indicate there was a problem
         humidityList.add(new Double(Double.NaN));
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
         //Indicate there was a problem
         humidityList.add(new Double(Double.NaN));
      }
      finally{
         this.publishHumidityLogEvent(les,this.humidityList,min,max);
      }
   }
   
   /*
   */
   private void requestHumidityTimeData(){
      this.humidityTimeList = new LinkedList<Date>();
      //Log Event String
      String les = new String();
      try{
         MissionLog ml = this.getMissionLog();
         this.humidityTimeList = ml.requestHumidityTimeLog();
         les = new String("Humidity Time Log");
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String(me.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
      }
      finally{
         this.publishHumidityTimeLogEvent(les, humidityTimeList);
      }
   }

   /*
   */
   private void requestTemperatureTimeData(){
      this.tempTimeList = new LinkedList<Date>();
      //Log Event String
      String les = new String();
      try{
         MissionLog ml = this.getMissionLog();
         this.tempTimeList = ml.requestTemperatureTimeLog();
         les = new String("Temperature Time Log");
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String(me.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
      }
      finally{
         this.publishTemperatureTimeLogEvent(les,tempTimeList);
      }
   }
   
   /*
   */
   private void setAddress(String address){
      this.address = new String(address);
   }
   
   /*
   */
   private void setName(String name){
      this.name = new String(name);
   }
   
   /*
   */
   private void setUnits(Units units){
      this.units = units;
   }
   
   /*
   */
   private void setUpMissionLog
   (
      String adapterName,
      String adapterPort
   ){
      DSPortAdapter dspa = null;
      try{
         //dspa = OneWireAccessProvider.getDefaultAdapter();
         dspa = OneWireAccessProvider.getAdapter(adapterName,
                                                       adapterPort);
         this.missionLog = new MissionLog(new OneWireContainer41(
                                          dspa, this.getAddress()));
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
      }
      catch(OneWireException owe){
         owe.printStackTrace();
      }
   }

   /*
   */
   private NewMissionData setUpNewMissionData(){
      NewMissionData nmd = new NewMissionData();
      nmd.setSampleRate(this.getSampleRate());
      nmd.setStartDelay(this.getStartDelay());
      nmd.setHumidityHighAlarm(this.getHighHumidityAlarm());
      nmd.setHumidityLowAlarm(this.getLowHumidityAlarm());
      Units units = this.getNewMissionTempAlarmUnits();
      double hitemp = this.getHighTemperatureAlarm();
      double lotemp = this.getLowTemperatureAlarm();
      //For the temperature alarms, need to convert to celsius
      if(units == Units.ENGLISH){
         if(hitemp != Double.NaN){
            hitemp = WeatherConvert.fahrenheitToCelsius(hitemp);
         }
         if(lotemp != Double.NaN){
            lotemp = WeatherConvert.fahrenheitToCelsius(lotemp);
         }
      }
      else if(units == Units.ABSOLUTE){
         if(hitemp != Double.NaN){
            hitemp = WeatherConvert.kelvinToCelsius(hitemp);
         }
         if(lotemp != Double.NaN){
            lotemp = WeatherConvert.kelvinToCelsius(lotemp);
         }
      }
      //At the moment, will not do anything, but set up the
      //infrastructure, regardless
      nmd.setTemperatureLowAlarm(lotemp);
      nmd.setTemperatureHighAlarm(hitemp);
      nmd.setRolloverEnabled(this.getRolloverEnabled());
      nmd.setSynchClock(this.getSynchronizedClock());
      nmd.setEnableTemperatureChannel(
                               this.getTemperatureChannelEnabled());
      nmd.setEnableHumidityChannel(this.getHumidityChannelEnabled());
      return nmd;
   }
}
