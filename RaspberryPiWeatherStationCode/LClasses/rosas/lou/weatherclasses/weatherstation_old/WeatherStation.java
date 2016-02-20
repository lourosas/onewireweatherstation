/********************************************************************
WeatherStation Class (original model) for starting the
Weather Station executable for monitoring weather via the
OneWire Hardware
A class by Lou Rosas
********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

//
//
//
public class WeatherStation implements Observer{
   private static final int defaultUpdateRate = 10; //10 minutes

   private Hashtable sensorHash;
   private Hashtable tempSensorHash;
   private Hashtable humiditySensorHash;
   private int updateRate;
   private Stack tempSensorStack;
   private Stack humiditySensorStack;
   //Set up a Stack to hold all the dewpoint calculations for
   //multiple temperature and humidity sensors from all possible
   //weather networks (connected to separate ports reporting to
   //this station)
   private Stack dewpointStack;
   //Set up the Stack to hold all the dewpoint calculations for
   //multiple temperature and humidity sensors from all possible
   //wether networks (connected to separate ports reporting to
   //this station application)
   private Stack heatIndexStack;
   private boolean quit;

   //**********************Public Methods****************************
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public WeatherStation(){
      this.sensorHash         = new Hashtable();
      this.tempSensorHash     = new Hashtable();
      this.humiditySensorHash = new Hashtable();
      //For multiple temperature sensors
      this.tempSensorStack = new Stack();
      //For multiple humidity sensors
      this.humiditySensorStack = new Stack();
      //Set up multiple dewpoint calculations for the multiple
      //humidity and temperature sensors connected to this
      //application
      this.tempSensorStack = new Stack();
      //Set up the Dewpoint Stack to house the Dewpoint data
      this.dewpointStack = new Stack();
      //Set up the Heat Index Stack to house the Heat Index data
      this.heatIndexStack = new Stack();
      //Set to run indefinitely
      this.setQuit(false);
      //Set the update rate to 10 min.
      this.setUpdateRate(defaultUpdateRate);
      this.findSensorInformation();
      this.findTempSensorInformation();
      this.findHumiditySensorInformation();
      this.setUpTemperatureSensors();
      this.setUpHumiditySensors();
      //Set the Units after all the sensors are set up
      this.setUnitsEnglish();  //Be default, set the units to English
      this.runSystem();
   }

   /*****************************************************************
   Get the current update rate in minutes
   *****************************************************************/
   public int getUpdateRate(){
      return this.updateRate;
   }

   /*****************************************************************
   Run the weather station by polling the sensors at a specific
   rate
   *****************************************************************/
   public void runSystem(){
      Calendar cal   = Calendar.getInstance();
      double temp    = -1000.;
      int currentMinute;
      int lastMinute = -1;
      int i          = 0;
      int updateRate = this.getUpdateRate();
      //Run continuously until told otherwise
      while(!this.getQuit()){
         try{
            Thread.sleep(1000);  //Sleep for one second
         }
         catch(InterruptedException ie){}
         cal.setTimeInMillis(System.currentTimeMillis());
         currentMinute = cal.get(Calendar.MINUTE);
         if(currentMinute != lastMinute){
            if((i % updateRate) == 0){
               System.out.println(cal.getTime());
               this.grabTemperatureData();
               this.grabHumidityData();
               this.calculateDewpoint();
               this.calculateHeatIndex();
               //Go ahead and make a test print of all the relevant
               //data to this point
               this.testPrintWeatherData();
            }
            lastMinute = currentMinute;
            ++i;
         }
      }
      this.freeAllSensorPorts();
   }

   /*****************************************************************
   Set the quit boolean
   *****************************************************************/
   public void setQuit(boolean toQuit){
      this.quit = toQuit;
   }

   /*****************************************************************
   Set up the units for the temperature sensors.  This indicates
   The units the Temperature Sensors should return the Temperature
   Data and HOW the other temperature related data (Heat Index,
   Dew Point, Wind Chill) should be archived.
   *****************************************************************/
   public void setTemperatureUnits(int units){
      Enumeration e = tempSensorStack.elements();
      while(e.hasMoreElements()){
         TemperatureSensor ts = (TemperatureSensor)e.nextElement();
         try{
            ts.setUnits(units);
         }
         catch(UnitsException ue){
            ts.setUnits(TemperatureSensor.CELSIUS);
         }
      }
   }

   /*****************************************************************
   Set the units to absolute values:  really, this is only used
   for the temperature data:  this sets the units from the
   temperature sensor to Kelvin
   *****************************************************************/
   public void setUnitsAbsolute(){
      //Set the temperature units first
      this.setTemperatureUnits(TemperatureSensor.KELVIN);
   }

   /*****************************************************************
   Set the Units to Metric:  primarily for temperature related
   data
   *****************************************************************/
   public void setUnitsMetric(){
      //Set the temperature units first
      this.setTemperatureUnits(TemperatureSensor.CELSIUS);
   }

   /*****************************************************************
   Set the units to English:  primarily for Temperature related
   data
   *****************************************************************/
   public void setUnitsEnglish(){
      //Set the temperature units first
      this.setTemperatureUnits(TemperatureSensor.FAHRENHEIT);
   }

   /*****************************************************************
   Set the current update rate in minutes
   *****************************************************************/
   public void setUpdateRate(int rate){
      this.updateRate = rate;
   }

   /*****************************************************************
   Implementation of the update() method as part of implementing
   the Runnable interface
   *****************************************************************/
   public void update(Observable o, Object arg){
      if(o instanceof SearchPort){
         SearchPort sp = (SearchPort)o;
         System.out.println(sp.getPortSearchResults());
      }
   }

   //**********************Private Methods***************************
   //
   //Calculate the dewpoint for a given pair of humidity and
   //temperature sensors.  This is based on the temperature and
   //humidty sensor stacks and is based on the assumption that
   //That each temp sensor in the temperature stack corresponds 
   //to an exact humidity sensor at the same position in the
   //humidity stack.  The dewpoint is a formulaic calculation
   //based on temperature and relative humidity and is an
   //approximation.  The actual calculation depends upon wetbulb
   //and dry bulb measurements.  This is an approximation based on
   //the Manus-Tetens formula.
   //Td = (237.7 * alpha[t,RH])/(17.27 - alpha[t, RH])
   //where alpha[t,RH] = (17.27*t/(237.7 + t)) + ln(RH/100)
   //and 0.0 < RH < 100.0
   //
   private void calculateDewpoint(){
      final double a = 17.271;
      final double b = 237.7; //Degrees celsius
      Enumeration t = this.tempSensorStack.elements();
      Enumeration h = this.humiditySensorStack.elements();
      //Need to check the size of the Dewpoint stack to see if any
      //values.  Essentially, need to recreate the Stack everytime
      //So as not to mis-lead the number of sensors
      if(this.dewpointStack == null||this.dewpointStack.size() > 0){
         this.dewpointStack = new Stack();
      }
      while(t.hasMoreElements()){
         double dp = -999.9; //Dewpoint value (default)
         TemperatureSensor ts = null;
         HumiditySensor    hs = null;
         ts = (TemperatureSensor)t.nextElement();
         if(h.hasMoreElements()){
            hs = (HumiditySensor)h.nextElement();
         }
         //Only perform the calculation if there is BOTH a Temperature
         //and Humidity Sensor
         if((ts != null) && (hs != null)){
            //Need celsius for this calculation
            double temp = ts.getTemperatureCelsius();
            //Get the Device humidity for now
            double hum  = hs.getDeviceHumidity();
            if(hum >= 0. && temp > -999.9){
               double gamma =((a*temp)/(b+temp)) + Math.log(hum*.01);
               dp = (b*gamma)/(a - gamma);
               //Now need convert to the appropriate units relative to
               //the temperature sensor
               if(ts.getUnits() == TemperatureSensor.FAHRENHEIT){
                  dp = this.convertToFahrenheit(dp,
                                          TemperatureSensor.CELSIUS);
               }
               else if(ts.getUnits() == TemperatureSensor.KELVIN){
                  dp = this.convertToKelvin(dp,
                                          TemperatureSensor.CELSIUS);
               }
               //Don't do anything for celsius, since dewpoint is
               //in celsius already
            }
         }
         this.dewpointStack.push(new Double(dp));
      }
   }

   //
   //Calculate the heat index for a given pair of humidity and
   //temperature sensors.  This is based on the temperature and
   //humidity sensor stacks and is based on the assumption that
   //each temperature sensor in the temperature stack corresponds
   //to an excact humidity sensor at the same position in the
   //humidity stack.  The heat index is a formulaic calculation
   //based on temprature and relative humidity and is an
   //approximation (although pretty good) which is based on sixteen
   //calculations.
   //heatindex = 16.923                            +
   //            (.185212*tf)                      +
   //            (5.37941 * rh)                    -
   //            (.100254*tf*rh)                   +
   //            ((9.41685 * 10^-3) * tf^2)        +
   //            ((7.28898 * 10^-3) * rh^2)        +
   //            ((3.45372*10^-4) * tf^2*rh)       -
   //            ((8.14971*10^-4) * tf *rh^2)      +
   //            ((1.02102*10^-5) * tf^2 * rh^2)   -
   //            ((3.8646*10^-5) * tf^3)           +
   //            ((2.91583*10^-5) * rh^3)          +
   //            ((1.42721*10^-6) * tf^3 * rh)     +
   //            ((1.97483*10^-7) * tf * rh^3)     -
   //            ((2.18429*10^-8) * tf^3 * rh^2)   +
   //            ((8.43296*10^-10) * tf^2 * rh^3)  -
   //            ((4.81975*10^-11)*t^3*rh^3)
   //
   //NOTE:  The Heat Index Calculation becomes inaccurate at a Dry Bulb
   //       less than 70 F.  If that is the case, the default value
   //       is set.  For those values, the System will have to determine
   //       The reason for the default Heat Index.  It is up to the
   //       System to figure out the appropriateness of the Heat Index
   //       data for display.
   //
   private void calculateHeatIndex(){
      Enumeration t = this.tempSensorStack.elements();
      Enumeration h = this.humiditySensorStack.elements();
      //Need to check the size of the Heat Index stack to see if any
      //values.  Essentially, need to recreate the Stack everytime
      //So as not to mis-lead the number of sensors
      if(this.heatIndexStack==null || this.heatIndexStack.size()>0){
         this.heatIndexStack = new Stack();
      }
      while(t.hasMoreElements()){
         double hi = -999.9;  //The default dewpoint value
         TemperatureSensor ts = null;
         HumiditySensor    hs = null;
         ts = (TemperatureSensor)t.nextElement();
         if(h.hasMoreElements()){
            hs = (HumiditySensor)h.nextElement();
         }
         //ONLY perform the calculation if there is BOTH a
         //Temperature AND a Humidity Sensor
         if((ts != null) && (hs != null)){
            //Need the Fahrenheit temperature for this calculation
            double temp = ts.getTemperatureFahrenheit();
            //Get the Device Humidity (for now)
            double hum = hs.getDeviceHumidity();
            //Only perform this if the temp is >= 70 deg F
            if(hum >= 0. && temp >= 70.){
               hi =  16.923;
               hi += (.185212 * temp);
               hi += (5.37941 * hum);
               hi -= ((.100254) * temp * hum);
               hi += ((9.41685 * .001) * temp * temp);
               hi += ((7.28898 * .001) * hum * hum);
               hi += ((3.45372 * .0001) * temp * temp * hum);
               hi -= ((8.14971 * .0001) * temp * hum * hum);
               hi += ((1.02102 * .00001) * temp * temp * hum * hum);
               hi -= ((3.8646  * .00001) * temp * temp * temp);
               hi += ((2.91583 * .00001) * hum * hum * hum);
               hi += ((1.42721 * .000001)* temp * temp * temp * hum);
               hi += ((1.97483 * .0000001) * temp * hum * hum * hum);
               hi -= ((2.18429 * .00000001) * temp * temp * temp 
                                                        * hum * hum);
               hi += ((8.43196 * .0000000001) * temp * temp * hum 
                                                        * hum * hum);
               hi -= ((4.81975 * .00000000001) * temp * temp * temp
                                                  * hum * hum * hum);
               //Now need to convert to the appropriate units
               //relative to the temperature sensor
               if(ts.getUnits() == TemperatureSensor.CELSIUS){
                  hi = this.convertToCelsius(hi,
                                       TemperatureSensor.FAHRENHEIT);
               }
               else if(ts.getUnits() == TemperatureSensor.KELVIN){
                  hi = this.convertToKelvin(hi,
                                       TemperatureSensor.FAHRENHEIT);
               }
               //Don't do anything for Fahrenheit, heat index already
               //Calculated in that
            }
         }
         this.heatIndexStack.push(new Double(hi));
      }
   }

   //
   //Convert a given temperature to Celsius
   //
   private double convertToCelsius(double temp, int units){
      double returnTemp = temp;

      if(units == TemperatureSensor.FAHRENHEIT){
         //(5/9)(temperature - freezing point of water (F))
         returnTemp = .5556*(temp - 32);
      }
      else if(units == TemperatureSensor.KELVIN){
         //Subtract absolute zero from the value
         returnTemp = temp - 273.15;
      }
      return returnTemp;
   }

   //
   //Convert a given temperature to Kelvin
   //
   private double convertToKelvin(double temp, int units){
      double returnTemp = temp;

      if(units == TemperatureSensor.CELSIUS){
         //Add absolute zero to the value
         returnTemp = temp + 273.15;
      }
      else if(units == TemperatureSensor.FAHRENHEIT){
         //Convert to Celsius First
         returnTemp = this.convertToCelsius(temp, units);
         //Then Convert to Kelvin (add absolute zero)
         returnTemp += 273.15;
      }
      return returnTemp;
   }

   //
   //Convert a given temperature to Fahrenheit
   //
   private double convertToFahrenheit(double temp, int units){
      double returnTemp = temp;

      //Convert to Celsius and then to Fahrenheit
      if(units == TemperatureSensor.KELVIN){
         returnTemp = this.convertToCelsius(temp, units);
         //(9/5 * deg C) + the freezing point of water (F)
         returnTemp = ((1.8) * returnTemp) + 32;
      }
      else if(units == TemperatureSensor.CELSIUS){
         returnTemp = ((1.8) * returnTemp) + 32;
      }
      return returnTemp;
   }

   //
   //Find all possible ports
   //
   private Stack findPorts(){
      Stack portStack = new Stack();

      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      while(ports.hasMoreElements()){
         CommPortIdentifier cpi =
                             (CommPortIdentifier)ports.nextElement();
         if(cpi.getPortType() == CommPortIdentifier.PORT_SERIAL){
            portStack.push(new String(cpi.getName()));
         }
      }
      return portStack;
   }

   //
   //Find all the possible one wire sensor information
   //
   private void findSensorInformation(){
      Hashtable typeHash = new Hashtable();
      SearchPort searchPort = new SearchPort();
      searchPort.addObserver(this);
      Stack portStack = this.findPorts();
      while(!portStack.empty()){
         String portString = (String)portStack.pop();
         searchPort.setCurrentPort(portString);
         String hashString = searchPort.grabPortData();
         if(!hashString.startsWith("Nothing")){
            String[] data = hashString.split("\\n");
            for(int i = 0; i < data.length; i += 2){
               try{
                  typeHash.put(data[i], data[i + 1]);
               }
               catch(NullPointerException npe){
                  npe.printStackTrace();
               }
            }
            this.sensorHash.put(portString, typeHash);
         }
      }
   }

   //
   //Find all the Humidity Sensors on the One Wire Network
   //This conforms currently to Dallas Semiconductors:
   //DS2438.  Populate the appropriate Hashtable with this
   //information.
   //
   private void findHumiditySensorInformation(){
      //Get an enumeration of the keys in the Hashtable
      Enumeration e = this.sensorHash.keys();
      while(e.hasMoreElements()){
         String key = (String)e.nextElement();
         try{
            Hashtable typeHash = (Hashtable)this.sensorHash.get(key);
            Enumeration ids = typeHash.keys();
            while(ids.hasMoreElements()){
               String id   = (String)ids.nextElement();
               String name = (String)typeHash.get(id);
               if(name.equals("DS2438")){
                  Stack tempStack = new Stack();
                  tempStack.push("Hygrometer"); //Type
                  tempStack.push(name);
                  tempStack.push(id);
                  this.humiditySensorHash.put(key, tempStack);
               }
            }
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
         }
      }
   }

   //
   //Find all the Temperature Sensors on the One Wire Network
   //This conforms currently to Dallas Semiconductors:
   //DS18S20, DS1920.  Populate the appropriate Hashtable with
   //this information
   //
   private void findTempSensorInformation(){
      //Get an enumeration of the keys in the Hashtable
      Enumeration e = this.sensorHash.keys();
      while(e.hasMoreElements()){
         String key = (String)e.nextElement();
         try{
            Hashtable typeHash = (Hashtable)this.sensorHash.get(key);
            Enumeration ids = typeHash.keys();
            while(ids.hasMoreElements()){
               String id   = (String)ids.nextElement();
               String name = (String)typeHash.get(id);
               if(name.equals("DS1920") || name.equals("DS18S20")){
                  Stack tempStack = new Stack();
                  tempStack.push("Thermometer"); //Type
                  tempStack.push(name);
                  tempStack.push(id);
                  this.tempSensorHash.put(key, tempStack);
               }
            }
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
         }
      }
   }

   //
   //Free all the ports associated with all the sensors
   //
   private void freeAllSensorPorts(){
      Enumeration temps = this.tempSensorStack.elements();
      Enumeration humds = this.humiditySensorStack.elements();
      while(temps.hasMoreElements()){
         TemperatureSensor ts=(TemperatureSensor)temps.nextElement();
         ts.freePort();
      }
      while(humds.hasMoreElements()){
         HumiditySensor hs = (HumiditySensor)humds.nextElement();
         hs.freePort();
      }
   }

   //
   //Get the quit boolean (this is private, since the users of this
   //class need to know nothing about its state: just set the state)
   //
   private boolean getQuit(){
      return this.quit;
   }

   //
   //For all the possible humidity sensors, grab the humidity data
   //
   private void grabHumidityData(){
      double humidity;
      Enumeration e = this.humiditySensorStack.elements();
      while(e.hasMoreElements()){
         HumiditySensor hs;
         hs = (HumiditySensor)e.nextElement();
         try{
            hs.calculateHumidity();
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
            this.freeAllSensorPorts();
            System.exit(0);
         }
      }
   }

   //
   //For all the possible temperature sensors, grab the temperature
   //data
   //
   private void grabTemperatureData(){
      double temperature;
      Enumeration e = this.tempSensorStack.elements();
      while(e.hasMoreElements()){
         TemperatureSensor ts = (TemperatureSensor)e.nextElement();
         try{
            temperature = ts.calcTemperature();
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
            this.freeAllSensorPorts();
            System.exit(0);
         }
      }
   }

   //
   //Set up the Humidity Sensors (Instantiate the appropriate
   //number of Humidity Sensors)
   //
   private void setUpHumiditySensors(){
      HumiditySensor hs;
      Enumeration e = this.humiditySensorHash.keys();
      while(e.hasMoreElements()){
         String portName = (String)e.nextElement();
         try{
            Stack data=(Stack)this.humiditySensorHash.get(portName);
            String id   = (String)data.pop();
            String name = (String)data.pop();
            String type = (String)data.pop();
            hs = new HumiditySensor(portName, id, name, type);
            this.humiditySensorStack.push(hs);
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
	    this.freeAllSensorPorts();
	    System.exit(0);
         }
      }
   }

   //
   //Set up the Temperature Sensors (Instantiate the appropriate
   //number of Temperature Sensors)
   //
   private void setUpTemperatureSensors(){
      TemperatureSensor ts;
      Enumeration e = this.tempSensorHash.keys();
      while(e.hasMoreElements()){
         String portName = (String)e.nextElement();
         try{
            Stack sensorData = 
                            (Stack)this.tempSensorHash.get(portName);
            String id   = (String)sensorData.pop();
            String name = (String)sensorData.pop();
            String type = (String)sensorData.pop();
            ts = new TemperatureSensor(portName, id, name, type);
            ts.resetBus(); //Reset the bus of every temp sensor
            this.tempSensorStack.push(ts);
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
            this.freeAllSensorPorts();
            System.exit(0);
         }
      }
   }
   
   //
   //Make a test print of the weather data
   //
   private void testPrintWeatherData(){
      Enumeration tss = this.tempSensorStack.elements();
      Enumeration hss = this.humiditySensorStack.elements();
      Enumeration dps = this.dewpointStack.elements();
      Enumeration his = this.heatIndexStack.elements();
      while(tss.hasMoreElements()){
         TemperatureSensor ts = (TemperatureSensor)tss.nextElement();
         //Print out the Temperature Sensor data
         System.out.println(ts);
         if(hss.hasMoreElements()){
            HumiditySensor hs = (HumiditySensor)hss.nextElement();
            //Print out the Humidity Sensor data
            System.out.println(hs);
         }
         //When printing the data from the Dewpoint Stack, need to
         //go ahead and pop the dewpoint data from the stack
         if(dps.hasMoreElements()){
            //Go ahead an pop off the Stack--to avoid any confusion
            //as to the actual number of sensors
            Double dewpoint = (Double)this.dewpointStack.pop();
            System.out.print("Dewpoint = " + dewpoint);
            if(ts.getUnits() == TemperatureSensor.CELSIUS){
               System.out.print(" deg C\n\n" );
            }
            else if(ts.getUnits() == TemperatureSensor.FAHRENHEIT){
               System.out.print(" deg F\n\n");
            }
            else{
               System.out.print(" deg K\n\n");
            }
         }
         //Remember, this is just test printing, real system
         //discernment in another method
         if(his.hasMoreElements()){
            Double heatindex = (Double)this.heatIndexStack.pop();
            System.out.print("Heat Index = " + heatindex);
            if(ts.getUnits() == TemperatureSensor.CELSIUS){
               System.out.print(" deg C\n\n");
            }
            else if(ts.getUnits() == TemperatureSensor.FAHRENHEIT){
               System.out.print(" deg F\n\n");
            }
            else{
               System.out.print(" deg K\n\n");
            }
         }
      }
   }
}
