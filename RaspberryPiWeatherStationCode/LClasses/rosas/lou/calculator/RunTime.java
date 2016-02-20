/*******************************************************************
RunTime Class
Copyright (C) 2008 Lou Rosas
This file is part of PaceCalculator.
PaceCalculator is free software; you can redistribute it
and/or modify
it under the terms of the GNU General Public License as published
by the Free Software Foundation; either version 3 of the License,
or (at your option) any later version.
PaceCalculator is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.
If not, see <http://www.gnu.org/licenses/>.
//******************************************************************/
package rosas.lou.calculator;

import java.lang.*;
import java.util.*;
import java.util.regex.*;
import myclasses.*;
import rosas.lou.*;
import rosas.lou.calculator.*;

/*******************************************************************
The RunTime Class by Lou Rosas.
This Class encapsulates the time data used by the Pace Calculator
This class takes time data in either integer or String format and
saves off the data locally in the following format:
Hours:    integer
Minites:  integer
Seconds:  double:  in Seconds.decimal
*******************************************************************/
public class RunTime{
   public static final short HOURS   = 0;
   public static final short MINUTES = 1;
   public static final short SECONDS = 2;
   
   //Private Attributes here
   private int    hours;
   private int    minutes;
   private double seconds;
   //**********************Public Methods****************************
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public RunTime(){
      this(0,0,0.0);
   }
   
   /*****************************************************************
   Constructor taking the hours, minutes and seconds input
   *****************************************************************/
   public RunTime(int hrs, int mins, double secs)
   throws RunTimeFormatException{
      this.setTime(hrs, mins, secs);
   }
   
   /*****************************************************************
   Constructor taking a String.  By default, the String SHOULD be
   considered in Traditional Hours format
   *****************************************************************/
   public RunTime(String time) throws RunTimeFormatException{
      this(time, RunTime.HOURS, false);
   }
   
   /*****************************************************************
   Constructor taking a String, the type of data( hours, minutes 
   or seconds) 
   and a boolean indicating if the
   input is in decimal format
   *****************************************************************/
   public RunTime(String time, int type, boolean isDecimal)
   throws RunTimeFormatException{
      this.convertTime(time, type, isDecimal);
   }
   
   /*****************************************************************
   Constructor taking a double for the time input.  By default, 
   this should be considered DECIMAL Hours.
   *****************************************************************/
   public RunTime(double time){
      this(time, RunTime.HOURS);
   }
   
   /*****************************************************************
   Constructor taking a double, and an indicator if the input is 
   in Hours, minutes or seconds format.
   *****************************************************************/
   public RunTime(double time, short format){
      this.setTime(time, format);
   }
   
   /*****************************************************************
   The Copy constuctor
   *****************************************************************/
   public RunTime(RunTime time){
      try{
         time.getHours();  //To Check to see if the object exists
         this.setTime(time);
      }
      catch(NullPointerException npe){
         this.setTime(0,0,0.0);
      }
   }
   
   /*****************************************************************
   Return the current hours value
   *****************************************************************/
   public int getHours(){
      return this.hours;
   }
   
   /*****************************************************************
   Return the current minutes value
   *****************************************************************/
   public int getMinutes(){
      return this.minutes;
   }
   
   /*****************************************************************
   Return the current seconds value
   *****************************************************************/
   public double getSeconds(){
      return this.seconds;
   }
   
   /*****************************************************************
   Return the RunTime Class
   *****************************************************************/
   public RunTime getTime(){
      return (new RunTime(this));
   }
   
   /*****************************************************************
   Return the total time in hours.  This method returns the time
   in hours in the form of:  hr.decimal hours
   This is done by the following formula:
   hours + minutes/60 + seconds/3600
   i.e 3 hours 30 minutes 0 seconds would be converted to 3.5 hours
   *****************************************************************/
   public double getTimeInHours(){
      double returnHours;
      int    hrs  = this.getHours();
      int    mins = this.getMinutes();
      double secs = this.getSeconds();
 
      returnHours = hrs + mins/60. + secs/3600.;

      return returnHours;
   }
   
