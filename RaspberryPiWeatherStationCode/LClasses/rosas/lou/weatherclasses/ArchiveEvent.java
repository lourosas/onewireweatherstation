/**
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.io.*;
import rosas.lou.weatherclasses.*;

/**
*/
public class ArchiveEvent{
   //Source of the Archive Event
   private Object source;
   //File where the data was saved
   private File file;
   //Embedded message in the Archive Event
   private String message;
   
   //**********************Constructors*******************************
   /**
   Constructor Initializing all the data
   */
   public ArchiveEvent(Object source, String message, File file){
      this.setSource(source);
      this.setFile(file);
      this.setMessage(message);
   }
   
   //************************Public Methods***************************
   /**
   Get the file where the data was saved
   */
   public File getFile(){
      return this.file;
   }
   
   /**
   Get the associated message of the Archive Event
   */
   public String getMessage(){
      return this.message;
   }
   
   /**
   Get the Source of the Archive Event
   */
   public Object getSource(){
      return this.source;
   }
   
   //********************Private Methods******************************
   /**
   */
   private void setFile(File file){
      this.file = file;
   }

   /**
   */
   private void setMessage(String message){
      this.message = new String(message);
   }
   
   /**
   */
   private void setSource(Object source){
      this.source = source;
   }
}
