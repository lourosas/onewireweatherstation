/********************************************************************
<GNU Stuff to go here>
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.io.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import java.sql.*;

public class Database{
   private static Database instance;

   {
      instance = null;
   }

   /*****************************************************************
   *****************************************************************/
   //////////////////////////Public Methods//////////////////////////
   /**
   **/
   public static Database getInstance(){
      if(instance == null){
         instance = new Database();
      }
      return instance;
   }

   /**
   **/
   public List<String> dewpointFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> returnList = null;
      return returnList;
   }

   /**
   **/
   public List<String> heatIndexFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> returnList = null;
      return returnList;
   }

   /**
   **/
   public List<String> humidityFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> returnList = null;
      return returnList;
   }

   /**
   **/
   public List<String> pressureFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> returnList = null;
      return returnList;
   }

   /**
   **/
   public List<String> requestData(String request){
      List<String> returnList = null;
      return returnList;
   }

   /**
   **/
   public List<String> temperatureFromDatabase
   (
      String month,
      String day,
      String year
   ){
      List<String> returnList = null;
      return returnList;
   }

   /**
   **/
   public void store(WeatherEvent event){}

   ////////////////////////Protected Methods/////////////////////////
   /**
   With this Singleton class, there is really nothing to do with
   this:  just create the object and return it as needed.
   **/
   protected Database(){}
}
