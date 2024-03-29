/*
Copyright 2022 Lou Rosas

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
import rosas.lou.weatherclasses.*;

public interface CurrentWeatherDataObserver{
   public void receiveMessage(String message);
   public void receiveError(String error);
   public void updateDewpoint(WeatherData data);
   public void updateHeatindex(WeatherData data);
   public void updateHumidity(WeatherData data);
   public void updatePressure(WeatherData data);
   public void updateTemperature(WeatherData data);
}
