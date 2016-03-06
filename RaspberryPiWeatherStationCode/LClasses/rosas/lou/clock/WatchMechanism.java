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

import java.util.*;
import java.lang.*;
import rosas.lou.clock.Clock;

public class WatchMechanism extends Clock implements Runnable{
   private static final short WAIT = 1; //Wait time in milliseconds
   private long currentTime;
   private Date currentDate;
   private RunState state;
   private static WatchMechanism singleton;
   {
      currentTime = 0;
      currentDate = null;
      state       = RunState.STOP;
      singleton   = null;
   }
   /////////////////////////Constructor///////////////////////////////
   /**
   Constructor of no arguments
   */
   private WatchMechanism(){
      this.currentTime = this.getTime();
      this.currentDate = this.getDate();
   }

   ///////////////////////Public Methods//////////////////////////////
   /**
   static singleton instance
   */
   public static WatchMechanism getInstance(){
      if(singleton == null){
         singleton = new WatchMechanism();
      }
      return singleton;
   }
   
   /**
   */
   public long getCurrentTime(){
      return this.currentTime;
   }

   /**
   */
   public Date getCurrentDate(){
      return this.currentDate;
   }

   /**
   Implementing the run() method in the Runnable Interface
   */
   public void run(){
      while(this.state == RunState.RUN){
         try{
            this.currentTime = this.getTime();
            this.currentDate = this.getDate();
            Thread.sleep(WAIT);
         }
         catch(InterruptedException ie){}
      }
   }

   /**
   */
   public void setRunState(RunState state){
      this.state = state;
   }
}
