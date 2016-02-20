//******************************************************************
//Pace Calculator Application
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
import java.io.IOException;
import java.io.File;
import rosas.lou.*;
import rosas.lou.calculator.*;

/********************************************************************
The Pace Calculator class by Lou Rosas.  This class is the "Model" 
portion of the Pace Calculator application.
From what I know, the model holds the state of the system.  It
stores and maintains the data related to the system then notifies
the appropriate interests of a change (in its state or data)
Hence, this class holds the data and the state for the Pace
Calculator application.
********************************************************************/
public class PaceCalculator extends Observable{
   //Static Public Units
   public static final short MILES      = 0;
   public static final short KILOMETERS = 1;
   public static final short METERS     = 2;

   //Private attributes here
   private short distanceMode;
   private RunData rundata;
   
   //***********************Public Methods***************************
   /*****************************************************************
   Constructor of no arguments
   Sets the initial units to Miles.
   *****************************************************************/
   public PaceCalculator(){
      this.initializeDistanceUnits(MILES);
      this.initializeRundata();
   }
   
   /*****************************************************************
   Constructor Accepting the Observer.
   This constructor registers the Observer.
   *****************************************************************/
   public PaceCalculator(Observer observer){
      this.initializeDistanceUnits(MILES);
      this.initializeRundata();
      this.addObserver(observer);
   }

