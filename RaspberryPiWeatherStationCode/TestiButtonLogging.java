import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class TestiButtonLogging{
   private DSPortAdapter      dspa;
   private OneWireContainer41  owc;

   public static void main(String [] args){
      new TestiButtonLogging();
   }
   
   public TestiButtonLogging(){
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable hash = ps.findPorts();
      Enumeration<String> e = hash.keys();
      while(e.hasMoreElements()){
         String key = (String)e.nextElement();
         Stack<String> current = (Stack)hash.get(key);
         this.findSensors(current);
      }
   }
   
   private void findSensors(Stack<String> s){
      WeatherNetwork wn = new WeatherNetwork();
      WeatherStation ws = new WeatherStation();
      Enumeration<String> e = s.elements();
      while(e.hasMoreElements()){
         String name    = e.nextElement();
         String address = e.nextElement();
         this.setUp(name, address);
      }
   }
   
   private void processMissionData(){
      try{
         System.out.println(this.owc.getNumberMissionChannels());
         this.owc.loadMissionResults();
         System.out.println("" + this.owc.isMissionLoaded());
         System.out.println("Temperature Readings");
         int tempChan = OneWireContainer41.TEMPERATURE_CHANNEL;
         if(this.owc.getMissionChannelEnable(tempChan)){
            int tempCount=this.owc.getMissionSampleCount(tempChan);
            System.out.println("Temperature Count:  " + tempCount);
            for(int i = 0; i < tempCount; i++){
               double temp = this.owc.getMissionSample(tempChan, i);
               System.out.println(temp);
            }
         }
         System.out.println("Humidity Readings");
         int humiChan = OneWireContainer41.DATA_CHANNEL;
         if(this.owc.getMissionChannelEnable(humiChan)){
            int humiCount=this.owc.getMissionSampleCount(humiChan);
            System.out.println("Humidity Count:  " + humiCount);
            for(int i = 0; i < humiCount; i++){
               double humidity =
                              this.owc.getMissionSample(humiChan,i);
               System.out.println(humidity);
            }
         }
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException we){ we.printStackTrace(); }
   }
   
   private void setUp(String name, String address){
      if(!name.equals("DS1990A")){
         System.out.println(name + ", " + address + "\n");
         //Now start to test the sensor
         this.setUpDSPA();
         this.setUpContainer(name, address);
         this.processMissionData();
         //this.setUpThermometer(name, address);
         //this.measure();
         //this.findMemory();
         //this.startMission();
      }   
   }
   
   private void setUpContainer(String name, String address){
      this.owc = new OneWireContainer41(this.dspa, address);
   }   
   
   private void setUpDSPA(){
      try{ this.dspa = OneWireAccessProvider.getDefaultAdapter(); }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException we){ we.printStackTrace(); }
   }
}