import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class TestDatabaseClient{
   public static void main(String [] args){
      new TestDatabaseClient();
   }

   public TestDatabaseClient(){
      WeatherDatabaseClient client = new WeatherDatabaseClient();
      String [] dates = new String[3];
      dates[0] = "September";
      dates[1] = "07";
      dates[2] = "2020";
      client.requestData("temperature", dates);
      client.requestData("humidity", dates);
      client.requestData("pressure", dates);
      client.requestData("dewpoint", dates);
      client.requestData("heatindex", dates);
      /*
      int count = 0;
      do{
         try{
            Thread.sleep(2000);
            client.requestData();
         }
         catch(InterruptedException ie){}
      }while(++count < 5);
      */
   }
}
