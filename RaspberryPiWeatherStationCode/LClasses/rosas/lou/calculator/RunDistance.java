//*******************************************************************
//RunDistance Class
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
import myclasses.*;
import rosas.lou.*;
import rosas.lou.calculator.*;

/********************************************************************
RunDistance Class by Lou Rosas
This class encapsulates the distance data used by the Pace
Calculator.  It stores the units:  Miles, Kilometers and Meters
and the value.
********************************************************************/
public class RunDistance{
   //Public Class Constants
   public static final short MILES      = 0;
   public static final short KILOMETERS = 1;
   public static final short METERS     = 2;

   //Private attributes
   double distance;
   short  units;

   //************************Public Methods**************************
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public RunDistance(){
      this(0.0, RunDistance.MILES);
   }

   /*****************************************************************
   Constructor taking the distance value and the units.  Set both.
   Throws RunDistanceException
   *****************************************************************/
   public RunDistance(double distance){
      this(distance, RunDistance.MILES);
   }

   /*****************************************************************
   Constructor taking the distance value and the units.  Set both.
   Throws RunDistanceException
   *****************************************************************/
   public RunDistance(double distance, short units)
   throws RunDistanceException{
      this.setDistance(distance);
      this.setInitialUnits(units);
   }
   
   /*****************************************************************
   Constructor taking the distance input as a String.
   By default, the units value is in Miles.
   Throws:  RunDistanceException
   *****************************************************************/
   public RunDistance(String sDistance) throws RunDistanceException{
      this(sDistance, RunDistance.MILES);
   }

   /*****************************************************************
   Constructor taking the distance input as a String, and the
   Units value
   *****************************************************************/
   public RunDistance(String sDistance, short units)
   throws RunDistanceException{
      this.setDistance(sDistance);
      this.setInitialUnits(units);
   }
   
   /*****************************************************************
   The Copy Constructor.
   Throws:  RunDistanceException.
   *****************************************************************/
   public RunDistance(RunDistance rd) throws RunDistanceException{
      try{
         this.setDistance(rd.getDistance());
         this.setInitialUnits(rd.getUnits());
      }
      catch(NullPointerException npe){
         this.setDistance(0.);
         this.setInitialUnits(RunDistance.MILES);
      }
   }

   /*****************************************************************
   Return the current distance value
   *****************************************************************/
   public double getDistance(){
      return this.distance;
   }

   /*****************************************************************
   Return the current units value
   *****************************************************************/
   public short getUnits(){
      return this.units;
   }

   /*****************************************************************
   Set the distance attribute by taking the String input and
   converting it to a double.  
   Throws:  RunDistanceException
   ****************************************************************/
   public void setDistance(String sDistance)
   throws RunDistanceException{
      double distance = this.convertStringToDistance(sDistance);
      this.setDistance(distance);
   }

   /*****************************************************************
   Set the distance attribute
   Throws:  RunDistanceException
   *****************************************************************/
   public void setDistance(double distance)
   throws RunDistanceException{
      if(distance >= 0.){
         this.distance = distance;
      }
      else{
         String message = "Distance must be non-negative!";
         throw(new RunDistanceException(message));
      }
   }

   /*****************************************************************
   Set the units attribute.  Convert the distance attribute to the
   new value that corresponds to the new unit.
   *****************************************************************/
   public void setUnits(short units){
      if(units >= RunDistance.MILES && units <= RunDistance.METERS){
         this.convertToNewUnits(units);
         this.setInitialUnits(units);
      }
   }

   /*****************************************************************
   Override the toString() method.
   By DEFAULT, rounds to the nearest 100th of a unit!
   And just prints that out!
   *****************************************************************/
   public String toString(){
      String returnString = new String();
      short units = this.getUnits();
      double roundDistance = this.getDistance() * 100.;

      returnString += "" + (Math.round(roundDistance)/100.);

      if(units == RunDistance.MILES){
         returnString += " Miles";
      }
      else if(units == RunDistance.KILOMETERS){
         returnString += " Kilometers";
      }
      else if(units == RunDistance.METERS){
         returnString += " Meters";
      }
      return returnString;
   }

   //*************************Private Methods************************
   //****************************************************************
   //Convert the distance String value to a double and return the
   //the converted value.
   //This is done by parsing the String value.  Throw a
   //RunDistanceException if the parsing is unsuccessfull
   //****************************************************************
   private double convertStringToDistance(String sDistance)
   throws RunDistanceException{
      double distance = 0.;
      try{
         distance = Double.parseDouble(sDistance);
         return distance;
      }
      catch(NumberFormatException nfe){
         String message = "Distance must be a valid number!";
         throw(new RunDistanceException(message));
      }
   }

   //****************************************************************
   //Convert the distance to the new units.  This is a simple
   //conversion.  The only problem is going from metric to English
   //units.
   //****************************************************************
   private void convertToNewUnits(short newUnits){
      short mi = RunDistance.MILES;
      short km = RunDistance.KILOMETERS;
      short m  = RunDistance.METERS;
      if(newUnits >= RunDistance.MILES  && 
         newUnits <= RunDistance.METERS &&
         newUnits != this.getUnits()){
         short currentUnits = this.getUnits();
         //Convert Kilometers to Miles
         if(newUnits == mi && currentUnits == km){
            this.setDistance((this.getDistance())/1.609);
         }
         //Convert Meters to Miles
         else if(newUnits == mi && currentUnits == m){
            this.setDistance((this.getDistance())/1609.);
         }
         //Convert Miles to Kilometers
         else if(newUnits == km && currentUnits == mi){
            this.setDistance((this.getDistance())*1.609);
         }
         //Convert Miles to Meters
         else if(newUnits == m  && currentUnits == mi){
            this.setDistance((this.getDistance())*1609.);
         }
         //Convert Kilometers to Meters
         else if(newUnits == m && currentUnits == km){
            this.setDistance((this.getDistance())*1000.);
         }
         //Convert Meters to Kilometers
         else if(newUnits == km && currentUnits == m){
            this.setDistance((this.getDistance())/1000.);
         }
      }
   }

   //****************************************************************
   //Set the initial units attribute upon construction.
   //****************************************************************
   private void setInitialUnits(short units){
      if(units >= RunDistance.MILES && units <= RunDistance.METERS){
         this.units = units;
      }
      else{
         this.units = RunDistance.MILES;
      }
   }
}
