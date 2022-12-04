import java.lang.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import javax.sql.*;

public class Connect{
   public static void main(String[] args){
      new Connect();
   }

   public Connect(){
      String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      String DB_URL = "jdbc:mysql://192.168.1.119:3306/weatherdata";
      String USER   = "root";
      String PASS   = "password";
      Connection conn = null;
      Statement  stmt = null;
      try{
         Class.forName(JDBC_DRIVER);
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
      }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         if(conn != null){
            try{
               conn.close();
            }
            catch(Exception e){}
         }
      }
   }
}
