/*
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

/*
*/
public class MissionEvent{
   private Object source; //The Source of the Mission Event
   //The message embedded in the Mission Event
   private String message;
   
   //***********************Constructors***************************
   /*
   Constructor Initializing all the data related to a Mission Event
   */
   public MissionEvent(Object source, String message){
      this.setSource(source);
      this.setMessage(message);
   }
   
   //************************Public Methods************************
   /*
   Get the associated message of the Mission Event
   */
   public String getMessage(){
      return this.message;
   }
   
   /*
   Get the Source of the Mission Event
   */
   public Object getSource(){
      return this.source;
   }
   
   //*******************Private Methods****************************
   /*
   */
   private void setMessage(String message){
      this.message = new String(message);
   }
   
   /*
   */
   private void setSource(Object source){
      this.source = source;
   }
}