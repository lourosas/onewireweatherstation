/*
*/

package rosas.lou.clock;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.clock.*;

public class LTimer implements ClockObserver{

   private State currentState, previousState;
   
   //Instances
   private List<TimeObserver> t_o_List = null;
   
   //Primatives
   private boolean toPublish;
   private int hour, min, sec;
   private long millisecs, startTime, stopTime, elapsedTime;
   private long totalsecs;
   
   //*********************Constructor*******************************
   /*
   Constructor of no arguments
   */
   public LTimer(){
      this.initialize();
   }

   //*********************Public Methods****************************
   /*
   */
   public void addTimerObserver(TimeObserver to){
      try{
         this.t_o_List.add(to);
      }
      catch(NullPointerException npe){
         this.t_o_List = new Vector<TimeObserver>();
         this.t_o_List.add(to);
      }
   }
   
   /*
   */
   public int getHours(){
      return this.hour;
   }

   /*
   */
   public int getMinutes(){
      return this.min;
   }

   /*
   */
   public int getSeconds(){
      return this.sec;
   }

   /*
   */
   public long getMilliSecs(){
      return this.millisecs;
   }
   
   /*
   */
   public long getElapsedTime(){
      return this.elapsedTime;
   }
   
   /*
   */
   public long getStartTime(){
      return this.startTime;
   }
   
   /*
   */
   public long getStopTime(){
      return this.stopTime;
   }
   
   /*
   */
   public boolean getToPublish(){
      return this.toPublish;
   }

   /*
   */
   public long getTotalSecs(){
      return this.totalsecs;
   }

   /*
   */
   public void initialize(){
      final int INIT = 0;
      this.setHour(INIT);
      this.setMinute(INIT);
      this.setSecond(INIT);
      this.setMillis(INIT);
      this.setStartTime(INIT);
      this.setStopTime(INIT);
      this.setElapsedTime(this.getStopTime(), this.getStartTime());
      this.setTotalSecs(INIT);
      this.setToPublish(true);
      this.setCurrentState(State.RESET);
      this.setPreviousState(State.RESET);
   }

   /*
   Reset the clock
   */
   public void reset(){
      this.initialize();
      this.publishTimeEvents();
      this.setToPublish(false);
   }

   /*
   */
   public void start(){
      this.setCurrentState(State.START);
      this.setStartTime(this.getMilliSecs());
      this.setToPublish(true);
      //this.publishTimeEvents();
   }
   
   public void stop(){
      this.setStopTime(this.getMilliSecs());
      this.setCurrentState(State.STOP);
      this.publishTimeEvents();
      this.setToPublish(false);
   }
   
   /*
   Implementation of the ClockObserver interface
   */
   public void updateTime(long millisecs){
      this.millisecs = millisecs;
      //this.setCurrentTime(millisecs);
      this.publishTimeEvents();
   }

   //**********************Private Methods***************************
   /*
   */
   private State getCurrentState(){
      return this.currentState;
   }
   
   /*
   */
   private State getPreviousState(){
      return this.previousState;
   }

   /*
   */
   private void publishTimeEvents(){
      try{

         //Test prints:  I currently do not care about accuracy...
         //System.out.println(this.getMilliSecs());
         //System.out.println(this.getDate());
         //String time;
         /*
         String mins = String.format("%02d:", this.min);
         String secs = String.format("%02d.", this.sec);
         if(this.getStart()){
            time = new String(this.hour + ":" + mins + secs);
         }
         else{
            String mils = String.format("%03d", this.millisecs);
            time = new String(this.hour + ":" + mins + secs + mils);
         }
         if(this.getCurrentState() == State.START){
            long currentTime = this.getMilliSecs();
            this.setElapsedTime(currentTime, this.getStartTime());
            while(i.hasNext()){
               TimeObserver t = i.next();
               t.updateTime(this.getElapsedTime());
               System.out.println(this.getElapsedTime());
               //t.updateTime(time);
            }
         }
         */
         if(this.getToPublish()){
            long deltaTime;
            long totalTime;
            State state = this.getCurrentState();
            if(state == State.START){
               deltaTime = this.getMilliSecs() - this.getStartTime();
            }
            else{
               deltaTime = this.getStopTime() - this.getStartTime();
            }
            totalTime = deltaTime + this.getElapsedTime();
            //Publish
            Iterator<TimeObserver> i = this.t_o_List.iterator();
            while(i.hasNext()){
               TimeObserver t = i.next();
               //Send in a TimeObject
            }
            //Set the elapsed time for a Stop
            if(state != State.START){
               this.setElapsedTime(deltaTime);
            }
         }
      }
      catch(NullPointerException npe){
         System.out.println("No Time Observers");
         System.exit(1);
      }
   }
   
   /*
   */
   private void setCurrentTime(long mills){
      this.millisecs = mills;
   }
   
   /*
   */
   private void setHour(int hr){
      this.hour = hr;
   }

   /*
   */
   private void setMinute(int minute){
      this.min = minute;
   }

   /*
   */
   private void setSecond(int second){
      this.sec = second;
   }
   
   /*
   */
   private void setCurrentState(State state){
      if(state == State.STOP  ||
         state == State.START ||
         state == State.RESET){
         this.currentState = state;
      }
      else{
         this.currentState = State.STOP;
      }
   }
   
   /*
   */
   private void setElapsedTime(long current, long previous){
      this.elapsedTime += (current - previous);
   }
   
   /*
   */
   private void setElapsedTime(long deltaTime){
      this.elapsedTime += deltaTime;
   }
   
   /*
   */
   private void setPreviousState(State state){
      if(state == State.STOP  ||
         state == State.START ||
         state == State.RESET){
         this.previousState = state;
      }
      else{
         this.previousState = State.STOP;
      }
   }

   /*
   */
   private void setMillis(long millis){
      this.millisecs = millis;
   }
   
   /*
   */
   private void setStartTime(long startTime){
      this.startTime = startTime;
   }
   
   /*
   */
   private void setStopTime(long stopTime){
      this.stopTime = stopTime;
   }
   
   /*
   */
   private void setToPublish(boolean publish){
      this.toPublish = publish;
   }

   /*
   */
   private void setTotalSecs(long total){
      totalsecs = total;
   }

}
