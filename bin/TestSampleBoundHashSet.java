//////////////////////////////////////////////////////////////////////
import java.lang.*;
import java.util.*;
import java.util.concurrent.*;
import rosas.lou.runnables.*;

public class TestSampleBoundHashSet{
   /**/
   public static void main(String [] args){
      new TestSampleBoundHashSet();
   }

   /**/
   public TestSampleBoundHashSet(){
      System.out.println("Hello World");
      BoundedHashSet<Integer> bhs = new BoundedHashSet<Integer>(1);
      SampleBoundHashSet hashSet  = new SampleBoundHashSet(bhs);
      Thread t = new Thread(hashSet);
      t.start();
      Thread w = new Thread(hashSet);
      w.start();
      Thread u = new Thread(hashSet);
      u.start();
      hashSet.setRemove(true);
      hashSet.setRemoveMonitor(true);
   }
}
//////////////////////////////////////////////////////////////////////
