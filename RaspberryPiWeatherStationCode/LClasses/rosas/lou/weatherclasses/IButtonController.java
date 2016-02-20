/**
* Copyright (C) 2011 Lou Rosas
* This file is part of many applications registered with
* the GNU General Public License as published
* by the Free Software Foundation; either version 3 of the License,
* or (at your option) any later version.
* PaceCalculator is distributed in the hope that it will be
* useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.
* If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import rosas.lou.weatherclasses.*;

public class IButtonController
implements ActionListener, KeyListener, ItemListener{
   public IButton ibutton         = null; //The Model
   public IButtonView ibuttonview = null; //The View

   //**********************Constructors*****************************
   /**
   Constructor of no arguments (just create the damn Model and View
   here)
   */
   public IButtonController(){
      this.ibutton     = new IButton();
      this.ibuttonview = new IButtonView("", this);
   }
   
   /**
   Constructor taking the argument of the Model
   */
   public IButtonController(IButton ib){
      this.ibutton = ib;
   }
   
   /**
   Constructor taking the arguments of the Model and View
   */
   public IButtonController(IButton ib, IButtonView ibv){
      this.ibutton     = ib;
      this.ibuttonview = ibv;
   }
   
   //*********************Public Methods****************************
   /**
   Implementation of the actionPerformed method from the
   ActionListener Interface
   */
   public void actionPerformed(ActionEvent e){
      if(e.getSource() instanceof JButton){
         JButton b = (JButton)e.getSource();
         if(b.getText().equals("Start New Mission")){
            this.setUpNewMission();
         }
         else if(b.getText().equals("Clear Mission")){
            this.clearMission();
         }
         else if(b.getText().equals("Stop Mission")){
            this.stopMission();
         }
         else if(b.getText().equals("Refresh Mission Data")){
            this.refreshAllMissionData();
         }
         else if(b.getText().equals("Save Dewpoint Data")){
            System.out.println(b.getActionCommand());
         }
         else if(b.getText().equals("Save Heat Index Data")){
            System.out.println(b.getActionCommand());
         }
         else if(b.getText().equals("Save Humidity Data")){
            System.out.println(b.getActionCommand());
         }
         else if(b.getText().equals("Save Mission Data")){
            this.ibuttonview.saveMissionData(this);
         }
         else if(b.getText().equals("Save Temperature Data")){
            System.out.println(b.getActionCommand());
         }
         else if(b.getText().equals("Refresh")){
            if(b.getActionCommand().equals("Temp Refresh")){
               this.ibutton.requestTemperatureData();
            }
            else if(b.getActionCommand().equals("Humi Refresh")){
               this.ibutton.requestHumidityData();
            }
            else if(b.getActionCommand().equals("DP Refresh")){
               this.ibutton.requestDewpointData();
            }
            else if(b.getActionCommand().equals("HI Refresh")){
               this.ibutton.requestHeatIndexData();
            }
            else if(b.getActionCommand().equals("AllData Refresh")){
               //Essentially, should have the same effect as
               //"Refresh Mission Data" on the "Mission" Tab
               this.refreshAllMissionData();
            }
         }
      }
      else if(e.getSource() instanceof JFileChooser){
         JFileChooser jfc = (JFileChooser)e.getSource();
         if(e.getActionCommand().equals("ApproveSelection")){
            if(jfc.getDialogTitle().equals("Save Mission Data")){
               this.ibutton.saveAllData(jfc.getSelectedFile());
            }
         }
      }
   }
   
   /**
   Implementation of the itemStateChanged method from the
   ItemListener Interface
   */
   public void itemStateChanged(ItemEvent e){
      Object o = e.getItem();
      if(o instanceof JRadioButton){
         this.handleJRadioButton((JRadioButton)o);
      }
      else if(o instanceof JCheckBox){
         this.handleJCheckBox((JCheckBox)o);
      }
   }
   
   /**
   Implementation of the keyPressed method from the KeyListener
   Interface
   */
   public void keyPressed(KeyEvent k){
      //Something I discovered the hard way:  only invoke this
      //when Java default look and feel (Metal) is invoked
      //others cause issues!
      String l_and_f = UIManager.getLookAndFeel().getName();
      if(l_and_f.equals("Metal")){
         if(k.getSource() instanceof JButton){
            if(k.getKeyCode() == KeyEvent.VK_ENTER){
               JButton button = (JButton)k.getSource();
               button.doClick();
            }
         }
      }
   }
   
   /**
   Implementation of the keyReleased method from the KeyListener
   Interface
   */
   public void keyReleased(KeyEvent k){}

   /**
   Implementation of the keyTyped method from the KeyListener
   Interface
   */
   public void keyTyped(KeyEvent k){
      if(k.getSource() instanceof JButton){
         if(k.getKeyCode() == KeyEvent.VK_ENTER){
            JButton button = (JButton)k.getSource();
            button.doClick();
         }
      }
   }
   
   /**
   */
   public void setIButton(IButton ib){
      this.ibutton = ib;
   }
   
   /**
   */
   public void setIButtonView(IButtonView ibv){
      this.ibuttonview = ibv;
   }
   
   //********************Private Methods****************************
   /*
   */
   private void clearMission(){
      this.ibutton.clearMemory();
   }
   
   /*
   */
   private int getMissionStartDelay()
   throws NumberFormatException{
      String msd               = null;
      int missionStartDelay = IButton.DEFAULT_DELAY;
      /*try{
         msd = this.ibuttonview.requestMissionStartDelay();
         missionStartDelay = Integer.parseInt(msd);
      }
      catch(NullPointerException npe){
         System.out.println("No View Objects!!");
      }
      catch(NumberFormatException nfe){
         if(!msd.equals("")){
            //Print out the Mission Start Delay Error
            this.ibuttonview.missionStartDelayError();
            throw nfe;
         }
         else{
            //If Empty, set to the Mission Delay to 10 seconds
            //Convert to minutes (so to convert to seconds when
            //setting up the Mission)
            missionStartDelay = IButton.DEFAULT_DELAY;
         }
      }*/
      return missionStartDelay;
   }
   
   /*
   */
   private int getSamplingRate() throws NumberFormatException{
      String sr           = null;
      int samplingRate = IButton.DEFAULT_LOGGING_RATE/60;
      /*try{
         sr = this.ibuttonview.requestSamplingRate();
         samplingRate = Integer.parseInt(sr);
      }
      catch(NullPointerException npe){
         System.out.println("No View Objects!!");
      }
      catch(NumberFormatException nfe){
         if(!sr.equals("")){
            //Print out the Sampling Rate Error
            this.ibuttonview.samplingRateError();
            throw nfe;
         }
         else{
             //If Empty, set to the Default Logging rate (10 min)
             //Convert to minutes (so to convert to seconds when
             //setting up the Mission)
            samplingRate = IButton.DEFAULT_LOGGING_RATE/60;
         }
      }*/
      return samplingRate;
   }
   
   /*
   */
   private double getTemperatureHighAlarm()
   throws NumberFormatException{
      String tha = null;
      double highAlarm = Double.NaN;
      /*try{
         tha = this.ibuttonview.requestTemperatureHighAlarm();
         highAlarm = Double.parseDouble(tha);
      }
      catch(NullPointerException npe){
         System.out.println("No View Objects!!");
      }
      catch(NumberFormatException nfe){
         if(!tha.equals("")){
            this.ibuttonview.highTemperatureAlarmError();
            throw nfe;
         }
         else{
             //If Empty, set to the Default High Temperature Alarm
             //Double.NaN
             highAlarm = Double.NaN;
         }
      }*/
      return highAlarm;
   }
   
   /*
   */
   private double getTemperatureLowAlarm()
   throws NumberFormatException{
      String tla      = null;
      double lowAlarm = Double.NaN;
      /*try{
         tla = this.ibuttonview.requestTemperatureLowAlarm();
         lowAlarm = Double.parseDouble(tla);
      }
      catch(NullPointerException npe){
         System.out.println("No View Objects!!");
      }
      catch(NumberFormatException nfe){
         if(!tla.equals("")){
            //Print out the Temperature Alarm Error
            this.ibuttonview.lowTemperatureAlarmError();
            throw nfe;
         }
         else{
             //If Empty, set to the Default Low Temperature Alarm
             //Double.NaN
             lowAlarm = Double.NaN;
         }
      }*/
      return lowAlarm;
   }
   /*
   */
   private void handleJCheckBox(JCheckBox jcb){
      String command = jcb.getActionCommand();
   }
   
   /*
   */
   private void handleJRadioButton(JRadioButton jrb){
      if(jrb.isSelected()){}
   }
   
   /**
   Need to message the Model to get all update all the possible
   data
   */
   private void refreshAllMissionData(){
      this.ibutton.requestTemperatureData();
      this.ibutton.requestHumidityData();
      this.ibutton.requestDewpointData();
      this.ibutton.requestHeatIndexData();
   }
   
   /*
   */
   private void refreshDewpointData(){
      /*try{
         //Set the view to "Display"
         this.ibuttonview.setToDisplay(true);
      }
      catch(NullPointerException npe){
         //For the time being, do nothing if there is no
         //"Directly Connected" display
      }
      finally{
         //Request both the temperature and humidity data
         this.ibutton.requestMissionData(true, true);
         //Request the dew point data, specifically
         this.ibutton.requestDewpointData();
      }*/
   }
   
   /*
   */
   private void refreshHeatIndexData(){
      /*try{
         //Set the view to "Display"
         this.ibuttonview.setToDisplay(true);
      }
      catch(NullPointerException npe){
         //For the time being, do nothing if there is no
         //"Directly Connected" display
      }
      finally{
         //Request both the temperature and humidity data
         this.ibutton.requestMissionData(true, true);
         //Request the heat index data, specifically
         this.ibutton.requestHeatIndexData();
      }*/
   }
   
   /*
   */
   private void refreshHumidityData(){
      /*try{
         //Set the view to "Display" the data
         this.ibuttonview.setToDisplay(true);
      }
      catch(NullPointerException npe){
         //For the time being, do nothing if there is no
         //"Directly Connected" display
      }
      finally{
         //Request the humidity data only
         this.ibutton.requestMissionData(false, true);
      }*/
   }
   
   /*
   */
   private void refreshTemperatureData(){
      /*try{
         //Set the view to "Display" the data
         this.ibuttonview.setToDisplay(true);
      }
      catch(NullPointerException npe){
         //For the time being, do nothing if there is no
         //"Directly Connected" display
      }
      finally{
         //Request the temperature data only
         this.ibutton.requestMissionData(true, false);
         this.ibutton.requestTemperatureMaxData();
         this.ibutton.requestTemperatureMinData();
      }*/
   }
   
   /*
   */
   private void setUpNewMission(){
      NewMissionData nmd = this.ibuttonview.requestNewMissionData();
      this.ibutton.startMission(nmd);
   }
   
   /*
   */
   private void stopMission(){
      this.ibutton.stopMission();
   }
}