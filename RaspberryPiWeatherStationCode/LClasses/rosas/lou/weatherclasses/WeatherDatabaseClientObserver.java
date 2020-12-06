/********************************************************************
//******************************************************************
//Weather Client Observer Interface
//Copyright (C) 2020 by Lou Rosas
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
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public interface WeatherDatabaseClientObserver{
   public void alertDewpointTimeout();
   public void alertHeatIndexTimeout();
   public void alertHumidityTimeout();
   public void alertPressureTimeout();
   public void alertTemperatureTimeout();
   public void alertNoDewpointData();
   public void alertNoDewpointData(Exception e);
   public void alertNoHeatIndexData();
   public void alertNoHeatIndexData(Exception e);
   public void alertNoHumidityData();
   public void alertNoHumidityData(Exception e);
   public void alertNoPressureData();
   public void alertNoPressureData(Exception e);
   public void alertNoTemperatureData();
   public void alertNoTemperatureData(Exception e);
   public void updateDewpointData(List<WeatherData> data);
   public void updateHeatIndexData(List<WeatherData> data);
   public void updateHumidityData(List<WeatherData> data);
   public void updatePressureData(List<WeatherData> data);
   public void updateTemperatureData(List<WeatherData> data);
}
