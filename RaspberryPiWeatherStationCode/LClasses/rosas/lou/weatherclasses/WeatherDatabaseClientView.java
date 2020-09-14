/*
Copyright 2020 Lou Rosas

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
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.TestPanel2;

//////////////////////////////////////////////////////////////////////
/*
*/
//////////////////////////////////////////////////////////////////////
public class WeatherDatabaseClientView extends GenericJFrame{
   private static final short WIDTH        = 750;
   private static final short HEIGHT       = 500;
   private static final short TOTAL_PANELS = 5;

   ///////////////////////////Public Methods//////////////////////////
   /////////////////////////////Constructors//////////////////////////
   /**/
   public WeatherDatabaseClientView(){
      this("");
   }

   /**/
   public WeatherDatabaseClientView(String title){
      super(title);
      //Add Model
      //Add Controllers
      this.setUpGUI();
   }

   //////////////////////////Private Methods//////////////////////////
   /**/
   private JPanel setUpDewPointPanel(){
      JPanel dewPointPanel = new JPanel();
      dewPointPanel.setLayout(new BorderLayout());
      JLabel dpLabel = new JLabel("Dew Point");
      dewPointPanel.add(dpLabel, BorderLayout.CENTER);
      return dewPointPanel;
   }

   /**/
   private JPanel setUpHeatIndexPanel(){
      JPanel heatIndexPanel = new JPanel();
      heatIndexPanel.setLayout(new BorderLayout());
      JLabel hiLabel = new JLabel("Heat Index");
      heatIndexPanel.add(hiLabel, BorderLayout.CENTER);
      return heatIndexPanel;
   }

   /**/
   private JPanel setUpHumidityPanel(){
      JPanel humidityPanel = new JPanel();
      humidityPanel.setLayout(new BorderLayout());
      JLabel humiLabel = new JLabel("Humidity");
      humidityPanel.add(humiLabel, BorderLayout.CENTER);
      return humidityPanel;
      
   }
   /**/
   private void setUpGUI(){
      this.setLayout(new BorderLayout());
      this.setSize(WIDTH,HEIGHT);
      this.setResizable(false);
      JTabbedPane jtp = new JTabbedPane();
      jtp.addTab("Temperature",
                 null,
                 this.setUpTemperaturePanel(),
                 "Viewing Temperature Data");
      jtp.setMnemonicAt(0, KeyEvent.VK_T);
      jtp.addTab("Humidity",
                 null,
                 this.setUpHumidityPanel(),
                 "Viewing Humdity Data");
      jtp.setMnemonicAt(1, KeyEvent.VK_H);
      jtp.addTab("Pressure",
                 null,
                 this.setUpPressurePanel(),
                 "Viewing Barometric Pressure Data");
      jtp.setMnemonicAt(2, KeyEvent.VK_P);
      jtp.addTab("Dew Point",
                 null,
                 this.setUpDewPointPanel(),
                 "Viewing Dew Point Data");
      jtp.setMnemonicAt(3, KeyEvent.VK_D);
      jtp.addTab("Heat Index",
                 null,
                 this.setUpHeatIndexPanel(),
                 "Viewing Heat Index Data");
      jtp.setMnemonicAt(4, KeyEvent.VK_I);

      this.getContentPane().add(jtp, BorderLayout.CENTER);
      this.setVisible(true);
   }

   /**/
   private JPanel setUpPressurePanel(){
      JPanel pressurePanel = new JPanel();
      pressurePanel.setLayout(new BorderLayout());
      JLabel presLabel = new JLabel("Pressure");
      pressurePanel.add(presLabel, BorderLayout.CENTER);
      return pressurePanel;
   }

   /**/
   private JPanel setUpTemperaturePanel(){
      JPanel temperaturePanel = new JPanel();
      temperaturePanel.setLayout(new BorderLayout());
      JLabel tempLabel = new JLabel("Temperature");
      temperaturePanel.add(tempLabel, BorderLayout.CENTER);
      return temperaturePanel;
   }
}
//////////////////////////////////////////////////////////////////////
