/**
* Copyright (C) 2012 Lou Rosas
* This file is part of many applications registered with
* the GNU General Public License as published
* by the Free Software Foundation; either version 3 of the License,
* or (at your option) any later version.
* PaceCalculator is distributed in the hope that it will be
* useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.
* If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

/*
*/
public class LogEvent{
   private Object source;  //The Source of the Log Event
   //The Message embedded in the Log Event
   private String message     = null;
   //The current data list (could be anything)
   private List<Object> list  = null;
   //The current Max Value
   private double max         = Double.NaN;
   //The current Min Value
   private double min         = Double.NaN;
   //The current units for the temperature settings
   private Units units;
   
   //**********************Constructors****************************
   /*
   Constructor Initializing all the data but the Units, min and max
   */   
   public LogEvent
   (
      Object       source,
      String       message,
      List<Object> list
   ){
      try{
         this.source      = source;
         this.message     = new String(message);
         this.list        = new LinkedList(list);
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }
   
   /*
   Constructor Initializing all the data but min and max
   */
   public LogEvent
   (
      Object       source,
      String       message,
      List<Object> list,
      Units        units
   ){
      try{
         this.source      = source;
         this.message     = new String(message);
         this.list        = new LinkedList(list);
         this.units       = units;
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }
   
   /*
   Constructor Initializing all the data but units
   */
   public LogEvent
   (
      Object       source,
      String       message,
      List<Object> list,
      double       min,
      double       max
   ){
      try{
         this.source      = source;
         this.message     = new String(message);
         this.list        = new LinkedList(list);
         this.min         = min;
         this.max         = max;
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }
   
   /*
   Constructor initializing everything
   */
      public LogEvent
   (
      Object       source,
      String       message,
      List<Object> list,
      double       min,
      double       max,
      Units        units
   ){
      try{
         this.source      = source;
         this.message     = new String(message);
         this.list        = new LinkedList(list);
         this.min         = min;
         this.max         = max;
         this.units       = units;
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
   }

   //*********************Public Methods***************************
   /**
   Get Data at a specified index
   */
   public Object getData(int index){
      Object data = null;
      try{
         data = list.get(index);
      }
      catch(NullPointerException npe){
         data = null;
      }
      catch(IndexOutOfBoundsException ibe){
         data = null;
      }
      return data;
   }
   
   /**
   Get the List containing all the appropriate Data
   */
   public List<Object> getDataList(){
      return this.list;
   }
   
   /**
   Get the Max Value (whatever weather measurement represented)
   */
   public double getMax(){
      return this.max;
   }
   
   /**
   Get the associated message of the Memory Event
   */   
   public  String getMessage(){
      return this.message;
   }
   
   /**
   Get the Min Value (what ever weather measurement represented)
   */
   public double getMin(){
      return this.min;
   }
   
   /**
   Get the Source of the Log Event
   */
   public Object getSource(){
      return this.source;
   }
   
   /**
   Get the units for the temperature related data
   */
   public Units getUnits(){
      return this.units;
   }
 
   //*********************Private Methods**************************
   /*
   */
   private void setList(List<Object> currentList){
      this.list = new LinkedList<Object>(currentList);
   }
   
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