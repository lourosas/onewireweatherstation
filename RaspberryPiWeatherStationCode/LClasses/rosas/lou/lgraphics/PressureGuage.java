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

/////////////////////////Public Methods///////////////////////////////
public class PressureGuage extends AnalogGuage{
   /*
   */
   public PressureGuage(String data, String units){
      super(data, units);
   }

   /*
   */
   @Override
   public void paintComponent(Graphics g){
      int width   = this.getWidth();
      int height  = this.getHeight();
      int XCENTER = 240;
      int YCENTER = 240;
      int min             =   0;
      int max             =  -1;
      double radToTics    = 0.0;
      double zeroDegValue = 0.0;
      int    modulo       = 1;

      super.paintComponent(g);
      if(this.units().equals("INHG")     ||
         this.units().equals("INCHES HG")){
         min          = 27;
         max          = 32;
         radToTics    = 3.75;
         zeroDegValue = 31.375;
      }
      else if(this.units().equals("MMHG") ||
              this.units().equals("MILLIMETERS HG")){
         min          = 685;
         max          = 815;
         radToTics    = 97.5;
         zeroDegValue = 798.75;
         modulo       = 5;
      }
      else if(this.units().equals("MB")   ||
              this.units().equals("MILLIBARS")){
         min          =  914;
         max          = 1084;
         radToTics    = 127.5;
         zeroDegValue = 1062.75;
         modulo       = 10;
      }
      for(int i = min; i < (max + 1); ++i){
         if((i % modulo) == 0){
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
      g.setFont(new Font("TimesRoman", Font.BOLD, 16));
      if(this.units().equals("INHG")       ||
         this.units().equals("INCHES HG")){
         String value = this.data() + " in. Hg";
         g.drawString(value, 210, 400);
      }
      else if(this.units().equals("MMHG") ||
              this.units().equals("MILLIMETERS HG")){
         String value = this.data() + " mm Hg";
         g.drawString(value, 205,400);
      }
      else if(this.units().equals("MB")   ||
              this.units().equals("MILLIBARS")){
         String value = this.data()+" mb";
         g.drawString(value, 205,400);
      }
      Point end = this.grabEnd(this.data(),
                               DISTANCE_DOT_FROM_ORIGIN-10,
                               radToTics,
                               zeroDegValue);
      g.drawLine(XCENTER,YCENTER,end.x,end.y);
   }
}
