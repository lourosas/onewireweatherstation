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
import rosas.lou.*;
import rosas.lou.clock.TimeListener;
import rosas.lou.clock.StopWatch;

/**
 * */
public class StopWatchController implements ActionListener,
KeyListener{
   private static final short QUERYTIME = 1000;
   private StopWatch stopWatch;

   /**
 * Constructor of no arguments
 * */
   public StopWatchController(){
      this(QUERYTIME);
   }

   /**
 * Constructor taking the Query Time as an argument...
 * */
   public StopWatchController(short queryTime){
      this.stopWatch = new StopWatch();
      //Now, need to start the StopWatch
      Thread t = new Thread(this.stopWatch);
      t.start();
   }

   /**
 * Implementation of the actionPerformed method from the
 * ActionListener Interface
 * */
   public void actionPerformed(ActionEvent e){
      if(e.getSource() instanceof JButton){
         this.handleJButton((JButton)e.getSource());
      }
      else if(e.getSource() instanceof JMenuItem){
         this.handleJMenuItem((JMenuItem)e.getSource());
      }
   }

   /**
 *
 * */
   public void addTimeListenerToModel(TimeListener tl){
      this.stopWatch.addTimeListener(tl);
   }

   /**
 * Implementation of the keyPressed method from the KeyListener
 * Interface
 * */
   public void keyPressed(KeyEvent k){
      Object object  = k.getSource();
      int    keyCode = k.getKeyCode();
      if(object instanceof JButton){
         if(keyCode == KeyEvent.VK_ENTER){
            JButton button = (JButton)object;
            button.doClick();
         }
      }
   }

   /**
 *
 * */
   public void keyReleased(KeyEvent k){}

   /**
 * Implementation of the keyTyped method from the KeyListener
 * Interface
 * */
   public void keyTyped(KeyEvent k){}

   /**
 * */
   public void killTheStopWatch(){
      this.stopWatch.kill();
   }

   ////////////////////////////Private Methods////////////////////////
   //
   //
   //
   private void handleJButton(JButton jb){
      if(jb.getActionCommand().equals("Start")){
         this.stopWatch.start();
      }
      else if(jb.getActionCommand().equals("Stop")){
         this.stopWatch.stop();
      }
      else if(jb.getActionCommand().equals("Reset")){
         this.stopWatch.reset();
      }
      else if(jb.getActionCommand().equals("Lap")){
         this.stopWatch.lap();
      }
   }

   //
   //
   //
   private void handleJMenuItem(JMenuItem jmi){
      if(jmi.getActionCommand().equals("Quit")){
         this.killTheStopWatch();
         System.exit(0);
      }
      else if(jmi.getActionCommand().equals("Start")){
         this.stopWatch.start();
      }
      else if(jmi.getActionCommand().equals("Stop")){
         this.stopWatch.stop();
      }
      else if(jmi.getActionCommand().equals("Reset")){
         this.stopWatch.reset();
      }
      else if(jmi.getActionCommand().equals("Lap")){
         this.stopWatch.lap();
      }
   }
}

//////////////////////////////////////////////////////////////////////
