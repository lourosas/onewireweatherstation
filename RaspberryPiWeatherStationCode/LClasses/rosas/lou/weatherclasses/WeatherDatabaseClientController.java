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
import java.awt.event.*;
import javax.swing.*;
import rosas.lou.weatherclasses.*;

//////////////////////////////////////////////////////////////////////
/*
The complete Controller for the WeatherDatabaseClient application
*/
//////////////////////////////////////////////////////////////////////
public class WeatherDatabaseClientController extends
GenericWeatherController implements ActionListener, KeyListener,
ItemListener{
   private WeatherDatabaseClientView _view;
   private WeatherDatabaseClient     _model;

   {
      _view  = null;
      _model = null;
   };

   /**/
   public WeatherDatabaseClientController(){}

   /**/
   public WeatherDatabaseClientController
   (
      WeatherDatabaseClientView view
   ){
      this._view = view;
      this._model = new WeatherDatabaseClient();
      this._model.addObserver(this._view);
   }

   /**/
   public WeatherDatabaseClientController
   (
      WeatherDatabaseClientView view,
      WeatherDatabaseClient     model
   ){
      this._view  = view;
      this._model = model;
      this._model.addObserver(this._view);
   }

   /////////////////////////Public Methods////////////////////////////
   /**/
   public void actionPerformed(ActionEvent ae){
      //System.out.println(ae);
      this.handleJButton(ae);
      this.handleJComboBox(ae);
      this.handleJMenuItem(ae);
      this.handleJTextField(ae);
   }

   /**/
   public void keyPressed(KeyEvent ke){
      try{
         JTextField jt = ((JTextField)ke.getSource());
         if(jt.getName().toUpperCase().equals("PORT")){
            char k = ke.getKeyChar();
            int  c = ke.getKeyCode();
            if((k >= '0' && k <= '9') || c == KeyEvent.VK_BACK_SPACE){
               jt.setEditable(true);
            }
            else{
               jt.setEditable(false);
            }
         }
         else if(jt.getName().toUpperCase().equals("ADDRESS")){
            char k = ke.getKeyChar();
            int  c = ke.getKeyCode();
            if((k >= '0' && k <= '9') || k == '.' ||
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
         if(jt.getName().toUpperCase().equals("ADDRESS")){
            //System.out.println(jt.getText());
            this._model.setServerAddress(jt.getText());
         }
         else if(jt.getName().toUpperCase().equals("PORT")){
            //System.out.println(jt.getText());
            this._model.setServerPort(jt.getText());
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
            if(command.contains("T")){
               this.handleTemperatureItemSelection(command);
            }
            else if(command.contains("HG") || command.contains("HD")){
               this.handleHumidityItemSelection(command);
            }
            else if(command.contains("HI")){
               this.handleHeatIndexItemSelection(command);
            }
            else if(command.contains("DP")){
               this.handleDewpointItemSelection(command);
            }
            else if(command.contains("PG") || command.contains("PD")){
               this.handlePressureItemSelection(command);
            }
            else if(command.toUpperCase().equals("MMS") ||
                    command.toUpperCase().equals("INCHES") ||
                    command.toUpperCase().equals("MILLIBARS")){
               this.handlePressureItemSelection(command);
            }
         }
      }
      catch(ClassCastException cce){}
   }
   ////////////////////////Private Methods////////////////////////////
   /**/
   private void handleJButton(ActionEvent ae){
      try{
         JButton button = ((JButton)ae.getSource());
         if(button.getActionCommand().equals("TemperatureRefresh")){
            this._model.requestData("TEMPERATURE");
         }
         else if(button.getActionCommand().equals("HumidityRefresh")){
            this._model.requestData("HUMIDITY");
         }
         else if(button.getActionCommand().equals("DewpointRefresh")){
            this._model.requestData("DEWPOINT");
         }
         else if(button.getActionCommand().equals("HeatIndexRefresh")){
            this._model.requestData("HEATINDEX");
         }
         else if(button.getActionCommand().equals("Pressure Refresh")){
            this._model.requestData("PRESSURE");
         }
      }
      catch(ClassCastException cce){}
   }

   /**/
   private void handleJComboBox(ActionEvent ae){
      try{
         JComboBox jcb = ((JComboBox)ae.getSource());
         if(jcb.getName().toUpperCase().equals("MONTH")){
            this._model.setMonth((String)jcb.getSelectedItem());
         }
         else if(jcb.getName().toUpperCase().equals("DAY")){
            this._model.setDay((String)jcb.getSelectedItem());
         }
         else if(jcb.getName().toUpperCase().equals("YEAR")){
            this._model.setYear((String)jcb.getSelectedItem());
         }
      }
      catch(ClassCastException cce){}
   }

   /**/
   private void handleJMenuItem(ActionEvent ae){}

   /**/
   private void handleJTextField(ActionEvent ae){
      try{
         JTextField jt = ((JTextField)ae.getSource());
         if(jt.getName().toUpperCase().equals("ADDRESS")){
            this._model.setServerAddress(jt.getText());
         }
         else if(jt.getName().toUpperCase().equals("PORT")){
            this._model.setServerPort(jt.getText());
         }
      }
      catch(ClassCastException cce){}
   }

   /**/
   private void handleHeatIndexItemSelection(String command){
      if(command.toUpperCase().equals("HICELSIUS")){
         this._view.setHeatIndexUnits(Units.METRIC);
      }
      else if(command.toUpperCase().equals("HIFAHRENHEIT")){
         this._view.setHeatIndexUnits(Units.ENGLISH);

      }
      else if(command.toUpperCase().equals("HIKELVIN")){
         this._view.setHeatIndexUnits(Units.ABSOLUTE);
      }
      else if(command.toUpperCase().equals("HIGRAPH")){
         //GRAPH = 0
         this._view.setHeatIndexDisplay((short)0);
      }
      else if(command.toUpperCase().equals("HIDATA")){
         //DATA = 1
         this._view.setHeatIndexDisplay((short)1);
      }
      this._model.publishHeatIndexData();
   }

   /**/
   private void handleHumidityItemSelection(String command){
      if(command.toUpperCase().equals("HGRAPH")){
         this._view.setHumidityDisplay((short)0);
      }
      else if(command.toUpperCase().equals("HDATA")){
         this._view.setHumidityDisplay((short)1);
      }
      this._model.publishHumdityData();
   }

   /**/
   private void handleDewpointItemSelection(String command){
      if(command.toUpperCase().equals("DPCELSIUS")){
         this._view.setDewpointUnits(Units.METRIC);
      }
      else if(command.toUpperCase().equals("DPFAHRENHEIT")){
         this._view.setDewpointUnits(Units.ENGLISH);
      }
      else if(command.toUpperCase().equals("DPKELVIN")){
         this._view.setDewpointUnits(Units.ABSOLUTE);
      }
      else if(command.toUpperCase().equals("DPGRAPH")){
         //GRAPH = 0
         this._view.setDewpointDisplay((short)0);
      }
      else if(command.toUpperCase().equals("DPDATA")){
         //DATA = 1
         this._view.setDewpointDisplay((short)1);
      }
      this._model.publishDewpointData();
   }

   /**/
   private void handlePressureItemSelection(String command){
      if(command.toUpperCase().equals("MMS")){
         this._view.setPressureUnits(Units.METRIC);
      }
      else if(command.toUpperCase().equals("INCHES")){
         this._view.setPressureUnits(Units.ENGLISH);
      }
      else if(command.toUpperCase().equals("MILLIBARS")){
         this._view.setPressureUnits(Units.ABSOLUTE);
      }
      else if(command.toUpperCase().equals("PGRAPH")){
         //GRAPH = 0
         this._view.setPressureDisplay((short)0);
      }
      else if(command.toUpperCase().equals("PDATA")){
         // = DATA = 1
         this._view.setPressureDisplay((short)1);
      }
      this._model.publishPressureData();
   }

   /**/
   private void handleTemperatureItemSelection(String command){
      if(command.toUpperCase().equals("TCELSIUS")){
         this._view.setTemperatureUnits(Units.METRIC);
      }
      else if(command.toUpperCase().equals("TFAHRENHEIT")){
         this._view.setTemperatureUnits(Units.ENGLISH);
      }
      else if(command.toUpperCase().equals("TKELVIN")){
         this._view.setTemperatureUnits(Units.ABSOLUTE);
      }
      else if(command.toUpperCase().equals("TGRAPH")){
         //GRAPH = 0
         this._view.setTemperatureDisplay((short)0);
      }
      else if(command.toUpperCase().equals("TDATA")){
         //DATA = 1
         this._view.setTemperatureDisplay((short)1);
      }
      this._model.publishTemperatureData();
   }
}
