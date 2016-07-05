/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.WeatherStation;
import rosas.lou.weatherclasses.WeatherEvent;
import rosas.lou.weatherclasses.Units;

public interface CalculatedObserver{
   public void updateDewpoint(WeatherEvent event);
   public void updateDewpoint(WeatherStorage store);
   public void updateHeatIndex(WeatherEvent event);
   public void updateWindChill(WeatherEvent event);
}
