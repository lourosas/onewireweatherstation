/********************************************************************
<GNU Stuff to go here>
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.io.*;
import java.util.*;
import rosas.lou.weatherclasses.*;
import java.sql.*;

public class MySQLWeatherDatabase extends Database{
   private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   private static final String DB_URL = "jdbc:mysql://localhost:3306/weatherdata";
   private static final String USER = "root";
   private static final String PASS = "password";
   private static MySQLWeatherDatabase instance;
   private double dewptc;
   private double dewptf;
   private double dewptk;
   private double heatidxc;
   private double heatidxf;
   private double heatidxk;
   private double humidity;
   private double press_in;
   private double press_mm;
   private double press_mb;
   private double tempc;
   private double tempf;
   private double tempk;
   private int    dpArchiveCounter;
   private int    hiArchiveCounter;
   private int    presArchiveCounter;
   private int    tempArchiveCounter;

   {
      instance           = null;
      dewptc             = Thermometer.DEFAULTTEMP;
      dewptf             = Thermometer.DEFAULTTEMP;
      dewptk             = Thermometer.DEFAULTTEMP;
      heatidxc           = Thermometer.DEFAULTTEMP;
      heatidxf           = Thermometer.DEFAULTTEMP;
      heatidxk           = Thermometer.DEFAULTTEMP;
      humidity           = Hygrometer.DEFAULTHUMIDITY;
      press_mm           = Barometer.DEFAULTPRESSURE;
      press_in           = Barometer.DEFAULTPRESSURE;
      press_mb           = Barometer.DEFAULTPRESSURE;
      tempc              = Thermometer.DEFAULTTEMP;
      tempf              = Thermometer.DEFAULTTEMP;
      tempk              = Thermometer.DEFAULTTEMP;
      dpArchiveCounter   = 0;
      hiArchiveCounter   = 0;
      tempArchiveCounter = 0;
      presArchiveCounter = 0;
   }

   //////////////////////////Public Methods//////////////////////////
   /*
   Overriding the Database static method--so as to make sure to
   obtain the correct Database instance
   */
   public static MySQLWeatherDatabase getInstance(){
      if(instance == null){
         instance = new MySQLWeatherDatabase();
      }
      return instance;
   }

   /*
   Override the same method from the Database class
   */
   public List<String> requestData(String request){
      System.out.println(theRequest);
      List<String> list = null;
      String upcase = theRequest.toUpperCase();
      if(request.toUpperCase().contains("MISSIONDATA")){
         returnList = this.missionData(request);
      }
      else if(request.toUpperCase().contains("HUMIDITYDATA")){
         if(upcase.contains("MAX")){
            returnList = this.maxHumidity(request);
         }
         else if(upcase.contains("MIN")){
            returnList = this.minHumidity(request);
         }
         else{
            returnList = this.humidity(request);
         }
      }
      else if(request.toUpperCase().contains("PRESSUREDATA")){
         if(upcase.contains("MAX")){
            returnList = this.maxBarometricPressure(request);
         }
         else if(upcase.contains("MIN")){
            returnList = this.minBarometricPressure(request);
         }
         else{
            returnList = this.barometricPressure(request);
         }
      }
      else if(request.toUpperCase().contains("DEWPOINTDATA")){
         if(upcase.contains("MAX")){
            returnList = this.maxDewpoint(request);
         }
         else if(upcase.contains("MIN")){
            returnList = this.minDewpoint(request);
         }
         else{
            returnList = this.dewpoint(request);
         }
      }
      else if(request.toUpperCase().contains("HEATINDEXDATA")){
         if(upcase.contains("MIN")){
            returnList = this.minHeatIndex(request);
         }
         else if(upcase.contains("MAX")){
            returnList = this.maxHeatIndex(request);
         }
         else{
            returnList = this.heatIndex(request);
         }
      }
      else if(upcase.contains("TEMPERATUREDATA")){
         if(upcase.contains("MIN")){
            returnList = this.minTemperature(request);
         }
         else if(upcase.contains("MAX")){
            returnList = this.maxTemperature(rquest);
         }
         else{
            returnList = this.temperature(request);
         }
      }
      return list;
   }

   /*
   Override the same method from the Database class
   */
   public void store(WeatherEvent event){
      String propertyName = event.getPropertyName();
      Connection conn = null;
      Statement  stmt = null;
      Calendar cal = event.getCalendar();
      String time = String.format("%tT", cal.getTime());
      time += String.format(" %tZ", cal.getTime());
      String month = String.format("%tB", cal.getTime());
      String day   = String.format("%td", cal.getTime());
      String year  = String.format("%tY", cal.getTime());
      try{
         //Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String insert = "INSERT IGNORE INTO missiondata ";
         insert += "(month, day, year) ";
         insert += "VALUES( '" + month +"'";
         insert += ", '" + day + "','" + year + "')";
         stmt.executeUpdate(insert);
         if(propertyName.equals("Thermometer")){
            this.temperature(event);
         }
         else if(propertyName.equals("Hygrometer")){
            this.humidity(event);
         }
         else if(propertyName.equals("Barometer")){
            this.barometricPressure(event);
         }
         else if(propertyName.equals("Dewpoint")){
            this.dewpoint(event);
         }
         else if(propertyName.equals("Heat Index")){
            this.heatIndex(event);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sq){}
      }
   }

   //////////////////////////Protected Methods///////////////////////
   /*
   */
   protected MySQLWeatherDatabase(){
      //Connect to the Database
      this.attemptToConnect();
   }

   /////////////////////////Private Methods//////////////////////////
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
   private void barometricPressure(WeatherEvent event){
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

   /*
   */
   private List<String> barometricPressure(String command){
      Connection conn         = null;
      Statement  stmt         = null;
      ResultSet    resultSet  = null;
      List<String> returnList = null;
      try{
         //Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet  = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexdata = new String();
            if(command.contains("*") || command.contains("month")){
               String month = resultSet.getString("month");
               indexdata += month;
            }
            if(command.contains("*") || command.contains("day")){
               String day = resultSet.getString("day");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += day;
            }
            if(command.contains("*") || command.contains("year")){
               String year = resultSet.getString("year");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += year;
            }
            if(command.contains("*") || command.contains("time")){
               String time = resultSet.getString("time");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += time;
            }            
            if(command.contains("*") || command.contains("mmHg")){
               double metric = resultSet.getDouble("mmHg");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + metric;
            }
            if(command.contains("*") || command.contains("inHg")){
               double english = resultSet.getDouble("inHg");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + english;
            }
            if(command.contains("*") || command.contains("mB")){
               double absolute = resultSet.getDouble("mB");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + absolute;
            }
            returnList.add(indexdata);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
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
   private void dewpoint(WeatherEvent event){
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

   /*
   */
   private List<String> dewpoint(String command){
      Connection conn         = null;
      Statement  stmt         = null;
      ResultSet    resultSet  = null;
      List<String> returnList = null;
      try{
         //Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet  = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexdata = new String();
            if(command.contains("*") || command.contains("month")){
               String month = resultSet.getString("month");
               indexdata += month;
            }
            if(command.contains("*") || command.contains("day")){
               String day = resultSet.getString("day");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += day;
            }
            if(command.contains("*") || command.contains("year")){
               String year = resultSet.getString("year");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += year;
            }
            if(command.contains("*") || command.contains("time")){
               String time = resultSet.getString("time");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += time;
            }            
            if(command.contains("*") || command.contains("dewptc")){
               double metric = resultSet.getDouble("dewptc");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + metric;
            }
            if(command.contains("*") || command.contains("dewptf")){
               double english = resultSet.getDouble("dewptf");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + english;
            }
            if(command.contains("*") || command.contains("dewptk")){
               double absolute = resultSet.getDouble("dewptk");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + absolute;
            }
            returnList.add(indexdata);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> maxBarometricPressure(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MAX(mmHg)")){
               double minp = resultSet.getDouble("MAX(mmHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            else if(command.contains("max(mmHg)")){
               double minp = resultSet.getDouble("MAX(mmHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            if(command.contains("MAX(inHg)")){
               double minp = resultSet.getDouble("MAX(inHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            else if(command.contains("max(inHg)")){
               double minp = resultSet.getDouble("MAX(inHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            if(command.contains("MAX(mB)")){
               double minp = resultSet.getDouble("MAX(mB)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            if(command.contains("max(mB)")){
               double dpk = resultSet.getDouble("MAX(mB)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpk;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> minBarometricPressure(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MIN(mmHg)")){
               double minp = resultSet.getDouble("MIN(mmHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            else if(command.contains("min(mmHg)")){
               double minp = resultSet.getDouble("min(mmHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            if(command.contains("MIN(inHg)")){
               double minp = resultSet.getDouble("MIN(inHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            else if(command.contains("min(inHg)")){
               double minp = resultSet.getDouble("min(inHg)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            if(command.contains("MIN(mB)")){
               double minp = resultSet.getDouble("MIN(mB)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + minp;
            }
            if(command.contains("min(mB)")){
               double dpk = resultSet.getDouble("min(mB)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpk;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> maxDewpoint(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MAX(dewptc)")){
               double dpc = resultSet.getDouble("MAX(dewptc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpc;
            }
            else if(command.contains("max(dewptc)")){
               double dpc = resultSet.getDouble("MAX(dewptc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpc;
            }
            if(command.contains("MAX(dewptf)")){
               double dpf = resultSet.getDouble("MAX(dewptf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpf;
            }
            else if(command.contains("max(dewptf)")){
               double dpf = resultSet.getDouble("MAX(dewptf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpf;
            }
            if(command.contains("MAX(dewptk)")){
               double dpk = resultSet.getDouble("MAX(dewptk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpk;
            }
            if(command.contains("max(dewptk)")){
               double dpk = resultSet.getDouble("MAX(dewptk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpk;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> minDewpoint(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MIN(dewptc)")){
               double dpc = resultSet.getDouble("MIN(dewptc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpc;
            }
            else if(command.contains("min(dewptc)")){
               double dpc = resultSet.getDouble("min(dewptc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpc;
            }
            if(command.contains("MIN(dewptf)")){
               double dpf = resultSet.getDouble("MIN(dewptf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpf;
            }
            else if(command.contains("min(dewptf)")){
               double dpf = resultSet.getDouble("min(dewptf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpf;
            }
            if(command.contains("MIN(dewptk)")){
               double dpk = resultSet.getDouble("MIN(dewptk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpk;
            }
            if(command.contains("min(dewptk)")){
               double dpk = resultSet.getDouble("min(dewptk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + dpk;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> maxHeatIndex(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         System.out.println("Database: " + command);
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MAX(heatindexc)")){
               double hic = resultSet.getDouble("MAX(heatindexc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hic;
            }
            else if(command.contains("max(heatindexc)")){
               double hic = resultSet.getDouble("MAX(heatindexc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hic;
            }
            if(command.contains("MAX(heatindexf)")){
               double hif = resultSet.getDouble("MAX(heatindexf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hif;
            }
            else if(command.contains("max(heatindexf)")){
               double hif = resultSet.getDouble("MAX(heatindexf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hif;
            }
            if(command.contains("MAX(heatindexk)")){
               double hik = resultSet.getDouble("MAX(heatindexk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hik;
            }
            if(command.contains("max(heatindexk)")){
               double hik = resultSet.getDouble("MAX(heatindexk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hik;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> minHeatIndex(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         System.out.println("Database: " + command);
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MIN(heatindexc)")){
               double hic = resultSet.getDouble("MIN(heatindexc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hic;
            }
            else if(command.contains("min(heatindexc)")){
               double hic = resultSet.getDouble("min(heatindexc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hic;
            }
            if(command.contains("MIN(heatindexf)")){
               double hif = resultSet.getDouble("MIN(heatindexf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hif;
            }
            else if(command.contains("min(heatindexf)")){
               double hif = resultSet.getDouble("min(heatindexf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hif;
            }
            if(command.contains("MIN(heatindexk)")){
               double hik = resultSet.getDouble("MIN(heatindexk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hik;
            }
            if(command.contains("min(heatindexk)")){
               double hik = resultSet.getDouble("min(heatindexk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + hik;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> maxHumidity(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null; 
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MAX(humidity)")){
               double maxHumidity =
                                resultSet.getDouble("MAX(humidity)");
               indexData += ", " + maxHumidity;
            }
            else if(command.contains("max(humidity)")){
               double maxHumidity =
                                resultSet.getDouble("MAX(humidity)");
               indexData += ", " + maxHumidity;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> minHumdity(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null; 
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MIN(humidity)")){
               double minHumidity =
                                resultSet.getDouble("MIN(humidity)");
               indexData += ", " + minHumidity;
            }
            else if(command.contains("min(humidity)")){
               double minHumidity =
                                resultSet.getDouble("MIN(humidity)");
               indexData += ", " + minHumidity;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> maxTemperature(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MAX(tempc)")){
               double tempc = resultSet.getDouble("MAX(tempc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempc;
            }
            else if(command.contains("max(tempc)")){
               double tempc = resultSet.getDouble("max(tempc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempc;
            }
            if(command.contains("MAX(tempf)")){
               double tempf = resultSet.getDouble("MAX(tempf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempf;
            }
            else if(command.contains("max(tempf)")){
               double tempf = resultSet.getDouble("max(tempf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempf;
            }
            if(command.contains("MAX(tempk)")){
               double tempk = resultSet.getDouble("MAX(tempk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempk;
            }
            if(command.contains("max(tempk)")){
               double tempk = resultSet.getDouble("max(tempk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempk;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> minTemperature(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexData = new String();
            String month = resultSet.getString("month");
            indexData += month;
            String day = resultSet.getString("day");
            indexData += ", " + day;
            String year = resultSet.getString("year");
            indexData += ", " + year;
            if(command.contains("MIN(tempc)")){
               double tempc = resultSet.getDouble("MIN(tempc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempc;
            }
            else if(command.contains("min(tempc)")){
               double tempc = resultSet.getDouble("min(tempc)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempc;
            }
            if(command.contains("MIN(tempf)")){
               double tempf = resultSet.getDouble("MIN(tempf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempf;
            }
            else if(command.contains("min(tempf)")){
               double tempf = resultSet.getDouble("min(tempf)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempf;
            }
            if(command.contains("MIN(tempk)")){
               double tempk = resultSet.getDouble("MIN(tempk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempk;
            }
            if(command.contains("min(tempk)")){
               double tempk = resultSet.getDouble("min(tempk)");
               if(indexData.length() > 0){
                  indexData += ", ";
               }
               indexData += "" + tempk;
            }
            returnList.add(indexData);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private List<String> missionData(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         System.out.println("Database:  " + command);
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                                  ResultSet.CONCUR_UPDATABLE);
         resultSet  = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexdata = new String();
            if(command.contains("*") || command.contains("month")){
               String month = resultSet.getString("month");
               indexdata += month;
            }
            if(command.contains("*") || command.contains("day")){
               String day = resultSet.getString("day");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += day;
            }
            if(command.contains("*") || command.contains("year")){
               String year = resultSet.getString("year");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += year;
            }
            returnList.add(indexdata);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private void heatIndex(WeatherEvent event){
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

   /*
   */
   private List<String> heatIndex(String command){
      Connection conn         = null;
      Statement  stmt         = null;
      ResultSet    resultSet  = null;
      List<String> returnList = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet  = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexdata = new String();
            if(command.contains("*") || command.contains("month")){
               String month = resultSet.getString("month");
               indexdata += month;
            }
            if(command.contains("*") || command.contains("day")){
               String day = resultSet.getString("day");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += day;
            }
            if(command.contains("*") || command.contains("year")){
               String year = resultSet.getString("year");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += year;
            }
            if(command.contains("*") || command.contains("time")){
               String time = resultSet.getString("time");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += time;
            }            
            if(command.contains("*") ||
                                      command.contains("heatindexc")){
               double metric = resultSet.getDouble("heatindexc");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + metric;
            }
            if(command.contains("*") ||
                                      command.contains("heatindexf")){
               double english = resultSet.getDouble("heatindexf");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + english;
            }
            if(command.contains("*") ||
                                      command.contains("heatindexk")){
               double absolute = resultSet.getDouble("heatindexk");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + absolute;
            }
            returnList.add(indexdata);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }

   /*
   */
   private void humidity(WeatherEvent event){
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
         //Class.forName(JDBC_DRIVER);
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

   /*
   */
   private List<String> humidity(String command){
      Connection   conn       = null;
      Statement    stmt       = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                                   ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexdata = new String();
            if(command.contains("*") || command.contains("month")){
               String month = resultSet.getString("month");
               indexdata += month;
            }
            if(command.contains("*") || command.contains("day")){
               String day = resultSet.getString("day");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += day;
            }
            if(command.contains("*") || command.contains("year")){
               String year = resultSet.getString("year");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += year;
            }
            if(command.contains("*") || command.contains("time")){
               String time = resultSet.getString("time");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += time;
            }
            if(command.contains("*") || command.contains("humidity")){
               double humidity = resultSet.getDouble("humidity");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + humidity;
            }
            returnList.add(indexdata);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
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
   private void temperature(WeatherEvent event){
      final int MAX_TIMES = 3;
      int counter       = 0;
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

   /*
   */
   private List<String> temperature(String command){
      Connection conn   = null;
      Statement  stmt   = null;
      List<String> returnList = null;
      ResultSet    resultSet  = null;
      try{
         System.out.println("Database:  " + command);
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt = conn.createStatement(
                                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                                  ResultSet.CONCUR_UPDATABLE);
         resultSet = stmt.executeQuery(command);
         returnList = new LinkedList<String>();
         while(resultSet.next()){
            String indexdata = new String();
            if(command.contains("*") || command.contains("month")){
               String month = resultSet.getString("month");
               indexdata += month;
            }
            if(command.contains("*") || command.contains("day")){
               String day   = resultSet.getString("day");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += day;
            }
            if(command.contains("*") || command.contains("year")){
               String year  = resultSet.getString("year");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += year;
            }
            if(command.contains("*") || command.contains("time")){
               String time  = resultSet.getString("time");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += time;
            }
            if(command.contains("*") || command.contains("tempc")){
               double tempc = resultSet.getDouble("tempc");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + tempc;
            }
            if(command.contains("*") || command.contains("tempf")){
               double tempf = resultSet.getDouble("tempf");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + tempf;
            }
            if(command.contains("*") || command.contains("tempk")){
               double tempk = resultSet.getDouble("tempk");
               if(indexdata.length() > 0){
                  indexdata += ", ";
               }
               indexdata += "" + tempk;
            }
            returnList.add(indexdata);
         }
      }
      catch(SQLException sqe){
         sqe.printStackTrace();
         resultSet = null;
      }
      catch(Exception e){
         e.printStackTrace();
         resultSet = null;
      }
      finally{
         try{
            stmt.close();
            conn.close();
         }
         catch(SQLException sqe2){}
         return returnList;
      }
   }
}
