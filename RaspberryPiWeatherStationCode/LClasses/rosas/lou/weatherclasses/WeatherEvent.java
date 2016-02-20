/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class WeatherEvent{
   private Object source;  //The Source of the WeatherEvent
   //The Name of the event:  Temperature, Humidity, Preasure...
   private String eventName;
   private double value; //Value of the data of the event
   private Units  units; //METRIC, ENGLISH, ABSOLUTE
   
   //*********************Constructors******************************
   /*
   Constructor intitializing all the data related to an event
   */
   public WeatherEvent
   (
      Object source,
      String name, 
      double value,
      Units  units
   ){
      this.source    = source;
      this.eventName = name;
      this.value     = value;
      this.units     = units;
   }
   
   //*********************Public Methods****************************
   /*
   */
   public String getPropertyName(){
      return this.eventName;
   }
   
   /*
   */
   public Object getSource(){
      return this.source;
   }
   
   /*
   */
   public double getValue(){
      return this.value;
   }
   
   /*
   */
   public Units getUnits(){
      return this.units;
   }
}