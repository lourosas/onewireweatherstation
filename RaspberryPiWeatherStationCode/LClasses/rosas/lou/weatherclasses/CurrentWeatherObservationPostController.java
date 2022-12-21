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
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import rosas.lou.weatherclasses.*;
import myclasses.*;

public class CurrentWeatherObservationPostController
extends CurrentWeatherController{
   private CurrentWeatherView            _view      = null;
   private CurrentWeatherObservationPost _model     = null;

   /**/
   public CurrentWeatherObservationPostController(){}

   /**/
   public CurrentWeatherObservationPostController
   (
      CurrentWeatherView view
   ){}

   /**/
   public CurrentWeatherObservationPostController
   (
      CurrentWeatherView            view,
      CurrentWeatherObservationPost model
   ){
      this._model = model;
      this._view  = view;
   }

   /**/
   public void addModel(CurrentWeatherObservationPost model){
      this._model = model;
   }

   /**/
   public void addObserverToModel(CurrentWeatherDataObserver ob){
      this._model.addObserver(ob);
   }

   /**/
   public void addView(CurrentWeatherView view){
      this._view = view;
   }

   /////////////////////////Interface Implementation//////////////////
   ////////////////////////ItemListener Interface/////////////////////
   /**/
   public void actionPerformed(ActionEvent e){
      if(e.getSource() instanceof JButton){
         String cmd = e.getActionCommand().toUpperCase();
         if(e.getActionCommand().toUpperCase().equals("REFRESH")){
            this._model.requestUpdateFromPublisher();
         }
         else if(e.getActionCommand().toUpperCase().equals("SAVE")){
            try{
               JFileChooser jfc = new JFileChooser();
               FileNameExtensionFilter filter =
                   new FileNameExtensionFilter("Weather Data", "wea");
               jfc.setFileFilter(filter);
               int fileValue = jfc.showSaveDialog(this._view);
               if(fileValue == JFileChooser.APPROVE_OPTION){
                  this._model.save(jfc.getSelectedFile());
               }
            }
            catch(HeadlessException he){}
         }
         //This will need to change...
         else if(cmd.equals("REQUEST")){
            //this._view.displayInteraction("SLEEPTIME");
            //this._model.changeSleepTime();
            SuperUserAccountDialog suad =
                               new SuperUserAccountDialog(this._view);
            if(suad.isSuperUser()){
               SleepTimeFrame stf = SleepTimeFrame.instance(this);
            }
         }
         else if(cmd.contains("SLEEPT")){
            SleepTimeFrame stf = SleepTimeFrame.instance(this);
            if(cmd.equals("SLEEPTIMESET")){
               int[] hms = stf.requestTimes();
               this._model.setSleepTime(hms[0],hms[1],hms[2]);
            }
            else if(cmd.equals("SLEEPTIMECLEAR")){
               stf.clear();
            }
            else if(cmd.equals("SLEEPTIMECANCEL")){
                stf.cancel();
            }
         }
      }
      else if(e.getSource() instanceof JTextField){
         JTextField jtf = (JTextField)e.getSource();
         String name = jtf.getName().toUpperCase();
         if(name.equals("HOURS") || name.equals("MINUTES")){
            SleepTimeFrame stf = SleepTimeFrame.instance(this);
            stf.nextTextField(name);
         }
      }
   }

   /**/
   public void itemStateChanged(ItemEvent e){
      if(e.getStateChange() == ItemEvent.SELECTED){
         JRadioButton jrb = (JRadioButton)e.getItem();
         String command   = jrb.getActionCommand();
         this._view.updateTheViews(command);
      }
   }

   /**/
   public void keyPressed(KeyEvent k){
      //If there <ENTER> button is pressed when a button
      //has focus...this is typical...
      if(k.getKeyCode() == KeyEvent.VK_ENTER){
         try{
            JButton button = ((JButton)k.getSource());
            button.doClick(100);
         }
         catch(ClassCastException cce){}
      }
   }

   /**/
   public void keyTyped(KeyEvent k){
      try{
         JTextField jtf = (JTextField)k.getComponent();
         char c = k.getKeyChar();
         if(!Character.isDigit(c)){
            k.consume();
         }
      }
      catch(ClassCastException cce){}
   }
}
