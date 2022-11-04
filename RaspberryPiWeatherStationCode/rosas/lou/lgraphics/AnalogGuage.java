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
   protected static final int DISTANCE_DOT_FROM_ORIGIN = 480/2 - 40;
   protected static final int DIAMETER_BIG_DOT         = 8;
   protected static final int DIAMETER_SMALL_DOT       = 4;

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
   }

   ////////////////////////Protected Methods//////////////////////////
   /**/
   protected String data(){
      return this._data;
   }

   /**/
   protected String units(){
      return this._units;
   }

   /*
   */
   protected Point minToLocation
   (
      int step,
      int radius,
      double radToTics,
      double zeroDegValue
   ){
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
      double t = Math.PI/radToTics*(step  - zeroDegValue);
      int x    = (int)(480/2 + radius * Math.cos(t));
      int y    = (int)(480/2 + radius * Math.sin(t));
      return new Point(x,y);
   }

   /*
   */
   protected Point grabEnd
   (
      String data,
      int radius,
      double radToTics,
      double zeroDegValue
   ){
      int    engValue = 0;
      double metValue;
      double t;

      metValue = Double.parseDouble(data);
      t        = Math.PI/radToTics*(metValue - zeroDegValue);
      int x = (int)(480/2 + radius * Math.cos(t));
      int y = (int)(480/2 + radius * Math.sin(t));
      return new Point(x,y);
   }

   /////////////////////////Private Methods///////////////////////////

}
