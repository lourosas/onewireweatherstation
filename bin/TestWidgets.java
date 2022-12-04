import java.lang.*;
import java.util.*;
import rosas.lou.runnables.*;

public class TestWidgets{
   /**/
   public static void main(String [] args){
      new TestWidgets();
   }

   /**/
   public TestWidgets(){
      System.out.println("Hello World");
      SampleWidgetTest swt = new SampleWidgetTest();
      Thread t0 = new Thread(swt);
      Thread t1 = new Thread(swt);
      t1.start();
      t0.start();
      /*
      new LoggingWidget().doSomething();
      new Widget().doSomething();
      */
   }
}
