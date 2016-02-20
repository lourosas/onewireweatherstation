/*
The ThermometerListener interface.  Used by the WeatherStation
instance to notify the instances implementing this interface of a
change in the state of a Thermometer
An Interface By Lou Rosas
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.WeatherStation;
import rosas.lou.weatherclasses.Units;

public interface ThermometerListener{
   /*
   Update the Temperature Data upon notification of a change in
   state of a Thermometer
   */
   public void updateTemperature
   (
      WeatherStation ws,
      double temp,
      Units units
   );
}
