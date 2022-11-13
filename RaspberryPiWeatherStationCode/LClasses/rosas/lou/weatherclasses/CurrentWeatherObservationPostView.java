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
extends CurrentWeatherView implements CurrentWeatherDataObserver
{
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
   private JPanel setUpPressureAnalog(String units){
      String display = "";
      if(units.equals("MMHG")){
         display=String.format("%1$.2f",_pressureData.metricData());
      }
      else if(units.equals("INHG")){
         display=String.format("%1$.2f",_pressureData.englishData());
      }
      else if(units.equals("MB")){
         display=String.format("%1$.1f",_pressureData.absoluteData());
      }

      //return new JPanel();
      return new PressureGuage(display, units);
   }

   /**/
   private JPanel setUpPressureDigital(String units){
      String display = "";
      if(units.equals("MMHG")){
         display=String.format("%1$.2f",_pressureData.metricData());
         display = display.concat(" millimeters Hg");
      }
      else if(units.equals("INHG")){
         display=String.format("%1$.2f",_pressureData.englishData());
         display = display.concat(" inches Hg");
      }
      else if(units.equals("MB")){
         display=String.format("%1$.1f",_pressureData.absoluteData());
         display = display.concat(" millibars");
      }
      JPanel panel         = new JPanel();
      JTextField textField = new JTextField(display);
      textField.setEditable(false);
      panel.add(textField);
      return panel;
   }

   /**/
   private JPanel setUpPressurePanel(){
      JPanel pressurePanel = new JPanel();
      pressurePanel.setLayout(new BorderLayout());
      pressurePanel.add(this.setUpPressureNorthPanel(),
                                                  BorderLayout.NORTH);
      pressurePanel.add(this.setUpPressureCenterPanel(),
                                                 BorderLayout.CENTER);
      return pressurePanel;
   }

   /**/
   private JPanel setUpPressureCenterPanel(){
      JPanel centerPanel = new JPanel();
      JLabel pressLabel  = new JLabel("Pressure");
      centerPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      centerPanel.add(pressLabel);
      return centerPanel;
   }

   /**/
   private JPanel setUpPressureNorthPanel(){
      JPanel panel              = new JPanel();
      ButtonGroup pressureGroup = new ButtonGroup();
      ButtonGroup graphGroup    = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());
      panel.setLayout(new GridLayout(2,1));

      JPanel topPanel   = new JPanel();
      JRadioButton mmHg = new JRadioButton("millimeters Hg");
      mmHg.setActionCommand("mmHg");
      pressureGroup.add(mmHg);
      mmHg.addItemListener(this._controller);
      mmHg.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePressurePane();
            }
         }
      });
      topPanel.add(mmHg);
      JRadioButton inHg = new JRadioButton("inches Hg");
      inHg.setActionCommand("inHg");
      pressureGroup.add(inHg);
      inHg.addItemListener(this._controller);
      inHg.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePressurePane();
            }
         }
      });
      topPanel.add(inHg);
      JRadioButton mb = new JRadioButton("millibars", true);
      mb.setActionCommand("mb");
      pressureGroup.add(mb);
      mb.addItemListener(this._controller);
      mb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePressurePane();
            }
         }
      });
      topPanel.add(mb);
      panel.add(topPanel);

      JPanel bottomPanel  = new JPanel();
      JRadioButton analog = new JRadioButton("Analog");
      analog.setActionCommand("PresAnalog");
      graphGroup.add(analog);
      analog.addItemListener(this._controller);
      analog.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePressurePane();
            }
         }
      });
      bottomPanel.add(analog);
      JRadioButton digital = new JRadioButton("Digital", true);
      digital.setActionCommand("PresDigital");
      graphGroup.add(digital);
      digital.addItemListener(this._controller);
      digital.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePressurePane();
            }
         }
      });
      bottomPanel.add(digital);
      panel.add(bottomPanel);
      return panel;
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
                  this.setUpPressurePanel(),
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

   /*
   private void updateHumidityPane(){
      String display = "";
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(1);
      JPanel panel        = (JPanel)jtp.getComponent(1);
      JPanel topPanel     = (JPanel)panel.getComponent(0);
      JPanel displayPanel = (JPanel)topPanel.getComponent(0);
      JPanel centerPanel  = (JPanel)panel.getComponent(1);
      Calendar cal        = this._humidityData.calendar();

      for(int i = 0; i < displayPanel.getComponentCount(); ++i){
         JRadioButton dis=(JRadioButton)displayPanel.getComponent(i);
         if(dis.isSelected()){
            display = dis.getActionCommand().toUpperCase();
         }
      }

      centerPanel.removeAll();
      centerPanel.setLayout(new BorderLayout());
      String time      = cal.getTime().toString();
      JPanel timePanel = new JPanel();
      timePanel.add(new JLabel(time));
      centerPanel.add(timePanel, BorderLayout.NORTH);
      if(display.equals("HUMIDITYANALOG")){
         JPanel analogPanel = this.setUpHumidityAnalog();
         centerPanel.add(analogPanel, BorderLayout.CENTER);
      }
      else if(display.equals("HUMIDITYDIGITAL")){
         JPanel digitalPanel = this.setUpHumidityDigital();
         centerPanel.add(digitalPanel, BorderLayout.CENTER);
      }

      jtp.setSelectedIndex(0);
      jtp.setSelectedIndex(1);
      jtp.setSelectedIndex(index);
   }
   */

   /**/
   private void updateHumidityPane(WeatherData data){
      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(1);
      GenericHumidityPanel panel =
                            (GenericHumidityPanel)jtp.getComponent(1);
      panel.update(data);
      //this.updateTheViews("2");
      jtp.setSelectedIndex(0);
      jtp.setSelectedIndex(index);
   }

   /**/
   private void updatePressurePane(){
      String units   = "";
      String display = "";

      JTabbedPane jtp =
                   (JTabbedPane)this.getContentPane().getComponent(0);
      int index = jtp.getSelectedIndex();
      jtp.setSelectedIndex(2);
      JPanel panel         = (JPanel)jtp.getComponent(2);
      JPanel topPanel      = (JPanel)panel.getComponent(0);
      JPanel unitsPanel    = (JPanel)topPanel.getComponent(0);
      JPanel displayPanel  = (JPanel)topPanel.getComponent(1);
      JPanel centerPanel   = (JPanel)panel.getComponent(1);
      Calendar cal         = this._pressureData.calendar();

      for(int i = 0; i < unitsPanel.getComponentCount(); ++i){
         JRadioButton un=(JRadioButton)unitsPanel.getComponent(i);
         if(un.isSelected()){
            units = un.getActionCommand().toUpperCase();
         }
      }
      for(int i = 0; i < displayPanel.getComponentCount(); ++i){
         JRadioButton dis=(JRadioButton)displayPanel.getComponent(i);
         if(dis.isSelected()){
            display = dis.getActionCommand().toUpperCase();
         }
      }

      centerPanel.removeAll();
      centerPanel.setLayout(new BorderLayout());
      String time      = cal.getTime().toString();
      JPanel timePanel = new JPanel();
      timePanel.add(new JLabel(time));
      centerPanel.add(timePanel, BorderLayout.NORTH);
      if(display.equals("PRESANALOG")){
         JPanel analogPanel = this.setUpPressureAnalog(units);
         centerPanel.add(analogPanel, BorderLayout.CENTER);
      }
      else if(display.equals("PRESDIGITAL")){
         JPanel digitalPanel = this.setUpPressureDigital(units);
         centerPanel.add(digitalPanel, BorderLayout.CENTER);
      }

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
      if(this.pressure_minutes!=data.calendar().get(Calendar.MINUTE)||
         this.pressure_seconds!=data.calendar().get(Calendar.SECOND)){
         this.pressure_minutes=data.calendar().get(Calendar.MINUTE);
         this.pressure_seconds=data.calendar().get(Calendar.SECOND);
         this._pressureData  = data;
         this.updatePressurePane();
      }
   }

   /**/
   public void updateTemperature(WeatherData data){
      this.updateTemperaturePane(data);
   }
}
