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
import myclasses.*;
import rosas.lou.weatherclasses.*;

public abstract class CurrentWeatherView extends GenericJFrame
implements CurrentWeatherDataObserver{
   /////////////////////////Constructors//////////////////////////////
   public CurrentWeatherView(String title){ super(title); }

   //////////////////////Interface Implementation/////////////////////
   public void updateTemperature(WeatherData data){}
}
