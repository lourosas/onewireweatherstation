/**
* Copyright (C) 2011 Lou Rosas
* This file is part of many applications registered with
* the GNU General Public License as published
* by the Free Software Foundation; either version 3 of the License,
* or (at your option) any later version.
* IButton is distributed in the hope that it will be
* useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.
* If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.TestPanel2;

public class IButtonView  extends GenericJFrame
implements MemoryListener, MissionListener, LogListener{
   
   private static final short WIDTH  = 700;
   private static final short HEIGHT = 470;
   
   private enum State{GRAPH, DATA}
   
   private ButtonGroup itemGroup = null;
   //Temperature Low Alarm Label
   private JLabel tlal           = null;
   //Temperature High Alarm Label
   private JLabel thal           = null;
   //Temperature Low Alarm Text Field
   private JTextField  tlatf     = null;
   //Temperature High Alarm Text Field
   private JTextField  thatf     = null;
   //Sampling Rate Text Field
   private JTextField  srtf      = null;
   //Mission Start Delay Text Field
   private JTextField  msdltf    = null;
   //The JCheckBox Fields
   private JCheckBox   srtc      = null;
   private JCheckBox   er        = null;
   //The JComboBox Properties
   private JComboBox  tempComboBox = null;
   private JComboBox  humiComboBox = null;
   private JComboBox  dpComboBox   = null; //Dew Point Combo Box
   private JComboBox  hiComboBox   = null; //Heat Index Combo Box
   
   private IButton ib            = null;
   
   private String fromTemp            = null;
   private String toTemp              = null;
   private String tempSelectionString = null;
   private String humiSelectionString = null;
   private String dpSelectionString   = null;
   private String hiSelectionString   = null;
   
   //Make the time data global!
   //NEED TO GET RID OF BOTH OF THESE!!!
   private LinkedList<Date> tempTimeLog = null;
   private LinkedList<Date> humiTimeLog = null;
   //LogEvent Data Based on current temperature "state"
   //Celsius Temperature Data Event
   private LogEvent cTempEvent = null;
   //Fahrenheit Temperature Data Event
   private LogEvent fTempEvent = null;
   //Kelvin Temperature Data Event
   private LogEvent kTempEvent = null;
   //Humidity Data Event
   private LogEvent humiEvent  = null;
   //Celsius Dewpoint Data Event
   private LogEvent cDPEvent   = null;
   //Kelvin  Dewpoint Data Event
   private LogEvent kDPEvent   = null;
   //Fahrenheit Dewpoint Data Event
   private LogEvent fDPEvent   = null;
   //Celsius Heat Index Data Event
   private LogEvent cHIEvent   = null;
   //Kelvin Heat Index Data Event
   private LogEvent kHIEvent   = null;
   //Fahreheit Heat Index Event
   private LogEvent fHIEvent   = null;
   //Temperature Time Log Event
   private LogEvent tempTimeLogEvent     = null;
   //Humidity Time Log Event
   private LogEvent humidityTimeLogEvent = null;

   private Units temperatureUnits;
   private Units dewpointUnits;
   private Units heatIndexUnits;
   //Display States
   private State dewpointDisplayState;
   private State heatIndexDisplayState;
   private State humidityDisplayState;
   private State temperatureDisplayState;
   //Should the view display the current data, or not?
   private boolean toDisplay;
   //********************Constructor********************************
   /**
   Constructor of no arguments
   */
   public IButtonView(){
      this("", null);
   }
   
   /**
   Constructor taking the Title attribute and Controller Object
   */
   public IButtonView(String title, Object controller){
      super(title);
      this.humiTimeLog = new LinkedList<Date>();
      this.ib = new IButton();
      this.ib.addLogListener(this);
      this.ib.addMemoryListener(this);
      this.ib.addMissionListener(this);
      this.setUpGUI(controller);
   }
   
   //*******************Public Methods******************************
   /**
   */
   public void highTemperatureAlarmError(){
      String issue   = new String("Please Enter an Acutal Number");
      issue = issue.concat(" for the High Temperature Alarm");
      String display = new String("Input Error");
      String error   = new String("High Temperature Alarm Error");
      JOptionPane.showMessageDialog(this, issue, display,
                                         JOptionPane.ERROR_MESSAGE);
      this.thatf.requestFocus();
      this.thatf.selectAll();  
   }
   
   /**
   */
   public void lowTemperatureAlarmError(){
      String issue   = new String("Please Enter an Acutal Number");
      issue = issue.concat(" for the Low Temperature Alarm");
      String display = new String("Input Error");
      String error   = new String("Low Temperature Alarm Error");
      JOptionPane.showMessageDialog(this, issue, display,
                                         JOptionPane.ERROR_MESSAGE);
      this.tlatf.requestFocus();
      this.tlatf.selectAll();  
   }
   
   /**
   */
   public void missionStartDelayError(){
      String issue   = new String("Please Enter an Acutal Number");
      issue = issue.concat(" for the Mission Start Delay");
      String display = new String("Input Error");
      String error   = new String("Mission Start Delay Error");
      JOptionPane.showMessageDialog(this, issue, display,
                                         JOptionPane.ERROR_MESSAGE);
      this.msdltf.requestFocus();
      this.msdltf.selectAll();      
   }
   
   /**
   Implementation of the MemoryListener interface
   */
   public void onMemoryEvent(MemoryEvent event){
      String message = event.getMessage();
      if(message.split(" ")[0].equals("Memory") &&
         message.split(" ")[1].equals("Cleared")){
         String display = new String("Mission Cleared");
         JOptionPane.showMessageDialog(this, message, display,
                                   JOptionPane.INFORMATION_MESSAGE);
      }
      else{
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);         
      }
   }
   
   /**
   Implementation of the MissionListener interface
   */
   public void onMissionEvent(MissionEvent event){
      String message = event.getMessage();
      if(message.split(" ")[0].equals("Mission") &&
         message.split(" ")[1].equals("Started")){
         String display = new String("Mission Started");
         JOptionPane.showMessageDialog(this, message, display,
                                   JOptionPane.INFORMATION_MESSAGE);
      }
      //More to be put in here, as the System is Developed
      else if(message.split(" ")[0].equals("Mission") &&
              message.split(" ")[1].equals("Stopped")){
         String display = new String("Mission Stopped");
         JOptionPane.showMessageDialog(this, message, display,
                                   JOptionPane.INFORMATION_MESSAGE);
      }
      else{
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /**
   Implementation of the LogListener interface:
   Dewpoint Logs
   */
   public void onDewpointLogEvent(LogEvent event){
      String message = event.getMessage();
      if(!(message.split(" ")[0].equals("Dew"))  ||
         !(message.split(" ")[1].equals("Point")) ||
         !(message.split(" ")[2].equals("Calculated"))){
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      else{
         if(event.getUnits() == Units.METRIC)
            this.cDPEvent = event;
         else if(event.getUnits() == Units.ENGLISH)
            this.fDPEvent = event;
         else
            this.kDPEvent = event;
         //Since there is no separate time event for the dew point
         //(connected to temperature), set up the days PRIOR to
         //setting up the dew point data display
         //Go ahead and populate the Dew Point JComboBox
         this.setUpDifferentDaysInDPComboBox();
         if(this.dewpointUnits == event.getUnits())
            this.prepDewPointDataDisplay();
      }
   }
   
   /**
   Implementation of the LogListener interface:
   extreme temperature data
   */
   public void onExtremeTemperatureLogEvent
   (
      LogEvent temp,
      LogEvent time
   ){
   }
   
   /**
   Implementation of the LogListener interface:
   Heat Index Logs
   */
   public void onHeatIndexLogEvent(LogEvent event){
      String message = event.getMessage();
      if(!(message.split(" ")[0].equals("Heat"))  ||
         !(message.split(" ")[1].equals("Index")) ||
         !(message.split(" ")[2].equals("Calculated"))){
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      else{
         if(event.getUnits() == Units.METRIC)
            this.cHIEvent = event;
         else if(event.getUnits() == Units.ENGLISH)
            this.fHIEvent = event;
         else
            this.kHIEvent = event;
         //Since there is no separate time event for the heat index
         //(connected to temperature), set up the days PRIOR to
         //setting up the dew point data display
         //Go ahead and populate the Dew Point JComboBox
         this.setUpDifferentDaysInHIComboBox();
         if(this.heatIndexUnits == event.getUnits()){
            this.prepHeatIndexDataDisplay();
         }
      }
   }
   
   /**
   Implementation of the LogListener interface:
   Humidity Logs
   */
   public void onHumidityLogEvent(LogEvent event){
      String message = event.getMessage();
      if(!(message.split(" ")[0].equals("Humidity")) ||
         !(message.split(" ")[1].equals("Log"))){
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      else{
         this.humiEvent = event;
         this.prepHumidityDataDisplay();
      }
   }
   
   /**
   Implementation of the LogListener Interface for both
   the Humidity and Time Logs
   */
   public void onHumidityLogEvent
   (
      LogEvent humidity,
      LogEvent time
   ){
      //TBD:  need to figure out how to take advantage of this
   }
   
   /**
   Implementation of the LogListener interface:
   Humidity Time Logs
   */
   public void onHumidityTimeLogEvent(LogEvent event){
      String message = event.getMessage();
      if(!(message.split(" ")[0].equals("Humidity")) ||
         !(message.split(" ")[1].equals("Time"))){
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      else{
         //The event is "valid", so go ahead and populate the
         //Log data associated with it...
         this.setUpHumidityTimeData(event);
         //Also, go ahead and populate the different days in the
         //Humidity JComboBox
         this.setUpDifferentDaysInHumiComboBox();
      }
   }
   
   /**
   Implementation of the LogListener interface:
   Temperature Logs
   */
   public void onTemperatureLogEvent(LogEvent event){
      String message = event.getMessage();
      //This would indicate an error (bigtime)
      if(!(message.split(" ")[0].equals("Temperature")) ||
         !(message.split(" ")[1].equals("Log"))){
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      else{
         if(event.getUnits() == Units.METRIC)
            this.cTempEvent = event;
         else if(event.getUnits() == Units.ENGLISH)
            this.fTempEvent = event;
         else
            this.kTempEvent = event;
         if(this.temperatureUnits == event.getUnits())
            this.prepTemperatureDataDisplay();
      }
   }
   
   /**
   Implementation of the LogListener Interface for both the
   Temperature and Time Logs
   */
   public void onTemperatureLogEvent
   (
      LogEvent temp,
      LogEvent time
   ){
      //TBD:  need to figure out how to take advantage of this
   }
   
   /**
   Implementation of the LogListener interface:
   Temperature Time Logs
   */
   public void onTemperatureTimeLogEvent(LogEvent event){
      String message = event.getMessage();
      //This would indicate an error (bigtime)
      if(!(message.split(" ")[0].equals("Temperature")) ||
         !(message.split(" ")[1].equals("Time"))){
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      else{
         //If the event is "Valid", go ahead and populate the
         //Time Log data
         this.setUpTemperatureTimeData(event);
         //Also, go ahead and populate the different days in the
         //Temperature JComboBox
         this.setUpDifferentDaysInTempComboBox();
      }
   }
   
   /*
   */
   public String requestSamplingRate(){
      return this.srtf.getText();
   }
   
   /*
   */
   public String requestMissionStartDelay(){
      return this.msdltf.getText();
   }
   
   /*
   */
   public String requestTemperatureLowAlarm(){
      return this.tlatf.getText();
   }
   
   /*
   */
   public String requestTemperatureHighAlarm(){
      return this.thatf.getText();
   }
   
   /*
   */
   public void resetNewMissionDefaults(){
      Enumeration e = this.itemGroup.getElements();
      while(e.hasMoreElements()){
         Object o = e.nextElement();
         if(o instanceof JRadioButton){
            JRadioButton b = (JRadioButton)o;
            if(b.getActionCommand().equals("NMCelsius")){
               b.setSelected(true);
               b.doClick();
            }
         }
      }
      //Clear out the Check Box Values, as well and alert the
      //listeners
      //Synchronize Real Time Clock
      this.srtc.setSelected(false);
      //Enable Rollover
      this.er.setSelected(false);
      this.thatf.setText(""); this.tlatf.setText("");
      this.srtf.setText("");  this.msdltf.setText("");
   }
   
   /*
   */
   public void samplingRateError(){
      String issue   = new String("Please Enter an Acutal Number");
      issue = issue.concat(" for the Sampling Rate");
      String display = new String("Input Error");
      String error   = new String("Sampling Rate Error");
      JOptionPane.showMessageDialog(this, issue, display,
                                         JOptionPane.ERROR_MESSAGE);
      this.srtf.requestFocus();
      this.srtf.selectAll();
   }
   
   /**
   Set whether or not the current weather data should be displayed
   or just be "refreshed"
   */
   public void setToDisplay(boolean display){
      this.toDisplay = display;
   }
   
   //*******************Private Methods*****************************
   /*
   */
   private void convertTemperatures(ItemEvent e, AbstractButton ab){
      String lowAlarmString  = null;
      String highAlarmString = null;
      double lowAlarm        = Double.NaN;
      double highAlarm       = Double.NaN;
      if(e.getStateChange() == ItemEvent.DESELECTED){
         this.fromTemp = ab.getActionCommand();
      }
      else if(e.getStateChange() == ItemEvent.SELECTED){
         this.toTemp = ab.getActionCommand();
      }
      if(!this.fromTemp.equals(this.toTemp)){
         //Low Alarm Issues
         try{
            lowAlarmString = this.tlatf.getText();
            lowAlarm =
                  this.setConvert(lowAlarmString, fromTemp, toTemp);
            lowAlarmString =
                        String.format("%.2f", new Double(lowAlarm));
            this.tlatf.setText(lowAlarmString);
         }
         //In this case either the toTemp, fromTemp, or both will
         //be null, and that will be OK, since the Low Alarm Text
         //Field will be populated, with an appropriate value
         //(as specified)
         //A null for the Low Alarm Text field will bennifit as
         //well, since nothing will be set in the text field,
         //possibly indicating something is wrong.
         catch(NullPointerException npe){}
         catch(NumberFormatException nfe){ this.tlatf.setText(""); }
         //High Alarm Issues
         try{
            highAlarmString = this.thatf.getText();
            highAlarm =
                 this.setConvert(highAlarmString, fromTemp, toTemp);
            highAlarmString =
                       String.format("%.2f", new Double(highAlarm));
            this.thatf.setText(highAlarmString);
         }
         //In this case either the toTemp, fromTemp, or both will 
         //be null, and that will be OK, since the High Alarm Text
         //Field will be populated, with an appropriate value
         //(as specified)
         //A null for the High Alarm Text field will benefit as
         //well, since nothing will be set in the text field,
         //possibly indicating something is wrong.
         catch(NullPointerException npe){}
         catch(NumberFormatException nfe){ this.thatf.setText(""); }
      }
   }
   
   /**
   */
   private boolean getToDisplay(){
      return this.toDisplay;
   }
   
   /**
   Given the Specific Time Log, and a specific date, get all
   the calendar data associated with that date
   */
   private LinkedList<Integer> getDayIndeces
   (
      LinkedList<Date> timeLog,
      String requestedDate
   ){
      LinkedList<Integer> indeces = new LinkedList<Integer>();
      if(requestedDate.equals("All Days")){
         indeces.add(new Integer(0));
         indeces.add(new Integer(timeLog.size() - 1));
      }
      else{
         Iterator i = timeLog.iterator();
         while(i.hasNext()){
            Date date = (Date)i.next();
            String currentDate =
                          DateFormat.getDateInstance().format(date);
            if(currentDate.equals(requestedDate)){
               indeces.add(new Integer(timeLog.indexOf(date)));
            }
         }
      }
      return indeces;
   }
   
   /**
   Given the Specific Time Log, get the days
   */
   private LinkedList<String> getTheDays(LinkedList<Date> timeLog){
      LinkedList<String> returnStrings = new LinkedList<String>();
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
            returnStrings.add(currentDate);
         }
      }
      return returnStrings;
   }
   
   /**
   Go ahead and "prep" the humidity data for display.
   Then, go ahead and message the appropriate method to set up the
   dew point data.
   */
   private void prepDewPointDataDisplay(){
      LogEvent event = null;
      if(this.dewpointUnits == Units.METRIC)
         event = this.cDPEvent;
      else if(this.dewpointUnits == Units.ENGLISH)
         event = this.fDPEvent;
      else
         event = this.kDPEvent;
      if(this.dewpointDisplayState == State.GRAPH)
         this.setUpDewpointGraph(event);
      else
         this.setUpDewpointData();
   }
   
   /**
   Go ahead and "prep" the heat index data for display.
   Then, go ahead and message the appropriate method to set up the
   heat index data
   */
   private void prepHeatIndexDataDisplay(){
      LogEvent event = null;
      if(this.heatIndexUnits == Units.METRIC)
         event = this.cHIEvent;
      else if(this.heatIndexUnits == Units.ENGLISH)
         event = this.fHIEvent;
      else
         event = this.kHIEvent;
      if(this.heatIndexDisplayState == State.GRAPH)
         this.setUpHeatIndexGraph(event);
      else
         this.setUpHeatIndexData();
   }
   
   /**
   Go ahead and "prep" the humidity data for display.
   Then, go ahead and message the appropriate method to set up the
   humidity data
   */
   private void prepHumidityDataDisplay(){
      LogEvent event = null;
      if(this.humidityDisplayState == State.GRAPH)
         this.setUpHumidityGraph(this.humiEvent);
      else
         this.setUpHumidityData();
   }
   
   /**
   Go ahead and "prep" the temperature data for display.
   Then, go ahead and message the appropriate method to set up the
   temperature data.
   */
   private void prepTemperatureDataDisplay(){
      LogEvent event = null;
      if(this.temperatureUnits == Units.METRIC)
         event = this.cTempEvent;
      else if(this.temperatureUnits == Units.ENGLISH)
         event = this.fTempEvent;
      else
         event = this.kTempEvent;
      if(this.temperatureDisplayState == State.GRAPH)
         this.setUpTemperatureGraph(event);
      else
         this.setUpTemperatureData();
   }
   
   /*
   */
   private double setConvert(String alarm, String from, String to)
   throws NumberFormatException{
      double convert = Double.NaN;  
      convert = Double.parseDouble(alarm);

      if(from.equals("NMCelsius") && to.equals("NMFahrenheit")){
         convert = WeatherConvert.celsiusToFahrenheit(convert);
      }
      else if(from.equals("NMCelsius") && to.equals("NMKelvin")){
         convert = WeatherConvert.celsiusToKelvin(convert);
      }
      else if(from.equals("NMFahrenheit")&&to.equals("NMCelsius")){
         convert = WeatherConvert.fahrenheitToCelsius(convert);
      }
      else if(from.equals("NMFahrenheit") && to.equals("NMKelvin")){
         convert = WeatherConvert.fahrenheitToKelvin(convert);
      }
      else if(from.equals("NMKelvin") && to.equals("NMCelsius")){
         convert = WeatherConvert.kelvinToCelsius(convert);
      }
      else if(from.equals("NMKelvin") && to.equals("NMFahrenheit")){
         convert = WeatherConvert.kelvinToFahrenheit(convert);
      }
      else{
         throw new NumberFormatException();
      }
      return convert;
   }
   
   /*
   */
   private JPanel setDewpointPanel(Object controller){
      JPanel dewpointPanel = new JPanel();
      dewpointPanel.setLayout(new BorderLayout());
      JPanel northPanel = this.setDewpointNorthPanel(controller);
      JPanel centerPanel= this.setDewpointCenterPanel(controller);
      JPanel southPanel = this.setDewpointSouthPanel(controller);
      
      dewpointPanel.add(northPanel,  BorderLayout.NORTH);
      dewpointPanel.add(centerPanel, BorderLayout.CENTER);
      dewpointPanel.add(southPanel,  BorderLayout.SOUTH);
      return dewpointPanel;
   }
   
   /*
   */
   private JPanel setDewpointCenterPanel(Object controller){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }
   
   /*
   */
   private JPanel setDewpointNorthPanel(Object controller){
      JPanel northPanel      = new JPanel();
      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup dataGroup  = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JRadioButton celsius = new JRadioButton("Celsius", true);
      unitsGroup.add(celsius);
      this.setUpLocalDPTabItemListener(celsius);
      this.dewpointUnits = Units.METRIC;
      northPanel.add(celsius);
      
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      unitsGroup.add(fahrenheit);
      this.setUpLocalDPTabItemListener(fahrenheit);
      northPanel.add(fahrenheit);
      
      JRadioButton kelvin = new JRadioButton("Kelvin");
      unitsGroup.add(kelvin);
      this.setUpLocalDPTabItemListener(kelvin);
      northPanel.add(kelvin);
      
      JRadioButton graph = new JRadioButton("Graph", true);
      this.dewpointDisplayState = State.GRAPH;
      dataGroup.add(graph);
      this.setUpLocalDPTabItemListener(graph);
      northPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data");
      dataGroup.add(data);
      this.setUpLocalDPTabItemListener(data);
      northPanel.add(data);
      
      //Initialize the data
      String dates[] = {"All Days"};
      this.dpSelectionString = new String(dates[0]);
      this.dpComboBox = new JComboBox(dates);
      this.dpComboBox.setName("Dewpoint");
      this.dpComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDay(dpComboBox);
         }
      });
      northPanel.add(this.dpComboBox);
      return northPanel;
   }
   
   /*
   */
   private JPanel setDewpointSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      IButtonController ibc = new IButtonController(this.ib, this);
      
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      //Dew Point Refresh
      refresh.setActionCommand("DP Refresh");
      refresh.addActionListener(ibc);
      refresh.addKeyListener(ibc);
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Dew Point Data");
      save.addActionListener(ibc);
      save.addKeyListener(ibc);
      southPanel.add(save);
      
      return southPanel;
   }
   
   /*
   */
   private JPanel setHeatIndexPanel(Object controller){
      JPanel heatIndexPanel = new JPanel();
      heatIndexPanel.setLayout(new BorderLayout());
      JPanel northPanel = this.setHeatIndexNorthPanel(controller);
      JPanel centerPanel=this.setHeatIndexCenterPanel(controller);
      JPanel southPanel = this.setHeatIndexSouthPanel(controller);
      
      heatIndexPanel.add(northPanel,  BorderLayout.NORTH);
      heatIndexPanel.add(centerPanel, BorderLayout.CENTER);
      heatIndexPanel.add(southPanel,  BorderLayout.SOUTH);
      return heatIndexPanel;
   }
   
   /*
   */
   private JPanel setHeatIndexCenterPanel(Object controller){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }
   
   /*
   */
   private JPanel setHeatIndexNorthPanel(Object controller){
      JPanel northPanel      = new JPanel();
      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup dataGroup  = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JRadioButton celsius = new JRadioButton("Celsius", true);
      unitsGroup.add(celsius);
      this.setUpLocalHI_TabItemListener(celsius);
      this.heatIndexUnits = Units.METRIC;
      northPanel.add(celsius);
      
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      unitsGroup.add(fahrenheit);
      this.setUpLocalHI_TabItemListener(fahrenheit);
      northPanel.add(fahrenheit);
      
      JRadioButton kelvin = new JRadioButton("Kelvin");
      unitsGroup.add(kelvin);
      this.setUpLocalHI_TabItemListener(kelvin);
      northPanel.add(kelvin);
      
      JRadioButton graph = new JRadioButton("Graph", true);
      this.heatIndexDisplayState = State.GRAPH;
      dataGroup.add(graph);
      this.setUpLocalHI_TabItemListener(graph);
      northPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data");
      dataGroup.add(data);
      this.setUpLocalHI_TabItemListener(data);
      northPanel.add(data);
      //Initialize the data
      String dates[] = {"All Days"};
      this.hiSelectionString = new String(dates[0]);
      
      this.hiComboBox = new JComboBox(dates);
      this.hiComboBox.setName("Heat Index");
      this.hiComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDay(hiComboBox);
         }
      });
      northPanel.add(this.hiComboBox);
      
      return northPanel;
   }
   
   /*
   */
   private JPanel setHeatIndexSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      IButtonController ibc = new IButtonController(this.ib, this);
      
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      //Heat Index Refresh
      refresh.setActionCommand("HI Refresh");
      refresh.addActionListener(ibc);
      refresh.addKeyListener(ibc);
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Heat Index Data");
      save.addActionListener(ibc);
      save.addKeyListener(ibc);
      southPanel.add(save);
      
      return southPanel;
   }
   
   /*
   */
   private JPanel setHumidityPanel(Object controller){
      JPanel humidityPanel = new JPanel();
      humidityPanel.setLayout(new BorderLayout());
      JPanel northPanel = this.setHumidityNorthPanel(controller);
      JPanel centerPanel= this.setHumidityCenterPanel(controller);
      JPanel southPanel = this.setHumiditySouthPanel(controller);
      
      humidityPanel.add(northPanel,  BorderLayout.NORTH);
      humidityPanel.add(centerPanel, BorderLayout.CENTER);
      humidityPanel.add(southPanel,  BorderLayout.SOUTH);
      return humidityPanel;
   }
   
   /*
   */
   private JPanel setHumidityCenterPanel(Object controller){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }

   /*
   */
   private JPanel setHumidityNorthPanel(Object controller){
      JPanel northPanel     = new JPanel();
      ButtonGroup dataGroup = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JRadioButton graph = new JRadioButton("Graph", true);
      this.humidityDisplayState = State.GRAPH;
      dataGroup.add(graph);
      this.setUpLocalHumidityTabItemListener(graph);
      northPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data");
      dataGroup.add(data);
      this.setUpLocalHumidityTabItemListener(data);
      northPanel.add(data);
      
      //Humidity Combo Box data
      String dates[] = {"All Days"};
      this.humiSelectionString = new String(dates[0]);
      this.humiComboBox        = new JComboBox(dates);
      this.humiComboBox.setName("Humidity");
      this.humiComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDay(humiComboBox);
         }
      });
      northPanel.add(this.humiComboBox);
      
      return northPanel;
   }
   
   /*
   */
   private JPanel setHumiditySouthPanel(Object controller){
      IButtonController ibc = new IButtonController(this.ib, this);
      JPanel southPanel = new JPanel();
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Humidity Refresh");
      refresh.addActionListener(ibc);
      refresh.addKeyListener(ibc);
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Humidity Data");
      save.addActionListener(ibc);
      save.addKeyListener(ibc);
      southPanel.add(save);
      return southPanel;
   }
   
   /*
   */
   private JPanel setMissionButtonPanel(Object controller){
      //controller object currently not used
      //Button Panel
      JPanel buttonPanel = new JPanel();
      buttonPanel.setBorder(BorderFactory.createTitledBorder(
                                 BorderFactory.createEtchedBorder(),
                                 "Command")
      );
      JButton start = new JButton("Start New Mission");
      buttonPanel.add(start);
      start.addActionListener(new IButtonController(this.ib, this));
      start.addKeyListener(new IButtonController(this.ib, this));
      //start.addActionListener((ActionListener)controller);
      //start.addKeyListener((KeyListener)controller);
      JButton clear = new JButton("Clear Mission");
      buttonPanel.add(clear);
      //clear.addActionListener((ActionListener)controller);
      clear.addActionListener(new IButtonController(this.ib, this));
      clear.addKeyListener(new IButtonController(this.ib, this));
      JButton stop = new JButton("Stop Mission");
      buttonPanel.add(stop);
      //stop.addActionListener((ActionListener)controller);
      stop.addActionListener(new IButtonController(this.ib, this));
      stop.addKeyListener(new IButtonController(this.ib, this));
      JButton refresh = new JButton("Refresh Mission Data");
      buttonPanel.add(refresh);
      //refresh.addActionListener((ActionListener)controller);
      refresh.addActionListener(
                               new IButtonController(this.ib,this));
      refresh.addKeyListener(new IButtonController(this.ib, this));
      JButton save = new JButton("Save Mission Data");
      buttonPanel.add(save);
      save.addActionListener(new IButtonController(this.ib, this));
      save.addKeyListener(new IButtonController(this.ib, this));
      
      return buttonPanel;
   }
   
   /*
   */
   private JPanel setMissionPanel(Object controller){
      JPanel missionPanel = new JPanel();
      missionPanel.setLayout(new BorderLayout());
      
      missionPanel.add(this.setNewMissionPanel(controller),
                                               BorderLayout.CENTER);
      missionPanel.add(this.setMissionButtonPanel(controller),
                                                BorderLayout.SOUTH);

      return missionPanel;
   }
   
   /*
   */
   private JPanel setNewMissionPanel(Object controller){
      //controller object currently NOT USED!
      //New Mission Panel
      this.itemGroup         = new ButtonGroup();
      JPanel newMissionPanel = new JPanel();
      newMissionPanel.setLayout(new BorderLayout());
      newMissionPanel.setBorder(
                                 BorderFactory.createTitledBorder(
                                 BorderFactory.createEtchedBorder(),
                                 "New Mission Settings")
      );
      //Radio Panel
      JPanel radioPanel = new JPanel();
      JRadioButton celsius = new JRadioButton("Celsius", true);
      //New Mission Celsius
      celsius.setActionCommand("NMCelsius");
      this.setUpLocalMissionTabItemListener(celsius);
      this.itemGroup.add(celsius);
      celsius.addItemListener(new IButtonController(this.ib));
      //celsius.addItemListener((ItemListener)controller));
      radioPanel.add(celsius);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      //New Mission Fahrenheit
      fahrenheit.setActionCommand("NMFahrenheit");
      this.setUpLocalMissionTabItemListener(fahrenheit);
      fahrenheit.addItemListener(new IButtonController(this.ib));
      //fahrenheit.addItemListener((ItemListener)controller);
      this.itemGroup.add(fahrenheit);
      radioPanel.add(fahrenheit);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      //New Mission Kelvin
      kelvin.setActionCommand("NMKelvin");
      kelvin.addItemListener(new IButtonController(this.ib));
      //kelvin.addItemListener((ItemListener)controller);
      this.setUpLocalMissionTabItemListener(kelvin);
      this.itemGroup.add(kelvin);
      radioPanel.add(kelvin);
      newMissionPanel.add(radioPanel, BorderLayout.NORTH);
      
      //Center Panel
      JPanel centerPanel = new JPanel();
      GroupLayout centerLayout = new GroupLayout(centerPanel);
      centerPanel.setLayout(centerLayout);
      //Synchronize Real Time Clock
      this.srtc = new JCheckBox("Synchronize Real Time Clock?");
      this.srtc.addItemListener(new IButtonController(this.ib));
      //Enable Rollover
      this.er = new JCheckBox("Enable Rollover?");
      this.er.addItemListener(new IButtonController(this.ib));
      //Sampling Rate Label
      JLabel srl = new JLabel("Sampling Rate (in minutes)",
                                              SwingConstants.RIGHT);
      //Mission Start Delay Label
      JLabel msdl = new JLabel("Mission Start Delay?",
                                              SwingConstants.RIGHT);
      //Temperature Low Alarm Label
      this.tlal = new JLabel("Temperature Low Alarm? (\u00B0C)",
                                              SwingConstants.RIGHT);
      //Temperature High Alarm Label
      this.thal= new JLabel("Temperature High Alarm? (\u00B0C)",
                                              SwingConstants.RIGHT);

      //Sampling Rate Text Field
      this.srtf   = new JTextField(4);
      //Mission Start Delay Text Field
      this.msdltf = new JTextField(4);
      //Temperature Low Alarm Text Field
      this.tlatf  = new JTextField(4);
      //Temperature High Alarm Text Field
      this.thatf  = new JTextField(4);
      centerLayout.setHorizontalGroup(
         centerLayout.createParallelGroup(
                                   GroupLayout.Alignment.LEADING)
         .addGroup(centerLayout.createSequentialGroup()
             .addGroup(centerLayout.createParallelGroup(
                            GroupLayout.Alignment.LEADING, false)
                 .addGroup(centerLayout.createSequentialGroup()
                     .addGap(33, 33, 33)
                     .addComponent(srtc))
                 .addGroup(centerLayout.createSequentialGroup()
                     .addContainerGap()
                     .addGroup(centerLayout.createParallelGroup(
                                     GroupLayout.Alignment.TRAILING)
                         .addComponent(msdl)
                         .addComponent(srl))
                     .addPreferredGap(
                             LayoutStyle.ComponentPlacement.RELATED)
                     .addGroup(centerLayout.createParallelGroup(
                                      GroupLayout.Alignment.LEADING)
                         .addComponent(this.msdltf,
                                          GroupLayout.DEFAULT_SIZE,
                                          59,
                                          Short.MAX_VALUE)
                         .addComponent(this.srtf, 0, 0,
                                                 Short.MAX_VALUE))))
             .addGap(33, 33, 33)
             .addGroup(centerLayout.createParallelGroup(
                               GroupLayout.Alignment.LEADING, false)
                 .addGroup(centerLayout.createParallelGroup(
                                      GroupLayout.Alignment.LEADING)
                     .addComponent(er)
                     .addGroup(GroupLayout.Alignment.TRAILING,
                                centerLayout.createSequentialGroup()
                         .addComponent(tlal)
                         .addPreferredGap(
                             LayoutStyle.ComponentPlacement.RELATED)
                         .addComponent(this.tlatf,
                                         GroupLayout.PREFERRED_SIZE,
                                           GroupLayout.DEFAULT_SIZE,
                                       GroupLayout.PREFERRED_SIZE)))
                 .addGroup(centerLayout.createSequentialGroup()
                     .addComponent(thal)
                     .addPreferredGap(
                             LayoutStyle.ComponentPlacement.RELATED,
                                           GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE)
                     .addComponent(this.thatf,
                                         GroupLayout.PREFERRED_SIZE,
                                           GroupLayout.DEFAULT_SIZE,
                                       GroupLayout.PREFERRED_SIZE)))
             .addGap(20, 20, 20))
      );
      centerLayout.setVerticalGroup(
         centerLayout.createParallelGroup(
                                      GroupLayout.Alignment.LEADING)
         .addGroup(centerLayout.createSequentialGroup()
             .addContainerGap()
             .addGroup(centerLayout.createParallelGroup(
                                     GroupLayout.Alignment.BASELINE)
                 .addComponent(srtc)
                 .addComponent(er))
             .addGap(20, 20, 20)
             .addGroup(centerLayout.createParallelGroup(
                                     GroupLayout.Alignment.BASELINE)
                 .addComponent(srl)
                 .addComponent(this.srtf,
                               GroupLayout.PREFERRED_SIZE,
                               GroupLayout.DEFAULT_SIZE,
                               GroupLayout.PREFERRED_SIZE)
                 .addComponent(this.tlatf,
                               GroupLayout.PREFERRED_SIZE,
                               GroupLayout.DEFAULT_SIZE,
                               GroupLayout.PREFERRED_SIZE)
                 .addComponent(tlal))
             .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
             .addGroup(centerLayout.createParallelGroup(
                                      GroupLayout.Alignment.BASELINE)
                 .addComponent(msdl)
                 .addComponent(this.msdltf,
                                       GroupLayout.PREFERRED_SIZE,
                                       GroupLayout.DEFAULT_SIZE,
                                       GroupLayout.PREFERRED_SIZE)
                 .addComponent(thal)
                 .addComponent(this.thatf,
                                       GroupLayout.PREFERRED_SIZE,
                                         GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
             .addContainerGap(120, Short.MAX_VALUE))
      );
      newMissionPanel.add(centerPanel, BorderLayout.CENTER);
      
      return newMissionPanel;
   }
   
   /*
   */
   private JPanel setTemperaturePanel(Object controller){
      JPanel temperaturePanel = new JPanel();
      temperaturePanel.setLayout(new BorderLayout());
      
      temperaturePanel.add(this.setTempNorthPanel(controller),
                                                BorderLayout.NORTH);
      temperaturePanel.add(this.setTempCenterPanel(),
                                               BorderLayout.CENTER);
      temperaturePanel.add(this.setTempSouthPanel(controller),
                                                BorderLayout.SOUTH);
      return temperaturePanel;
   }
   
   /**
   No need for the Object Controller in this case, since this
   is only viewing the data...
   */
   private JPanel setTempCenterPanel(){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }
   
   /*
   */
   private JPanel setTempNorthPanel(Object controller){
      JPanel northPanel            = new JPanel();
      ButtonGroup temperatureGroup = new ButtonGroup();
      ButtonGroup dataGroup        = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      //Set up the temperature panel
      JPanel tempPanel = new JPanel();
      
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("TCelsius");
      temperatureGroup.add(celsius);
      this.setUpLocalTempTabItemListener(celsius);
      this.temperatureUnits = Units.METRIC;
      tempPanel.add(celsius);
      
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("TFahrenheit");
      temperatureGroup.add(fahrenheit);
      this.setUpLocalTempTabItemListener(fahrenheit);
      tempPanel.add(fahrenheit);
      
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("TKelvin");
      temperatureGroup.add(kelvin);
      this.setUpLocalTempTabItemListener(kelvin);
      tempPanel.add(kelvin);
      
      northPanel.add(tempPanel);
      
      JPanel dataPanel = new JPanel();
      
      JRadioButton graph = new JRadioButton("Graph", true);
      this.temperatureDisplayState = State.GRAPH;
      dataGroup.add(graph);
      this.setUpLocalTempTabItemListener(graph);
      dataPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data");
      dataGroup.add(data);
      this.setUpLocalTempTabItemListener(data);
      dataPanel.add(data);
      
      northPanel.add(dataPanel);
      //Initialize the data
      String dates[] = {"All Days"};
      this.tempSelectionString = new String("All Days");
      
      this.tempComboBox = new JComboBox(dates);
      this.tempComboBox.setName("Temperature");
      this.tempComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDay(tempComboBox);
         }
      });
      northPanel.add(this.tempComboBox);
      return northPanel;
   }
   
   /*
   */
   private JPanel setTempSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Temp Refresh");
      refresh.addActionListener(
                               new IButtonController(this.ib,this));
      refresh.addKeyListener(new IButtonController(this.ib, this));
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Temperature Data");
      save.addActionListener(new IButtonController(this.ib, this));
      save.addKeyListener(new IButtonController(this.ib, this));
      southPanel.add(save);
      
      return southPanel;
   }
   
   /**
   Set up the data to display
   */
   private void setTheDay(Object comboBox){
      if(comboBox instanceof JComboBox){
         JComboBox jcbox = (JComboBox)comboBox;
         String selected = (String)jcbox.getSelectedItem();
         if(jcbox.getName().equals("Temperature")){
            //Esentially, a "State String" for the temperature
            //Selection:  to be used for both graphing and
            //textual representation of the data...
            this.tempSelectionString = new String(selected);
            this.prepTemperatureDataDisplay();
         }
         else if(jcbox.getName().equals("Humidity")){
            this.humiSelectionString = new String(selected);
            this.prepHumidityDataDisplay();
         }
         else if(jcbox.getName().equals("Dewpoint")){
            this.dpSelectionString = new String(selected);
            this.prepDewPointDataDisplay();
         }
         else if(jcbox.getName().equals("Heat Index")){
            this.hiSelectionString = new String(selected);
            this.prepHeatIndexDataDisplay();
         }
      }
   }
   
   /**
   Display the Dew Point data in the GUI...
   */
   private void setUpDewpointData(){
      int dpTab      = -1;
      int missionTab = -1;
      String delimeter;
      double min, max;
      try{
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int j = 0; j < jtp.getTabCount(); j++){
            if(jtp.getTitleAt(j).equals("Dew Point")){
               dpTab = j;
            }
            else if(jtp.getTitleAt(j).equals("Mission")){
               missionTab = j;
            }
         }
         jtp.setSelectedIndex(dpTab);
         JPanel dpPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)dpPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll();
         drawPanel.setLayout(new BorderLayout());
         JScrollPane jsp = this.setUpDewpointText();
         drawPanel.add(jsp, BorderLayout.CENTER);
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(dpTab);
      }
      catch(NullPointerException npe){
         //npe.printStackTrace();
         String message = new String("No Dew Point Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /**
   Get the "appropriate event", put the data in a JTextArea,
   put the JTextArea in a JScrollPane, return...In this case,
   this is the Dewpoint Data
   */
   private JScrollPane setUpDewpointText(){
      String delimeter;
      double min, max;
      JScrollPane dpScrollPane = null;
      try{
         LogEvent event = null;
         if(this.dewpointUnits == Units.METRIC){
            event = this.cDPEvent;
            delimeter = new String("\u00B0C");
         }
         else if(this.dewpointUnits == Units.ENGLISH){
            event = this.fDPEvent;
            delimeter = new String("\u00B0F");
         }
         else{
            event = this.kDPEvent;
            delimeter = new String("K");
         }
         JTextArea dpTextArea = new JTextArea(28, 35);
         dpTextArea.setEditable(false);
         LinkedList list  = new LinkedList(event.getDataList());
         LinkedList tlist =
                new LinkedList(this.tempTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                  this.getDayIndeces(tlist, this.dpSelectionString);
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         tlist= new LinkedList(tlist.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         Iterator i = list.iterator();
         Iterator t = tlist.iterator();
         while(i.hasNext()){
            String date = new String((Date)t.next() + ", ");
            String data = String.format("%.2f", (Double)i.next());
            String out = new String(date);
            out = out.concat(data + " " + delimeter + "\n");
            dpTextArea.append(out);
         }
         min = event.getMin();
         max = event.getMax();
         String mins = String.format("%.2f", new Double(min));
         String maxs = String.format("%.2f", new Double(max));
         dpTextArea.append("Min:  " + mins + delimeter + "\n");
         dpTextArea.append("Max:  " + maxs + delimeter + "\n");
         dpScrollPane = new JScrollPane(dpTextArea);
      }
      catch(NullPointerException npe){}
      catch(IndexOutOfBoundsException ibe){
         String message = new String("Please Hit the 'Refresh'");
         message = message.concat(" Button");
         String display = new String("Hit Refresh");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      finally{
         return dpScrollPane;
      }
   }
   
   /**
   For the Dew Point JComboBox, go ahead and set-up the different
   days that contain dewpoint data.  This is to allow the user to
   select specific daily dew point data, as well as viewing ALL the
   data (every data point recorded from ALL the days).
   PLEASE NOTE:  there is NO dpTimeLogEvent-->not needed since the
   dew point is a combination of the temperature and humidity.
   Hence, I used the tempTimeLogEvent:  since that is what is
   recorded from the dew point.
   */
   private void setUpDifferentDaysInDPComboBox(){
      //Get the Date Listing for all the Temperature Data
      //This will be used in for the Dew Point data, as well
      LinkedList<Date> timeLog =
                new LinkedList(this.tempTimeLogEvent.getDataList());
      //Get all the separate days from the Temperature Time Log
      LinkedList<String> days = this.getTheDays(timeLog);
      Iterator i = days.iterator();
      while(i.hasNext()){
         boolean alreadyThere = false;
         String date = (String)i.next();
         for(int j = 0; j < this.dpComboBox.getItemCount(); j++){
            String val = (String)this.dpComboBox.getItemAt(j);
            if(date.equals(val)){
               j = this.tempComboBox.getItemCount();
               alreadyThere = true;
            }
         }
         if(!alreadyThere){ this.dpComboBox.addItem(date); }
      }
   }
   
   /**
   For the Heat Index JComboBox, go ahead and set-up the different
   days that contain dewpoint data.  This is to allow the user to
   select specific daily dew point data, as well as viewing ALL the
   data (every data point recorded from ALL the days).
   PLEASE NOTE:  there is NO hiTimeLogEvent-->not needed since the
   heat index is a combination of the temperature and humidity.
   Hence, I used the tempTimeLogEvent:  since that is what is
   recorded from the dew point.
   */
   private void setUpDifferentDaysInHIComboBox(){
      //Get the Date Listing for all the Temperature Data
      //This will be used for the Heat Index Data, as well
      LinkedList<Date> timeLog =
                new LinkedList(this.tempTimeLogEvent.getDataList());
      //Get all the separate days from the Temperature Time Log
      LinkedList<String> days = this.getTheDays(timeLog);
      Iterator i = days.iterator();
      while(i.hasNext()){
         boolean alreadyThere = false;
         String date = (String)i.next();
         for(int j = 0; j < this.hiComboBox.getItemCount(); j++){
            String val = (String)this.hiComboBox.getItemAt(j);
            if(date.equals(val)){
               j = this.hiComboBox.getItemCount();
               alreadyThere = true;
            }
         }
         if(!alreadyThere){ this.hiComboBox.addItem(date); }
      }
   }
   
   /**
   For the Humidity JComboBox, go ahead and set-up the different
   days that contain humidity data.  This is to allow the user to 
   select specific daily humidity data, as well as view ALL the data
   (every data point recorded from ALL the days)
   */
   private void setUpDifferentDaysInHumiComboBox(){
      //Get the Date Listing for all the Humidity Data
      LinkedList<Date> humiLog =
            new LinkedList(this.humidityTimeLogEvent.getDataList());
      //Get all the separate days from the Humidity Time Log
      LinkedList<String> days = this.getTheDays(humiLog);
      Iterator i = days.iterator();
      while(i.hasNext()){
         boolean alreadyThere = false;
         String date = (String)i.next();
         for(int j = 0; j < this.humiComboBox.getItemCount(); j++){
            String val = (String)this.humiComboBox.getItemAt(j);
            if(date.equals(val)){
               j = this.humiComboBox.getItemCount();
               alreadyThere = true;
            }
         }
         if(!alreadyThere){ this.humiComboBox.addItem(date); }
      }
   }
   
   /**
   For the Temperature JComboBox, go ahead and set-up the different
   days that contain temperature data.  This is to allow the user to
   select specific daily temperature data, as well as viewing ALL
   the data (every data point recorded from ALL the days)
   */
   private void setUpDifferentDaysInTempComboBox(){
      //Get the Date Listing for all the Temperature Data
      LinkedList<Date> timeLog= 
                new LinkedList(this.tempTimeLogEvent.getDataList());
      
      //Get all the separate days from the Temperature Time Log
      LinkedList<String> days = this.getTheDays(timeLog);
      Iterator i = days.iterator();
      while(i.hasNext()){
         boolean alreadyThere = false;
         String date = (String)i.next();
         for(int j = 0; j < this.tempComboBox.getItemCount(); j++){
            String val = (String)this.tempComboBox.getItemAt(j);
            if(date.equals(val)){
               j = this.tempComboBox.getItemCount();
               alreadyThere = true;
            }
         }
         if(!alreadyThere){ this.tempComboBox.addItem(date); }
      }
   }
   
   /**
   */
   private void setUpDewpointGraph(LogEvent event){
      int dpTab      = -1;
      int missionTab = -1;
      try{
         LinkedList list = new LinkedList(event.getDataList());
         //Go ahead and use the Temperature Log Event...
         LinkedList<Date> date =
                new LinkedList(this.tempTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                   this.getDayIndeces(date, this.dpSelectionString);
         date = new LinkedList(date.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int j = 0; j < jtp.getTabCount(); j++){
            if(jtp.getTitleAt(j).equals("Dew Point")){
               dpTab = j;
            }
            else if(jtp.getTitleAt(j).equals("Mission")){
               missionTab = j;
            }
         }
         jtp.setSelectedIndex(dpTab);
         JPanel dpPanel = (JPanel)jtp.getSelectedComponent();
         GenericJInteractionFrame dpFrame = 
                          new GenericJInteractionFrame("Dew Point");
         //Super-Impose the Temperature Time onto the dew point
         TestPanel2 tp = new TestPanel2(list,
                                        event.getMin(),
                                        event.getMax(),
                                        date);
         dpFrame.setSize(500, 500);
         dpFrame.setResizable(false);
         dpFrame.add(tp);
         dpFrame.setVisible(true);
         //Essentially, get the middle component, which is the
         //Center component to put the graph...
         JPanel drawPanel = (JPanel)dpPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll();
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new TestPanel2(list,
                                      event.getMin(),
                                      event.getMax(),
                                      date),
                                      BorderLayout.CENTER);
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(dpTab);
      }
      catch(NullPointerException npe){
         String message = new String("No Dew Point Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      //In response to the issues associated with the Temperature
      //Time Log Event updates...want to see if by catching the
      //exception, I can get the this panel to continue to work.
      //Because the indeces are based on the time event.
      catch(IndexOutOfBoundsException ibe){
         String message = new String("Please Hit the 'Refresh'");
         message = message.concat(" Button");
         String display = new String("Hit Refresh!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /*
   */
   private void setUpHeatIndexData(){
      int hiTab      = -1;
      int missionTab = -1;
      try{
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int j = 0; j < jtp.getTabCount(); j++){
            if(jtp.getTitleAt(j).equals("Heat Index")){
               hiTab = j;
            }
            else if(jtp.getTitleAt(j).equals("Mission")){
               missionTab = j;
            }
         }
         jtp.setSelectedIndex(hiTab);
         JPanel dpPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)dpPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll();
         drawPanel.setLayout(new BorderLayout());
         JScrollPane jsp = this.setUpHeatIndexText();
         drawPanel.add(jsp, BorderLayout.CENTER);
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(hiTab);
      }
      catch(NullPointerException npe){
         String message = new String("No Heat Index Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /**
   Get the "appropriate event", put the data in a JTextArea,
   put the JTextArea in a JScrollPane, return...In this case,
   this is the Dewpoint Data
   */
   private JScrollPane setUpHeatIndexText(){
      String delimeter;
      double min, max;
      //This should actually be the hiScrollPane!
      JScrollPane hiScrollPane = null;
      try{
         LogEvent event = null;
         if(this.heatIndexUnits == Units.METRIC){
            event = this.cHIEvent;
            delimeter = new String("\u00B0C");
         }
         else if(this.heatIndexUnits == Units.ENGLISH){
            event = this.fHIEvent;
            delimeter = new String("\u00B0F");
         }
         else{
            event = this.kHIEvent;
            delimeter = new String("K");
         }
         JTextArea hiTextArea = new JTextArea(28, 35);
         hiTextArea.setEditable(false);
         LinkedList list = new LinkedList(event.getDataList());
         LinkedList tlist =
                new LinkedList(this.tempTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                  this.getDayIndeces(tlist, this.hiSelectionString);
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         tlist= new LinkedList(tlist.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         Iterator i = list.iterator();
         Iterator t = tlist.iterator();
         while(i.hasNext()){
            String date = new String((Date)t.next() + ", ");
            String data = String.format("%.2f", (Double)i.next());
            String out  = new String(date);
            out = out.concat(data + " " + delimeter + "\n");
            hiTextArea.append(out);
         }
         min = event.getMin();
         max = event.getMax();
         String mins = String.format("%.2f", new Double(min));
         String maxs = String.format("%.2f", new Double(max));
         hiTextArea.append("Min:  " + mins + delimeter + "\n");
         hiTextArea.append("Max:  " + maxs + delimeter + "\n");
         hiScrollPane = new JScrollPane(hiTextArea);
      }
      catch(NullPointerException npe){}
      catch(IndexOutOfBoundsException ibe){
         String message = new String("Please Hit the 'Refresh'");
         message = message.concat(" Button");
         String display = new String("Hit Refresh!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      finally{
         return hiScrollPane;
      }
   }
   
   /**
   */
   private void setUpHeatIndexGraph(LogEvent event){
      int hiTab      = -1;
      int missionTab = -1;
      try{
         LinkedList list = new LinkedList(event.getDataList());
         //Go ahead and use the Temperature Log Event...
         LinkedList<Date> date =
                new LinkedList(this.tempTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                   this.getDayIndeces(date, this.hiSelectionString);
         date = new LinkedList(date.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int j = 0; j < jtp.getTabCount(); j++){
            if(jtp.getTitleAt(j).equals("Heat Index")){
               hiTab = j;
            }
            else if(jtp.getTitleAt(j).equals("Mission")){
               missionTab = j;
            }
         }
         jtp.setSelectedIndex(hiTab);
         JPanel hiPanel = (JPanel)jtp.getSelectedComponent();
         GenericJInteractionFrame hiFrame = 
                         new GenericJInteractionFrame("Heat Index");
         //Super-Impose the Temperature Time onto the heat index
         TestPanel2 tp = new TestPanel2(list,
                                        event.getMin(),
                                        event.getMax(),
                                        date);
         hiFrame.setSize(500, 500);
         hiFrame.setResizable(false);
         hiFrame.add(tp);
         hiFrame.setVisible(true);
         //Essentially, get the middle component, which is the
         //Center component to put the graph...
         JPanel drawPanel = (JPanel)hiPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll();
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new TestPanel2(list,
                                      event.getMin(),
                                      event.getMax(),
                                      date),
                                      BorderLayout.CENTER);
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(hiTab);
      }
      catch(NullPointerException npe){
         String message = new String("No Heat Index Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
      //In response to the issues associated with the Temperature
      //Time Log Event updates...want to see if by catching the
      //exception, I can get the this panel to continue to work.
      //Because the indeces are based on the time event.
      catch(IndexOutOfBoundsException ibe){
         String message = new String("Please Hit the 'Refresh'");
         message = message.concat(" Button");
         String display = new String("Hit Refresh!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /*
   */
   private void setUpLocalDPTabItemListener(JToggleButton jtb){
      jtb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               if(ab.isSelected()){
                  //Since displaying the data, stay on the
                  //current panel...
                  setToDisplay(true);
                  if(ab.getActionCommand().equals("Celsius"))
                     dewpointUnits = Units.METRIC;
                  else if
                  (
                     ab.getActionCommand().equals("Fahrenheit")
                  )
                     dewpointUnits = Units.ENGLISH;
                  else if(ab.getActionCommand().equals("Kelvin"))
                     dewpointUnits = Units.ABSOLUTE;
                  else if(ab.getActionCommand().equals("Graph"))
                     dewpointDisplayState = State.GRAPH;
                  else if(ab.getActionCommand().equals("Data"))
                     dewpointDisplayState = State.DATA;
                  prepDewPointDataDisplay();
               }
            }
         }
      });
   }
   
   /*
   */
   private void setUpLocalHI_TabItemListener(JToggleButton jtb){
      jtb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               if(ab.isSelected()){
                  //Since displaying the data, stay on the
                  //current panel...
                  setToDisplay(true);
                  if(ab.getActionCommand().equals("Celsius"))
                     heatIndexUnits = Units.METRIC;
                  else if
                  (
                     ab.getActionCommand().equals("Fahrenheit")
                  )
                     heatIndexUnits = Units.ENGLISH;
                  else if(ab.getActionCommand().equals("Kelvin"))
                     heatIndexUnits = Units.ABSOLUTE;
                  else if(ab.getActionCommand().equals("Graph"))
                     heatIndexDisplayState = State.GRAPH;
                  else if(ab.getActionCommand().equals("Data"))
                     heatIndexDisplayState = State.DATA;
                  prepHeatIndexDataDisplay();
               }
            }
         }
      });
   }

   /**
   This will need to be attended to at the time the data display
   is developed
   */
   private void setUpLocalHumidityTabItemListener(JToggleButton jtb){
      jtb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               if(ab.isSelected()){
                  setToDisplay(true);
                  if(ab.getActionCommand().equals("Graph")){
                     humidityDisplayState = State.GRAPH;
                  }
                  //Not Graph, it is data...
                  else{
                     humidityDisplayState = State.DATA;
                  }
                  prepHumidityDataDisplay();
               }
            }
         }
      });
   }

   /*
   */
   private void setUpLocalMissionTabItemListener(JToggleButton jtb){
      jtb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               convertTemperatures(e, ab);
               if(ab.isSelected()){
                  if(ab.getActionCommand().equals("NMCelsius")){
                     tlal.setText(
                                "Temperature Low Alarm? (\u00B0C)");
                     thal.setText(
                               "Temperature High Alarm? (\u00B0C)");
                  }
                  else if(ab.getActionCommand().
                                            equals("NMFahrenheit")){
                     tlal.setText(
                                "Temperature Low Alarm? (\u00B0F)");
                     thal.setText(
                               "Temperature High Alarm? (\u00B0F)");
                  
                  }
                  else if(ab.getActionCommand().equals("NMKelvin")){
                     tlal.setText("Temperature Low Alarm? (K)");
                     thal.setText("Temperature High Alarm? (K)");
                  }
               }
            }
         }
      });
   }
   
   /*
   */
   private void setUpLocalTempTabItemListener(JToggleButton jtb){
      jtb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               if(ab.isSelected()){
                  //Since displaying the data, stay on the
                  //current panel...
                  setToDisplay(true);
                  if(ab.getActionCommand().equals("TCelsius"))
                     temperatureUnits = Units.METRIC;
                  else if
                  (
                     ab.getActionCommand().equals("TFahrenheit")
                  )
                     temperatureUnits = Units.ENGLISH;
                  else if(ab.getActionCommand().equals("TKelvin"))
                     temperatureUnits = Units.ABSOLUTE;
                  else if(ab.getActionCommand().equals("Graph"))
                     temperatureDisplayState = State.GRAPH;
                  else if(ab.getActionCommand().equals("Data"))
                     temperatureDisplayState = State.DATA;
                  prepTemperatureDataDisplay();
               }
            }
         }
      });
   }
   
   /*
   Set up the GUI
   */
   private void setUpGUI(Object controller){
      try{
         //UIManager.setLookAndFeel(
         //         "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
         UIManager.setLookAndFeel(
                       UIManager.getSystemLookAndFeelClassName());
         //UIManager.setLookAndFeel(
         //              "javax.swing.plaf.metal.MetalLookAndFeel");
         this.setSize(WIDTH, HEIGHT);
         this.setResizable(false);
         JTabbedPane jtp = new JTabbedPane();
         jtp.addTab("Mission",
                    null,
                    this.setMissionPanel(controller),
                    "Setting/Clearing Mission Data");
         jtp.addTab("Temperature",
                    null,
                    this.setTemperaturePanel(controller),
                    "Viewing Temperature Mission Data");
         jtp.addTab("Humidity",
                    null,
                    this.setHumidityPanel(controller),
                    "Viewing Humidity Mission Data");
         jtp.addTab("Dew Point",
                    null,
                    this.setDewpointPanel(controller),
                    "Viewing Dew Point Mission Data");
         jtp.addTab("Heat Index",
                    null,
                    this.setHeatIndexPanel(controller),
                    "Viewing Heat Index Mission Data");
         this.getContentPane().add(jtp);
         //this.pack();
         this.setVisible(true);
      }
      catch(Exception e){ e.printStackTrace(); }
   }
   
   /**
   Display the Humidity data in the GUI...
   */
   private void setUpHumidityData(){
      int humidityTab = -1;
      int missionTab  = -1;
      String delimeter;
      double min, max;
      try{
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int j = 0; j < jtp.getTabCount(); j++){
            if(jtp.getTitleAt(j).equals("Humidity")){
               humidityTab = j;
            }
            else if(jtp.getTitleAt(j).equals("Mission")){
               missionTab = j;
            }
         }
         jtp.setSelectedIndex(humidityTab);
         JPanel humidityPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)humidityPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll();
         drawPanel.setLayout(new BorderLayout());
         JScrollPane jsp = this.setUpHumidityText();
         drawPanel.add(jsp, BorderLayout.CENTER);
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(humidityTab);
      }
      catch(NullPointerException npe){
         String message = new String("No Humidity Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /**
   Get the "appropriate event", put the data in a JTextArea,
   put the JTextArea in a JScrollPane, return...In this case,
   this is the Dewpoint Data
   */
   private JScrollPane setUpHumidityText(){
      double min, max;
      JScrollPane humidityScrollPane = null;
      try{
         JTextArea humidityTextArea = new JTextArea(28, 35);
         humidityTextArea.setEditable(false);
         LinkedList list =
                       new LinkedList(this.humiEvent.getDataList());
         LinkedList tlist =
            new LinkedList(this.humidityTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                this.getDayIndeces(tlist, this.humiSelectionString);
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         tlist = new LinkedList(tlist.subList(indeces.getFirst(),
                                              indeces.getLast()+1));
         Iterator i = list.iterator();
         Iterator t = tlist.iterator();
         while(i.hasNext()){
            String date = new String((Date)t.next() + ", ");
            String data = String.format("%.2f", (Double)i.next());
            String out = new String(date);
            out = out.concat(data + "%\n");
            humidityTextArea.append(out);
         }
         min = this.humiEvent.getMin();
         max = this.humiEvent.getMax();
         String mins = String.format("%.2f", new Double(min));
         String maxs = String.format("%.2f", new Double(max));
         humidityTextArea.append("Min:  " + mins + "%\n");
         humidityTextArea.append("Max:  " + maxs+ "%\n");
         humidityScrollPane = new JScrollPane(humidityTextArea);
      }
      catch(NullPointerException npe){
      }
      finally{
         return humidityScrollPane;
      }
   }

   /*
   */
   private void setUpHumidityGraph(LogEvent event){
      try{
         int humidityTab = -1;
         int missionTab  = -1;
         LinkedList list = new LinkedList(event.getDataList());
         LinkedList<Date> date =
            new LinkedList(this.humidityTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                this.getDayIndeces(date, this.humiSelectionString);
         date = new LinkedList(date.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).equals("Humidity"))
               humidityTab = i;
            else if(jtp.getTitleAt(i).equals("Mission"))
               missionTab  = i;
         }
         jtp.setSelectedIndex(humidityTab);
         JPanel humiPanel = (JPanel)jtp.getSelectedComponent();
         GenericJInteractionFrame testFrame =
                           new GenericJInteractionFrame("Humidity");
         TestPanel2 tp = new TestPanel2(list,
                                        event.getMin(),
                                        event.getMax(),
                                        date);
         testFrame.setSize(500,500);
         testFrame.setResizable(false);
         testFrame.add(tp);
         testFrame.setVisible(true);
         //Essentially, get the middle component, which is the
         //Center component to put the graph...
         JPanel drawPanel = (JPanel)humiPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll(); //Remove all as needed 
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new TestPanel2(list,
                                      event.getMin(),
                                      event.getMax(),
                                      date),
                                      BorderLayout.CENTER);
         System.out.println(drawPanel.getComponentCount());
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(humidityTab);
      }
      catch(NullPointerException npe){
         String message = new String("No Humidity Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }

   /*
   */
   private void setUpHumidityTimeData(LogEvent event){
      try{
         this.humidityTimeLogEvent = event;
      }
      catch(NullPointerException npe){
         //Clear out the Humidity Time Log Linked List
         this.humiTimeLog.clear();
         String message = new String("No Humidity Time Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /*
   */
   private void setUpTemperatureData(){
      int tempTab    = -1;
      int missionTab = -1;
      try{
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int j = 0; j < jtp.getTabCount(); j++){
            if(jtp.getTitleAt(j).equals("Temperature")){
               tempTab = j;
            }
            else if(jtp.getTitleAt(j).equals("Mission")){
               missionTab = j;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         //Essentially, get the middle component, which is the
         //Center component to put the graph...
         JPanel drawPanel = (JPanel)tempPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll(); //Remove all as needed 
         drawPanel.setLayout(new BorderLayout());
         JScrollPane jsp = this.setUpTemperatureText();
         drawPanel.add(jsp, BorderLayout.CENTER);
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(tempTab);
      }
      catch(NullPointerException npe){
         String message = new String("No Temperature Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /**
   Given a list of entries (which will usually correspond to a given
   set of dates) set up the temperature data that corresponds to
   those given indices
   */
   private void setUpTemperatureData(LinkedList<Integer> list){
      try{
         int first = (Integer)list.getFirst().intValue();
         int last  = (Integer)list.getLast().intValue();
         LinkedList<Date> dates =
                new LinkedList(this.tempTimeLogEvent.getDataList());
         LinkedList<Date> currentDates =
                         new LinkedList(dates.subList(first, last));
         LogEvent event = null;
         if(this.temperatureUnits == Units.METRIC){
            event = this.cTempEvent;
         }
         else if(this.temperatureUnits == Units.ENGLISH){
            event = this.fTempEvent;
         }
         else{
            event = this.kTempEvent;
         }
         LinkedList<Double> temp =
                                new LinkedList(event.getDataList());
         temp = new LinkedList(temp.subList(first, last));
      }
      catch(NullPointerException npe){}
   }
   
   /**
   Get the "appropriate event", put the data in a JTextArea,
   put the JTextArea in a JScrollPane, return...In this case,
   this is the Dewpoint Data
   */
   private JScrollPane setUpTemperatureText(){
      String delimeter;
      double min, max;
      JScrollPane tempScrollPane = null;
      try{
         LogEvent event = null;
         if(this.temperatureUnits == Units.METRIC){
            event     = this.cTempEvent;
            delimeter = new String("\u00B0C");
         }
         else if(this.temperatureUnits == Units.ENGLISH){
            event     = this.fTempEvent;
            delimeter = new String("\u00B0F");
         }
         else{
            event     = this.kTempEvent;
            delimeter = new String("K");            
         }
         JTextArea tempTextArea = new JTextArea(28, 35);
         tempTextArea.setEditable(false);
         LinkedList list  = new LinkedList(event.getDataList());
         //This is more TBD than anything else...need to figure
         //out how to set up the particular day...solution
         //a "stop gap" for now...
         LinkedList tlist =
                new LinkedList(this.tempTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                this.getDayIndeces(tlist, this.tempSelectionString);
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         tlist = new LinkedList(tlist.subList(indeces.getFirst(),
                                              indeces.getLast()+1));
         Iterator i = list.iterator();
         Iterator t = tlist.iterator();
         while(i.hasNext()){
            String date = new String((Date)t.next() + ", ");
            String data = String.format("%.2f", (Double)i.next());
            String out = new String(date);
            out = out.concat(data + " " + delimeter + "\n");
            tempTextArea.append(out);
         }
         min = event.getMin();
         max = event.getMax();
         String mins = String.format("%.2f", new Double(min));
         String maxs = String.format("%.2f", new Double(max));
         tempTextArea.append("Min:  " + mins + delimeter + "\n");
         tempTextArea.append("Max:  " + maxs + delimeter + "\n");
         tempScrollPane = new JScrollPane(tempTextArea);
      }
      catch(NullPointerException npe){}
      finally{
         return tempScrollPane;
      }
   }
   
   /*
   */
   private void setUpTemperatureGraph(LogEvent event){
      try{
         int tempTab    = -1;
         int missionTab = -1;
         LinkedList list = new LinkedList(event.getDataList());
         LinkedList<Date> date =
                new LinkedList(this.tempTimeLogEvent.getDataList());
         LinkedList<Integer> indeces =
                 this.getDayIndeces(date, this.tempSelectionString);
         date = new LinkedList(date.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         list = new LinkedList(list.subList(indeces.getFirst(),
                                            indeces.getLast() + 1));
         JTabbedPane jtp =
                 (JTabbedPane)this.getContentPane().getComponent(0);
         for(int j = 0; j < jtp.getTabCount(); j++){
            if(jtp.getTitleAt(j).equals("Temperature")){
               tempTab = j;
            }
            else if(jtp.getTitleAt(j).equals("Mission")){
               missionTab = j;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         //Will need to change
         GenericJInteractionFrame testFrame =
                        new GenericJInteractionFrame("Temperature");
         TestPanel2 tp = new TestPanel2(list,
                                        event.getMin(),
                                        event.getMax(),
                                        date);
         testFrame.setSize(500, 500);
         testFrame.setResizable(false);
         testFrame.add(tp);
         testFrame.setVisible(true);
         //Essentially, get the middle component, which is the
         //Center component to put the graph...
         JPanel drawPanel = (JPanel)tempPanel.getComponent(1);
         if(drawPanel.getComponentCount() > 0)
            drawPanel.removeAll(); //Remove all as needed 
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new TestPanel2(list,
                                      event.getMin(),
                                      event.getMax(),
                                      date),
                                      BorderLayout.CENTER);
         System.out.println(drawPanel.getComponentCount());
         jtp.setSelectedIndex(missionTab);
         if(this.getToDisplay())
            jtp.setSelectedIndex(tempTab);
         //Eventually, get rid of the line above, and do this
         //line below
         //drawPanel.add(tp, BorderLayout.CENTER);
      }
      catch(NullPointerException npe){
         String message = new String("No Temperature Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /*
   Set up the timeLog LinkedList for the new time data
   to be used in every data graph as the time reference
   */
   private void setUpTemperatureTimeData(LogEvent event){
      try{
         this.tempTimeLogEvent = event;
      }
      catch(NullPointerException npe){
         //Clear out the Temperature Time Log Linked List
         String message = new String("No Temperature Time Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /*
   */
   private void setUpTemperatureTimeGraph(LogEvent event){
      try{
         LinkedList list = new LinkedList(event.getDataList());
         Iterator i = list.iterator();
         Calendar c = Calendar.getInstance();
         while(i.hasNext()){
            c.setTime((Date)i.next());
            System.out.print(c.get(Calendar.MONTH) + 1 + "/");
            System.out.println(c.get(Calendar.DAY_OF_MONTH));
            //System.out.println((Date)i.next());
         }
      }
      catch(NullPointerException npe){
         String message = new String("No Temperature Time Data!");
         String display = new String("Error!");
         JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
      }
   }
}
