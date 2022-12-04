/////////////////////////////////////////////////////////////////////
/*
want to make something random here in the test.
Will start out easy
*/
import java.lang.*;
import java.util.*;
import rosas.lou.runnables.*;
import java.util.concurrent.locks.*;

public class TestObserver{
   private Lock      lock       = null;
   private Condition toContinue = null;

   public static void main(String [] args){
      new TestObserver();
   }

   public TestObserver(){
      int END_LOOP    = 10;

      System.out.println("Hello World");
      PrimitiveObserver po0 = new PrimitiveObserver("Observer 0");
      PrimitiveObserver po1 = new PrimitiveObserver("Observer 1");
      Thread t0 = new Thread(po0);
      Thread t1 = new Thread(po1);
      t0.start();
      t1.start();
      int count = 0;
      try{
         while(count < END_LOOP){
            po0.setTrigger();
            Thread.sleep(5000);
            po1.setTrigger();
            Thread.sleep(5000);
            po1.setTrigger();
            Thread.sleep(10000);
            //System.out.println("poop\n");
            System.out.println((int)(Math.random() * 10000) + "\n");
            ++count;
         }
         po0.setRun(false);
         po1.setRun(false);
      }
      catch(InterruptedException ie){
         ie.printStackTrace();
      }
      try{
         t0.join();
         t1.join();
      }
      catch(InterruptedException ie){
         ie.printStackTrace();
      }
   }
}
/////////////////////////////////////////////////////////////////////
