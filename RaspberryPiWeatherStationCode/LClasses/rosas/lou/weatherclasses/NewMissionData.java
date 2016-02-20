////////////////////////////////////////////////////////////////////
//Copyright (C) 2011 Lou Rosas
//This file is part of many applications registered with
//the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
////////////////////////////////////////////////////////////////////

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import rosas.lou.weatherclasses.*;

public class NewMissionData{
   private String  sampleRate;
   private String  startDelay;
   private boolean rolloverEnabled;
   private boolean synchClock;
   private boolean enableTemperatureChannel;
   private boolean enableHumidityChannel;
   private String  units;
   private String  temperatureLowAlarm;
   private String  temperatureHighAlarm;
   private String  humidityLowAlarm;
   private String  humidityHighAlarm;
   
   //Initializer
   {
      this.setSampleRate("");
      this.setStartDelay("");
      this.setRolloverEnabled(false);
      this.setSynchClock(false);
      this.setEnableTemperatureChannel(true);
      this.setEnableHumidityChannel(true);
      this.setUnits("Celsius");
      this.setTemperatureLowAlarm("");
      this.setTemperatureHighAlarm("");
      this.setHumidityLowAlarm("");
      this.setHumidityHighAlarm("");
   }
   
   ///////////////////////Constructors//////////////////////////////
   //
   //Constructor of no arguments
   //Set the "Default" of all the data
   //
   public NewMissionData(){}
   
   ///////////////////Public Methods////////////////////////////////
   public String getSampleRate(){ return this.sampleRate; }
   
   public String getStartDelay(){ return this.startDelay; }
   
   public boolean getRolloverEnabled(){
      return this.rolloverEnabled; 
   }
   
   public boolean getSynchClock(){ return this.synchClock; }
   
   public boolean getEnableTemperatureChannel(){
      return this.enableTemperatureChannel;
   }
   
   public boolean getEnableHumidityChannel(){
      return this.enableHumidityChannel;
   }

   public String getUnits(){
      return this.units;
   }
   
   public String getTemperatureLowAlarm(){
      return this.temperatureLowAlarm;
   }
   
   public String getTemperatureHighAlarm(){
      return this.temperatureHighAlarm;
   }
   
   public String getHumidityLowAlarm(){
      return this.humidityLowAlarm;
   }
   public String getHumidityHighAlarm(){
      return this.humidityHighAlarm;
   }

   public void setSampleRate(String rate){
      this.sampleRate = new String(rate);
   }
   
   public void setStartDelay(String delay){
      this.startDelay = new String(delay);
   }
   
   public void setRolloverEnabled(boolean enabled){
      this.rolloverEnabled = enabled;
   }
   
   public void setSynchClock(boolean synch){
      this.synchClock = synch;
   }
   
   public void setEnableTemperatureChannel(boolean enable){
      this.enableTemperatureChannel = enable;
   }
   
   public void setEnableHumidityChannel(boolean enable){
      this.enableHumidityChannel = enable;
   }

   public void setUnits(String units){
      this.units = new String(units);
   }
   
   public void setTemperatureLowAlarm(String temp){
      this.temperatureLowAlarm = new String(temp);
   }
   
   public void setTemperatureHighAlarm(String temp){
      this.temperatureHighAlarm = new String(temp);
   }
   
   public void setHumidityLowAlarm(String humidity){
      this.humidityLowAlarm = new String(humidity);
   }
   
   public void setHumidityHighAlarm(String humidity){
      this.humidityHighAlarm = new String(humidity);
   }
   
   /**/
   public String toString(){
      String rs = new String(this.sampleRate + "\n");
      rs = rs.concat(this.startDelay + "\n");
      rs = rs.concat(this.rolloverEnabled + "\n");
      rs = rs.concat(this.synchClock + "\n");
      rs = rs.concat(this.enableTemperatureChannel + "\n");
      rs = rs.concat(this.enableHumidityChannel + "\n");
      rs = rs.concat(this.units + "\n");
      rs = rs.concat(this.temperatureLowAlarm + "\n");
      rs = rs.concat(this.temperatureHighAlarm + "\n");
      rs = rs.concat(this.humidityLowAlarm + "\n");
      rs = rs.concat(this.humidityHighAlarm + "\n");
      return rs;
   }
}
