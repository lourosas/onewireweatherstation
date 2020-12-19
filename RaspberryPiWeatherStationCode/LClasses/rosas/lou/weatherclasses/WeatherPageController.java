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
import java.util.*;
import java.awt.*;
import java.io.File;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import rosas.lou.weatherclasses.*;

//////////////////////////////////////////////////////////////////////
/*
The complete Controller for the WeatherPage application
*/
//////////////////////////////////////////////////////////////////////
public class WeatherPageController extends
GenericWeatherController implements ActionListener, KeyListener,
ItemListener{
   private WeatherDatabaseClientView _view;
   private WeatherPage               _model;

   {
      _view  = null;
      _model = null;
   };

   /**/
   public WeatherPageController(){}

   /**/
   public WeatherPageController(WeatherDatabaseClientView view){}

   /**/
   public WeatherPageController(
      WeatherDatabaseClientView view,
      WeatherPage model
   ){
      this._view  = view;
      this._model = model;
      this._model.addObserver(this._view);
   }

   /**/
   public void addModel(WeatherPage model){
      this._model = model;
   }

   /**/
   public void addView(WeatherDatabaseClientView view){
      this._view = view;
   }

   /**/
   public void actionPerformed(ActionEvent ae){
      this.handleJButton(ae);
      this.handleJComboBox(ae);
      //this.handleJMenuItem(ae);
      this.handleJTextField(ae);
   }

   /**/
   public void keyPressed(KeyEvent ke){
      try{
         JTextField jt = (JTextField)ke.getSource();
         if(jt.getName().toUpperCase().equals("ADDRESS")){
            char k = ke.getKeyChar();
            int  c = ke.getKeyCode();
            if((k >= '0' && k <= '9') || k == '.' || k == ':' ||
                c == KeyEvent.VK_BACK_SPACE){
               jt.setEditable(true);
            }
            else{
               jt.setEditable(false);
            }
         }
      }
      catch(ClassCastException cce){}
      try{
         int code = ke.getKeyCode();
         if(code == KeyEvent.VK_ENTER){
            JButton button = ((JButton)ke.getSource());
            button.doClick();
         }
      }
      catch(ClassCastException cce){}
   }

   /**/
   public void keyReleased(KeyEvent ke){
      try{
         JTextField jt = ((JTextField)ke.getSource());
         int  c = ke.getKeyCode();
         char k = ke.getKeyChar();
         if(c == KeyEvent.VK_ENTER){
            //System.out.println(jt.getText());
            this._model.setServerAddress(jt.getText());
         }
      }
      catch(ClassCastException cce){}
   }

   /**/
   public void keyTyped(KeyEvent ke){}

   /**/
   public void itemStateChanged(ItemEvent ie){
      try{
         AbstractButton ab = (AbstractButton)ie.getSource();
         if(ab.isSelected()){
            String command = ab.getActionCommand();
            this.handleTemperatureItemSelection(command);
            this.handleHumidityItemSelection(command);
            this.handleHeatIndexItemSelection(command);
            this.handleDewpointItemSelection(command);
            this.handlePressureItemSelection(command);
         }
      }
      catch(ClassCastException cce){}
   }



   /////////////////////////Private Methods///////////////////////////
   /**/
   private void handleJButton(ActionEvent ae){
      try{
         String mo = this._view.getMonth();
         String dy = this._view.getDay();
         String yr = this._view.getYear();

         JButton button = (JButton)ae.getSource();
         this._model.setCalendar(mo,dy,yr);
         if(button.getActionCommand().equals("TemperatureRefresh")){
            this._model.grabTemperatureData(mo,dy,yr);
         }
         else if(button.getActionCommand().equals("TemperatureSave")){
            this.saveTemperature();
         }
         else if(button.getActionCommand().equals("HumidityRefresh")){
            this._model.grabHumidityData(mo,dy,yr);
         }
         else if(button.getActionCommand().equals("HumiditySave")){
            this.saveHumidity();
         }
         else if(button.getActionCommand().equals("Pressure Refresh")){
            this._model.grabPressureData(mo,dy,yr);
         }
         else if(button.getActionCommand().equals("Pressure Save")){
            this.savePressure();
         }
         else if(button.getActionCommand().equals("DewpointRefresh")){
            this._model.grabDewpointData(mo,dy,yr);
         }
         else if(button.getActionCommand().equals("DewpointSave")){
            this.saveDewpoint();
         }
         else if(button.getActionCommand().equals("HeatIndexRefresh")){
            this._model.grabHeatIndexData(mo,dy,yr);
         }
         else if(button.getActionCommand().equals("HeatIndexSave")){
            //this._view.saveHeatIndex();
            this.saveHeatIndex();
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      catch(ClassCastException cce){}
   }

   /**/
   private void handleJComboBox(ActionEvent ae){
      try{
         JComboBox jcb = ((JComboBox)ae.getSource());
         String value = (String)jcb.getSelectedItem();
         if(jcb.getName().toUpperCase().equals("MONTH")){
            this._model.setCalendar(value,null,null);
         }
         else if(jcb.getName().toUpperCase().equals("DAY")){
            this._model.setCalendar(null,value,null);
         }
         else if(jcb.getName().toUpperCase().equals("YEAR")){
            this._model.setCalendar(null,null,value);
         }
      }
      catch(ClassCastException cce){}
      catch(NullPointerException npe){}
   }

   /**/
   private void handleJTextField(ActionEvent ae){}

   /**/
   private void handleDewpointItemSelection(String command){
      if(command.contains("DP")){
         if(command.toUpperCase().equals("DPCELSIUS")){
            this._view.displayDewpoint(Units.METRIC);
         }
         else if(command.toUpperCase().equals("DPFAHRENHEIT")){
            this._view.displayDewpoint(Units.ENGLISH);
         }
         else if(command.toUpperCase().equals("DPKELVIN")){
            this._view.displayDewpoint(Units.ABSOLUTE);
         }
         else if(command.toUpperCase().equals("DPGRAPH")){
            //GRAPH = 0
            this._view.displayDewpoint((short)0);
         }
         else if(command.toUpperCase().equals("DPDATA")){
            //DATA = 1
            this._view.displayDewpoint((short)1);
         }
      }
   }

   /**/
   private void handleHeatIndexItemSelection(String command){
      if(command.contains("HI")){
         if(command.toUpperCase().equals("HICELSIUS")){
            this._view.displayHeatIndex(Units.METRIC);
         }
         else if(command.toUpperCase().equals("HIFAHRENHEIT")){
            this._view.displayHeatIndex(Units.ENGLISH);
         }
         else if(command.toUpperCase().equals("HIKELVIN")){
            this._view.displayHeatIndex(Units.ABSOLUTE);
         }
         else if(command.toUpperCase().equals("HIGRAPH")){
            //GRAPH = 0
            this._view.displayHeatIndex((short)0);
         }
         else if(command.toUpperCase().equals("HIDATA")){
            //DATA  = 1
            this._view.displayHeatIndex((short)1);
         }
      }
   }

   /**/
   private void handleHumidityItemSelection(String command){
      if(command.contains("HG") || command.contains("HD")){
         if(command.toUpperCase().equals("HGRAPH")){
            this._view.displayHumidity((short)0);
         }
         else if(command.toUpperCase().equals("HDATA")){
            this._view.displayHumidity((short)1);
         }
      }
   }

   /**/
   private void handlePressureItemSelection(String command){
      if(command.contains("PG") || command.contains("PD") ||
         command.toUpperCase().equals("MMS")              ||
         command.toUpperCase().equals("INCHES")           ||
         command.toUpperCase().equals("MILLIBARS")){
         if(command.toUpperCase().equals("MMS")){
            this._view.displayPressure(Units.METRIC);
         }
         else if(command.toUpperCase().equals("INCHES")){
            this._view.displayPressure(Units.ENGLISH);
         }
         else if(command.toUpperCase().equals("MILLIBARS")){
            this._view.displayPressure(Units.ABSOLUTE);
         }
         else if(command.toUpperCase().equals("PGRAPH")){
            //GRAPH = 0
            this._view.displayPressure((short)0);
         }
         else if(command.toUpperCase().equals("PDATA")){
            //DATA = 1
            this._view.displayPressure((short)1);
         }
      }
   }

   /**/
   private void handleTemperatureItemSelection(String command){
      if(command.contains("T")){
         if(command.toUpperCase().equals("TCELSIUS")){
            this._view.displayTemperature(Units.METRIC);
         }
         else if(command.toUpperCase().equals("TFAHRENHEIT")){
            this._view.displayTemperature(Units.ENGLISH);
         }
         else if(command.toUpperCase().equals("TKELVIN")){
            this._view.displayTemperature(Units.ABSOLUTE);
         }
         else if(command.toUpperCase().equals("TGRAPH")){
            //GRAPH = 0
            this._view.displayTemperature((short)0);
         }
         else if(command.toUpperCase().equals("TDATA")){
            //DATA = 1
            this._view.displayTemperature((short)1);
         }
      }
   }

   /**/
   private void saveDewpoint(){
      try{
         JFileChooser jfc = new JFileChooser();
         int fileValue = jfc.showSaveDialog(this._view);
         if(fileValue == JFileChooser.APPROVE_OPTION){
            File saveFile = jfc.getSelectedFile();
            Units units = this._view.getDewpointUnits();
            this._model.saveDewpoint(saveFile, units);
         }
      }
      catch(HeadlessException he){}
   }

   /**/
   private void saveHeatIndex(){
      try{
         JFileChooser jfc = new JFileChooser();
         int fileValue = jfc.showSaveDialog(this._view);
         if(fileValue == JFileChooser.APPROVE_OPTION){
            File saveFile = jfc.getSelectedFile();
            Units units = this._view.getHeatIndexUnits();
            this._model.saveHeatIndex(saveFile, units);
         }
      }
      catch(HeadlessException he){}
   }

   /**/
   private void saveHumidity(){
      try{
         JFileChooser jfc = new JFileChooser();
         int fileValue = jfc.showSaveDialog(this._view);
         if(fileValue == JFileChooser.APPROVE_OPTION){
            File saveFile = jfc.getSelectedFile();
            this._model.saveHumidity(saveFile);
         }
      }
      catch(HeadlessException he){}
   }

   /**/
   private void savePressure(){
      try{
         JFileChooser jfc = new JFileChooser();
         int fileValue = jfc.showSaveDialog(this._view);
         if(fileValue == JFileChooser.APPROVE_OPTION){
            File saveFile = jfc.getSelectedFile();
            Units units = this._view.getPressureUnits();
            this._model.savePressure(saveFile, units);
         }
      }
      catch(HeadlessException he){}
   }

   /**/
   private void saveTemperature(){
      try{
         JFileChooser jfc = new JFileChooser();
         int fileValue = jfc.showSaveDialog(this._view);
         if(fileValue == JFileChooser.APPROVE_OPTION){
            File saveFile = jfc.getSelectedFile();
            Units units = this._view.getTemperatureUnits();
            this._model.saveTemperature(saveFile, units);
         }
      }
      catch(HeadlessException he){}
   }
}
