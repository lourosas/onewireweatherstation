/********************************************************************
The controller for the SerialPortLister
********************************************************************/
package rosas.lou.weatherclasses;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rosas.lou.weatherclasses.*;

//
//
//
public class SerialPortListerController implements ActionListener{
   private Object view;
   private Object model;
   private SerialPortChooser spc;
   
   //******************Public Methods********************************
   //
   //Constructor of no arguments
   //
   public SerialPortListerController(){
      spc = new SerialPortChooser(this);
   }
   
   //
   //Constructor taking the view and the controller
   //
   public SerialPortListerController(Object theView, Object theModel){
      this.setView(theView);
      this.setModel(theModel);
      spc = new SerialPortChooser(this);
   }
   
   //
   //Implementation of the actionPerformed method from the
   //ActionListener Interface
   //
   public void actionPerformed(ActionEvent e){
      Object o = e.getSource();
      
      if(o instanceof JButton){
         this.handleButtonItem((JButton)o);
      }
   }
   
   //
   //Get the model
   //
   public Object getModel(){
      return this.model;
   }
   
   //
   //Get the view
   //
   public Object getView(){
      return this.view;
   }
   
   //
   //Set the model
   //
   public void setModel(Object theModel){
      this.model = theModel;
   }
   
   //
   //Set the View
   //
   public void setView(Object theView){
      this.view = theView;
   }
   
   //******************Private Methods*******************************
   //
   //Handle the Button Action Events
   //
   private void handleButtonItem(JButton button){
      String command = button.getActionCommand();
      SerialPortLister spl = (SerialPortLister)this.model;
      if(command.equals("Quit")){
         spl.quit();
      }
      else if(command.equals("Rescan")){
         String portName = new String();
         portName = this.spc.requestPort();
         spl.requestAdapterInfo(portName);
      }
      else if(command.equals("Show Ports")){
         this.spc.setVisible(true);
      }
      else if(command.equals("SPC_CANCEL")){
         this.spc.setVisible(false);
      }
      else if(command.equals("SPC_OK")){
         String portName = new String();
         //Now get the port name (Or Something like that)
         //Get the current port that is chosen to list 
         //the adapter and anything one-wire devices (hopefully)
         portName = this.spc.requestPort();
         spl.requestAdapterInfo(portName);
         this.spc.setVisible(false);
      }
   }
}