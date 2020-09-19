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

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.*;
//import gnu.io.*;
//import com.dalsemi.onewire.utils.Convert;


public interface WeatherData{

   //***********************Public Methods***************************
   /*
   */
   public final double DEFAULTVALUE    = -999.9;
   public final double DEFAULTHUMIDITY = -99.9;
   public Calendar calendar();
   public void data(Units units,double value,String message,Calendar cal);
   public void data(Units units,double value,String message);
   public void data(Units units,double value);
   public double absoluteData();
   public double englishData();
   public double metricData();
   public double percentageData();
   public String message();
   public WeatherDataType type();
   public String toStringAbsolute();
   public String toStringEnglish();
   public String toStringMetric();
   public String toStringPercentage();
}
