//////////////////////////////////////////////////////////////////////
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
//////////////////////////////////////////////////////////////////////
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

public class GenericThermalPanel extends JPanel{
   private WeatherData _data         = null;
   private long _millistime          = -1;
   private PanelUpdateObserver _view = null;
   //////////////////////Constructors/////////////////////////////////
   public GenericThermalPanel
   (
      CurrentWeatherController controller,
      int                      index
   ){
      super();
      this.setLayout(new BorderLayout());
      this.add(this.setNorthPanel(controller, index),
                                                  BorderLayout.NORTH);
      this.add(this.setCenterPanel(controller, index),
                                                 BorderLayout.CENTER);
   }

   /////////////////////Public Methods////////////////////////////////
   /*
   This will need to change!!!
   */
   public void addObserver(PanelUpdateObserver view){
      this._view = view;
   }

   /**/
   public void update(WeatherData data){
      if(this._data == null){
         this._data = data;
      }
      if(this._millistime < data.calendar().getTimeInMillis()){
         this._data = data;
         this._millistime = data.calendar().getTimeInMillis();
         this.updatePane();
      }
   }

   ////////////////////Private Methods////////////////////////////////
   /**/
   private JPanel setCenterPanel
   (
      CurrentWeatherController controller,
      int                      index
   ){
      JPanel panel = new JPanel();
      JLabel label = new JLabel("Generic Thermal Panel");
      panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      panel.add(label);
      return panel;
   }

   /**/
   private JPanel setNorthPanel
   (
      CurrentWeatherController controller,
      int                      index
   ){
      String idx = "" + index;
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());
      panel.setLayout(new GridLayout(2,1));

      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup graphGroup = new ButtonGroup();

      JPanel topPanel    = new JPanel();
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand(idx);
      unitsGroup.add(celsius);
      celsius.addItemListener(controller);
      celsius.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      topPanel.add(celsius);
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand(idx);
      unitsGroup.add(fahrenheit);
      fahrenheit.addItemListener(controller);
      fahrenheit.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      topPanel.add(fahrenheit);
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand(idx);
      unitsGroup.add(kelvin);
      kelvin.addItemListener(controller);
      kelvin.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      topPanel.add(kelvin);
      panel.add(topPanel);
      JPanel bottomPanel = new JPanel();
      JRadioButton analog = new JRadioButton("Analog");
      analog.setActionCommand(idx);
      graphGroup.add(analog);
      analog.addItemListener(controller);
      analog.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      bottomPanel.add(analog);
      JRadioButton digital = new JRadioButton("Digital", true);
      digital.setActionCommand(idx);
      graphGroup.add(digital);
      digital.addItemListener(controller);
      digital.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      bottomPanel.add(digital);
      panel.add(bottomPanel);
      return panel;
   }

   /**/
   private JPanel setUpAnalog(String units){
      double value   = Double.NaN;
      String display = new String("");
      if(units.equals("CELSIUS")){
         value    = this._data.metricData();
         display  = String.format("%1$.2f", value);
      }
      else if(units.equals("FAHRENHEIT")){
         value    = this._data.englishData();
         display  = String.format("%1$.0f", value);
      }
      else if(units.equals("KELVIN")){
         value    = this._data.absoluteData();
         display  = String.format("%1$.2f", value);
      }
      return new ThermalGuage(display, units);
   }

   /**/
   private JPanel setUpDigital(String units){
      double value   = Double.NaN;
      String display = new String("");
      if(units.equals("CELSIUS")){
         value    = this._data.metricData();
         display  = String.format("%1$.2f", value);
         display += " \u00b0C";
      }
      else if(units.equals("FAHRENHEIT")){
         value    = this._data.englishData();
         display  = String.format("%1$.0f", value);
         display += " \u00b0F";
      }
      else if(units.equals("KELVIN")){
         value    = this._data.absoluteData();
         display  = String.format("%1$.2f", value);
         display += " K";
      }
      JPanel panel         = new JPanel();
      JTextField textField = new JTextField(display);
      textField.setEditable(false);
      panel.add(textField);
      return panel;
   }

   /**/
   private void updatePane(){
      String units   = "";
      String display = "";

      JPanel topPanel     = (JPanel)this.getComponent(0);
      JPanel unitsPanel   = (JPanel)topPanel.getComponent(0);
      JPanel displayPanel = (JPanel)topPanel.getComponent(1);
      JPanel centerPanel  = (JPanel)this.getComponent(1);
      Calendar cal = this._data.calendar();

      for(int i = 0; i < unitsPanel.getComponentCount(); ++i){
         JRadioButton un = (JRadioButton)unitsPanel.getComponent(i);
         if(un.isSelected()){
            units = un.getText().toUpperCase();
         }
      }
      for(int i = 0; i < displayPanel.getComponentCount(); ++i){
         JRadioButton dis=(JRadioButton)displayPanel.getComponent(i);
         if(dis.isSelected()){
            display = dis.getText().toUpperCase();
         }
      }
      centerPanel.removeAll();
      centerPanel.setLayout(new BorderLayout());
      String time      = cal.getTime().toString();
      JPanel timePanel = new JPanel();
      timePanel.add(new JLabel(time));
      centerPanel.add(timePanel, BorderLayout.NORTH);
      if(display.equals("ANALOG")){
         centerPanel.add(this.setUpAnalog(units),BorderLayout.CENTER);
      }
      else if(display.equals("DIGITAL")){
         centerPanel.add(this.setUpDigital(units),BorderLayout.CENTER);
      }
   }
}
