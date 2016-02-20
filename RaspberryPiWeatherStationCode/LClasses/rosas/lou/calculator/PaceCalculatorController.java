//*******************************************************************
//Pace Calculator Application
//Copyright (C) 2008 Lou Rosas
//This file is part of PaceCalculator.
//PaceCalculator is free software; you can redistribute it
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
package rosas.lou.calculator;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rosas.lou.*;
import rosas.lou.calculator.*;

/********************************************************************
The PaceCalculatorController class by Lou Rosas.
This class contains all the operations and attributes related to
the controller of the PaceCalculator Application.
It handles all user input.
********************************************************************/
public class PaceCalculatorController extends Observable
implements ActionListener, KeyListener, ItemListener{
   private Object view;
   private Object model;
   
   //***************************Public Methods***********************
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public PaceCalculatorController(){}
   
   /*****************************************************************
   The constructor setting the View Object and the Model Object.
   Since the Controller Controls both the View and Model Ojbects
   In the Model-View-Controller architecture, BOTH the objects
   needed associations to the Controller object.
   *****************************************************************/
   public PaceCalculatorController(Object theView, Object theModel){
      this.setView(theView);
      this.setModel(theModel);
   }
   
   /*****************************************************************
   Set the View Object for this instance.  This is for the purpose
   of obtaining data from the views upon user interaction.
   *****************************************************************/
   public void setView(Object theView){
      this.view = theView;
      this.addObserver((PaceCalculatorView)this.view);
   }
   
   /*****************************************************************
   Set the Model object.  This is for the purpose of requesting 
   the model to change its state as needed.
   *****************************************************************/
   public void setModel(Object theModel){
      this.model = theModel;
   }

   /*****************************************************************
    Request the Model Object
   *****************************************************************/
   public Object requestModel(){
      return this.model;
   }
   
   /*****************************************************************
   Implementation of the actionPerformed method from the 
   ActionListener Interface
   *****************************************************************/
   public void actionPerformed(ActionEvent e){
      Object o = e.getSource();
      if(o instanceof JButton){
         this.handleButtonItem((JButton)o);
      }
      else if(o instanceof JMenuItem){
         this.handleMenuItem((JMenuItem)o);
      }
      else if(o instanceof JTextField){
         this.handleTextField((JTextField)o);
      }
      else if(o instanceof JFileChooser){
         JFileChooser jfc = (JFileChooser)o;
         if(e.getActionCommand().equals("ApproveSelection")){
            PaceCalculator pc = (PaceCalculator)this.model;
            pc.saveRunData(jfc.getSelectedFile());
         }
      }
   }
   
   /*****************************************************************
   Implementation of the keyPressed method from the KeyListener
   Interface
   *****************************************************************/
   public void keyPressed(KeyEvent k){
      Object o    = k.getSource();
      int keyCode = k.getKeyCode();
      
      if(o instanceof JButton && keyCode == KeyEvent.VK_ENTER){
         ((JButton)o).doClick();
      }
   }
   
   /*****************************************************************
   Implementation of the keyReleased method from the KeyListener
   Interface
   *****************************************************************/
   public void keyReleased(KeyEvent k){}
   
   /*****************************************************************
   Implementation of the keyTyped method from the KeyListener
   Interface
   *****************************************************************/
   public void keyTyped(KeyEvent k){}
   
   /*****************************************************************
   Implementation of the itemStateChanged method from the
   ItemListener Interface
   *****************************************************************/
   public void itemStateChanged(ItemEvent ie){
      Object o = ie.getItem();
      if(o instanceof JRadioButton){
         JRadioButton jrb = (JRadioButton)o;
         if(jrb.isSelected()){
            String units = jrb.getActionCommand();
            ((PaceCalculator)this.model).setDistanceUnits(units);
         }
      }
   } 
   
   //***************************Private Methods**********************
   /*****************************************************************
   Handle Action Events from a JButton press
   *****************************************************************/
   private void handleButtonItem(JButton button){
      String command = button.getActionCommand();
      PaceCalculatorView pcv = (PaceCalculatorView)this.view;
      PaceCalculator pc = (PaceCalculator)this.model;
      if(!(command.equals("Quit"))){
         if(command.equals("Calculate Pace")     ||
            command.equals("Calculate Run Time") ||
            command.equals("Calculate Distance")){
            //Display the appropriate window:
            //The Calcuate Pace Window or the Calculate Run Time Window
            //or the Calculate Distance Window
            //Update the view observer
            this.setChanged();
            this.notifyObservers(command);
            this.clearChanged();
            //Ping the Pace Calculator for the current Run Data
            pc.pingRunData();
         }
         else if(command.equals("Save")){
            this.setChanged();
            this.notifyObservers(command);
            this.clearChanged();
         }
         else if(command.equals("CPW-Cancel") ||
                 command.equals("CRT-Cancel") ||
                 command.equals("CDW-Cancel") ||
                 command.equals("Clear")){
            pc.simpleAction(command);
         }
         else if(command.equals("CPW-Calculate Pace")){
            Stack  timeStack = pcv.requestRunTime(command);
            String distance  = pcv.requestRunDistance(command);
            timeStack.push(distance);
            pc.calculatePace(timeStack);            
         }
         else if(command.equals("CRT-Calculate Run Time")){
            String distance  = pcv.requestRunDistance(command);
            Stack  paceStack = pcv.requestPace(command);
            paceStack.push(distance);
            pc.calculateRunTime(paceStack);
         }
         else if(command.equals("CDW-Calculate Distance")){
            Stack timeStack = pcv.requestRunTime(command);
            Stack paceStack = pcv.requestPace(command);
            pc.calculateRunDistance(paceStack, timeStack);
         }
      }
      else{
         //A real simple "quit" for the method
         pc.quit();
      }
   }
   
   /*****************************************************************
   Handle Action Events from a JMenuItem Selection
   *****************************************************************/
   private void handleMenuItem(JMenuItem item){
      String command = item.getActionCommand();
      PaceCalculatorView pcv = (PaceCalculatorView)this.view;
      PaceCalculator pc = (PaceCalculator)this.model;
      if(!(command.equals("Quit"))){
         if(command.equals("Calculate Pace")    ||
            command.equals("Calculate Runtime") ||
            command.equals("Calculate Distance")||
            command.equals("Save")              ||
            command.equals("PCV-Help")          ||
            command.equals("About")){
            //Display the appropriate window:
            //The Calcuate Pace Window or the Calculate Run Time
            //Window
            //or the Calculate Distance Window
            //Notify the Observer (for this, just the view)
            this.setChanged();
            this.notifyObservers(command);
            this.clearChanged();
         }
         else if(command.equals("Clear")     ||
                 command.equals("CPW-Cancel")||
                 command.equals("CRT-Cancel")||
                 command.equals("CDW-Cancel")||
                 command.equals("CPW-Help")  ||
                 command.equals("CRT-Help")  ||
                 command.equals("CDW-Help")){
            pc.simpleAction(command);//Same thing as from the button
         }
         else if(command.equals("CPW-Calculate Pace")){
            Stack  timeStack = pcv.requestRunTime(command);
            String distance  = pcv.requestRunDistance(command);
            timeStack.push(distance);
            pc.calculatePace(timeStack);
         }
         else if(command.equals("CRT-Calculate Run Time")){
            String distance  = pcv.requestRunDistance(command);
            Stack  paceStack = pcv.requestPace(command);
            paceStack.push(distance);
            pc.calculateRunTime(paceStack);
         }
         else if(command.equals("CDW-Calculate Distance")){
            Stack timeStack = pcv.requestRunTime(command);
            Stack paceStack = pcv.requestPace(command);
            pc.calculateRunDistance(paceStack, timeStack);
         }
      }
      else{
         pc.quit();
      }
   }

   /********************************************************************
   I don't think this will be needed.
   ********************************************************************/
   private void handleTextField(JTextField textField){}

}
