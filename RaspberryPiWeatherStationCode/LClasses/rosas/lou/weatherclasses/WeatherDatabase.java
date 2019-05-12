/////////////////////////////////////////////////////////////////////
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
/////////////////////////////////////////////////////////////////////

package rosas.lou.weatherclasses;

import java.lang.*;
import java.io.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import java.sql.*;

public interface WeatherDatabase{
   public void temperature(WeatherData data);
   public void humidity(WeatherData data);
   public void barometricPressure(WeatherData data);
   public void dewpoint(WeatherData data);
   public void heatIndex(WeatherData data);
   public List<WeatherData> temperature(String month,String day,String year);
   public List<WeatherData> humidity(String month,String day,String year);
   public List<WeatherData> barometricPressure(String month,String day, String year);
   public List<WeatherData> dewpoint(String month,String day,String year);
   public List<WeatherData> heatIndex(String month,String day,String year);
   public List<String> temperature(String request);
   public List<String> humidity(String request);
   public List<String> barometricPressure(String request);
   public List<String> dewpoint(String request);
   public List<String> heatIndex(String request);
   public WeatherData dewPointAvg(String month,String day, String year);
   public WeatherData dewPointMax(String month,String day, String year);
   public WeatherData dewPointMin(String month,String day, String year);
   public WeatherData heatIndexAvg(String month,String day, String year);
   public WeatherData heatIndexMax(String month,String day, String year);
   public WeatherData heatIndexMin(String month,String day, String year);
   public WeatherData humidityAvg(String month,String day, String year);
   public WeatherData humidityMax(String month,String day, String year);
   public WeatherData humidityMin(String month,String day, String year);
   public WeatherData temperatureAvg(String month,String day,String year);
   public WeatherData temperatureMax(String month,String day,String year);
   public WeatherData temperatureMin(String month,String day,String year);
}
