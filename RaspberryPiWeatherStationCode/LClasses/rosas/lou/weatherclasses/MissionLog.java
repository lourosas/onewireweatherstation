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
   private int temperatureDataCount;
   private int humidityDataCount;
   //Instantiate as a LinkedList
   private List<Double> temperatureLog;
   //Instantiate as a LinkedList
   private List<Double> humidityLog;
   //Instantiate as a LinkedLists
   private List<Date> temperatureTimeLog;
   private List<Date> humidityTimeLog;
   OneWireContainer41 owc41  = null;
   OneWireContainer22 owc22  = null;
   
   //*********************Public Methods***************************
   /*
   */
   public MissionLog(OneWireContainer41 currentOwc){
      this.setOneWireContainer41(currentOwc);
      this.temperatureLog     = new LinkedList<Double>();
      this.humidityLog        = new LinkedList<Double>();
      this.temperatureTimeLog = new LinkedList<Date>();
      this.humidityTimeLog    = new LinkedList<Date>();
   }
   
   /*
   */
   public void clearLog() throws MemoryException{
      //TBD:  Refer to Use Case I1
      OneWireContainer41 owc = this.getOneWireContainer41();
      try{
         //Need to stop Mission First!!
         this.stopLogging();
         this.humidityLog.clear();
         this.temperatureLog.clear();
         this.temperatureTimeLog.clear();
         this.humidityTimeLog.clear();
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
   
   /*
   */
   public OneWireContainer41 getOneWireContainer41(){
      return this.owc41;
   }
   
   /*
   */
   public OneWireContainer22 getOneWireContainer22(){
      return this.owc22;
   }
   
   /*
   */
   public List requestHumidityLog(){
      try{
         this.loadHumidityData();
      }
      catch(NumberFormatException nfe){
         this.humidityLog.clear();
         throw new MissionException(nfe.getMessage());
      }
      catch(NullPointerException npe){
         this.humidityLog.clear();
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         this.humidityLog.clear();
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         this.humidityLog.clear();
         throw new MissionException(owe.getMessage());
      }
      return this.humidityLog;
   }

   /*
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
   
   /*
   */
   public double requestHumidityLogAt(int index)
   throws MissionException{
      Double data = new Double(Double.NaN);
      try{
         data = (Double)this.humidityLog.get(index);
      }
      catch(IndexOutOfBoundsException ibe){
         throw new MissionException(ibe.getMessage());
      }
      return data.doubleValue();
   }
   
   /*
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
   
   /*
   */
   public double requestTemperatureLogAt(int index)
   throws MissionException{
      Double data = new Double(Double.NaN);
      try{
         data = (Double)this.temperatureLog.get(index);
      }
      catch(IndexOutOfBoundsException ibe){
         throw new MissionException(ibe.getMessage());
      }
      return data.doubleValue();
   }
   
   /*
   */
   public Date requestTemperatureTimeLogAt(int index)
   throws MissionException{
      Date date = new Date();
      try{
         date = (Date)this.temperatureTimeLog.get(index);
      }
      catch(IndexOutOfBoundsException ibe){
         throw new MissionException(ibe.getMessage());
      }
      return date;
   }
   
   /*
   */
   public List requestTemperatureLog() throws MissionException{
      try{
         this.loadTemperatureData();
      }
      catch(NumberFormatException nfe){
         this.temperatureLog.clear();
         throw new MissionException(nfe.getMessage());
      }
      catch(NullPointerException npe){
         this.temperatureLog.clear();
         throw new MissionException(npe.getMessage());
      }
      catch(OneWireIOException ioe){
         this.temperatureLog.clear();
         throw new MissionException(ioe.getMessage());
      }
      catch(OneWireException owe){
         this.temperatureLog.clear();
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

   /*
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
   
   /*
   Start a new mission with the default logging rate:  10 Min
   (600 sec), and the default start delay:  10 sec
   */
   public void startLogging() throws MissionException{
      this.startLogging(DEFAULT_LOGGING_RATE, DEFAULT_START_DELAY);
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
   I am not quiet sure how to accomplish this feat just yet.
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
 
   /*
   ****************Need to be revamped*************************
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
   I am not quiet sure how to accomplish this feat just yet.
   */
   public void startLogging(NewMissionData nmd)
   throws MissionException{
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
         channels[owc.TEMPERATURE_CHANNEL] =
                                  nmd.getEnableTemperatureChannel();
         //enable the humidity channel (log humidity data)
         channels[owc.DATA_CHANNEL]= nmd.getEnableHumidityChannel();
         //Start a new mission with the sample rate, the delay time,
         //rollover enabled (either set, or not set), sychClock
         //(either set, or not set), temperature, humidity channels
         //set as appropriate
         owc.startNewMission(nmd.getSampleRate(),
                             nmd.getStartDelay(),
                             nmd.getRolloverEnabled(),
                             nmd.getSynchClock(),
                             channels);
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
   /*
   */
   private long getHumidityLastSampleTime(){
      return 0;
   }
   
   /*
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
   
   /*
   */
   private void loadHumidityData() throws OneWireIOException,
   OneWireException, NullPointerException{
      //Clear the data in the Humidity List
      this.humidityLog.clear();
      OneWireContainer41 owc = this.getOneWireContainer41();
      int humidchan = OneWireContainer41.DATA_CHANNEL;
      owc.loadMissionResults();
      if(owc.getMissionChannelEnable(humidchan)){
         //Get the Sample Count for the Humidity Channel in the
         //Current Mission
         int owccount = owc.getMissionSampleCount(humidchan);
         for(int i = 0; i < owccount; i++){
            double humidity = owc.getMissionSample(humidchan, i);
            this.humidityLog.add(new Double(humidity));
         }
      }
   }
   
   /*
   */
   private void loadTemperatureData()
   throws OneWireIOException,OneWireException,NullPointerException{
      //Clear the data in the Temperature List
      this.temperatureLog.clear();
      OneWireContainer41 owc = this.getOneWireContainer41();
      int tempchan = OneWireContainer41.TEMPERATURE_CHANNEL;
      owc.loadMissionResults();
      //If the Temperature Channel is enabled, go ahead and store
      //the data in the temperature log
      if(owc.getMissionChannelEnable(tempchan)){
         //Get the Sample Count for the Temperature Channel in the
         //current mission
         int owccount = owc.getMissionSampleCount(tempchan);
         for(int i = 0; i < owccount; i++){
            double temp = owc.getMissionSample(tempchan, i);
            this.temperatureLog.add(new Double(temp));
         }
      }
   }
   
   /**
   */
   private List loadTemperatureData(Units units)
   throws OneWireIOException,OneWireException,NullPointerException{
      List<WeatherData> tempList = new LinkedList<WeatherData>();
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
         tempList = this.setUpWeatherData(dateList, tempList);
      }
      return tempList;
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
}
