/********************************************************************
This is the view for listing the available serial ports.
********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.Thread;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import myclasses.*;
import rosas.lou.weatherclasses.*;

public class SerialPortListerView extends GenericJFrame
implements Observer{
   //Appropriate Private Attributes
   private static final short WIDTH  = 400;
   private static final short HEIGHT = 500;
    
   private ButtonGroup buttonGroup;
   private JTextArea responseText;
    
   //********************Public Methods*****************************
   //***************************************************************
   //Constructor passing in the Controller argument
   // (needed for interaction)
   //***************************************************************
   public SerialPortListerView(Object controller){
      super("Serial Port Lister");
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
      //new SerialPortChooser(controller);
   }
    
   //
   //Implementation of the update method as part of the Observer
   //Interface Implementation
   //
   public void update(Observable o, Object arg){
      if(arg instanceof String){
         this.responseText.append((String)arg);
      }
   }
    
   //********************Private Methods****************************
   //
   //Set up the GUI--for the entire View
   //
   private void setUpGUI(Object controller){
      //Get the Content Pane (ALWAYS, STEP 1 in a GUI set up)
      Container contentPane = this.getContentPane();
      //Get the screen dimensions to set the panel
      //at the center of the screen
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.setSize(new Dimension(WIDTH, HEIGHT));
      this.setLocation((int)(dim.getWidth()/2  - WIDTH/2),
                       (int)(dim.getHeight()/2 - HEIGHT/2));
      //Set up the appropriate Panels
      JPanel centerPanel = this.setCenterPanel();
      JPanel southPanel  = this.setSouthPanel(controller);
      
      contentPane.add(centerPanel, BorderLayout.CENTER);
      contentPane.add(southPanel,  BorderLayout.SOUTH);
   }
  
   //
   //Set up the Center Panel
   //
   private JPanel setCenterPanel(){
      int textRows = 25;
      int textCols = 40;
      JPanel centerPanel = new JPanel();
      this.responseText = new JTextArea(textRows, textCols);
      
      responseText.setLineWrap(true);
      responseText.setWrapStyleWord(true);
      responseText.setFont(new Font("Monospaced", Font.BOLD, 14));
      responseText.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(responseText);
      //centerPanel.add(responseText);
      centerPanel.add(scrollPane);
      
      return centerPanel;
   }
   
   //
   //Set up the South Panel (button panel)
   //
   private JPanel setSouthPanel(Object controller){
      this.buttonGroup = new ButtonGroup();
      JPanel southPanel = new JPanel();
      
      JButton quitButton      = new JButton("Quit");
      JButton rescanButton = new JButton("Rescan");
      JButton ports             = new JButton("Show Ports");
      
      
      this.buttonGroup.add(quitButton);
      this.buttonGroup.add(rescanButton);
      this.buttonGroup.add(ports);
      
      rescanButton.addActionListener((ActionListener)controller);
      quitButton.addActionListener((ActionListener)controller);
      ports.addActionListener((ActionListener)controller);
      southPanel.add(rescanButton);
      southPanel.add(quitButton);
      southPanel.add(ports);
      
      return southPanel;
   }
}
