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
import rosas.lou.weatherclasses.*;

public abstract class WeatherDataPublisher{
   public abstract void addBarometerObserver(BarometerObserver bo);
   public abstract void removeBarometerObserver(BarometerObserver bo);
   public abstract void addCalculatedObserver(CalculatedObserver co);
   public abstract void removeCalculatedObserver(CalculatedObserver co);
   public abstract void addTemperatureHumidityObserver(
                               TemperatureHumidityObserver observer);
   public abstract void removeTemperatureHumidityObserver(
                               TemperatureHumidityObserver observer);
   
   protected abstract void publishTemperature(WeatherData data);
   protected abstract void publishTemperature(double temp,Units units);
   protected abstract void publishHumidity(WeatherData data);
   protected abstract void publishHumidity(double humidity);
   protected abstract void publishBarometricPressure(WeatherData data);
   protected abstract void publishBarometricPressure(double p,Units u);
   protected abstract void publishDewpoint(WeatherData data);
   protected abstract void publishDewpoint(double dp, Units units);
   protected abstract void publishHeatIndex(WeatherData data);
   protected abstract void publishHeatIndex(double hI, Units units);
}
