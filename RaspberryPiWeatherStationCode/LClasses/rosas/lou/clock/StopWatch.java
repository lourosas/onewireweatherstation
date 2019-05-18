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
import java.text.DateFormat;
import rosas.lou.clock.WatchMechanism;
import rosas.lou.clock.TimeFormater;
import rosas.lou.clock.ClockState;

public class StopWatch implements Runnable{
   private long  lapTime;
   private int   querryTime;
   private long  startTime;
   private long  stopTime;
   private long  elapsedTime;
   private LinkedList<Long> lapTimes;
   private List<TimeListener> tl_List;
   private Date startDate;
   private Date currentDate;
   private WatchMechanism watchMechanism;
   private State state;
   //Set this here, but not prepared to set up a Singleton, just yet
   private static StopWatch singleton;
   private RunState runState;
   private TimeFormater timeFormater;
   
   {
      lapTime        = 0;
      querryTime     = 1000;  //Initialize to 1 second of querry
      startTime      = 0;
      stopTime       = 0;
      elapsedTime    = 0;
      startDate      = null;
      currentDate    = null;
      lapTimes       = null;
      tl_List        = null;
      watchMechanism = null;
      timeFormater   = null;
      state          = State.STOP;
      runState       = RunState.RUN;
   }

   //////////////////////Constructor//////////////////////////////////
   /**
   */
   public StopWatch(){
      this.watchMechanism = WatchMechanism.getInstance();
      this.watchMechanism.setRunState(RunState.RUN);
      //Start the Watch Mechanism
      Thread t = new Thread(this.watchMechanism);
      t.start();
   }

   /**
   Constructor setting the Querry Time in Milliseconds
   */
   public StopWatch(int time){
      this.watchMechanism = WatchMechanism.getInstance();
      this.setQuerryTime(time);
      //Start the Watch Mechanism
      Thread t = new Thread(this.watchMechanism);
      t.start();
   }

   ///////////////////Public Methods//////////////////////////////////
   /**
 * */
   public void addTimeListener(TimeListener tl){
      try{
         this.tl_List.add(tl);
      }
      catch(NullPointerException npe){
         this.tl_List = new LinkedList<TimeListener>();
         this.tl_List.add(tl);
      }
      finally{
         this.publishCurrentState();
         this.publishElapsedTime();
      }
   }

   /**
   */
   public void kill(){
      this.setRunState(RunState.STOP);
      this.watchMechanism.setRunState(RunState.STOP);
   }

   /**
   */
   public void lap(){
      long currentTime = this.watchMechanism.getCurrentTime();
      try{
         this.setState(State.LAP);
         this.lapTime = currentTime - this.lapTime;
         this.lapTimes.add(this.lapTime);
      }
      catch(NullPointerException npe){
         this.lapTimes = new LinkedList<Long>();
         this.lapTimes.add(new Long(this.lapTime));
      }
      finally{
         this.lapTime = currentTime;
         this.publishLapTimes();
         this.setState(State.RUN);
      }
   }

   /**
   */
   public void reset(){
      try{
         //Set the State to Reset
         this.setState(State.RESET);
         //Erase the Lap Times Stack
         this.lapTimes.clear();
      }
      catch(NullPointerException npe){}
      finally{
         //Reset all the time data
         this.startTime   = 0;
         this.stopTime    = this.startTime;
         this.elapsedTime = this.startTime;
         this.lapTime     = this.startTime;
         //Publish the Current Elapsed Time (which will send along
         //the state, as well)
         this.publishElapsedTime();        
         //Go ahead a publish the new time
         //Set the State to Stop
         this.setState(State.STOP);
      }
   }

   /**
   Implementing the run() method as part of the Runnable Interface
   */
   public void run(){
      //How do I want to proceed with this???  Good Question
      while(this.getRunState() == RunState.RUN){
         try{
            if(this.getState() == State.RUN ||
               this.getState() == State.LAP){
               this.running();
            }
            Thread.sleep(this.querryTime);
         }
         catch(InterruptedException ie){}
      }
   }

   /**
   */
   public void setQuerryTime(int time){
      this.querryTime = time;
   }

   /**
   */
   public void start(){
      this.setState(State.START);
      this.publishCurrentState();
      this.startTime = this.watchMechanism.getCurrentTime();
      this.startDate = this.watchMechanism.getCurrentDate();
      this.lapTime   = this.startTime;
      this.setState(State.RUN);
   }

   /**
   */
   public void stop(){
      this.setState(State.STOP);
      this.stopTime = this.watchMechanism.getCurrentTime();
      this.calculateElapsedTime();
      this.publishElapsedTime();
   }

   ////////////////////////////Private Methods////////////////////////
   /**
   */
   private void calculateElapsedTime(){
      //This may have to change:  based on the different states
      this.elapsedTime += this.stopTime - this.startTime;
      //this.elapsedTime = this.stopTime - this.startTime;
      this.startTime   = this.stopTime;
   }

   /**
 * */
   private RunState getRunState(){
      return this.runState;
   }

   /**
   */
   private State getState(){
      return this.state;
   }

   /**
   */
   private void publishCurrentState(){
      ClockState clockState = new ClockState(this.getState());
      try{
         //Send it to the TimeListeners
         Iterator<TimeListener> it = this.tl_List.iterator();
         while(it.hasNext()){
            TimeListener tl = it.next();
            tl.update(clockState);
         }
      }
      catch(NullPointerException npe){}
   }

   /**
   */
   private void publishElapsedTime(){
      try{
         this.timeFormater.setTime(this.elapsedTime);
      }
      catch(NullPointerException npe){
         this.timeFormater = new TimeFormater(this.elapsedTime);
      }
      try{
         //Send to the TimeListeners
         Iterator<TimeListener> it = this.tl_List.iterator();
         ClockState clockState = new ClockState(this.getState());
         while(it.hasNext()){
            TimeListener tl = it.next();
            tl.update(this.timeFormater);
            tl.update(this.timeFormater, clockState);
            tl.update(this);
         }
      }
      catch(NullPointerException npe){}
   }

   /**
   */
   private void publishLapTimes(){
      Stack<TimeFormater> tfStack = new Stack<TimeFormater>();
      ClockState clockState       = new ClockState(this.getState());
      Iterator it = this.lapTimes.iterator();
      while(it.hasNext()){
         TimeFormater timeFormatter =
                                    new TimeFormater((long)it.next());
         tfStack.push(timeFormatter);
      }
      try{
         Iterator<TimeListener> tl_I = this.tl_List.iterator();
         while(tl_I.hasNext()){
            TimeListener tl = tl_I.next();
            tl.update(tfStack, clockState);
         }
      }
      catch(NullPointerException npe){}
   }

   /**
   */
   private void running(){
      this.stopTime = this.watchMechanism.getCurrentTime();
      this.calculateElapsedTime();
      this.publishElapsedTime();
   }

   /**
 * */
   private void setRunState(RunState state){
      this.runState = state;
   }

   /**
   */
   private void setState(State state){
      this.state = state;
   }
}

//////////////////////////////////////////////////////////////////////
