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
   private void setYAxis(Graphics2D g2){
      final double MID = 2.5;
      double increment = this._data.size();
      int h = this.getHeight();
      int w = this.getWidth();

      Font font             = g2.getFont();
      FontRenderContext frc = g2.getFontRenderContext();
      LineMetrics lm        = font.getLineMetrics("0", frc);
      float sh = lm.getAscent() + lm.getDescent();

      g2.setPaint(Color.BLACK);
      g2.draw(new Line2D.Double(PAD, 0, PAD, h - PAD));
      g2.draw(new Line2D.Double(PAD, h - PAD, w, h - PAD));
      //Now, try to start drawing the y-axis text
      float yinc = (h - PAD)/(float)(this.max() - this.min());
      for(int i = (int)this.min() - 1; i < (int)this.max() + 1; i++){
         if((i % 5) == 0 || i == ((int)this.min() - 1)){
            String s = new String("" + i);
            float sw = (float)font.getStringBounds(s,frc).getWidth();
            float sx = (PAD - sw)/2;
            float sy = h - PAD - (i-((int)this.min()-1))*yinc+sh/4;
            g2.drawString(s, sx, sy);
            if(i!=((int)this.min()-1) || i!=((int)this.max()+1)){
               sy -= sh/4;
               g2.draw(new Line2D.Double(PAD,sy,w,sy));
            }
            if((i!=(int)this.min()-1) && (i-(this.min()-1)>=2*MID)){
               float f = (float)i - (float)MID;
               s  = new String("" + f);
               sw = (float)font.getStringBounds(s,frc).getWidth();
               sx = (PAD - sw)/2;
               sy = h - PAD -(f -((int)this.min() - 1))*yinc + sh/4;
               g2.drawString(s, sx, sy);
            }
         }
         else if(i==((int)this.max()-2)&&
                 (((int)this.max()-1)%5>MID)||
                 (((int)this.max()-1)%5==0)){
            String s = "";
            float sx, sy, sw;
            sx = sy = sw = (float)0;
            if((int)(this.max() - 1) % 5 > MID){
               s = new String("" + i);
               sy = h - PAD -(i -((int)this.min()-1)*yinc+sh/4);
            }
            else{
               float f = ((int)this.max() + 1) - (float)MID;
               s = new String("" + f);
               sy=h-PAD - (f - ((int)this.min() - 1)) * yinc + sh/4;
            }
            sw = (float)font.getStringBounds(s,frc).getWidth();
            sx = (PAD - sw)/2;
            g2.drawString(s,sx,sy);
         }
      }
   }
}
