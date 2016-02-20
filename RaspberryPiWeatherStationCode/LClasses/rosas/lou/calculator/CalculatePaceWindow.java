//*******************************************************************
//CalculatePaceWindow
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
The Pace CalculatePaceWindow class by Lou Rosas.  This class
contains all the operations and attributes related to the view of
this Window.
********************************************************************/
public class CalculatePaceWindow extends GenericJInteractionFrame
implements Observer{
   //The appropriate private attributes and types
   private static final short WIDTH  = 420;
   private static final short HEIGHT = 300;
   
   private ButtonGroup buttonGroup, menuItemGroup, radioButtonGroup;
   private Vector textFieldVector;
   private JLabel distanceModeLabel;
   
   //****************************Public Methods**********************
   /*****************************************************************
   Constructor taking the Controller Object:  which is very needed
   for registering the GUIs to the Listeners
   *****************************************************************/
   public CalculatePaceWindow(Object controller){
      super("Calculate Pace");
      textFieldVector = new Vector();
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
   }

   /*****************************************************************
   Return the data stored in the text field reflecting the Run
   Distance.
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
   Return the data stored in the text fields reflecting the
   Run Time.
   *****************************************************************/
   public Stack requestRunTime(){
      String seconds    = new String();
      String minutes    = new String();
      String hours      = new String();
      Stack returnStack = new Stack();
      Enumeration e = textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         if(jtf.getName().equals("Hour Text Field")){
            hours = jtf.getText();
         }
         else if(jtf.getName().equals("Minute Text Field")){
            minutes = jtf.getText();
         }
         else if(jtf.getName().equals("Second Text Field")){
            seconds = jtf.getText();
         }
      }
      returnStack.push(seconds);
      returnStack.push(minutes);
      returnStack.push(hours);
      return returnStack;
   }
   
   /*****************************************************************
   Implementation of the update method as part of the
   implementation of the Observer Interface.
   *****************************************************************/
   public void update(Observable o, Object arg){
      if(o instanceof PaceCalculator){
         if(arg instanceof Integer){
         }
         else if(arg instanceof String){
            String value = (String)arg;
            if(value.equals("CPW-Cancel")){
               //Need to broadcast a WindowClosing Event!!!
               //In the Window World, thie is to "Dispatch an event"
               //instead of just plain "Firing an event" like it does
               //with buttons.  This is one area I wish Java would
               //be more consistent:  NAMING similar functionality
               int event = WindowEvent.WINDOW_CLOSING;
               WindowEvent we = new WindowEvent(this, event);
               this.dispatchEvent(we);
            }
            else if(value.equals("Clear")){
               this.clearTextFields();
               this.disableButtons();
               this.disableMenuItems();
            }
            else if(value.equals("CPW-Help")){
               this.displayHelpPane();
            }
         }
         else if(arg instanceof Stack){
            try{
               if(this.isVisible()){
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
                        if(data.substring(0, 5).equals("Hours")){
                           String focus = data.substring(0, 5);
                           this.focusTextField(focus);
                        }
                        else if
                        (
                           data.substring(0, 7).equals("Minutes") ||
                           data.substring(0, 7).equals("Seconds")
                        ){
                           String focus = data.substring(0, 8);
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
   //*************************Private Methods************************
   //****************************************************************
   //Clear out all the text fields
   //****************************************************************
   private void clearTextFields(){
      Enumeration e = textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         jtf.setText("");  //Clear out the text in the Text Field
         if((jtf.getName().equals("Hour Text Field"))){
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
   private void displayHelpPane(){
      JFrame helpFrame =
                 new GenericJInteractionFrame("Calculate Pace Help");
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      JEditorPane ep = null;
      try{
         ep = new JEditorPane(
          "file:///LClasses/rosas/lou/docs/calculatePaceWindowHelp.html");
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      ep.setEditable(false);
      JScrollPane hsp = new JScrollPane(ep);

      helpFrame.setSize(400,400);
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
   //Request focus on the appropriate Text Field in the GUI
   //****************************************************************
   private void focusTextField(String message){
      Enumeration e = this.textFieldVector.elements();
      while(e.hasMoreElements()){
         JTextField jtf = (JTextField)e.nextElement();
         String name = jtf.getName();
         if(message.equals("Hours") &&
            name.equals("Hour Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
         else if(message.equals("Minutes") &&
            name.equals("Minute Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
         else if(message.equals("Seconds") &&
            name.equals("Second Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
         else if(message.equals("Distance") &&
            name.equals("Distance Text Field")){
            jtf.requestFocus();
            jtf.selectAll();
         }
      }
   }
   
   //****************************************************************
   //
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
      //If the Window is not visible, this should be performed
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
   //Set up the GUI.
   //****************************************************************
   private void setUpGUI(Object controller){
      //Get the Content Pane of the GUI
      Container contentPane = this.getContentPane();
      //Get the Screen dimensions
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //Set the Size
      this.setSize(WIDTH, HEIGHT);
      this.setLocation(
         (int)(dim.getWidth()/2  - WIDTH/2),
         (int)(dim.getHeight()/2 - HEIGHT/2));
      //Set up the Appropriate Panels in the GUI
      JPanel northPanel  = this.setNorthPanel(controller);
      JPanel centerPanel = this.setCenterPanel(controller);
      JPanel southPanel  = this.setSouthPanel(controller);
      contentPane.add(northPanel,  BorderLayout.NORTH);
      contentPane.add(centerPanel, BorderLayout.CENTER);
      contentPane.add(southPanel,  BorderLayout.SOUTH);
      //Set up the menu bar
      this.setJMenuBar(this.setUpMenuBar(controller));
   }
   
   //****************************************************************
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
      JPanel rbPanel    = new JPanel();
      
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JLabel modeLabel = 
                new JLabel("Distance Mode: ", SwingConstants.CENTER);
      
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
   //****************************************************************
   private JPanel setCenterPanel(Object controller){
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new GridBagLayout());
      
      JPanel rtPanel = this.setUpRunTimePanel();
      GridBagConstraints rtConstraints = new GridBagConstraints();
      rtConstraints.gridx   = 0;
      rtConstraints.gridy   = 0;
      rtConstraints.fill    = GridBagConstraints.VERTICAL;
      rtConstraints.anchor  = GridBagConstraints.NORTHWEST;
      rtConstraints.weightx = 0;
      rtConstraints.weighty = 0;
      centerPanel.add(rtPanel, rtConstraints);
      
      JPanel dtPanel = this.setUpDistancePanel(controller);
      GridBagConstraints dtConstraints = new GridBagConstraints();
      dtConstraints.gridx   = 0;
      dtConstraints.gridy   = 1;
      dtConstraints.fill    = GridBagConstraints.VERTICAL;
      dtConstraints.anchor  = GridBagConstraints.NORTHWEST;
      dtConstraints.weightx = 0;
      dtConstraints.weighty = 0;
      centerPanel.add(dtPanel, dtConstraints);
      
      JPanel pcPanel = this.setUpPacePanel();
      GridBagConstraints pcConstraints = new GridBagConstraints();
      pcConstraints.gridx   = 0;
      pcConstraints.gridy   = 2;
      pcConstraints.fill    = GridBagConstraints.VERTICAL;
      pcConstraints.anchor  = GridBagConstraints.NORTHWEST;
      pcConstraints.weightx = 0;
      pcConstraints.weighty = 0;
      centerPanel.add(pcPanel, pcConstraints);
      
      return centerPanel;
   }
   
   //****************************************************************
   //****************************************************************
   private JPanel setUpDistancePanel(Object controller){
      final int DISLENGTH = 10;
      Object o = controller;
      PaceCalculatorController pcc = (PaceCalculatorController)o;
      PaceCalculator pc = (PaceCalculator)(pcc.requestModel());
      int mode = pc.getDistanceUnits();
      
      //Distance Panel and additions
      JPanel disPanel = new JPanel();
      JLabel disLabel = new JLabel("Distance:");
      disLabel.setOpaque(true);
      disPanel.add(disLabel);
      
      JPanel enterPanel = new JPanel();
      enterPanel.setBorder(BorderFactory.createEmptyBorder(0,7,0,0));
      JTextField enterTextField = new JTextField();
      enterTextField.setName("Distance Text Field");
      enterTextField.setColumns(DISLENGTH);
      textFieldVector.add(enterTextField);
      enterPanel.add(enterTextField);
      if(mode == PaceCalculator.MILES){
         this.distanceModeLabel = new JLabel("Miles");
      }
      else if(mode == PaceCalculator.KILOMETERS){
         this.distanceModeLabel = new JLabel("Kilometers");
      }
      else if(mode == PaceCalculator.METERS){
         this.distanceModeLabel = new JLabel("Meters");
      }
      enterPanel.add(distanceModeLabel);
      disPanel.add(enterPanel);
      
      return disPanel;
   }
   
   //****************************************************************
   //****************************************************************
   private JPanel setUpPacePanel(){
      final int MINLENGTH = 2;
      final int SECLENGTH = 4;
      
      //Pace Panel and additions
      JPanel pacePanel = new JPanel();
      JLabel paceLabel = new JLabel("Pace:");
      paceLabel.setOpaque(true);
      pacePanel.add(paceLabel);
      
      JPanel minPanel = new JPanel();
      minPanel.setBorder(BorderFactory.createEmptyBorder(0,29,0,0));
      JTextField minTextField = new JTextField();
      minTextField.setName("Pace Minutes Text Field");
      minTextField.setColumns(MINLENGTH);
      minTextField.setEditable(false);
      minPanel.add(minTextField);
      textFieldVector.add(minTextField);
      JLabel minLabel = new JLabel("Minutes");
      minLabel.setOpaque(true);
      minPanel.add(minLabel);
      pacePanel.add(minPanel);
      
      JPanel secPanel = new JPanel();
      //secPanel.setBorder(BorderFactory.createEmptyBorder(0,1,0,0));
      JTextField secTextField = new JTextField();
      secTextField.setName("Pace Seconds Text Field");
      secTextField.setColumns(SECLENGTH);
      secTextField.setEditable(false);
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
      final int HRLENGTH  = 4;
      final int MINLENGTH = 2;
      final int SECLENGTH = 4;
      
      //Run Time Panel and additions
      JPanel rtPanel = new JPanel();
      JLabel rtLabel = new JLabel("Run Time:");
      rtLabel.setOpaque(true);
      rtPanel.add(rtLabel);
      
      JPanel hrPanel = new JPanel();
      hrPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
      JTextField hrTextField = new JTextField();
      hrTextField.setName("Hour Text Field");
      hrTextField.setColumns(HRLENGTH);
      hrPanel.add(hrTextField);
      textFieldVector.add(hrTextField);
      JLabel hrLabel = new JLabel("Hours");
      hrLabel.setOpaque(true);
      hrPanel.add(hrLabel);
      rtPanel.add(hrPanel);
      
      JPanel minPanel = new JPanel();
      //minPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
      JTextField minTextField = new JTextField();
      minTextField.setName("Minute Text Field");
      minTextField.setColumns(MINLENGTH);
      minPanel.add(minTextField);
      textFieldVector.add(minTextField);
      JLabel minLabel = new JLabel("Minutes");
      minLabel.setOpaque(true);
      minPanel.add(minLabel);
      rtPanel.add(minPanel);
      
      JPanel secPanel = new JPanel();
      //secPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
      JTextField secTextField = new JTextField();
      secTextField.setName("Second Text Field");
      secTextField.setColumns(SECLENGTH);
      secPanel.add(secTextField);
      textFieldVector.add(secTextField);
      JLabel secLabel = new JLabel("Seconds");
      secLabel.setOpaque(true);
      secPanel.add(secLabel);
      rtPanel.add(secPanel);
      
      return rtPanel;
   }
   
   //****************************************************************
   //****************************************************************
   private JPanel setSouthPanel(Object controller){
      JPanel southPanel = new JPanel();
      this.buttonGroup = new ButtonGroup();
      
      southPanel.setBorder(BorderFactory.createRaisedBevelBorder());
      
      JButton calculatePace = new JButton("Calculate Pace");
      calculatePace.setActionCommand("CPW-Calculate Pace");
      JButton clear = new JButton("Clear");
      clear.setActionCommand("Clear");
      JButton cancel = new JButton("Cancel");
      cancel.setActionCommand("CPW-Cancel");
      JButton save = new JButton("Save");
      
      calculatePace.addActionListener((ActionListener)controller);
      calculatePace.addKeyListener((KeyListener)controller);
      clear.addActionListener((ActionListener)controller);
      clear.addKeyListener((KeyListener)controller);
      cancel.addActionListener((ActionListener)controller);
      cancel.addKeyListener((KeyListener)controller);
      save.addActionListener((ActionListener)controller);
      save.addKeyListener((KeyListener)controller);
      
      buttonGroup.add(calculatePace);  buttonGroup.add(clear);
      buttonGroup.add(cancel);         buttonGroup.add(save);
      
      southPanel.add(calculatePace);   southPanel.add(clear);
      southPanel.add(cancel);          southPanel.add(save);
      
      this.disableButtons();
      
      return southPanel;
   }
   
   //****************************************************************
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
   //****************************************************************
   private JMenu setUpFileMenu(Object controller){
      JMenu file = new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);
      //Clear Menu Item
      JMenuItem clear = new JMenuItem("Clear", 'l');
      //Cancel Menu Item
      JMenuItem cancel = new JMenuItem("Cancel", 'C');
      cancel.setActionCommand("CPW-Cancel");
      //Calculate Pace Menu Item
      JMenuItem calculatePace = new JMenuItem("Calculate Pace", 'P');
      calculatePace.setActionCommand("CPW-Calculate Pace");
      //Save Menu Item
      JMenuItem save = new JMenuItem("Save", 'S');
      
      clear.addActionListener((ActionListener)controller);
      cancel.addActionListener((ActionListener)controller);
      calculatePace.addActionListener((ActionListener)controller);
      save.addActionListener((ActionListener)controller);
      
      this.menuItemGroup.add(clear);
      this.menuItemGroup.add(cancel);
      this.menuItemGroup.add(calculatePace);
      this.menuItemGroup.add(save);
      
      file.add(clear);
      file.add(cancel);
      file.add(calculatePace);
      file.addSeparator(); //Add the separator     
      file.add(save);
      
      this.disableMenuItems();
      
      return file;
   }
   
   //****************************************************************
   //****************************************************************
   private JMenu setUpHelpMenu(Object controller){
      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic(KeyEvent.VK_H);
      
      //Help Menu Item
      JMenuItem help = new JMenuItem("Help", 'H');
      help.setActionCommand("CPW-Help");
      help.addActionListener((ActionListener)controller);
      this.menuItemGroup.add(help);
      helpMenu.add(help);

      return helpMenu;
   }

   //****************************************************************
   //If an execption occured related to Calculating the pace, this
   //method should be messaged.  Indicate what the error was by
   //Displaying the appropriate Error Message.
   //****************************************************************
   private void showErrorDialog(String error){
      System.out.println(error);
      String exception = new String("Input error!\n");
      String display   = new String("Input error");
      if(error.equals("Divide By 0 Error")){
         exception += error + "!  ";
         exception += "Please enter a value for Distance > 0";
      }
      else if(error.equals("Hours")   || error.equals("Minutes") ||
              error.equals("Seconds") || error.equals("Distance")){
         exception += error + " not entered properly!\n";
         exception += "Please enter a proper number for the ";
         exception += error + " text field";
      }
      else if(error.substring(0,8).equals("Distance")){
         exception += error + " (and not zero)";
      }
      else if(error.substring(0, 5).equals("Hours")   ||
              error.substring(0, 7).equals("Minutes") ||
              error.substring(0, 7).equals("Seconds")){
         exception += error;
      }
      JOptionPane.showMessageDialog(this, exception, display,
                                    JOptionPane.ERROR_MESSAGE);      
   }
   
   //****************************************************************
   //
   //****************************************************************
   private void showSaveErrorDialog(String data){
      String exception = new String("Save Error\n");
      String display   = new String("Save to File Error");
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
