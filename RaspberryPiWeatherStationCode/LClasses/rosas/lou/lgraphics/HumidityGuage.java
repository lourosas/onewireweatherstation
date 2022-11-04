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
            
         }
      }
   }
}
