//////////////////////////////////////////////////////////////////////
import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import rosas.lou.clock.TimeFormater;
import rosas.lou.clock.TimeListener;
import rosas.lou.clock.StopWatch;
import rosas.lou.clock.ClockState;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;

public class TestiButton implements TimeListener{
   private DSPortAdapter dspa;
   private TemperatureContainer thermalSensor;
   private ClockContainer clockContainer;
   private OneWireContainer41 owc;
   private OneWireContainer10 owcten;
   private OneWireContainer26 owctwosix;
   private HumidityContainer hygrometer;
   private StopWatch stopWatch;
   
   public static void main(String [] args){
      new TestiButton();
   }
   
   public TestiButton(){
      this.stopWatch = new StopWatch(300000);
      this.stopWatch.addTimeListener(this);
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable hash = ps.findPorts();
      Enumeration<Stack> e = hash.keys();
      while(e.hasMoreElements()){
         Stack<String> key     = (Stack)e.nextElement();
         Stack<String> current = (Stack)hash.get(key);
         this.setUpDSPA(key);
         this.findSensors(current);
      }
      Thread t = new Thread(this.stopWatch);
      t.start();
      this.stopWatch.start();
   }
   
   public void findSensors(Stack<String> s){
      WeatherNetwork wn = new WeatherNetwork();
      WeatherStation ws = new WeatherStation();
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         this.getData(name, address);
      }
   }

   ///////////////////////////////////////////////////////////////////
   public void update(ClockState cs){}

   public void update(Object o){}

   public void update(Object o, ClockState cs){}

   public void update(Stack<TimeFormater> tf, ClockState cs){}

   public void update(TimeFormater tf){
      this.measureTemp();
      this.measureHumidity();
   }

   public void update(TimeFormater tf, ClockState cs){}
   
   ///////////////////////////////////////////////////////////////////
   private void getData(String name, String address){
      if(!name.equals("DS1990A")){
         //Now start to test the sensor
         this.setUpSensors(name, address);
         //this.setUpThermometer(name, address);
         //this.measureTemp();
         //this.findMemory();
         //this.startMission();
      }
   }
   
   private void startMission(){
      try{
         this.owc.stopMission();
         this.owc.clearMemory();
         //skip the resolution part for now
         boolean [] enableChannel =
                   new boolean[this.owc.getNumberMissionChannels()];
         enableChannel[this.owc.TEMPERATURE_CHANNEL] = true;
         enableChannel[this.owc.DATA_CHANNEL]        = true;
         //Measure every 5 minutes
         this.owc.startNewMission(300,30,false,true,enableChannel);
      }
      catch(OneWireIOException iwe){ iwe.printStackTrace(); }
      catch(OneWireException owe){ owe.printStackTrace(); }
   }
   
   private void findMemory(){
      System.out.println("\nOWC:  " + this.owc + "\n");
      try{
         System.out.println(this.owc.isMissionRolloverEnabled());
         System.out.println(this.owc.getNumberMissionChannels());
      }
      catch(OneWireIOException iwe){ iwe.printStackTrace(); }
      catch(OneWireException owe){ owe.printStackTrace(); }
   }
   
   private void measure(){
      try{
         double currentTemp;
         double currentHumidity;
         //byte [] state = this.clockContainer.readDevice();
         //this.clockContainer.setClockRunEnable(true, state);
         //this.clockContainer.writeDevice(state);
         byte [] state = this.thermalSensor.readDevice();
         //perform the temperature conversion
         this.thermalSensor.doTemperatureConvert(state);
         //read the result of the converstion
         state = this.thermalSensor.readDevice();
         //get the temperature from the state data
         //value returned is in celsius
         currentTemp = this.thermalSensor.getTemperature(state);
         System.out.println(currentTemp);
         System.out.println(Convert.toFahrenheit(currentTemp));
         state = this.hygrometer.readDevice();
         this.hygrometer.doHumidityConvert(state);
         currentHumidity = this.hygrometer.getHumidity(state);
         System.out.println(currentHumidity);
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException owe){ owe.printStackTrace(); }
   }

   private void measureHumidity(){
      double humidity;
      try{
         //Read Temp
         byte[] state = this.owctwosix.readDevice();
         this.owctwosix.doTemperatureConvert(state);
         double temp = this.owctwosix.getTemperature(state);

         //Going to calculate old school
         //Read the output voltage
         this.owctwosix.doADConvert(OneWireContainer26.CHANNEL_VAD,
                                                               state);
         double vad = this.owctwosix.getADVoltage(
                               OneWireContainer26.CHANNEL_VAD, state);
         
         //Read the supply voltage
         this.owctwosix.doADConvert(OneWireContainer26.CHANNEL_VDD,
                                                               state);
         double vdd = this.owctwosix.getADVoltage(
                               OneWireContainer26.CHANNEL_VDD, state);

         //Calculate Humdity
         double rh = (vad/vdd - 0.16)*(5000.0/31.);
         humidity = (rh /(1.0546 -0.00216 * temp));
         System.out.println(rh);
         System.out.println(humidity);

         //Try to get humidity from device
         state = this.hygrometer.readDevice();
         this.hygrometer.doHumidityConvert(state);
         humidity = this.hygrometer.getHumidity(state);
         System.out.println(humidity);
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException owe){ owe.printStackTrace(); }
   }

   private void measureTemp(){
      try{
         double currentTempC;
         double currentTempF;
         double currentTempK;
         byte [] state = this.thermalSensor.readDevice();
         //perform the temperature conversion
         this.thermalSensor.doTemperatureConvert(state);
         //read the result of the conversion
         state = this.thermalSensor.readDevice();
         //get the temperature from the state data
         currentTempC = this.thermalSensor.getTemperature(state);
         //convert the temperature to Fahrenheit
         currentTempF = Convert.toFahrenheit(currentTempC);
         //convert the temperature to Kelvin
         currentTempK = currentTempC + 273.15;
         System.out.println(currentTempC);
         System.out.println(currentTempF);
         System.out.println(currentTempK);
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException   owe){ owe.printStackTrace(); }
   }
   
   private void setUpDSPA(){
      try{
         this.dspa = OneWireAccessProvider.getDefaultAdapter();
      }
      catch(OneWireIOException ioe){
         ioe.printStackTrace();
      }
      catch(OneWireException we){
         we.printStackTrace();
      }
   }

   private void setUpDSPA(Stack<String> adapterData){
      final int ADAPTER_DATA_SIZE = 2;
      if(adapterData.size() == ADAPTER_DATA_SIZE){
         String name = adapterData.pop();
         String port = adapterData.pop();
         try{
            this.dspa = OneWireAccessProvider.getAdapter(name, port);
         }
         catch(OneWireIOException ioe){ ioe.printStackTrace(); }
         catch(OneWireException   owe){ owe.printStackTrace(); }
      }
   }

   //Need to figure out which sensor has the DS2438:  the hydrometer
   //OR the Barometer (since they use the same type sensor).
   private void setUpHydrometer(String name, String address){
      System.out.println(name + ", " + address);
      this.hygrometer = new OneWireContainer26(this.dspa, address);
      this.owctwosix  = new OneWireContainer26(this.dspa, address);
   }

   private void setUpSensors(String name, String address){
      if(name.equals("DS1920") || name.equals("DS18S20")){
         this.setUpThermometer(name, address);
      }
      //This one will probably need to chnange, some what to
      //accomodate the Barometer...BUT, let me see if I can get the
      //Hygrometer working first!!
      else if(name.equals("DS2438")){
         this.setUpHydrometer(name, address);
      }
   }
   
   private void setUpThermometer(String name, String address){
      System.out.println(name + ", " + address);
      try{
         if(name.equals("DS1920") || name.equals("DS18S20")){
            this.thermalSensor =
                           new OneWireContainer10(this.dspa, address);
            this.owcten = new OneWireContainer10(this.dspa, address);
            byte [] state = this.thermalSensor.readDevice();
            if(this.thermalSensor.hasSelectableTemperatureResolution()){
               double [] resolution = 
                       this.thermalSensor.getTemperatureResolutions();
               //Set to the highest resolution
               this.thermalSensor.setTemperatureResolution(
                                  resolution[resolution.length - 1],
                                  state);
            }
            this.thermalSensor.writeDevice(state);
         }
         else{ 
            this.thermalSensor =
                         new OneWireContainer41(this.dspa, address);
            this.clockContainer =
                         new OneWireContainer41(this.dspa, address);
            this.hygrometer =
                         new OneWireContainer41(this.dspa, address);
            this.owc = new OneWireContainer41(this.dspa, address);
            byte [] state = this.thermalSensor.readDevice();
            if(this.thermalSensor.hasSelectableTemperatureResolution()){
               double[] resolution =
                     this.thermalSensor.getTemperatureResolutions();
               this.thermalSensor.setTemperatureResolution(
                                  resolution[resolution.length - 1],
                                  state);
            }
            this.thermalSensor.writeDevice(state);
            state = this.clockContainer.readDevice();
            this.clockContainer.setClockRunEnable(true, state);
            this.clockContainer.writeDevice(state);
            state = this.hygrometer.readDevice();
            if(this.hygrometer.hasSelectableHumidityResolution()){
               double [] resolution =
                           this.hygrometer.getHumidityResolutions();
               this.hygrometer.setHumidityResolution(
                                  resolution[resolution.length - 1],
                                  state);        
            }
            this.hygrometer.writeDevice(state); 
         }
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException owe){ owe.printStackTrace(); }
   }
}
//////////////////////////////////////////////////////////////////////
