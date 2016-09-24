/**
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;

/**
*/
public class ArchiveException extends RuntimeException{
   //************************Public Methods***************************
   /**
   Constructor of No Arguments
   */
   public ArchiveException(){
      this("Archive Exception");
   }
   
   /**
   Constructor setting the reason in the message of the Super Class
   */
   public ArchiveException(String reason){
      super(reason);
   }
}
