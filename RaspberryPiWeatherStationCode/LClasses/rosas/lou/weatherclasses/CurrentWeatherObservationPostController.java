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
import rosas.lou.weatherclasses.*;

public class CurrentWeatherObservationPostController
extends CurrentWeatherController{
   private CurrentWeatherView           _view  = null;
   private CurrentWeatherDataSubscriber _model = null;

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
      CurrentWeatherView view,
      CurrentWeatherDataSubscriber model
   ){
      this._model = model;
      this._view  = view;
   }

   /**/
   public void addModel(CurrentWeatherDataSubscriber model){
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
   public void itemStateChanged(ItemEvent e){
      if(e.getStateChange() == ItemEvent.SELECTED){
         JRadioButton jrb = (JRadioButton)e.getItem();
         String command   = jrb.getActionCommand();
         this._view.updateTheViews(command);
      }
   }
}
