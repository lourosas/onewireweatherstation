import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestDatabaseClient{
   public static void main(String [] args){
      new TestDatabaseClient();
   }

   public TestDatabaseClient(){
      WeatherDatabaseClient client = new WeatherDatabaseClient();
      int count = 0;
      do{
         try{
            Thread.sleep(2000);
            client.requestData();
         }
         catch(InterruptedException ie){}
      }while(++count < 5);
   }
}
