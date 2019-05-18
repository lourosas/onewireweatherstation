/********************************************************************
* Copyright (C) 2015 Lou Rosas
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

package rosas.lou.clock;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rosas.lou.clock.*;

public class TimerController
implements ActionListener, KeyListener{
   private LTimer    timer     = null;
   private TimerView timerview = null;

   //**********************Constuctors******************************
   /*
   Constructor of no arguments
   */
   public TimerController(){}

   //**********************Public Methods***************************
   /*
   Implementation of the actionPerformed method from the
   ActionListener Interface
   */
   public void actionPerformed(ActionEvent e){
      Object o = e.getSource();
      if(o instanceof JButton){
         this.handleButtonItem((JButton)o);
      }
      else if(o instanceof JMenuItem){
         this.handleJMenuItem((JMenuItem)o);
      }
   }

   /*
   */
   public void addTimer(LTimer t){
      this.timer = t;
   }

   /*
   */
   public void addView(TimerView tv){
      this.timerview = tv;
   }

   /*
   */
   public void initialize(LTimer t, TimerView tv){
      this.addTimer(t);
      this.addView(tv);
   }

   /*
   Implementation of the keyPressed method from the KeyListener
   Interface
   */
   public void keyPressed(KeyEvent k){
      if(k.getSource() instanceof JButton){
         if(k.getKeyCode() == KeyEvent.VK_ENTER){
            JButton button = (JButton)k.getSource();
            button.doClick();
         }
      }
   }

   /*
   Implementation of the keyReleased method from the KeyListener
   Interface
   */
   public void keyReleased(KeyEvent k){}

   /*
   Implementation of the keyTyped method from the KeyListener
   Interface
   */
   public void keyTyped(KeyEvent k){}

   //******************Private Methods******************************
   /*
   */
   private void handleButtonItem(JButton b){
      String buttonText = b.getText();
      if(buttonText.equals("Quit")){
         System.exit(0);
      }
      else if(buttonText.equals("Reset")){
         this.timer.reset();
         this.timerview.resetPressed();
      }
      else if(buttonText.equals("Start")){
         this.timer.start();
         this.timerview.startPressed();
      }
      else if(buttonText.equals("Stop")){
         this.timer.stop();
         this.timerview.stopPressed();
      }
      else if(buttonText.equals("Lap")){
         this.timer.lap();
      }
   }

   /*
   */
   private void handleJMenuItem(JMenuItem j){
      String menuItemText = j.getText();
      if(menuItemText.equals("Quit")){
         System.exit(0);
      }
      else if(menuItemText.equals("Start")){
         this.timer.start();
         this.timerview.startPressed();
      }
      else if(menuItemText.equals("Stop")){
         this.timer.stop();
         this.timerview.stopPressed();
      }
      else if(menuItemText.equals("Reset")){
         this.timer.reset();
         this.timerview.resetPressed();
      }
      else if(menuItemText.equals("Lap")){
         this.timer.lap();
      }
   }
}
