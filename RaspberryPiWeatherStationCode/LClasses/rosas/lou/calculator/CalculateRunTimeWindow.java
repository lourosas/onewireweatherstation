//*******************************************************************
//CalculateRunTimeWindow
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
The CalculateRunTimeWindow class by Lou Rosas.  This class contains
all the operations and attributes related to the view of this
window which is dedicated to user input for calculating run time
given: 1)Distance, 2)Pace
********************************************************************/
public class CalculateRunTimeWindow extends GenericJInteractionFrame
implements Observer{
   //The appropriate private attributes and types
   private static final short WIDTH  = 420;
   private static final short HEIGHT = 300;

   private ButtonGroup buttonGroup, menuItemGroup, radioButtonGroup;
   private Vector textFieldVector;
   private JLabel distanceModeLabel;

   //***************************Public Methods***********************
   /*****************************************************************
   The Constructor taking the Controller Object:  which is needed
   for registering the GUIs to the Listeners
   *****************************************************************/
   public CalculateRunTimeWindow(Object controller){
      super("Calculate Run Time");
      textFieldVector = new Vector();
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
   }

   /*****************************************************************
   Return the data stored in the text fields reflecting the
   Run Pace.
   *****************************************************************/
   public Stack requestPace(){
      String seconds = new String();
      String minutes = new String();

      Stack paceStack = new Stack();

      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         if(jtf.getName().equals("Pace Minutes Text Field")){
            minutes = jtf.getText();
         }
         else if(jtf.getName().equals("Pace Seconds Text Field")){
            seconds = jtf.getText();
         }
      }
      paceStack.push(seconds);
      paceStack.push(minutes);

      return paceStack;
   }

   /*****************************************************************
   Return the data stored in the text field reflecting the
   Run Distance.
   *****************************************************************/
   public String requestRunDistance(){
      String distance = new String();
      Enumeration e = textFieldVector.elements();

      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         if(jtf.getName().equals("Distance Text Field")){
            distance = jtf.getText();
         }
      }

      return distance;
   }

   /*****************************************************************
   Implementation of the update method as part of the Observer
   Interface.
   *****************************************************************/
   public void update(Observable o, Object arg){
      if(o instanceof PaceCalculator){
         if(arg instanceof Integer){
         }
         else if(arg instanceof String){
            String value = (String)arg;
            if(value.equals("CRT-Cancel")){
               //See the comments in CalculatePaceWindow.java
               int event = WindowEvent.WINDOW_CLOSING;
               WindowEvent we = new WindowEvent(this, event);
               this.dispatchEvent(we);
            }
            else if(value.equals("Clear")){
               this.clearTextFields();
               this.disableButtons();
               this.disableMenuItems();
            }
            else if(value.equals("CRT-Help")){
               this.displayHelpPane();
            }
         }
         else if(arg instanceof Stack){
            try{
               if(this.isVisible()){  //Only Complete for visibility
                  Stack responseStack = (Stack)arg;
                  if(responseStack.peek() instanceof String){
                     String value = (String)responseStack.pop();
                     String data  = (String)responseStack.pop();
                     if(value.equals("Divide By 0 Error")){
                        //Display the error as appropriate
                        this.showErrorDialog(value);
                        this.focusTextField("Distance");
                     }
                     else if(value.equals("Number Error")){
                        this.showErrorDialog(data);
                        this.focusTextField(data);
                     }
                     else if(value.equals("RunDistance Error")){
                        this.showErrorDialog(data);
                        this.focusTextField("Distance");
                     }
                     else if(value.equals("RunTime Error")){
                        this.showErrorDialog(data);
                        String focus = data.substring(0, 7);
                        this.focusTextField(focus);
                     }
                     else if(value.equals("File Save Error")){
                        data.concat(", could not save data to file");
                        this.showSaveErrorDialog(data);
                     }
                  }
               }
            }
            catch(EmptyStackException ese){}//Don't do anything yet
         }
         else if(arg instanceof RunData){
            if(this.displayRunData((RunData)arg)){
               this.enableButtons();
               this.enableMenuItems();
            }
         }
      }
   }

   //***********************Private Methods**************************
   //****************************************************************
   //Clear out the text fields
   //****************************************************************
   private void clearTextFields(){
      Enumeration e = textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         jtf.setText("");
         if(jtf.getName().equals("Distance Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
      }
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
   private void displayHelpPane(){
      JFrame helpFrame =
             new GenericJInteractionFrame("Calculate Run Time Help");
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      JEditorPane jep = null;
      try{
         jep = new JEditorPane(
            "file:///LClasses/rosas/lou/docs/calculateRunTimeWindowHelp.html");
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      jep.setEditable(false);
      JScrollPane hsp = new JScrollPane(jep);
      helpFrame.setSize(400,400);
      helpFrame.setLocation((int)((dim.getWidth()  - 400)/2),
                            (int)((dim.getHeight() - 400)/2));
      helpFrame.add(hsp, BorderLayout.CENTER);
      helpFrame.setVisible(true);
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
   private void displayDistance(RunDistance rundistance){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf   = (JTextField)e.nextElement();
         String current   = new String();
         String textField = new String(jtf.getName());
         if(textField.equals("Distance Text Field")){
            double distance = rundistance.getDistance();
            double roundD   = distance * 100.;
            current += (Math.round(roundD)/100.);
            jtf.setText(current);
         }
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void displayPace(RunTime pace){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf   = (JTextField)e.nextElement();
         String current   = new String();
         String textField = new String(jtf.getName());
         if(textField.equals("Pace Minutes Text Field")){
            current += "" + pace.getMinutes();
            jtf.setText(current);
         }
         else if(textField.equals("Pace Seconds Text Field")){
            double paceseconds = pace.getSeconds();
            double paceRound   = paceseconds * 1000.;
            current += (Math.round(paceRound)/1000.);
            try{
               if(paceRound > 9.){
                  current = current.substring(0,5);
               }
               else{
                  current = current.substring(0,4);
               }
            }
            catch(StringIndexOutOfBoundsException sobe){
               current = current;
            }
            jtf.setText(current);
         }
      }
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void displayRuntime(RunTime runtime){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf   = (JTextField)e.nextElement();
         String current   = new String();
         String textField = new String(jtf.getName());
         if(textField.equals("Hour Text Field")){
            current += "" + runtime.getHours();
            jtf.setText(current);
         }
         else if(textField.equals("Minute Text Field")){
            current += "" + runtime.getMinutes();
            jtf.setText(current);
         }
         else if(textField.equals("Second Text Field")){
            double secs    = runtime.getSeconds();
            double roundS  = secs * 1000;
            current += (Math.round(roundS)/1000.);
            try{
               if(secs > 9.){
                  current = current.substring(0,5);
               }
               else{
                  current = current.substring(0,4);
               }
            }
            catch(StringIndexOutOfBoundsException sobe){
               current = current;
            }
            jtf.setText(current);
         }
      }
   }

   //****************************************************************
   //Display the Current Run Data
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
         if(message.equals("Distance") && 
            name.equals("Distance Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
         else if(message.equals("Minutes") &&
                 name.equals("Pace Minutes Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
         else if(message.equals("Seconds") &&
                 name.equals("Pace Seconds Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
      }
   }

   //****************************************************************
   //Handle the changes related to the JRadio Buttons (Upon a change
   //in the Distance Mode)
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
         default:
            this.distanceModeLabel.setText("Miles");
            isMiles = true;
      }
      //If the window is not visible, this should be performed
      if(!this.isVisible()){
         Enumeration e = radioButtonGroup.getElements();
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
   //Set up the GUI
   //****************************************************************
   private void setUpGUI(Object controller){
      //Get the Content Pane of the GUI
      Container contentPane = this.getContentPane();
      //Get the screen dimensions for window placement
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //Set the Size
      this.setSize(WIDTH, HEIGHT);
      this.setLocation((int)(dim.getWidth()/2  -  WIDTH/2),
                       (int)(dim.getHeight()/2 - HEIGHT/2));
      //Set up the appropriate Panels in the GUI
      JPanel northPanel  = this.setNorthPanel(controller);
      JPanel centerPanel = this.setCenterPanel(controller);
      JPanel southPanel  = this.setSouthPanel(controller);
      //Now add everything together
      contentPane.add(northPanel,  BorderLayout.NORTH);
      contentPane.add(centerPanel, BorderLayout.CENTER);
      contentPane.add(southPanel,  BorderLayout.SOUTH);
      //Set up the Menu Bar
      this.setJMenuBar(this.setUpMenuBar(controller));
   }

   //****************************************************************
   //
   //****************************************************************
   private JPanel setNorthPanel(Object controller){
      JRadioButton miles, kilometers, meters;
      Object o = controller;
      PaceCalculatorController pcc = (PaceCalculatorController)o;
      PaceCalculator pc = (PaceCalculator)(pcc.requestModel());

      int mode = pc.getDistanceUnits();
      boolean isMiles      = (mode == PaceCalculator.MILES);
      boolean isKilometers = (mode == PaceCalculator.KILOMETERS);
      boolean isMeters     = (mode == PaceCalculator.METERS);

      JPanel northPanel = new JPanel();
      JPanel rbPanel    = new JPanel();  //Radio Button Panel

      northPanel.setBorder(BorderFactory.createEtchedBorder());
      int labelPos     = SwingConstants.RIGHT;
      JLabel modeLabel = new JLabel("Distance Mode: ", labelPos);

      radioButtonGroup = new ButtonGroup();
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
   //
   //****************************************************************
   private JPanel setCenterPanel(Object controller){
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new GridBagLayout());

      JPanel distancePanel = this.setUpDistancePanel(controller);
      GridBagConstraints dConstraints = new GridBagConstraints();
      dConstraints.gridx   = 0;
      dConstraints.gridy   = 0;
      dConstraints.fill    = GridBagConstraints.VERTICAL;
      dConstraints.anchor  = GridBagConstraints.NORTHWEST;
      dConstraints.weightx = 0;
      dConstraints.weighty = 0;
      centerPanel.add(distancePanel, dConstraints);

      JPanel pacePanel = this.setUpPacePanel();
      GridBagConstraints pConstraints = new GridBagConstraints();
      pConstraints.gridx   = 0;
      pConstraints.gridy   = 1;
      pConstraints.fill    = GridBagConstraints.VERTICAL;
      pConstraints.anchor  = GridBagConstraints.NORTHWEST;
      pConstraints.weightx = 0;
      pConstraints.weighty = 0;
      centerPanel.add(pacePanel, pConstraints);

      JPanel runTimePanel = this.setUpRunTimePanel();
      GridBagConstraints rtConstraints = new GridBagConstraints();
      rtConstraints.gridx   = 0;
      rtConstraints.gridy   = 2;
      rtConstraints.fill    = GridBagConstraints.VERTICAL;
      rtConstraints.anchor  = GridBagConstraints.NORTHWEST;
      rtConstraints.weightx = 0;
      rtConstraints.weighty = 0;
      centerPanel.add(runTimePanel, rtConstraints);

      return centerPanel;
   }

   //****************************************************************
   //
   //****************************************************************
   private JPanel setUpDistancePanel(Object controller){
      final int LENGTH = 10;
      Object o = controller;
      PaceCalculatorController pcc = (PaceCalculatorController)o;
      PaceCalculator pc = (PaceCalculator)(pcc.requestModel());
      int mode = pc.getDistanceUnits();

      //Distance Panel additions
      JPanel distanceP = new JPanel();
      JLabel distanceL = new JLabel("Distance:");
      distanceL.setOpaque(true);
      distanceP.add(distanceL);

      JPanel textPanel = new JPanel();
      textPanel.setBorder(BorderFactory.createEmptyBorder(0,7,0,0));
      JTextField textField = new JTextField();
      textField.setName("Distance Text Field");
      textField.setColumns(LENGTH);
      textFieldVector.add(textField);
      textPanel.add(textField);
      if(mode == PaceCalculator.MILES){
         this.distanceModeLabel = new JLabel("Miles");
      }
      else if(mode == PaceCalculator.KILOMETERS){
         this.distanceModeLabel = new JLabel("Kilometers");
      }
      else if(mode == PaceCalculator.METERS){
         this.distanceModeLabel = new JLabel("Meters");
      }
      textPanel.add(distanceModeLabel);
      distanceP.add(textPanel);

      return distanceP;
   }

   //****************************************************************
   //
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
      minTextField.setName("Pace Minutes Text Field");
      minTextField.setColumns(MINLEN);
      minPanel.add(minTextField);
      textFieldVector.add(minTextField);
      JLabel minLabel = new JLabel("Minutes");
      minLabel.setOpaque(true);
      minPanel.add(minLabel);
      pacePanel.add(minPanel);

      JPanel secPanel = new JPanel();
      JTextField secTextField = new JTextField();
      secTextField.setName("Pace Seconds Text Field");
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
   //
   //****************************************************************
   private JPanel setUpRunTimePanel(){
      final int HRSLEN = 2;
      final int MINLEN = 2;
      final int SECLEN = 4;

      //Run Tim Panel and additions
      JPanel runtimePanel = new JPanel();
      JLabel runtimeLabel = new JLabel("Run Time:");
      runtimeLabel.setOpaque(true);
      runtimePanel.add(runtimeLabel);

      JPanel hourP = new JPanel();
      hourP.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));

      JTextField hourTF = new JTextField();
      hourTF.setName("Hour Text Field");
      hourTF.setColumns(HRSLEN);
      hourTF.setEditable(false);
      hourP.add(hourTF);
      textFieldVector.add(hourTF);
      JLabel hourLB = new JLabel("Hours");
      hourLB.setOpaque(true);
      hourP.add(hourLB);
      runtimePanel.add(hourP);

      JPanel minP = new JPanel();
      JTextField minTF = new JTextField();
      minTF.setName("Minute Text Field");
      minTF.setColumns(MINLEN);
      minTF.setEditable(false);
      minP.add(minTF);
      textFieldVector.add(minTF);
      JLabel minLabel = new JLabel("Minutes");
      minLabel.setOpaque(true);
      minP.add(minLabel);
      runtimePanel.add(minP);

      JPanel secP = new JPanel();
      JTextField secTF = new JTextField();
      secTF.setName("Second Text Field");
      secTF.setColumns(SECLEN);
      secTF.setEditable(false);
      secP.add(secTF);
      textFieldVector.add(secTF);
      JLabel secLabel = new JLabel("Seconds");
      secLabel.setOpaque(true);
      secP.add(secLabel);
      runtimePanel.add(secP);

      return runtimePanel;
   }

   //****************************************************************
   //
   //****************************************************************
   private JPanel setSouthPanel(Object controller){
      this.buttonGroup  = new ButtonGroup();
      JPanel southPanel = new JPanel();

      southPanel.setBorder(BorderFactory.createRaisedBevelBorder());

      JButton calcRunTime = new JButton("Calculate Run Time");
      calcRunTime.setActionCommand("CRT-Calculate Run Time");
      JButton clear = new JButton("Clear");
      JButton cancel = new JButton("Cancel");
      cancel.setActionCommand("CRT-Cancel");
      JButton save = new JButton("Save");

      calcRunTime.addActionListener((ActionListener)controller);
      calcRunTime.addKeyListener((KeyListener)controller);
      clear.addActionListener((ActionListener)controller);
      clear.addKeyListener((KeyListener)controller);
      cancel.addActionListener((ActionListener)controller);
      cancel.addKeyListener((KeyListener)controller);
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);

      buttonGroup.add(calcRunTime);  buttonGroup.add(clear);
      buttonGroup.add(cancel);       buttonGroup.add(save);

      southPanel.add(calcRunTime);   southPanel.add(clear);
      southPanel.add(cancel);        southPanel.add(save);
      
      this.disableButtons();

      return southPanel;
   }

   //****************************************************************
   //
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
   //
   //****************************************************************
   private JMenu setUpFileMenu(Object controller){
      JMenu file = new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);
      //Clear Menu Item
      JMenuItem clear = new JMenuItem("Clear", 'l');
      //Cancel Menu Item
      JMenuItem cancel = new JMenuItem("Cancel", 'C');
      cancel.setActionCommand("CRT-Cancel");
      //Calculate Run Time Menu Item
      JMenuItem calcRunTime =new JMenuItem("Calculate Run Time",'R');
      calcRunTime.setActionCommand("CRT-Calculate Run Time");
      //Save Menu Item
      JMenuItem save = new JMenuItem("Save", 'S');

      clear.addActionListener((ActionListener)controller);
      cancel.addActionListener((ActionListener)controller);
      calcRunTime.addActionListener((ActionListener)controller);
      save.addActionListener((ActionListener)controller);

      this.menuItemGroup.add(clear);
      this.menuItemGroup.add(cancel);
      this.menuItemGroup.add(calcRunTime);
      this.menuItemGroup.add(save);

      file.add(clear);
      file.add(cancel);
      file.add(calcRunTime);
      file.addSeparator();
      file.add(save);
      
      this.disableMenuItems();

      return file;
   }

   //****************************************************************
   //
   //****************************************************************
   private JMenu setUpHelpMenu(Object controller){
      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic(KeyEvent.VK_H);

      //Help Menu Item
      JMenuItem help = new JMenuItem("Help", 'H');
      help.setActionCommand("CRT-Help");
      help.addActionListener((ActionListener)controller);
      this.menuItemGroup.add(help);
      helpMenu.add(help);

      return helpMenu;
   }

   //****************************************************************
   //
   //****************************************************************
   private void showErrorDialog(String error){
      String exception = new String("Input error!\n");
      String display   = new String("Input error");
      exception += error + "!  ";
      if(error.equals("Divide By 0 Error")){
         exception += "Please enter a value for Distance > 0";
      }
      else if(error.equals("Minutes") || error.equals("Seconds") ||
              error.equals("Distance")){
         exception += error + " not entered properly!\n";
         exception += "Please enter a proper number for the ";
         exception += error + " text field";
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
