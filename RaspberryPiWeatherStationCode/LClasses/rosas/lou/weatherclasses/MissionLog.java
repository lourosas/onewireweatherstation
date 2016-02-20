/*
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class MissionLog{
   //Default Logging Rate set to 600 sec (10 min.)
   public static final int DEFAULT_LOGGING_RATE = 600;
   //Default Start Delay = 10 sec-->delay 10 secs before starting
   //a new mission
   public static final int DEFAULT_START_DELAY = 10;
   private int humidityDataCount;
   private boolean rollover;
   private boolean synched;
   private double  tempHighAlarm;
   private double  tempLowAlarm;
   private double  humidityHighAlarm;
   private double  humidityLowAlarm;
   private boolean temperatureChannelEnabled;
   private boolean humidityChannelEnabled;
   private int     sampleRate;
   private int     startDelay;
   private int     temperatureDataCount;
   //Instantiate as a LinkedList
   private List<WeatherData> temperatureLog;
   //Instantiate as a LinkedList
   private List<WeatherData> humidityLog;
   //Instantiate as a LinkedLists
   private List<Date> temperatureTimeLog;
   private List<Date> humidityTimeLog;
   private OneWireContainer41 owc41;
   private OneWireContainer22 owc22;
   //Initializer
   {
      this.temperatureDataCount = 0;
      this.humidityDataCount    = 0;

      this.rollover = false;
      this.synched  = false;

      this.tempHighAlarm = Double.NaN;
      this.tempLowAlarm  = Double.NaN;
      this.humidityHighAlarm = Double.NaN;
      this.humidityLowAlarm  = Double.NaN;

      this.temperatureChannelEnabled = false;
      this.humidityChannelEnabled    = false;
      
      this.sampleRate     = DEFAULT_LOGGING_RATE;
      this.startDelay     = DEFAULT_START_DELAY;
      this.temperatureLog = null;
      this.humidityLog    = null;
      this.owc41          = null;
      this.owc22          = null;
   }
   
   //*********************Public Methods***************************
   /*
   */
   public MissionLog(OneWireContainer41 currentOwc){
      this.setOneWireContainer41(currentOwc);
   }
   
   /**
   */
   public void clearLog() throws MemoryException{
      //TBD:  Refer to Use Case I1
      OneWireContainer41 owc = this.getOneWireContainer41();
      try{
         //Need to stop Mission First!!
         this.stopLogging();
         this.humidityLog.clear();
         this.temperatureLog.clear();
         //this.temperatureTimeLog.clear();
         //this.humidityTimeLog.clear();
         owc.clearMissionResults();
      }
      catch(MissionException me){
         throw new MemoryException(me.getMessage());
      }
      catch(NullPointerException npe){
         throw new MemoryException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MemoryException(ioe.getMessage());
      }
      catch(OneWireException   owe){
         throw new MemoryException(owe.getMessage());
      }
   }
   
   /**
   */
   public OneWireContainer41 getOneWireContainer41(){
      return this.owc41;
   }
   
   /**
   */
   public OneWireContainer22 getOneWireContainer22(){
      return this.owc22;
   }
   
   /**
   */
   public boolean isMissionRunning() throws MissionException{
      OneWireContainer41 owc = this.getOneWireContainer41();
      boolean isRunning = false;
      try{
         isRunning = owc.isMissionRunning();
         return isRunning;
      }
      catch(NullPointerException npe){
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         throw new MissionException(owe.getMessage());
      }
   }
   
   /**
   */
   public List requestHumidityLog(){
      this.humidityLog = new LinkedList<WeatherData>();
      try{
         this.humidityLog = this.loadHumidityData();
      }
      catch(NumberFormatException nfe){
         throw new MissionException(nfe.getMessage());
      }
      catch(NullPointerException npe){
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         throw new MissionException(owe.getMessage());
      }
      return this.humidityLog;
   }

   /**
   */
   public List requestHumidityTimeLog() throws MissionException{
      try{
         this.loadHumidityTimeStampData();
      }
      catch(NullPointerException npe){
         this.humidityTimeLog.clear();
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException iwe){
         this.humidityTimeLog.clear();
         throw new MissionException(iwe.getMessage());
      }
      catch(OneWireException owe){
         this.humidityTimeLog.clear();
         throw new MissionException(owe.getMessage());
      }
      return this.humidityTimeLog;
   }
   
   /**
   */
   public Date requestHumidityTimeLogAt(int index)
   throws MissionException{
      Date date = new Date();
      try{
         date = (Date)this.humidityTimeLog.get(index);
      }
      catch(IndexOutOfBoundsException ibe){
         throw new MissionException(ibe.getMessage());
      }
      return date;
   }
   
   /**
   */
   public List requestTemperatureLog() throws MissionException{
      this.temperatureLog = new LinkedList<WeatherData>();
      try{
         this.temperatureLog = this.loadTemperatureData();
      }
      catch(NumberFormatException nfe){
         throw new MissionException(nfe.getMessage());
      }
      catch(NullPointerException npe){
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         throw new MissionException(owe.getMessage());
      }
      return this.temperatureLog;
   }
   
   /**
   */
   public List requestTemperatureLog(Units units)
   throws MissionException{
      List<WeatherData> tempList = new LinkedList<WeatherData>();
      try{
         tempList = this.loadTemperatureData(units);
      }
      catch(NumberFormatException nfe){
         throw new MissionException(nfe.getMessage());
      }
      catch(NullPointerException npe){
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         throw new MissionException(owe.getMessage());
      }
      return tempList;
   }

   /**
   */
   public List requestTemperatureTimeLog() throws MissionException{
      try{
         this.loadTemperatureTimeStampData();
      }
      catch(NumberFormatException nfe){
         this.temperatureTimeLog.clear();
         throw new MissionException(nfe.getMessage());
      }
      catch(NullPointerException npe){
         this.temperatureTimeLog.clear();
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException iwe){
         this.temperatureTimeLog.clear();
         throw new MissionException(iwe.getMessage());
      }
      catch(OneWireException owe){
         this.temperatureTimeLog.clear();
         throw new MissionException(owe.getMessage());
      }
      return this.temperatureTimeLog;
   }
   
   /**
   */
   public void setHighHumidityAlarm(double humidity){
      this.humidityHighAlarm = humidity;
   }
   
   /**
   */
   public void setHighTemperatureAlarm(double temp){
      this.tempHighAlarm = temp;
   }
   
   /**
   */
   public void setLowHumidityAlarm(double humidity){
      this.humidityLowAlarm = humidity;
   }
   
   /**
   */
   public void setLowTemperatureAlarm(double temp){
      this.tempLowAlarm = temp;
   }
      
   /**
   */
   public void setHumidityEnabled(boolean enabled){
      this.humidityChannelEnabled = enabled;
   }
   
   /**
   */
   public void setRollover(boolean rollover){
      this.rollover = rollover;
   }
   
   /**
   */
   public void setSampleRate(int rate){
      this.sampleRate = rate;
   }

   /**
   */
   public void setStartDelay(int delay){
      this.startDelay = delay;
   }
   
   /**
   */
   public void setTemperatureEnabled(boolean enabled){
      this.temperatureChannelEnabled = enabled;
   }
   
   /**
   */
   public void setSynchronizedClock(boolean synched){
      this.synched = synched;
   }
   
   /**
   Load in the mission data and start logging...essentially, message
   the Mission Container to start a new mission
   */
   public void startLogging() throws MissionException{
      try{
         OneWireContainer41 owc = this.getOneWireContainer41();
         boolean[] channels =
                          new boolean[owc.getNumberMissionChannels()];
         channels[owc.TEMPERATURE_CHANNEL] =
                                       this.temperatureChannelEnabled;
         channels[owc.DATA_CHANNEL] = this.humidityChannelEnabled;
         if(this.tempHighAlarm != Double.NaN){
            owc.setMissionAlarm(owc.TEMPERATURE_CHANNEL,
                                TemperatureContainer.ALARM_HIGH,
                                this.tempHighAlarm);
            owc.setMissionAlarmEnable(owc.TEMPERATURE_CHANNEL,
                                      TemperatureContainer.ALARM_HIGH,
                                      true);
         }
         if(this.tempLowAlarm != Double.NaN){
            owc.setMissionAlarm(owc.TEMPERATURE_CHANNEL,
                                TemperatureContainer.ALARM_LOW,
                                this.tempLowAlarm);
            owc.setMissionAlarmEnable(owc.TEMPERATURE_CHANNEL,
                                      TemperatureContainer.ALARM_LOW,
                                      true);
         }
         if(this.humidityHighAlarm != Double.NaN){
            owc.setMissionAlarm(owc.DATA_CHANNEL,
                                HumidityContainer.ALARM_HIGH,
                                this.humidityHighAlarm);
            owc.setMissionAlarmEnable(owc.DATA_CHANNEL,
                                      HumidityContainer.ALARM_HIGH,
                                      true);
         }
         if(this.humidityLowAlarm != Double.NaN){
             owc.setMissionAlarm(owc.DATA_CHANNEL,
                                HumidityContainer.ALARM_LOW,
                                this.humidityLowAlarm);
            owc.setMissionAlarmEnable(owc.DATA_CHANNEL,
                                      HumidityContainer.ALARM_LOW,
                                      true);
         }
         //Set the Humidity and Temperature Resolutions (to the
         //lowest for now)
         owc.setMissionResolution(owc.TEMPERATURE_CHANNEL,
               owc.getMissionResolutions(owc.TEMPERATURE_CHANNEL)[0]);
         owc.setMissionResolution(owc.DATA_CHANNEL,
                      owc.getMissionResolutions(owc.DATA_CHANNEL)[0]);
         //Now, start the new Mission
         owc.startNewMission(this.sampleRate, this.startDelay,
                               this.rollover, this.synched, channels);
      }
      catch(NullPointerException npe){
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         throw new MissionException(owe.getMessage());
      }
   }
   
   /*
   Start a new mission with a desired logging rate, and the default
   start delay:  10 sec
   */
   public void startLogging(int sampleRate) throws MissionException{
      this.startLogging(sampleRate, DEFAULT_START_DELAY);
   }
   
   /*
   Start a new mission with the desired logging rage and desired
   start delay.
   
   Lots more to write here!
   First, this method is NOT complete!  There is so much more to do
   with this method!
   This is what I call the "Default" type method!  I started very
   easy at the start!
   As more functionality is added to the application, I suspect 
   either this method will need improvement OR other methods will
   need to be added to handle the extra functionality.  I am going
   to have to think long and hard about that.
   Here is a list of functionality NOT added to this method (and to
   the current app)
   1)  No High Or Low Temperature Alarm Settings
   2)  No High or Low Humidity Alarm Settings
   3)  No Mission Resolution for EITHER high or low settings:  I am
       currently ONLY using the default settings
   4)  No Independent setting of the channels (Temperature or
       humidity)
   As stated earlier, all of this will EVENTUALLY have to go into
   the functionality of the application:  be that in this method, or
   others.
   I am not quite sure how to accomplish this feat just yet.
   */
   public void startLogging
   (
      int sampleRate,
      int startDelay
   ) throws MissionException{
      OneWireContainer41 owc = this.getOneWireContainer41();
      try{
         //First, if a mission is in progress, go ahead and stop
         //the mission
         this.stopLogging();
         //Get the total number of mission channels, first
         boolean[] channels = 
                        new boolean[owc.getNumberMissionChannels()];
         //Lots more setting (probably) to go in here...TBD...
         //enable the temperature channel (log temperature data)
         channels[owc.TEMPERATURE_CHANNEL] = true;
         //enable the humidity channel (log humidity data)
         channels[owc.DATA_CHANNEL]        = true;
         //Start a new mission with the sample rate, the delay time,
         //By default, NO rollover enabled, by default, the clock
         //synched and by default, BOTH the Temperature and Humidity
         //channels enabled
         owc.startNewMission(sampleRate, startDelay,
                                             false, true, channels);
      }
      catch(NullPointerException npe){
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         throw new MissionException(owe.getMessage());
      }
   }

   /**
   */
   public void stopLogging() throws MissionException{
      OneWireContainer41 owc = this.getOneWireContainer41();
      try{
         owc.stopMission();
      }
      catch(NullPointerException npe){
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException   owe){
         throw new MissionException(owe.getMessage());
      }
   }
   
   //**********************Private Methods*************************
   /**
   */
   private double getHighHumidityAlarm(){
      return this.humidityHighAlarm;
   }
   
   /**
   */
   private double getHighTemperatureAlarm(){
      return this.tempHighAlarm;
   }
   
   /**
   */
   private boolean getHumidityEnabled(){
      return this.humidityChannelEnabled;
   }
   
   /**
   */
   private double getLowHumidityAlarm(){
      return this.humidityLowAlarm;
   }
   
   /**
   */
   private double getLowTemperatureAlarm(){
      return this.tempLowAlarm;
   }
   
   /**
   */
   private boolean getTemperatureEnabled(){
      return this.temperatureChannelEnabled;
   }
   
   /**
   */
   private long getHumidityLastSampleTime(){
      return 0;
   }
   
   /**
   */
   private boolean getRollover(){
      return this.rollover;
   }
   
   /**
   */
   private int getSampleRate(){
      return this.sampleRate;
   }
   
   /**
   */
   private int getStartDelay(){
      return this.startDelay;
   }
   
   /**
   */
   private boolean getSychronizedClock(){
      return this.synched;
   }
   
   /**
   */
   private long getTemperatureLastSampleTime(){
      //Set the default value to this, this is technically
      //impossible...and will guarantee the List will load
      long time = -1;
      if(!this.temperatureTimeLog.isEmpty()){
         int size = this.temperatureTimeLog.size();
         Date date = (Date)this.temperatureTimeLog.get(size - 1);
         time = date.getTime();
      }
      return time;
   }
   
   private List loadHumidityData() throws OneWireIOException,
   OneWireException, NullPointerException{
      List<WeatherData> humidityList =new LinkedList<WeatherData>();
      OneWireContainer41 owc = this.getOneWireContainer41();
      int humidchan = OneWireContainer41.DATA_CHANNEL;
      owc.loadMissionResults();
      if(owc.getMissionChannelEnable(humidchan)){
         //Get the Sample Count for the Humidity Channel in the
         //Current Mission
         int owccount = owc.getMissionSampleCount(humidchan);
         List<Double> humiList = new LinkedList<Double>();
         List<Date>   dateList = new LinkedList<Date>();
         for(int i = 0; i < owccount; i++){
            long time = owc.getMissionSampleTimeStamp(humidchan, i);
            double humidity = owc.getMissionSample(humidchan, i);
            dateList.add(new Date(time));
            humiList.add(new Double(humidity));
         }
         humidityList = this.setUpWeatherData(humiList,
                                              dateList,
                                              Types.HUMIDITY,
                                              Units.NULL);
      }
      return humidityList;
   }
   
   /**
   Grab all the temperature data in the three different units; and
   return the appended list.
   */
   private List loadTemperatureData()
   throws OneWireIOException,OneWireException,NullPointerException{
      List<WeatherData> temperatureList =
                                      new LinkedList<WeatherData>();
      OneWireContainer41 owc = this.getOneWireContainer41();
      int tempchan = OneWireContainer41.TEMPERATURE_CHANNEL;
      owc.loadMissionResults();
      //If the Temperature Channel is enabled, go ahead and store
      //the data in the WeatherData List (Which is essentially a big
      //log)
      if(owc.getMissionChannelEnable(tempchan)){
         //Get the Sample Count for the Temperature Channel in the
         //Current Mission
         int owccount = owc.getMissionSampleCount(tempchan);
         List<Double> tempList = new LinkedList<Double>();
         List<Date>   dateList = new LinkedList<Date>();
         for(int i = 0; i < owccount; i++){
            long time = owc.getMissionSampleTimeStamp(tempchan, i);
            double temp = owc.getMissionSample(tempchan, i);
            dateList.add(new Date(time));
            tempList.add(new Double(temp));
         }
         List<WeatherData> l = new LinkedList<WeatherData>();
         l = this.setUpWeatherData(tempList,dateList,
                                   Types.TEMPERATURE, Units.METRIC);
         temperatureList.addAll(l);
         l = this.setUpWeatherData(tempList,dateList,
                                   Types.TEMPERATURE,
                                   Units.ENGLISH);
         temperatureList.addAll(l);
         l = this.setUpWeatherData(tempList, dateList,
                                   Types.TEMPERATURE,
                                   Units.ABSOLUTE);
         temperatureList.addAll(l);
      }
      return temperatureList;
   }
   
   /**
   */
   private List loadTemperatureData(Units units)
   throws OneWireIOException,OneWireException,NullPointerException{
      List<WeatherData> temperatureList =
                                      new LinkedList<WeatherData>();
      OneWireContainer41 owc = this.getOneWireContainer41();
      int tempchan = OneWireContainer41.TEMPERATURE_CHANNEL;
      owc.loadMissionResults();
      //If the Temperature Channel is enabled, go ahead and store
      //the data in the WeatherData List (Which is essentially a big
      //log)
      if(owc.getMissionChannelEnable(tempchan)){
         //Get the Sample Count for the Temperature Channel in the
         //Current Mission
         int owccount = owc.getMissionSampleCount(tempchan);
         List<Double> tempList = new LinkedList<Double>();
         List<Date>   dateList = new LinkedList<Date>();
         for(int i = 0; i < owccount; i++){
            long time = owc.getMissionSampleTimeStamp(tempchan, i);
            double temp = owc.getMissionSample(tempchan, i);
            dateList.add(new Date(time));
            tempList.add(new Double(temp));
         }
         temperatureList = this.setUpWeatherData(tempList,
                                                 dateList,
                                                 Types.TEMPERATURE,
                                                 units);
      }
      return temperatureList;
   }
   
   /*
   */
   private void loadHumidityTimeStampData()
   throws OneWireIOException, OneWireException,
   NullPointerException{
      //Clear the data in the Humidity Time List
      this.humidityTimeLog.clear();
      OneWireContainer41 owc = this.getOneWireContainer41();
      int humiditychan = OneWireContainer41.DATA_CHANNEL;
      owc.loadMissionResults();
      //Get the Sample Count for the Temperature Channel in the
      //current mission
      int owccount = owc.getMissionSampleCount(humiditychan);
      //Now, need to load the data into the appropriate List
      for(int i = 0; i < owccount; i++){
         long time = owc.getMissionSampleTimeStamp(humiditychan, i);
         this.humidityTimeLog.add(new Date(time));
      }
   }
   
   /*
   */
   private void loadTemperatureTimeStampData()
   throws OneWireIOException, OneWireException,
   NullPointerException{
      //Clear the data in the Temperature Time List
      this.temperatureTimeLog.clear();
      OneWireContainer41 owc = this.getOneWireContainer41();
      int tempchan = OneWireContainer41.TEMPERATURE_CHANNEL;
      owc.loadMissionResults();
      //Get the Sample Count for the Temperature Channel in the
      //current mission
      int owccount = owc.getMissionSampleCount(tempchan);
      //Now, need to load the data into the appropriate List
      for(int i = 0; i < owccount; i++){
         long time = owc.getMissionSampleTimeStamp(tempchan, i);
         this.temperatureTimeLog.add(new Date(time));
      }
   }
   
   /*
   */
   private void setOneWireContainer41
   (
      OneWireContainer41 currentOwc
   ){
      this.owc41 = currentOwc;
   }
   
   /*
   */
   private void setOneWireContainer22
   (
      OneWireContainer22 currentOwc
   ){
      this.owc22 = currentOwc;
   }
   
   /**
   The purpose of this method is to create a WeatherData List
   GROUPED by Date, returning that list to the messaging method
   */
   private List setUpWeatherData
   (
      List<Double> data,
      List<Date>   date,
      Types        type,
      Units        units
   ){
      List<WeatherData> returnList = new LinkedList<WeatherData>();
      //First Step, is to find the data with the SAME date
      int from         = 0;
      int dateListSize = date.size();
      WeatherData wd;
      try{
         for(int i = 0; i < dateListSize; i++){
            Date fromDate    = date.get(from);
            Date currentDate = date.get(i);
            Calendar fromCalendar    = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();
            fromCalendar.setTime(fromDate);
            currentCalendar.setTime(currentDate);
            //Need to set up some testing at this point!
            if(fromCalendar.get(Calendar.DAY_OF_YEAR) !=
               currentCalendar.get(Calendar.DAY_OF_YEAR)){
               //Set up the Sub-List for both the data and date
               List<Double> subdata = data.subList(from, i);
               List<Date>   subdate = date.subList(from, i);
               //Create the WeatherData instance based on the data
               wd = new WeatherData(type, units, subdata, subdate);
               //Add to the returnList
               returnList.add(wd);
               //set from to i
               from = i;
            }
         }
         List<Double> subdata = data.subList(from, dateListSize);
         List<Date>   subdate = date.subList(from, dateListSize);
         //Create the WeatherData instance based on the data
         wd = new WeatherData(type, units, subdata, subdate);
         //Add to the returnList
         returnList.add(wd);
      }
      catch(IndexOutOfBoundsException be){
         be.printStackTrace();
      }
      //Return what you have, regardless
      finally{
         return returnList;
      }
   }
}
