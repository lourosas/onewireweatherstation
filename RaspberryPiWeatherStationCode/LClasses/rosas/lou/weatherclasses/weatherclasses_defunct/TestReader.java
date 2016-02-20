import myclasses.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import sun.jdbc.odbc.*;

public class TestReader{
    
    public static void main(String[] args){
        try{
            new TestReader(args[0]);
        }
        catch(ArrayIndexOutOfBoundsException e){
            String s = "File name needed before anything can be saved";
            System.out.println(s);
        }

    }

    public TestReader(String file_name){
        Statement st    = null;
        Connection con  = null;
        new JdbcOdbcDriver();
        String myURL = "jdbc:odbc:weatherdata";
        String fileString = file_name;
        String readString = new String("");
        StatementReader sr = null;

        try{
            con = DriverManager.getConnection(myURL);
            System.out.println("Connected");
            sr = new StatementReader(new FileReader(fileString));
            System.out.println(fileString + " opened");
            con.setAutoCommit(false);
            st = con.createStatement();
            while((readString = sr.readStatement()) != null){
                System.out.println(readString);
                st.executeUpdate(readString);
            }
            con.commit();
            System.out.println("Data Update Complete");
        }
        catch(SQLException sqle){ sqle.printStackTrace(); }
        catch(FileNotFoundException f){ f.printStackTrace(); }
        catch(Exception e){ e.printStackTrace(); }
    }
}