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
   private static final int YPAD = 50;
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
      else{
         if(this._units != Units.ENGLISH){
            this.setPressureYAxisNonEnglish(g2);
            //this.setTypicalYAxis(g2);
         }
         else{
            this.setPressureYAxisEnglish(g2);
         }
      }
      g2.setPaint(Color.BLUE);
      double max = this.max();
      double min = this.min();
      if(this._data.get(0).type() != WeatherDataType.PRESSURE){
         max += 1.0;
         min -= 1.0;
      }
      else{
         if(this._units == Units.ENGLISH){
            max += 0.05;
            min -= 0.05;
         }
         else{
            max += 0.10;
            min -= 0.10;
         }
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
         data1 = this.setTheValue(data1);
         data2 = this.setTheValue(data2);

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
         double dx1 = (YPAD + value1 * (w-YPAD)/TINCREMENT);
         double dx2 = (YPAD + value2 * (w-YPAD)/TINCREMENT);
         if(data1 > WeatherData.DEFAULTVALUE &&
            data2 > WeatherData.DEFAULTVALUE){
            dy1 = h-PAD-(data1 - min)*yinc;
            dy2 = h-PAD-(data2 - min)*yinc;
            //g2.fill(new Ellipse2D.Double(dx1-2, dy1-2, 4, 4));
            g2.draw(new Line2D.Double(dx1, dy1, dx2, dy2));
          }
      }
   }

   /**/
   private double max() throws NumberFormatException{
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
         else if(this._units == Units.PERCENTAGE){
            value = it.next().percentageData();
         }
         else{
            throw new NumberFormatException();
         }
         if(value > max){
            max = value;
         }
      }
      return max;
   }

   /**/
   private double min() throws NumberFormatException{
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
         else if(this._units == Units.PERCENTAGE){
            value = it.next().percentageData();
         }
         else{
            throw new NumberFormatException();
         }
         if(value < min && value > WeatherData.DEFAULTVALUE){
            min = value;
         }
      }
      return min;
   }

   /**/
   private double setTheValue(double data){
      double returnValue = Double.NEGATIVE_INFINITY;
      String value       = null;
      Double d           = null;
      try{
         value = String.format("%.2f", data);
         if(this._data.get(0).type() == WeatherDataType.PRESSURE){
            if(this._units == Units.ABSOLUTE ||
               this._units == Units.METRIC){
               value = String.format("%.1f", data);
            }
         }
         d           = Double.parseDouble(value);
         returnValue = d.doubleValue();
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      return returnValue;
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
      float xinc = (w - YPAD)/(float)HOURS;
      for(int i = 0; i < HOURS; i++){
         s  = i + ":00";
         sy = h;
         sw = (float)font.getStringBounds(s,frc).getWidth();
         sx = YPAD + i*xinc - sw/2;
         //if(i%6 == 0 || i == 23){
         if(i%3 == 0 || i == 23){
            g2.setPaint(Color.BLACK);
            g2.drawString(s, sx, sy);
            g2.setPaint(Color.LIGHT_GRAY);
            g2.draw(new Line2D.Double(sx+sw/2,0,sx+sw/2,h-PAD));
         }
      }
   }

   /**/
   private void setPressureYAxisNonEnglish(Graphics2D g2){
      final double MID = 2.5;
      int increment    = this._data.size();
      int h            = this.getHeight();
      int w            = this.getWidth();
      try{
         double min = this.min() - 0.1;
         double max = this.max() + 0.1;
         Font font             = g2.getFont();
         FontRenderContext frc = g2.getFontRenderContext();
         LineMetrics lm        = font.getLineMetrics("0", frc);
         float sh              = lm.getAscent() + lm.getDescent();

         g2.setPaint(Color.BLACK);
         g2.draw(new Line2D.Double(YPAD, 0, YPAD, h - PAD));
         g2.draw(new Line2D.Double(YPAD, h - PAD, w, h - PAD));
         //Now, start drawing out the y-axis text
         double yinc  = (h - PAD)/(double)(max - min);
         double value = min;
         while(value < max){
            int temp = (int)(value * 10.0);
            if((temp % 5) == 0 || value == min){
               String s = new String("" + (double)(temp/10.0));
               if(value == min){
                  s = new String(String.format("%.2f", value));
               }
               double sw=(double)font.getStringBounds(s,frc).getWidth();
               double sx = (YPAD - sw)/2.0;
               double currentValue = (double)(temp/10.0);
               double sy = h - PAD -(currentValue - min)*yinc+sh/4;
               if(value == min){
                  sy = h - PAD - (value - min) + sh/4;
               }
               if(temp % 5 != 4){
                  g2.drawString(s, (float)sx, (float)sy);
               }
               if(value != min){
                  sy = h - PAD - (currentValue - min)*yinc;
                  g2.draw(new Line2D.Double(YPAD,sy,w,sy));
               }
            }
            value += 0.01;
         }
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
   }

   /**/
   private void setPressureYAxisEnglish(Graphics2D g2){
      final double MID = 2.5;
      int increment    = this._data.size();
      int h            = this.getHeight();
      int w            = this.getWidth();
      try{
         double min = this.min() - 0.05;
         double max = this.max() + 0.05;
         Font font             = g2.getFont();
         FontRenderContext frc = g2.getFontRenderContext();
         LineMetrics lm        = font.getLineMetrics("0", frc);
         float sh              = lm.getAscent() + lm.getDescent();

         g2.setPaint(Color.BLACK);
         g2.draw(new Line2D.Double(YPAD, 0, YPAD, h - PAD));
         g2.draw(new Line2D.Double(YPAD, h - PAD, w, h - PAD));
         //Now, start drawing out the y-axis text
         double yinc  = (h - PAD)/(double)(max - min);
         double value = min;
         while(value < max){
            int temp = (int)(value * 100.0);
            if((temp % 5) == 0 || value == min){
               String s = new String("" + (double)(temp/100.0));
               if(value == min){
                  s = new String(String.format("%.2f", value));
               }
               double sw=(double)font.getStringBounds(s,frc).getWidth();
               double sx = (YPAD - sw)/2.0;
               double currentValue = (double)(temp/100.0);
               double sy = h - PAD -(currentValue - min)*yinc+sh/4;
               if(value == min){
                  sy = h - PAD - (value - min) + sh/4;
               }
               if(temp % 5 != 4){
                  g2.drawString(s, (float)sx, (float)sy);
               }
               if(value != min){
                  sy = h - PAD - (currentValue - min)*yinc;
                  g2.draw(new Line2D.Double(YPAD,sy,w,sy));
               }
            }
            value += 0.01;
         }
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
   }

   /**/
   private void setTypicalYAxis(Graphics2D g2){
      final double MID = 2.5;
      int increment    = this._data.size();
      int h            = this.getHeight();
      int w            = this.getWidth();
      try{
         double min = this.min() - 1.0;
         double max = this.max() + 1.0;
         Font font             = g2.getFont();
         FontRenderContext frc = g2.getFontRenderContext();
         LineMetrics lm        = font.getLineMetrics("0", frc);
         float sh              = lm.getAscent() + lm.getDescent();

         g2.setPaint(Color.BLACK);
         g2.draw(new Line2D.Double(YPAD, 0, YPAD, h - PAD));
         g2.draw(new Line2D.Double(YPAD, h - PAD, w, h - PAD));
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
               double sx = (YPAD - sw)/2.0;
               double sy = h - PAD -(temp - min)*yinc +sh/4;
               if(value == min){
                  sy = h - PAD -(value-min) + sh/4;
               }
               if(temp % 5 != 4){
                  g2.drawString(s, (float)sx, (float)sy);
               }
               if(value != min){
                  sy = h - PAD -(temp - min)*yinc;
                  g2.draw(new Line2D.Double(YPAD,sy,w,sy));
               }
            }
            value += 1.0;
         }
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
   }
}
