/*
A Class by Lou Rosas
*/

package rosas.lou.weatherstation;

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
   ){}
}
