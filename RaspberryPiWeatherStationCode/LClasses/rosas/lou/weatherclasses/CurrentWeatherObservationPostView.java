/*
Copyright 2022 Lou Rosas

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
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.*;

public class CurrentWeatherObservationPostView
extends CurrentWeatherView implements CurrentWeatherDataObserver{
   private static final short WIDTH         = 750;
   private static final short HEIGHT        = 700;
   private static final short TOTAL_PANELS  = 5;

   private WeatherData _humidityData            = null;
   private WeatherData _temperatureData         = null;
   private WeatherData _pressureData            = null;
   private WeatherData _dewpointData            = null;
   private CurrentWeatherController _controller = null;

   private int dewpoint_minutes                 = -1;
   private int dewpoint_seconds                 = -1;
   private int humidity_minutes                 = -1;
   private int humidity_seconds                 = -1;
   private int pressure_minutes                 = -1;
   private int pressure_seconds                 = -1;
   private int temp_minutes                     = -1;
   private int temp_seconds                     = -1;

   //private CurrentWeatherObservationPostController _controller=null;
   //
   //Lots more stuff to add
   //
   //

   /////////////////////////Constructors//////////////////////////////
   /*
   */
   public CurrentWeatherObservationPostView(){
      this("");
   }

   /**/
   public CurrentWeatherObservationPostView(String title){
      super(title);
      this._controller=new CurrentWeatherObservationPostController();
      this._controller.addView(this);
      this.setUpGUI();
   }

   /**/
   public CurrentWeatherObservationPostView
   (
      String title,
      CurrentWeatherController controller
   ){
      super(title);
      this._controller = controller;
      this._controller.addView(this);
      this.setUpGUI();
   }

   ////////////////////Insatance Methods//////////////////////////////
   ////////////////////Public Methods/////////////////////////////////
   /**/
   public void updateTheViews(String idx){
      try{
         JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
         int selectedIndex = jtp.getSelectedIndex();
         int index = Integer.parseInt(idx);
         if(selectedIndex == index){
            if(index > 0){
               jtp.setSelectedIndex(index - 1);
            }
            else{
               jtp.setSelectedIndex(index + 1);
            }
            jtp.setSelectedIndex(index);
         }
      }
      catch(NumberFormatException nfe){}
   }
   /////////////////////Protected Methods/////////////////////////////
   //////////////////////Private Methods//////////////////////////////
   /**/
   private void setUpGUI(){
      this.setLayout(new BorderLayout());
      this.setSize(WIDTH, HEIGHT);
      this.setResizable(false);
      JTabbedPane jtp = this.setUpTabbedPane();
      JPanel southPanel = this.setUpSouthPanel();
      this.getContentPane().add(jtp, BorderLayout.CENTER);
      this.getContentPane().add(southPanel, BorderLayout.SOUTH);

      this.setVisible(true);
   }

   /**/
   private JPanel setUpSouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Refresh");
      refresh.setMnemonic(KeyEvent.VK_R);
      refresh.addActionListener(this._controller);
      refresh.addKeyListener(this._controller);
      panel.add(refresh);

      JButton save = new JButton("Save");
      save.setActionCommand("Save");
      save.setMnemonic(KeyEvent.VK_S);
      save.addActionListener(this._controller);
      save.addKeyListener(this._controller);
      panel.add(save);
      
      JButton requestTime = new JButton("Request Time");
      requestTime.setActionCommand("Request");
      requestTime.addActionListener(this._controller);
      requestTime.addKeyListener(this._controller);
      panel.add(requestTime);

      return panel;
   }

   /**/
   private JTabbedPane setUpTabbedPane(){
      JTabbedPane jtp = new JTabbedPane();

      jtp.addTab("Temperature",
                 null,
                 //this.setUpTemperaturePanel(),
                 new GenericThermalPanel(this._controller,0),
                 "Current Temperature");
      jtp.setMnemonicAt(0, KeyEvent.VK_T);
      jtp.addTab("Humidity",
                  null,
                  //this.setUpHumidityPanel(),
                  new GenericHumidityPanel(this._controller,1),
                  "Current Humidity");
      jtp.setMnemonicAt(1, KeyEvent.VK_H);
      jtp.addTab("Pressure",
                  null,
                  //this.setUpPressurePanel(),
                  new GenericPressurePanel(this._controller,2),
                  "Current Pressure");
      jtp.setMnemonicAt(2,KeyEvent.VK_P);
      jtp.addTab("Dew Point",
                 null,
                 //this.setUpDewPointPanel(),
                 new GenericThermalPanel(this._controller,3),
                 "Current Dewpoint");
      jtp.setMnemonicAt(3,KeyEvent.VK_D);
      jtp.addTab("Heat Index",
                  null,
                  new GenericThermalPanel(this._controller,4),
                  "Current Heat Index");
      jtp.setMnemonicAt(4,KeyEvent.VK_I);

      //Set on the Temperature tab
      jtp.setSelectedIndex(0);

      return jtp;
   }

   /**/
   private void updateDewPointPane(WeatherData data){
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(3);
      GenericThermalPanel p=(GenericThermalPanel)jtp.getComponent(3);
      p.update(data);
      //this.updateTheViews("3");
      jtp.setSelectedIndex(0);
      jtp.setSelectedIndex(index);
   }

   /*
   This is the way it will be implemented--probably should say more
   than what I just said
   */
   private void updateHeatIndexPane(WeatherData data){
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(4);
      GenericThermalPanel panel =
                             (GenericThermalPanel)jtp.getComponent(4);

      panel.update(data);
      //this.updateTheViews("4")
      jtp.setSelectedIndex(0);
      jtp.setSelectedIndex(index);
   }

   /**/
   private void updateHumidityPane(WeatherData data){
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(1);
      GenericHumidityPanel panel =
                            (GenericHumidityPanel)jtp.getComponent(1);
      panel.update(data);
      //this.updateTheViews("1");
      jtp.setSelectedIndex(0);
      jtp.setSelectedIndex(index);
   }

   /**/
   private void updatePressurePane(WeatherData data){
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(2);
      GenericPressurePanel panel =
                            (GenericPressurePanel)jtp.getComponent(2);
      panel.update(data);
      //this.updateTheViews("2");
      jtp.setSelectedIndex(0);
      jtp.setSelectedIndex(index);
   }

   /**/
   private void updateTemperaturePane(WeatherData data){
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(0);
      GenericThermalPanel p=(GenericThermalPanel)jtp.getComponent(0);
      p.update(data);
      //this.updateTheViews("0");
      jtp.setSelectedIndex(1);
      jtp.setSelectedIndex(0);
      jtp.setSelectedIndex(index);
   }

   ///////////////////Interface Implementation////////////////////////
   /**/
   public void receiveMessage(String message){}

   /**/
   public void receiveError(String error){
      //System.out.println(error);
      JOptionPane.showMessageDialog(this,
                                    error,
                                    "Received Error",
                                    JOptionPane.ERROR_MESSAGE);
   }

   /**/
   public void updateDewpoint(WeatherData data){
      this.updateDewPointPane(data);
   }

   /**/
   public void updateHeatindex(WeatherData data){
      this.updateHeatIndexPane(data);
   }

   /**/
   public void updateHumidity(WeatherData data){
      this.updateHumidityPane(data);
   }

   /**/
   public void updatePressure(WeatherData data){
      this.updatePressurePane(data);
   }

   /**/
   public void updateTemperature(WeatherData data){
      this.updateTemperaturePane(data);
   }
}
