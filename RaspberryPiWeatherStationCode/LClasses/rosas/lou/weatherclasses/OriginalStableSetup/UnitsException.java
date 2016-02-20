//
//Units Exception Class By Lou Rosas
//This is part of the Weather Station application
//
//
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;

//
//The UnitsException Class by Lou Rosas
//This exception is thrown by any extension of the Sensor Class
//upon an error to setting the units related to a given sensor
//
public class UnitsException extends RuntimeException{
   //*******************Public Methods*******************************
   //
   //Constructor of no arguments.
   //Use the pre-defined methods in the super class (which is
   //adequate) instead of creating new functionality
   //
   public UnitsException(){
      this("Units Exception");
   }

   //
   //Constructor setting the reason in the message of the super class
   //constructor
   //
   public UnitsException(String reason){
      super(reason);
   }
}
