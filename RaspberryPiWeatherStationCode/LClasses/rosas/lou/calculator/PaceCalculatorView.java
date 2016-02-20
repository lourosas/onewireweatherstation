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
import javax.swing.text.*;
import java.io.*;
import myclasses.*;
import rosas.lou.*;
import rosas.lou.calculator.*;

/********************************************************************
The PaceCalculatorView class by Lou Rosas.  This class contains
all the operations and attributes related to the view of the
PaceCalculator--essentially, it is the GUI the user sees
and interacts with on the screen.
********************************************************************/
public class PaceCalculatorView extends GenericJFrame
implements Observer{
   private static final short WIDTH  = 540;
   private static final short HEIGHT = 180;
   
   private ButtonGroup buttonGroup;
   private ButtonGroup menuItemGroup;
   private CalculatePaceWindow cpw;
   private CalculateRunTimeWindow crtw;
   private CalculateDistanceWindow cdw;
   
   //***********************Public Methods***************************
   /*****************************************************************
   Constructor of no arguments
   *****************************************************************/
   public PaceCalculatorView(){
      this("", null);
   }
   
   /*****************************************************************
   Constructor taking the String attribute for the title and the
   controller object
   *****************************************************************/
   public PaceCalculatorView(String title, Object controller){
      super(title);
      //Set up the GUI and pass in the controller
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
   }

   /*****************************************************************
   Request the Pace Data from either the:
   1) Calculate Run Time Window
   2) Calculate Distance Window
   *****************************************************************/
   public Stack requestPace(String window){
      Stack paceStack = new Stack();
      try{
         if(window.equals("CRT-Calculate Run Time")){
            paceStack = crtw.requestPace();
         }
         else if(window.equals("CDW-Calculate Distance")){
            paceStack = cdw.requestPace();
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
      return paceStack;
   }

   /*****************************************************************
   Request the Run Distance Value from either the:
   1) Calculate Pace Window
   2) Calculate Run Time Window
   *****************************************************************/
   public String requestRunDistance(String window){
      String distance = new String();
      try{
         if(window.equals("CPW-Calculate Pace")){
            distance = cpw.requestRunDistance();
         }
         else if(window.equals("CRT-Calculate Run Time")){
            distance = crtw.requestRunDistance();
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();//This is just a test print for now
      }
      return distance;
   }

   /*****************************************************************
   Request the Run Time Value from either the:
   1) Calculate Pace Window
   2) Calculate Distance Window
   *****************************************************************/
   public Stack requestRunTime(String window){
      Stack returnStack = new Stack();
      try{
         //Currently, only three possiblities
         if(window.equals("CPW-Calculate Pace")){
            returnStack = cpw.requestRunTime();
         }
         else if(window.equals("CDW-Calculate Distance")){
            returnStack = cdw.requestRunTime();
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();//This is just a test print for now
      }
      return returnStack;
   }

   /*****************************************************************
   Implementation of the update method as part of the
   implementation of the Observer Interface.
   *****************************************************************/
   public void update(Observable o, Object arg){
      if(o instanceof PaceCalculatorController){
         if(arg instanceof String){
            String request = (String)arg;
            if(request.equals("Save")){
               this.setUpJFileChooser(o);
            }
            else if(!(request.equals("Quit"))){
               this.requestDisplay(request, o);
	         }
         }
      }
      else if(o instanceof PaceCalculator){}
   }

   //************************Private Methods*************************
   /*****************************************************************
   *****************************************************************/
   private void disableButtons(){
      Enumeration e = this.buttonGroup.getElements();
      while(e.hasMoreElements()){
         JButton b = (JButton)e.nextElement();
         if(!b.getText().equals("Quit")){
            b.setEnabled(false);
         }
      }
   }
   
   //****************************************************************
   //****************************************************************
   private void disableMenuItems(String toDisable){
      Enumeration e = this.menuItemGroup.getElements();
      while(e.hasMoreElements()){
         JMenuItem i = (JMenuItem)e.nextElement();
         if(i.getText().equals(toDisable)){
            i.setEnabled(false);
         }
      }
   }
   
   //****************************************************************
   //****************************************************************
   private void displayAboutPane(){
      JEditorPane aboutPane   = null;
      JEditorPane missionPane = null;
      JFrame aboutFrame = new GenericJInteractionFrame("About");
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      JTabbedPane jtp = new JTabbedPane();
      try{
         aboutPane = new JEditorPane(
            "file:///LClasses/rosas/lou/docs/paceCalculatorAbout.html");
         missionPane = new JEditorPane(
            "file:///LClasses/rosas/lou/docs/paceCalculatorMission.html");
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      aboutPane.setEditable(false);
      missionPane.setEditable(false);
      JScrollPane aboutJSP   = new JScrollPane(aboutPane);
      JScrollPane missionJSP = new JScrollPane(missionPane);
      jtp.addTab("About", null, aboutJSP, "");
      jtp.addTab("Mission Statement", null, missionJSP, "");
      aboutFrame.setSize(450,400);
      aboutFrame.setLocation((int)((dim.getWidth()  - 450)/2),
                             (int)((dim.getHeight() - 400)/2));
      aboutFrame.add(jtp, BorderLayout.CENTER);
      aboutFrame.setVisible(true);
   }
   
   //****************************************************************
   //****************************************************************
   private void displayHelpPane(){
      JEditorPane calcPacePane = null;
      JEditorPane calcRTPane   = null;
      JEditorPane calcDistPane = null;
      JFrame helpFrame =
                new GenericJInteractionFrame("Pace Calculator Help");
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      JTabbedPane jtp = new JTabbedPane();
      try{
         calcPacePane = new JEditorPane(
               "file:///LClasses/rosas/lou/docs/calculatePace.html");
         calcRTPane   = new JEditorPane(
            "file:///LClasses/rosas/lou/docs/calculateRuntime.html");
         calcDistPane = new JEditorPane(
           "file:///LClasses/rosas/lou/docs/calculateDistance.html");
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      calcPacePane.setEditable(false);
      calcRTPane.setEditable(false);
      calcDistPane.setEditable(false);
      JScrollPane calcPaceJSP = new JScrollPane(calcPacePane);
      JScrollPane calcRTJSP   = new JScrollPane(calcRTPane);
      JScrollPane calcDistSP  = new JScrollPane(calcDistPane);
      jtp.addTab("Calculate Pace", null, calcPaceJSP, "");
      jtp.addTab("Calculate Runtime", null, calcRTJSP, "");
      jtp.addTab("Calculate Distance", null, calcDistSP, "");
      
      helpFrame.setSize(400,400);
      helpFrame.setLocation((int)((dim.getWidth()  - 400)/2),
                            (int)((dim.getHeight() - 400)/2));
      helpFrame.add(jtp, BorderLayout.CENTER);
      helpFrame.setVisible(true);
   }

   //****************************************************************
   //****************************************************************
   private void enableAllButtons(){
      Enumeration e = this.buttonGroup.getElements();
      while(e.hasMoreElements()){
         JButton b = (JButton)e.nextElement();
         b.setEnabled(true);
      }
   }
   
   //****************************************************************
   //****************************************************************
   private void enableAllMenuItems(){
      Enumeration e = this.menuItemGroup.getElements();
      while(e.hasMoreElements()){
         JMenuItem i = (JMenuItem)e.nextElement();
         if(!i.isEnabled()){
            i.setEnabled(true);
         }
      }
   }
 
   //****************************************************************
   //Determine the correct display window to display.
   //The Controller is passed along to indicate which Listener to
   //register the GUI components.
   //****************************************************************
   private void requestDisplay
   (
      String windowRequest,
      Object controller
   ){
      if(windowRequest.equals("Calculate Pace")){
         this.setupCalculatePaceWindow(controller);
      }
      else if(windowRequest.equals("Calculate Runtime") ||
         windowRequest.equals("Calculate Run Time")){
         this.setupCalculateRuntimeWindow(controller);
      }
      else if(windowRequest.equals("Calculate Distance")){
         this.setupCalculateDistanceWindow(controller);
      }
      else if(windowRequest.equals("PCV-Help")){
         this.displayHelpPane();
      }
      else if(windowRequest.equals("About")){
         this.displayAboutPane();
      }
   }
     
   //****************************************************************
   //Set up the GUI For display
   //***************************************************************/
   private void setUpGUI(Object controller){
      //Get the content pane
      Container contentPane = this.getContentPane();
      //Get the screen dimensions
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //Set the size
      this.setSize(WIDTH, HEIGHT);
      //Set up the location
      this.setLocation((int)((dim.getWidth()  - WIDTH)/2),
                       (int)((dim.getHeight() - HEIGHT)/32));
      //Set up the rest of the GUI
      contentPane.add(this.setNorthPanel(), BorderLayout.NORTH);
      contentPane.add(this.setCenterPanel(controller),
                      BorderLayout.CENTER);
      //Set up the menu bar
      this.setJMenuBar(this.setUpMenuBar(controller));
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void setUpJFileChooser(Object controller){
      int selectMode = JFileChooser.FILES_AND_DIRECTORIES;
      JFileChooser tempChooser = new JFileChooser();
      
      tempChooser.setApproveButtonText("OK");
      tempChooser.setFileSelectionMode(selectMode);
      tempChooser.addActionListener((ActionListener)controller);
      
      tempChooser.showSaveDialog(this);
   }
  
   //****************************************************************
   //Set up and display the Calculate Distance Window, register it
   //with the appropriate listeners, and display.
   //****************************************************************
   private void setupCalculateDistanceWindow(Object controller){
      Object o;
      o = ((PaceCalculatorController)controller).requestModel();
      PaceCalculator pc = (PaceCalculator)o;
      try{
         cdw.setVisible(true);
      }
      catch(NullPointerException npe){
         cdw = new CalculateDistanceWindow(controller);
         pc.addObserver(cdw);
         cdw.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent w){
               enableAllButtons();
               enableAllMenuItems();
            }
         });
      }
      finally{
         this.disableButtons();
         this.disableMenuItems("Calculate Distance");
         this.disableMenuItems("Calculate Runtime");
         this.disableMenuItems("Calculate Pace");
         cdw.requestFocus();
      }
   }

   //****************************************************************
   //Set up and display the Calculate Pace Window, register it with
   //the appropriate listeners, and display.
   //NOTE:  Go Ahead and write more here later!!!
   //****************************************************************
   private void setupCalculatePaceWindow(Object controller){
      Object o;
      o = ((PaceCalculatorController)controller).requestModel();
      PaceCalculator pc = (PaceCalculator)o;
      try{
         cpw.setVisible(true);
      }
      catch(NullPointerException npe){
         cpw = new CalculatePaceWindow(controller);
         pc.addObserver(cpw);
         cpw.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent w){
               enableAllButtons();
               enableAllMenuItems();
            }
         });
      }
      finally{
         this.disableButtons();
         this.disableMenuItems("Calculate Distance");
         this.disableMenuItems("Calculate Runtime");
         this.disableMenuItems("Calculate Pace");
         cpw.requestFocus();
      }
   }

   //****************************************************************
   //Set up and display the Calculate Run Time Window, register it 
   //with the appropriate listeners, and display.
   //****************************************************************
   private void setupCalculateRuntimeWindow(Object controller){
      Object o;
      o = ((PaceCalculatorController)controller).requestModel();
      PaceCalculator pc = (PaceCalculator)o;
      try{
         crtw.setVisible(true);
      }
      catch(NullPointerException npe){
         crtw = new CalculateRunTimeWindow(controller);
         pc.addObserver(crtw);
         crtw.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent w){
               enableAllButtons();
               enableAllMenuItems();
            }
         });
      }
      finally{
         this.disableButtons();
         this.disableMenuItems("Calculate Distance");
         this.disableMenuItems("Calculate Runtime");
         this.disableMenuItems("Calculate Pace");
         crtw.requestFocus();
      }
   }

   //****************************************************************
   //Set up the North Panel of the GUI and return the JPanel
   //****************************************************************
   private JPanel setNorthPanel(){
      String calculateString = 
                     new String("What would you like to calculate?");
      JPanel northPanel = new JPanel();
      JLabel calculateLabel =
                  new JLabel(calculateString, SwingConstants.CENTER);
      
      //Set up the border
      northPanel.setBorder(
                       BorderFactory.createEmptyBorder(10,10,10,10));
      northPanel.add(calculateLabel);
      
      return northPanel;
   }
   
   //****************************************************************
   //Set up the Center Panel of the GUI and return the JPanel
   //****************************************************************
   private JPanel setCenterPanel(Object controller){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEtchedBorder());
      centerPanel.add(this.setUpButtonPanel(controller));
      
      return centerPanel;
   }
   
   //****************************************************************
   //Actually set up the Button Panel, put it in the appropriate
   //dimensions
   //****************************************************************
   private JPanel setUpButtonPanel(Object controller){
      JPanel buttonPanel = new JPanel();
      buttonPanel.setBorder(
                    BorderFactory.createEmptyBorder(15, 10, 10, 1));
      
      //Instantiate the button group
      this.buttonGroup = new ButtonGroup();
      
      //Set up the buttons and add to the GUI
      //Calculate Pace Button
      JButton calculatePace = new JButton("Calculate Pace");
      calculatePace.setMnemonic(KeyEvent.VK_P);
      //Register the button with the Action Listener
      calculatePace.addActionListener((ActionListener)controller);
      //Register the button with a KeyListener
      calculatePace.addKeyListener((KeyListener)controller);
      //Add the button to the Panel
      buttonPanel.add(calculatePace);
      //Add the Button to the ButtonGroup
      this.buttonGroup.add(calculatePace);
      
      //Calculate Run Time Button
      JButton calculateRunTime = new JButton("Calculate Run Time");
      calculateRunTime.setMnemonic(KeyEvent.VK_R);
      //Register the button with the Action Listener
      calculateRunTime.addActionListener((ActionListener)controller);
      //Register the button with a Key Listener
      calculateRunTime.addKeyListener((KeyListener)controller);
      //Add the button to the Panel
      buttonPanel.add(calculateRunTime);
      //Add the Button to the ButtonGroup
      this.buttonGroup.add(calculateRunTime);
      
      //Calculate Run Distance Button
      JButton calculateDistance = new JButton("Calculate Distance");
      calculateDistance.setMnemonic(KeyEvent.VK_D);
      //Register the button with the Action Listener
      calculateDistance.addActionListener((ActionListener)controller);
      //Register the button with the Key Listener
      calculateDistance.addKeyListener((KeyListener)controller);
      //Add the button to the Panel
      buttonPanel.add(calculateDistance);
      //Add the Button to the ButtonGroup
      this.buttonGroup.add(calculateDistance);
      
      //Quit Button
      JButton quitButton = new JButton("Quit");
      quitButton.setMnemonic(KeyEvent.VK_Q);
      //Register the button with the Action Listener
      quitButton.addActionListener((ActionListener)controller);
      //Register the button with the Key Listener
      quitButton.addKeyListener((KeyListener)controller);
      //Add the button to the Panel
      buttonPanel.add(quitButton);
      //Add the Button to the ButtonGroup
      this.buttonGroup.add(quitButton);
      
      return buttonPanel;
   }
   
   //****************************************************************
   //Set up the Menu Bar for the GUI
   //****************************************************************
   private JMenuBar setUpMenuBar(Object controller){
      JMenuBar jmenuBar = new JMenuBar();
      
      this.menuItemGroup = new ButtonGroup();
      //Set up the File Menu
      jmenuBar.add(this.setUpFileMenu(controller));
      //Setup the Action Menu
      jmenuBar.add(this.setUpActionMenu(controller));
      //Setup the Help Menu
      jmenuBar.add(this.setUpHelpMenu(controller));
      
      return jmenuBar;
   }
   
   //****************************************************************
   //Set up the File Menu
   //****************************************************************
   private JMenu setUpFileMenu(Object controller){
      JMenu file = new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);
      
      //Open Menu Item==>None Needed!!
      
      //Add the separator
      file.addSeparator();
      
      //Quit Menu Item
      JMenuItem quit = new JMenuItem("Quit", 'Q');
      quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                               InputEvent.CTRL_MASK));
      //Register with the Action Listener
      quit.addActionListener((ActionListener)controller);
      //Add the Menu Item to the button group
      this.menuItemGroup.add(quit);
      //Add to the file Menu
      file.add(quit);
      
      return file;
   }
   
   //****************************************************************
   //Set up the Action Menu
   //****************************************************************
   private JMenu setUpActionMenu(Object controller){
      JMenu action = new JMenu("Action");
      action.setMnemonic(KeyEvent.VK_A);
      
      //Calculate Distance Menu Item
      JMenuItem calculateDistance =
                            new JMenuItem("Calculate Distance", 'D');
      calculateDistance.setAccelerator(
          KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK)
      );
      //Register with the Action Listener
      calculateDistance.addActionListener((ActionListener)controller);
      //Add the Menu Item to the button group
      this.menuItemGroup.add(calculateDistance);
      //Add to the Action Menu
      action.add(calculateDistance);
      
      //Calculate Pace Menu Item
      JMenuItem calculatePace = new JMenuItem("Calculate Pace", 'P');
      calculatePace.setAccelerator(
          KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK)
      );
      //Register with the Action Listener
      calculatePace.addActionListener((ActionListener)controller);
      //Add to button group
      this.menuItemGroup.add(calculatePace);
      //Add to the Action Menu
      action.add(calculatePace);
      
      //Calculate Run Time Menu Item
      JMenuItem calculateRunTime =
                             new JMenuItem("Calculate Runtime", 'R');
      calculateRunTime.setAccelerator(
          KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK)
      );
      //Register with the Action Listener (add the Action Listener)
      calculateRunTime.addActionListener((ActionListener)controller);
      //Add the menu item to the button group
      this.menuItemGroup.add(calculateRunTime);
      //Add to the Action Menu
      action.add(calculateRunTime);
      
      return action;
   }
   
   //****************************************************************
   //Set up the Help Menu
   //****************************************************************
   private JMenu setUpHelpMenu(Object controller){
      JMenu help = new JMenu("Help");
      help.setMnemonic(KeyEvent.VK_H);
      
      //Help Menu Item
      JMenuItem helpItem = new JMenuItem("Help", 'H');
      helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
      helpItem.setActionCommand("PCV-Help");
      //Add the Action Listener
      helpItem.addActionListener((ActionListener)controller);
      //Add to the button group
      this.menuItemGroup.add(helpItem);
      //Add to the Help Menu
      help.add(helpItem);
      
      help.addSeparator();
      
      //About Menu Item
      JMenuItem about = new JMenuItem("About", 'A');
      about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,0));
      //Add the Action Listener
      about.addActionListener((ActionListener)controller);
      //Add to the button group
      this.menuItemGroup.add(about);
      //Add to the Help Menu
      help.add(about);
      
      return help;
   }
}
