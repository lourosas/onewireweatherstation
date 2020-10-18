//////////////////////////////////////////////////////////////////////
/*
*/
//////////////////////////////////////////////////////////////////////

package rosas.lou.lgraphics;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import rosas.lou.weatherclasses.*;
/*
Use this class specifically for Thermal and Hygrometer data
*/
public class WeatherPanel extends JPanel{
   private static final int PAD = 25;
   private java.util.List<WeatherData> _data;
   private Units _units;

   private Color color;


   {
      color = Color.BLACK;
   };

   /**/
   public WeatherPanel(java.util.List<WeatherData> data, Units units){
      this._data  = data;
      this._units = units;

   }

   /**/
   public void paintComponent(Graphics g){
      int TINCREMENT = 1440;

      super.paintComponent(g);
      this.setBackground(Color.WHITE);
      Graphics2D g2    = (Graphics2D)g;
      double increment = this._data.size();

      //double increment = 1440.0;
      int h            = this.getHeight();
      int w            = this.getWidth();
      this.setXAxis(g2);
      if(this._data.get(0).type() != WeatherDataType.PRESSURE){
         this.setTypicalYAxis(g2);
      }
      else{}
      g2.setPaint(Color.BLUE);
      double max = this.max();
      double min = this.min();
      if(this._data.get(0).type() != WeatherDataType.PRESSURE){
         max += 1.0;
         min -= 1.0;
      }
      else{
         max += 0.30;
         min -= 0.30;
      }
      double yinc = (h - PAD)/(double)(max - min);
      for(int i = 0; i < increment - 1; i++){
         double data1 = Double.NEGATIVE_INFINITY;
         double data2 = Double.NEGATIVE_INFINITY;
         if(this._data.get(i).type() != WeatherDataType.HUMIDITY){
            if(this._units == Units.METRIC){
               data1 = this._data.get(i).metricData();
               data2 = this._data.get(i+1).metricData();
            }
            else if(this._units == Units.ENGLISH){
               data1 = this._data.get(i).englishData();
               data2 = this._data.get(i+1).englishData();
            }
            else if(this._units == Units.ABSOLUTE){
               data1 = this._data.get(i).absoluteData();
               data2 = this._data.get(i+1).absoluteData();
            }
         }
         else{
            data1 = this._data.get(i).percentageData();
            data2 = this._data.get(i+1).percentageData();
         }
         double dy1, dy2;
         String time1 = this._data.get(i).time();
         String time2 = this._data.get(i+1).time();
         int hour1 = 0; int hour2 = 0; int min1 = 0; int min2 = 0;
         int value1 = 0; int value2 = 0;
         try{
            hour1 = Integer.parseInt(time1.split(":")[0].trim());
            hour2 = Integer.parseInt(time2.split(":")[0].trim());
            min1  = Integer.parseInt(time1.split(":")[1].trim());
            min2  = Integer.parseInt(time2.split(":")[1].trim());
            value1 = hour1*60+min1;
            value2 = hour2*60+min2;
         }
         catch(NumberFormatException nfe){ nfe.printStackTrace();}
         double dx1 = (PAD + value1 * (w-PAD)/TINCREMENT);
         double dx2 = (PAD + value2 * (w-PAD)/TINCREMENT);
         dy1 = h-PAD-(data1 - min)*yinc;
         dy2 = h-PAD-(data2 - min)*yinc;
         //g2.fill(new Ellipse2D.Double(dx1-2, dy1-2, 4, 4));
         g2.draw(new Line2D.Double(dx1, dy1, dx2, dy2));
      }
   }

   /**/
   private double max(){
      double max = Double.NEGATIVE_INFINITY;
      Iterator<WeatherData> it = this._data.iterator();
      while(it.hasNext()){
         //WeatherData data = it.next();
         double value = 0.0;
         if(this._units == Units.METRIC){
            value = it.next().metricData();
         }
         else if(this._units == Units.ENGLISH){
            value = it.next().englishData();
         }
         else if(this._units == Units.ABSOLUTE){
            value = it.next().absoluteData();
         }
         if(value > max){
            max = value;
         }
      }
      return max;
   }

   /**/
   private double min(){
      double min = Double.POSITIVE_INFINITY;
      Iterator<WeatherData> it = this._data.iterator();
      while(it.hasNext()){
         //WeatherData data = it.next();
         double value = 0.0;
         if(this._units == Units.METRIC){
            value = it.next().metricData();
         }
         else if(this._units == Units.ENGLISH){
            value = it.next().englishData();
         }
         else if(this._units == Units.ABSOLUTE){
            value = it.next().absoluteData();
         }
         if(value < min){
            min = value;
         }
      }
      return min;
   }

   /**/
   private void setXAxis(Graphics2D g2){
      int HOURS                = 24;
      Iterator<WeatherData> it = this._data.iterator();
      int h                    = this.getHeight();
      int w                    = this.getWidth();
      int value                = 0;

      Font font                = g2.getFont();
      FontRenderContext frc    = g2.getFontRenderContext();
      LineMetrics lm           = font.getLineMetrics("0", frc);

      float sh   = lm.getAscent() - lm.getDescent();
      float sy   = h - sh;
      String s   = "x-axis";
      float sw   = (float)font.getStringBounds(s, frc).getWidth();
      float sx   = (w - sw);
      //float xinc = (w - PAD)/(float)this._data.size();
      float xinc = (w - PAD)/(float)HOURS;
      for(int i = 0; i < HOURS; i++){
         s  = i + ":00";
         sy = h;
         sw = (float)font.getStringBounds(s,frc).getWidth();
         sx = PAD + i*xinc - sw/2;
         if(i%6 == 0 || i == 23){
            g2.drawString(s, sx, sy);
         }
      }
   }

   /**/
   private void setTypicalYAxis(Graphics2D g2){
      final double MID = 2.5;
      int increment    = this._data.size();
      int h            = this.getHeight();
      int w            = this.getWidth();
      double min       = this.min();
      double max       = this.max();
      max += 1.0;
      min -= 1.0;

      Font font             = g2.getFont();
      FontRenderContext frc = g2.getFontRenderContext();
      LineMetrics lm        = font.getLineMetrics("0", frc);
      float sh              = lm.getAscent() + lm.getDescent();

      g2.setPaint(Color.BLACK);
      g2.draw(new Line2D.Double(PAD, 0, PAD, h - PAD));
      g2.draw(new Line2D.Double(PAD, h - PAD, w, h - PAD));
      //Now, try to start drawing the y-axis text
      double yinc = (h - PAD)/(double)(max - min);
      double value = min;
      while(value < max){
         int temp = (int)value;
         if((temp % 5) == 0 || value == min){
            String s = new String("" + temp);
            if(value == min){
               s = new String("" + String.format("%.2f",value));
            }
            double sw=(double)font.getStringBounds(s,frc).getWidth();
            double sx = (PAD - sw)/2.0;
            double sy = h - PAD -(temp - min)*yinc +sh/4;
            if(value == min){
               sy = h - PAD -(value-min) + sh/4;
            }
            if(temp % 5 != 4){
               g2.drawString(s, (float)sx, (float)sy);
            }
            if(value != min){
               sy = h - PAD -(temp - min)*yinc;
               g2.draw(new Line2D.Double(PAD,sy,w,sy));
            }
         }
         value += 1.0;
      }
   }

}
