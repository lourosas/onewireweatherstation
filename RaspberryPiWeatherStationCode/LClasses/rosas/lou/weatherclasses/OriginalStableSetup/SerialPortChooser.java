/********************************************************************
The Serial Port View Class
********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import gnu.io.*;
import myclasses.*;
import rosas.lou.weatherclasses.*;

public class SerialPortChooser extends GenericJInteractionFrame
implements Observer{
   private static final short WIDTH  = 300;
   private static final short HEIGHT = 200;

   private ButtonGroup buttonGroup, radioButtonGroup;
   //private JPanel radioPanel;

   //*************************Public Methods*************************
   //
   //The Constructor, passing in the Controller
   //
   public SerialPortChooser(Object controller){
      super("Serial Port Chooser"); 
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
   }
   
   //
   //Get the port name requested by the user
   //
   public String requestPort(){
      Enumeration be = this.radioButtonGroup.getElements();
      String portName = new String();
      while(be.hasMoreElements()){
         Object o = be.nextElement();
         if(o instanceof JRadioButton){
            if(((JRadioButton)o).isSelected())
               portName = ((JRadioButton)o).getText();
         }
      }
      return portName;
   }

   //
   //The update() method as part of implementing the Observer
   //interface.  This is probably not needed.
   //
   public void update(Observable o, Object arg){
   }

   //**************************Private Methods***********************
   //
   //Find all the notable ports and display them
   //
   private void findPorts(JPanel radioPanel){
      this.radioButtonGroup = new ButtonGroup();
      String portName   = new String();
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      while(ports.hasMoreElements()){
         CommPortIdentifier cp =
                          (CommPortIdentifier)ports.nextElement();
         if(cp.getPortType() == CommPortIdentifier.PORT_SERIAL){
            portName = cp.getName();
            if(cp.getCurrentOwner() != null){
               portName += " **BUSY**";
            }
            JRadioButton jrb = new JRadioButton(portName);
            if(this.radioButtonGroup.getButtonCount() == 0){
               jrb.setSelected(true);
            }
            radioPanel.add(jrb);
            this.radioButtonGroup.add(jrb);
         }
      }
   }
   
   //
   //Set up the GUI for the entire view
   //
   private void setUpGUI(Object controller){
      this.setSize(WIDTH, HEIGHT);

      Container contentPane = this.getContentPane();
      
      this.setSize(WIDTH, HEIGHT);

      //JPanel centerPanel = this.setCenterPanel();
      //this.radioPanel    = this.setCenterPanel();
      this.setCenterPanel(contentPane);
      JPanel southPanel  = this.setSouthPanel(controller);

      contentPane.add(southPanel, BorderLayout.SOUTH);
   }

   //
   //Set up the Center Panel (real simple for now)
   //
   private void setCenterPanel(Container container){
      JPanel radioPanel = new JPanel(new GridLayout(0, 1));
      radioPanel.setBorder(
                       BorderFactory.createEmptyBorder(20,20,20,20));
      //Find all the ports that are currently active
      this.findPorts(radioPanel);
      container.add(radioPanel, BorderLayout.CENTER);
      
   }

   //
   //Set up the South Panel
   //
   private JPanel setSouthPanel(Object controller){
      JPanel buttonPanel = new JPanel();
      this.buttonGroup = new ButtonGroup();
      JButton okButton     = new JButton("OK");
      JButton cancelButton = new JButton("Cancel");
      okButton.setActionCommand("SPC_OK");
      cancelButton.setActionCommand("SPC_CANCEL");
      this.buttonGroup.add(okButton); buttonGroup.add(cancelButton);
      okButton.addActionListener((ActionListener)controller);
      cancelButton.addActionListener((ActionListener)controller);
      buttonPanel.add(okButton); buttonPanel.add(cancelButton);
      return buttonPanel;
   }
}
