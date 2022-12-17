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
   private static int HEIGHT                    = 300;

   private CurrentWeatherController _controller = null;
   private JTextField               _hrs        = null;
   private JTextField               _mins       = null;
   private JTextField               _secs       = null;

   ///////////////////////Public Methods//////////////////////////////
   /**/
   public static SleepTimeFrame instance(){
      int confirm = superUserConfirm();
      if(confirm == 0){
         if(_instance == null){
            _instance = new SleepTimeFrame();
         }
         return _instance;
      }
      else{
         return null;
      }
   }
   
   //////////////////////Protected Methods////////////////////////////
   /**/
   protected SleepTimeFrame(){
      super("Sleep Time");
      this.setLayout(new BorderLayout());
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(3,2));
      panel.add(new JLabel("Hours: ", SwingConstants.RIGHT));
      this._hrs = new JTextField(3);
      panel.add(this._hrs);
      panel.add(new JLabel("Minutes: ", SwingConstants.RIGHT));
      this._mins = new JTextField(3);
      panel.add(this._mins);
      panel.add(new JLabel("Seconds: ", SwingConstants.RIGHT));
      this._secs = new JTextField(3);
      panel.add(this._secs);
      this.getContentPane().add(panel, BorderLayout.CENTER);
      //Add The Buttons
      this.setSize(WIDTH, HEIGHT);
      //this.setResizable(false);
   }

   /////////////////////Private Methods///////////////////////////////
   /**/
   private static int superUserConfirm(){
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(2, 2));
      panel.add(new JLabel("Super User: ",SwingConstants.RIGHT));
      panel.add(new JTextField(20));
      panel.add(new JLabel("Password: ",  SwingConstants.RIGHT));
      panel.add(new JPasswordField(20));
      int n = JOptionPane.showConfirmDialog(null,
                                        panel,
                                        "Password",
                                        JOptionPane.OK_CANCEL_OPTION);
      return n;
   }
}
