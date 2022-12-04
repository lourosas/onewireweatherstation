import java.lang.*;
import java.util.*;
import java.time.*;

public class TestTimer3{
   public static void main(String [] args){
       new TestTimer3();
   }

   public TestTimer3(){
      try{
         Instant instant1 = Instant.now();
	 while(true){
            Thread.sleep(1000);
            Instant instant2 = Instant.now();
            Duration duration = Duration.between(instant1, instant2);
            System.out.println(duration.toMillis());
         }
      }
      catch(InterruptedException ie){}
      catch(DateTimeException dte){ dte.printStackTrace(); }
      catch(ArithmeticException ae){ ae.printStackTrace(); }
   }
}
