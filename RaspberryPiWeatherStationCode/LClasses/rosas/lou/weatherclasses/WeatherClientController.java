/********************************************************************
//******************************************************************
//Weather Client Controller Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import rosas.lou.weatherclasses.*;

public class WeatherClientController implements
ActionListener, KeyListener, ItemListener{
   WeatherClient     client;
   WeatherClientView view;

   {
      client = null;
      view   = null;
   }
   ////////////////////////Construtors///////////////////////////////
   /**
   **/
   public WeatherClientController
   (
      WeatherClientView view,
      WeatherClient client
   ){
      this.client = client;
      this.view   = view;
   }

   ///////////////////////Public Methods/////////////////////////////
   /**
   Implementation of the actionPerformed method from the
   ActionListener Interface
   **/
   public void actionPerformed(ActionEvent e){
      Object o = e.getSource();
      if(o instanceof JComboBox){
         int mask = java.awt.event.InputEvent.BUTTON1_MASK;
         if(e.getModifiers() == mask){
            //Needing to do this for the sake of investigation
            System.out.println(o);
            System.out.println();
            System.out.println(e);
            System.out.println(((JComboBox)o).getSelectedItem());
            this.handleJComboBox((JComboBox)o);
         }
      }
      else if(o instanceof JButton){
         JButton jb = (JButton)o;
         String command = jb.getActionCommand();
         if(command.equals("Temp Refresh")){
            ViewState state = this.view.requestTemperatureState();
            this.client.requestTemperatureData(state);
         }
         else if(command.equals("Humidity Refresh")){
            ViewState state = this.view.requestHumidityState();
            this.client.requestHumidityData(state);
         }
         else if(command.equals("P Refresh")){
            ViewState state = this.view.requestPressureState();
            this.client.requestPressureData(state);
         }
         else if(command.equals("DP Refresh")){
            ViewState state = this.view.requestDewpointState();
            this.client.requestDewpointData(state);
         }
         else if(command.equals("HI Refresh")){
            ViewState state = this.view.requestHeatIndexState();
            this.client.requestHeatIndexData(state);
         }
         else if(command.equals("Temp Save")){
            this.view.requestTemperatureJFileChooser();
         }
         else if(command.equals("Humidity Save")){
            this.view.requestHumidityJFileChooser();
         }
         else if(command.equals("P Save")){
            this.view.requestPressureJFileChooser();
         }
         else if(command.equals("DP Save")){
            this.view.requestDewpointJFileChooser();
         }
         else if(command.equals("HI Save")){
            this.view.requestHeatIndexJFileChooser();
         }
      }
      else if(o instanceof JFileChooser){
         ViewState state;
         JFileChooser jfc = (JFileChooser)o;
         if(e.getActionCommand().equals("ApproveSelection")){
            String from = jfc.getApproveButtonToolTipText();
            if(from.toUpperCase().contains("TEMPERATURE")){
               this.handleTemperatureSave(jfc);
            }
            else if(from.toUpperCase().contains("HUMIDITY")){
               this.handleHumiditySave(jfc);
            }
            else if(from.toUpperCase().contains("PRESSURE")){
               this.handlePressureSave(jfc);
            }
            else if(from.toUpperCase().contains("DEWPOINT")){
               this.handleDewpointSave(jfc);
            }
            else if(from.toUpperCase().contains("HEAT INDEX")){
               this.handleHeatIndexSave(jfc);
            }
         }
      }
   }

   /**
   Implementation of the itemStateChanged method of the ItemListener
   Inteface
   **/
   public void itemStateChanged(ItemEvent e){
      Object o = e.getItem();
      if(o instanceof JRadioButton){
         this.handleJRadioButton((JRadioButton)o);
      }
   }

   /**
   Implementation of the keyPressed method of the KeyListener
   Interface
   **/
   public void keyPressed(KeyEvent k){
      Object o = k.getSource();
      int code = k.getKeyCode();
      if(o instanceof JComboBox && code == KeyEvent.VK_ENTER){
         this.handleJComboBox((JComboBox)o);
      }
      else if(o instanceof JButton && code == KeyEvent.VK_ENTER){
         ((JButton)o).doClick();
      }
   }

   /**
   Implementation of the keyReleased method of the KeyListener
   Interface
   **/
   public void keyReleased(KeyEvent k){}

   /**
   Implementation of the keyTyped method of the KeyListner
   Interface
   **/
   public void keyTyped(KeyEvent k){}

   /**
   **/
   public void requestMissionData(){
      this.client.requestMissionData();
   }

   ///////////////////////Private Methods////////////////////////////
   /**
   **/
   private void handleJComboBox(JComboBox jcb){
      String command = jcb.getActionCommand(); 
      if(command.equals("Humidity Combo Box")){
         ViewState state = this.view.requestHumidityState();
         this.client.requestHumidityData(state);
      }
      else if(command.equals("Temperature Combo Box")){
         ViewState state = this.view.requestTemperatureState();
         this.client.requestTemperatureData(state);
      }
      else if(command.equals("Pressure Combo Box")){
         ViewState state = this.view.requestPressureState();
         this.client.requestPressureData(state);
      }
      else if(command.equals("Dewpoint Combo Box")){
         ViewState state = this.view.requestDewpointState();
         this.client.requestDewpointData(state);
      }
      else if(command.equals("Heat Index Combo Box")){
         ViewState state = this.view.requestHeatIndexState();
         this.client.requestHeatIndexData(state);
      }
   }
   
   /**
   **/
   private void handleJRadioButton(JRadioButton jrb){
      if(jrb.isSelected()){
         String command = jrb.getActionCommand();
         if(command.equals("TCelsius") ||
            command.equals("TFahrenheit") ||
            command.equals("TKelvin")){
            ViewState state = this.view.requestTemperatureState();
            this.client.requestTemperatureData(state);
         }
         else if(command.equals("PMetric")  ||
                 command.equals("PEnglish") ||
                 command.equals("PAbsolute")){
            ViewState state = this.view.requestPressureState();
            this.client.requestPressureData(state);
         }
         else if(command.equals("DPCelsius")    ||
                 command.equals("DPFahrenheit") ||
                 command.equals("DPKelvin")){
            ViewState state = this.view.requestDewpointState();
            this.client.requestDewpointData(state);
         }
         else if(command.equals("HICelsius")    ||
                 command.equals("HIFahrenheit") ||
                 command.equals("HIKelvin")){
            ViewState state = this.view.requestHeatIndexState();
            this.client.requestHeatIndexData(state);
         }
      }
   }

   /**
   **/
   private void handleDewpointSave(JFileChooser jfc){
      File dewpointFile = jfc.getSelectedFile();
      if(dewpointFile.isDirectory()){
         this.view.alertDewpointSaveError("Directory");
         this.view.requestDewpointJFileChooser();
      }
      else if(dewpointFile.exists()){
         int overwrite = this.view.alertDewpointSaveError("exists");
         if(overwrite == JOptionPane.YES_OPTION){
            //Save Data
            this.client.saveDewpointData(dewpointFile);
         }
         else{
            //Start Over
            this.view.requestDewpointJFileChooser();
         }
      }
      else{
         //Save Data
         this.client.saveDewpointData(dewpointFile);
      }
   }

   /**
   **/
   private void handleHeatIndexSave(JFileChooser jfc){
      File hiFile = jfc.getSelectedFile();
      if(hiFile.isDirectory()){
         this.view.alertHeatIndexSaveError("Directory");
         this.view.requestHeatIndexJFileChooser();
      }
      else if(hiFile.exists()){
         //That is what is acutally happening:  appending the file
         int append = this.view.alertHeatIndexSaveError("exists");
         if(append == JOptionPane.YES_OPTION){
            //Save Data
            this.client.saveHeatIndexData(hiFile);
         }
         else{
            //Start Over
            this.view.requestHeatIndexJFileChooser();
         }
      }
      else{
         this.client.saveHeatIndexData(hiFile);
      }
   }

   /**
   **/
   private void handleHumiditySave(JFileChooser jfc){
      File humidityFile = jfc.getSelectedFile();
      if(humidityFile.isDirectory()){
         this.view.alertHumiditySaveError("Directory");
         this.view.requestHumidityJFileChooser();
      }
      else if(humidityFile.exists()){
         int overwrite = this.view.alertHumiditySaveError("exists");
         if(overwrite == JOptionPane.YES_OPTION){
            //Save Data
            this.client.saveHumidityData(humidityFile);
         }
         else{
            //Start Over
            this.view.requestHumidityJFileChooser();
         }
      }
      else{
         //Save Data
         this.client.saveHumidityData(humidityFile);
      }
   }

   //
   //
   //
   private void handlePressureSave(JFileChooser jfc){
      File pressureFile = jfc.getSelectedFile();
      if(pressureFile.isDirectory()){
         this.view.alertPressureSaveError("Directory");
         this.view.requestPressureJFileChooser(); 
      }
      else if(pressureFile.exists()){
         int overwrite = this.view.alertPressureSaveError("exists");
         if(overwrite == JOptionPane.YES_OPTION){
            //Save Data
            this.client.savePressureData(pressureFile);
         }
         else{
            //Start Over
            this.view.requestPressureJFileChooser();
         }
      }
      else{
         //Save Data
         this.client.savePressureData(pressureFile);
      }
   }

   /**
   **/
   private void handleTemperatureSave(JFileChooser jfc){
      File tempFile = jfc.getSelectedFile();
      if(tempFile.isDirectory()){
         //Alert the user of the issue
         this.view.alertTemperatureSaveError("Directory");
         this.view.requestTemperatureJFileChooser();
      }
      else if(tempFile.exists()){
         int overwrite =
                       this.view.alertTemperatureSaveError("exists");
         if(overwrite == JOptionPane.YES_OPTION){
            //Save Data
            this.client.saveTemperatureData(tempFile);
         }
         else{
            //Start over
            this.view.requestTemperatureJFileChooser();
         }
      }
      else if(!tempFile.exists()){
         //Save Data
         this.client.saveTemperatureData(tempFile);
      }
   }
}
