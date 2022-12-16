//******************************************************************
//Archive Event Class
//Copyright (C) 2016 by Lou Rosas
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
import java.io.*;
import rosas.lou.weatherclasses.*;

/**
*/
public class ArchiveEvent{
   //Source of the Archive Event
   private Object source;
   //File where the data was saved
   private File file;
   //Embedded message in the Archive Event
   private String message;
   
   //**********************Constructors*******************************
   /**
   Constructor Initializing all the data
   */
   public ArchiveEvent(Object source, String message, File file){
      this.setSource(source);
      this.setFile(file);
      this.setMessage(message);
   }
   
   //************************Public Methods***************************
   /**
   Get the file where the data was saved
   */
   public File getFile(){
      return this.file;
   }
   
   /**
   Get the associated message of the Archive Event
   */
   public String getMessage(){
      return this.message;
   }
   
   /**
   Get the Source of the Archive Event
   */
   public Object getSource(){
      return this.source;
   }
   
   //********************Private Methods******************************
   /**
   */
   private void setFile(File file){
      this.file = file;
   }

   /**
   */
   private void setMessage(String message){
      this.message = new String(message);
   }
   
   /**
   */
   private void setSource(Object source){
      this.source = source;
   }
}
