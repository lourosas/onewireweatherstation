//////////////////////////////////////////////////////////////////////
import java.lang.*;
import java.util.*;
import java.util.concurrent.*;
import rosas.lou.runnables.*;

public class TestPreloader{
   /**/
   public static void main(String [] args){
      new TestPreloader();
   }

   /**/
   public TestPreloader(){
      try{
         System.out.println("Hello World");
         Preloader preloader = new Preloader();
         System.out.println(preloader.get());
      }
      catch(NullPointerException npe){
         System.out.println("Product Info is NULL");
      }
      catch(Exception e){ e.printStackTrace(); }
   }
}
