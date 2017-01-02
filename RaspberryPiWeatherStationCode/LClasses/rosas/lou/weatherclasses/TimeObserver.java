//******************************************************************
//Time Observer Interface
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************

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
