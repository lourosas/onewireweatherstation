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

public class URLWeatherParser{
   static String [] years = {"2017", "2018", "2019", "2020"};
   static String [] dates = null;
   static String [] months = {"January", "February", "March", "April",
   "May", "June", "July", "August", "September", "October",
   "November", "December"};

   private String minMaxAvgFile = new String("MinMaxAvg.txt");
   private String dailyFile     = new String("Daily.txt");
   private String globalDate    = null;

   /**/
   public static void main(String [] args){
      new URLWeatherParser();
   }

   /**/
   public URLWeatherParser(){
      System.out.println("Hello World");
      this.setUpDates();
      this.parseTheData();
   }

   /**/
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
                     FileWriter fw=new FileWriter(this.dailyFile,true);
                     PrintWriter pw=new PrintWriter(fw, true);
                     pw.println(this.globalDate);
                     pw.println(line);
                     pw.close();
                  }
               }
            }
         }
      }
      catch(MalformedURLException mle){
         System.out.println("URL Exception " + mle);
      }
      catch(IOException ioe){
         System.out.println("IOException " + ioe);
      }
   }

   /**/
   private void parseHighLowAverage(URLConnection conn){
      try{
         conn.connect();
         BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
         String line = null;
         while((line = in.readLine()) != null){
            if(line.contains("drawHighLowTable()")){
               while(!(line = in.readLine()).contains("]);")){
                  if(line.contains("Temperature")){
                     if(!line.contains("N/A")){
                        try{
                           FileWriter fw =
                              new FileWriter(this.minMaxAvgFile,true);
                           PrintWriter pw  = new PrintWriter(fw,true);
                           pw.println(this.globalDate);
                           pw.close();
                        }
                        catch(IOException ioe){
                           ioe.printStackTrace();
                        }
                        this.splitHighLowValues(line);
                     }
                  }
                  else if(line.contains("Humidity")){
                     if(!line.contains("N/A")){
                        this.splitHighLowValues(line);
                     }
                  }
                  else if(line.contains("Dew Point")){
                     if(!line.contains("N/A")){
                        this.splitHighLowValues(line);
                     }
                  }
                  else if(line.contains("Heat Index")){
                     if(!line.contains("N/A")){
                        this.splitHighLowValues(line);
                     }
                  }
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

   /**/
   private void parseTheData(){
      String date  = null;
      String month = null;
      String year  = null;

      for(int i = 3; i < years.length; i++){
         year = years[i];
         for(int j = 0; j < months.length; j++){
            month = months[j];
            for(int k = 0; k < dates.length; k++){
               date = dates[k];
               StringBuffer send = new StringBuffer("http://");
               send.append("68.98.39.39:8000/daily");
               send.append("?month="+month+"&date="+date);
               send.append("&year="+year+"&units=English");
               try{
                  this.globalDate=new String(year+"-"+month+"-"+date);
                  URL url = new URL(send.toString().trim());
                  URLConnection conn = url.openConnection();
                  this.parseDailyData(conn);
                  conn = url.openConnection();
                  this.parseHighLowAverage(conn);
                  //this.parseDailyData(conn);
               }
               catch(MalformedURLException mle){
                  System.out.println("URL Exception " + mle);
               }
               catch(IOException ioe){
                  System.out.println("IO Exception " + ioe);
               }
            }
         }
      }
   }

   /**/
   private void setUpDates(){
      dates = new String[31];
      for(int i = 0; i < 31; i++){
         dates[i] = String.format("%02d", i+1);
      }
   }

   /**/
   private void splitHighLowValues(String line){
      String [] tString = line.split(",");
      String tempString = tString[0].substring(2,
                                               tString[0].length()-1);
      String minString = tString[1].trim();
      minString = minString.substring(1, minString.length()-1);
      double min = Double.parseDouble(minString);
      String maxString = tString[2].trim();
      maxString = maxString.substring(1, maxString.length()-1);
      double max = Double.parseDouble(maxString);
      String avgString = tString[3].trim();
      avgString = avgString.substring(1, avgString.length()-2);
      double avg = Double.parseDouble(avgString);
      try{
         FileWriter  fw = new FileWriter(this.minMaxAvgFile, true);
         PrintWriter pw = new PrintWriter(fw, true);
         pw.println(tempString + ": " + min + " " + max + " " + avg);
         pw.close();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
   }
}
