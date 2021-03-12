/*
Weather Client Observer Interface
Copyright (C) 2020 by Lou Rosas
This file is part of onewireweatherstation application.
onewireweatherstation is free software; you can redistribute it
and/or modify
it under the terms of the GNU General Public License as published
by the Free Software Foundation; either version 3 of the License,
or (at your option) any later version.
PaceCalculator is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.
If not, see <http://www.gnu.org/licenses/>.
*/
//////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import myclasses.*;
import rosas.lou.weatherclasses.*;

public abstract class WeatherView extends GenericJFrame
implements WeatherDatabaseClientObserver{
   public WeatherView(String title){ super(title); }
   public void displayDewpoint(Units units){}
   public void displayDewpoint(short display){}
   public void displayHeatIndex(Units units){}
   public void displayHeatIndex(short display){}
   public void displayHumidity(short display){}
   public void displayPressure(Units units){}
   public void displayPressure(short display){}
   public void displayTemperature(Units units){}
   public void displayTemperature(short display){}
   public String getDay(){ return null; }
   public String getMonth(){ return null; }
   public String getYear(){ return null; }
   public Units getDewpointUnits(){ return Units.NULL; }
   public Units getHeatIndexUnits(){ return Units.NULL; }
   public Units getHumidityUnits(){ return Units.NULL; }
   public Units getPressureUnits(){ return Units.NULL; }
   public Units getTemperatureUnits(){ return Units.NULL; }
   public void alertDewpointTimeout(){}
   public void alertHeatIndexTimeout(){}
   public void alertHumidityTimeout(){}
   public void alertPressureTimeout(){}
   public void alertTemperatureTimeout(){}
   public void alertNoDewpointData(){}
   public void alertNoDewpointData(Exception e){}
   public void alertNoDewpointMinMaxAvg(Exception e){}
   public void alertNoHeatIndexData(){}
   public void alertNoHeatIndexData(Exception e){}
   public void alertNoHeatIndexMinMaxAvg(Exception e){}
   public void alertNoHumidityData(){}
   public void alertNoHumidityData(Exception e){}
   public void alertNoHumidityMinMaxAvg(Exception e){}
   public void alertNoPressureData(){}
   public void alertNoPressureData(Exception e){}
   public void alertNoTemperatureData(){}
   public void alertNoTemperatureData(Exception e){}
   public void alertNoTemperatureMinMaxAvgData(Exception e){}
   public void updateAddress(String address){}
   public void updatePort(String port){}
   public void updateDewpointData(List<WeatherData> data){}
   public void updateDewpointMinMaxAvg(List<WeatherData> data){}
   public void updateHeatIndexData(List<WeatherData> data){}
   public void updateHeatIndexMinMaxAvg(List<WeatherData> data){}
   public void updateHumidityData(List<WeatherData> data){}
   public void updateHumidityMinMaxAvg(List<WeatherData> data){}
   public void updatePressureData(List<WeatherData> data){}
   public void updateTemperatureData(List<WeatherData> data){}
   public void updateTemperatureMinMaxAvg(List<WeatherData> data){}
}
//////////////////////////////////////////////////////////////////////
