/********************************************************************
WeatherStation Class for starting the Weather Station.  Used for
monitoring weather data via the One-Wire Hardware
A class by Lou Rosas
********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

//Database stuff
import java.sql.*;

//
//
//
public class WeatherStation extends Observable{

   private static final short DEFAULT_UPDATE_RATE = 10; //10 minutes
   private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   private static final String DB_URL =
                                "jdbc:mysql://localhost/weatherdata";
   private static final String DB_USER_PW = "weatherstation";

   private short   updaterate;
   private short   porttype;
   private boolean toQuit;
   private short   currentUnits;

   private Stack   sensorStack;
   private Stack   dewpointStack;
   private Stack   heatindexStack;
   private Stack   windchillStack;

   private String currentDate;
   private String currentDBTable;

   private Hashtable temperatureHash;
   private Hashtable humidityHash;
   private Hashtable barometerHash;

   public static final short USB_PORT       = 0;
   public static final short SERIAL_PORT    = 1;
   public static final short UNITS_ENGLISH  = 0;
   public static final short UNITS_METRIC   = 1;
   public static final short UNITS_ABSOLUTE = 2;


   /**************************Constructors**************************/
   /**
   Constructor of no arguments
   */
   public WeatherStation(){
      //Default to the Default Update rate and Port (Serial)
      this(SERIAL_PORT, UNITS_ENGLISH);
   }

   /**
   Constructor Accepting the Port Type (USB or Serial)
   */
   public WeatherStation(short port_type){
      //Default to the DEFAULT_UPDATE_RATE
      this(port_type, UNITS_ENGLISH);
   }

   /**
   Constructor Accepting the Port Type (USB or Serial) and the
   update rate (in minutes)
   */
   public WeatherStation(short port_type, short units){
      this.sensorStack     = new Stack();
      this.temperatureHash = new Hashtable();
      this.humidityHash    = new Hashtable();
      this.barometerHash   = new Hashtable();
      //Set the Port Type
      if(port_type == USB_PORT || port_type == SERIAL_PORT){
         this.portType(port_type);
      }
      else{//Default to Serial Port, let the Hardware figure it out
         this.portType(SERIAL_PORT);
      }
      if(units >= UNITS_ENGLISH && units <= UNITS_ABSOLUTE){
         this.units(units);
      }
      else{ //Default to English Units
         this.units(UNITS_ENGLISH);
      }
      //Set up the Date String ONLY (this is handled in the
      //runSystem() method
      this.currentDate    = new String();
      //Set up the Current Data Base Table name (to nothing)
      this.currentDBTable = new String();
      //Run Continuously
      this.quit(false);
      //Set the Update Rate
      this.updateRate(DEFAULT_UPDATE_RATE);
      //Find the Sensor Information
      this.findSensorInformation();
      //Now, Run the System
      this.runSystem();
   }

   /**********************Public Methods****************************/
   /**
   Set the port type (USB or Serial)
   */
   public void portType(short port_type){
      this.porttype = port_type;
   }

   /**
   Get the port type (USB or Serial)
   */
   public short portType(){
      return this.porttype;
   }

   /**
   Indicate it is time to quit (stop) the Weather Station
   */
   public void quit(boolean quit){
      this.toQuit = quit;
   }

   /**
   Run the Weather Station by polling the sensors at a specific rate
   */
   public void runSystem(){
      Calendar cal   = Calendar.getInstance();
      int lastMinute = -1;
      int i          =  0;
      int updateRate = this.updateRate();
      int currentMinute;
      //Run Continuously until told otherwise
      while(!this.quit()){
         try{
            Thread.sleep(1000); //Sleep for one second
         }
         catch(InterruptedException ie){ ie.printStackTrace(); }
         cal.setTimeInMillis(System.currentTimeMillis());
         currentMinute = cal.get(Calendar.MINUTE);
         if(currentMinute != lastMinute){
            if((i % updateRate) == 0){
               DateFormat df =
                        DateFormat.getDateInstance(DateFormat.SHORT);
               System.out.println(df.format(cal.getTime()));
               String s = String.format("%tT", cal.getTime());
               System.out.println(s);
               this.grabTemperatureData();
               this.grabHumidityData();
               this.calculateDewpoint();
               this.calculateHeatIndex();
               this.grabBarometricData();
               //Save to database after hardware query
               this.saveToDataBase();
            }
            lastMinute = currentMinute;
            ++i;
         }
      }
      //After all that's said and done, free the Sensor Ports
      this.freeAllSensorPorts();
   }

   /**
   Set the System Units (Metric, English, Absolute)
   */
   public void units(short units){
      this.currentUnits = units;
   }

   /**
   Get the System Units (Metric, English, Absolute)
   */
   public short units(){
      return this.currentUnits;
   }

   /**
   Set the Update Rate:  the rate (in minutes) in which the sensors
   are polled
   */
   public void updateRate(short rate){
      this.updaterate = rate;
   }

   /**
   Get the Update Rate:  the rate (in minutes) in which the sensors
   are polled
   */
   public short updateRate(){
      return this.updaterate;
   }

   /***********************Private Methods**************************/
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
   Td = (237.7 * alpha[t,RH])/(17.27 - alpha[t, RH])
   where alpha[t,RH] = (17.27*t/(237.7 + t)) + ln(RH/100)
   and 0.0 < RH < 100.0
   */
   private void calculateDewpoint(){
      final double a = 17.271;
      final double b = 237.7;  //Degrees Celsius

      double dp = -999.9; //Default Dewpoint Value

      Enumeration e  = this.temperatureHash.keys();
      while(e.hasMoreElements()){
         dp = -999.9;
         this.dewpointStack = new Stack();
         try{
            String kv = (String)e.nextElement(); //Key Value
            TemperatureSensor ts = 
                     (TemperatureSensor)this.temperatureHash.get(kv);
            HumiditySensor hs = 
                           (HumiditySensor)this.humidityHash.get(kv);
            //Need celsius for this calculation
            double temp = ts.getTemperatureCelsius();
            //Get the Device humidity for now
            double hum  = hs.getDeviceHumidity();
            //Only calculate if both humidity and temperature are
            //VIABLE (not returning the default answer)
            if(hum >= 0. && temp > -999.9){
               double gamma =((a*temp)/(b+temp)) + Math.log(hum*.01);
               dp = (b*gamma)/(a - gamma);
               //Now need convert to the appropriate units relative to
               //the temperature sensor
               if(this.units() == UNITS_ENGLISH){
                  dp = (dp * 1.8) + 32; //Convert to Fahrenheit
               }
               else if(this.units() == UNITS_ABSOLUTE){
                  dp = dp + 273.15;  //Convert to Kelvin
               }
               //Do not do anything for celsius, since dewpoint is
               //calculated in celsius, already
            }
            //Indicate an issue with one of the Sensors (the sensor
            //might not be null)
            else{ throw new NullPointerException(); }
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
            //Set to default value upon recognition of problem
            dp = -999.0;
         }
         finally{
            this.dewpointStack.push(new Double(dp));
            System.out.println(dp);
         }
      }
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
   private void calculateHeatIndex(){
      double hi = -999.9;  //Default Heat Index Value
      
      Enumeration e = this.temperatureHash.keys();
      while(e.hasMoreElements()){
         hi = -999.9;  //Default the Heat Index Value
         this.heatindexStack = new Stack();
         try{
            String kv = (String)e.nextElement(); //Key Value
            TemperatureSensor ts =
                     (TemperatureSensor)this.temperatureHash.get(kv);
            HumiditySensor hs =
                           (HumiditySensor)this.humidityHash.get(kv);
            //Get the temperature (get Fahrenheit for this calculation)
            double temp = ts.getTemperatureFahrenheit();
            //Get the device humidity for now
            double hum  = hs.getDeviceHumidity();
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
               if(this.units() == UNITS_METRIC){
                  hi = .5556 * (hi - 32);
               }
               else if(this.units() == UNITS_ABSOLUTE){
                  hi = (.5556 * (hi - 32)) + 273.15;
               }
               //Don't do anything for Fahrenheit, heat index already
               //Calculated in that
            }
            else if(hum >= 0. && temp < 70. && temp != -999.9){
               hi = -999.9;
            }
            else{
               throw new NullPointerException();
            }
         }
         catch(NullPointerException npe){
            npe.printStackTrace();
            hi = -999.9;
         }
         finally{
            this.heatindexStack.push(new Double(hi));
            System.out.println(hi);
         }
      }
   }

   /**
   Calculate the Wind Chill (This is TBD)
   */
   private void calculateWindChill(){
      //TBD
   }

   /**
   Request all the possible one-wire sensors from the PortSniffer
   */
   private void findSensorInformation(){
      PortSniffer ps = new PortSniffer(this.portType());
      Hashtable sensorhash = ps.findPorts();
      this.grabTemperatureSensors(sensorhash);
      this.grabHumiditySensors(sensorhash);
      this.grabBarometricSensors(sensorhash);
   }

   /**
   Free all the ports associated with all the sensors
   */
   private void freeAllSensorPorts(){
      Enumeration sensors = this.sensorStack.elements();
      while(sensors.hasMoreElements()){
         Sensor sensor = (Sensor)sensors.nextElement();
         sensor.freePort();
      }
   }

   /**
   Get the Current DataBase Table
   */
   private String getDBTableName(){
      return this.currentDBTable;
   }

   /**
   For all possible Barometric Sensors, grab the barometric data
   */
   private void grabBarometricData(){
      Enumeration e = this.sensorStack.elements();
      while(e.hasMoreElements()){
         Object o = e.nextElement();
         if(o instanceof BarometricSensor){
            try{
               ((BarometricSensor)o).calculateBarometricPressure();
               System.out.println((BarometricSensor)o);
            }
            catch(NullPointerException npe){
               npe.printStackTrace();
               this.freeAllSensorPorts();
               System.exit(1);
            }
         }
      }
   }

   /**
   Grab the current time in short format
   */
   private String grabCurrentTime(){
      Calendar cal = Calendar.getInstance();
      String returnString = String.format("%tT", cal.getTime());
      return returnString;
   }

   /**
   For all possible Humidity Sensors, grab the humidity data
   */
   private void grabHumidityData(){
      Enumeration e = this.sensorStack.elements();
      while(e.hasMoreElements()){
         Object o = e.nextElement();
         if(o instanceof HumiditySensor){
            try{
               ((HumiditySensor)o).calculateHumidity();
               System.out.println((HumiditySensor)o);
            }
            catch(NullPointerException npe){
               npe.printStackTrace();
               this.freeAllSensorPorts();
               System.exit(1);
            }
         }
      }
   }

   /**
   For all possible Temperature Sensors, grab the temperature data
   This is a new approach to how I want to set up the sensors.
   It makes the code more manageable and definately easier to
   understand.  Plus, it follows the K.I.S.S. priciple while
   still implementing the design.
   */
   private void grabTemperatureData(){
      Enumeration e = this.sensorStack.elements();
      while(e.hasMoreElements()){
         Object o = e.nextElement();
         if(o instanceof TemperatureSensor)
            try{
               ((TemperatureSensor)o).calcTemperature();
               System.out.println((TemperatureSensor)o);
            }
            catch(NullPointerException npe){
               npe.printStackTrace();
               this.freeAllSensorPorts();
               System.exit(1);
            }
      }
   }

   /**
   Grab the Barometric Sensor Information and instantiate the
   appropriate number of Barometric Sensors
   */
   private void grabBarometricSensors(Hashtable sensorHash){
      Sensor bar;
      Enumeration e = sensorHash.keys();
      while(e.hasMoreElements()){
         String portName = (String)e.nextElement();
         try{
            Stack stack = (Stack)sensorHash.get(portName);
            Enumeration data = stack.elements();
            while(data.hasMoreElements()){
               String name = (String)data.nextElement();
               if(name.equals("DS2438")){
                  String id = (String)data.nextElement();
                  if(id.startsWith("9200")){
                     if(this.portType() == USB_PORT){
                        bar = new BarometricSensor(
                                     portName,
                                     id,
                                     name,
                                     "Barometer",
                                     "DS9490R");
                     }
                     else{
                        bar = new BarometricSensor(
                                     portName,
                                     id,
                                     name,
                                     "Barometer");
                     }
                     bar.resetBus();
                     this.sensorStack.push(bar);
                     this.barometerHash.put(bar.getPortName(), bar);
                  }
               }
            }
         }
         catch(NullPointerException npe){ npe.printStackTrace(); }
      }
   }

   /**
   Grab the Humidity Sensor Information and instantiate the
   appropriate number of Humidity Sensors
   */
   private void grabHumiditySensors(Hashtable sensorHash){
      Sensor hs;
      Enumeration e = sensorHash.keys();
      while(e.hasMoreElements()){
         //Get the Port Name as the Key
         String portName = (String)e.nextElement();
         try{
            Stack stack = (Stack)sensorHash.get(portName);
            Enumeration data = stack.elements();
            while(data.hasMoreElements()){
               String name = (String)data.nextElement();
               if(name.equals("DS2438")){
                  String id = (String)data.nextElement();
                  if(id.startsWith("C3000")){
                     if(this.portType() == USB_PORT){
                        hs = new HumiditySensor(
                                    portName,
                                    id,
                                    name,
                                    "Hygrometer",
                                    "DS9490R");
                     }
                     else{
                        hs = new HumiditySensor(
                                    portName,
                                    id,
                                    name,
                                    "Hygrometer");
                     }
                     //Reset the bus for every Humidity Sensor
                     hs.resetBus();
                     this.sensorStack.push(hs);
                     this.humidityHash.put(hs.getPortName(), hs);
                  }
               }
            }
         }
         catch(NullPointerException npe){ npe.printStackTrace(); }
      }
   }

   /**
   Grab the Temperature Sensor Information and instantiate the
   appropriate number of Temperature Sensors
   */
   private void grabTemperatureSensors(Hashtable sensorHash){
      Sensor ts;
      //Get an Enumeration of the keys in the Hashtable
      Enumeration e = sensorHash.keys();
      while(e.hasMoreElements()){
         //Get the Port Name as the Key
         String portName = (String)e.nextElement();
         try{
            Stack stack = (Stack)sensorHash.get(portName);
            Enumeration data = stack.elements();
            while(data.hasMoreElements()){
               String name = (String)data.nextElement();
               if(name.equals("DS1920") || name.equals("DS18S20")){
                  String id = (String)data.nextElement();
                  if(this.portType() == USB_PORT){
                     ts = new TemperatureSensor(
                                 portName,
                                 id,
                                 name,
                                 "Thermometer",
                                 "DS9490R");
                  }
                  else{
                     ts = new TemperatureSensor(
                                 portName,
                                 id,
                                 name,
                                 "Thermometer");
                  }
                  //Reset the bus for every Temperature Sensor
                  ts.resetBus();
                  this.sensorStack.push(ts);
                  this.temperatureHash.put(ts.getPortName(), ts);
               }
            }
         }
         catch(NullPointerException npe){ npe.printStackTrace(); }
      }
   }

   /**
   Return the Quit indicator
   */
   private boolean quit(){
      return this.toQuit;
   }

   /**
   */
   private boolean setCurrentDate(){
      boolean isNewDate = false;
      Calendar cal      = Calendar.getInstance();
      //Now, set the date and compare
      String date = String.format("%tm", cal.getTime());
      date = date.concat("_");
      date = date.concat(String.format("%td", cal.getTime()));
      date = date.concat("_");
      date = date.concat(String.format("%tY", cal.getTime()));
      isNewDate = !(date.equals(this.currentDate));
      //Set the current date as appropriate
      if(isNewDate){
         this.currentDate = new String(date);
      }
      return isNewDate;
   }

   /**
   Save the current data to the database
   */
   private void saveToDataBase(){
      //If there is a new date, need to set a new table in the
      //database
      if(this.setCurrentDate()){
         //Set up the New Database Table name (based on the date)
         this.setDBTableName(this.currentDate);
         //Set up a new table in the Database
         this.setNewTable();
      }
      //First, grab the Time
      String time = this.grabCurrentTime();
      //Now set up the Database connections
      Connection connection = null;
      Statement  statement  = null;
      Enumeration sensors = this.sensorStack.elements();
      String useStmnt = new String("USE weatherdata; ");
      String exStmnt  = new String("INSERT into ");
      exStmnt = exStmnt.concat(this.getDBTableName());
      exStmnt = exStmnt.concat(" ( time , temp, tempunits, ");
      exStmnt = exStmnt.concat("humidity, pressure, presunits, ");
      exStmnt = exStmnt.concat("heatindex, hiunits, dewpoint, ");
      exStmnt = exStmnt.concat(" dpunits ) ");
      exStmnt = exStmnt.concat("values ( \'");
      exStmnt = exStmnt.concat(this.grabCurrentTime());
      exStmnt = exStmnt.concat("\' , ");
      while(sensors.hasMoreElements()){
         Sensor s = (Sensor)sensors.nextElement();
         if(s instanceof TemperatureSensor){
            String unitType = new String();
            double temp;
            int units = this.units();
            if(units == UNITS_ENGLISH){
               temp =
                   ((TemperatureSensor)s).getTemperatureFahrenheit();
               unitType = new String("F");
            }
            else if(units == UNITS_ABSOLUTE){
               temp = ((TemperatureSensor)s).getTemperatureKelvin();
               unitType = new String("K");
            }
            else{
               temp = ((TemperatureSensor)s).getTemperatureCelsius();
               unitType = new String("C");
            }
            exStmnt = exStmnt.concat(temp + " , ");
            exStmnt = exStmnt.concat("\'" + unitType + "\'" + " , ");
         }
         else if(s instanceof HumiditySensor){
            double humidity;
            //I think it is just best to get the device humidity,
            //regardless!!!
            humidity = ((HumiditySensor)s).getDeviceHumidity();
            exStmnt = exStmnt.concat(humidity + ", ");
         }
         else if(s instanceof BarometricSensor){
            String unitsType = new String();
            double pressure;
            int    units = this.units();
            if(units == UNITS_ENGLISH){
               pressure = ((BarometricSensor)s).getPressure();
               unitsType = new String("in Hg");
            }
            else if(units == UNITS_ABSOLUTE){
               pressure = ((BarometricSensor)s).getPressure_mb();
               unitsType = new String("mb");
            }
            //In metric units
            else{
               pressure = ((BarometricSensor)s).getPressure_mm();
               unitsType = new String("mm Hg");
            }
            exStmnt = exStmnt.concat(pressure + ", ");
            exStmnt = exStmnt.concat("\'" + unitsType + "\'" + ", ");
         }
      }
      //Now need to get the Heat Index and units
      //A temporary fix to a greater problem! (to be fixed later)
      Double hi = (Double)this.heatindexStack.peek();
      exStmnt = exStmnt.concat(hi + ", ");
      if(this.units() == UNITS_ENGLISH){
         exStmnt = exStmnt.concat(" \'F\' ,");
      }
      else if(this.units() == UNITS_ABSOLUTE){
         exStmnt = exStmnt.concat(" \'K\' ,");
      }
      else{
         exStmnt = exStmnt.concat(" \'C\' ,");
      }
      //Now need to get the Dew Point and units
      //A temporary fix to a greater problem! (to be fixed later)
      Double dp = (Double)this.dewpointStack.peek();
      exStmnt = exStmnt.concat(dp + ", ");
      if(this.units() == UNITS_ENGLISH){
         exStmnt = exStmnt.concat(" \'F\' ");
      }
      else if(this.units() == UNITS_ABSOLUTE){
         exStmnt = exStmnt.concat(" \'K\' ");
      }
      else{
         exStmnt = exStmnt.concat(" \'C\' ");
      }

      exStmnt = exStmnt.concat(" );");
      try{
         //Load the Database Driver Class
         Class.forName(JDBC_DRIVER);
         //Establish connection to the Database
         connection = DriverManager.getConnection(DB_URL,
                                                  DB_USER_PW,
                                                  DB_USER_PW);
         //Create the statement to query the database
         statement = connection.createStatement();
         //Execute the statements
         statement.execute(useStmnt);
         statement.execute(exStmnt);
      }
      catch(SQLException sqle){
         sqle.printStackTrace();
      }
      catch(ClassNotFoundException cnfe){
         cnfe.printStackTrace();
         this.freeAllSensorPorts();
         System.exit(1);
      }
      finally{
         try{
            statement.close();
            connection.close();
         }
         catch(Exception e){
            e.printStackTrace();
         }
      }
   }

   /**
   Set up a new table in the WeatherData Data Base
   */
   private void setNewTable(){
      Connection connection = null;
      Statement  statement  = null;

      String useStmnt = new String("USE weatherdata; ");
      String exStmnt = new String("CREATE TABLE ");
      exStmnt = exStmnt.concat(this.getDBTableName());
      exStmnt = exStmnt.concat(" ( input INT AUTO_INCREMENT, ");
      exStmnt = exStmnt.concat("time varchar(10), ");
      exStmnt = exStmnt.concat("temp float, ");
      exStmnt = exStmnt.concat("tempunits varchar(2), ");
      exStmnt = exStmnt.concat("humidity float, ");
      exStmnt = exStmnt.concat("pressure float, ");
      exStmnt = exStmnt.concat("presunits varchar(10), ");
      exStmnt = exStmnt.concat("heatindex float, ");
      exStmnt = exStmnt.concat("hiunits varchar(2), ");
      exStmnt = exStmnt.concat("dewpoint float, ");
      exStmnt = exStmnt.concat("dpunits varchar(2), ");
      exStmnt = exStmnt.concat("PRIMARY KEY (input) ) ;");
      
      
      //Now, go ahead and set up the new Database Table!
      try{
         //Load the Database Driver Class
         Class.forName(JDBC_DRIVER);
         //Establish connection to the Database
         connection = DriverManager.getConnection(DB_URL,
                                                  DB_USER_PW,
                                                  DB_USER_PW);
         //Create the statement to query database
         statement = connection.createStatement();

         //Now go ahead and execute the selected statements
         statement.execute(useStmnt);
         statement.execute(exStmnt);
      }
      catch(SQLException sqle){
         sqle.printStackTrace();
      }
      catch(ClassNotFoundException cnfe){
         cnfe.printStackTrace();
         this.freeAllSensorPorts();
         System.exit(1);
      }
      finally{
         try{
            statement.close();
            connection.close();
         }
         catch(Exception e){
            e.printStackTrace();
         }
      }
   }

   /**
   Set the Current DataBase Table Name
   */
   private void setDBTableName(String name){
      this.currentDBTable = new String(name);
   }

   /**
   Make a quick test print of the data
   */
   private void testPrintWeatherData(){
   }
}
