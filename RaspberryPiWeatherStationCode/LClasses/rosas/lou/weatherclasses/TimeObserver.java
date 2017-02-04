/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.WeatherStation;
import rosas.lou.weatherclasses.WeatherEvent;
import rosas.lou.weatherclasses.Units;

public interface TimeObserver{
   public void updateTime();
   public void updateTime(String formatedTime);
   public void updateTime(String mo, String day, String year);
   public void updateTime(String yr, String mo,  String day,
                          String hr, String min, String sec);
}