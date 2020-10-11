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
      super.paintComponent(g);
      this.setBackground(Color.WHITE);
      Graphics2D g2    = (Graphics2D)g;
      double increment = this._data.size();
      int h            = this.getHeight();
      int w            = this.getWidth();

      this.setXAxis(g2);
      this.setYAxis(g2);
      g2.setPaint(Color.BLUE);
      double max = this.max();
      double min = this.min();
      if(this._data.get(0).type() != WeatherDataType.PRESSURE){
         max += 1.0;
         min -= 1.0;
      }
      else{
         max += 0.10;
         min -= 0.10;
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
         double dx1 = PAD + i * ((w - PAD)/increment);
         double dx2 = PAD + (i + 1) * ((w - PAD)/increment);
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
      Iterator<WeatherData> it = this._data.iterator();
      int h                    = this.getHeight();
      int w                    = this.getWidth();
      int value                = 0;

      Font font             = g2.getFont();
      FontRenderContext frc = g2.getFontRenderContext();
      LineMetrics lm        = font.getLineMetrics("0", frc);

      float sh   = lm.getAscent() - lm.getDescent();
      float sy   = h - sh;
      String s   = "x-axis";
      float sw   = (float)font.getStringBounds(s, frc).getWidth();
      float sx   = (w - sw);
      float xinc = (w - PAD)/(float)this._data.size();

      String currentHour = "-"; //put something in for comparison
      while(it.hasNext()){
         String time = it.next().time();
         String hour = time.split(":")[0];
         if(!currentHour.equals(hour)){
            currentHour = hour;
            s = hour + ":" + time.split(":")[1];
            sy = h;
            sw = (float)font.getStringBounds(s,frc).getWidth();
            sx = PAD + value*xinc - sw/2;
            System.out.println(s);
            System.out.println(sx);
            g2.drawString(s, sx, sy);
         }
         ++value;
      }
   }

   /**/
   private void setYAxis(Graphics2D g2){}
}
