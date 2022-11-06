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
public class ThermalGuage extends AnalogGuage{
   /*
   */
   public ThermalGuage(String data, String units){
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

      super.paintComponent(g);
      int min             =    0;
      int max             =   -1;
      double radToTics    =  0.0;
      double zeroDegValue =  0.0;
      if(this.units().equals("TEMPF") || this.units().equals("DPF")){
         min          =   -40;
         max          =   120;
         radToTics    = 120.0;  //PI radians per 120 degrees
         zeroDegValue = 100.0;  //at Zero (radians), 100 degrees
      }
      else if(this.units().equals("TEMPC") ||
              this.units().equals("DPC")){
         min          =  -40;
         max          =   60;
         radToTics    = 75.0;
         zeroDegValue = 47.5;
      }
      else if(this.units().equals("TEMPK") ||
              this.units().equals("DPK")){
         min          =    233;
         max          =    333;
         radToTics    =   75.0;
         zeroDegValue = 320.65;
      }
      for(int i = min; i < (max + 1)  ; i++){
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
      String numerical = this.concatTempAndUnits();
      g.setFont(new Font("TimesRoman", Font.BOLD, 16));
      if(this.units().equals("TEMPF") ||
         this.units().equals("DPF")){
         g.drawString(numerical, 230, 400);
      }
      else if(this.units().equals("TEMPC") ||
              this.units().equals("DPC")){
         g.drawString(numerical, 220, 400);
      }
      else if(this.units().equals("TEMPK") ||
              this.units().equals("DPK")){
         g.drawString(numerical, 215,400);
      }
      Point end = this.grabEnd(this.data(),
                               DISTANCE_DOT_FROM_ORIGIN-10,
                               radToTics,
                               zeroDegValue);
      g.drawLine(XCENTER,YCENTER,end.x,end.y);
   }

   /////////////////////Protected Methods/////////////////////////////
   /*
   */
   @Override
   protected Point grabEnd
   (
      String data,
      int radius,
      double radToTics,
      double zeroDegValue
   ){
      int    engValue;
      double metValue;
      double t;
      if(this.units().equals("TEMPF")){
         engValue = Integer.parseInt(data);
         t = Math.PI/radToTics*(engValue - zeroDegValue);
      }
      else{
         metValue = Double.parseDouble(data);
         t = Math.PI/radToTics*(metValue - zeroDegValue);
      }
      int x = (int)(480/2 + radius * Math.cos(t));
      int y = (int)(480/2 + radius * Math.sin(t));
      return new Point(x,y);
   }

   //////////////////////Private Methods//////////////////////////////
   /*
   */
   private String concatTempAndUnits(){
      //Going to start here for now...then go into more detail as
      //needed...
      String tempUnits = this.data();
      if(this.units().equals("TEMPC") ||
         this.units().equals("DPC")){
         tempUnits = tempUnits.concat(" \u00b0C");
      }
      else if(this.units().equals("TEMPF") ||
              this.units().equals("DPF")){
         tempUnits = tempUnits.concat(" \u00b0F");
      }
      else if(this.units().equals("TEMPK") ||
              this.units().equals("DPK")){
         tempUnits = tempUnits.concat(" K");
      }
      return tempUnits;
   }
}
