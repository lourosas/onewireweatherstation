import java.lang.*;
import java.util.*;
import java.sql.*;

public class Hello{
   public static void main(String[] args){
      new Hello();
   }

   public Hello(){
      System.out.println("Hello World");
      attemptToConnect();
   }
   
   public void attemptToConnect(){
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL = "jdbc:mysql://localhost:3306/test";
      final String USER = "root";
      final String PASS = "password";
      Connection conn = null;
      Statement stmt  = null;
      ResultSet rs    = null;
      try{
         Class.forName("com.mysql.jdbc.Driver");
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         System.out.println("Using the correct database");
      }
      catch(SQLException sqe){ 
         //sqe.printStackTrace();
         this.createDataBase();
      }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            if(stmt != null){ stmt.close(); }
         }
         catch(SQLException sqe2){}
      }
   }

   private void createDataBase(){
      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL = "jdbc:mysql://localhost:3306";
      final String USER = "root";
      final String PASS = "password";
      Connection conn = null;
      Statement stmt  = null;
      try{
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         String sql = "CREATE DATABASE test";
         stmt.executeUpdate(sql);
         System.out.println("Database Created Successfully");
         sql = "USE test";
         stmt.executeUpdate(sql);
         System.out.println("Using the correct database");
         this.setUpTables();
      }
      catch(SQLException sqe){sqe.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            if(stmt != null){
               stmt.close();
               conn.close();
            }
         }
         catch(SQLException sqe2){}
      }
   }

   private void setUpTables(){
      final String DB_URL = "jdbc:mysql://localhost:3306/test";
      final String USER = "root";
      final String PASS = "password";
      Connection conn = null;
      Statement  stmt = null;
      try{
         String table = "DROP TABLE IF EXISTS HAS_DATA";
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "DROP TABLE IF EXISTS TEMPERATUREDATA";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "DROP TABLE IF EXISTS MISSIONDATA";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE MISSIONDATA(";
         table += "THEDATE char(20), PRIMARY KEY(THEDATE))";
         stmt = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("Table Created Successfully");
         table = "CREATE TABLE TEMPERATUREDATA(";
         table += "THEDATE char(20), THETIME char(20), ";
         table += "TEMPC decimal(5,2), TEMPF decimal(5,2), ";
         table += "TEMPK decimal(5,2), ";
         table += "PRIMARY KEY(THEDATE, THETIME), ";
         table += "FOREIGN KEY(THEDATE) REFERENCES MISSIONDATA(";
         table += "THEDATE) ON DELETE CASCADE)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("Table Created Successfully");
         table = "CREATE INDEX IX_SOME_DATA ON MISSIONDATA(THEDATE)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE INDEX IX_SOME_DATA ON TEMPERATUREDATA(THETIME)";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         table = "CREATE TABLE HAS_DATA(";
         table += "THEDATE char(20), THETIME char(20), ";
         table += "PRIMARY KEY(THEDATE, THETIME), ";
         table += "FOREIGN KEY(THEDATE) REFERENCES MISSIONDATA(";
         table += "THEDATE) ON DELETE CASCADE, ";
         table += "FOREIGN KEY(THETIME) REFERENCES TEMPERATUREDATA(";
         table += "THETIME) ON DELETE CASCADE )ENGINE=INNODB";
         stmt  = conn.createStatement();
         stmt.executeUpdate(table);
         System.out.println("Table Created Successfully");
      }
      catch(SQLException sqe){ sqe.printStackTrace(); }
      catch(Exception e){}
   }
}
