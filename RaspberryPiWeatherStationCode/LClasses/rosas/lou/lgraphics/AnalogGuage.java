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
      //g.fillOval(237,237,15,15);
      g.setFont(g.getFont().deriveFont(Font.BOLD,20));
      g.drawString("12", XCENTER-10, YCENTER-215);
      g.drawString("3",  XCENTER+224,YCENTER);
      g.drawString("6",  XCENTER-10, YCENTER+235);
      g.drawString("9",  XCENTER-230,YCENTER);
      /*
      int x = 240 + (int)((240-30)*Math.sin(0));
      int y = 240 - (int)((240-30)*Math.cos(0));
      g.drawString("A", x, y);
      x = 240 + (int)((240-30)*Math.sin(Math.PI/2));
      y = 240 - (int)((240-30)*Math.cos(Math.PI/2));
      g.drawString("B", x, y);
      x = 240 + (int)((240-30)*Math.sin(Math.PI));
      y = 240 - (int)((240-30)*Math.cos(Math.PI));
      g.drawString("C", x, y);
      x = 240 + (int)((240-30)*Math.sin(3*Math.PI/2));
      y = 240 - (int)((240-30)*Math.cos(3*Math.PI/2));
      g.drawString("D", x, y);
      */
      /*
      String value = new String("-40");
      int x = 240 + (int)(210*Math.sin(7*Math.PI/6));
      int y = 253 - (int)(210*Math.cos(7*Math.PI/6));
      g.drawString(value, x, y);
      value = new String("120");
      x = 240 + (int)(210*Math.sin(5*Math.PI/6));
      y = 253 - (int)(210*Math.cos(5*Math.PI/6));
      g.drawString(value, x, y);
      value = new String("40");
      x = 240;
      y = 253 - (int)(210*Math.cos(0));
      g.drawString(value, x, y);
      */
      /*
      for(int i = 1; i < 13; ++i){
         String value = Integer.toString(i);

         int x = 240+(int)(210*Math.sin(i*Math.PI/6));
         //int y = 253-(int)(210*Math.cos(i*Math.PI/6));
         int y = 240-(int)(210*Math.cos(i*Math.PI/6));
         g.drawString(value, x, y);
      }
      */
   }
}
