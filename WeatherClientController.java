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
         ViewState state = view.requestHumidityState();
         this.client.requestHumidityData(state);
      }
      if(command.equals("Temperature Combo Box")){
         ViewState state = view.requestTemperatureState();
         this.client.requestTemperatureData(state);
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
            ViewState state = view.requestTemperatureState();
            this.client.requestTemperatureData(state);
         }
      }
   }
}
