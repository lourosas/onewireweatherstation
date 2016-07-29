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
   static Database instance;
   double tempc;
   double tempf;
   double tempk;
   private int tempArchiveCounter;
   {
      instance           = null;
      tempArchiveCounter = 0;
      tempc = Thermometer.DEFAULTTEMP;
      tempf = Thermometer.DEFAULTTEMP;
      tempk = Thermometer.DEFAULTTEMP;
   }

   //****************************************************************
   /**
   **/
   protected Database(){
      //connect to the database
      this.attemptToConnect();
   }

   /////////////////////////////Public Methods///////////////////////
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
   public void store(WeatherEvent event){
      String propertyName = event.getPropertyName();
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER = "root";
      final String PASS = "password";
      Connection conn   = null;
      Statement  stmt   = null;
      Calendar cal = event.getCalendar();
      String time = String.format("%tT", cal.getTime());
      time += String.format(" %tZ", cal.getTime());
      String month = String.format("%tB", cal.getTime());
      String day   = String.format("%td", cal.getTime());
      String year  = String.format("%tY", cal.getTime());
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT IGNORE INTO missiondata ";
         insert += "(month, day, year) ";
         insert += "VALUES( '" + month +"'";
         insert += ", '" + day + "','" + year + "')";
         stmt.executeUpdate(insert);
         if(propertyName.equals("Thermometer")){
            this.archiveTemperature(event);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
      }
      catch(Exception e){}
      finally{
         try{ 
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
      }
   }

   //////////////////////////Private Methods/////////////////////////
   /**
   **/
   private void archiveTemperature(WeatherEvent event){
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER = "root";
      final String PASS = "password";
      final int MAX_TIMES = 3;
      Connection conn   = null;
      Statement  stmt   = null;
      Calendar cal      = event.getCalendar();
      String  time      = String.format("%tT", cal.getTime());
      time += String.format(" %tZ", cal.getTime());
      String month = String.format("%tB", cal.getTime());
      String day   = String.format("%td", cal.getTime());
      String year  = String.format("%tY", cal.getTime());
      try{
         if(event.getUnits() == Units.METRIC){
            this.tempc = event.getValue();
            ++tempArchiveCounter;
         }
         else if(event.getUnits() == Units.ENGLISH){
            this.tempf = event.getValue();
            ++tempArchiveCounter;
         }
         else if(event.getUnits() == Units.ABSOLUTE){
            this.tempk = event.getValue();
            ++tempArchiveCounter;
         }
         if(tempArchiveCounter >= MAX_TIMES){
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String insert = "INSERT INTO temperaturedata ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"', "+tempc+", "+tempf+", "+tempk+")";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            insert = "INSERT INTO has_temperature_data ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"')";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            tempArchiveCounter = 0;
         }
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){}
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
      }
   }

   /**
   SHOULD ONLY HAPPEN ONCE AND IF THE DATABASE AND THE ASSOCIATED
   TABLES ARE NOT CREATED.
   Attempt to connect to the database weatherdata.  If the
   weatherdata database is not set up, go ahead and create it, with
   the appropriate tables.
   Assumptions:  the mysql database is running.
   **/
   private void attemptToConnect(){
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER = "root";
      final String PASS = "password";
      Connection conn   = null;
      Statement  stmt   = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         System.out.println("Using database:  weatherdata");
      }
      catch(SQLException sqe){
         //If the database is not created, attempt to created it
         this.createDatabase();
      }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            if(stmt != null){ stmt.close(); conn.close(); }
         }
         catch(SQLException sqe2){}//nothing to be done
      }
   }

   /**
   Need to alert the System and Users SOMEHOW the database was not
   created!!!  Not sure how to implement just yet.
   **/
   private void createDatabase(){
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL = "jdbc:mysql://localhost:3306";
      final String USER = "root";
      final String PASS = "password";
      Connection conn   = null;
      Statement  stmt   = null;
      try{
         System.out.println("Creating Database weatherdata");
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String sql = "CREATE DATABASE weatherdata";
         stmt.executeUpdate(sql);
         System.out.println("Database:  weatherdata created");
         sql = "USE weatherdata";
         stmt.executeUpdate(sql);
         System.out.println("Using weatherdata database");
         this.setUpTables();
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            if(stmt != null){
               stmt.close();
               conn.close();
            }
         }
         catch(SQLException sqe2){}//Nothing to be done
      }
   }

   /**
   **/
   private void setUpTables(){
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER  = "root";
      final String PASS  = "password";
      Connection conn    = null;
      Statement  stmt    = null;
      try{
         String table = "DROP TABLE IF EXISTS missiondata";
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE missiondata(";
         table += "month char(15), day char(15), year char(5), ";
         table += "PRIMARY KEY(month,day,year))";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("mission table created successfully");
         this.setUpTemperatureTables();
         //this.setUpHumidityTables();
         //this.setUpBarometerTables();
         //this.setUpDewpointTables();
         //this.setUpHeatIndexTables();
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){}
      finally{
         try{
            if(stmt != null){
               stmt.close();
               conn.close();
            }
         }
         catch(Exception e){}
      }
   }

   /**
   **/
   private void setUpTemperatureTables(){
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER  = "root";
      final String PASS  = "password";
      Connection conn    = null;
      Statement  stmt    = null;
      try{
         String table = "DROP TABLE IF EXISTS has_temperature_data";
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "DROP TABLE IF EXISTS temperaturedata";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table =  "CREATE TABLE temperaturedata(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), tempc decimal(5,2), ";
         table += "tempf decimal(5,2), tempk decimal(5,2), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("temperature data table created");
         table =  "CREATE INDEX IX_SOMEDATA ON missiondata(month, ";
         table += "day, year)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE INDEX IX_SOMEDATA ON temperaturedata(time)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE has_temperature_data(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE,";
         table += " FOREIGN KEY(time) REFERENCES temperaturedata(";
         table += "time) ON DELETE CASCADE)ENGINE=INNODB";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("has_temperature_data table created");
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){}
      finally{
         try{
            if(stmt != null){
               stmt.close();
               conn.close();
            }
         }
         catch(Exception e){}
      }
   }
}
