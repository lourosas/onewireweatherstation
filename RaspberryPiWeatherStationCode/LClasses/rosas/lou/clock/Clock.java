/*
*/

package rosas.lou.clock;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.clock.*;

public class Clock implements Runnable{
   private List<ClockObserver> c_o_List = null;
   private long millis;
   private Date date;

   //*********************Constructor********************************
   /*
   Constructor of no arguments
   */
   public Clock(){}

   //*********************Public Methods*****************************
   /*
   */
   public void addClockObserver(ClockObserver co){
      try{
         this.c_o_List.add(co);
      }
      catch(NullPointerException npe){
         this.c_o_List = new Vector<ClockObserver>();
         this.c_o_List.add(co);
      }
   }
   /*
   */
   public long getMilliSeconds(){
      return this.millis;
   }

   /*
   */
   public void initialize(){}

   /*
   */
   public void reset(){}

   /*
   Implementing the run method as part of implementing the Runnable
   interface
   */
   public void run(){
      final int SLEEPTIME = 1000;
      long millis         = 0;
      Date date;
      while(true){
         try{
            Thread.sleep(SLEEPTIME);
            millis = Calendar.getInstance().getTimeInMillis();
            date =   Calendar.getInstance().getTime();
            this.millis = millis;
            this.publishTimeEvents();
         }
         catch(InterruptedException ie){}
      }
   }

   /*
   Simply, just set the clock
   */
   public void set(){}

   /*
   Set to a specific time
   */
   public void set(int hr, int min, int sec){}

   //**********************Private Methods*************************
   /*
   */
   private void publishTimeEvents(){
      Iterator<ClockObserver> i = this.c_o_List.iterator();
      while(i.hasNext()){
         ClockObserver co = i.next();
         co.updateTime(this.getMilliSeconds());
      }
   }
   
   /*
   */
   private void setMilliSeconds(long millis){
      this.millis = millis;
   }
}
