/*********************************************************************
*********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;
import gnu.io.*;
import java.io.*;
import myclasses.*;

/*********************************************************************
*********************************************************************/
public class WeatherDataFile{
   private List<WeatherData> weatherDataList;
   private File file;
   private String fileName;
   {
      List<WeatherData> weatherDataList = null;
      File file                         = null;
      String fileName                   = null;
   }

   //**********************Constructors*******************************
   /**
   Constructor of no argument
   */
   public WeatherDataFile(){}

   /**
   Constructor taking the Weather Data List Object
   */
   public WeatherDataFile(List<WeatherData> weatherList){
      this.setWeatherData(weatherList);
   }

   /**
   */
   public WeatherDataFile(List<WeatherData> weatherList, File file){
      this.setWeatherData(weatherList);
      this.setWeatherDataFile(file);
   }

   /**
   Constructor taking the WeatherData List Object and the File to
   Save the data
   */
   public WeatherDataFile
   (
      List<WeatherData> weatherList,
      String currentFileName
   ){
      this.setWeatherData(weatherList);
      this.setWeatherDataFile(currentFileName);
   }
   
   //**********************Public Methods*****************************
   /**
   */
   public void setWeatherData(List<WeatherData> weatherList){
      this.weatherDataList = new LinkedList<WeatherData>(weatherList);
   }

   /**
   */
   public void setWeatherDataFile(File currentFile){
      this.fileName = new String(currentFile.getName());
      this.file     = new File(fileName);
      //Test Prints
      System.out.println("File Name String:  " + this.fileName);
      System.out.println("File Name:  " + this.file);
   }

   /**
   */
   public void setWeatherDataFile(String currentFileName){
      this.fileName = new String(currentFileName);
      this.file     = new File(this.fileName);
   }
}
