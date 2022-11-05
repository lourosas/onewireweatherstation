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

      super.paintComponent(g);
      if(this.units().equals("INHG")){
         min          = 27;
         mix          = 32;
         radToTics    = 15.0/4.0;
         zeroDegValue = 31.25;
      }
      else if(this.units().equals("MMHG")){

      }
      else if(this.units().equals("MB")){

      }
      for(int i = min; i < (max + 1); ++i){
         
      }
   }
}
