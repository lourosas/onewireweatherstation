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
   public void itemStateChanged(ItemEvent ie){}
   ////////////////////////Private Methods////////////////////////////
   /**/
   private void handleJButton(ActionEvent ae){}

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
}
