/*
Copyright 2020 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.text.ParseException;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.WeatherPanel;

//////////////////////////////////////////////////////////////////////
/*
*/
//////////////////////////////////////////////////////////////////////
public class WeatherDatabaseClientView extends WeatherView
implements WeatherDatabaseClientObserver{
   private static final short GRAPH        = 0;
   private static final short DATA         = 1;
   private static final short WIDTH        = 750;
   private static final short HEIGHT       = 600;
   private static final short TOTAL_PANELS = 5;

   private static String [] MONTHS = {"January", "February",
   "March", "April", "May", "June", "July", "August", "September",
   "October", "November", "December"};
   private static String [] DAYS = {"01","02","03","04","05","06",
   "07","08","09","10","11","12","13","14","15","16","17","18","19",
   "20","21","22","23","24","25","26","27","28","29","30","31"};
   private static String [] YEARS = {"2017", "2018", "2019", "2020",
   "2021"};

   private GenericWeatherController _controller = null;
   //Really do not need these to be global any longer--handled by
   //The Controller directly, realtime...
   private JTextField _address        = null;
   //private JTextField _port           = null;
   private JComboBox<String> _monthCB = null;
   private JComboBox<String> _dayCB   = null;
   private JComboBox<String> _yearCB  = null;

   private java.util.List<WeatherData> temperatureData = null;
   private java.util.List<WeatherData> temperatureMinMaxAvgData=null;
   private java.util.List<WeatherData> humidityData    = null;
   private java.util.List<WeatherData> humidityMinMaxAvgData=null;
   private java.util.List<WeatherData> pressureData    = null;
   private java.util.List<WeatherData> dewpointData    = null;
   private java.util.List<WeatherData> dewpointMinMaxAvgData=null;
   private java.util.List<WeatherData> heatIndexData   = null;
   private java.util.List<WeatherData> heatIndexMinMaxAvgData=null;

   private Units dewpointUnits    = Units.METRIC;
   private Units temperatureUnits = Units.METRIC;
   private Units heatIndexUnits   = Units.METRIC;
   private Units pressureUnits    = Units.ABSOLUTE;
   private short heatIndexDisplay = GRAPH;
   private short tempDisplay      = GRAPH;
   private short humidityDisplay  = GRAPH;
   private short dewpointDisplay  = GRAPH;
   private short pressureDisplay  = GRAPH;

   private ButtonGroup saveButtonGroup = null;

   ///////////////////////////Public Methods//////////////////////////
   /////////////////////////////Constructors//////////////////////////
   /**/
   public WeatherDatabaseClientView(){
      this("");
   }

   /**/
   public WeatherDatabaseClientView(String title){
      super(title);
      //Add Model
      //Add Controllers
      this._controller = new WeatherDatabaseClientController(this);
      this.setUpGUI();
   }

   /**/
   public WeatherDatabaseClientView(
      String title,
      GenericWeatherController controller
   ){
      super(title);
      this._controller = controller;
      this._controller.addView(this);
      this.setUpGUI();
   }

   //////////////////////////Public Methods///////////////////////////
   /**/
   public String address(){
      return this._address.getText();
   }

   /*
   public String port(){
      return this._port.getText();
   }
   */

   ///////WeatherDatabaseClientObserver Interface Implementaion///////
   public void alertDewpointTimeout(){}

   public void alertHeatIndexTimeout(){}

   public void alertHumidityTimeout(){}

   public void alertPressureTimeout(){}

   public void alertTemperatureTimeout(){}

   public void alertNoDewpointData(){
      try{
         int tempTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("DEW POINT")){
               tempTab = i;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)tempPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
         drawPanel.setLayout(new BorderLayout());
         String errorString = new String("No Dewpoint Data ");
         errorString = errorString.concat("Available");
         //errorString = errorString.concat("Available for this Date");
         JLabel label = new JLabel(errorString,SwingConstants.CENTER);
         drawPanel.add(label, BorderLayout.CENTER);
         this.addDateToPanel(new DewpointData(),
                             (JPanel)tempPanel.getComponent(2),2);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(tempTab);
         this.enableSaveButton("dewpoint", false);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
      finally{
         this.dewpointData = new java.util.LinkedList<WeatherData>();
      }
   }

   public void alertNoDewpointData(Exception e){
      this.alertNoDewpointData();
      this.enableSaveButton("dewpoint", false);
      JOptionPane.showMessageDialog(this, e.getMessage(),
                                    "Dewpoint Data Error",
                                    JOptionPane.ERROR_MESSAGE);
      this.dewpointData = new java.util.LinkedList<WeatherData>();
   }

   public void alertNoDewpointMinMaxAvg(Exception e){
      e.printStackTrace();
   }

   public void alertNoHeatIndexData(){
      try{
         int tempTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("HEAT INDEX")){
               tempTab = i;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)tempPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
         drawPanel.setLayout(new BorderLayout());
         String errorString = new String("No Heatindex Data ");
         errorString = errorString.concat("Available");
         //errorString = errorString.concat("Available for this Date");
         JLabel label = new JLabel(errorString,SwingConstants.CENTER);
         drawPanel.add(label, BorderLayout.CENTER);
         this.addDateToPanel(new HeatIndexData(),
                                 (JPanel)tempPanel.getComponent(2),2);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(tempTab);
         this.enableSaveButton("heatindex", false);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
      finally{
         this.heatIndexData = new java.util.LinkedList<WeatherData>();
      }
   }

   public void alertNoHeatIndexData(Exception e){
      this.alertNoHeatIndexData();
      this.enableSaveButton("heatindex", false);
      JOptionPane.showMessageDialog(this, e.getMessage(),
                                    "Heat Index Data Error",
                                    JOptionPane.ERROR_MESSAGE);
      this.heatIndexData = new java.util.LinkedList<WeatherData>();

   }

   public void alertNoHeatIndexMinMaxAvg(Exception e){
      e.printStackTrace();
   }

   public void alertNoHumidityData(){
      try{
         int tempTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("HUMIDITY")){
               tempTab = i;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)tempPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
         drawPanel.setLayout(new BorderLayout());
         String errorString = new String("No Humidity Data ");
         errorString = errorString.concat("Available");
         //errorString = errorString.concat("Available for this Date");
         JLabel label = new JLabel(errorString,SwingConstants.CENTER);
         drawPanel.add(label, BorderLayout.CENTER);
         this.addDateToPanel(new HumidityData(),
                                 (JPanel)tempPanel.getComponent(2),1);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(tempTab);
         this.enableSaveButton("humidity", false);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
      finally{
         this.humidityData = new java.util.LinkedList<WeatherData>();
      }
   }

   public void alertNoHumidityData(Exception e){
      //Indicate there is no data to display
      this.alertNoHumidityData();
      this.enableSaveButton("Humidity", false);
      JOptionPane.showMessageDialog(this, e.getMessage(),
                                    "Humidity Data Error",
                                    JOptionPane.ERROR_MESSAGE);

      this.humidityData = new java.util.LinkedList<WeatherData>();
   }

   public void alertNoHumidityMinMaxAvg(Exception e){
      e.printStackTrace();
   }

   public void alertNoPressureData(){
      try{
         int tempTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("PRESSURE")){
               tempTab = i;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)tempPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
         drawPanel.setLayout(new BorderLayout());
         String errorString = new String("No Pressure Data ");
         errorString = errorString.concat("Available");
         //errorString = errorString.concat("Available for this Date");
         JLabel label = new JLabel(errorString,SwingConstants.CENTER);
         drawPanel.add(label, BorderLayout.CENTER);
         this.addDateToPanel(new PressureData(),
                                 (JPanel)tempPanel.getComponent(2),2);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(tempTab);
         this.enableSaveButton("pressure", false);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
      finally{
         this.pressureData = new java.util.LinkedList<WeatherData>();
      }
   }

   public void alertNoPressureData(Exception e){
      this.alertNoPressureData();
      this.enableSaveButton("pressure", false);
      JOptionPane.showMessageDialog(this, e.getMessage(),
                                    "Pressure Data Error",
                                    JOptionPane.ERROR_MESSAGE);
      this.pressureData = new java.util.LinkedList<WeatherData>();
   }

   public void alertNoTemperatureData(){
      try{
         int tempTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("TEMPERATURE")){
               tempTab = i;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)tempPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
         drawPanel.setLayout(new BorderLayout());
         String errorString = new String("No Temperature Data ");
         errorString = errorString.concat("Available");
         //errorString = errorString.concat("Available for this Date");
         JLabel label = new JLabel(errorString,SwingConstants.CENTER);
         drawPanel.add(label, BorderLayout.CENTER);
         this.addDateToPanel(new TemperatureData(),
                            (JPanel)tempPanel.getComponent(2),2);
         jtp.setSelectedIndex(tempTab + 1);
         jtp.setSelectedIndex(tempTab);
         //If there is NO DATA, do not want to print
         //this.setTemperatureDisplay(GRAPH);
         this.enableSaveButton("temperature", false);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
      finally{
         this.temperatureData=new java.util.LinkedList<WeatherData>();
      }
   }

   public void alertNoTemperatureData(Exception e){
      //Do not want to PRINT the data if there is none...
      this.alertNoTemperatureData();
      this.enableSaveButton("temperature", false);
      JOptionPane.showMessageDialog(this, e.getMessage(),
                                    "Temperature Data Error",
                                    JOptionPane.ERROR_MESSAGE);
      this.temperatureData = new java.util.LinkedList<WeatherData>();
   }

   public void alertNoTemperatureMinMaxAvgData(Exception e){
      e.printStackTrace();//change later
   }

   public void updateAddress(String address){
      this._address.setText(address);
      this._address.setCaretPosition(this._address.getText().length());
      this._address.requestFocus();
      this._address.selectAll();
      this.pressCurrentRefresh();
   }

   public void updatePort(String port){
      //this._port .setText(port);
      //this._port.setCaretPosition(this._port.getText().length());
   }

   public void updateDewpointData(java.util.List<WeatherData> data){
      this.dewpointData = data;
      if(this.dewpointDisplay == GRAPH){
         this.graphDewpoint(data);
         this.enableSaveButton("dewpoint", false);
      }
      else{
         this.printDewpoint(data);
         this.enableSaveButton("dewpoint", true);
      }
   }

   public void updateDewpointMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      this.dewpointMinMaxAvgData = data;
      if(this.dewpointDisplay == GRAPH){
         this.displayDewpointMinMaxAvg(data);
      }
      else{}
   }

   public void updateHeatIndexData(java.util.List<WeatherData> data){
      this.heatIndexData = data;
      if(this.heatIndexDisplay == GRAPH){
         this.graphHeatIndex(data);
         this.enableSaveButton("heatindex", false);
      }
      else{
         this.printHeatIndex(data);
         this.enableSaveButton("heatindex", true);
      }
   }

   public void updateHeatIndexMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      this.heatIndexMinMaxAvgData = data;
      if(this.heatIndexDisplay == GRAPH){
         this.displayHeatIndexMinMaxAvg(data);
      }
      else{}
   }

   public void updateHumidityData(java.util.List<WeatherData> data){
      this.humidityData = data;
      if(this.humidityDisplay == GRAPH){
         this.graphHumidity(data);
         this.enableSaveButton("humidity", false);
      }
      else{
         this.printHumidity(data);
         this.enableSaveButton("humidity", true);
      }
   }

   public void updateHumidityMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      this.humidityMinMaxAvgData = data;
      if(this.humidityDisplay == GRAPH){
         this.displayHumidityMinMaxAvg(data);
      }
      else{}
   }

   public void updatePressureData(java.util.List<WeatherData> data){
      this.pressureData = data;
      if(this.pressureDisplay == GRAPH){
         this.graphPressure(data);
         this.enableSaveButton("pressure", false);
      }
      else{
         this.printPressure(data);
         this.enableSaveButton("pressure", true);
      }
   }

   public void updateTemperatureData(java.util.List<WeatherData> data){
      this.temperatureData = data;
      if(this.tempDisplay == GRAPH){
         this.graphTemperature(data);
         this.enableSaveButton("temperature", false);
      }
      else{
         this.printTemperature(data);
         this.enableSaveButton("temperature", true);
      }
   }

   public void updateTemperatureMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      this.temperatureMinMaxAvgData = data;
      if(this.tempDisplay == GRAPH){
         this.displayTempMinMaxAvg(data);
      }
      else{}
   }

   ///////////////////////Public Methods//////////////////////////////
   /**/
   public void displayDewpoint(short display){
      this.setDewpointDisplay(display);
      this.displayDewpoint(this.dewpointUnits);
   }

   /**/
   public void displayDewpoint(Units units){
      try{
         this.setDewpointUnits(units);
         if(this.dewpointData.size() > 0){
            this.updateDewpointData(this.dewpointData);
            this.updateDewpointMinMaxAvg(this.dewpointMinMaxAvgData);
         }
         else{
            this.alertNoDewpointData();
         }
      }
      catch(NullPointerException npe){
         this.pressCurrentRefresh();
      }
   }

   /**/
   public void displayHeatIndex(short display){
      this.setHeatIndexDisplay(display);
      this.displayHeatIndex(this.heatIndexUnits);
   }

   /**/
   public void displayHeatIndex(Units units){
      try{
         this.setHeatIndexUnits(units);
         if(this.heatIndexData.size() > 0){
            this.updateHeatIndexData(this.heatIndexData);
         }
         else{
            this.alertNoHeatIndexData();
         }
      }
      catch(NullPointerException npe){
         this.pressCurrentRefresh();
      }
   }

   /**/
   public void displayHumidity(short display){
      try{
         this.setHumidityDisplay(display);
         if(this.humidityData.size() > 0){
            this.updateHumidityData(this.humidityData);
         }
         else{
            this.alertNoHumidityData();
         }
      }
      catch(NullPointerException npe){
         this.pressCurrentRefresh();
      }
   }

   /**/
   public void displayPressure(short display){
      this.setPressureDisplay(display);
      this.displayPressure(this.pressureUnits);
   }

   /**/
   public void displayPressure(Units units){
      try{
         this.setPressureUnits(units);
         if(this.pressureData.size() > 0){
            this.updatePressureData(this.pressureData);
         }
         else{
            this.alertNoPressureData();
         }
      }
      catch(NullPointerException npe){
         this.pressCurrentRefresh();
      }
   }

   /**/
   public void displayTemperature(short display){
      this.setTemperatureDisplay(display);
      this.displayTemperature(this.temperatureUnits);
   }
   /**/
   public void displayTemperature(Units units){
      try{
         this.setTemperatureUnits(units);
         if(this.temperatureData.size() > 0){
            this.updateTemperatureData(this.temperatureData);
         }
         else{
            this.alertNoTemperatureData();
         }
      }
      catch(NullPointerException npe){
         this.pressCurrentRefresh();
      }
   }

   /**/
   public String getDay(){
      return (String)this._dayCB.getSelectedItem();
   }

   /**/
   public String getMonth(){
      return (String)this._monthCB.getSelectedItem();
   }

   /**/
   public String getYear(){
      return (String)this._yearCB.getSelectedItem();
   }

   /**/
   public Units getDewpointUnits(){
      return this.dewpointUnits;
   }

   /**/
   public Units getHeatIndexUnits(){
      return this.heatIndexUnits;
   }

   /**/
   public Units getPressureUnits(){
      return this.pressureUnits;
   }

   /**/
   public Units getTemperatureUnits(){
      return this.temperatureUnits;
   }

   /**/
   public void setDewpointDisplay(short display){
      this.dewpointDisplay = display;
      if(this.tempDisplay == GRAPH){
         this.enableSaveButton("dewpoint", false);
      }
      else{
         this.enableSaveButton("dewpoint", true);
      }
   }

   /**/
   public void setHeatIndexDisplay(short display){
      this.heatIndexDisplay = display;
      if(this.tempDisplay == GRAPH){
         this.enableSaveButton("heatindex", false);
      }
      else{
         this.enableSaveButton("heatindex", true);
      }
   }
   /**/
   public void setHumidityDisplay(short display){
      this.humidityDisplay = display;
      if(this.tempDisplay == GRAPH){
         this.enableSaveButton("humidity", false);
      }
      else{
         this.enableSaveButton("humidity", true);
      }
   }

   /**/
   public void setPressureDisplay(short display){
      this.pressureDisplay = display;
      if(this.tempDisplay == GRAPH){
         this.enableSaveButton("pressure", false);
      }
      else{
         this.enableSaveButton("pressure", true);
      }
   }

   /**/
   public void setTemperatureDisplay(short display){
      this.tempDisplay = display;
      if(this.tempDisplay == GRAPH){
         this.enableSaveButton("temperature", false);
      }
      else{
         this.enableSaveButton("temperature", true);
      }
   }

   /**/
   public void setDewpointUnits(Units units){
      this.dewpointUnits = units;
   }

   /**/
   public void setHeatIndexUnits(Units units){
      this.heatIndexUnits = units;
   }

   /**/
   public void setPressureUnits(Units units){
      this.pressureUnits = units;
   }

   /**/
   public void setTemperatureUnits(Units units){
      this.temperatureUnits = units;
   }

   //////////////////////////Private Methods//////////////////////////
   /**/
   private void addDateToPanel(WeatherData wd,JPanel panel,int index){
      try{
         panel.remove(index);
         //panel.removeAll();
      }
      catch(ArrayIndexOutOfBoundsException obe){}
      finally{
         String dateString = null;
         JLabel dateLabel  = null;
         String month      = null;
         String day        = null;
         String year       = null;
         if(wd != null){
            try{
               if(wd.month()==null||wd.day()==null||wd.year()==null){
                  throw new NullPointerException();
               }
               month = wd.month();day = wd.day();year = wd.year();
            }
            catch(NullPointerException npe){
               month = (String)this._monthCB.getSelectedItem();
               day   = (String)this._dayCB.getSelectedItem();
               year  = (String)this._yearCB.getSelectedItem();
            }
            finally{
               dateString = new String(month+" "+day+", "+year);
               dateLabel  = new JLabel(dateString);
               //Royal Blue
               dateLabel.setForeground(new Color(0x4961E1));
               panel.add(dateLabel);
            }
         }
      }
   }

   /**/
   private void displayDewpointMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      try{
         WeatherData minData = data.get(0);
         WeatherData maxData = data.get(1);
         WeatherData avgData = data.get(2);
         String min = null; String max = null; String avg = null;
         if(this.dewpointUnits == Units.METRIC){
            min = minData.toStringMetric();
            max = maxData.toStringMetric();
            avg = avgData.toStringMetric();
         }
         else if(this.dewpointUnits == Units.ENGLISH){
            min = minData.toStringEnglish();
            max = maxData.toStringEnglish();
            avg = avgData.toStringEnglish();
         }
         else if(this.dewpointUnits == Units.ABSOLUTE){
            min = minData.toStringAbsolute();
            max = maxData.toStringAbsolute();
            avg = avgData.toStringAbsolute();
         }
         System.out.println(min);
         System.out.println(max);
         System.out.println(avg);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void displayHeatIndexMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      try{
         System.out.println(data);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void displayHumidityMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      try{
         System.out.println(data);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void displayTempMinMaxAvg
   (
      java.util.List<WeatherData> data
   ){
      try{
         System.out.println(data);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void enableSaveButton(String button, boolean enable){
      String command = new String();
      if(button.equals("temperature")){
         command = "TEMPERATURESAVE";
      }
      else if(button.equals("humidity")){
         command = "HUMIDITYSAVE";
      }
      else if(button.equals("pressure")){
         command = "PRESSURE SAVE";
      }
      else if(button.equals("heatindex")){
         command = "HEATINDEXSAVE";
      }
      else if(button.equals("dewpoint")){
         command = "DEWPOINTSAVE";
      }
      try{
         Enumeration<AbstractButton> e =
                                   this.saveButtonGroup.getElements();
         while(e.hasMoreElements()){
            AbstractButton ab = e.nextElement();
            if(ab.getActionCommand().toUpperCase().equals(command)){
               ab.setEnabled(enable);
            }
         }
      }
      catch(NullPointerException npe){}
   }

   /**/
   private String grabMeasureString
   (
      java.util.List<WeatherData> data,
      Units units
   ){
      String value = null;
      try{
         Iterator<WeatherData> it = data.iterator();
         value = new String();
         while(it.hasNext()){
            WeatherData wd = it.next();
            value = value.concat(wd.month() + " " + wd.day() + " ");
            value = value.concat(wd.year() + " " +wd.time() + " ");
            if(units == Units.ABSOLUTE){
               value = value.concat(wd.toStringAbsolute());
            }
            else if(units == Units.ENGLISH){
               value = value.concat(wd.toStringEnglish());
            }
            else if(units == Units.METRIC){
               value = value.concat(wd.toStringMetric());
            }
            else if(units == Units.PERCENTAGE){
               value = value.concat(wd.toStringPercentage());
            }
            value = value.concat("\n");
         }
      }
      catch(NullPointerException npe){}
      return value;
   }

   /**/
   private void graphDewpoint(java.util.List<WeatherData> data){
      try{
         int dpTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("DEW POINT")){
               dpTab = i;
            }
         }
         jtp.setSelectedIndex(dpTab);
         JPanel dpPanel   = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)dpPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new WeatherPanel(data, this.dewpointUnits),
                                                 BorderLayout.CENTER);
         this.addDateToPanel(data.get(0),
                                   (JPanel)dpPanel.getComponent(2),2);
         jtp.setSelectedIndex(dpTab + 1);
         jtp.setSelectedIndex(dpTab);

      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void printDewpoint(java.util.List<WeatherData> data){
      String dewpoint=this.grabMeasureString(data,this.dewpointUnits);
      try{
         JTextArea dpArea = new JTextArea(dewpoint);
         dpArea.setEditable(false);
         JScrollPane dpSP = new JScrollPane(dpArea);
         int dpTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("DEW POINT")){
               dpTab = i;
            }
         }
         jtp.setSelectedIndex(dpTab);
         JPanel dpPanel   = (JPanel)jtp.getSelectedComponent();
         JPanel textPanel = (JPanel)dpPanel.getComponent(0);
         if(textPanel.getComponentCount() > 0){
            textPanel.removeAll();
         }
         textPanel.setBorder(
                        BorderFactory.createEmptyBorder(25,25,25,25));
         textPanel.setLayout(new BorderLayout());
         textPanel.add(dpSP, BorderLayout.CENTER);
         this.addDateToPanel(null,(JPanel)dpPanel.getComponent(2),2);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(dpTab);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void graphHeatIndex(java.util.List<WeatherData> data){
      try{
         int hiTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("HEAT INDEX")){
               hiTab = i;
            }
         }
         jtp.setSelectedIndex(hiTab);
         JPanel hiPanel   = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)hiPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new WeatherPanel(data, this.heatIndexUnits),
                                                 BorderLayout.CENTER);
         this.addDateToPanel(data.get(0),
                             (JPanel)hiPanel.getComponent(2),2);
         jtp.setSelectedIndex(hiTab -1);
         jtp.setSelectedIndex(hiTab);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }
   /**/
   private void printHeatIndex(java.util.List<WeatherData> data){
      String heatIndex = this.grabMeasureString(data,
                                                this.heatIndexUnits);
      try{
         JTextArea heatIndexArea = new JTextArea(heatIndex);
         heatIndexArea.setEditable(false);
         JScrollPane heatIndexSP = new JScrollPane(heatIndexArea);
         int hiTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("HEAT INDEX")){
               hiTab = i;
            }
         }
         jtp.setSelectedIndex(hiTab);
         JPanel hiPanel   = (JPanel)jtp.getSelectedComponent();
         JPanel textPanel = (JPanel)hiPanel.getComponent(0);
         if(textPanel.getComponentCount() > 0){
            textPanel.removeAll();
         }
         textPanel.setBorder(
                        BorderFactory.createEmptyBorder(25,25,25,25));
         textPanel.setLayout(new BorderLayout());
         textPanel.add(heatIndexSP, BorderLayout.CENTER);
         this.addDateToPanel(null,(JPanel)hiPanel.getComponent(2),2);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(hiTab);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void graphHumidity(java.util.List<WeatherData> data){
      try{
         int humidityTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("HUMIDITY")){
               humidityTab = i;
            }
         }
         jtp.setSelectedIndex(humidityTab);
         JPanel humidityPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel     = (JPanel)humidityPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
         drawPanel.setLayout(new BorderLayout());

         drawPanel.add(new WeatherPanel(data, Units.PERCENTAGE),
                                                 BorderLayout.CENTER);
         this.addDateToPanel(data.get(0),
                             (JPanel)humidityPanel.getComponent(2),1);
         jtp.setSelectedIndex(humidityTab + 1);
         jtp.setSelectedIndex(humidityTab);
      }
      catch(NullPointerException npe){npe.printStackTrace();}
   }

   /**/
   private void printHumidity(java.util.List<WeatherData> data){
      String humidity = this.grabMeasureString(data,Units.PERCENTAGE);
      try{
         JTextArea humidityArea = new JTextArea(humidity);
         humidityArea.setEditable(false);
         JScrollPane humiditySP = new JScrollPane(humidityArea);
         int humidityTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("HUMIDITY")){
               humidityTab = i;
            }
         }
         jtp.setSelectedIndex(humidityTab);
         JPanel humidityPanel = (JPanel)jtp.getSelectedComponent();
         JPanel textPanel     = (JPanel)humidityPanel.getComponent(0);
         if(textPanel.getComponentCount() > 0){
            textPanel.removeAll();
         }
         textPanel.setBorder(
                        BorderFactory.createEmptyBorder(25,25,25,25));
         textPanel.setLayout(new BorderLayout());
         textPanel.add(humiditySP, BorderLayout.CENTER);
         this.addDateToPanel(null,
                             (JPanel)humidityPanel.getComponent(2),1);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(humidityTab);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void graphPressure(java.util.List<WeatherData> data){
      try{
         int pressureTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("PRESSURE")){
               pressureTab = i;
            }
         }
         jtp.setSelectedIndex(pressureTab);
         JPanel pressurePanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel     = (JPanel)pressurePanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new WeatherPanel(data, this.pressureUnits),
                                                 BorderLayout.CENTER);
         this.addDateToPanel(data.get(0),
                             (JPanel)pressurePanel.getComponent(2),2);
         jtp.setSelectedIndex(pressureTab + 1);
         jtp.setSelectedIndex(pressureTab);

      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void printPressure(java.util.List<WeatherData> data){
      String pressure=this.grabMeasureString(data,this.pressureUnits);
      try{
         JTextArea pressureArea = new JTextArea(pressure);
         pressureArea.setEditable(false);
         JScrollPane pressureSP = new JScrollPane(pressureArea);
         int pressureTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("PRESSURE")){
               pressureTab = i;
            }
         }
         jtp.setSelectedIndex(pressureTab);
         JPanel pressurePanel = (JPanel)jtp.getSelectedComponent();
         JPanel textPanel     = (JPanel)pressurePanel.getComponent(0);
         if(textPanel.getComponentCount() > 0){
            textPanel.removeAll();
         }
         textPanel.setBorder(
                        BorderFactory.createEmptyBorder(25,25,25,25));
         textPanel.setLayout(new BorderLayout());
         textPanel.add(pressureSP, BorderLayout.CENTER);
         this.addDateToPanel(null,
                             (JPanel)pressurePanel.getComponent(2),2);
         jtp.setSelectedIndex(0);
         jtp.setSelectedIndex(pressureTab);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   /**/
   private void graphTemperature(java.util.List<WeatherData> data){
      try{
         int tempTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("TEMPERATURE")){
               tempTab = i;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         JPanel drawPanel = (JPanel)tempPanel.getComponent(0);
         if(drawPanel.getComponentCount() > 0){
            drawPanel.removeAll();
         }
         drawPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
         drawPanel.setLayout(new BorderLayout());
         drawPanel.add(new WeatherPanel(data, this.temperatureUnits),
                                                 BorderLayout.CENTER);
         this.addDateToPanel(data.get(0),
                             (JPanel)tempPanel.getComponent(2),2);
         jtp.setSelectedIndex(tempTab + 1);
         jtp.setSelectedIndex(tempTab);
      }
      catch(NullPointerException npe){npe.printStackTrace();}
   }

   /**/
   private void printTemperature(java.util.List<WeatherData> data){
      String temp=this.grabMeasureString(data,this.temperatureUnits);
      try{
         JTextArea tempArea = new JTextArea(temp);
         tempArea.setEditable(false);
         JScrollPane tempSP = new JScrollPane(tempArea);
         int tempTab = -1;
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(1);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).toUpperCase().equals("TEMPERATURE")){
               tempTab = i;
            }
         }
         jtp.setSelectedIndex(tempTab);
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         JPanel textPanel = (JPanel)tempPanel.getComponent(0);
         if(textPanel.getComponentCount() > 0){
            textPanel.removeAll();
         }
         textPanel.setBorder(
                        BorderFactory.createEmptyBorder(25,25,25,25));
         textPanel.setLayout(new BorderLayout());
         textPanel.add(tempSP, BorderLayout.CENTER);
         this.addDateToPanel(null,(JPanel)tempPanel.getComponent(2),2);
         jtp.setSelectedIndex(tempTab + 1);
         jtp.setSelectedIndex(tempTab);
      }
      catch(NullPointerException npe){npe.printStackTrace();}
   }

   /**/
   private void pressCurrentRefresh(){
      JTabbedPane jtp =
                (JTabbedPane)this.getContentPane().getComponent(1);
      JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
      JPanel nextPanel = (JPanel)tempPanel.getComponent(1);
      ((JButton)nextPanel.getComponent(0)).doClick();
   }

   /**/
   private void setUpCurrentDate(){
      Calendar now = Calendar.getInstance();
      int month    = now.get(Calendar.MONTH);
      int day      = now.get(Calendar.DAY_OF_MONTH) - 1;
      int year     = now.get(Calendar.YEAR) - 2017;
      this._monthCB.setSelectedIndex(month);
      this._dayCB.setSelectedIndex(day);
      this._yearCB.setSelectedIndex(year);
   }

   /**/
   private void setUpDateComboBoxes(){
      this._monthCB = new JComboBox();
      this._monthCB.setActionCommand("Month Combo Box");
      this._monthCB.setName("Month");
      for(int i = 0; i < this.MONTHS.length; i++){
         this._monthCB.addItem(this.MONTHS[i].trim());
      }
      this._dayCB = new JComboBox();
      this._dayCB.setActionCommand("Day Combo Box");
      this._dayCB.setName("Day");
      for(int i = 0; i < this.DAYS.length; i++){
         this._dayCB.addItem(this.DAYS[i].trim());
      }
      this._yearCB = new JComboBox();
      this._yearCB.setActionCommand("Year Combo Box");
      this._yearCB.setName("Year");
      for(int i = 0; i < this.YEARS.length; i++){
         this._yearCB.addItem(this.YEARS[i].trim());
      }
      this._monthCB.addActionListener(this._controller);
      //this._monthCB.addItemListener(this._controller);
      //this._monthCB.addKeyListener(this._controller);
      this._dayCB.addActionListener(this._controller);
      //this._dayCB.addItemListener(this._controller);
      //this._dayCB.addKeyListener(this._controller);
      this._yearCB.addActionListener(this._controller);
      //this._yearCB.addItemListener(this._controller);
      //this._yearCB.addKeyListener(this._controller);
      //Set the current date in the combo box
      this.setUpCurrentDate();
   }

   /**/
   private JPanel setUpDewPointPanel(){
      JPanel dewPointPanel = new JPanel();
      dewPointPanel.setLayout(new BorderLayout());
      JPanel centerPanel = new JPanel();
      JLabel dpLabel = new JLabel("Dew Point");
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      centerPanel.add(dpLabel);
      dewPointPanel.add(centerPanel, BorderLayout.CENTER);
      dewPointPanel.add(this.setUpDewpointSouthPanel(),
                                                  BorderLayout.SOUTH);
      dewPointPanel.add(this.setUpDewpointNorthPanel(),
                                                  BorderLayout.NORTH);
      return dewPointPanel;
   }

   /**/
   private JPanel setUpDewpointNorthPanel(){
      JPanel panel                = new JPanel();
      ButtonGroup dewpointGroup   = new ButtonGroup();
      ButtonGroup dataGroup       = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JPanel dewpointPanel = new JPanel();
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("DPCelsius");
      dewpointGroup.add(celsius);
      dewpointPanel.add(celsius);
      celsius.addItemListener(this._controller);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("DPFahrenheit");
      dewpointGroup.add(fahrenheit);
      dewpointPanel.add(fahrenheit);
      fahrenheit.addItemListener(this._controller);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("DPKelvin");
      dewpointGroup.add(kelvin);
      dewpointPanel.add(kelvin);
      kelvin.addItemListener(this._controller);
      panel.add(dewpointPanel);

      JPanel dataPanel = new JPanel();
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("DPGraph");
      dataGroup.add(graph);
      dataPanel.add(graph);
      graph.addItemListener(this._controller);
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("DPData");
      dataGroup.add(data);
      dataPanel.add(data);
      data.addItemListener(this._controller);
      panel.add(dataPanel);

      return panel;
   }

   /**/
   private JPanel setUpDewpointSouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("DewpointRefresh");
      refresh.addActionListener(this._controller);
      refresh.addKeyListener(this._controller);
      panel.add(refresh);

      JButton save = new JButton("Save");
      save.setActionCommand("DewpointSave");
      save.addActionListener(this._controller);
      save.addKeyListener(this._controller);
      if(this.dewpointDisplay == GRAPH){
         save.setEnabled(false);
      }
      else{
         save.setEnabled(true);
      }
      this.saveButtonGroup.add(save);
      panel.add(save);

      return panel;
   }

   /**/
   private JPanel setUpHeatIndexPanel(){
      JPanel heatIndexPanel = new JPanel();
      heatIndexPanel.setLayout(new BorderLayout());
      JPanel centerPanel = new JPanel();
      JLabel hiLabel = new JLabel("Heat Index");
      centerPanel.add(hiLabel);
      heatIndexPanel.add(centerPanel, BorderLayout.CENTER);
      heatIndexPanel.add(this.setUpHeatIndexSouthPanel(),
                                                  BorderLayout.SOUTH);
      heatIndexPanel.add(this.setUpHeatIndexNorthPanel(),
                                                  BorderLayout.NORTH);
      return heatIndexPanel;
   }

   /**/
   private JPanel setUpHeatIndexNorthPanel(){
      JPanel panel               = new JPanel();
      ButtonGroup heatIndexGroup = new ButtonGroup();
      ButtonGroup dataGroup      = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JPanel heatIndexPanel = new JPanel();
      JRadioButton celsius  = new JRadioButton("Celsuis", true);
      celsius.setActionCommand("HICelsius");
      heatIndexGroup.add(celsius);
      heatIndexPanel.add(celsius);
      celsius.addItemListener(this._controller);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("HIFahrenheit");
      heatIndexGroup.add(fahrenheit);
      heatIndexPanel.add(fahrenheit);
      fahrenheit.addItemListener(this._controller);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("HIKelvin");
      heatIndexGroup.add(kelvin);
      heatIndexPanel.add(kelvin);
      kelvin.addItemListener(this._controller);
      panel.add(heatIndexPanel);

      JPanel dataPanel = new JPanel();
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("HIGraph");
      dataGroup.add(graph);
      dataPanel.add(graph);
      graph.addItemListener(this._controller);
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("HIData");
      dataGroup.add(data);
      dataPanel.add(data);
      data.addItemListener(this._controller);
      panel.add(dataPanel);

      return panel;
   }

   /**/
   private JPanel setUpHeatIndexSouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("HeatIndexRefresh");
      refresh.addActionListener(this._controller);
      refresh.addKeyListener(this._controller);
      panel.add(refresh);

      JButton save = new JButton("Save");
      save.setActionCommand("HeatIndexSave");
      save.addActionListener(this._controller);
      save.addKeyListener(this._controller);
      if(this.heatIndexDisplay == GRAPH){
         save.setEnabled(false);
      }
      else{
         save.setEnabled(true);
      }
      this.saveButtonGroup.add(save);
      panel.add(save);

      return panel;
   }

   /**/
   private JPanel setUpHumidityPanel(){
      JPanel humidityPanel = new JPanel();
      humidityPanel.setLayout(new BorderLayout());
      JPanel centerPanel = new JPanel();
      JLabel humiLabel = new JLabel("Humidity");
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      centerPanel.add(humiLabel);
      humidityPanel.add(centerPanel, BorderLayout.CENTER);
      humidityPanel.add(this.setUpHumiditySouthPanel(),
                                                  BorderLayout.SOUTH);
      humidityPanel.add(this.setUpHumidityNorthPanel(),
                                                  BorderLayout.NORTH);
      return humidityPanel;
   }

   /**/
   private JPanel setUpHumidityNorthPanel(){
      JPanel panel               = new JPanel();
      ButtonGroup dataGroup      = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JPanel dataPanel = new JPanel();
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("HGraph");
      dataGroup.add(graph);
      dataPanel.add(graph);
      graph.addItemListener(this._controller);
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("HData");
      dataGroup.add(data);
      dataPanel.add(data);
      data.addItemListener(this._controller);

      panel.add(dataPanel);
      return panel;
   }

   /**/
   private JPanel setUpHumiditySouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("HumidityRefresh");
      refresh.addActionListener(this._controller);
      refresh.addKeyListener(this._controller);
      panel.add(refresh);

      JButton save = new JButton("Save");
      save.setActionCommand("HumiditySave");
      save.addActionListener(this._controller);
      save.addKeyListener(this._controller);
      if(this.humidityDisplay == GRAPH){
         save.setEnabled(false);
      }
      else{
         save.setEnabled(true);
      }
      this.saveButtonGroup.add(save);
      panel.add(save);

      return panel;
   }


   /**/
   private JTabbedPane setTabbedPane(){
      JTabbedPane jtp = new JTabbedPane();

      jtp.addTab("Temperature",
                 null,
                 this.setUpTemperaturePanel(),
                 "Viewing Temperature Data");
      jtp.setMnemonicAt(0, KeyEvent.VK_T);
      jtp.addTab("Humidity",
                 null,
                 this.setUpHumidityPanel(),
                 "Viewing Humdity Data");
      jtp.setMnemonicAt(1, KeyEvent.VK_H);
      jtp.addTab("Pressure",
                 null,
                 this.setUpPressurePanel(),
                 "Viewing Barometric Pressure Data");
      jtp.setMnemonicAt(2, KeyEvent.VK_P);
      jtp.addTab("Dew Point",
                 null,
                 this.setUpDewPointPanel(),
                 "Viewing Dew Point Data");
      jtp.setMnemonicAt(3, KeyEvent.VK_D);
      jtp.addTab("Heat Index",
                 null,
                 this.setUpHeatIndexPanel(),
                 "Viewing Heat Index Data");
      jtp.setMnemonicAt(4, KeyEvent.VK_I);

      return jtp;
   }

   /**/
   private void setUpGUI(){
      this.setLayout(new BorderLayout());
      this.setSize(WIDTH,HEIGHT);
      this.setResizable(false);
      this.saveButtonGroup = new ButtonGroup();
      JPanel northPanel = this.setUpNorthPanel();
      JTabbedPane jtp = this.setTabbedPane();
      this.getContentPane().add(northPanel, BorderLayout.NORTH);
      this.getContentPane().add(jtp, BorderLayout.CENTER);
      this.setVisible(true);
   }

   /**/
   private JPanel setUpNorthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

      JLabel addLabel=new JLabel("Address:  ",SwingConstants.RIGHT);
      this._address = new JTextField("Enter Address", 16);
      this._address.setName("Address");
      this._address.addActionListener(this._controller);
      this._address.addKeyListener(this._controller);
      JLabel gap = new JLabel("                  ");
      this._address.requestFocus();
      this._address.selectAll();
      this.setUpDateComboBoxes();

      panel.add(addLabel);
      panel.add(this._address);
      panel.add(gap);
      panel.add(this._monthCB);
      panel.add(this._dayCB);
      panel.add(this._yearCB);
      return panel;
   }

   /**/
   private JPanel setUpPressurePanel(){
      JPanel pressurePanel = new JPanel();
      pressurePanel.setLayout(new BorderLayout());
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      JLabel presLabel = new JLabel("Pressure");
      centerPanel.add(presLabel);
      pressurePanel.add(centerPanel, BorderLayout.CENTER);
      pressurePanel.add(this.setUpPressureSouthPanel(), BorderLayout.SOUTH);
      pressurePanel.add(this.setUpPressureNorthPanel(), BorderLayout.NORTH);
      return pressurePanel;
   }

   /**/
   private JPanel setUpPressureNorthPanel(){
      JPanel panel              = new JPanel();
      ButtonGroup pressureGroup = new ButtonGroup();
      ButtonGroup dataGroup     = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JPanel pressurePanel = new JPanel();
      JRadioButton mms  = new JRadioButton("millimeters Hg");
      mms.setActionCommand("mms");
      pressureGroup.add(mms);
      pressurePanel.add(mms);
      mms.addItemListener(this._controller);
      JRadioButton inches = new JRadioButton("inches Hg");
      inches.setActionCommand("inches") ;
      pressureGroup.add(inches);
      pressurePanel.add(inches);
      inches.addItemListener(this._controller);
      JRadioButton millibars = new JRadioButton("millibars", true);
      millibars.setActionCommand("millibars");
      pressureGroup.add(millibars);
      pressurePanel.add(millibars);
      millibars.addItemListener(this._controller);
      panel.add(pressurePanel);

      JPanel dataPanel = new JPanel();
      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("PGraph");
      dataGroup.add(graph);
      dataPanel.add(graph);
      graph.addItemListener(this._controller);
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("PData");
      dataGroup.add(data);
      dataPanel.add(data);
      data.addItemListener(this._controller);
      panel.add(dataPanel);

      return panel;
   }

   /**/
   private JPanel setUpPressureSouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Pressure Refresh");
      refresh.addActionListener(this._controller);
      refresh.addKeyListener(this._controller);
      panel.add(refresh);

      JButton save = new JButton("Save");
      save.setActionCommand("Pressure Save");
      save.addActionListener(this._controller);
      save.addKeyListener(this._controller);
      if(this.pressureDisplay == GRAPH){
         save.setEnabled(false);
      }
      else{
         save.setEnabled(true);
      }
      this.saveButtonGroup.add(save);
      panel.add(save);

      return panel;
   }

   /**/
   private JPanel setUpTemperaturePanel(){
      JPanel temperaturePanel = new JPanel();
      temperaturePanel.setLayout(new BorderLayout());
      JPanel centerPanel = new JPanel();
      JLabel tempLabel = new JLabel("Temperature");
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      centerPanel.add(tempLabel);
      temperaturePanel.add(centerPanel, BorderLayout.CENTER);
      temperaturePanel.add(this.setUpTemperatureSouthPanel(),
                                                  BorderLayout.SOUTH);
      temperaturePanel.add(this.setUpTemperatureNorthPanel(),
                                                  BorderLayout.NORTH);
      return temperaturePanel;
   }

   /**/
   private JPanel setUpTemperatureNorthPanel(){
      JPanel panel                 = new JPanel();
      ButtonGroup temperatureGroup = new ButtonGroup();
      ButtonGroup dataGroup        = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JPanel temperaturePanel = new JPanel();
      JRadioButton celsius    = new JRadioButton("Celsius", true);
      celsius.setActionCommand("TCelsius");
      temperatureGroup.add(celsius);
      temperaturePanel.add(celsius);
      celsius.addItemListener(this._controller);
      //Add another listener...
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("TFahrenheit");
      temperatureGroup.add(fahrenheit);
      temperaturePanel.add(fahrenheit);
      fahrenheit.addItemListener(this._controller);
      //Add another listener...
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("TKelvin");
      temperatureGroup.add(kelvin);
      temperaturePanel.add(kelvin);
      kelvin.addItemListener(this._controller);
      //Add another listener...

      panel.add(temperaturePanel);

      JPanel dataPanel = new JPanel();

      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("TGraph");
      dataGroup.add(graph);
      dataPanel.add(graph);
      graph.addItemListener(this._controller);
      //Add another Listener...
      JRadioButton data = new JRadioButton("Data");
      data.setActionCommand("TData");
      dataGroup.add(data);
      dataPanel.add(data);
      data.addItemListener(this._controller);
      //Add another Listener...
      panel.add(dataPanel);
      return panel;
   }

   /**/
   private JPanel setUpTemperatureSouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("TemperatureRefresh");
      refresh.addActionListener(this._controller);
      refresh.addKeyListener(this._controller);

      panel.add(refresh);

      JButton save = new JButton("Save");
      save.setActionCommand("TemperatureSave");
      save.addActionListener(this._controller);
      save.addKeyListener(this._controller);
      if(this.tempDisplay == GRAPH){
         save.setEnabled(false);
      }
      else{
         save.setEnabled(true);
      }
      this.saveButtonGroup.add(save);
      panel.add(save);

      return panel;
   }
}
//////////////////////////////////////////////////////////////////////
