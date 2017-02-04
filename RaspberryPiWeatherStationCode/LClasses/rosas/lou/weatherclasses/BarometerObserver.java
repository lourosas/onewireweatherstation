/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.WeatherStation;
import rosas.lou.weatherclasses.WeatherEvent;
import rosas.lou.weatherclasses.Units;

public interface BarometerObserver{
   public void updatePressure(WeatherEvent event);
   public void updatePressure(WeatherStorage store);
}
