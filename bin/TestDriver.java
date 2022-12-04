import java.lang.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import rosas.lou.runnables.*;

public class TestDriver{
   /**/
   public static void main(String [] args){
      new TestDriver();
   }

   /**/
   public TestDriver(){
      int nThreads = 5;
      final CountDownLatch startGate = new CountDownLatch(1);
      final CountDownLatch endGate   = new CountDownLatch(nThreads);

      for(int i = 0; i < nThreads; ++i){
         Worker w = new Worker(startGate, endGate);
         Thread t = new Thread(w);
         t.start();
      }
      try{
         this.doSomethingElse();
         //System.out.println(startGate.getCount());
         startGate.countDown();
         //System.out.println(startGate.getCount());
         //this.doSomethingElse();
         endGate.await();
         //System.out.println(endGate.getCount());
         this.doSomethingElse();
      }
      catch(InterruptedException ie){
         ie.printStackTrace();
      }
   }

   /**/
   private void doSomethingElse(){
      try{
         System.out.print("doSomethingElse():  ");
         System.out.println(Thread.currentThread().getName());
         System.out.println(Calendar.getInstance().getTimeInMillis());
         Thread.sleep(10000);
         System.out.println(Calendar.getInstance().getTimeInMillis());
      }
      catch(InterruptedException ie){}
   }
}
