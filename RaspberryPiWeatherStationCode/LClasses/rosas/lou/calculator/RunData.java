//*******************************************************************
//RunData Class
//Copyright (C) 2008 Lou Rosas
//This file is part of PaceCalculator.
//PaceCalculator is free software; you can redistribute it
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
package rosas.lou.calculator;

import java.lang.*;
import java.util.*;
import java.text.*;
import rosas.lou.*;
import rosas.lou.calculator.*;
import myclasses.*;

/********************************************************************
The RunData Class by Lou Rosas.  This class stores the data from 
a typical run: 1)Run Time, 2)Distance, 3)Pace
********************************************************************/
public class RunData{
   //Private attributes
   private Calendar    calendar;
   private RunTime     time;
   private RunTime     pace;
   private RunDistance distance;


   //*************************Public Methods*************************
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public RunData(){
      this.setRunData(new RunTime(),new RunTime(),new RunDistance());
   }

   /*****************************************************************
   Constructor Accepting the Pace, Run Time, and Run Distance
   *****************************************************************/
   public RunData(RunTime time, RunTime pace, RunDistance distance){
      this.setRunData(time, pace, distance);
   }

   /*****************************************************************
   Copy Constructor
   *****************************************************************/
   public RunData(RunData rd){
      this.setRunData(rd);
   }

   /*****************************************************************
   Set up the Run Data, including the Date information
   *****************************************************************/
   public void setRunData
   (
      RunTime     time,
      RunTime     pace,
      RunDistance distance
   ){
      this.setRuntime(time);
      this.setPace(pace);
      this.setDistance(distance);
      //Save off the Calendar data as needed
      this.calendar = Calendar.getInstance();
   }

   /*****************************************************************
   Set up the Run Data with an actual Run Data object
   *****************************************************************/
   public void setRunData(RunData rd){
      try{
         this.setRuntime(rd.getRuntime());
         this.setPace(rd.getPace());
         this.setDistance(rd.getDistance());

      }
      catch(NullPointerException npe){
         this.setRuntime(new RunTime());
         this.setPace(new RunTime());
         this.setDistance(new RunDistance());
      }
      finally{
         //Save off the Calendar data as needed
         this.calendar = Calendar.getInstance();
      }
   }

   /*****************************************************************
   Get the Date:  go ahead and return the Calendar object
   *****************************************************************/
   public Calendar getDate(){
      return this.calendar;
   }

   /*****************************************************************
   Return the Run Distance
   *****************************************************************/
   public RunDistance getDistance(){
      RunDistance returnDistance = new RunDistance(this.distance);
      return returnDistance;
   }

   /*****************************************************************
   Return the Pace
   *****************************************************************/
   public RunTime getPace(){
      RunTime returnPace = new RunTime(this.pace);
      return returnPace;
   }

   /*****************************************************************
   Return a copy of the current RunData instance
   *****************************************************************/
   public RunData getRunData(){
      RunData returnRunData = new RunData(this);
      return returnRunData;
   }

   /*****************************************************************
   Return the Run Time
   *****************************************************************/
   public RunTime getRuntime(){
      RunTime returnRuntime = new RunTime(this.time);
      return returnRuntime;
   }
   
   /*****************************************************************
   Set the Run Distance data
   *****************************************************************/
   public void setDistance(RunDistance distance){
      this.distance = new RunDistance(distance);
   }

   /*****************************************************************
   Set the Run Time data
   *****************************************************************/
   public void setRuntime(RunTime time){
      this.time = new RunTime(time);
   }

   /*****************************************************************
   Set the Pace data
   *****************************************************************/
   public void setPace(RunTime pace){
      this.pace = new RunTime(pace);
   }

   /*****************************************************************
   Override the toString() method
   *****************************************************************/
   public String toString(){
      String returnString = new String();

      returnString += this.getDateString() + "\t";
      returnString += this.getRuntime() + "\t";
      returnString += this.getPace() + "\t";
      returnString += this.getDistance();
      
      return returnString;
   }

   //*********************Private Methods****************************
   //****************************************************************
   //Get the actual Date String for the Calendar instance
   //****************************************************************
   private String getDateString(){
      Date date = this.getDate().getTime();
      String dString = new String();

      //dString += DateFormat.getDateInstance().format(date) + " ";
      //dString += DateFormat.getTimeInstance().format(date) + "\n";
      //For now, just go ahead and get the Time Data
      dString += DateFormat.getDateInstance().format(date) + " ";

      return dString;
   }


}
