/*
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;

/*
*/
public class MemoryException extends RuntimeException{

   //**********************Public Methods**************************
   /*
   Constructor of No Arguments
   */
   public MemoryException(){
      this("Memory Exception");
   }
   
   /*
   Constructor setting the reason in the message of the Super Class
   */
   public MemoryException(String reason){
      super(reason);
   }
}

