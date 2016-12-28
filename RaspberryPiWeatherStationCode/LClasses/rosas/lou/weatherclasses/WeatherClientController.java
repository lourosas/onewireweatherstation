/********************************************************************
<GNU Stuff to go here>
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
      }
      else if(o instanceof JFileChooser){
         ViewState state;
         JFileChooser jfc = (JFileChooser)o;
         if(e.getActionCommand().equals("ApproveSelection")){
            String from = jfc.getApproveButtonToolTipText();
            if(from.toUpperCase().contains("TEMPERATURE")){
               this.handleTemperatureSave(jfc);
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
      System.out.println(command);
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
   private void handleTemperatureSave(JFileChooser jfc){
      File tempFile = jfc.getSelectedFile();
      if(tempFile.isDirectory()){
         //Alert the user of the issue
         this.view.alertTemperatureSaveError("Directory");
      }
      else if(tempFile.exists()){
         int overwrite =
                       this.view.alertTemperatureSaveError("exists");
         if(overwrite == JOptionPane.YES_OPTION){
            //Save Data
         }
         else{
            //Start over
            this.view.requestTemperatureJFileChooser();
         }
      }
      else if(!tempFile.exists()){
         System.out.println("NEW FILE!!!");
      }
   }
}
