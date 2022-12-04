import java.lang.*;
import java.util.*;
import rosas.lou.clock.*;


public class TestTimer implements ClockObserver{
   public static void main(String [] args){
      new TestTimer();
   }

   public TestTimer(){
      LClock clock = new LClock();
      clock.addObserver(this);
      Thread t = new Thread(clock,"clock");
      t.start();
   }

   /*
   Clock Observer Interface implementation
   */
   public void updateTime(long milliseconds){
      System.out.println(milliseconds);
   }
}
