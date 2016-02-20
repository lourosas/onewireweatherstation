//*******************************************************************
//RunDistanceException Class By Lou Rosas
//Copyright (C) 2008
//This file is part of PaceCalculator.
//PaceCalculator is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************
package rosas.lou;

import java.lang.*;
import java.util.*;

/********************************************************************
The RunDistanceException Class by Lou Rosas.
This Exception is thrown by the RunDistance Class upon an error
related to storing or translating distance data in the RunDistance
Class.
********************************************************************/
public class RunDistanceException extends RuntimeException{
   //*********************Public Methods*****************************
   /*****************************************************************
   Constructor of No Arguments.
   Use the pre-defined methods in the super class that are
   adequate instead of creating new functionality that essentially
   does the same thing.
   *****************************************************************/
   public RunDistanceException(){
      this("Run Distance Exception");
   }

   /*****************************************************************
   Constructor setting the reason in the message of the Super class
   constructor.
   *****************************************************************/
   public RunDistanceException(String reason){
      super(reason);
   }
}
