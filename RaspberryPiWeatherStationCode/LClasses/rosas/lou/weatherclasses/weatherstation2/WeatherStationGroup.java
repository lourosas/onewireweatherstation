/*
A Class by Lou Rosas
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

public class WeatherStationGroup implements ThermometerListener{

   //*********************Constructors*******************************
   /*
   Constructor of no arguments
   */
   public WeatherStationGroup(){}

   //***********************Public Methods***************************
   /*
   Register with the WeatherStation to receive measurements from the
   differents sensors (via the different Listener implementations).
   This is via this initialize() method
   */
   public void initialize(WeatherStation ws){
      //Start by registering as a ThermometerListener interface
      //Which any instance of this class is
      ws.addThermometerListener(this);
   }

   /*
   Implementation of the updateTemperature method via the
   ThermometerListener Interface
   */
   public void updateTemperature
   (
      WeatherStation ws, 
      double         temp,
      Units          units
   ){
      //For now, this is the temporary fix:  will come up with a 
      //more complete solution after the system is running
      System.out.print("\nTemperature:  " + temp);
      if(units == Units.METRIC){
         System.out.print(" \u00B0" + "C\n");
      }
      else if(units == Units.ENGLISH){
         System.out.print(" \u00B0" + "F\n");
      }
      else if(units == Units.ABSOLUTE){
         System.out.print(" K\n");
      }
      else{
         System.out.print(" \u00B0" + "C\n");
      }
   }
}
