/********************************************************************
* Copyright (C) 2015 Lou Rosas
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
********************************************************************/

package rosas.lou.clock;

import java.lang.*;
import java.util.*;
import java.text.*;
import rosas.lou.clock.*;

public class TimeFormater{
   private static final short HOURS_IN_DAY = 24;
   private static final short MINS_IN_HOUR = 60;

   private long  currentTime;
   private int   currentMillis;
   private int   currentSecs;
   private int   currentMins;
   private int   currentHours;
   private int   currentDays;

   {
      currentMillis = 0;
      currentSecs   = 0;
      currentMins   = 0;
      currentHours  = 0;
      currentTime   = 0;
      currentDays   = 0;
   }

   /////////////////////Constructors//////////////////////////////////
   /**
 * Constructor of no arguments
 * */
   public TimeFormater(){
      this.initialize();
   }

   /**
 * Constructor taking the currentTime
 * */
   public TimeFormater(long time){
      this.initialize(time);
   }

   /////////////////////Public Methods////////////////////////////////
   //
   //
   //
   public String getDays(){
      String days = new String("" + this.currentDays);
      return days;
   }

   //
   //
   //
   public String getHours(){
      String hours = String.format("%02d", this.currentHours);
      return hours;
   }

   //
   //
   //
   public String getMilliSeconds(){
      String millis = String.format("%03d", this.currentMillis);
      return millis;
   }

   //
   //
   //
   public String getMinutes(){
      String minutes = String.format("%02d", this.currentMins);
      return minutes;
   }

   //
   //
   //
   public String getSeconds(){
      String seconds = String.format("%02d", this.currentSecs);
      return seconds;
   }

   //
   //
   //
   public void setTime(long millis){
      this.currentTime = millis;
      this.calculateTime();
   }

   //
   //
   //
   public String toString(){
      String returnString = new String("" + this.currentDays + ":");
      returnString += String.format("%02d:" , this.currentHours);
      returnString += String.format("%02d:" , this.currentMins);
      returnString += String.format("%02d." , this.currentSecs);
      returnString += String.format("%03d"  , this.currentMillis);
      return returnString;
   }

   //////////////////////////Private Methods//////////////////////////

   //
   //
   //
   //
   private void calculateTime(){
      Calendar cal = Calendar.getInstance();
      TimeZone tz = TimeZone.getTimeZone("UTC");
      cal.setTimeZone(tz);
      cal.setTimeInMillis(this.currentTime);
      this.currentMillis = cal.get(Calendar.MILLISECOND);
      this.currentSecs   = cal.get(Calendar.SECOND);
      this.currentMins   = cal.get(Calendar.MINUTE);
      this.currentHours  = cal.get(Calendar.HOUR_OF_DAY);
      this.currentDays   = cal.get(Calendar.DAY_OF_YEAR) - 1;
   }

   //
   //
   //
   //
   private void initialize(){
      this.initialize(this.currentTime);
   }

   //
   //
   //
   //
   private void initialize(long time){
      Calendar cal = Calendar.getInstance();
      TimeZone tz = TimeZone.getTimeZone("UTC");
      cal.setTimeZone(tz);
      this.currentTime = time;
      cal.setTimeInMillis(this.currentTime);
      this.currentMillis = cal.get(Calendar.MILLISECOND);
      this.currentSecs   = cal.get(Calendar.SECOND);
      this.currentMins   = cal.get(Calendar.MINUTE);
      this.currentHours  = cal.get(Calendar.HOUR_OF_DAY);
      this.currentDays   = cal.get(Calendar.DAY_OF_YEAR) - 1;
   }
}
