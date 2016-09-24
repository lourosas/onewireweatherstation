/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.WeatherStation;
import rosas.lou.weatherclasses.WeatherEvent;
import rosas.lou.weatherclasses.WeatherStorage;
import rosas.lou.weatherclasses.Units;

public interface HumidityObserver{
   public void updateHumidity(WeatherEvent   event);
   public void updateHumidity(WeatherStorage event);
}
