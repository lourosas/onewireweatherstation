import java.lang.*;
import java.util.*;
import rosas.lou.clock.*;
import myclasses.*;

public class TestCountdownTimer{
   public static void main(String [] args){
      new TestCountdownTimer();
   }

   public TestCountdownTimer(){
      LClock clock = new LClock();
      CountdownTimer countdownTimer = new CountdownTimer(clock);
      CountdownTimerController controller = new CountdownTimerController();
      CountdownTimerView view = new CountdownTimerView("Countdown",controller);
      countdownTimer.addSubscriber(view);
      controller.addModel(countdownTimer);
      controller.addView(view);

      Thread t = new Thread(clock, "clock");
      t.start();

      /*
      countdownTimer.start(65);
      try{ Thread.sleep(20000); }
      catch(InterruptedException ie){}
      countdownTimer.stop();
      try{ Thread.sleep(14000); }
      catch(InterruptedException ie){}
      countdownTimer.start();
      try{ Thread.sleep(15000); }
      catch(InterruptedException ie){}
      countdownTimer.stop();
      try{ Thread.sleep(25000); }
      catch(InterruptedException ie){}
      countdownTimer.start();
      try{ t.join(); }
      catch(InterruptedException ie){}
      */
   }
}
