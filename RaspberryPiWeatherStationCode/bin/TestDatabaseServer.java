import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestDatabaseServer{
   public static void main(String [] args){
      new TestDatabaseServer();
   }

   public TestDatabaseServer(){
      SimpleServer wdbs = new WeatherDatabaseServer();
      Thread thread = new Thread(wdbs);
      thread.start();
   }
}
