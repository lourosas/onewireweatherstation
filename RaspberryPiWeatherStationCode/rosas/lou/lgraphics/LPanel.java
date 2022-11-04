/*********************************************************************
*********************************************************************/

package rosas.lou.lgraphics;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class LPanel extends JPanel{
   
   java.util.List<Object> data = null;
   java.util.List<Date>   time = null;
   int min = Integer.MAX_VALUE;
   int max = Integer.MIN_VALUE;
   static final int PAD = 25;
   
   //***********************Public Methods****************************
   /***/
   public LPanel(){}
   
   /**
   */
   public LPanel
   (
      LinkedList<Object> data,
      LinkedList<Date>   time
   ){
      this.data = new LinkedList(data);
      this.time = new LinkedList(time);
   }
   
   /**
   */
   public LPanel
   (
      LinkedList<Object> data,
      double min,
      double max,
      LinkedList<Date>   time
   ){
      this.data = new LinkedList(data);
      this.min  = (int)min - 1;
      this.max  = (int)max + 1;
      this.time = new LinkedList(time);
   }
   
   /**
   */
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      this.setBackground(Color.WHITE);
      Graphics2D g2 = (Graphics2D)g;
      double increment = this.data.size();
      int h = this.getHeight();
      int w = this.getWidth();
      
      this.setXAxis(g2);
      this.setYAxis(g2);
      //set a y-increment to see the data better
      //The upper and lower ranges are now set based on the min and
      //max values from the data.  The Min and Max values are
      //decremented and incremented by 1, respectievely, to give a
      //little more "cushion room" on the graph.  The data is then
      //scaled to display the data in more "relative" format.  This
      //is done for the purpose of visualization.  Data with values
      //close together on an absolute scale can have very little
      //separation, and thus makes it hard to appreciate the data
      //presented.
      g2.setPaint(Color.BLUE);
      double yinc = (h - PAD)/(double)(this.max - this.min);
      for(int i = 0; i < increment - 1; i++){
         Double data1 = (Double)this.data.get(i);
         Double data2 = (Double)this.data.get(i + 1);
         double dy1, dy2;
         double dx1 = PAD + i * ((w - PAD)/increment);
         double dx2 = PAD + (i + 1) * ((w - PAD)/increment);
         if(!(data1.isNaN() || data2.isNaN())){
            g2.setPaint(Color.BLUE);
            dy1 = h-PAD-(data1.doubleValue()-this.min)*yinc;
            dy2 = h-PAD-(data2.doubleValue()-this.min)*yinc;
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
   
   //*************************Private Methods*************************
   /**
   */
   private void setXAxis(Graphics2D g2){
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
      //float sy = h - lm.getAscent();
      //float sy = h;
      float sy = h - sh;
      float sw = (float)font.getStringBounds(s,frc).getWidth();
      float sx = (w - sw)/2;
      //g2.drawString(s, sx, sy);
      float xinc = (w - PAD)/(float)time.size();

      Iterator i = this.time.iterator();
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
            if(min < 10)
               m = "0" + min;
            else
               m = "" + min;
            if(hour==1 || hour==4 || hour==7 || hour==10 || 
               hour==13|| hour==16||hour==19 || hour==22){
               if(hour == 1 || hour == 4 || hour == 7)
                  hr = "0" + hour;
               else
                  hr = "" + hour;
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
   
   /**
   */
   private void setYAxis(Graphics2D g2){
      final double MID = 2.5;
      double increment = this.data.size();
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
      float yinc = (h - PAD)/(float)(this.max - this.min);
      for(int i = this.min; i < this.max; i++){
         if((i % 5) == 0 || i == this.min){
            String s = new String("" + i);
            float sw =(float)font.getStringBounds(s,frc).getWidth();
            float sx = (PAD - sw)/2;
            float sy = h - PAD - (i - this.min)*yinc + sh/4;
            g2.drawString(s, sx, sy);  
            if(i != this.min || i != this.max){
               //Draw the line of the current "reference" temps
               sy -= sh/4;
               g2.draw(new Line2D.Double(PAD,sy,w,sy));
            }
            if(i != this.min && ((i - this.min) >= 2*MID)){
               float f = (float) i - (float)MID;
               s = new String("" + f);
               sw = (float)font.getStringBounds(s,frc).getWidth();
               sx = (PAD - sw)/2;
               sy = h - PAD - (f - this.min)*yinc + sh/4;
               g2.drawString(s, sx, sy);               
            }
         }
         else if(i == this.max - 1 &&
                 (this.max % 5 > MID || this.max % 5 == 0)){
            String s = "";
            float sx, sy, sw;
            sx = sy = sw = (float)0;
            if(this.max % 5 > MID){
               s = new String("" + i);
               sy = h - PAD - (i - this.min)*yinc + sh/4;
            }
            else{
               float f = this.max - (float)MID;
               s = new String("" + f);
               sy = h - PAD - (f - this.min)*yinc + sh/4;
            }
            sw =(float)font.getStringBounds(s,frc).getWidth();
            sx = (PAD - sw)/2;
            g2.drawString(s, sx, sy); 
         }
      }
   }
}
