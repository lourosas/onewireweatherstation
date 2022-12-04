import java.lang.*;
import java.util.*;
import rosas.lou.clock.*;

public class TestTimer2{
   public static void main(String [] args){
      new TestTimer2();
   }

   public TestTimer2(){
      LClock clock = new LClock();
      LTimer timer = new LTimer(clock);
      Thread t = new Thread(clock, "clock");
      t.start();
      try{
         timer.start();
         //Thread.sleep(10000);
         //timer.stop();
         //Thread.sleep(3000);
         //timer.start();
         Thread.sleep(600000);
         timer.stop();
	 clock.stop();
         t.join();
      }
      catch(InterruptedException ie){}
   }
}
