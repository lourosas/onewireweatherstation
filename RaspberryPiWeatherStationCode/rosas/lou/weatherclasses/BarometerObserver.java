/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.WeatherStation;
import rosas.lou.weatherclasses.WeatherEvent;
import rosas.lou.weatherclasses.Units;
import rosas.lou.weatherclasses.WeatherData;

public interface BarometerObserver{
   public void updatePressure(WeatherEvent event);
   public void updatePressure(WeatherStorage store);
   public void updatePressure(WeatherData data);
   public void updatePressureAbsolute(double data);
   public void updatePressureEnglish(double data);
   public void updatePressureMetric(double data);
}
