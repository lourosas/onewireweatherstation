/********************************************************************
//******************************************************************
//Weather Client Observer Interface
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
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public interface WeatherClientObserver{
   public void alertDewpointTimeout();
   public void alertMissionTimeout();
   public void alertHeatIndexTimeout();
   public void alertHumidityTimeout();
   public void alertPressureTimeout();
   public void alertTemperatureTimeout();
   public void updateDewpointData(List<String> dewpointData);
   public void updateHeatIndexData(List<String> heatIndexData);
   public void updateHumidityData(List<String> humidityData);
   public void updateMissionData(List<String> missionData);
   public void updatePressureData(List<String> pressureData);
   public void updateTemperatureData(List<String> tempData);
}
