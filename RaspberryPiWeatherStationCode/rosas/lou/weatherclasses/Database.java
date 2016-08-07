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
   double dewptc;
   double dewptf;
   double dewptk;
   double heatidxc;
   double heatidxf;
   double heatidxk;
   double humidity;
   double press_in;
   double press_mm;
   double press_mb;
   double tempc;
   double tempf;
   double tempk;
   private int dpArchiveCounter;
   private int hiArchiveCounter;
   private int presArchiveCounter;
   private int tempArchiveCounter;
   {
      instance           = null;
      dpArchiveCounter   = 0;
      hiArchiveCounter   = 0;
      tempArchiveCounter = 0;
      presArchiveCounter = 0;
      dewptc   = Thermometer.DEFAULTTEMP;
      dewptf   = Thermometer.DEFAULTTEMP;
      dewptk   = Thermometer.DEFAULTTEMP;
      heatidxc = Thermometer.DEFAULTTEMP;
      heatidxf = Thermometer.DEFAULTTEMP;
      heatidxk = Thermometer.DEFAULTTEMP;
      humidity = Hygrometer.DEFAULTHUMIDITY;
      press_mm = Barometer.DEFAULTPRESSURE;
      press_in = Barometer.DEFAULTPRESSURE;
      press_mb = Barometer.DEFAULTPRESSURE;
      tempc    = Thermometer.DEFAULTTEMP;
      tempf    = Thermometer.DEFAULTTEMP;
      tempk    = Thermometer.DEFAULTTEMP;
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
         else if(propertyName.equals("Hygrometer")){
            this.archiveHumidity(event);
         }
         else if(propertyName.equals("Barometer")){
            this.archiveBarometricPressure(event);
         }
         else if(propertyName.equals("Dewpoint")){
            this.archiveDewpoint(event);
         }
         else if(propertyName.equals("Heat Index")){
            this.archiveHeatIndex(event);
         }
         else{ System.out.println(propertyName); }
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
   private void archiveBarometricPressure(WeatherEvent event){
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
      System.out.println(month+", "+day+", "+year+", "+time);
      try{
         if(event.getUnits() == Units.METRIC){
            this.press_mm = event.getValue();
            ++presArchiveCounter;
         }
         else if(event.getUnits() == Units.ENGLISH){
            this.press_in = event.getValue();
            ++presArchiveCounter;
         }
         else if(event.getUnits() == Units.ABSOLUTE){
            this.press_mb = event.getValue();
            ++presArchiveCounter;
         }
         if(presArchiveCounter >= MAX_TIMES){
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String insert = "INSERT INTO pressuredata ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"', "+press_mm+", "+press_in+", ";
            insert += press_mb + ")";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            insert = "INSERT INTO has_pressure_data ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"')";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            this.presArchiveCounter = 0;
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
   **/
   private void archiveDewpoint(WeatherEvent event){
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
            this.dewptc =  event.getValue();
            ++dpArchiveCounter;
         }
         else if(event.getUnits() == Units.ENGLISH){
            this.dewptf = event.getValue();
            ++dpArchiveCounter;
         }
         else if(event.getUnits() == Units.ABSOLUTE){
            this.dewptk = event.getValue();
            ++dpArchiveCounter;
         }
         if(dpArchiveCounter >= MAX_TIMES){
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String insert = "INSERT INTO dewpointdata ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"', "+dewptc+", "+dewptf+", "+dewptk+")";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            insert = "INSERT INTO has_dewpoint_data ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"')";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            dpArchiveCounter = 0;
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
   **/
   private void archiveHeatIndex(WeatherEvent event){
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
      System.out.println(month+", "+day+", "+year+", "+time);
      try{
         if(event.getUnits() == Units.METRIC){
            this.heatidxc =  event.getValue();
            ++this.hiArchiveCounter;
         }
         else if(event.getUnits() == Units.ENGLISH){
            this.heatidxf = event.getValue();
            ++this.hiArchiveCounter;
         }
         else if(event.getUnits() == Units.ABSOLUTE){
            this.heatidxk = event.getValue();
            ++this.hiArchiveCounter;
         }
         if(this.hiArchiveCounter >= MAX_TIMES){
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String insert = "INSERT INTO heatindexdata ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"', " + heatidxc + ", " +heatidxf + ", ";
            insert += heatidxk + ")";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            insert = "INSERT INTO has_heatindex_data ";
            insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
            insert += time+"')";
            System.out.println(insert);
            stmt.executeUpdate(insert);
            this.hiArchiveCounter = 0;
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
   **/
   private void archiveHumidity(WeatherEvent event){
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
      System.out.println(month+", "+day+", "+year+", "+time);
      try{
         this.humidity = event.getValue();
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT INTO humiditydata ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time + "', " + this.humidity + ")";
         System.out.println(insert);
         stmt.executeUpdate(insert);
         insert = "INSERT INTO has_humidity_data ";
         insert += "VALUES( '"+month+"', '"+day+"', '"+year+"', '";
         insert += time+"')";
         stmt.executeUpdate(insert);
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

   /**
   **/
   private void setUpBarometerTables(){
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER  = "root";
      final String PASS  = "password";
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

   /**
   **/
   private void setUpDewpointTables(){
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER  = "root";
      final String PASS  = "password";
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

   /**
   **/
   private void setUpHeatIndexTables(){
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER  = "root";
      final String PASS  = "password";
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

   /**
   **/
   private void setUpHumidityTables(){
      final String DB_URL="jdbc:mysql://localhost:3306/weatherdata";
      final String USER  = "root";
      final String PASS  = "password";
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
