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

public class SuperUserAccountDialog{
   private static Hashtable<String, char[]> accounts=new Hashtable();

   private JTextField     _superUser = null;
   private JPasswordField _password  = null;
   private boolean        _toDisplay = false;
   private String name = "Administrator";
   private String pwd  = "Password";
   
   {
      accounts.put(name, pwd.toCharArray());
   };

   //////////////////////////Constructor//////////////////////////////
   /**/
   public SuperUserAccountDialog(){
      if(this.setUpDialogBox() == 0){
         checkPassword();
      }
   }
   ////////////////////////Public Methods/////////////////////////////
   ////////////////////////Private Methods////////////////////////////
   /**/
   private boolean checkPassword(){
      System.out.println(_superUser.getText());
      System.out.println(_password.getPassword());
      System.out.println(accounts);
      return false;
   }

   /**/
   private int setUpDialogBox(){
      JPanel panel         = new JPanel();
      panel.setLayout(new GridLayout(2,2));
      panel.add(new JLabel("Super User: ", SwingConstants.RIGHT));
      this._superUser = new JTextField(20);
      panel.add(this._superUser);
      panel.add(new JLabel("Password: ", SwingConstants.RIGHT));
      this._password = new JPasswordField(20);
      panel.add(this._password);
      return JOptionPane.showConfirmDialog(null,
                                        panel,
                                        "Password",
                                        JOptionPane.OK_CANCEL_OPTION);
   }
}

