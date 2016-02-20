/********************************************************************
* Copyright (C) 2008 Lou Rosas
* This file is part of many applications registered with
* the GNU General Public License as published
* by the Free Software Foundation; either version 3 of the License,
* or (at your option) any later version.
* PaceCalculator is distributed in the hope that it will be
* useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.
* If not, see <http://www.gnu.org/licenses/>.
********************************************************************/
package myclasses;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GenericJFrame extends JFrame{
   /**
   * Constructor of no arguments
   */
   public GenericJFrame(){
      this("");
   }
   /**
   *Title Constructor
   */
   public GenericJFrame(String title){
      super(title);
      this.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent w){
            setVisible(false);
            System.exit(0);
         }
      });
   }
}