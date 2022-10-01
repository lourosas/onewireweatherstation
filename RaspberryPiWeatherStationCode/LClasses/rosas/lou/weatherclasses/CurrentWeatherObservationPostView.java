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
import rosas.lou.lgraphics.WeatherPanel;

public class CurrentWeatherObservationPostView
extends CurrentWeatherView implements CurrentWeatherDataObserver
{
   private static final short WIDTH         = 750;
   private static final short HEIGHT        = 700;
   private static final short TOTAL_PANELS  = 5;


   private CurrentWeatherController _controller = null;
   private int temp_minutes = -1;
   private int temp_seconds = -1;

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

      return panel;
   }

   /**/
   private JTabbedPane setUpTabbedPane(){
      JTabbedPane jtp = new JTabbedPane();

      jtp.addTab("Temperature",
                 null,
                 this.setUpTemperaturePanel(),
                 "Current Temperature Data");
      jtp.setMnemonicAt(0, KeyEvent.VK_T);

      //Set on the Temperature tab
      jtp.setSelectedIndex(0);

      return jtp;
   }

   /**/
   private JPanel setUpTemperatureCenterPanel(){
      JPanel centerPanel = new JPanel();
      JLabel tempLabel = new JLabel("Temperature");
      centerPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      centerPanel.add(tempLabel);
      return centerPanel;
   }

   /**/
   private JPanel setUpTemperatureNorthPanel(){
      JPanel panel                 = new JPanel();
      ButtonGroup temperatureGroup = new ButtonGroup();
      ButtonGroup graphGroup       = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());
      panel.setLayout(new GridLayout(2,1));

      JPanel topPanel = new JPanel();
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("TempC");
      temperatureGroup.add(celsius);
      celsius.addItemListener(this._controller);
      topPanel.add(celsius);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("TempF");
      temperatureGroup.add(fahrenheit);
      fahrenheit.addItemListener(this._controller);
      topPanel.add(fahrenheit);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("TempK");
      temperatureGroup.add(kelvin);
      kelvin.addItemListener(this._controller);
      topPanel.add(kelvin);
      panel.add(topPanel);

      JPanel bottomPanel = new JPanel();
      JRadioButton analog = new JRadioButton("Analog");
      analog.setActionCommand("TempAnalog");
      graphGroup.add(analog);
      analog.addItemListener(this._controller);
      bottomPanel.add(analog);
      JRadioButton digital = new JRadioButton("Digital", true);
      digital.setActionCommand("TempDigital");
      graphGroup.add(digital);
      digital.addItemListener(this._controller);
      bottomPanel.add(digital);

      panel.add(bottomPanel);
      return panel;
   }

   /**/
   private JPanel setUpTemperaturePanel(){
      JPanel temperaturePanel = new JPanel();
      temperaturePanel.setLayout(new BorderLayout());
      temperaturePanel.add(this.setUpTemperatureNorthPanel(),
                                                  BorderLayout.NORTH);
      //Set the Center Panel next
      temperaturePanel.add(this.setUpTemperatureCenterPanel(),
                                                 BorderLayout.CENTER);

      return temperaturePanel;
   }

   ///////////////////Interface Implementation////////////////////////
   /**/
   public void updateTemperature(WeatherData data){
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      JPanel panel = (JPanel)jtp.getComponent(0);
      JPanel topPanel = (JPanel)panel.getComponent(0);
      JPanel unitsPanel = (JPanel)topPanel.getComponent(0);
      JPanel displayPanel = (JPanel)topPanel.getComponent(1);
      Calendar cal = data.calendar();
      if(temp_minutes != data.calendar().get(Calendar.MINUTE) ||
         temp_seconds != data.calendar().get(Calendar.SECOND)){
         System.out.println("\n+++++++++++++++++++++++++++++++++\n");
         System.out.println(data);
         System.out.println("\n+++++++++++++++++++++++++++++++++\n");
         temp_minutes = data.calendar().get(Calendar.MINUTE);
         temp_seconds = data.calendar().get(Calendar.SECOND);
         System.out.println(temp_minutes);
         System.out.println(temp_seconds);
         for(int i = 0; i < unitsPanel.getComponentCount(); ++i){
            JRadioButton un = (JRadioButton)unitsPanel.getComponent(i);
            if(un.isSelected()){
               System.out.println(un.getActionCommand());
            }
         }
         for(int i = 0; i < displayPanel.getComponentCount(); ++i){
            JRadioButton dis=(JRadioButton)displayPanel.getComponent(i);
            if(dis.isSelected()){
               System.out.println(dis.getActionCommand());
            }
         }
      }
   }
}
