//////////////////////////////////////////////////////////////////////
/*
*/
//////////////////////////////////////////////////////////////////////
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import com.sun.net.httpserver.*;

public class URLWeatherParserImproved{
   /**/
   public static void main(String [] args){
      new URLWeatherParserImproved();
   }

   public URLWeatherParserImproved(){
      System.out.println("Hello World");
      this.connectAndParse();
      //this.printWebPage();
   }

   private void connectAndParse(){
      StringBuffer send = new StringBuffer("http://");
      send.append("68.110.91.225:8000/daily");
      send.append("?month=October"+"&date=27");
      send.append("&year=2020"+"&units=English");
      System.out.println(send);
      System.out.println();
      try{
         URL url = new URL(send.toString().trim());
         URLConnection conn = url.openConnection();
         this.parseDailyData(conn);
      }
      catch(MalformedURLException mle){}
      catch(IOException ioe){}
   }

   private void parseDailyData(URLConnection conn){
      try{
         conn.connect();
         BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
         String line = null;
         while((line = in.readLine()) != null){
            if(line.contains("theData.addRows")){
               while(!(line = in.readLine()).contains("]);")){
                  if(line.length() > 0){
                     System.out.println(line);
                  }
               }
            }
         }
      }
      catch(MalformedURLException mle){ mle.printStackTrace(); }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }

   private void printWebPage(){
      StringBuffer send = new StringBuffer("http://");
      send.append("68.110.91.225:8000/daily");
      send.append("?month=October"+"&date=24");
      send.append("&year=2020"+"&units=English");
      System.out.println(send);
      System.out.println();
      try{
         URL  url = new URL(send.toString().trim());
         URLConnection conn = url.openConnection();
         BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
         String line = null;
         while((line = in.readLine()) != null){
            System.out.println(line);
         }
      }
      catch(MalformedURLException mle){}
      catch(IOException ioe){}
   }
}
