/********************************************************************
<GNU Stuff to go here>
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
