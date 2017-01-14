/********************************************************************
<GNU stuff goes here>
********************************************************************/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.text.Format;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.*;
import java.io.*;
import rosas.lou.weatherclasses.*;

public class Wunderground implements TemperatureObserver,
BarometerObserver, HumidityObserver, CalculatedObserver,
ExtremeObserver, Runnable{
   //Timeout in 5 seconds
   private final long TIMEOUT = 5000;
   //By convention, the Metric Data is stored in the first position
   //in the list
   private final int METRIC  = 0;
   //By convention, the English Data is stored in the second
   //position in the list
   private final int ENGLISH = 1;
   //By convention, the Absolute Data is stored in the third
   //position in the list
   private final int ABSOLUTE = 2;
   //
   private final String USERNAME = "KAZTUCSO647";
   //
   private final String PASSWORD = "7ku8hcyj";
   //
   private final String URL      = "weatherstation.wunderground.com";

 
   //State Enumeration
   private enum State{WAIT, RECEIVE, SEND}

   private State   state;
   private boolean dewpointUpdated;
   private boolean extremeUpdated;
   private boolean heatIndexUpdated;
   private boolean humidityUpdated;
   private boolean pressureUpdated;
   private boolean temperatureUpdated;
   private long    currentTime;
   private long    startTime;
   private double  currentTemperature;
   private WeatherEvent temp;
   private WeatherEvent humidity;
   private WeatherEvent pressure;
   private WeatherEvent heatIndex;
   private WeatherEvent dewPoint;
   private Socket socket;

   {
      state              = State.WAIT;
      dewpointUpdated    = false;
      extremeUpdated     = false;
      heatIndexUpdated   = false;
      humidityUpdated    = false;
      pressureUpdated    = false;
      temperatureUpdated = false;
      temp               = null;
      humidity           = null;
      pressure           = null;
      heatIndex          = null;
      dewPoint           = null;
      startTime          = 0;
      currentTime        = 0;
      socket             = null;
   }

   //Constructor
   public Wunderground(){
      this.reset();
   }

   //Public Methods

   //Interface Implementations
   //
   //Implementation of the Runnable interface
   //
   public void run(){
      Thread t = Thread.currentThread();
      while(true){
         if(this.state == State.RECEIVE){
            Calendar cal = Calendar.getInstance();
            this.currentTime = cal.getTimeInMillis();
            if(this.currentTime - this.startTime >= TIMEOUT){
               this.state = State.SEND;
            }
            else if(this.checkAllUpdates()){
               this.state = State.SEND;
            }
         }
         else if(this.state == State.SEND){
            StringBuffer message = this.setUpMessage();
            this.sendToWunderground(message.toString().trim());
            //The last thing you do is go back to the WAIT state
            this.reset();
         }
      }
   }

   //
   //Implementation of the BarometerObserver Interface
   //
   public void updatePressure(WeatherEvent event){}

   //
   //Implementation of the BarometerObserver Interface
   //
   public void updatePressure(WeatherStorage data){
      List<WeatherEvent> event = data.getLatestData("pressure");
      try{
         //Get the English Value
         this.pressure = event.get(ENGLISH);
         this.pressureUpdated = true;
         Calendar cal = Calendar.getInstance();
         this.startTime = cal.getTimeInMillis();
         if(this.state == State.WAIT){
            this.state = State.RECEIVE;
         }
      }
      catch(NullPointerException npe){
         this.pressure = null;
      }
   }

   //
   //Implementation of the CaclulatedObserver Interface
   //
   public void updateDewpoint(WeatherEvent event){}

   //
   //Implementation of the CaclulatedObserver Interface
   //
   public void updateDewpoint(WeatherStorage data){
      List<WeatherEvent> event = data.getLatestData("dewpoint");
      try{
         //Get the English Value
         this.dewPoint = event.get(ENGLISH);
         this.dewpointUpdated = true;
         Calendar cal = Calendar.getInstance();
         this.startTime = cal.getTimeInMillis();
         if(this.state == State.WAIT){
            this.state = State.RECEIVE;
         }
      }
      catch(NullPointerException npe){
         this.dewPoint = null;
      }
   }

   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateHeatIndex(WeatherEvent event){}
   
   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateHeatIndex(WeatherStorage data){
      List<WeatherEvent> event = data.getLatestData("heatindex");
      try{
         //Get the English Value
         this.heatIndex = event.get(ENGLISH);
         this.heatIndexUpdated = true;
         Calendar cal = Calendar.getInstance();
         this.startTime = cal.getTimeInMillis();
         if(this.state == State.WAIT){
            this.state = State.RECEIVE;
         }
      }
      catch(NullPointerException npe){
         this.heatIndex = null;
      }
   }

   //
   //Implementation of the CalculatedObserver Interface
   //
   public void updateWindChill(WeatherEvent event){}

   //
   //Implementation of the ExtremeObserver
   //
   public void updateExtremes(WeatherEvent event){}

   //
   //Implementation of the ExtremeObserver
   //
   public void updateExtremes(WeatherStorage data){
      List<WeatherEvent> max = data.getMax("Temperature");
      Iterator<WeatherEvent> it = max.iterator();
      while(it.hasNext()){
         WeatherEvent we = it.next();
      }
   }

   //
   //Implementation of the HumidityObserver Interface
   //
   public void updateHumidity(WeatherEvent event){}

   //
   //Implementation of the HumidityObserver Interface
   //
   public void updateHumidity(WeatherStorage data){
      List<WeatherEvent> event = data.getLatestData("humidity");
      try{
         Iterator<WeatherEvent> it = event.iterator();
         while(it.hasNext()){
            this.humidity = it.next();
         }
         this.humidityUpdated = true;
         Calendar cal = Calendar.getInstance();
         this.startTime = cal.getTimeInMillis();
         if(this.state == State.WAIT){
            this.state = State.RECEIVE;
         }
      }
      catch(NullPointerException npe){
         this.humidity = null;
      }
   }

   //
   //Implementation of the TemperatureObserver Interface
   //
   public void updateTemperature(WeatherEvent event){}
   
   //
   //Implementation of the TemperatureObserver Interface
   //
   public void updateTemperature(WeatherStorage data){
      List<WeatherEvent> event = data.getLatestData("temperature");
      try{
         //Get the English Value
         this.temp = event.get(ENGLISH);
         this.temperatureUpdated = true;
         Calendar cal = Calendar.getInstance();
         this.startTime = cal.getTimeInMillis();
         if(this.state == State.WAIT){
            this.state = State.RECEIVE;
         }
      }
      catch(NullPointerException npe){
         this.temp = null;
      }
   }

   //Private Methods
   //
   //Check to see if all the updates have come in
   //
   private boolean checkAllUpdates(){
      boolean isAllUpdated = true;
      isAllUpdated &= this.temperatureUpdated;
      isAllUpdated &= this.humidityUpdated;
      isAllUpdated &= this.pressureUpdated;
      isAllUpdated &= this.dewpointUpdated;
      isAllUpdated &= this.heatIndexUpdated;
      return isAllUpdated;
   }

   //
   //
   //
   private String findUTC(){
      String UTC = new String();
      SimpleDateFormat formatter =
             new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
      formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
      if(this.temp != null){
         Calendar cal = this.temp.getCalendar();
         Date date    = cal.getTime();
         UTC          = formatter.format(date);
      }
      else if(this.humidity != null){
         Calendar cal = this.humidity.getCalendar();
         Date date    = cal.getTime();
         UTC          = formatter.format(date);
      }
      else if(this.pressure != null){
         Calendar cal = this.pressure.getCalendar();
         Date date    = cal.getTime();
         UTC          = formatter.format(date);
      }
      UTC = UTC.replaceAll(":", "%3A");
      return UTC;
   }

   //
   //Reset the data fields as needed
   //
   private void reset(){
      this.dewpointUpdated    = false;
      this.extremeUpdated     = false;
      this.heatIndexUpdated   = false;
      this.humidityUpdated    = false;
      this.pressureUpdated    = false;
      this.temperatureUpdated = false;
      this.temp               = null;
      this.humidity           = null;
      this.pressure           = null;
      this.heatIndex          = null;
      this.dewPoint           = null;
      this.state              = State.WAIT;
   }

   //
   //
   //
   private void sendToWunderground(String message){
      System.out.println(message);
      final int PORT = 80;
      final int TIMES = 100;
      try{
         Socket socket = new Socket(this.URL, PORT);
         //Test Print
         System.out.println(socket);
         PrintStream out = new PrintStream(socket.getOutputStream());
         System.out.println("Sending:  " + message);
         out.print(message);
         BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                          socket.getInputStream()));
         int i = 0;
         while(i < TIMES){
            if(in.ready()){
               System.out.println(in.readLine());
            }
            try{ Thread.sleep(100); }
            catch(InterruptedException ie){}
            ++i;
         }
         socket.close();
      }
      catch(UnknownHostException uhe){
         System.out.println("Unable to connect to Wunderground");
         System.out.println(uhe);
      }
      catch(NullPointerException npe){
         System.out.println("Wunderground Socket Error:  " + npe);
      }
      catch(IOException ioe){
         System.out.println("Wunderground I/O Error:  " + ioe);
      }
   }

   //
   //
   //
   private StringBuffer setUpMessage(){
      StringBuffer message = new StringBuffer();

      message.append("GET https://" + this.URL);
      //message.append("https://" + URL);
      message.append("/weatherstation/updateweatherstation.php?");
      message.append("ID=" + this.USERNAME);
      message.append("&PASSWORD=" + this.PASSWORD);
      //message.append("&dateutc="+this.findUTC());
      message.append("&dateutc=now");
      if(this.temperatureUpdated){
         message.append("&tempf=" + this.temp.getValue());
      }
      if(this.humidityUpdated){
         message.append("&humidity=" + this.humidity.getValue());
      }
      if(this.pressureUpdated){
         message.append("&baromin=" + this.pressure.getValue());
      }
      //if(this.heatIndexUpdated){
      //   message.append("&heatidxf=" + this.heatIndex.getValue());
      //}
      if(this.dewpointUpdated){
         message.append("&dewptf=" + this.dewPoint.getValue());
      }
      message.append("&softwaretype=tws&action=updateraw ");
      //message.append("HTTP//1.1\r\nConnection: keep-alive\r\n\r\n");
      message.append("HTTP//1.1");
      return message;
   }
}
