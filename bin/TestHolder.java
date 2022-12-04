import java.lang.*;
import java.util.*;
import rosas.lou.runnables.Holder;

public class TestHolder{
   private static Holder holder = new Holder(42);
   private static Holder holder1 = new Holder();
   private static Holder holder2 = new Holder(-1);

   /**/
   public static void main(String [] args){
      new TestHolder();
   }

   public TestHolder(){
      //Holder holder = new Holder();
      System.out.println("Hello World!");
      System.out.println(holder.value());
      System.out.println(holder1.value());
      System.out.println(holder2.value());
   }
}
