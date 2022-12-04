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

public class TestURLStringRequest{
   /**/
   public static void main(String [] args){
      new TestURLStringRequest();
   }

   /**/
   public TestURLStringRequest(){
      System.out.println("Hello World");
      StringBuffer send = new StringBuffer("http://");
      send.append("68.98.39.39:8000/daily");
      send.append("?month=August&date=06&year=2019");
      send.append("&units=English");
      try{
         System.out.println(send);
         URL url = new URL(send.toString().trim());
         URLConnection conn = url.openConnection();
         conn.connect();
         BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
         String line = null;
         while((line = in.readLine()) != null){
            //System.out.println(line);
            if(line.contains("drawHighLowTable()")){
               System.out.println(line);
               while(!(line = in.readLine()).contains("]);")){
                  if(line.contains("Temperature")){
                     System.out.println(line);
                  }
                  else if(line.contains("Humidity")){
                     System.out.println(line);
                  }
                  else if(line.contains("Dew Point")){
                     System.out.println(line);
                  }
                  else if(line.contains("Heat Index")){
                     System.out.println(line);
                  }
               }
            }
            else if(line.contains("tempdata.addRows")){
               while(!(line = in.readLine()).contains("]);")){
                  System.out.println(line);
               }
            }
            else if(line.contains("humdata.addRows")){
               while(!(line = in.readLine()).contains("]);")){
                  System.out.println(line);
               }
            }
            else if(line.contains("presdata.addRows")){
               while(!(line = in.readLine()).contains("]);")){
                  System.out.println(line);
               }
            }
            else if(line.contains("dpdata.addRows")){
               while(!(line = in.readLine()).contains("]);")){
                  System.out.println(line);
               }
            }
            else if(line.contains("hidata.addRows")){
               while(!(line = in.readLine()).contains("]);")){
                  System.out.println(line);
               }
            }
         }
      }
      catch(MalformedURLException mle){
         System.out.println("URL Exception " + mle);
      }
      catch(IOException ioe){
         System.out.println("IO Exception " + ioe);
      }
   }
}
