/////////////////////////////////////////////////////////////////////
/*
*/
import java.lang.*;
import java.util.*;
import rosas.lou.runnables.*;

public class TestThreads3{
   public static void main(String [] args){
      new TestThreads3();
   }

   public TestThreads3(){
      Producer p = new Producer();
      Thread  t1 = new Thread(p);
      Consumer c = new Consumer(p);
      Thread  t2 = new Thread(c);

      t1.start();
      t2.start();
   }
}
