import java.lang.*;
import java.util.*;
import rosas.lou.clock.*;

public class TestTimer4{
   public static void main(String [] args){
      new TestTimer4();
   }

   public TestTimer4(){
      LClock clock   = new LClock();
      LTimer timer   = new LTimer(clock);
      LTimerController controller = new LTimerController();
      LTimerView ltv = new LTimerView("Test Timer", controller);
      //Will have to come up with a better way, but do this for now
      //Technically, everything should go through the Controller
      timer.addSubscriber(ltv);
      controller.addView(ltv);
      controller.addModel(timer);
      Thread t = new Thread(clock, "clock");
      t.start();
   }
}