   /****************************************************************
   Return the total time in minutes.  This method returns the time
   in minutes in decimal form:  minutes.decimal minutes.
   This is done by the following formula:
   hours/60 + minutes+ seconds*60.
   *****************************************************************/
   public double getTimeInMinutes(){
      double returnMins;
      int    hrs  = this.getHours();
      int    mins = this.getMinutes();
      double secs = this.getSeconds();
      
      returnMins = hrs*60. + mins + secs/60.;
      
      return returnMins;
   }
   
   /*****************************************************************
   Return the total time in secods.  This method returns the time
   in seconds in decimal form:  seconds.decimal seconds.
   This is done by the following formula:
   hours*3600 + minutes*60 + seconds.
   *****************************************************************/
   public double getTimeInSeconds(){
      double returnSecs;
      int    hrs  = this.getHours();
      int    mins = this.getMinutes();
      double secs = this.getSeconds();

      returnSecs = hrs*3600. + mins*60. + secs;

      return returnSecs;
   }
   
   /*****************************************************************
   Set the attributes for this object.  Given hours, minutes and
   seconds, set the appropriate instance attributes.
   *****************************************************************/
   public void setTime(int hrs, int mins, double sec)
   throws RunTimeFormatException{
      this.setHours(hrs);
      this.setMinutes(mins);
      this.setSeconds(sec);
   }
   
   /*****************************************************************
   Covert and then set the attributes for this object
   Given a time value and a format, set the hours, minutes and
   seconds.  Based on the format, the value will be converted to
   integer hours, integer minutes and decimal seconds.
   *****************************************************************/
   public void setTime(double time, short format)
   throws RunTimeFormatException{
      double currentTime = time;
      int    hours, minutes;
      double seconds;
      if(format == RunTime.HOURS){
         hours        = (int)currentTime;
         currentTime -= hours;
         currentTime *= 60;
         minutes      = (int)currentTime;
         currentTime -= minutes;
         currentTime *= 60;
         seconds      = currentTime;
      }
      else if(format == RunTime.MINUTES){
         hours        = (int)(currentTime/60.);
         currentTime -= (hours*60);
         minutes      = (int)currentTime;
         currentTime -= minutes;
         currentTime *= 60;
         seconds      = currentTime;
      }
      else{  //Only other thing it could be would be seconds
         hours        = (int)(currentTime/3600.);
         currentTime -= (hours*3600);
         minutes      = (int)(currentTime/60.);
         currentTime -= (minutes*60);
         seconds      = currentTime;
      }
      this.setTime(hours, minutes, seconds);   
   }
   
   /*****************************************************************
   Set the time using another RunTime Object
   Similar to the Copy Constructor.  The time instance attributes
   are set based on an input 
   *****************************************************************/
   public void setTime(RunTime runTime)
   throws RunTimeFormatException{
      try{
         int    hrs  = runTime.getHours();
         int    mins = runTime.getMinutes();
         double secs = runTime.getSeconds();
         this.setTime(hrs, mins, secs);
      }
      catch(NullPointerException npe){
         this.setTime(0,0,0.0);
      }
   }
   
   /*****************************************************************
   Override the toString() method
   This will round the the seconds to the thousandths, add a
   zero if the minutes and seconds are less than 10, print the
   hours values only if it is greater than 0 and put in the
   format:  hh:mm:ss.ss.
   *****************************************************************/
   public String toString(){
      int    hours     = this.getHours();
      int    minutes   = this.getMinutes();
      double seconds   = this.getSeconds();
      String rString   = new String();
      String secString = new String();
      //Get rid of the Java 6 backwards compatability issues
      //return String.format("%d:%02d:%02.2f",hours,minutes,seconds);
      if(hours > 0){
         rString += hours + ":";
      }
      if(minutes < 10){
         rString += "0" + minutes + ":";
      }
      else{
         if(minutes > 9){
            rString += minutes + ":";
         }
         else{
            rString += "0" + minutes + ":";
         }
      }
      double roundSecs = seconds * 1000.;
      
      if(seconds < 10.){
         secString = "0" + (Math.round(roundSecs)/1000.);
      }
      else{
         secString = "" + (Math.round(roundSecs)/1000.);
      }
      rString += secString;
      return rString;
   }
   
