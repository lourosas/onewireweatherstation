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
The complete Controller for the WeatherPage application
*/
//////////////////////////////////////////////////////////////////////
public class WeatherPageController extends
GenericWeatherController implements ActionListener, KeyListener,
ItemListener{
   private WeatherDatabaseClientView _view;
   private WeatherPage               _model;

   {
      _view  = null;
      _model = null;
   };

   /**/
   public WeatherPageController(){}

   /**/
   public WeatherPageController(WeatherDatabaseClientView view){}

   /**/
   public WeatherPageController(
      WeatherDatabaseClientView view,
      WeatherPage model
   ){
      this._view  = view;
      this._model = model;
      this._model.addObserver(this._view);
   }

   /**/
   public void addModel(WeatherPage model){
      this._model = model;
   }

   /**/
   public void addView(WeatherDatabaseClientView view){
      this._view = view;
   }

   /**/
   public void actionPerformed(ActionEvent ae){
      this.handleJButton(ae);
      this.handleJComboBox(ae);
      //this.handleJMenuItem(ae);
      this.handleJTextField(ae);
   }

   /**/
   public void keyPressed(KeyEvent ke){}

   /**/
   public void keyReleased(KeyEvent ke){}

   /**/
   public void keyTyped(KeyEvent ke){}

   /**/
   public void itemStateChanged(ItemEvent ie){}

   /////////////////////////Private Methods///////////////////////////
   /**/
   private void handleJButton(ActionEvent ae){
      try{
         String mo = this._view.getMonth();
         String dy = this._view.getDay();
         String yr = this._view.getYear();


         JButton button = (JButton)ae.getSource();
         this._model.setCalendar(mo,dy,yr);
         if(button.getActionCommand().equals("TemperatureRefresh")){
            this._model.grabTemperatureData(mo,dy,yr);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      catch(ClassCastException cce){}
   }

   /**/
   private void handleJComboBox(ActionEvent ae){
      try{
         JComboBox jcb = ((JComboBox)ae.getSource());
         String value = (String)jcb.getSelectedItem();
         if(jcb.getName().toUpperCase().equals("MONTH")){
            this._model.setCalendar(value,null,null);
         }
         else if(jcb.getName().toUpperCase().equals("DAY")){
            this._model.setCalendar(null,value,null);
         }
         else if(jcb.getName().toUpperCase().equals("YEAR")){
            this._model.setCalendar(null,null,value);
         }
      }
      catch(ClassCastException cce){}
      catch(NullPointerException npe){}
   }

   /**/
   private void handleJTextField(ActionEvent ae){}
}
