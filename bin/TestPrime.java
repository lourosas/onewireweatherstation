import java.lang.*;
import java.util.*;
import rosas.lou.runnables.*;

public class TestPrime{
   public static void main(String [] args){
      try{
         new TestPrime(args[0]);
      }
      catch(ArrayIndexOutOfBoundsException oob){
         oob.printStackTrace();
      }
   }

   public TestPrime(String number){
      try{
         System.out.println("Hello World");
         long possPrime = Long.parseLong(number);
         long centuries = (possPrime/100) + 1;
         for(int i = 0; i < centuries; ++i){
            //Thread t = new Thread(new TestRange(i*100,possPrime));
            //t.start();
            new Thread(new TestRange(i*100,possPrime)).start();
         }
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
   }
}
