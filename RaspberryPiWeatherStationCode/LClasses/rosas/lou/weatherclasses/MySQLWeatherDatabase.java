/////////////////////////////////////////////////////////////////////
/*
Copyright 2018 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/////////////////////////////////////////////////////////////////////

package rosas.lou.weatherclasses;

import java.lang.*;
import java.io.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import java.sql.*;

public class MySQLWeatherDatabase implements WeatherDatabase{
   private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   private static final String DB_URL = "jdbc:mysql://localhost:3306/weatherdata";
   private static final String USER   = "root";
   private static final String PASS   = "password";
   //Singlton implementation
   private static MySQLWeatherDatabase _instance;

   {
      _instance = null;
   }

   //////////////////////////Public Methods//////////////////////////
   /*
   Overriding the Database static method--so as to make sure to
   obtain the correct Database Instance
   */
   public static MySQLWeatherDatabase getInstance(){
      if(_instance ==  null){
         _instance = new MySQLWeatherDatabase();
      }
      return _instance;
   }

   ///////////////////////Protected Methods//////////////////////////
   /*
   */
   protected MySQLWeatherDatabase(){
      //Connect to the Database (MySQL)
      this.attemptToConnect();
   }   

   
   ///////////////////////Private Methods////////////////////////////
   /*
   */
   private void attemptToConnect(){
      Connection conn = null;
      Statement  stmt = null;
      try{
         //Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         System.out.println("Using database:  weatherdata");
      }
      catch(SQLException sqe){
         //If the database is not created, attempt to create
         this.createDatabase();
      }
      catch(Exception e){
         e.printStackTrace();
      }
      finally{
         try{
            if(stmt != null){
               stmt.close();
               conn.close();
            }
         }
         catch(SQLException ex){}//Nothing to be done
      }
   }

   /*
   */
   private void createDatabase(){
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
      catch(SQLException sqe){
         sqe.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
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

   /*
   */
   private void setUpTables(){
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
         this.setUpHumidityTables();
         this.setUpBarometerTables();
         this.setUpDewpointTables();
         this.setUpHeatIndexTables();
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

   /*
   */
   private void setUpBarometerTables(){
      Connection conn    = null;
      Statement  stmt    = null;
      try{
         String table = "DROP TABLE IF EXISTS has_pressure_data";
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "DROP TABLE IF EXISTS pressuredata";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table =  "CREATE TABLE pressuredata(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), mmHg decimal(6,2), ";
         table += "inHg decimal(6,2), mB decimal(6,2), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("pressuredata table created");
         table =  "CREATE INDEX IX_SOMEPDATA ON missiondata(month, ";
         table += "day, year)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE INDEX IX_SOMEPDATA ON pressuredata(time)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE has_pressure_data(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE,";
         table += " FOREIGN KEY(time) REFERENCES pressuredata(";
         table += "time) ON DELETE CASCADE)ENGINE=INNODB";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("has_pressure_data table created");
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

   /*
   */
   private void setUpDewpointTables(){
      Connection conn    = null;
      Statement  stmt    = null;
      try{
         String table = "DROP TABLE IF EXISTS has_dewpoint_data";
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "DROP TABLE IF EXISTS dewpointdata";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table =  "CREATE TABLE dewpointdata(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), dewptc decimal(5,2), ";
         table += "dewptf decimal(5,2), dewptk decimal(5,2), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("dewpointdata table created");
         table =  "CREATE INDEX IX_SOMEDPDATA ON missiondata(month,";
         table += " day, year)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE INDEX IX_SOMEDPDATA ON dewpointdata(time)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE has_dewpoint_data(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE,";
         table += " FOREIGN KEY(time) REFERENCES dewpointdata(";
         table += "time) ON DELETE CASCADE)ENGINE=INNODB";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("has_dewpoint_data table created");
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

   /*
   */
   private void setUpHeatIndexTables(){
      Connection conn    = null;
      Statement  stmt    = null;
      try{
         String table = "DROP TABLE IF EXISTS has_heatindex_data";
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "DROP TABLE IF EXISTS heatindexdata";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table =  "CREATE TABLE heatindexdata(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), heatindexc decimal(5,2), ";
         table += "heatindexf decimal(5,2), ";
         table += "heatindexk decimal(5,2), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("heatindexdata table created");
         table =  "CREATE INDEX IX_SOMEHIDATA ON missiondata(month,";
         table += " day, year)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE INDEX IX_SOMEHIDATA ON heatindexdata(time)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE has_heatindex_data(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE,";
         table += " FOREIGN KEY(time) REFERENCES heatindexdata(";
         table += "time) ON DELETE CASCADE)ENGINE=INNODB";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("has_heatindex_data table created");
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

   /*
   */
   private void setUpHumidityTables(){
      Connection conn    = null;
      Statement  stmt    = null;
      try{
         String table = "DROP TABLE IF EXISTS has_humidity_data";
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "DROP TABLE IF EXISTS humiditydata";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table =  "CREATE TABLE humiditydata(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), humidity decimal(5,2), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("humidity data table created");
         table =  "CREATE INDEX IX_SOMEHDATA ON missiondata(month, ";
         table += "day, year)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE INDEX IX_SOMEHDATA ON humiditydata(time)";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE has_humidity_data(";
         table += "month char(15), day char(15), year char(5), ";
         table += "time char(15), ";
         table += "PRIMARY KEY(month, day, year, time), ";
         table += "FOREIGN KEY(month, day, year) REFERENCES ";
         table += "missiondata(month, day, year) ON DELETE CASCADE,";
         table += " FOREIGN KEY(time) REFERENCES humiditydata(";
         table += "time) ON DELETE CASCADE)ENGINE=INNODB";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
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

   /*
   */
   private void setUpTemperatureTables(){
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

   /*
   */
   private void store(String month_, String day_, String year_){
      Connection conn = null;
      Statement  stmt = null;
      try{
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT IGNORE INTO missiondata ";
         insert += "(month, day, year) ";
         insert += "VALUES( '" + month_ +"'";
         insert += ", '" + day_ + "','" + year_ + "')";
         stmt.executeUpdate(insert); 
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sql){}
      }
   }

   ////////////////Interface Implementations/////////////////////////
   /*
   */
   public void barometricPressure(WeatherData pressure_){
      Connection conn = null;
      Statement  stmt = null;
      Calendar cal    = pressure_.calendar();
      String month    = null;
      String day      = null;
      String year     = null;
      String time     = null;
      if(cal != null){
         month = String.format("%tB", cal.getTime());
         day   = String.format("%td", cal.getTime());
         year  = String.format("%tY", cal.getTime());
         this.store(month, day, year);
         time  = String.format("%tT",  cal.getTime());
         time += String.format(" %tZ", cal.getTime());
      }
      try{
         double abs = pressure_.absoluteData();
         double met = pressure_.metricData();
         double eng = pressure_.englishData();
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT INTO pressuredata ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"', "+met+", "+eng+", "+abs+")";
         System.out.println(insert);
         stmt.executeUpdate(insert);
         insert = "INSERT INTO has_pressure_data ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"')";
         System.out.println(insert);
         stmt.executeUpdate(insert); 
      }
      catch(SQLException sql){ sql.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe){}
         System.out.println("\n" + pressure_ + "\n");
      }
   }

   /*
   */
   public void dewpoint(WeatherData dpData_){
      Connection conn = null;
      Statement  stmt = null;
      Calendar cal    = dpData_.calendar();
      String month    = null;
      String day      = null;
      String year     = null;
      String time     = null;
      if(cal != null){
         month = String.format("%tB", cal.getTime());
         day   = String.format("%td", cal.getTime());
         year  = String.format("%tY", cal.getTime());
         this.store(month, day, year);
         time  = String.format("%tT",  cal.getTime());
         time += String.format(" %tZ", cal.getTime());
      }      
      try{
         double dewpk = dpData_.absoluteData();
         double dewpc = dpData_.metricData();
         double dewpf = dpData_.englishData();
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT INTO dewpointdata ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"', "+dewpc+", "+dewpf+", "+dewpk+")";
         System.out.println(insert);
         stmt.executeUpdate(insert);
         insert = "INSERT INTO has_dewpoint_data ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"')";
         System.out.println(insert);
         stmt.executeUpdate(insert); 
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqle){}
         System.out.println("\n" + dpData_ + "\n");
      }
   }

   /*
   */
   public void heatIndex(WeatherData hiData_){
      Connection conn = null;
      Statement  stmt = null;
      Calendar cal    = hiData_.calendar();
      String month    = null;
      String day      = null;
      String year     = null;
      String time     = null;
      if(cal != null){
         month = String.format("%tB", cal.getTime());
         day   = String.format("%td", cal.getTime());
         year  = String.format("%tY", cal.getTime());
         this.store(month, day, year);
         time  = String.format("%tT",  cal.getTime());
         time += String.format(" %tZ", cal.getTime());
      }
      try{
         double hindxk = hiData_.absoluteData();
         double hindxc = hiData_.metricData();
         double hindxf = hiData_.englishData();
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT INTO heatindexdata ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"', "+hindxc+", "+hindxf+", "+hindxk+")";
         System.out.println(insert);
         stmt.executeUpdate(insert);
         insert = "INSERT INTO has_heatindex_data ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"')";
         System.out.println(insert);
         stmt.executeUpdate(insert); 
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqle){}
         System.out.println("\n" + hiData_ + "\n");
      }
   }

   /*
   */
   public void humidity(WeatherData humidity_){
      Connection conn = null;
      Statement  stmt = null;
      Calendar cal    = humidity_.calendar();
      String month    = null;
      String day      = null;
      String year     = null;
      String time     = null;
      if(cal != null){
         month = String.format("%tB", cal.getTime());
         day   = String.format("%td", cal.getTime());
         year  = String.format("%tY", cal.getTime());
         this.store(month, day, year);
         time  = String.format("%tT",  cal.getTime());
         time += String.format(" %tZ", cal.getTime());
      }
      try{
         double humidity = humidity_.percentageData();
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT INTO humiditydata ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"', "+ humidity +")";
         System.out.println(insert);
         stmt.executeUpdate(insert);
         insert = "INSERT INTO has_humidity_data ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"')";
         System.out.println(insert);
         stmt.executeUpdate(insert); 
      }
      catch(SQLException sql){ sql.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe){}
         System.out.println("\n" + humidity_ + "\n");
      }
   }

   /*
   */
   public void temperature(WeatherData temperature_){
      Connection conn = null;
      Statement  stmt = null;
      Calendar cal    = temperature_.calendar();
      String month    = null;
      String day      = null;
      String year     = null;
      String time     = null;
      if(cal != null){
         month = String.format("%tB", cal.getTime());
         day   = String.format("%td", cal.getTime());
         year  = String.format("%tY", cal.getTime());
         this.store(month, day, year);
         time  = String.format("%tT",  cal.getTime());
         time += String.format(" %tZ", cal.getTime());
      }
      try{
         double tempk = temperature_.absoluteData();
         double tempc = temperature_.metricData();
         double tempf = temperature_.englishData();
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
      }
      catch(SQLException sql){ sql.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe){}
         System.out.println("\n" + temperature_ + "\n");
      }
   }
}
