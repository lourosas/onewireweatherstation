/**
*/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.weatherclasses.*;
import gnu.io.*;

public class WeatherData{
   private Types        type;
   private String       date;
   private List<Double> data;
   private List<Date>   dates;
   private double       min;
   private double       max;
   private Date         minTime;
   private Date         maxTime;
   private Units        units;
   
   /**Constructors*/
   /**
   */
   public WeatherData
   (
      Types type,
      List<Double> data,
      List<Date>   dates
   ){
      this(type, Units.NULL, data, dates);
   }
   /**
   */
   public WeatherData
   (
      Types type,
      Units units,
      List<Double> data,
      List<Date> dates
   ){
      this.type  = type;
      this.units = units;
      this.setData(data);
      this.setDates(dates);
      this.findMin();
      this.findMax();
   }
   
   /**Public Methods*/
   /**
   */
   public List<Double> getData(){
      List<Double> returnList = new LinkedList<Double>(this.data);
      return returnList;
   }
   
   /**
   */
   public String getDate(){
      return this.date;
   }
   
   /**
   */
   public List<Date> getDates(){
      List<Date> returnList = new LinkedList<Date>(this.dates);
      return returnList;
   }
   
   /**
   */
   public double getMax(){
      return this.max;
   }
   
   /**
   */
   public Date getMaxTime(){
      return this.maxTime;
   }
   
   /**
   */
   public double getMin(){
      return this.min;
   }
   
   /**
   */
   public Date getMinTime(){
      return this.minTime;
   }
   
   /**
   */
   public Types getType(){
      return this.type;
   }
   
   /**
   */
   public Units getUnits(){
      return this.units;
   }
   
   /**
   Override the default toString() method inherited by every object
   */
   public String toString(){
      String rs = new String();
      rs = rs.concat("Type:  " + this.getType() + "\n");
      rs = rs.concat("Date:  " + this.getDate() + "\n");
      rs = rs.concat("Units: " + this.getUnits() + "\n");
      rs = rs.concat("Data Size: " + this.data.size() + "\n");
      if(Double.isNaN(this.getMin())){
         rs = rs.concat("Min: N/A\n");
      }
      else{
         rs = rs.concat("Min:  " + this.getMin() + "\n");
      }
      if(this.getMinTime() == null){
         rs = rs.concat("Min Time:  N/A\n");
      }
      else{
         rs = rs.concat("Min Time: " + this.getMinTime() + "\n");
      }
      if(Double.isNaN(this.getMax())){
         rs = rs.concat("Max:  N/A\n");
      }
      else{
         rs = rs.concat("Max: " + this.getMax() + "\n");
      }
      if(this.getMaxTime() == null){
         rs = rs.concat("Min Time:  N/A\n");
      }
      else{
         rs = rs.concat("Max Time: " + this.getMaxTime() + "\n");
      }
      return rs;
   }
   
   /**Private Methods*/
   /**
   */
   private void convertCelsiusToKelvin(){
      try{
         for(int i = 0; i < this.data.size(); i++){
            double temp = (this.data.get(i)).doubleValue();
            temp = WeatherConvert.celsiusToKelvin(temp);
            this.data.set(i, new Double(temp));
         }
      }
      catch(IndexOutOfBoundsException obe){
         obe.printStackTrace();
      }      
   }
   
   /**
   */
   private void convertCelsiusToFahrenheit(){
      try{
         for(int i = 0; i < this.data.size(); i++){
            double temp = (this.data.get(i)).doubleValue();
            temp = WeatherConvert.celsiusToFahrenheit(temp);
            this.data.set(i, new Double(temp));
         }
      }
      catch(IndexOutOfBoundsException obe){
         obe.printStackTrace();
      }
   }
   
   /**
   */
   private void findMax(){
      double max = -Double.MAX_VALUE;
      Iterator<Double> i = this.data.iterator();
      Iterator<Date>   j = this.dates.iterator();
      while(i.hasNext()){
         double value = (i.next()).doubleValue();
         Date   time  = j.next();
         if(value > max){
            max = value;
            this.maxTime = new Date(time.getTime());
         }
      }
      this.max = (max == -Double.MAX_VALUE) ? Double.NaN: max;
   }
   
   /**
   */
   private void findMin(){
      double min = Double.MAX_VALUE;
      Iterator<Double> i = this.data.iterator();
      Iterator<Date>   j = this.dates.iterator();
      while(i.hasNext()){
         double value = (i.next()).doubleValue();
         Date   time  = j.next();
         if(value < min){
            min = value;
            this.minTime = new Date(time.getTime());
         }
      }
      this.min = (min == Double.MAX_VALUE) ? Double.NaN : min;
   }
   
   /**
   */
   private void setData(List<Double> data)
   throws NullPointerException{
      this.data = new LinkedList<Double>(data);
      if(this.getUnits() == Units.ENGLISH){
         this.convertCelsiusToFahrenheit();
      }
      else if(this.getUnits() == Units.ABSOLUTE){
         this.convertCelsiusToKelvin();
      }
   }
   
   /**
   */
   private void setDate(List<Date> dates){
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      //Get the first date in the list, since all the data has the
      //same date.
      this.date = new String(df.format(dates.get(0)));
   }
   
   /**
   */
   private void setDates(List<Date> dates)
   throws NullPointerException{
      this.dates = new LinkedList<Date>(dates);
      this.setDate(this.dates);
   }
   
}
