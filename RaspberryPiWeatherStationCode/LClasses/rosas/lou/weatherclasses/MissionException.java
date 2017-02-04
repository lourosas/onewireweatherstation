/*
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;

/*
*/
public class MissionException extends RuntimeException{

   //**********************Public Methods**************************
   /*
   Constructor of No Arguments
   */
   public MissionException(){
      this("Mission Exception");
   }
   
   /*
   Constructor setting the reason in the message of the Super Class
   */
   public MissionException(String reason){
      super(reason);
   }
}