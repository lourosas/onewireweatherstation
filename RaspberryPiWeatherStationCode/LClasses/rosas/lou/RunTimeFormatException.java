//*******************************************************************
//RunTimeFormatException Class By Lou Rosas
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
The RunTimeFormatException Class by Lou Rosas.
This Exception is thrown by the RunTime Class upon an error
related to storing or translating data in the RunTime Class.
********************************************************************/
public class RunTimeFormatException extends RuntimeException{

   private String reason;

   //*************************Public Methods*************************
   /*****************************************************************
   Constructor of No Arguments.
   *****************************************************************/
   public RunTimeFormatException(){
      this("RunTime Exception");
   }

   /*****************************************************************
   Constructor setting the reason for the exception.
   *****************************************************************/
   public RunTimeFormatException(String reason){
      super(reason);
      this.setReason(reason);
   }

   /*****************************************************************
   Get the reason for the exception.
   *****************************************************************/
   public String getReason(){
      return this.reason;
   }

   //************************Private Methods*************************
   //****************************************************************
   //Set the reason for the exception.
   //****************************************************************
   public void setReason(String currentReason){
      this.reason = new String(currentReason);
   }
}
