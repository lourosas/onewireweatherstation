/*********************************************************************
*********************************************************************/

package rosas.lou.lgraphics;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import rosas.lou.lgraphics.LPanel;
import rosas.lou.weatherclasses.WeatherData;

public class WeatherPanelTotal extends JPanel{
   private WeatherData temp;
   private WeatherData humi;
   private WeatherData hIdx;
   private WeatherData dewP;
   private int min;
   private int max;
   private int size;
   private Color color;
   private static final int PAD = 25;
   
   {
      temp = null;
      humi = null;
      hIdx = null;
      dewP = null;
      min  = Integer.MAX_VALUE;
      max  = Integer.MIN_VALUE;
      size = 0;
      color = Color.BLACK;
   }
   
   //************************Constructors*****************************
   /**
   */
   public WeatherPanelTotal
   (
      WeatherData temperature,
      WeatherData humidity,
      WeatherData dewPoint,
      WeatherData heatIndex
   ){
      this.temp = temperature;
      this.humi = humidity;
      this.dewP = dewPoint;
      this.hIdx = heatIndex;
      this.setMin();
      this.setMax();
      this.setSize();
   }
   
   //***********************Public Methods****************************
   /***/
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      this.setBackground(Color.WHITE);
      Graphics2D g2 = (Graphics2D)g;
      
      this.setXAxis(g2);
      this.setYAxis(g2);
      
