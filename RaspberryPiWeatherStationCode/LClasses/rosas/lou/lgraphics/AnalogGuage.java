//////////////////////////////////////////////////////////////////////
/*
Copyright 2022 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
*/
public class AnalogGuage extends JPanel{
   private static final int DISTANCE_DOT_FROM_ORIGIN = 480/2 - 40;
   private static final int DIAMETER_BIG_DOT         = 8;
   private static final int DIAMETER_SMALL_DOT       = 4;

   private String _data;
   private String _units;

   /**/
   public AnalogGuage(String data, String units){
      this._data  = data;
      this._units = units;
   }

   /*
   */
   @Override
   public void paintComponent(Graphics g){
      int width  = this.getWidth();
      int height = this.getHeight();
      int XCENTER = 240;
      int YCENTER = 240;
      super.paintComponent(g);
      this.setBackground(Color.BLACK);
      g.setColor(Color.WHITE);
      //g.fillOval(5, 5, 480,480);
      //g.fillOval(XCENTER-235,YCENTER-235,480,480);
      g.fillOval(0,0,480,480);
      g.setColor(Color.BLACK);
      //g.fillOval(10,10,470,470);
      //g.fillOval(XCENTER-230, YCENTER-230,470,470);
      g.fillOval(5, 5,470,470);

      g.setColor(Color.RED);
      for(int i = -40; /*i < 60*/i < 121  ; i++){
         if((i%10) == 0){
            Point dot = this.minToLocation(i,DISTANCE_DOT_FROM_ORIGIN);
            g.fillOval(dot.x - (DIAMETER_SMALL_DOT/2),
                       dot.y - (DIAMETER_SMALL_DOT/2),
                       DIAMETER_SMALL_DOT,
                       DIAMETER_SMALL_DOT);
            g.drawString(""+i, dot.x-10, dot.y-5);
         }
      }
      String numerical = this.concatTempAndUnits();
      g.setFont(new Font("TimesRoman", Font.BOLD, 16));
      if(this._units.equals("TEMPF")){
         g.drawString(numerical, 230, 400);
      }
      else if(this._units.equals("TEMPC")){
         g.drawString(numerical, 220, 400);
      }
      else if(this._units.equals("TEMPK")){
         g.drawString(numerical, 215,400);
      }
      Point end =this.grabEnd(this._data,DISTANCE_DOT_FROM_ORIGIN-10);
      g.drawLine(XCENTER,YCENTER,end.x,end.y);
   }

   /////////////////////////Private Methods///////////////////////////
   /*
   */
   private String concatTempAndUnits(){
      String tempUnits = this._data;
      if(this._units.equals("TEMPC")){
         tempUnits = tempUnits.concat(" \u00b0C");
      }
      else if(this._units.equals("TEMPF")){
         tempUnits = tempUnits.concat(" \u00b0F");
      }
      else if(this._units.equals("TEMPK")){
         tempUnits = tempUnits.concat(" K");
      }
      return tempUnits;
   }
   /*
   */
   private Point minToLocation(int step, int radius){
      /*
      2PI radians/60 ticks
      radians/tick * tick = radians
      grab the sine and cosine values of both
      x_value = (X-Center + R*cos(angle above))
      y_value = (Y-Center + R*sin(angle above))
      These values will need to change based on the number of markings
      in the 2PI radians
      */
      //double t = 2*Math.PI*(step-15)/60;
      double t = Math.PI/120.0*(step  - 100);
      int x    = (int)(480/2 + radius * Math.cos(t));
      int y    = (int)(480/2 + radius * Math.sin(t));
      return new Point(x,y);
   }

   /*
   */
   private Point grabEnd(String data, int radius){
      int    engValue;
      double metValue;
      double t;
      if(this._units.equals("TEMPF")){
         engValue = Integer.parseInt(data);
         t = Math.PI/120.0*(engValue - 100);
      }
      else{
         metValue = Double.parseDouble(data);
         t = Math.PI/120.0*(metValue - 100);
      }
      int x = (int)(480/2 + radius * Math.cos(t));
      int y = (int)(480/2 + radius * Math.sin(t));
      return new Point(x,y);
   }
}
