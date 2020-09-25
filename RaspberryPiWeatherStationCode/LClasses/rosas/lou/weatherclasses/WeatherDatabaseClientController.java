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
public class WeatherDatabaseClientController implements
ActionListener, KeyListener, ItemListener{
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
   }

   /**/
   public WeatherDatabaseClientController
   (
      WeatherDatabaseClientView view,
      WeatherDatabaseClient     model
   ){
      this._view  = view;
      this._model = model;
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
      //System.out.println(ke);
   }

   /**/
   public void keyReleased(KeyEvent ke){}

   /**/
   public void keyTyped(KeyEvent ke){}

   /**/
   public void itemStateChanged(ItemEvent ie){}
   ////////////////////////Private Methods////////////////////////////
   /**/
   private void handleJButton(ActionEvent ae){}

   /**/
   private void handleJComboBox(ActionEvent ae){
      try{
         JComboBox jcb = ((JComboBox)ae.getSource());
         if(jcb.getName().toUpperCase().equals("MONTH")){
            System.out.println((String)jcb.getSelectedItem());
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
         String address = this._view.address().trim();
         String port    = this._view.port().trim();
      }
      catch(ClassCastException cce){}
   }
}
