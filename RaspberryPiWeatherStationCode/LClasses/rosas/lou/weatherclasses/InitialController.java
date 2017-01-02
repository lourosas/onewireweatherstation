//******************************************************************
//Initial Controller Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rosas.lou.weatherclasses.*;

public class InitialController
implements ActionListener, KeyListener{
   //**********************Constructors*****************************
   /*
   Constructor of no arguments
   */
   public InitialController(){}

   //********************Public Methods*****************************
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

   //************************Private Methods************************
   /*
   */
   public void handleButtonItem(JButton b){
      String buttonText = b.getText();
      if(buttonText.equals("Quit")){
         System.exit(0);
      }
      else if(buttonText.equals("Start")){
         System.out.println(buttonText);
      }
      else if(buttonText.equals("Stop")){
         System.out.println(buttonText);
      }
   }

   /*
   */
   public void handleJMenuItem(JMenuItem j){
      String menuItemText = j.getText();
      if(menuItemText.equals("Quit")){
         System.exit(0);
      }
   }
}