   /*****************************************************************
   Given a RunData object, calculate the run pace
   *****************************************************************/
   public void calculatePace(RunData currentData){
      Stack    exceptionStack = new Stack();
      RunDistance runDistance = currentData.getDistance();
      RunTime     runTime     = currentData.getRuntime();
      RunTime     pace;

      double distance = runDistance.getDistance();
      double seconds  = runTime.getTimeInSeconds();
      double paceTime = seconds/distance;
      try{
         if(seconds == 0. && distance == 0.){
            paceTime = 0.;
         }
         else if(seconds > 0. && distance == 0.){
            throw(new ArithmeticException("/ by zero"));
         }
         pace = new RunTime(paceTime, RunTime.SECONDS);
         this.rundata = new RunData(runTime, pace, runDistance);
         this.pingRunData();
      }
      catch(ArithmeticException ae){
         String error = new String(ae.getMessage());
         if(error.equals("/ by zero")){
            exceptionStack.push(ae.getMessage());
            exceptionStack.push("Divide By 0 Error");
            this.setChanged();
            this.notifyObservers(exceptionStack);
            this.clearChanged();
         }
      }
      catch(RunTimeFormatException rtfe){
         exceptionStack.push(rtfe.getReason());
         exceptionStack.push(new String("RunTime Error"));
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
      catch(RunDistanceException rde){
         exceptionStack.push(rde.getMessage());
         exceptionStack.push(new String("RunDistance Error"));
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
   }

   /*****************************************************************
   Given the run distance and run time, compute the pace of the run.
   Broadcast the result for the Observers.
   *****************************************************************/
   public void calculatePace
   (
      String distance,
      String hours,
      String minutes,
      String seconds
   ){
      Stack paceStack = new Stack();
      paceStack.push(seconds);
      paceStack.push(minutes);
      paceStack.push(hours);
      paceStack.push(distance);
      this.calculatePace(paceStack);
   }

   /*****************************************************************
   Calculate the Pace by putting all the required data in a Stack
   and popping that data out in order to calculate the pace
   *****************************************************************/
   public void calculatePace(Stack paceStack){
      Stack exceptionStack   = new Stack();
      try{
         double distance, sec;
         int hour, min;
         short units = this.getDistanceUnits();
         RunTime     runtime, pace;
         RunDistance rundistance;
         distance = this.getDistanceValue((String)paceStack.pop());
         hour     = this.getHourValue((String)paceStack.pop());
         min      = this.getMinValue((String)paceStack.pop());
         sec      = this.getSecValue((String)paceStack.pop());
         //Create the objects needed to calculate the pace
         runtime     = new RunTime(hour, min, sec);
         rundistance = new RunDistance(distance, units);
         pace = new RunTime();
         //Build a RunData object and call calculatePace()
         //with that object
         this.calculatePace(new RunData(runtime, pace, rundistance));
      }
      catch(NumberFormatException nfe){
         exceptionStack.push(nfe.getMessage());
         exceptionStack.push(new String("Number Error"));
         //Notify the Observers of the errors in input.
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
      catch(RunTimeFormatException rtfe){
         exceptionStack.push(rtfe.getReason());
         exceptionStack.push(new String("RunTime Error"));
         //Notify the Observers of the error
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
      catch(RunDistanceException rde){
         exceptionStack.push(rde.getMessage());
         exceptionStack.push(new String("RunDistance Error"));
         //Notify the Observers of the errors in the input
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
   }
   
   /*****************************************************************
   Calculate the Run Distance given a RunData object.
   *****************************************************************/
   public void calculateRunDistance(RunData data){
      Stack exceptionStack = new Stack();
      RunTime runTime = data.getRuntime();
      RunTime pace    = data.getPace();
      RunDistance runDistance;
      
      short  distanceUnits = this.getDistanceUnits();
      double runTimeSecs   = runTime.getTimeInSeconds();
      double paceSecs      = pace.getTimeInSeconds();
      double distance;
      try{
         if(runTimeSecs == 0. && paceSecs == 0.){
            distance = 0.;
         }
         else if(runTimeSecs > 0. && paceSecs == 0.){
            throw(new ArithmeticException("/ by zero"));
         }
         distance        = runTimeSecs/paceSecs;
         runDistance     = new RunDistance(distance, distanceUnits);
         this.rundata    = new RunData(runTime, pace, runDistance);
         this.pingRunData();
      }
      //All other exception should be handled by the the time
      //this method is messaged.  Since the RunDistance object
      //is created by the two RunTime objects created prior to this
      //message, and there is no RunTime constructor called, any
      //and all RunTimeFormatException objects SHOULD BE HANDLED
      //PRIOR to messaging this method.  Thus, the RunTime data
      //should is pristine and when used to create the RunDistance
      //Object, should NOT triger a RunTimeFormatException.
      catch(ArithmeticException ae){
         String error = new String(ae.getMessage());
         if(error.equals("/ by zero")){
            exceptionStack.push(ae.getMessage());
            exceptionStack.push("Divide By 0 Error");
            this.setChanged();
            this.notifyObservers(exceptionStack);
            this.clearChanged();
         }
      }
   }
   
   /*****************************************************************
   Given the Run Time and Run Pace, calculate the Run Distance.
   *****************************************************************/
   public void calculateRunDistance
   (
      String runtimeHrs,
      String runtimeMin,
      String runtimeSec,
      String paceMin,
      String paceSec
   ){
      Stack runtimeStack = new Stack();
      Stack paceStack    = new Stack();
      
      runtimeStack.push(runtimeSec);
      runtimeStack.push(runtimeMin);
      runtimeStack.push(runtimeHrs);
      paceStack.push(paceSec);
      paceStack.push(paceMin);
      this.calculateRunDistance(paceStack, runtimeStack);
   }
   
   /*****************************************************************
   Calculate the Run Distance by putting all the data required to
   calculate the Run Distance in two distinctive Stacks.  On stack
   Contains the Pace information, the other Stack contains the
   Run Time information
   *****************************************************************/
   public void calculateRunDistance(Stack pace, Stack runtime){
      Stack exceptionStack = new Stack();
      String where = new String("Run Time ");
      try{
         int min, hrs;
         double sec;
         RunTime currP, currRT;
         hrs = this.getHourValue((String)runtime.pop());
         min = this.getMinValue((String)runtime.pop());
         sec = this.getSecValue((String)runtime.pop());
         currRT = new RunTime(hrs, min, sec);
         where = new String("Pace ");
         min = this.getMinValue((String)pace.pop());
         sec = this.getSecValue((String)pace.pop());
         currP = new RunTime(0, min, sec);
         this.calculateRunDistance(new RunData(currRT,currP, null));
      }
      catch(NumberFormatException nfe){
         String message = where + nfe.getMessage();
         exceptionStack.push(message);
         exceptionStack.push("Number Error");
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
      catch(RunTimeFormatException rtfe){
         String message = where + rtfe.getMessage();
         exceptionStack.push(message);
         exceptionStack.push(new String("RunTime Error"));
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
      catch(EmptyStackException ese){ //Do this one right!
         ese.printStackTrace();
      }
   }
   
   /*****************************************************************
   Given a RunData object, calculate the run time.
   *****************************************************************/
   public void calculateRunTime(RunData calcData){
      RunDistance runDistance = calcData.getDistance();
      RunTime     pace        = calcData.getPace();
      RunTime     runTime;
      
      double distance = runDistance.getDistance();
      double seconds  = pace.getTimeInSeconds();
      double runtime  = distance * seconds;
      runTime = new RunTime(runtime, RunTime.SECONDS);
      this.rundata = new RunData(runTime, pace, runDistance);
      this.pingRunData();
   }
   
   /*****************************************************************
   Given the run distance, and data related to pace, calculate the
   Run Time.
   *****************************************************************/
   public void calculateRunTime
   (
      String distance,
      String minutes,
      String seconds
   ){
      Stack runTimeStack = new Stack();
      runTimeStack.push(seconds);
      runTimeStack.push(minutes);
      runTimeStack.push(distance);
      this.calculateRunTime(runTimeStack);
   }
   
   /*****************************************************************
   Calculate the Run Time by puttin all the required data in a
   Stack and popping that data as needed for Run Time calculation.
   *****************************************************************/
   public void calculateRunTime(Stack rtStack){
      Stack  exceptionStack  = new Stack();
      try{
         double distance, sec;
         int    min;
         short  units = this.getDistanceUnits();
         RunTime     runtime, pace;
         RunDistance rundistance;
         distance = this.getDistanceValue((String)rtStack.pop());
         min      = this.getMinValue((String)rtStack.pop());
         sec      = this.getSecValue((String)rtStack.pop());
         //Create the objects needed to calculate Run Time
         runtime     = new RunTime();
         pace        = new RunTime(0, min, sec);
         rundistance = new RunDistance(distance, units);
         this.calculateRunTime(new RunData(runtime,pace,rundistance));
      }
      catch(NumberFormatException nfe){
         exceptionStack.push(nfe.getMessage());
         exceptionStack.push(new String("Number Error"));
         //Notify the Observers and let them figure it out
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
      catch(RunTimeFormatException rtfe){
         exceptionStack.push(rtfe.getReason());
         exceptionStack.push(new String("RunTime Error"));
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
      catch(RunDistanceException rde){
         exceptionStack.push(rde.getMessage());
         exceptionStack.push(new String("RunDistance Error"));
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
   }
   
   /*****************************************************************
   Return the Units state.  Return the current distance units used
   for calculation.
   *****************************************************************/
   public short getDistanceUnits(){
      return this.distanceMode;
   }

   /*****************************************************************
   Quit the application.  A real simple exit strategy.
   *****************************************************************/
   public void quit(){
      System.out.println("\nThe Pace Calculator is quitting");
      System.exit(0);
   }
   
   /*****************************************************************
   Notify all the observers of the current Run Data.  In other
   words, notify the observers of the current state of the Model.
   *****************************************************************/
   public void pingRunData(){
      this.setChanged();
      this.notifyObservers(new RunData(this.rundata));
      this.clearChanged();
   }
   
   /*****************************************************************
   Given a file string, save the Run Data.
   *****************************************************************/
   public void saveRunData(String saveFile){
      //Try to implement based on interfaces
      Stack exceptionStack = new Stack();
      try{
         RunData rd = new RunData(this.rundata);
         RunDatafile rdf = new RunDatafile(rd, saveFile);
         rdf.save();
      }
      catch(IOException ioe){
         exceptionStack.push(ioe.getMessage());
         exceptionStack.push("File Save Error");
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
         
      }
   }
   
   /*****************************************************************
   Given a File object, save the Run Data.
   *****************************************************************/
   public void saveRunData(File file){
      Stack exceptionStack = new Stack();
      try{
         RunData rd = new RunData(this.rundata);
         RunDatafile rdf = new RunDatafile(rd, file);
         rdf.save();
      }
      catch(IOException ioe){
         exceptionStack.push(ioe.getMessage());
         exceptionStack.push("File Save Error");
         this.setChanged();
         this.notifyObservers(exceptionStack);
         this.clearChanged();
      }
   }

   /*****************************************************************
   Given a String, set the Distance Units.  Set the Units state
   of the Model.
   *****************************************************************/
   public void setDistanceUnits(String units){
      String useString = units.toUpperCase();
      if(useString.equals("MILES")){
         this.setDistanceUnits(MILES);
      }
      else if(useString.equals("KILOMETERS")){
         this.setDistanceUnits(KILOMETERS);
      }
      else if(useString.equals("METERS")){
         this.setDistanceUnits(METERS);
      }
   }
   
   /*****************************************************************
   Given an integer, set the Distance Units.  Set the Units state
   of the Model.
   *****************************************************************/
   public void setDistanceUnits(int units){
      if(units >= MILES && units <= METERS){
         this.distanceMode = (short)units;
      }
      this.resetPaceAndDistance();
   }

   /*****************************************************************
   Perform simple actions that don't actully modify the state of
   the model, rather notify the observers of simple actions
   requested by the user in the use of the system.
   *****************************************************************/
   public void simpleAction(String action){
      if(action.equals("Clear")){
         this.clearRundata();
      }
      this.setChanged();
      this.notifyObservers(action);
      this.clearChanged();
   }

   //***************************Private Methods**********************
   //****************************************************************
   //****************************************************************
   private void clearRundata(){
      if(this.rundata != null){
         short units  = this.getDistanceUnits();
         this.rundata = new RunData();
         this.rundata.setDistance(new RunDistance(0., units));
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private double getDistanceValue(String disString)
   throws NumberFormatException{
      double distance;
      try{
         distance = Double.parseDouble(disString);
      }
      catch(NumberFormatException nfe){
         NumberFormatException except;
         except = new NumberFormatException("Distance");
         throw(except);
      }
      return distance;
   }

   //****************************************************************
   //
   //****************************************************************
   private int getHourValue(String hourString)
   throws NumberFormatException{
      int hour;
      try{
         hour = Integer.parseInt(hourString);
      }
      catch(NumberFormatException nfe){
         if(hourString.equals("")){
            //If there is nothing in the String, set to 0
            //If there are any non-numbers, set the exception
            hour = 0;
         }
         else{
            NumberFormatException except;
            except = new NumberFormatException("Hours");
            throw(except);
         }
      }
      return hour;
   }

   //****************************************************************
   //
   //****************************************************************
   private int getMinValue(String minString)
   throws NumberFormatException{
      int minutes;
      try{
         minutes = Integer.parseInt(minString);
      }
      catch(NumberFormatException nfe){
         NumberFormatException except;
         except = new NumberFormatException("Minutes");
         throw(except);
      }
      return minutes;
   }
   //****************************************************************
   //
   //****************************************************************
   private double getSecValue(String secString)
   throws NumberFormatException{
      double seconds;
      try{
         seconds = Double.parseDouble(secString);
      }
      catch(NumberFormatException nfe){
         NumberFormatException except;
         except = new NumberFormatException("Seconds");
         throw(except);
      }
      return seconds;
   }
   
   //****************************************************************
   //Default the Distance Units to MILES
   //****************************************************************
   private void initializeDistanceUnits(int units){
      if(units >= MILES && units <= METERS){
         this.distanceMode = (short)units;
      }
      else{
         this.distanceMode = MILES;
      }
   }
   
   //****************************************************************
   //****************************************************************
   private void initializeRundata(){
      this.rundata = new RunData();
   }

   //****************************************************************
   //****************************************************************
   private void resetPaceAndDistance(){
      try{
         RunData     resetData   = new RunData(this.rundata);
         RunDistance runDistance = resetData.getDistance();
         RunTime     runTime     = resetData.getRuntime();
         RunTime     runPace     = resetData.getPace();
         runDistance.setUnits(this.getDistanceUnits());
         resetData.setRunData(runTime, runPace, runDistance);
         this.calculatePace(resetData);
      }
      catch(NullPointerException npe){}
   }
}
