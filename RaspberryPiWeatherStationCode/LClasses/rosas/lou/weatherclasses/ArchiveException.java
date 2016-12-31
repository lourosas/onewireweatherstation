//******************************************************************
//ArchiveException Class
//Copyright (C) 2016 by Lou Rosas
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
