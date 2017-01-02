//******************************************************************
//Mission Exception Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
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
