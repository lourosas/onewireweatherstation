/////////////////////////////////////////////////////////////////////
/**/
import java.lang.*;
import java.util.*;

public class TestThreads2{
   public static int pressureGuage = 0;
   public static int safetyLimit   = 20;

   public static void main(String [] args){
      new TestThreads2();
   }

   public TestThreads2(){
      Pressure [] p = new Pressure[10];
      Thread [] t   = new Thread[10];
      for(int i = 0; i < 10; i++){
         p[i] = new Pressure();
         t[i] = new Thread(p[i]);
         t[i].start();
      }
      try{
         for(int i = 0; i < 10; i++){
            t[i].join();
         }
      }
      catch(Exception e){}
      System.out.println("Guage:  " + pressureGuage);
   }
}

class Pressure implements Runnable{
//   static Object o = new Object();

   public Pressure(){}

   public void run(){
      this.raisePressure();
   }

   private void raisePressure(){
//      synchronized(o){
      if(TestThreads2.pressureGuage < TestThreads2.safetyLimit - 5){
         System.out.println(Thread.currentThread().getName());
         try{ Thread.sleep(1000); } catch(Exception e){}
         TestThreads2.pressureGuage += 15;
      }
//      }
   }
}
