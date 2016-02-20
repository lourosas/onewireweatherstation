//*******************************************************************
//CalculateDistanceWindow
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
import java.io.IOException;
import myclasses.*;
import rosas.lou.*;
import rosas.lou.calculator.*;

/********************************************************************
The CalculateDistanceWindow class by Lou Rosas.  This class
contains all the operations and attributes related to the view
of this window.
********************************************************************/
public class CalculateDistanceWindow extends GenericJInteractionFrame
implements Observer{
   //The Appropriate private attributes
   private static final short WIDTH  = 420;
   private static final short HEIGHT = 300;
   
   private ButtonGroup buttonGroup, menuItemGroup, radioButtonGroup;
   private Vector textFieldVector;
   private JLabel distanceModeLabel;
   
   //***************************Public Methods***********************
   /*****************************************************************
   Constructor taking the Controller Object
   *****************************************************************/
   public CalculateDistanceWindow(Object controller){
      super("Calculate Distance");
      textFieldVector = new Vector();
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
   }
   
   /*****************************************************************
   Return the data stored in the text fields reflecting the
   Run Pace
   *****************************************************************/
   public Stack requestPace(){
      String seconds   = new String();
      String minutes   = new String();
      Stack  paceStack = new Stack();
      
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         if(jtf.getName().equals("Pace Minutes")){
            minutes = new String(jtf.getText());
         }
         else if(jtf.getName().equals("Pace Seconds")){
            seconds = new String(jtf.getText());
         }
      }
      paceStack.push(seconds);
      paceStack.push(minutes);
      
      return paceStack;
   }
   
   /*****************************************************************
   Return the data stored in the text fields reflecting the
   Run Time
   *****************************************************************/
   public Stack requestRunTime(){
      String hours        = new String();
      String minutes      = new String();
      String seconds      = new String();
      Stack  runTimeStack = new Stack();
      
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         if(jtf.getName().equals("Run Time Hours")){
            hours = new String(jtf.getText());
         }
         else if(jtf.getName().equals("Run Time Minutes")){
            minutes = new String(jtf.getText());
         }
         else if(jtf.getName().equals("Run Time Seconds")){
            seconds = new String(jtf.getText());
         }
      }
      
      runTimeStack.push(seconds);
      runTimeStack.push(minutes);
      runTimeStack.push(hours);
      
      return runTimeStack;
   }
   
   /*****************************************************************
   Implementation of the update method as part of the
   Observer Interface Implementation
   *****************************************************************/
   public void update(Observable o, Object arg){
      if(o instanceof PaceCalculator){
         if(arg instanceof Integer){
         }
         else if(arg instanceof String){
            String value = (String)arg;
            if(value.equals("CDW-Cancel")){
               int event = WindowEvent.WINDOW_CLOSING;
               WindowEvent we = new WindowEvent(this, event);
               this.dispatchEvent(we);
            }
            else if(value.equals("Clear")){
               this.clearTextFields();
               this.disableButtons();
               this.disableMenuItems();
            }
            else if(value.equals("CDW-Help")){
               this.displayHelpPane();
            }
         }
         else if(arg instanceof Stack){
            if(this.isVisible()){
               Stack responseStack = (Stack)arg;
               if(responseStack.peek() instanceof String){
                  String value = (String)responseStack.pop();
                  String data  = (String)responseStack.pop();
                  if(value.equals("Divide By 0 Error")){
                     this.showErrorDialog(value);
                     this.focusTextField("Pace Minutes");
                  }
                  else if(value.equals("Number Error")){
                     this.showErrorDialog(data);
                     this.focusTextField(data);
                  }
                  else if(value.equals("RunTime Error")){
                     this.showErrorDialog(data);
                     String focus = data.substring(0,8);
                     if(focus.equals("Run Time")){
                        focus = data.substring(0, 14);
                        if(!focus.equals("Run Time Hours"))
                           focus = data.substring(0, 16);
                        this.focusTextField(focus);
                     }
                     else if(data.substring(0,4).equals("Pace")){
                        focus = data.substring(0, 12);
                        this.focusTextField(focus);
                     }
                  }
                  else if(value.equals("File Save Error")){
                     data.concat(", could not save data to file");
                     this.showSaveErrorDialog(data);
                  }
               }
            }
         }
         else if(arg instanceof RunData){
            if(this.displayRunData((RunData)arg)){
               this.enableButtons();
               this.enableMenuItems();
            }
         }
      }
   }

   //**************************Private Methods***********************
   //****************************************************************
   //Clear out the text fields
   //****************************************************************
   private void clearTextFields(){
      Enumeration e = textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         jtf.setText("");
      }
      this.focusTextField("Run Time Hours");
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void disableButtons(){
      Enumeration e = this.buttonGroup.getElements();
      while(e.hasMoreElements()){
         JButton b = (JButton)e.nextElement();
         if(b.getText().equals("Save")){
            b.setEnabled(false);
         }
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void disableMenuItems(){
      Enumeration e = this.menuItemGroup.getElements();
      while(e.hasMoreElements()){
         JMenuItem i = (JMenuItem)e.nextElement();
         if(i.getText().equals("Save")){
            i.setEnabled(false);
         }
      }
   }   
   
   //****************************************************************
   //
   //****************************************************************
   private void displayDistance(RunDistance distance){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf    = (JTextField)e.nextElement();
         String rounded    = new String();
         if(jtf.getName().equals("Distance Text Field")){
            double dist     = distance.getDistance();
            double disround = dist*100.;
            rounded += (Math.round(disround)/100.);
            jtf.setText(rounded);
         }
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void displayHelpPane(){
      JEditorPane ep = null;
      JFrame helpFrame = 
             new GenericJInteractionFrame("Calculate Distance Help");
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      try{
         ep = new JEditorPane(
           "file:///LClasses/rosas/lou/docs/calculateDistanceWindowHelp.html");
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      ep.setEditable(false);
      JScrollPane hsp = new JScrollPane(ep);
      
      helpFrame.setSize(400, 400);
      helpFrame.setLocation((int)((dim.getWidth()  - 400)/2),
                            (int)((dim.getHeight() - 400)/2));
      helpFrame.add(hsp, BorderLayout.CENTER);
      helpFrame.setVisible(true);
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void displayPace(RunTime pace){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         String data = new String();
         if(jtf.getName().equals("Pace Minutes")){
            data += "" + pace.getMinutes();
            jtf.setText(data);
         }
         else if(jtf.getName().equals("Pace Seconds")){
            double seconds = pace.getSeconds();
            double rounded = seconds * 1000.;
            data += (Math.round(rounded)/1000.);
            try{
               if(rounded > 9.){
                  data = data.substring(0, 5);
               }
               else{
                  data = data.substring(0, 4);
               }
            }
            catch(StringIndexOutOfBoundsException sobe){
               data = data;
            }
            jtf.setText(data);
         }
      }
   }

   //****************************************************************
   //
   //****************************************************************
   private boolean displayRunData(RunData rundata){
      boolean displayed = false;
      short units = rundata.getDistance().getUnits();
      this.handleRadioButtonChanges(units);
      if(this.shouldPrint(rundata)){
         this.displayDistance(rundata.getDistance());
         this.displayRuntime(rundata.getRuntime());
         this.displayPace(rundata.getPace());
         displayed = true;
      }
      return displayed;
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void displayRuntime(RunTime runtime){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         String data    = new String();
         if(jtf.getName().equals("Run Time Hours")){
            data += "" + runtime.getHours();
            jtf.setText(data);
         }
         else if(jtf.getName().equals("Run Time Minutes")){
            data += "" + runtime.getMinutes();
            jtf.setText(data);
         }
         else if(jtf.getName().equals("Run Time Seconds")){
            double secs      = runtime.getSeconds();
            double roundsecs = secs * 1000.;
            data += (Math.round(roundsecs)/1000.);
            try{
               if(secs > 9.){
                  data = data.substring(0, 5);
               }
               else{
                  data = data.substring(0, 4);
               }
            }
            catch(StringIndexOutOfBoundsException sobe){
               data = data;
            }
            jtf.setText(data);
         }
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void enableButtons(){
      Enumeration e = this.buttonGroup.getElements();
      while(e.hasMoreElements()){
         JButton b = (JButton)e.nextElement();
         if(!b.isEnabled()){
            b.setEnabled(true);
         }
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void enableMenuItems(){
      Enumeration e = this.menuItemGroup.getElements();
      while(e.hasMoreElements()){
         JMenuItem i = (JMenuItem)e.nextElement();
         if(!i.isEnabled()){
            i.setEnabled(true);
         }
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void focusTextField(String message){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         String name = jtf.getName();
         if(name.equals(message)){
            jtf.requestFocus();
            jtf.selectAll();
         }
      }
   }
   //****************************************************************
   //
   //****************************************************************
   private void handleRadioButtonChanges(short value){
      boolean isMiles      = false;
      boolean isKilometers = false;
      boolean isMeters     = false;
      switch(value){
         case RunDistance.MILES:
            this.distanceModeLabel.setText("Miles");
            isMiles = true;
            break;
         case RunDistance.KILOMETERS:
            this.distanceModeLabel.setText("Kilometers");
            isKilometers = true;
            break;
         case RunDistance.METERS:
            this.distanceModeLabel.setText("Meters");
            isMeters = true;
            break;
         default:  this.distanceModeLabel.setText("Miles");
      }
      if(!this.isVisible()){
         Enumeration e = this.radioButtonGroup.getElements();
         while(e.hasMoreElements()){
            JRadioButton jrb = (JRadioButton)e.nextElement();
            if(jrb.getText().equals("Miles")){
               jrb.setSelected(isMiles);
            }
            else if(jrb.getText().equals("Kilometers")){
               jrb.setSelected(isKilometers);
            }
            else if(jrb.getText().equals("Meters")){
               jrb.setSelected(isMeters);
            }
         }
      }
   }
   
   //****************************************************************
   //Set up the Center Panel
   //****************************************************************
   private JPanel setCenterPanel(Object controller){
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new GridBagLayout());
      
      JPanel runTimePanel = this.setUpRunTimePanel();
      GridBagConstraints runTimeConstr = new GridBagConstraints();
      runTimeConstr.gridx   = 0;
      runTimeConstr.gridy   = 0;
      runTimeConstr.fill    = GridBagConstraints.VERTICAL;
      runTimeConstr.anchor  = GridBagConstraints.NORTHWEST;
      runTimeConstr.weightx = 0;
      runTimeConstr.weighty = 0;
      centerPanel.add(runTimePanel, runTimeConstr);
      
      JPanel pacePanel = this.setUpPacePanel();
      GridBagConstraints paceConstraints = new GridBagConstraints();
      paceConstraints.gridx   = 0;
      paceConstraints.gridy   = 1;
      paceConstraints.fill    = GridBagConstraints.VERTICAL;
      paceConstraints.anchor  = GridBagConstraints.NORTHWEST;
      paceConstraints.weightx = 0;
      paceConstraints.weighty = 0;
      centerPanel.add(pacePanel, paceConstraints);
      
      JPanel disPanel = this.setUpDistancePanel();
      GridBagConstraints disConstraints = new GridBagConstraints();
      disConstraints.gridx   = 0;
      disConstraints.gridy   = 2;
      disConstraints.fill    = GridBagConstraints.VERTICAL;
      disConstraints.anchor  = GridBagConstraints.NORTHWEST;
      disConstraints.weightx = 0;
      disConstraints.weighty = 0;
      centerPanel.add(disPanel, disConstraints);
      
      return centerPanel;
   }
   
   //****************************************************************
   //Set up the North Panel
   //****************************************************************
   private JPanel setNorthPanel(Object controller){
      JPanel northPanel = new JPanel();
      JPanel rbPanel    = new JPanel();
      JRadioButton miles, kilometers, meters;
      radioButtonGroup = new ButtonGroup();

      Object o = controller;
      
      PaceCalculatorController pcc = (PaceCalculatorController)o;
      PaceCalculator pc = (PaceCalculator)(pcc.requestModel());
      
      int mode = pc.getDistanceUnits();
      boolean isMiles      = (mode == PaceCalculator.MILES);
      boolean isKilometers = (mode == PaceCalculator.KILOMETERS);
      boolean isMeters     = (mode == PaceCalculator.METERS);
      
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      JLabel modeLabel =
             new JLabel("Distance Mode:  ", SwingConstants.CENTER);
             
      miles      = new JRadioButton("Miles", isMiles);
      kilometers = new JRadioButton("Kilometers", isKilometers);
      meters     = new JRadioButton("Meters", isMeters);
      
      miles.addItemListener((ItemListener)controller);
      kilometers.addItemListener((ItemListener)controller);
      meters.addItemListener((ItemListener)controller);
      
      radioButtonGroup.add(miles);
      radioButtonGroup.add(kilometers);
      radioButtonGroup.add(meters);
      
      rbPanel.add(miles);
      rbPanel.add(kilometers);
      rbPanel.add(meters);
      
      northPanel.add(modeLabel);
      northPanel.add(rbPanel);
      
      return northPanel;
   }
   
   //****************************************************************
   //Set South Panel
   //****************************************************************
   private JPanel setSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      this.buttonGroup  = new ButtonGroup();
      
      southPanel.setBorder(BorderFactory.createRaisedBevelBorder());
      
      JButton calculateDistance = new JButton("Calculate Distance");
      calculateDistance.setActionCommand("CDW-Calculate Distance");
      JButton clear = new JButton("Clear");
      JButton cancel = new JButton("Cancel");
      cancel.setActionCommand("CDW-Cancel");
      JButton save = new JButton("Save");
      
      calculateDistance.addActionListener((ActionListener)controller);
      calculateDistance.addKeyListener((KeyListener)controller);
      clear.addActionListener((ActionListener)controller);
      clear.addKeyListener((KeyListener)controller);
      cancel.addActionListener((ActionListener)controller);
      cancel.addKeyListener((KeyListener)controller);
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);
      
      buttonGroup.add(calculateDistance);  buttonGroup.add(clear);
      buttonGroup.add(cancel);             buttonGroup.add(save);
      
      southPanel.add(calculateDistance);  southPanel.add(clear);
      southPanel.add(cancel);             southPanel.add(save);
      
      this.disableButtons();
      
      return southPanel;
   }
   
   //****************************************************************
   //Set up the Menu Bar
   //****************************************************************
   private JMenuBar setUpMenuBar(Object controller){
      JMenuBar jmenuBar = new JMenuBar();
      
      this.menuItemGroup = new ButtonGroup();
      //Set up the File Menu
      jmenuBar.add(this.setUpFileMenu(controller));
      //Set up the Help Menu
      jmenuBar.add(this.setUpHelpMenu(controller));
      
      return jmenuBar;
   }
   
   //****************************************************************
   //Set up the File Menu.  It is easier to separate out here as 
   //a separate method than to put everything into one mega-mehtod
   //That violates the cohesion rule
   //****************************************************************
   private JMenu setUpFileMenu(Object controller){
      JMenu file = new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);
      //Clear Menu Item
      JMenuItem clear = new JMenuItem("Clear", 'l');
      //Cancel Menu Item
      JMenuItem cancel = new JMenuItem("Cancel",'C');
      cancel.setActionCommand("CDW-Cancel");
      //Calculate Distance Menu Item
      JMenuItem calcDis = new JMenuItem("Calculate Distance", 'D');
      calcDis.setActionCommand("CDW-Calculate Distance");
      //Save Menu Item
      JMenuItem save = new JMenuItem("Save", 'S');
      
      clear.addActionListener((ActionListener)controller);
      cancel.addActionListener((ActionListener)controller);
      calcDis.addActionListener((ActionListener)controller);
      save.addActionListener((ActionListener)controller);
      
      this.menuItemGroup.add(clear);
      this.menuItemGroup.add(cancel);
      this.menuItemGroup.add(calcDis);
      this.menuItemGroup.add(save);
      
      file.add(clear);
      file.add(cancel);
      file.add(calcDis);
      file.addSeparator();
      file.add(save);
      
      this.disableMenuItems();
      
      return file;
   }
   
   //****************************************************************
   //Set up the Distance Panel and associated Swing objects
   //****************************************************************
   private JPanel setUpDistancePanel(){
      final int DISLEN = 10;
      
      //Distance Panel and additions
      JPanel disPanel = new JPanel();
      JLabel disLabel = new JLabel("Distance:");
      disLabel.setOpaque(true);
      disPanel.add(disLabel);
      
      JPanel distPanel = new JPanel();
      distPanel.setBorder(BorderFactory.createEmptyBorder(0,7,0,0));
      JTextField distTextField = new JTextField();
      distTextField.setName("Distance Text Field");
      distTextField.setColumns(DISLEN);
      distTextField.setEditable(false);
      distPanel.add(distTextField);
      textFieldVector.add(distTextField);
      //Set this by default
      this.distanceModeLabel = new JLabel("Miles");
      distPanel.add(this.distanceModeLabel);
      disPanel.add(distPanel);
      
      return disPanel;
   }
   
   //****************************************************************
   //Set up the Help Menu.
   //****************************************************************
   private JMenu setUpHelpMenu(Object controller){
      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic(KeyEvent.VK_H);
      //Help Menu Item
      JMenuItem help = new JMenuItem("Help", 'H');
      help.setActionCommand("CDW-Help");
      help.addActionListener((ActionListener)controller);
      this.menuItemGroup.add(help);
      helpMenu.add(help);
      
      return helpMenu;
   }
   
   //****************************************************************
   //Set up the GUI
   //****************************************************************
   private void setUpGUI(Object controller){
      //Get the Content Pane
      Container contentPane = this.getContentPane();
      //Get the screen dimensions
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //Set the size of the GUI
      this.setSize(WIDTH, HEIGHT);
      this.setLocation(
         (int)(dim.getWidth()/2  - WIDTH/2),
         (int)(dim.getHeight()/2 - HEIGHT/2));
      //Set up the appropriate Panels
      JPanel northPanel  = this.setNorthPanel(controller);
      JPanel centerPanel = this.setCenterPanel(controller);
      JPanel southPanel  = this.setSouthPanel(controller);
      
      contentPane.add(northPanel,  BorderLayout.NORTH);
      contentPane.add(centerPanel, BorderLayout.CENTER);
      contentPane.add(southPanel,  BorderLayout.SOUTH);
      
      //Set up the Menu Bar
      this.setJMenuBar(this.setUpMenuBar(controller));
   }
   
   //****************************************************************
   //****************************************************************
   private JPanel setUpPacePanel(){
      final int MINLEN = 2;
      final int SECLEN = 4;
      
      JPanel pacePanel = new JPanel();
      JLabel paceLabel = new JLabel("Pace:");
      paceLabel.setOpaque(true);
      pacePanel.add(paceLabel);
      
      JPanel minPanel = new JPanel();
      minPanel.setBorder(BorderFactory.createEmptyBorder(0,29,0,0));
      JTextField minTextField = new JTextField();
      minTextField.setName("Pace Minutes");
      minTextField.setColumns(MINLEN);
      minPanel.add(minTextField);
      textFieldVector.add(minTextField);
      JLabel minLabel = new JLabel("Minutes");
      minLabel.setOpaque(true);
      minPanel.add(minLabel);
      pacePanel.add(minPanel);
      
      JPanel secPanel = new JPanel();
      JTextField secTextField = new JTextField();
      secTextField.setName("Pace Seconds");
      secTextField.setColumns(SECLEN);
      secPanel.add(secTextField);
      textFieldVector.add(secTextField);
      JLabel secLabel = new JLabel("Seconds");
      secLabel.setOpaque(true);
      secPanel.add(secLabel);
      pacePanel.add(secPanel);
      
      return pacePanel;
   }
   
   //****************************************************************
   //****************************************************************
   private JPanel setUpRunTimePanel(){
      final int HRLEN  = 4;
      final int MINLEN = 2;
      final int SECLEN = 4;
      
      //Run Time Panel
      JPanel runTimePanel = new JPanel();
      JLabel runTimeLabel = new JLabel("Run Time:");
      runTimeLabel.setOpaque(true);
      runTimePanel.add(runTimeLabel);
      
      JPanel hourPanel = new JPanel();
      hourPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
      JTextField hourTextField = new JTextField();
      hourTextField.setName("Run Time Hours");
      hourTextField.setColumns(HRLEN);
      hourPanel.add(hourTextField);
      textFieldVector.add(hourTextField);
      JLabel hourLabel = new JLabel("Hours");
      hourLabel.setOpaque(true);
      hourPanel.add(hourLabel);
      runTimePanel.add(hourPanel);
      
      JPanel minPanel = new JPanel();
      JTextField minTextField = new JTextField();
      minTextField.setName("Run Time Minutes");
      minTextField.setColumns(MINLEN);
      minPanel.add(minTextField);
      textFieldVector.add(minTextField);
      JLabel minLabel = new JLabel("Minutes");
      minLabel.setOpaque(true);
      minPanel.add(minLabel);
      runTimePanel.add(minPanel);
      
      JPanel secPanel = new JPanel();
      JTextField secTextField = new JTextField();
      secTextField.setName("Run Time Seconds");
      secTextField.setColumns(SECLEN);
      secPanel.add(secTextField);
      textFieldVector.add(secTextField);
      JLabel secLabel = new JLabel("Seconds");
      secLabel.setOpaque(true);
      secPanel.add(secLabel);
      runTimePanel.add(secPanel);
      
      return runTimePanel;
   }
   
   //****************************************************************
   //****************************************************************
   private void showErrorDialog(String error){
      String exception = new String("Input error\n");
      String display   = new String("Input error");
      if(error.equals("Run Time Hours")   || 
         error.equals("Run Time Minutes") ||
         error.equals("Run Time Seconds") ||
         error.equals("Pace Minutes")     ||
         error.equals("Pace Seconds")){
         exception += "The " + error + " text field not entered ";
         exception += "properly!\n";
         exception += "Please enter a proper number for the ";
         exception += error + " text field";
      }
      else if(error.equals("Divide by 0 Error")){
         exception += error + "!\n";
         exception += "Please enter a value for Pace > 0";
      }
      else{
         exception += error + "!";
      }
      JOptionPane.showMessageDialog(this, exception, display,
                                    JOptionPane.ERROR_MESSAGE);
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void showSaveErrorDialog(String data){
      String exception = new String("Save Error!\n");
      String display   = new String("Save To File Error");
      exception.concat(", could not save data to file\n");
      JOptionPane.showMessageDialog(this, exception, display,
                                    JOptionPane.ERROR_MESSAGE);
   }
   
   //****************************************************************
   //
   //****************************************************************
   private boolean shouldPrint(RunData rundata){
      RunDistance distance = rundata.getDistance();
      RunTime     runtime  = rundata.getRuntime();
      RunTime     pace     = rundata.getPace();
      
      boolean     toPrint  = (distance.getDistance() > 0.);
      toPrint = toPrint || (runtime.getHours() > 0);
      toPrint = toPrint || (runtime.getMinutes() > 0);
      toPrint = toPrint || (runtime.getSeconds() > 0.);
      toPrint = toPrint || (pace.getHours() > 0);
      toPrint = toPrint || (pace.getMinutes() > 0);
      toPrint = toPrint || (pace.getSeconds() > 0.);
      
      return toPrint;
   }
}