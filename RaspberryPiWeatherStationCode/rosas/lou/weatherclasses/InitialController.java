/*
*/

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
