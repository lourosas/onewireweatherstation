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

public class GenericPressurePanel extends JPanel{
   private WeatherData _data       = null;
   private int _data_minutes       = -1;
   private int _data_seconds       = -1;

   ///////////////////////Constructors///////////////////////////////
   public GenericPressurePanel
   (
      CurrentWeatherController controller,
      int                      index
   ){
      super();
      this.setLayout(new BorderLayout());
      this.add(this.setNorthPanel(controller,index),
                                                  BorderLayout.NORTH);
      this.add(this.setCenterPanel(controller,index),
                                                 BorderLayout.CENTER);
   }


   ///////////////////////Public Methods//////////////////////////////
   /**/
   public void update(WeatherData data){
      this._data = data;
      if(this._data_minutes != data.calendar().get(Calendar.MINUTE) ||
         this._data_seconds != data.calendar().get(Calendar.SECOND)){
         this._data = data;
         this._data_minutes = data.calendar().get(Calendar.MINUTE);
         this._data_seconds = data.calendar().get(Calendar.SECOND);
         this.updatePane();
      }
   }

   ///////////////////////Private Methods/////////////////////////////
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
      String idx   = "" + index;
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());
      panel.setLayout(new GridLayout(2,1));

      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup graphGroup = new ButtonGroup();

      JPanel topPanel = new JPanel();
      JRadioButton mmHg = new JRadioButton("millimeters Hg");
      mmHg.setActionCommand(idx);
      unitsGroup.add(mmHg);
      mmHg.addItemListener(controller);
      mmHg.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      topPanel.add(mmHg);
      JRadioButton inHg = new JRadioButton("inches Hg");
      inHg.setActionCommand(idx);
      unitsGroup.add(inHg);
      inHg.addItemListener(controller);
      inHg.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      topPanel.add(inHg);
      JRadioButton mb = new JRadioButton("millibars", true);
      mb.setActionCommand(idx);
      unitsGroup.add(mb);
      mb.addItemListener(controller);
      mb.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e){
            if(e.getStateChange() == ItemEvent.SELECTED){
               updatePane();
            }
         }
      });
      topPanel.add(mb);
      panel.add(topPanel);

      JPanel bottomPanel  = new JPanel();
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
      String display = "";
      if(units.equals("MILLIMETERS HG")){
         display = String.format("%1$.2f",this._data.metricData());
      }
      else if(units.equals("INCHES HG")){
         display = String.format("%1$.2f",this._data.englishData());
      }
      else if(units.equals("MILLIBARS")){
         display = String.format("%1$.1f",this._data.absoluteData());
      }
      return new PressureGuage(display, units);
   }

   /**/
   private JPanel setUpDigital(String units){
      double value   = Double.NaN;
      String display = new String("");
      if(units.equals("MILLIMETERS HG")){
         display = String.format("%1$.2f",this._data.metricData());
         display = display.concat(" millimeters Hg");
      }
      else if(units.equals("INCHES HG")){
         display = String.format("%1$.2f",this._data.englishData());
         display = display.concat(" inches Hg");
      }
      else if(units.equals("MILLIBARS")){
         display = String.format("%1$.1f",this._data.absoluteData());
         display = display.concat(" millibars");
      }
      JPanel panel         = new JPanel();
      JTextField textField = new JTextField(display);
      textField.setEditable(false);
      panel.add(textField);
      return panel;
   }

   /**/
   private void updatePane(){
      String   units = new String("");
      String display = new String("");

      JPanel topPanel     = (JPanel)this.getComponent(0);
      JPanel unitsPanel   = (JPanel)topPanel.getComponent(0);
      JPanel displayPanel = (JPanel)topPanel.getComponent(1);
      JPanel centerPanel  = (JPanel)this.getComponent(1);
      Calendar cal        = this._data.calendar(); 

      for(int i = 0; i < unitsPanel.getComponentCount(); ++i){
         JRadioButton un=(JRadioButton)unitsPanel.getComponent(i);
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
