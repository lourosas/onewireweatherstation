/*
Copyright 2018 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.WeatherStation;
import rosas.lou.weatherclasses.WeatherEvent;
import rosas.lou.weatherclasses.Units;

public interface CalculatedObserver{
   public void updateDewpoint(WeatherEvent event);
   public void updateDewpoint(WeatherStorage store);
   public void updateDewpoint(WeatherData data);
   public void updateDewpointAbsolute(double dp);
   public void updateDewpointEnglish(double dp);
   public void updateDewpointMetric(double dp);
   public void updateHeatIndex(WeatherEvent event);
   public void updateHeatIndex(WeatherStorage store);
   public void updateHeatIndex(WeatherData data);
   public void updateHeatIndexAbsolute(double dp);
   public void updateHeatIndexEnglish(double dp);
   public void updateHeatIndexMetric(double dp);
   public void updateWindChill(WeatherEvent event);
}
