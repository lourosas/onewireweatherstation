import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.PortSniffer;
import gnu.io.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;

public class PortApp{
   private PortSniffer ps;
   private DSPortAdapter dspa;
   private TemperatureContainer thermo;

   {
      ps     = null;
      dspa   = null;
      thermo = null;
   };
   public static void main(String[] args){
      new PortApp();
   }

   public PortApp(){
      ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable<Stack<String>,Stack<String>> hash = ps.findPorts();
      Enumeration<Stack<String>> e = hash.keys();
      String name = null;
      String port = null;
      while(e.hasMoreElements()){
         Stack<String> key      = e.nextElement();
         Stack<String> elements = hash.get(key);
         name = key.pop();
         port = key.pop();
         System.out.println("Name:  "+name+", "+"Port: "+port);
         while(!elements.empty()){
            System.out.println("Element:  "+elements.pop());
         }
      }
      try{
         this.dspa = OneWireAccessProvider.getAdapter(name, port);
         this.printSensors();
         try{
            int x = 0;
            while(x < 10000){
               this.measure();
               Thread.sleep(600000);
               ++x;
            }
         }
         catch(InterruptedException ie){}
         
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException   owe){ owe.printStackTrace(); }
   }

   private void measure(){
      try{
         byte [] state = this.thermo.readDevice();
         this.thermo.doTemperatureConvert(state);
         state = this.thermo.readDevice();
         double currentTemp = this.thermo.getTemperature(state);
         System.out.println(currentTemp);
         System.out.println(Convert.toFahrenheit(currentTemp));
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException   owe){ owe.printStackTrace(); }
   }

   private void printSensors(){
      try{
         Enumeration<OneWireContainer> e = 
                                  this.dspa.getAllDeviceContainers();
         while(e.hasMoreElements()){
            OneWireContainer o = (OneWireContainer)e.nextElement();
            if(o.getName().equals("DS1920") || 
               o.getName().equals("DS18S20")){
               System.out.println(o.getName());
               System.out.println(o.getAddressAsString());
               System.out.println(o.getDescription());
               this.thermo = new OneWireContainer10(dspa,
                                              o.getAddressAsString());
               byte [] state = this.thermo.readDevice();
               if(this.thermo.hasSelectableTemperatureResolution()){
                  double [] resolution = 
                              this.thermo.getTemperatureResolutions();
                  this.thermo.setTemperatureResolution(
                               resolution[resolution.length-1],state);
                  this.thermo.writeDevice(state);
               }
            }
         }
      }
      catch(OneWireIOException ioe){ ioe.printStackTrace(); }
      catch(OneWireException   owe){ owe.printStackTrace(); }
   }
}
