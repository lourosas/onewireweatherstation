/////////////////////////////////////////////////////////////////////
/*
*/
import java.lang.*;
import java.util.*;
import rosas.lou.runnables.*;

public class TestThreads{
   public static void main(String [] args){
      new TestThreads();
   }

   public TestThreads(){
      System.out.println("Hello World");
      ThreadTest  tt0 = new ThreadTest("Thread 0");
      ThreadTest  tt1 = new ThreadTest("Thread 1");
      //ThreadTest2 tt2 = new ThreadTest2("Thread 2");
      //ThreadTest2 tt3 = new ThreadTest2("Thread 3");
      ThreadTest tt2 = new ThreadTest("Thread 2");
      ThreadTest tt3 = new ThreadTest("Thread 3");
      Thread t0 = new Thread(tt0);
      Thread t1 = new Thread(tt1);
      Thread t2 = new Thread(tt2);
      Thread t3 = new Thread(tt3);
      t0.start();
      t1.start();
      t2.start();
      t3.start();
      tt1.setTrigger();
      tt2.setTrigger();
      tt0.setTrigger();
      tt3.setTrigger();
      try{
         Thread.sleep(20000);
      }
      catch(InterruptedException e){e.printStackTrace();}
      tt3.setTrigger();
   }
}
