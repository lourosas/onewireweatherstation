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
   public void actionPerformed(ActionEvent e){}

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
   public void keyPressed(KeyEvent k){}

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
   private void handleJRadioButton(JRadioButton jrb){
      if(jrb.isSelected()){
         System.out.println(jrb.getActionCommand());
      }
   }
}
