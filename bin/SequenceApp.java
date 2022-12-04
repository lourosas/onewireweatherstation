//////////////////////////////////////////////////////////////////////
/*
*/
import java.lang.*;
import java.util.*;
import rosas.lou.runnables.*;

public class SequenceApp{
   public static void main(String [] args){
      new SequenceApp();
   }

   public SequenceApp(){
      System.out.println("Hello World");
      try{
         Request request = new Request();
         Thread th = new Thread(request);
	 th.start();
	 for(int i = 0; i < 24; i++) Thread.sleep(3600000);
         request.quit(true);
         Thread.sleep(2000);
         th.join();
      }
      catch(InterruptedException ie){}
   }
}
