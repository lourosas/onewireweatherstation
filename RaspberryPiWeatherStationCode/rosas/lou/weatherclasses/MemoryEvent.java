/*
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

/*
*/
public class MemoryEvent{
   private Object source;  //The Sourse of the Memory Event
   //The Message embedded in the Memory Event
   private String message;
   
   //**********************Constructors****************************
   /*
   Constructor Initializing all the data related to a Memory Event
   */
   public MemoryEvent(Object source, String message){
      this.setSource(source);
      this.setMessage(message);
   }
   
   //*********************Public Methods***************************
   /*
   Get the associated message of the Memory Event
   */
   public String getMessage(){
      return this.message;
   }
   
   /*
   Get the Source of the Memory Event
   */
   public Object getSource(){
      return this.source;
   }
   
   //*********************Private Methods**************************
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