   //**************************Private Methods***********************
   //****************************************************************
   //Convert the time from a String to an actual storage value.  
   //Include the Type--Hours, Minutes, Seconds and the boolean if the
   //****************************************************************
   private void convertTime
   (
      String timeString,
      int type,
      boolean isDecimal
   ) throws RunTimeFormatException{
      if(isDecimal){
         this.convertTimeDecimal(timeString, (short)type);
      }
      else{
         this.convertTimeNormal(timeString, (short)type);
      }
   }
   //****************************************************************
   //Convert the time from a String to an actual storage value.
   //The String Displays the time in "Typical" format:  HH:MM:SS.ss
   //or MM:SS.ss
   //****************************************************************  
   private void convertTimeNormal(String timeString, short type)
   throws RunTimeFormatException{
      int hrs     = 0;
      int mins    = 0;
      double secs = 0.;
      String hours, minutes, seconds, reason;
      if(type == RunTime.HOURS){
         reason = new String("Hours: ");
         reason += "Please enter time in HH:MM:SS.ss format";
      }
      else{
         reason = new String("Minutes: ");
         reason += "Please enter time in MM:SS.ss format";
      }
      try{
         String [] timeArray = timeString.split(":");
         int size = timeArray.length;
         if(type == RunTime.HOURS){
            hours   = new String(timeArray[size - 3]);
         }
         else{
            hours   = new String("");
         }
         minutes = new String(timeArray[size - 2]);
         seconds = new String(timeArray[size - 1]);
      }
      catch(PatternSyntaxException pse){
         throw(new RunTimeFormatException(reason));
      }
      catch(ArrayIndexOutOfBoundsException aob){
         throw(new RunTimeFormatException(reason));
      }
      try{
         hrs = Integer.parseInt(hours);
      }
      catch(NumberFormatException nfe){
         if(hours.equals("")){
            hrs = 0;
         }
         else{
            String except = "Hours: Please enter a NUMBER for hours";
            throw(new RunTimeFormatException(except));
         }
      }
      try{
         mins = Integer.parseInt(minutes);
      }
      catch(NumberFormatException nfe){
         String except = "Minutes: ";
         except += "Please enter a NUMBER for minutes";
         throw(new RunTimeFormatException(except));
      }
      try{
         secs = Double.parseDouble(seconds);
      }
      catch(NumberFormatException nfe){
         String except = "Seconds: ";
         except += "Please enter a NUMBER for seconds";
         throw(new RunTimeFormatException(except));
      }
      this.setTime(hrs, mins, secs);
   }
   
   //****************************************************************
   //Convert the time from a String to an actual storage value.
   //The String Displays the time in decimal format:  HH.hh or MM.mm
   //****************************************************************
   private void convertTimeDecimal(String timeString, short type)
   throws RunTimeFormatException{
      double currentTime;
      try{
         currentTime = Double.parseDouble(timeString);
         this.setTime(currentTime, type);
      }
      catch(NumberFormatException nfe){
         throw(new RunTimeFormatException());
      }
   }
   
   //****************************************************************
   //Set the Hours attribute
   //****************************************************************
   private void setHours(int hours)
   throws RunTimeFormatException{
      if(hours >= 0){
         this.hours = hours;
      }
      else{
         String message = new String("Hours must be ");
         message += "non-negative!";
         throw(new RunTimeFormatException(message));
      }
   }
   
   //****************************************************************
   //Set the Minutes attribute
   //****************************************************************
   private void setMinutes(int minutes)
   throws RunTimeFormatException{
      if(minutes >= 0){
         this.minutes = minutes;
      }
      else{
         String message = new String("Minutes must be ");
         message += "non-negative!";
         throw(new RunTimeFormatException(message));
      }
   }
   
   //****************************************************************
   //Set the Seconds attribute
   //****************************************************************
   private void setSeconds(double seconds) 
   throws RunTimeFormatException{
      if(seconds >= 0.){
         this.seconds = seconds;
      }
      else{
         String message = new String("Seconds must be ");
         message += "non-negative!";
         throw(new RunTimeFormatException(message));
      }
   }
}
