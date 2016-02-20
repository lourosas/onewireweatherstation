/*********************************************************************
* Copyright (C) 2013 Lou Rosas
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
*********************************************************************/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.TestPanel2;
import rosas.lou.lgraphics.LPanel;
import rosas.lou.lgraphics.WeatherPanelTotal;

public class IButtonView extends GenericJFrame
implements MemoryListener, MissionListener, LogListener{
   
   private static final short WIDTH  = 700;
   private static final short HEIGHT = 470;
   
   private enum State{GRAPH, DATA}

   private ButtonGroup itemGroup;
   //Temperature Low Alarm Label
   private JLabel tlal;
   //Temperature High Alarm Label
   private JLabel thal;
   //Temperature Low Alarm Text Field
   private JTextField  tlatf;
   //Temperature High Alarm Text Field
   private JTextField  thatf;
   //Sampling Rate Text Field
   private JTextField  srtf;
   //Mission Start Delay Text Field
   private JTextField  msdltf;
   //The JCheckBox Fields
   private JCheckBox   srtc;
   private JCheckBox   er;
   //The JComboBox Properties
   private JComboBox  tempComboBox;
   private JComboBox  humiComboBox;
   private JComboBox  dpComboBox; //Dew Point Combo Box
   private JComboBox  hiComboBox; //Heat Index Combo Box
   
   private IButton ib;
   
   private String fromTemp;
   private String toTemp;
   private String humiSelectionString;
   private String dpSelectionString;
   private String hiSelectionString;
   
   //Make the time data global!
   //NEED TO GET RID OF BOTH OF THESE!!!
   private LinkedList<Date> tempTimeLog;
   private LinkedList<Date> humiTimeLog;
   //LogEvent Data Based on current temperature "state"
   //Celsius Temperature Data Event
   private LogEvent cTempEvent;
   //Fahrenheit Temperature Data Event
   private LogEvent fTempEvent;
   //Kelvin Temperature Data Event
   private LogEvent kTempEvent;
   //Humidity Data Event
   private LogEvent humiEvent;
   //Celsius Dewpoint Data Event
   private LogEvent cDPEvent;
   //Kelvin  Dewpoint Data Event
   private LogEvent kDPEvent;
   //Fahrenheit Dewpoint Data Event
   private LogEvent fDPEvent;
   //Celsius Heat Index Data Event
   private LogEvent cHIEvent;
   //Kelvin Heat Index Data Event
   private LogEvent kHIEvent;
   //Fahreheit Heat Index Event
   private LogEvent fHIEvent;
   //Complete Dewpoint Log Event
   private LogEvent dewpointLogEvent;
   //Complete Heat Index Log Event
   private LogEvent heatIndexLogEvent;
   //Complete Humidity Log Event
   private LogEvent humidityLogEvent;
   //Complete Temperature Log Event
   private LogEvent temperatureLogEvent;
   
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

   //**********************Constructors******************************
   /**
   Constructor of no arguments
   */
   public IButtonView(){
      this("", null);
   }
   
   /**
   Constructor taking the Title and Controller Atributes
   */
   public IButtonView(String title, Object controller){
      super(title);
      this.ib = new IButton();
      this.ib.addLogListener(this);
      this.ib.addMemoryListener(this);
      this.ib.addMissionListener(this);
      if(controller == null){
         //Create a Controller Object if one is not provided
         //Object ibc = new IButtonController(this.ib, this);
         this.setUpGUI(new IButtonController(this.ib, this));
      }
      else{
         this.setUpGUI(controller);
      }
   }
   
   //**********************Public Methods****************************
   /**
   Implementation of the MemoryListener interface
   */   
   public void onMemoryEvent(MemoryEvent event){
      String message = event.getMessage();
      if(!message.contains("Exception")){
         JOptionPane.showMessageDialog(this, message);
         if(message.contains("Cleared")){
            //Reset to the default
            this.resetNewMissionGUI();
         }
      }
      else{
         String error = new String("Exception Occurred in ");
         error = error.concat("the Mission Memory");
         JOptionPane.showMessageDialog(this,
                                       error,
                                       "Exception Occurred",
                                       JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /**
   Implementation of the MissionListener interface
   */
   public void onMissionEvent(MissionEvent event){
      String message = event.getMessage();
      if(!message.contains("Exception")){
         JOptionPane.showMessageDialog(this, message);
      }
      else{
         String error = new String("Exception Occurred in Stopping");
         error = error + "\nthe mission";
         JOptionPane.showMessageDialog(this, 
                                       error,
                                       "Exeption Occurred",
                                       JOptionPane.ERROR_MESSAGE);
      }
   }
   
   /**
   Implementation of the LogListener interface:
   Dewpoint Logs
   */
   public void onDewpointLogEvent(LogEvent event){
      //Save off the LogEvent data
      this.setDewpointLogEvent(event);
      //Send to the Dewpoint Tab for appropriate display
      this.setUpThermalData(Types.DEWPOINT, true);
      //Send to the All Data Tab for display
      this.setUpAllData(true);
   }
   
   /**
   Implementation of the LogListener interface:
   extreme temperature data
   */
   public void onExtremeTemperatureLogEvent
   (
      LogEvent data,
      LogEvent time
   ){}
   
   /**
   Implementation of the LogListener interface:
   Heat Index Logs
   */
   public void onHeatIndexLogEvent(LogEvent event){
      //Save off the LogEvent data
      this.setHeatIndexLogEvent(event);
      //Send to the Heat Index Tab for the appropriate display
      this.setUpThermalData(Types.HEATINDEX, true);
      //Send to the All Data Tab for display
      this.setUpAllData(true);
   }
   
   /**
   Implementation of the LogListener interface:
   Humidity Logs
   */
   public void onHumidityLogEvent(LogEvent event){
      //Save off the LogEvent data
      this.setHumidityLogEvent(event);
      //Send to the Humidity Tab for the appropriate display
      this.setUpHumidityData(Types.HUMIDITY, true);
      //Send to the All Data Tab for display
      this.setUpAllData(true);
   }
   
   /**
   Implementation of the LogListener Interface for both
   the Humidity and Time Logs
   */
   public void onHumidityLogEvent(LogEvent humidty, LogEvent time){}
   
   /**
   Implementation of the LogListener interface:
   Humidity Time Logs
   */
   public void onHumidityTimeLogEvent(LogEvent event){}
   
   /**
   Implementation of the LogListener interface:
   Temperature Logs.
   */
   public void onTemperatureLogEvent(LogEvent event){
      //Need to save off the LogEvent data.
      this.setTemperatureLogEvent(event);
      //Send to the Temperature Tab for the appropriate display
      this.setUpThermalData(Types.TEMPERATURE, true);
      //Send to the All Data tab for display
      this.setUpAllData(true);
   }

   /**
   Implementation of the LogListener Interface for both the
   Temperature and Time Logs
   */
   public void onTemperatureLogEvent(LogEvent temp, LogEvent time){}
   
   /**
   Implementation of the LogListener interface:
   Temperature Time Logs
   */
   public void onTemperatureTimeLogEvent(LogEvent event){}
   
   /**
   */
   public NewMissionData requestNewMissionData(){
      NewMissionData nmd = new NewMissionData();
      nmd.setSampleRate(this.srtf.getText());
      nmd.setStartDelay(this.msdltf.getText());
      nmd.setTemperatureLowAlarm(this.tlatf.getText());
      nmd.setTemperatureHighAlarm(this.thatf.getText());
      Enumeration<AbstractButton> e = this.itemGroup.getElements();
      while(e.hasMoreElements()){
         AbstractButton ab = (AbstractButton)e.nextElement();
         if(ab.isSelected()){
            nmd.setUnits(ab.getText());
         }
      }
      nmd.setSynchClock(this.srtc.isSelected());
      nmd.setRolloverEnabled(this.er.isSelected());
      return nmd;
   }
   
   /**
   TBD...when the Controller is saved off persistently
   */
   public void saveMissionData(){}
   
   /**
   */
   public void saveMissionData(Object controller){
      int selectMode = JFileChooser.FILES_ONLY;
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter filter =
                          new FileNameExtensionFilter("*.csv", "csv");
      chooser.setDialogTitle("Save Mission Data");
      chooser.setFileSelectionMode(selectMode);
      chooser.setFileFilter(filter);
      chooser.addActionListener((ActionListener)controller);
      
      chooser.showSaveDialog(this);
   }
   
   /**
   Set whether or not the current weather data should be displayed
   or just be "refreshed"
   */
   public void setToDisplay(boolean display){
      this.toDisplay = display;
   }

   //**********************Private Methods***************************
   /**
   For multiple Weather Data, combine into ONE large Weather Data
   Object...this should be used when "All Days" is selected
   Basically, need to get the data in metric units so as to convert
   to the units to display the data in addtion, this will need to be
   converted to the appropriate units:  that should not be a problem,
   since the WeatherData class does that implicitly: it was designed
   to work directly with the data comming off the OneWire Temp device:
   which gets the temperature measurement in Metric, regardless.
   */
   private LinkedList<WeatherData> combineAllWeatherData
   (
      LinkedList<WeatherData> wd,
      LinkedList<WeatherData> mWD
   ){
      LinkedList<WeatherData> weatherList = new LinkedList();
      LinkedList<Date> dates              = new LinkedList();
      LinkedList<Double> values           = new LinkedList();
      //The Units and Type should be consistent with all the data
      Types type  = ((WeatherData)wd.get(0)).getType();
      Units units = ((WeatherData)wd.get(0)).getUnits();
      //Get the temperature in Metric...the WeatherData class will
      //automatically convert it for you...
      Iterator<WeatherData> it = mWD.iterator();
      while(it.hasNext()){
         WeatherData data = it.next();
         dates.addAll(data.getDates());
         values.addAll(data.getData());
      }
      WeatherData weatherData =
                          new WeatherData(type, units, values, dates);
      weatherList.add(weatherData);
      //Return this for now
      return weatherList;
   }
   /**
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
   private void drawTheData(JPanel panel, LinkedList<WeatherData> wd){
      try{
         System.out.println(panel);
      }
      catch(NullPointerException npe){}
   }
   
   /**
   */
   private LogEvent getDewpointLogEvent(){
      return this.dewpointLogEvent;
   }
   
   /**
   */
   private LogEvent getHeatIndexLogEvent(){
      return this.heatIndexLogEvent;
   }
   
   /**
   */
   private String getHowToDisplayData(String type){
      final int BREAK = 1000;
      String returnDisplay = null;
      JTabbedPane jtp      = null;
      JPanel displayPanel  = null;
      jtp = (JTabbedPane)this.getContentPane().getComponent(0);
      int currentTab = jtp.getSelectedIndex();
      for(int i = 0; i < jtp.getTabCount(); i++){
         if(jtp.getTitleAt(i).equals(type)){
            jtp.setSelectedIndex(i);
            JPanel currentPanel=(JPanel)jtp.getSelectedComponent();
            JPanel topPanel = (JPanel)currentPanel.getComponent(0);
            for(int j = 0; j < currentPanel.getComponentCount(); j++){
               displayPanel = (JPanel)topPanel.getComponent(j);
               if(displayPanel.getName().equals("DataPanel")){
                  j = BREAK;//Break out of the loop
               }
            }
            returnDisplay = this.getSelectedString(displayPanel);
         }
      }
      jtp.setSelectedIndex(currentTab);
      return returnDisplay;
   }
   
   /**
   */
   private LogEvent getHumidityLogEvent(){
      return this.humidityLogEvent;
   }
   
   /**
   */
   private boolean getToDisplay(){
      return this.toDisplay;
   }
   
   /**
   */
   private String getSelectedString(JPanel panel){
      String displayType = new String();
      int count = panel.getComponentCount();
      for(int j = 0; j< count; j++){
         JRadioButton jrb = (JRadioButton)panel.getComponent(j);
         String text = jrb.getText();
         boolean selected = jrb.isSelected();
         if(selected){
            displayType = new String(text);
         }
      }
      return displayType;
   }

   /**
   */
   private LogEvent getTemperatureLogEvent(){
      return this.temperatureLogEvent;
   }
   
   /**
   */
   private JComboBox getTheJComboBox(String type){
      JComboBox jcb = null;
      JTabbedPane jtp = null;
      jtp = (JTabbedPane)this.getContentPane().getComponent(0);
      int currentTab = jtp.getSelectedIndex();
      for(int i = 0; i < jtp.getTabCount(); i++){
         if(jtp.getTitleAt(i).equals(type)){
            jtp.setSelectedIndex(i);
            JPanel currentPanel = (JPanel)jtp.getSelectedComponent();
            JPanel topPanel = (JPanel)currentPanel.getComponent(0);
            //There are no units in humidity measurements,
            //Hence, the data panel is the first panel in the
            //Display Panel
            if(!type.equals("Humidity")){
               jcb = (JComboBox)topPanel.getComponent(2);
            }
            else if(type.equals("Humidity")){
               jcb = (JComboBox)topPanel.getComponent(1);
            }
         }
      }
      jtp.setSelectedIndex(currentTab);
      return jcb;
   }
   
   /**
   */
   private String getUnitsFromPanel(String type){
      final int BREAK = 1000;
      String returnType = null;
      JTabbedPane jtp = null;
      jtp = (JTabbedPane)this.getContentPane().getComponent(0);
      int currentTab = jtp.getSelectedIndex();
      for(int i = 0; i < jtp.getTabCount(); i++){
         if(jtp.getTitleAt(i).equals(type)){
            jtp.setSelectedIndex(i);
            JPanel currentPanel = (JPanel)jtp.getSelectedComponent();
            JPanel topPanel = (JPanel)currentPanel.getComponent(0);
            JPanel unitsPanel = null;
            for(int j = 0; j < topPanel.getComponentCount(); j++){
               JPanel panel = (JPanel)topPanel.getComponent(j);
               if(panel.getName().equals("UnitsPanel")){
                  unitsPanel = (JPanel)panel;
                  returnType = this.getSelectedString(unitsPanel);
                  j = BREAK;
               }
            }
         }
      }
      jtp.setSelectedIndex(currentTab);
      return returnType;
   }
   
   /**
   */
   private LinkedList<WeatherData> getWeatherData
   (
      LogEvent event,
      String date
   ){
      return this.getWeatherData(event, "Celsius", date);
   }

   /**
   */
   private Stack<String> getWhatDataToDisplay(String type){
      final int BREAK           = 1000;
      Stack<String> returnStack = null;
      JTabbedPane jtp           = null;
      jtp = (JTabbedPane)this.getContentPane().getComponent(0);
      int currentTab = jtp.getSelectedIndex();
      for(int i = 0; i < jtp.getTabCount(); i++){
         if(jtp.getTitleAt(i).equals(type)){
            jtp.setSelectedIndex(i);
            JPanel currentPanel = (JPanel)jtp.getSelectedComponent();
            JPanel topPanel = (JPanel)currentPanel.getComponent(0);
            JPanel typePanel = null;
            for(int j = 0; j < topPanel.getComponentCount(); j++){
               JPanel panel = (JPanel)topPanel.getComponent(j);
               if(panel.getName().equals("TypePanel")){
                  typePanel = panel;
                  int count = typePanel.getComponentCount();
                  for(int k = 0; k < count; k++){
                     JCheckBox jcb;
                     jcb = (JCheckBox)typePanel.getComponent(k);
                     if(jcb.isSelected()){
                        try{
                           returnStack.push(jcb.getName());
                        }
                        catch(NullPointerException npe){
                           returnStack = new Stack<String>();
                           returnStack.push(jcb.getName());
                        }
                     }
                  }
                  j = BREAK;
               }
            }
         }
      }
      jtp.setSelectedIndex(currentTab);
      return returnStack;
   }
   
   /**
   Grab all the data with the appropriate units, on the
   Appropriate Date
   */
   private LinkedList<WeatherData> getWeatherData
   (
      LogEvent event,
      String   units,
      String   date
   ){
      LinkedList<WeatherData> wd = new LinkedList();
      LinkedList<WeatherData> tempList =
                                      (LinkedList)event.getDataList();
      Types type = tempList.get(0).getType();
      Units unit;
      if(type != Types.HUMIDITY){
         if(units.toUpperCase().equals("CELSIUS")){
            unit = Units.METRIC;
         }
         else if(units.toUpperCase().equals("FAHRENHEIT")){
            unit = Units.ENGLISH;
         }
         else{
            unit = Units.ABSOLUTE;
         }
      }
      else{
         unit = Units.NULL;
      }
      Iterator<WeatherData> it = tempList.iterator();
      while(it.hasNext()){
         WeatherData data = it.next();
         if(data.getUnits() == unit){
            //Get all the Weather Data in the appropriate units
            if(date.toUpperCase().equals("ALL DAYS") ||
               date.equals(data.getDate())){
               wd.add(data);
            }
         }
      }
      return wd;
   }
   
   /**
   Reset the GUI Elements on the New Mission Panel
   */
   private void resetNewMissionGUI(){
      this.srtf.setText("");
      this.msdltf.setText("");
      this.tlatf.setText("");
      this.thatf.setText("");
      this.srtc.setSelected(false);
      this.er.setSelected(false);
      Enumeration<AbstractButton> e = this.itemGroup.getElements();
      while(e.hasMoreElements()){
         AbstractButton ab = (AbstractButton)e.nextElement();
         if(ab.getText().equals("Celsius")){
            ab.setSelected(true);
         }
         else{
            ab.setSelected(false);
         }
      }
   }
   
   /**
   */
   private JPanel setAllDataCenterPanel(){
      final int TOP    = 0;
      final int LEFT   = 5;
      final int BOTTOM = 0;
      final int RIGHT  = 5;
      //Center Border
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(
                                              TOP,LEFT,BOTTOM,RIGHT));
      return centerPanel;
   }
   
   /**
   */
   private JPanel setAllDataNorthPanel(){
      JPanel northPanel = new JPanel();
      northPanel.setName("NorthPanel");
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      ButtonGroup temperatureGroup = new ButtonGroup();
      
      JPanel typePanel = new JPanel();
      typePanel.setName("TypePanel");
      
      JCheckBox temp = new JCheckBox("Temperature");
      temp.setName("Temperature");
      temp.setActionCommand("AllDataTemp");
      this.setUpLocalCheckBoxItemListener(temp);
      typePanel.add(temp);
      
      JCheckBox humi = new JCheckBox("Humidity");
      humi.setName("Humidity");
      humi.setActionCommand("AllDataHumi");
      this.setUpLocalCheckBoxItemListener(humi);
      typePanel.add(humi);
      
      JCheckBox dewP = new JCheckBox("Dew Point");
      dewP.setName("Dew Point");
      dewP.setActionCommand("AllDataDP");
      this.setUpLocalCheckBoxItemListener(dewP);
      typePanel.add(dewP);
      
      JCheckBox hIdx = new JCheckBox("Heat Index");
      hIdx.setName("Heat Index");
      hIdx.setActionCommand("AllDataHI");
      this.setUpLocalCheckBoxItemListener(hIdx);
      typePanel.add(hIdx);
      
      northPanel.add(typePanel);
      
      JPanel tempPanel = new JPanel();
      tempPanel.setName("UnitsPanel");
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("AllDataCelsius");
      this.setUpLocalThermalTabItemListener(celsius);
      temperatureGroup.add(celsius);
      tempPanel.add(celsius);
      
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("AllDataFahrenheit");
      this.setUpLocalThermalTabItemListener(fahrenheit);
      temperatureGroup.add(fahrenheit);
      tempPanel.add(fahrenheit);
      
      //Cannot put the Kelvin Scale on in this version of the View!!
      
      northPanel.add(tempPanel);
      
      String dates[] = {"All Days"};
      JComboBox allDataComboBox = new JComboBox(dates);
      allDataComboBox.setName("AllData");
      this.setUpLocalJCBActionListener(allDataComboBox);
      northPanel.add(allDataComboBox);
      
      return northPanel;
   }
   
   /**
   */
   private JPanel setAllDataPanel(Object controller){
      String north  = BorderLayout.NORTH;
      String center = BorderLayout.CENTER;
      String south  = BorderLayout.SOUTH;
      JPanel allPanel = new JPanel();
      allPanel.setLayout(new BorderLayout());
      allPanel.add(this.setAllDataNorthPanel(),  north);
      allPanel.add(this.setAllDataCenterPanel(), center);
      allPanel.add(this.setAllDataSouthPanel(controller),  south);
      return allPanel;
   }
   
   /**
   */
   private JPanel setAllDataSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("AllData Refresh");
      refresh.addActionListener((ActionListener)controller);
      refresh.addKeyListener((KeyListener)controller);
      southPanel.add(refresh);
      
      return southPanel;
   }

   /**
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
   
   /**
   */
   private JPanel setDPCenterPanel(){
      final int TOP    = 0;
      final int LEFT   = 5;
      final int BOTTOM = 0;
      final int RIGHT  = 5;
      
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(
                                              TOP,LEFT,BOTTOM,RIGHT));
      return centerPanel;
   }
   
   /**
   */
   private JPanel setDPNorthPanel(Object controller){
      JPanel northPanel      = new JPanel();
      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup dataGroup  = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      
      //Set up the Units Panel
      JPanel unitsPanel = new JPanel();
      unitsPanel.setName("UnitsPanel");
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("DPCelsius");
      unitsGroup.add(celsius);
      this.setUpLocalThermalTabItemListener(celsius);
      unitsPanel.add(celsius);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("DPFahrenheit");
      unitsGroup.add(fahrenheit);
      this.setUpLocalThermalTabItemListener(fahrenheit);
      unitsPanel.add(fahrenheit);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("DPKelvin");
      unitsGroup.add(kelvin);
      this.setUpLocalThermalTabItemListener(kelvin);
      unitsPanel.add(kelvin);
      northPanel.add(unitsPanel);
      
      //Set up the Data Panel
      JPanel dataPanel = new JPanel();
      dataPanel.setName("DataPanel");
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("DPGraph");
      dataGroup.add(graph);
      this.setUpLocalThermalTabItemListener(graph);
      dataPanel.add(graph);
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("DPData");
      dataGroup.add(data);
      this.setUpLocalThermalTabItemListener(data);
      dataPanel.add(data);
      northPanel.add(dataPanel);
      
      //Set Up The Combo Box
      String dates[] = {"All Days"};
      JComboBox dewpointComboBox = new JComboBox(dates);
      dewpointComboBox.setName("Dewpoint");
      this.setUpLocalJCBActionListener(dewpointComboBox);
      northPanel.add(dewpointComboBox);
      return northPanel;
   }

   /**
   */
   private JPanel setDPSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      
      southPanel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("DP Refresh");
      refresh.addActionListener((ActionListener)controller);
      refresh.addKeyListener((KeyListener)controller);
      southPanel.add(refresh);

      JButton save = new JButton("Save Dewpoint Data");
      save.setActionCommand("DP Save");
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);
      southPanel.add(save);

      return southPanel;
   }
   
   /**
   */
   private void setDewpointLogEvent(LogEvent event){
      this.dewpointLogEvent = event;
   }

   /**
   */
   private JPanel setDewpointPanel(Object controller){
      String north  = BorderLayout.NORTH;
      String center = BorderLayout.CENTER;
      String south  = BorderLayout.SOUTH;
      JPanel dewpointPanel = new JPanel();
      dewpointPanel.setLayout(new BorderLayout());
      dewpointPanel.add(this.setDPNorthPanel(controller),  north);
      dewpointPanel.add(this.setDPCenterPanel(), center);
      dewpointPanel.add(this.setDPSouthPanel(controller), south);

      return dewpointPanel;
   }
   
   /**
   */
   private JPanel setHeatIndexPanel(Object controller){
      String north  = BorderLayout.NORTH;
      String center = BorderLayout.CENTER;
      String south  = BorderLayout.SOUTH;
      
      JPanel heatIndexPanel = new JPanel();
      heatIndexPanel.setLayout(new BorderLayout());
      heatIndexPanel.add(this.setHeatIndexNorthPanel(), north);
      heatIndexPanel.add(this.setHeatIndexCenterPanel(), center);
      heatIndexPanel.add(this.setHeatIndexSouthPanel(controller),south);
      return heatIndexPanel;
   }
   
   /**
   */
   private void setHeatIndexLogEvent(LogEvent event){
      this.heatIndexLogEvent = event;
   }
   
   /**
   */
   private JPanel setHeatIndexCenterPanel(){
      final int TOP    = 0;
      final int LEFT   = 5;
      final int BOTTOM = 0;
      final int RIGHT  = 5;
      
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(
                                              TOP,LEFT,BOTTOM,RIGHT));
      
      return centerPanel;
   }
   
   /**
   */
   private JPanel setHeatIndexNorthPanel(){
      JPanel northPanel      = new JPanel();
      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup dataGroup  = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      
      //Set up the Units Panel
      JPanel unitsPanel = new JPanel();
      unitsPanel.setName("UnitsPanel");
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("HICelsius");
      unitsGroup.add(celsius);
      this.setUpLocalThermalTabItemListener(celsius);
      unitsPanel.add(celsius);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("HIFahrenheit");
      unitsGroup.add(fahrenheit);
      this.setUpLocalThermalTabItemListener(fahrenheit);
      unitsPanel.add(fahrenheit);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("HIKelvin");
      unitsGroup.add(kelvin);
      this.setUpLocalThermalTabItemListener(kelvin);
      unitsPanel.add(kelvin);
      northPanel.add(unitsPanel);
      
      //Set up the Data Panel
      JPanel dataPanel = new JPanel();
      dataPanel.setName("DataPanel");
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("HIGraph");
      dataGroup.add(graph);
      this.setUpLocalThermalTabItemListener(graph);
      dataPanel.add(graph);
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("HIData");
      dataGroup.add(data);
      this.setUpLocalThermalTabItemListener(data);
      dataPanel.add(data);
      northPanel.add(dataPanel);
      
      //Set Up the Combo Box
      String dates[] = {"All Days"};
      JComboBox heatIndexComboBox = new JComboBox(dates);
      heatIndexComboBox.setName("HeatIndex");
      this.setUpLocalJCBActionListener(heatIndexComboBox);
      northPanel.add(heatIndexComboBox);

      return northPanel;
   }
   
   /**
   */
   private JPanel setHeatIndexSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("HI Refresh");
      refresh.addActionListener((ActionListener)controller);
      refresh.addKeyListener((KeyListener)controller);
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Heat Index Data");
      save.setActionCommand("HI Save");
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);
      southPanel.add(save);
      
      return southPanel;
   }
   
   /**
   */
   private JPanel setHumiCenterPanel(){
      final int TOP    = 0;
      final int LEFT   = 5;
      final int BOTTOM = 0;
      final int RIGHT  = 5;
      //Center Border
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(
                                              TOP,LEFT,BOTTOM,RIGHT));
      return centerPanel;
   }
   
   /**
   */
   private JPanel setHumiNorthPanel(){
      JPanel northPanel          = new JPanel();
      ButtonGroup dataGroup      = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      //Set up the Data
      JPanel dataPanel = new JPanel();
      dataPanel.setName("DataPanel");
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("HGraph");
      this.setUpLocalHumiTabItemListener(graph);
      dataGroup.add(graph);
      dataPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("HData");
      this.setUpLocalHumiTabItemListener(data);
      dataGroup.add(data);
      dataPanel.add(data);
      
      northPanel.add(dataPanel);
      
      //Set up the Combo Box
      String dates[] = {"All Days"};
      JComboBox humidityComboBox = new JComboBox(dates);
      humidityComboBox.setName("Humidity");
      this.setUpLocalJCBActionListener(humidityComboBox);
      northPanel.add(humidityComboBox);
      return northPanel;
   }
   
   /**
   */
   private JPanel setHumiSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Humi Refresh");
      refresh.addActionListener((ActionListener)controller);
      refresh.addKeyListener((KeyListener)controller);
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Humidity Data");
      save.setActionCommand("Humi Save");
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);
      southPanel.add(save);
      
      return southPanel;
   }
   
   /**
   */
   private void setHumidityLogEvent(LogEvent event){
      this.humidityLogEvent = event;
   }
   
   /**
   */
   private JPanel setHumidityPanel(Object controller){
      String north  = BorderLayout.NORTH;
      String center = BorderLayout.CENTER;
      String south  = BorderLayout.SOUTH;
      JPanel humidityPanel = new JPanel();
      humidityPanel.setLayout(new BorderLayout());
      humidityPanel.add(this.setHumiNorthPanel(), north);
      humidityPanel.add(this.setHumiCenterPanel(), center);
      humidityPanel.add(this.setHumiSouthPanel(controller), south);
      return humidityPanel;
   }

   /**
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
      start.addActionListener((ActionListener)controller);
      start.addKeyListener((KeyListener)controller);
      JButton clear = new JButton("Clear Mission");
      buttonPanel.add(clear);
      clear.addActionListener((ActionListener)controller);
      clear.addKeyListener((KeyListener)controller);
      JButton stop = new JButton("Stop Mission");
      buttonPanel.add(stop);
      stop.addActionListener((ActionListener)controller);
      stop.addKeyListener((KeyListener)controller);
      JButton refresh = new JButton("Refresh Mission Data");
      buttonPanel.add(refresh);
      refresh.addActionListener((ActionListener)controller);
      refresh.addKeyListener((KeyListener)controller);
      JButton save = new JButton("Save Mission Data");
      buttonPanel.add(save);
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);
      
      return buttonPanel;
   }
   
   /**
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
   
   /**
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
      //celsius.addItemListener((ItemListener)controller);
      radioPanel.add(celsius);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      //New Mission Fahrenheit
      fahrenheit.setActionCommand("NMFahrenheit");
      this.setUpLocalMissionTabItemListener(fahrenheit);
      //fahrenheit.addItemListener((ItemListener)controller);
      this.itemGroup.add(fahrenheit);
      radioPanel.add(fahrenheit);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      //New Mission Kelvin
      kelvin.setActionCommand("NMKelvin");
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
      //this.srtc.addItemListener((ItemListener)controller);
      //Enable Rollover
      this.er = new JCheckBox("Enable Rollover?");
      //this.er.addItemListener((ItemListener)controller);
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

   /**
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
   
   /**
   */
   private JPanel setTempNorthPanel(Object controller){
      JPanel northPanel            = new JPanel();
      ButtonGroup temperatureGroup = new ButtonGroup();
      ButtonGroup dataGroup        = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      //Set up the temperature panel
      JPanel tempPanel = new JPanel();
      tempPanel.setName("UnitsPanel");
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("TCelsius");
      temperatureGroup.add(celsius);
      this.setUpLocalThermalTabItemListener(celsius);
      this.temperatureUnits = Units.METRIC;
      tempPanel.add(celsius);
      
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("TFahrenheit");
      temperatureGroup.add(fahrenheit);
      this.setUpLocalThermalTabItemListener(fahrenheit);
      tempPanel.add(fahrenheit);
      
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("TKelvin");
      temperatureGroup.add(kelvin);
      this.setUpLocalThermalTabItemListener(kelvin);
      tempPanel.add(kelvin);
      
      northPanel.add(tempPanel);
      
      JPanel dataPanel = new JPanel();
      dataPanel.setName("DataPanel");
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("TGraph");
      this.temperatureDisplayState = State.GRAPH;
      dataGroup.add(graph);
      this.setUpLocalThermalTabItemListener(graph);
      dataPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("TData");
      dataGroup.add(data);
      this.setUpLocalThermalTabItemListener(data);
      dataPanel.add(data);
      
      northPanel.add(dataPanel);
      //Initialize the data
      String dates[] = {"All Days"};
      this.tempComboBox = new JComboBox(dates);
      this.tempComboBox.setName("Temperature");
      this.setUpLocalJCBActionListener(tempComboBox);
      northPanel.add(this.tempComboBox);
      return northPanel;
   }
   
   /**
   */
   private JPanel setTempSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Temp Refresh");
      refresh.addActionListener((ActionListener)controller);
      refresh.addKeyListener((KeyListener)controller);
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Temperature Data");
      save.setActionCommand("Temp Save");
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);
      southPanel.add(save);
      
      return southPanel;
   }   

   /**
   */
   private void setTemperatureLogEvent(LogEvent event){
      this.temperatureLogEvent = event;
   }
   
   /**
   Set up the data to display
   */
   private void setTheDay(Object comboBox){
      if(comboBox instanceof JComboBox){
         JComboBox jcbox = (JComboBox)comboBox;
         if(jcbox.getName().equals("Temperature")){
            this.setUpThermalData(Types.TEMPERATURE, false);
         }
         else if(jcbox.getName().equals("Humidity")){
            this.setUpHumidityData(Types.HUMIDITY, false);
         }
         else if(jcbox.getName().equals("Dewpoint")){
            this.setUpThermalData(Types.DEWPOINT, false);
         }
         else if(jcbox.getName().equals("HeatIndex")){
            this.setUpThermalData(Types.HEATINDEX, false);
         }
         else if(jcbox.getName().equals("AllData")){
            this.setUpAllData(false);
         }
      }
   }
   
   /**
   */
   private void setUpAllData(boolean displayException){
      String type                     = new String("All Data");
      Stack<String> displayStack      = null;
      JTabbedPane jtp                 = null;
      LogEvent event                  = null;
      LinkedList<WeatherData> wd      = null;
      LinkedList<WeatherData> mWD     = null;
      LinkedList<WeatherData> allData = null;
      try{
         jtp = (JTabbedPane)this.getContentPane().getComponent(0);
         int currentTab     = jtp.getSelectedIndex();
         String unitsString = this.getUnitsFromPanel(type);
         boolean grabDate   = false;
         String  date       = null;
         //Display what? Temperature, Humidity, Dew Point, Heat Index?
         displayStack = this.getWhatDataToDisplay(type);
         while(displayStack.size() > 0){
            String display = new String(displayStack.pop());
            if(display.toUpperCase().equals("TEMPERATURE")){
               event = this.getTemperatureLogEvent();
            }
            else if(display.toUpperCase().equals("HUMIDITY")){
               event = this.getHumidityLogEvent();
            }
            else if(display.toUpperCase().equals("DEW POINT")){
               event = this.getDewpointLogEvent();
            }
            else if(display.toUpperCase().equals("HEAT INDEX")){
               event = this.getHeatIndexLogEvent();
            }
            if(event.getMessage().toUpperCase().startsWith("ERROR")){
               throw new RuntimeException(event.getMessage());
            }
            try{
               //Grab the Date from ONE good event (if possible)
               if(!grabDate){
                  JComboBox jcb = this.getTheJComboBox(type);
                  this.setUpJComboBox(jcb, event);
                  date = new String((String)jcb.getSelectedItem());
                  grabDate = true;
               }
               wd = this.getWeatherData(event, unitsString, date);
               if(wd.size() > 1){
                  mWD = this.getWeatherData(event, date);
                  wd = this.combineAllWeatherData(wd, mWD);
               }
               try{
                  allData.add((WeatherData)wd.get(0));
               }
               catch(NullPointerException npe){
                  allData = new LinkedList<WeatherData>();
                  allData.add((WeatherData)wd.get(0));
               }
               event = null;//Nullify the event
            }
            catch(NullPointerException npe){
               String message = new String("No "+display+" Data!");
               String errordisplay = new String("Error!");
               JOptionPane.showMessageDialog(this, message,
                                         errordisplay,
                                         JOptionPane.ERROR_MESSAGE);
            }
            catch(RuntimeException re){
               int option = JOptionPane.ERROR_MESSAGE;
               String errordisplay = new String("Error!");
               String message = new String("No "+display+" Data!\n");
               message = 
                  message.concat("Could be one of several reasons\n");
               message =
                        message.concat("Please check to see if the ");
               message=message.concat("iButton\nis connected to an ");
               message=message.concat("adapter and the adapter\nis ");
               message=message.concat("connected to a computer.");
               JOptionPane.showMessageDialog(this, message,
                                         errordisplay,
                                         JOptionPane.ERROR_MESSAGE);
            }
         }
         this.setUpAllDataGraph(allData, type);
         jtp.setSelectedIndex(currentTab);
         //Set Visibity to "force" the system to draw the graph
         //(Stupid Java will "get around to it" when it wants to)
         this.setVisible(true);
      }
      catch(NullPointerException npe){}
   }
   
   /***/
   private void setUpAllDataGraph
   (
      LinkedList<WeatherData> list,
      String type
   ){
      try{
         WeatherData tempData = null;
         WeatherData humiData = null;
         WeatherData dewpData = null;
         WeatherData hidxData = null;
         Iterator it = list.iterator();
         while(it.hasNext()){
            WeatherData data = (WeatherData)it.next();
            if(data.getType() == Types.TEMPERATURE){
               tempData = data;
            }
            else if(data.getType() == Types.HUMIDITY){
               humiData = data;
            }
            else if(data.getType() == Types.DEWPOINT){
               dewpData = data;
            }
            else if(data.getType() == Types.HEATINDEX){
               hidxData = data;
            }
         }
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
         JPanel drawPanel = null;
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).equals(type)){
               jtp.setSelectedIndex(i);
               JPanel dataPanel = (JPanel)jtp.getSelectedComponent();
               drawPanel = (JPanel)dataPanel.getComponent(1);
               if(drawPanel.getComponentCount() >0){
                  drawPanel.removeAll(); //Remove Everything
               }
               drawPanel.setLayout(new BorderLayout());
               drawPanel.setBorder(
                            BorderFactory.createEmptyBorder(5,0,5,0));
            }
         }
         WeatherPanelTotal wpt =
           new WeatherPanelTotal(tempData,humiData,dewpData,hidxData);
         drawPanel.add(wpt, BorderLayout.CENTER);
      }
      catch(NullPointerException npe){}
   }
   
   /**
   */
   private void setUpDataGraph
   (
      LinkedList<WeatherData> list,
      String type
   ){
      try{
         WeatherData data = null;
         //First:  Clear the Drawing Panel out of previous data
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
         JPanel drawPanel = null;
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).equals(type)){
               jtp.setSelectedIndex(i);
               JPanel dataPanel = (JPanel)jtp.getSelectedComponent();
               drawPanel = (JPanel)dataPanel.getComponent(1);
               if(drawPanel.getComponentCount() > 0){
                  drawPanel.removeAll();//Remove everything
               }
               drawPanel.setLayout(new BorderLayout());
               drawPanel.setBorder(
                            BorderFactory.createEmptyBorder(5,0,5,0));
            }
         }
         if(list.size() == 1){
            data = (WeatherData)list.get(0);
            LinkedList<Date> dates = new LinkedList(data.getDates());
            LinkedList<Double> temps = new LinkedList(data.getData());
            LinkedList<Object> tempsO = new LinkedList(data.getData());
            LPanel lp =
                 new LPanel(tempsO,data.getMin(),data.getMax(),dates);
            drawPanel.add(lp, BorderLayout.CENTER);
         }
         else{
         }
      }
      catch(NullPointerException npe){}
   }

   /**
   */
   private void setUpDataText
   (
      LinkedList<WeatherData> list,
      String type
   ){
      try{
         String delimeter = new String();
         WeatherData data = null;
         //First, clear the Text Panel out of previous data
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
         JPanel textPanel = null;
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).equals(type)){
               jtp.setSelectedIndex(i);
               JPanel dataPanel = (JPanel)jtp.getSelectedComponent();
               textPanel = (JPanel)dataPanel.getComponent(1);
               if(textPanel.getComponentCount() > 0){
                  textPanel.removeAll();//Remove Everything
               }
               textPanel.setLayout(new BorderLayout());
            }
         }
         //Second: get the Weather Data
         if(list.size() == 1){
            data = (WeatherData)list.get(0); //Get the only entry
         }
         else{
            //Something is wrong...and do not draw out the data
            //might consider throwing an exception
         }
         //Third:  put the Selected data in the Text Area
         JTextArea textArea = new JTextArea(28, 35);
         //Do not let anyone modify the data
         textArea.setEditable(false);
         //Fourth:  put the data in the Text Area
         if(data.getUnits() == Units.METRIC){
            delimeter = new String("\u00B0C");
         }
         else if(data.getUnits() == Units.ENGLISH){
            delimeter = new String("\u00B0F");
         }
         else if(data.getUnits() == Units.ABSOLUTE){
            delimeter = new String("K");
         }
         else if(data.getUnits() == Units.NULL){
            //Humidity data
            delimeter = new String("%");
         }
         LinkedList<Date> dates    = new LinkedList(data.getDates());
         LinkedList<Object> values = new LinkedList(data.getData());

         Iterator d = dates.iterator();
         Iterator v = values.iterator();
         while(d.hasNext()){
            String date = new String((Date)d.next() + ": ");
            String value = String.format("%.2f", (Double)v.next());
            String out  = new String(date);
            out = out.concat(value + delimeter + "\n");
            textArea.append(out);
         }
         double max = data.getMax();
         double min = data.getMin();
         Date maxDate = data.getMaxTime();
         Date minDate = data.getMinTime();
         String maxString = new String("Max:  ");
         String minString = new String("Min:  ");
         maxString =
                 maxString.concat(String.format("%.2f", (Double)max));
         minString =
                 minString.concat(String.format("%.2f", (Double)min));
         maxString = maxString.concat(delimeter + ", " + maxDate);
         minString = minString.concat(delimeter + ", " + minDate);
         textArea.append(minString + "\n");
         textArea.append(maxString + "\n");
         //Fifth:  put the Text Area in A Scroll Pane
         JScrollPane scrollPane = new JScrollPane(textArea);
         //Sixth:  put the Scroll Pane in the Text Panel
         textPanel.add(scrollPane, BorderLayout.CENTER);
      }
      catch(NullPointerException npe){}
   }   
   
   /**
   */
   private void setUpHumidityData
   (
      Types types,
      boolean displayException
   ){
      try{
         String type = null;
         if(types == Types.HUMIDITY){
            type = new String("Humidity");
         }
         else{
            type = new String("UKNOWN"); //Should NEVER get here!!!
         }
         LogEvent event = this.getHumidityLogEvent();
         if(event.getMessage().startsWith("Error:")){
            throw new RuntimeException(event.getMessage());
         }
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
         int currentTab = jtp.getSelectedIndex();
         String display = this.getHowToDisplayData(type);
         JComboBox jcb  = this.getTheJComboBox(type);
         this.setUpJComboBox(jcb, event);
         String date = (String)jcb.getSelectedItem();
         LinkedList<WeatherData> hd = this.getWeatherData(event,date);
         if(hd.size() > 1){
            //No Units to attain, just need to get the data and
            //put in one big Weather Data Linked List
            hd = this.combineAllWeatherData(hd, hd);
         }
         if(display.equals("Graph")){
            this.setUpDataGraph(hd, type);
         }
         else if(display.equals("Data")){
            //if this works, the name is going to have to change
            this.setUpDataText(hd, type);
         }
         jtp.setSelectedIndex(currentTab);
         this.setVisible(true);
      }
      catch(NullPointerException npe){
         if(displayException){
            String message = new String("No Humidity Data!");
            String display = new String("Error!");
            JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
         }
      }
      catch(RuntimeException re){
         int option = JOptionPane.ERROR_MESSAGE;
         String display = new String("Error!");
         String message = new String("No Humidity Data!\n");
         message=message.concat("Could be one of several reasons\n");
         message=message.concat("Please check to see if the ");
         message=message.concat("iButton\nis connected to an ");
         message=message.concat("adapter and the adapter\nis ");
         message=message.concat("connected to a computer.");
         JOptionPane.showMessageDialog(this,message,display,option);
      }
   }
   
   /**
   */
   private void setUpLocalCheckBoxItemListener(JCheckBox jcb){
      jcb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               if(ab.getActionCommand().contains("AllData")){
                  setUpAllData(false);//Set up all the data
               }
            }
         }
      });
   }
   
   /**
   */
   private void setUpLocalHumiTabItemListener(JToggleButton jtb){
      jtb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               if(ab.isSelected()){
                  setUpHumidityData(Types.HUMIDITY, false);
               }
            }
         }
      });
   }
   
   /**
   */
   private void setUpLocalJCBActionListener(JComboBox jcb){
      jcb.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            Object o = e.getSource();
            if(o instanceof JComboBox){
               JComboBox jcb = (JComboBox)o;
               setTheDay(jcb);
            }
         }
      });
   }
   
   /**
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
   
   /**
   */
   private void setUpLocalThermalTabItemListener(JToggleButton jtb){
      jtb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            Object o = e.getItem();
            if(o instanceof AbstractButton){
               AbstractButton ab = (AbstractButton)o;
               if(ab.isSelected()){
                  String action = ab.getActionCommand();
                  if(action.startsWith("T")){
                     setUpThermalData(Types.TEMPERATURE, false);
                  }
                  else if(action.startsWith("DP")){
                     setUpThermalData(Types.DEWPOINT, false);
                  }
                  else if(action.startsWith("HI")){
                     setUpThermalData(Types.HEATINDEX,false);
                  }
                  else if(action.startsWith("All")){
                     setUpAllData(false);
                  }
               }
            }
         }
      });
   }
   
   /**
   Set up the GUI
   */
   private void setUpGUI(Object controller){
      try{
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
         jtp.addTab("All Data",
                    null,
                    this.setAllDataPanel(controller),
                    "Viewing all the Available Mission Data");
         this.getContentPane().add(jtp);
         //this.pack();
         this.setVisible(true);
      }
      catch(Exception e){ e.printStackTrace(); }
   }
   
   /**
   */
   private void setUpJComboBox(JComboBox jcb, LogEvent event){
      String date = new String("");
      //A rather cheesy way of doing this, but it works
      LinkedList<WeatherData> wd = (LinkedList)event.getDataList();
      //Store the dates in a Temporary Linked List to determine if the
      //Date that is being retrieved is already in the list.
      //There is NO reason to put the same date in multiple
      //Times!!!
      LinkedList<String> dateList    = new LinkedList();
      //The Dates that are in the JComboBox
      LinkedList<String> jcbDateList = new LinkedList();
      //Add them to the Linked List
      for(int i = 0; i < jcb.getItemCount(); i++){
         jcbDateList.add((String)jcb.getItemAt(i));
      }
      //Temp:  I need to fix what is below
      Iterator<WeatherData> it = wd.iterator();
      while(it.hasNext()){
         WeatherData data = (WeatherData)it.next();
         if(dateList.isEmpty()){
            dateList.add(data.getDate());
         }
         else if(!dateList.contains(data.getDate())){
            dateList.add(data.getDate());
         }
      }
      //Add Dates to the JComboBox that are not there from the
      //WeatherData
      if(!(jcbDateList.containsAll(dateList))){
         Iterator<String> dates = dateList.iterator();
         while(dates.hasNext()){
            date = dates.next();
            if(!jcbDateList.contains(date)){
               jcb.addItem(date);
            }
         }
      }
      //Now check to see if a date in the JComboBox is not in the
      //Date List from the Weather Data in the Weather Data List
      Iterator<String> jcbDates = jcbDateList.iterator();
      while(jcbDates.hasNext()){
         date = jcbDates.next();
         //Do not remove "All Days"  from the JComboBox
         if(!(date.toUpperCase().contains("ALL")) && 
            !(dateList.contains(date))){
            jcb.removeItem(date);  //Hopefully, this will work
         }
      }
   }
   
   /**
   */
   private void setUpThermalData
   (
      Types types,
      boolean displayException
   ){
      String type    = null;
      LogEvent event = null;
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int currentTab = jtp.getSelectedIndex();
      try{
         if(types == Types.TEMPERATURE){
            type  = new String("Temperature");
            event = this.getTemperatureLogEvent();
         }
         else if(types == Types.DEWPOINT){
            type  = new String("Dew Point");
            event = this.getDewpointLogEvent();
         }
         else if(types == Types.HEATINDEX){
            type = new String("Heat Index");
            event = this.getHeatIndexLogEvent();
         }
         else{
            type = new String("UKNOWN"); //Should never get here!!
         }
         //Handle an Error Message
         if(event.getMessage().startsWith("Error:")){
            throw new RuntimeException(event.getMessage());
         }
         String unitsString = this.getUnitsFromPanel(type);
         String display     = this.getHowToDisplayData(type);
         JComboBox jcb      = this.getTheJComboBox(type);
         this.setUpJComboBox(jcb, event);
         String date = (String)jcb.getSelectedItem();
         LinkedList<WeatherData> wd =
                        this.getWeatherData(event, unitsString, date);
         if(wd.size() > 1){
            LinkedList<WeatherData> mWD = new LinkedList();
            //Get the data in the metric units
            mWD = this.getWeatherData(event, date);
            //Get the data in the desired units
            wd = this.combineAllWeatherData(wd, mWD);
         }
         if(display.equals("Graph")){
            this.setUpDataGraph(wd, type);
         }
         else if(display.equals("Data")){
            this.setUpDataText(wd, type);
         }
         jtp.setSelectedIndex(currentTab);
         //Set Visibity to "force" the system to draw the graph
         //(Stupid Java will "get around to it" when it wants to)
         this.setVisible(true);
      }
      catch(NullPointerException npe){
         if(displayException){
            String message = new String("No " + type + " Data!");
            String display = new String("Error!");
            JOptionPane.showMessageDialog(this, message, display,
                                         JOptionPane.ERROR_MESSAGE);
         }
      }
      catch(RuntimeException re){
         int option = JOptionPane.ERROR_MESSAGE;
         String display = new String("Error!");
         String message = new String("No " + type + " Data!\n");
         message=message.concat("Could be one of several reasons\n");
         message=message.concat("Please check to see if the ");
         message=message.concat("iButton\nis connected to an ");
         message=message.concat("adapter and the adapter\nis ");
         message=message.concat("connected to a computer.");
         JOptionPane.showMessageDialog(this,message,display,option);
      }
   }
}
