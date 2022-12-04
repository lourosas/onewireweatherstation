import java.lang.*;
import java.util.*;
import rosas.lou.runnables.*;

public class TestSampleThread{
   public static void main(String [] args){
      try{
         new TestSampleThread(args[0]);
      }
      catch(ArrayIndexOutOfBoundsException oob){
         new TestSampleThread("0");
      }
   }

   public TestSampleThread(String number){
      int count = 0;
      try{
         count = Integer.parseInt(number);
      }
      catch(NumberFormatException nfe){
         count = 0;
      }
      finally{
         SampleThread st = new SampleThread(count);
         Thread t        = new Thread(st);
         t.start();
         /*
         do{
            try{
               System.out.println("Invoking join()");
               t.join(10000);
               System.out.println("Returned from join()");
            }
            catch(InterruptedException ie){
               ie.printStackTrace();
            }
         }while(st.processingCount > 0);
         System.out.println("The End");
         */
         try{
            System.out.println("Invoking join()");
            t.join(1000);
            System.out.println("Returned from join()");
            Thread.sleep(2300);
            System.out.println("poop");
         }
         catch(InterruptedException ie){
            ie.printStackTrace();
         }
      }
   }
}