      //Draw the Heat Index in Orange
      this.drawData(this.hIdx, g2, Color.ORANGE);
      //Draw the Dewpoint in Green
      this.drawData(this.dewP, g2, Color.GREEN);
      //Draw the Temperature in red
      this.drawData(this.temp, g2, Color.RED);
      //Draw the Humidity in Blue
      this.drawData(this.humi, g2, Color.BLUE);
   }
   
   //**************************Private Methods************************  
   /***/
   private int getDataSize(){
      return this.size;
   }
   /***/
   private void drawData(WeatherData data,Graphics2D g2,Color color){
      try{
         java.util.List<Double> currentData = data.getData();
         double increment = currentData.size();
         int h = this.getHeight();
         int w = this.getWidth();
         //set a y-increment to see the data better.
         //The upper and lower ranges are now set based on the min and
         //max values from the data.  The Min and Max values are
         //decremented and incremented by 1, respectievely, to give a
         //little more "cushion room" on the graph.  The data is then
         //scaled to display the data in more "relative" format.  This
         //is done for the purpose of visualization.  Data with values
         //close together on an absolute scale can have very little
         //separation, and thus makes it hard to appreciate the data
         //presented.
         g2.setPaint(color);
         double yinc=(h-PAD)/(double)(this.getMax() - this.getMin());
         for(int i = 0; i < increment - 1; i++){
            Double data1 = currentData.get(i);
            Double data2 = currentData.get(i + 1);
            double dy1, dy2;
            double dx1 = PAD + i * ((w - PAD)/increment);
            double dx2 = PAD + (i + 1) * ((w - PAD)/increment);
            if(!(data1.isNaN() || data2.isNaN())){
               g2.setPaint(color);
               dy1 = h-PAD-(data1.doubleValue()-this.getMin())*yinc;
               dy2 = h-PAD-(data2.doubleValue()-this.getMin())*yinc;
            }
            else{
               g2.setPaint(Color.RED);
               dy1 = h-PAD;
               dy2 = dy1;
               g2.fill(new Ellipse2D.Double(dx1-2, dy1-2, 4, 4));
            }
            g2.draw(new Line2D.Double(dx1, dy1, dx2, dy2));
         }
      }
      catch(NullPointerException npe){}
   }
   
   /***/
   private int getMax(){
      return this.max;
   }
   
   /***/
   private int getMin(){
      return this.min;
   }

   /***/
   private java.util.List<Date> getTheDates(){
      java.util.List<Date> returnList = null;
      try{
         returnList = this.temp.getDates();
      }
      catch(NullPointerException npe2){
         try{
            returnList = this.humi.getDates();
         }
         catch(NullPointerException npe3){
            try{
               returnList = this.dewP.getDates();
            }
            catch(NullPointerException npe4){
               try{
                  returnList = this.hIdx.getDates();
               }
               catch(NullPointerException npe){}
            }
         }
      }
      finally{
         return returnList;
      }
   }

   /***/
   private void setMax(){
      double currentMax = Integer.MIN_VALUE;
      try{
         currentMax = this.temp.getMax();
         if(currentMax > this.max){
            this.max = (int)currentMax + 1;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentMax = this.humi.getMax();
         if(currentMax > this.max){
            this.max = (int)currentMax + 1;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentMax = this.dewP.getMax();
         if(currentMax > this.max){
            this.max = (int)currentMax + 1;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentMax = this.hIdx.getMax();
         if(currentMax > this.max){
            this.max = (int)currentMax + 1;
         }
      }
      catch(NullPointerException npe){}
   }
   
   /***/
   private void setMin(){
      double currentMin = Integer.MAX_VALUE;
      try{
         currentMin = this.temp.getMin();
         if(currentMin < this.min){
            this.min = (int)currentMin - 1;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentMin = this.humi.getMin();
         if(currentMin < this.min){
            this.min = (int)currentMin - 1;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentMin = this.dewP.getMin();
         if(currentMin < this.min){
            this.min = (int)currentMin - 1;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentMin = this.hIdx.getMin();
         if(currentMin < this.min){
            this.min = (int)currentMin - 1;
         }
      }
      catch(NullPointerException npe){}
   }
   
   /***/
   private void setSize(){
      int currentSize = Integer.MIN_VALUE;
      try{
         currentSize = this.temp.getData().size();
         if(currentSize > this.size){
            this.size = currentSize;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentSize = this.humi.getData().size();
         if(currentSize > this.size){
            this.size = currentSize;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentSize = this.dewP.getData().size();
         if(currentSize > this.size){
            this.size = currentSize;
         }
      }
      catch(NullPointerException npe){}
      try{
         currentSize = this.hIdx.getData().size();
         if(currentSize > this.size){
            this.size = currentSize;
         }
      }
      catch(NullPointerException npe){}
   }
   
   /***/
   private void setXAxis(Graphics2D g2){
      try{
         //Get the dates
         //java.util.List<Date> time = this.temp.getDates();
         java.util.List<Date> time = this.getTheDates();
         int h = this.getHeight();
         int w = this.getWidth();

         int value = 0;
         int currentDay = -1;
         int currentHour = -1;
         Font font = g2.getFont();
         FontRenderContext frc = g2.getFontRenderContext();
         LineMetrics lm = font.getLineMetrics("0", frc);
         float sh = lm.getAscent() + lm.getDescent();  
         //Now, try to start drawing the "Text" for the axes
         String s = "x axis";//Do this, for now
         float sy = h - sh;
         float sw = (float)font.getStringBounds(s,frc).getWidth();
         float sx = (w - sw)/2;
         float xinc = (w - PAD)/(float)time.size();
         Iterator i = time.iterator();
         Calendar c = Calendar.getInstance();
         
         while(i.hasNext()){
            c.setTime((Date)i.next());
            int month = c.get(Calendar.MONTH) + 1;
            int day   = c.get(Calendar.DAY_OF_MONTH);
            if(day != currentDay){
               currentDay = day;
               s = "" + month + "/" + day;
               sy = h;
               sw = (float)font.getStringBounds(s,frc).getWidth();
               sx = PAD + value*xinc;
               g2.drawString(s, sx, sy);
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min  = c.get(Calendar.MINUTE);
            if(currentHour != hour){
               currentHour = hour;
               String m = "";
               String hr = "";
               if(min < 10) m = "0" + min;
               else         m = "" + min;
               if(hour==1 || hour==4 || hour==7 || hour==10 || 
                  hour==13|| hour==16||hour==19 || hour==22){
                  if(hour == 1 || hour == 4 || hour == 7)
                     hr = "0" + hour;
                  else hr = "" + hour;
                  s = hr + ":" + m;
                  sw = (float)font.getStringBounds(s,frc).getWidth();
                  sy = h - sh;
                  sx = PAD + value*xinc - sw/4;
                  //g2.drawString(s, sx, sy);
               }
            }
            ++value;
         }
      }
      catch(NullPointerException npe){}
   }
   
   /***/
   private void setYAxis(Graphics2D g2){
      try{
         final double MID = 2.5;
         double increment = this.getDataSize();
         int h = this.getHeight();
         int w = this.getWidth();

         Font font = g2.getFont();
         FontRenderContext frc = g2.getFontRenderContext();
         LineMetrics lm = font.getLineMetrics("0", frc);
         float sh = lm.getAscent() + lm.getDescent();
         
         g2.setPaint(Color.BLACK);
         g2.draw(new Line2D.Double(PAD, 0, PAD, h - PAD));
         g2.draw(new Line2D.Double(PAD, h - PAD, w, h - PAD));
         //Now, try to start drawing the "Text" for the y-axis
         float yinc = (h - PAD)/(float)(this.getMax()-this.getMin());
         //Now, try to start drawing the data
         for(int i = this.getMin(); i < this.getMax(); i++){
            if((i % 5) == 0 || i == this.getMin()){
               String s = new String("" + i);
               float sw =(float)font.getStringBounds(s,frc).getWidth();
               float sx = (PAD - sw)/2;
               float sy = h - PAD - (i - this.min)*yinc + sh/4;
               g2.drawString(s, sx, sy);  
               if(i != this.getMin() || i != this.getMax()){
                  //Draw the line of the current "reference" temps
                  sy -= sh/4;
                  g2.draw(new Line2D.Double(PAD,sy,w,sy));
               }
               if(i != this.getMin() && ((i - this.getMin()) >= 2*MID)){
                  float f = (float) i - (float)MID;
                  s = new String("" + f);
                  sw = (float)font.getStringBounds(s,frc).getWidth();
                  sx = (PAD - sw)/2;
                  sy = h - PAD - (f - this.getMin())*yinc + sh/4;
                  g2.drawString(s, sx, sy);               
               }
            }
            else if(i == this.getMax() - 1 &&
                 (this.getMax() % 5 > MID || this.getMax() % 5 == 0)){
               String s = "";
               float sx, sy, sw;
               sx = sy = sw = (float)0;
               if(this.getMax() % 5 > MID){
                  s = new String("" + i);
                  sy = h - PAD - (i - this.getMin())*yinc + sh/4;
               }
               else{
                  float f = this.getMax() - (float)MID;
                  s = new String("" + f);
                  sy = h - PAD - (f - this.getMin())*yinc + sh/4;
               }
               sw =(float)font.getStringBounds(s,frc).getWidth();
               sx = (PAD - sw)/2;
               g2.drawString(s, sx, sy); 
            }
         }
      }
      catch(NullPointerException npe){}
   }
}
