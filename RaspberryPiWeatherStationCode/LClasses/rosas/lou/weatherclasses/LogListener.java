/**
* Copyright (C) 2012 Lou Rosas
* This file is part of many applications registered with
* the GNU General Public License as published
* by the Free Software Foundation; either version 3 of the License,
* or (at your option) any later version.
* PaceCalculator is distributed in the hope that it will be
* useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.
* If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.LogEvent;

public interface LogListener{
   public void onDewpointLogEvent(LogEvent event);
   public void onExtremeTemperatureLogEvent(LogEvent data,LogEvent time);
   public void onHeatIndexLogEvent(LogEvent event);
   public void onHumidityLogEvent(LogEvent event);
   //Get both the humidity and associated time Event Data
   public void onHumidityLogEvent(LogEvent humidty, LogEvent time);
   public void onHumidityTimeLogEvent(LogEvent event);
   public void onTemperatureLogEvent(LogEvent event);
   //Get both the humidity and associated time Event Data
   public void onTemperatureLogEvent(LogEvent temp, LogEvent time);
   public void onTemperatureTimeLogEvent(LogEvent event);
}