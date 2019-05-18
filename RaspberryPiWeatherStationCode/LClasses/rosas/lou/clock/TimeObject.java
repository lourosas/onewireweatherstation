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

public class TimeObject{
   private final int INIT           = 0;
   private final int HOURS_IN_A_DAY = 24;

   Calendar calendar;
   State    state;
   long     totalTime;
   long     startTime;
   long     stopTime;
   int      days;
   int      hours;
   int      mins;
   int      secs;
   int      millis;
   int      epochHours;
   int      epochDays;

   //*************************Constructors***************************
   //*******************Constructor of no arguments******************
   public TimeObject(){
      this((long)INIT);
   }

   //****************Constructor taking the total time***************
   public TimeObject(long total){
      this.setUpTheEpoc();
      this.setStopTime((long)INIT);
      this.setStartTime((long)INIT);
      this.setCurrentState(State.STOP);
      this.setDeltaTime((long)INIT);
      this.setTotalTime(total);
   }

   //*********************Public Methods*****************************
   /**/
   public void setCurrentState(State state){
      this.state = state;
   }

   /**/
   public void setStartTime(long start){
      this.startTime = start;
   }
   
   /**/
   public void setStopTime(long stop){
      this.stopTime = stop;
   }

   /**/
   public void setTotalTime(long total){
      this.totalTime = total;
   }
   
   /**/
   public String toString(){
      String timeString = new String("");
   }
   
   //***********************Private Methods**************************
   /**/
   private State getCurrentState(){
      return this.state;
   }
   
   /**/
   private int getDays(){
      return this.days;
   }
   
   /**/
   private int getHours(){
      return this.hours;
   }
   
   /**/
   private int getMilliseconds(){
      return this.millis;
   }
   
   /**/
   private int getMinutes(){
      return this.mins;
   }
   
   /**/
   private int getSeconds(){
      return this.secs;
   }
   
   /**/
   private long getStartTime(){
      return this.startTime;
   }
   
   /**/
   private long getStopTime(){
      return this.stopTime;
   }
   
   /**/
   private void setReadableTime(long time){}

   /*
   Set up the Epoch!!!!
   */
   private void setUpTheEpoch(){
      this.calendar = Calendar.getInstance();
      this.calendar.setTimeInMillis((long)INIT);
      this.epochHours = this.calendar.get(Calendar.HOUR_OF_DAY);
      this.epochDays  = this.calendar.get(Calendar.DAY_OF_YEAR);
   }
}
