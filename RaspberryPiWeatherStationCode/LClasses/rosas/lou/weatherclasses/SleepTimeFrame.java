//////////////////////////////////////////////////////////////////////
/*
Copyright 2022 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
//////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.text.ParseException;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.*;

public class SleepTimeFrame extends GenericJInteractionFrame{
   private static SleepTimeFrame _instance      = null;
   private static int WIDTH                     = 300;
   private static int HEIGHT                    = 150;

   private CurrentWeatherController _controller = null;
   private JTextField               _hrs        = null;
   private JTextField               _mins       = null;
   private JTextField               _secs       = null;
   private int                      _hours      = -1;
   private int                      _minutes    = -1;
   private int                      _seconds    = -1;

   ///////////////////////Public Methods//////////////////////////////
   /**/
   public static SleepTimeFrame instance(CurrentWeatherController c){
      if(_instance == null){
         _instance = new SleepTimeFrame(c);
      }
      if(!_instance.isVisible()){
         _instance.setVisible(true);
      }
      return _instance;
   }
   
   /**/
   public void cancel(){
      this.clear();
      this.setVisible(false);
   }

   /**/
   public void clear(){
      this._hrs.setText("");
      this._mins.setText("");
      this._secs.setText("");
      this._hours   = 0;
      this._minutes = 0;
      this._seconds = 0;
      this._hrs.requestFocus();
   }

   /**/
   public void nextTextField(String name){
      if(name.equals("HOURS")){
         this._mins.requestFocus();
      }
      else if(name.equals("MINUTES")){
         this._secs.requestFocus();
      }
   }

   /**/
   public int[] requestTimes(){
      int[] times = new int[3];
      try{
         this._hours = Integer.parseInt(this._hrs.getText());
      }
      catch(NumberFormatException npe){
         this._hours = 0;
      }
      try{
         this._minutes = Integer.parseInt(this._mins.getText());
      }
      catch(NumberFormatException npe){
         this._minutes = 0;
      }
      try{
         this._seconds = Integer.parseInt(this._secs.getText());
      }
      catch(NumberFormatException npe){
         this._seconds = 0;
      }
      times[0] = this._hours;
      times[1] = this._minutes;
      times[2] = this._seconds;
      this.setVisible(false);
      return times;
   }
   
   //////////////////////Protected Methods////////////////////////////
   /**/
   protected SleepTimeFrame(CurrentWeatherController cwc){
      super("Sleep Time");
      this._controller = cwc;
      this.setLayout(new BorderLayout());
      JPanel panel = this.setNorthPanel();;
      //this.getContentPane().add(panel,BorderLayout.NORTH);
      panel = this.setCenterPanel();
      this.getContentPane().add(panel,BorderLayout.CENTER);
      //Add The Buttons
      panel = this.setSouthPanel();
      this.getContentPane().add(panel, BorderLayout.SOUTH);
      this.setSize(WIDTH, HEIGHT);
      this.setResizable(false);
   }

   /////////////////////Private Methods///////////////////////////////
   /**/
   private JPanel setCenterPanel(){
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(3,2));
      panel.add(new JLabel("Hours: ", SwingConstants.RIGHT));
      this._hrs = new JTextField(3);
      this._hrs.setName("Hours");
      this._hrs.addActionListener(this._controller);
      this._hrs.addKeyListener(this._controller);
      panel.add(this._hrs);

      panel.add(new JLabel("Minutes: ", SwingConstants.RIGHT));
      this._mins = new JTextField(3);
      this._mins.setName("Minutes");
      this._mins.addActionListener(this._controller);
      this._mins.addKeyListener(this._controller);
      panel.add(this._mins);

      panel.add(new JLabel("Seconds: ", SwingConstants.RIGHT));
      this._secs = new JTextField(3);
      this._secs.setName("Seconds");
      this._secs.addActionListener(this._controller);
      this._secs.addKeyListener(this._controller);
      panel.add(this._secs);

      this._hrs.requestFocus();
      return panel;
   }

   /**/
   private JPanel setNorthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());
      JLabel label = new JLabel("Hours: " + this._hours);
      panel.add(label);
      label = new JLabel("Minutes: " + this._minutes);
      panel.add(label);
      label = new JLabel("Seconds: " + this._seconds);
      panel.add(label);
      return panel;
   }

   /**/
   private JPanel setSouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton set = new JButton("Set");
      set.setActionCommand("SleeptimeSet");
      set.addActionListener(this._controller);
      set.addKeyListener(this._controller);
      panel.add(set);

      JButton clear = new JButton("Clear");
      clear.setActionCommand("SleeptimeClear");
      clear.addActionListener(this._controller);
      clear.addKeyListener(this._controller);
      panel.add(clear);

      JButton cancel = new JButton("Cancel");
      cancel.setActionCommand("SleeptimeCancel");
      cancel.addActionListener(this._controller);
      cancel.addKeyListener(this._controller);
      /*
      cancel.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setVisible(false);
         }
      });
      */
      panel.add(cancel);

      return panel;
   }
}
