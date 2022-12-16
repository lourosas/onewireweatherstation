//******************************************************************
//Weather Data File Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************
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
