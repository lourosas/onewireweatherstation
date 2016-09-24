import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ASimpleServer{
   ServerSocket serverSocket = null;
   DataInputStream in        = null;
   PrintWriter out           = null;

   public static void main(String[] args){
      new ASimpleServer();
   }

   public ASimpleServer(){
      ServerSocket listener = null;
      try{
         listener = new ServerSocket(9204);
         while(true){
            Socket socket = listener.accept();
            try{
               PrintWriter out = new PrintWriter(
                                     socket.getOutputStream(), true);
               out.println(new Date().toString() + "\npoop");
            }
            catch(Exception e){ e.printStackTrace(); }
         }
      }
      catch(Exception e){ e.printStackTrace(); }
   }
}
