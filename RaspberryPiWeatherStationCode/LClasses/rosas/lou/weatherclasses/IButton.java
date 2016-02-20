/**
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.*;
import java.io.File;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class IButton{
   public static final int DEFAULT_LOGGING_RATE = 600;
   public static final int DEFAULT_DELAY = 10;
   
   private static final int HIGH     = 0;
   private static final int LOW      = 1;
   private static final int TEMP     = 2;
   private static final int HUMIDITY = 3;
   
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
   /**
   */
   public IButton(){
      this.findSensors();
      this.setUnits("Celsius");
      this.setRolloverEnabled(false);
      this.setSynchronizedClock(false);
      this.setNewMissionTempAlarmUnits("Celsius");
      this.setLowTemperatureAlarm("");
      this.setLowHumidityAlarm("");
      this.setHighTemperatureAlarm("");
      this.setHighHumidityAlarm("");
      this.setHumidityChannelEnabled(true);
      this.setTemperatureChannelEnabled(true);
      this.setSampleRate("");
      this.setStartDelay("");
   }
   
   /**
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
      this.setUnits("Celsius");
      this.setRolloverEnabled(false);
      this.setSynchronizedClock(false);
      this.setNewMissionTempAlarmUnits("Celsius");
      this.setLowTemperatureAlarm("");
      this.setLowHumidityAlarm("");
      this.setHighTemperatureAlarm("");
      this.setHighHumidityAlarm("");
      this.setHumidityChannelEnabled(true);
      this.setTemperatureChannelEnabled(true);
      this.setSampleRate("");
      this.setStartDelay("");
   }
   
   /**
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
   
   /**
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
   
   /**
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
   
   /**
   */
   public void clearMemory(){
      String memoryEventString = new String();
      try{
         MissionLog ml = this.getMissionLog();
         ml.clearLog();
         memoryEventString = new String("Memory Cleared on ");
         memoryEventString = memoryEventString.concat(this.getName());
         memoryEventString = memoryEventString.concat(" device, ");
         memoryEventString = memoryEventString.concat("address: ");
         memoryEventString =
                          memoryEventString.concat(this.getAddress());
      }
      catch(MemoryException me){
         memoryEventString =
                          new String("Exception " + me.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         memoryEventString =
                          new String("Exception" + npe.getMessage());
      }
      finally{
         this.publishMemoryEvent(memoryEventString);
      }
   }
   
   /**
   */
   public String getAddress(){
      return this.address;
   }
      
   /**
   */
   public double getHighHumidityAlarm(){
      return this.highHumidityAlarm;
   }
   
   /**
   */
   public double getHighTemperatureAlarm(){
      return this.highTemperatureAlarm;
   }
   
   /**
   */
   public boolean getHumidityChannelEnabled(){
      return this.isHumidityChannelEnabled;
   }
   
   /**
   */
   public double getLowHumidityAlarm(){
      return this.lowHumidityAlarm;
   }
   
   /**
   */
   public double getLowTemperatureAlarm(){
      return this.lowTemperatureAlarm;
   }
   
   /**
   */
   public MissionLog getMissionLog(){
      return this.missionLog;
   }
   
   /**
   */
   public String getName(){
      return this.name;
   }
   
   /**
   */
   public boolean getRolloverEnabled(){
      return this.isRolloverEnabled;
   }

   /**
   */
   public int getSampleRate(){
      return this.sampleRate;
   }

   /**
   */
   public int getStartDelay(){
      return this.startDelay;
   }
   
   /**
   */
   public boolean getTemperatureChannelEnabled(){
      return this.isTemperatureChannelEnabled;
   }
   
   /**
   */
   public boolean getSynchronizedClock(){
      return this.isClockSynchronized;
   }
   
   /**
   */
   public Units getUnits(){
      return this.units;
   }
   
   /**
   Need both the Temperature and Humidity for this to work
   NEEDS TO BE REWORKED!!!!
   */
   public void requestDewpointData(){
      List<WeatherData> dewpoint    = new LinkedList<WeatherData>();
      List<WeatherData> fdewpoint   = new LinkedList<WeatherData>();
      List<WeatherData> kdewpoint   = new LinkedList<WeatherData>();
      //Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled() &&
            this.getHumidityChannelEnabled()){
            dewpoint = this.getDewpointData();
            les = new String("Dew Point Calculated and sent to");
            les = les.concat(" listeners");
         }
         else{
            les = new String("Error:  ");
            les = les.concat("Dew Point calculation not capable");
         }
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String("Error: " + me.getMessage());
      }
      catch(IndexOutOfBoundsException ibe){
         ibe.printStackTrace();
         les = new String("Error:  " + ibe.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String("Error:  " + npe.getMessage());
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         les = new String("Error:  " + nfe.getMessage());
      }
      finally{
         this.publishDewpointEvent(les, dewpoint);
      }
   }
   
   /**
   Need both the Temperature and Humidity for this to work
   */
   public void requestDewpointData(Units units){
      List<WeatherData> dewpoint = new LinkedList<WeatherData>();
      //Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled() &&
            this.getHumidityChannelEnabled()){
            dewpoint = this.getDewpointData(units);
            les = new String("Dew Point Calculated and sent to");
            les = les.concat(" listeners");
         }
         else{
            les = new String("Error: ");
            les = les.concat("Dew Point calculation not capable");
         }
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String("Error: " + me.getMessage());
      }
      catch(IndexOutOfBoundsException ibe){
         ibe.printStackTrace();
         les = new String("Error:  " + ibe.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String("Error:  " + npe.getMessage());
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         les = new String("Error:  " + nfe.getMessage());
      }
      finally{
         this.publishDewpointEvent(les, dewpoint);
      }
   }
   
   /**
   */
   public void requestHeatIndexData(){
      List<WeatherData> heatIndex   = new LinkedList<WeatherData>();
      List<WeatherData> fheatIndex  = new LinkedList<WeatherData>();
      List<WeatherData> kheatIndex  = new LinkedList<WeatherData>();
      //Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled() &&
            this.getHumidityChannelEnabled()){
            heatIndex = this.getHeatIndexData();
            les = new String("Heat Index Calculated and sent to");
            les = les.concat(" listeners");
         }
         else{
            les = new String("Error:  ");
            les = les.concat("Heat Index Calculation not capable");
         }
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String("Error:  " + me.getMessage());
      }
      catch(IndexOutOfBoundsException ibe){
         ibe.printStackTrace();
         les = new String("Error:  " + ibe.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String("Error:  " + npe.getMessage());
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         les = new String("Error:  " + nfe.getMessage());
      }
      finally{
         this.publishHeatIndexEvent(les, heatIndex);
      }
   }
   
   /**
   */
   public void requestHeatIndexData(Units units){
      List<WeatherData> heatindex = new LinkedList<WeatherData>();
      //Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled() &&
            this.getHumidityChannelEnabled()){
            heatindex = this.getHeatindexData(units);
            les = new String("Heat Index Calculated and sent to");
            les = les.concat(" listeners");
         }
         else{
            les = new String("Heat Index Calculation not capable");
         }
      }
      catch(IndexOutOfBoundsException ibe){
         ibe.printStackTrace();
         les = new String(ibe.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
         les = new String(nfe.getMessage());
      }
      finally{
         this.publishHeatIndexEvent(les, heatindex);
      }
   }
   
   /**
   Request the humidity for each day:  including the Min and Max
   */
   public void requestHumidityData(){
      List<WeatherData> humidity = new LinkedList<WeatherData>();
      //Event String
      String les = new String();
      try{
         if(this.getHumidityChannelEnabled()){
            MissionLog ml  = this.getMissionLog();
            humidity       = ml.requestHumidityLog();
            les = new String("Humidity Log Data Received");
            les = les.concat(" and sent to the listeners");
         }
         else{
            les = new String("Error: Humidity Not Currently Enabled");
         }
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String("Error: " + me.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String("Error: " + npe.getMessage());
      }
      finally{
         this.publishHumidityLogEvent(les,humidity);
      }
   }
   
   /**
   Request the temperature in all three units.  Publish all that
   data for the subscribers to "figure out" what to do.
   */
   public void requestTemperatureData(){
      List<WeatherData> allTemp = new LinkedList<WeatherData>();
      //Log Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled()){
            MissionLog ml = this.getMissionLog();
            allTemp       = ml.requestTemperatureLog();
            les = new String("Temperature Log Data Received");
            les = les.concat(" and sent to the listeners");
         }
         else{
            les = new String("Error: ");
            les = les.concat("Temperature Not Currently Enabled");
         }
      }
      catch(MissionException me){
         me.printStackTrace();
         les = new String("Error:  " + me.getMessage());
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String("Error:  " + npe.getMessage());
      }
      finally{
         this.publishTemperatureLogEvent(les, allTemp);
      }
   }
   
   /**
   */
   public void requestTemperatureData(Units units){
      List<WeatherData> temp = new LinkedList<WeatherData>();
      //Log Event String
      String les = new String();
      try{
         if(this.getTemperatureChannelEnabled()){
            MissionLog ml = this.getMissionLog();
            temp = ml.requestTemperatureLog(units);
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
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         les = new String(npe.getMessage());
      }
      finally{
         this.publishTemperatureLogEvent(les, temp);
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
         this.convertCelsiusToFahrenheit(fMaxTemps);
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
   
   /**
   */
   public void saveAllData(File file){
      //Archive Event String
      String aes = new String("");
      
      List<WeatherData> temp = new LinkedList<WeatherData>();
      List<WeatherData> humi = new LinkedList<WeatherData>();
      List<WeatherData> dewp = new LinkedList<WeatherData>();
      List<WeatherData> hidx = new LinkedList<WeatherData>();
      try{
         MissionLog ml = this.getMissionLog();
         WeatherDataFile wdf = new WeatherDataFile();
         if(this.getTemperatureChannelEnabled() &&
            this.getHumidityChannelEnabled()){
            temp = ml.requestTemperatureLog();
            humi = ml.requestHumidityLog();
            dewp = this.getDewpointData(temp, humi);
            hidx = this.getHeatIndexData(temp, humi);
            //Save data to the file
            wdf.setWeatherData(temp);
            wdf.setWeatherDataFile(file);
            aes  = new String("All Log data saved at: " + file);
         }
         else if(this.getTemperatureChannelEnabled()){
            temp = ml.requestTemperatureLog();
            //Save data to the file
            
            aes = new String("Error:  ");
            aes = aes.concat("Temperature Log data saved at:  ");
            aes = aes.concat(file + "\n");
            aes = aes.concat("Humidity, Dewpoint and Heat Index\n");
            aes = aes.concat("were NOT able to be saved");
            throw new MissionException(aes);
         }
         else if(this.getHumidityChannelEnabled()){
            humi = ml.requestHumidityLog();
            //Save data to the file
            
            aes = new String("Error:  Humidity Log data saved at:  ");
            aes = aes.concat(file + "\n");
            aes= aes.concat("Temperature, Dewpoint and Heat Index\n");
            aes = aes.concat("were NOT able to be saved");
            throw new MissionException(aes);
         }
         else{
            aes = new String("Error:  ");
            aes = aes.concat("No Weather Data able to be saved!!!");
            throw new MissionException(aes);
         }
      }
      catch(MissionException me){
      }
      catch(NullPointerException npe){}
      finally{}
   }


   /**
   Start a mission with the default sample rate set:  600 sec
   (10 min)
   */
   public void startMission(){
      //Go ahead and change this to the sample rate and start
      //delay set as part of setting the New Mission Data
      this.startMission(DEFAULT_LOGGING_RATE, DEFAULT_DELAY);
   }
   
   /**
   Start a mission with a selected sample rate:  entered in seconds.
   This might mean the sample time needs to be translated from hours
   or minutes to seconds.
   */
   public void startMission(int sampleRate){
      this.startMission(sampleRate, DEFAULT_DELAY);
   }
   
   /**
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
      this.setSampleRate(Integer.toString(sampleRate));
      this.setStartDelay(Integer.toString(startDelay));
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
         //needs to be redone, accepting a NewMissionData object
         //Essentially, start the logging with a NewMissionData
         //object:  since essentially, that is what you are doing,
         //regardless.
         //ml.startLogging(nmd);
         //Test Print...
         //System.out.println(nmd);
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
   
   /**
   */
   public void startMission(NewMissionData nmd){
      String mes = new String();//Mission Event String
      this.setUnits(nmd.getUnits());
      this.setRolloverEnabled(nmd.getRolloverEnabled());
      this.setSynchronizedClock(nmd.getSynchClock());
      this.setNewMissionTempAlarmUnits(nmd.getUnits());
      this.setLowTemperatureAlarm(nmd.getTemperatureLowAlarm());
      this.setLowHumidityAlarm(nmd.getHumidityLowAlarm());
      this.setHighTemperatureAlarm(nmd.getTemperatureHighAlarm());
      this.setHighHumidityAlarm(nmd.getHumidityHighAlarm());
      this.setHumidityChannelEnabled(nmd.getEnableHumidityChannel());
      this.setTemperatureChannelEnabled(
                                   nmd.getEnableTemperatureChannel());
      this.setSampleRate(nmd.getSampleRate());
      this.setStartDelay(nmd.getStartDelay());
      //TBD
      try{
         if(this.isMissionRunning()){
            mes = new String("Mission Running, ");
            mes = mes.concat("please stop the current\nmission to ");
            mes = mes.concat("start a new mission");
         }
         else{
            double value    = 0.;
            boolean enabled = false;
            //TBD on the Mission String
            MissionLog ml = this.getMissionLog();
            ml.setRollover(this.getRolloverEnabled());
            ml.setSynchronizedClock(this.getSynchronizedClock());
            value = this.getAlarmValue(this.TEMP, this.HIGH);
            if(value != Double.NaN){
               ml.setHighTemperatureAlarm(value);
            }
            value = this.getAlarmValue(this.TEMP, this.LOW);
            if(value != Double.NaN){
               ml.setLowTemperatureAlarm(value);
            }
            value = this.getAlarmValue(this.HUMIDITY, this.HIGH);
            if(value != Double.NaN){
               ml.setHighHumidityAlarm(value);
            }
            value = this.getAlarmValue(this.HUMIDITY, this.HIGH);
            if(value != Double.NaN){
               ml.setLowHumidityAlarm(value);
            }
            enabled = this.getTemperatureChannelEnabled();
            ml.setTemperatureEnabled(enabled);
            enabled = this.getHumidityChannelEnabled();
            ml.setHumidityEnabled(enabled);
            ml.setSampleRate(this.getSampleRate());
            ml.setStartDelay(this.getStartDelay());
            ml.startLogging();
            mes = new String("New Mission Started on:  ");
            mes = mes.concat(this.getName());
            mes = mes.concat(" device, address:  "+this.getAddress());
         }
      }
      catch(MissionException me){
         mes = new String(me.getMessage());
      }
      catch(NullPointerException npe){
         mes = new String(npe.getMessage());
      }
      finally{
         this.publishMissionEvent(mes);
      }
   }
   
   /**
   Stop the current mission
   */
   public void stopMission(){
      String missionEventString = new String();
      try{
         MissionLog ml = this.getMissionLog();
         ml.stopLogging();
         missionEventString = new String("Mission Stopped on:  ");
         missionEventString =
                           missionEventString.concat(this.getName());
         missionEventString = missionEventString.concat(" device, ");
         missionEventString = missionEventString.concat("address: ");
         missionEventString =
                        missionEventString.concat(this.getAddress());
      }
      catch(MissionException me){
         missionEventString =
                          new String("Exception:  "+me.getMessage());
      }
      catch(NullPointerException npe){
         missionEventString =
                         new String("Exception:  "+npe.getMessage());
      }
      finally{
         this.publishMissionEvent(missionEventString);
      }
   }
   
   /**
   Override the toString() method from the Object Class
   */
   public String toString(){
      return new String(this.getName()+", "+this.getAddress());
   }
   
   //**********************Private Methods*************************   
   /**
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
   Temperature MUST be in celsius!
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
   
   /**
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
               have one name and address (aside from the adapter
               address)*/
               this.setName(name);
               this.setAddress(address);
               adapterName = (String)port.pop();//Adapter Name
               adapterPort = (String)port.pop();//Adapter Port
               this.setUpMissionLog(adapterName, adapterPort);
            }
         }
      }
   }
   
   /**
   */
   private double getAlarmValue(int type, int place){
      double value = Double.NaN;
      switch(type){
         case TEMP:
            Units unit = this.getNewMissionTempAlarmUnits();
            switch(place){
               case HIGH:
                  value = this.getHighTemperatureAlarm();
                  break;
               case LOW:
                  value = this.getLowTemperatureAlarm();
                  break;
               default: 
                  value = Double.NaN;
            }
            if(value != Double.NaN){
               if(unit == Units.ENGLISH){
                  value = WeatherConvert.fahrenheitToCelsius(value);
               }
               else if(unit == Units.ABSOLUTE){
                     value=WeatherConvert.kelvinToCelsius(value);
               }
            }
            break;
         case HUMIDITY:
            switch(place){
               case HIGH:
                  value = this.getHighHumidityAlarm();
                  break;
               case LOW:
                  value = this.getLowHumidityAlarm();
                  break;
               default:
                  value = Double.NaN;
            }
            break;
         default:
            value = Double.NaN;
      }
      return value;
   }
   
   /**
   */
   private List getDewpointData(){
      List<WeatherData> temperature = new LinkedList<WeatherData>();
      List<WeatherData> humidity    = new LinkedList<WeatherData>();
      List<WeatherData> dewpoint    = new LinkedList<WeatherData>();
      MissionLog ml                 = this.getMissionLog();
      
      //Just need the Celsius Temperature...
      temperature = ml.requestTemperatureLog(Units.METRIC);
      humidity    = ml.requestHumidityLog();
      dewpoint = this.getDewpointData(temperature, humidity);
      return dewpoint;
   }

   /**
   */   
   private List getDewpointData(Units units){
      List<WeatherData> temperature = new LinkedList<WeatherData>();
      List<WeatherData> humidity    = new LinkedList<WeatherData>();
      List<WeatherData> dewpoint    = new LinkedList<WeatherData>();
      MissionLog ml                 = this.getMissionLog();
      //Need the temperature in Metric Units
      temperature = ml.requestTemperatureLog(Units.METRIC);
      humidity    = ml.requestHumidityLog();
      Iterator i  = temperature.iterator();
      Iterator h  = humidity.iterator();
      while(i.hasNext()){
         WeatherData tempData = (WeatherData)i.next();
         WeatherData humiData = (WeatherData)h.next();
         List<Double> data    = tempData.getData();
         List<Date>  dates    = tempData.getDates();
         List<Double> humi    = humiData.getData();
         List<Double> dewp    = new LinkedList<Double>();
         Iterator dt          = data.iterator();
         Iterator ht          = humi.iterator();
         while(dt.hasNext()){
            Double temp = (Double)dt.next();
            Double hum  = (Double)ht.next();
            Double dp   = this.calculateDewpoint(temp,hum);
            dewp.add(dp);
         }
         //Create a new WeatherData Object, with the current
         //Dates and the current dewpoint data
         WeatherData wd = new WeatherData(Types.DEWPOINT,
                                          units,
                                          dewp,
                                          dates);
         dewpoint.add(wd);
      }
      return dewpoint;
   }
   
   /**
   */
   private List getDewpointData
   (
      List<WeatherData> temp,
      List<WeatherData> humi
   ){
      List<WeatherData> dewpoint = new LinkedList<WeatherData>();
      Iterator tempi = temp.iterator();
      Iterator humii = humi.iterator();
      while(tempi.hasNext()){
         WeatherData tempData = (WeatherData)tempi.next();
         if(tempData.getUnits() == Units.METRIC){
            WeatherData humiData = (WeatherData)humii.next();
            List<Double> templ   = tempData.getData();
            List<Date>  tempd   = tempData.getDates();
            List<Double> humil   = humiData.getData();
            List<Date> humid   = humiData.getDates();
            List<Double> dewpl   = new LinkedList<Double>();
            Iterator templi = templ.iterator();  //Temp Iterator
            Iterator humili = humil.iterator();  //Humidity Iterator
            while(templi.hasNext()){
               Double currentTemp = (Double)templi.next();
               Double currentHumi = (Double)humili.next();
               Double dewpt =
                     this.calculateDewpoint(currentTemp, currentHumi);
               dewpl.add(dewpt);
            }
            WeatherData dewptData = new WeatherData(Types.DEWPOINT,
                                                    Units.METRIC,
                                                    dewpl, tempd);
            dewpoint.add(dewptData);
            dewptData = new WeatherData(Types.DEWPOINT, Units.ENGLISH,
                                        dewpl, tempd);
            dewpoint.add(dewptData);
            dewptData = new WeatherData(Types.DEWPOINT,
                                        Units.ABSOLUTE, dewpl, tempd);
            dewpoint.add(dewptData);
         }
      }
      return dewpoint;
   }
   
   /**
   */
   private List getHeatIndexData(){
      List<WeatherData> temperature = new LinkedList<WeatherData>();
      List<WeatherData> humidity    = new LinkedList<WeatherData>();
      List<WeatherData> heatIndex   = new LinkedList<WeatherData>();
      MissionLog ml                 = this.getMissionLog();
      
      //As the way the method calculateHeatIndex was written, just
      //need to Celsius Temperature
      temperature = ml.requestTemperatureLog(Units.METRIC);
      humidity    = ml.requestHumidityLog();
      heatIndex = this.getHeatIndexData(temperature, humidity);
      /*
      Iterator t  = temperature.iterator();
      Iterator h  = humidity.iterator();
      while(t.hasNext()){
         WeatherData tempData = (WeatherData)t.next();
         WeatherData humiData = (WeatherData)h.next();
         List<Double> temp    = tempData.getData();
         List<Date> tempDates = tempData.getDates();
         List<Double> humi    = humiData.getData();
         List<Date> humiDates = humiData.getDates();
         List<Double> hiList  = new LinkedList<Double>();
         Iterator ti = temp.iterator(); //Temp Iterator
         Iterator hi = humi.iterator(); //Humidity Iterator
         while(ti.hasNext()){
            Double currTemp = (Double)ti.next();
            Double currHumi = (Double)hi.next();
            Double hindx = this.calculateHeatIndex(currTemp,currHumi);
            hiList.add(hindx);
         }
         WeatherData hiData = new WeatherData(Types.HEATINDEX,
                                              Units.METRIC,
                                              hiList,
                                              tempDates);
         heatIndex.add(hiData);
         hiData = new WeatherData(Types.HEATINDEX,
                                  Units.ENGLISH,
                                  hiList,
                                  tempDates);
         heatIndex.add(hiData);
         hiData = new WeatherData(Types.HEATINDEX,
                                  Units.ABSOLUTE,
                                  hiList,
                                  tempDates);
         heatIndex.add(hiData);
      }
      */
      return heatIndex;
   }
   
   /**
   */
   private List getHeatindexData(Units units){
      List<WeatherData> temperature = new LinkedList<WeatherData>();
      List<WeatherData> humidity    = new LinkedList<WeatherData>();
      List<WeatherData> heatindex   = new LinkedList<WeatherData>();
      MissionLog ml = this.getMissionLog();
      //Need the temperature in Metric Units
      temperature = ml.requestTemperatureLog(Units.METRIC);
      humidity    = ml.requestHumidityLog();
      Iterator i  = temperature.iterator();
      Iterator h  = humidity.iterator();
      while(i.hasNext()){
         WeatherData tempData = (WeatherData)i.next();
         WeatherData humiData = (WeatherData)h.next();
         List<Double> data    = tempData.getData();
         List<Date>  dates    = tempData.getDates();
         List<Double> humi    = humiData.getData();
         List<Double> heat    = new LinkedList<Double>();
         Iterator tempi       = data.iterator();
         Iterator humii       = humi.iterator();
         while(tempi.hasNext()){
            Double temp = (Double)tempi.next();
            Double hum  = (Double)humii.next();
            Double hi   = this.calculateHeatIndex(temp, hum);
            heat.add(hi);
         }
         //Create a new WeatherData Object, with the current
         //Dates and current heat index data
         WeatherData wd = new WeatherData(Types.HEATINDEX,
                                          units,
                                          heat,
                                          dates);
         heatindex.add(wd);
      }
      return heatindex;
   }
   
   /**
   */
   private List getHeatIndexData
   (
      List<WeatherData> temp,
      List<WeatherData> humi
   ){
      List<WeatherData> heatIndex = new LinkedList<WeatherData>();
      Iterator tempi = temp.iterator();
      Iterator humii = humi.iterator();
      while(tempi.hasNext()){
         WeatherData tempData = (WeatherData)tempi.next();
         if(tempData.getUnits() == Units.METRIC){
            WeatherData humiData = (WeatherData)humii.next();
            List<Double> templ  = tempData.getData();
            List<Date>  tempd   = tempData.getDates();
            List<Double> humil  = humiData.getData();
            List<Date> humid    = humiData.getDates();
            List<Double> hidxl  = new LinkedList<Double>();
            Iterator templi = templ.iterator();  //Temp Iterator
            Iterator humili = humil.iterator();  //Humidity Iterator
            while(templi.hasNext()){
               Double currentTemp = (Double)templi.next();
               Double currentHumi = (Double)humili.next();
               Double hindx =
                    this.calculateHeatIndex(currentTemp, currentHumi);
               hidxl.add(hindx);
            }
            WeatherData hindxData = new WeatherData(Types.HEATINDEX,
                                                    Units.METRIC,
                                                    hidxl, tempd);
            heatIndex.add(hindxData);
            hindxData = new WeatherData(Types.HEATINDEX, Units.ENGLISH,
                                        hidxl, tempd);
            heatIndex.add(hindxData);
            hindxData = new WeatherData(Types.HEATINDEX,
                                        Units.ABSOLUTE, hidxl, tempd);
            heatIndex.add(hindxData);
         }
      }
      return heatIndex;
   }
   
   /**
   */
   private Units getNewMissionTempAlarmUnits(){
      return this.newMissionTempAlarmUnits;
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
   
   /**
   */
   private boolean isMissionRunning(){
      boolean isRunning = false;
      try{
         MissionLog ml = this.getMissionLog();
         isRunning = ml.isMissionRunning();
      }
      catch(MissionException me){
         isRunning = false;
      }
      catch(NullPointerException npe){
         isRunning = false;
      }
      finally{
         return isRunning;
      }
   }
   
   /**
   */
   private void publishDewpointEvent
   (
      String eventString,
      List   list
   ){
      LogEvent evt = new LogEvent(this, eventString, list);
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
   
   /**
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
   
   /**
   */
   private void publishHeatIndexEvent
   (
      String eventString,
      List   list
   ){
      LogEvent evt = new LogEvent(this, eventString, list);
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
   
   /**
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
   
   /**
   */
   private void publishHumidityLogEvent
   (
      String eventString,
      List   list
   ){
      LogEvent evt = new LogEvent(this, eventString, list);
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
   
   /**
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
   
   /**
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
   
   /**
   */
   private void publishTemperatureLogEvent
   (
      String eventString,
      List   list
   ){
      LogEvent evt = new LogEvent(this, eventString, list);
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
   
   /**
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
   
   /**
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
   
   /**
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
   
   /**
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
   */

   /**
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
   */
   
   /*
   */
   private void setAddress(String address){
      this.address = new String(address);
   }
   
   /**
   */
   private void setHighHumidityAlarm(String alarm){
      double humidity = 0.;
      try{
         humidity = Double.parseDouble(alarm);
         if(humidity < 0. || humidity > 100.){
            humidity = Double.NaN;
         }
      }
      catch(NullPointerException npe){
         humidity = Double.NaN;
      }
      catch(NumberFormatException nfe){
         humidity = Double.NaN;
      }
      finally{
         this.highHumidityAlarm = humidity;
      }
   }
   
   /**
   */
   private void setHighTemperatureAlarm(String alarm){
      try{
         this.highTemperatureAlarm = Double.parseDouble(alarm);
      }
      catch(NullPointerException npe){
         this.highTemperatureAlarm = Double.NaN;
      }
      catch(NumberFormatException nfe){
         this.highTemperatureAlarm = Double.NaN;
      }
   }
   
   /**
   */
   private void setLowHumidityAlarm(String alarm){
      double humidity = 0.;
      try{
         humidity = Double.parseDouble(alarm);
         if(humidity < 0. || humidity > 100.){
            humidity = Double.NaN;
         }
      }
      catch(NullPointerException npe){
         humidity = Double.NaN;
      }
      catch(NumberFormatException nfe){
         humidity = Double.NaN;
      }
      finally{
         this.lowHumidityAlarm = humidity;
      }
   }
   
   /**
   */
   private void setHumidityChannelEnabled(boolean isEnabled){
      this.isHumidityChannelEnabled = isEnabled;
   }
   
   /**
   */
   private void setLowTemperatureAlarm(String alarm){
      try{
         this.lowTemperatureAlarm = Double.parseDouble(alarm);
      }
      catch(NullPointerException npe){
         this.lowTemperatureAlarm = Double.NaN;
      }
      catch(NumberFormatException nfe){
         this.lowTemperatureAlarm = Double.NaN;
      }
   }
   
   /**
   */
   private void setName(String name){
      this.name = new String(name);
   }
   
   /**
   */
   private void setNewMissionTempAlarmUnits(String type){
      this.setUnits(type);
      if(type.contains("Celsius") || type.contains("celsius")){
         this.newMissionTempAlarmUnits = Units.METRIC;
      }
      else if(type.contains("Fahrenheit") ||
              type.contains("fahrenheit")){
         this.newMissionTempAlarmUnits = Units.ENGLISH;
      }
      else if(type.contains("Kelvin") || type.contains("kelvin")){
         this.newMissionTempAlarmUnits = Units.ABSOLUTE;
      }
      //The Default setting...go ahead and set to Metric
      else{
         this.newMissionTempAlarmUnits = Units.METRIC;
      }
   }
   
   /**
   */
   private void setRolloverEnabled(boolean isEnabled){
      this.isRolloverEnabled = isEnabled;
   }
      
   /**
   */
   public void setTemperatureChannelEnabled(boolean isEnabled){
      this.isTemperatureChannelEnabled = isEnabled;
   }
   
   /**
   Set the sample rate for the IButton...the number is entered in
   minutes, but the IButton device requires seconds, so the number
   must be converted to seconds:  hence, the MINSTOSECS convert
   constant...
   Set in seconds, entered in minutes
   */
   private void setSampleRate(String rate){
      final int MINSTOSECS = 60;
      int sampleRate = 1;
      try{
         sampleRate = Integer.parseInt(rate);
         if(sampleRate < 1){
            sampleRate = DEFAULT_LOGGING_RATE;
         }
         else{
            //Set the value in terms of seconds
            sampleRate *= MINSTOSECS;
         }
      }
      catch(NullPointerException npe){
         sampleRate = DEFAULT_LOGGING_RATE;
      }
      catch(NumberFormatException nfe){
         sampleRate = DEFAULT_LOGGING_RATE;
      }
      finally{
         this.sampleRate = sampleRate;
      }
   }
   
   /**
   */
   private void setStartDelay(String delay){
      int delayTime = DEFAULT_DELAY;
      try{
         delayTime = Integer.parseInt(delay);
         if(delayTime < 1){
            delayTime = DEFAULT_DELAY;
         }
      }
      catch(NullPointerException npe){
         delayTime = DEFAULT_DELAY;
      }
      catch(NumberFormatException nfe){
         delayTime = DEFAULT_DELAY;
      }
      finally{
         this.startDelay = delayTime;
      }
   }
   
   /**
   */
   private void setSynchronizedClock(boolean isSynchronized){
      this.isClockSynchronized = isSynchronized;
   }
   
   /**
   */
   private void setUnits(String type){
      if(type.contains("Celsius") || type.contains("celsius")){
         this.units = Units.METRIC;
      }
      else if(type.contains("Fahrenheit") ||
              type.contains("fahrenheit")){
         this.units = Units.ENGLISH;
      }
      else if(type.contains("Kelvin") || type.contains("kelvin")){
         this.units = Units.ABSOLUTE;
      }
      //The Default setting...go ahead and set to Metric
      else{
         this.units = Units.METRIC;
      }
   }
   
   /**
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
}
