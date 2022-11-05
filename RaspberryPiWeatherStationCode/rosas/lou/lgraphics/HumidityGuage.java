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
import rosas.lou.lgraphics.*;
import rosas.lou.weatherclasses.*;

////////////////////////////Public Methods////////////////////////////
public class HumidityGuage extends AnalogGuage{
   /*
   */
   public HumidityGuage(String data){
      super(data, "percentage"); //Classify the Units
   }

   /*
   */
   @Override
   public void paintComponent(Graphics g){
      int min             =   0;
      int max             = 100;
      double radToTics    = 75.0;
      double zeroDegValue = 87.5;
      int width           = this.getWidth();
      int height          = this.getHeight();
      int XCENTER         = 240;
      int YCENTER         = 240;

      super.paintComponent(g);
      for(int i = min; i < (max + 1); ++i){
         if((i%10) == 0){
            Point dot = this.minToLocation(i,
                                           DISTANCE_DOT_FROM_ORIGIN,
                                           radToTics,
                                           zeroDegValue);
            g.fillOval(dot.x - (DIAMETER_SMALL_DOT/2),
                       dot.y - (DIAMETER_SMALL_DOT/2),
                       DIAMETER_SMALL_DOT,
                       DIAMETER_SMALL_DOT);
            g.drawString(""+i, dot.x-10, dot.y-5);
         }
      }
      String percentage = (this.data()).concat("%");
      g.setFont(new Font("TimesRoman", Font.BOLD, 16));
      g.drawString(percentage, 230,400);
      Point end = this.grabEnd(this.data(),
                               DISTANCE_DOT_FROM_ORIGIN-10,
                               radToTics,
                               zeroDegValue);
      g.drawLine(XCENTER,YCENTER,end.x,end.y);
   }

   //////////////////////Protected Methods////////////////////////////
   /*
   */
   @Override
   protected Point grabEnd
   (
      String data,
      int    radius,
      double radToTics,
      double zeroDegValue
   ){
      int value = Integer.parseInt(data);
      double t  = Math.PI/radToTics*(value - zeroDegValue);
      int x     = (int)(480/2 + radius * Math.cos(t));
      int y     = (int)(480/2 + radius * Math.sin(t));
      return new Point(x,y);
   }
}
