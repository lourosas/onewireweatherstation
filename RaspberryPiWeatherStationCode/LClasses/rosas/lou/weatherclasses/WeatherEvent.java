/********************************************************************
<GNU Stuff to go here>

The purpose of the WeatherEvent Class is to store a weather event:
Temperature Reading, Humidity Reading, Barometric Presure Reading,
Dewpoint Calculation or Heat Index Calculation with the appropriate
units at a given time.
********************************************************************/

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
   private Calendar calendar; //Store the time of the event
   
   //*********************Constructors******************************
   /*
   Constructor intitializing all the data related to an event
   */
   public WeatherEvent
   (
      Object source,
      String name, 
      double value,
      Units  units,
      Calendar date
   ){
      this.source    = source;
      this.eventName = name;
      this.value     = value;
      this.units     = units;
      this.calendar  = date;
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

   /*
   */
   public Calendar getCalendar(){
      return this.calendar;
   }

   /*
   */
   public String toString(){
      Calendar cal = this.getCalendar();
      String returnString = String.format("%tc", cal.getTime());
      returnString = returnString.concat(", ");
      String data = String.format("%.2f", this.getValue());
      returnString = returnString.concat(data);
      String type = this.getPropertyName();
      if(type.equals("Barometer")){
         if(this.getUnits() == Units.METRIC) {
            returnString = returnString.concat("mmHg");
         }
         else if(this.getUnits() == Units.ENGLISH){
            returnString = returnString.concat("inHg");
         }
         else if(this.getUnits() == Units.ABSOLUTE){
            returnString = returnString.concat("MB");
         }
         else{
            returnString = returnString.concat("mmHg");
         }
      }
      else if(!(type.equals("Hygrometer"))){
         if(this.units == Units.METRIC){
            returnString = returnString.concat("\u00B0" + "C");
         }
         else if(this.units == Units.ENGLISH){
            returnString = returnString.concat("\u00B0" + "F");
         }
         else if(this.units == Units.ABSOLUTE){
            returnString = returnString.concat("K");
         }
         else{
            returnString = returnString.concat("\u00B0" + "C");
         }
      }
      else{ //It is humidity data
         returnString = returnString.concat("%");
      }
      return returnString;
   }

   /*
   */
   public String toStringDate(){
      Calendar cal = this.getCalendar();
      String returnString = String.format("%tc", cal.getTime());
      return returnString;
   }

   /*
   */
   public String toStringValue(){
      String type = this.getPropertyName();
      String returnString = String.format("%.2f", this.getValue());
      returnString = returnString.concat(", ");
      if(type.equals("Barometer")){
         if(this.getUnits() == Units.METRIC) {
            returnString = returnString.concat("mmHg");
         }
         else if(this.getUnits() == Units.ENGLISH){
            returnString = returnString.concat("inHg");
         }
         else if(this.getUnits() == Units.ABSOLUTE){
            returnString = returnString.concat("MB");
         }
         else{
            returnString = returnString.concat("mmHg");
         }
      }
      else if(!(type.equals("Hygrometer"))){
         if(this.units == Units.METRIC){
            returnString = returnString.concat("\u00B0" + "C");
         }
         else if(this.units == Units.ENGLISH){
            returnString = returnString.concat("\u00B0" + "F");
         }
         else if(this.units == Units.ABSOLUTE){
            returnString = returnString.concat("K");
         }
         else{
            returnString = returnString.concat("\u00B0" + "C");
         }
      }
      else{ //It is humidity data
         returnString = returnString.concat("%");
      }
      return returnString;
   }
}
