//******************************************************************
//Mission Event Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

/*
*/
public class MissionEvent{
   private Object source; //The Source of the Mission Event
   //The message embedded in the Mission Event
   private String message;
   
   //***********************Constructors***************************
   /*
   Constructor Initializing all the data related to a Mission Event
   */
   public MissionEvent(Object source, String message){
      this.setSource(source);
      this.setMessage(message);
   }
   
   //************************Public Methods************************
   /*
   Get the associated message of the Mission Event
   */
   public String getMessage(){
      return this.message;
   }
   
   /*
   Get the Source of the Mission Event
   */
   public Object getSource(){
      return this.source;
   }
   
   //*******************Private Methods****************************
   /*
   */
   private void setMessage(String message){
      this.message = new String(message);
   }
   
   /*
   */
   private void setSource(Object source){
      this.source = source;
   }
}
