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
public class WeatherDatabaseClientView extends GenericJFrame
implements WeatherDatabaseClientObserver{
   private static final short WIDTH        = 750;
   private static final short HEIGHT       = 600;
   private static final short TOTAL_PANELS = 5;
   private static String [] MONTHS = {"January", "February",
   "March", "April", "May", "June", "July", "August", "September",
   "October", "November", "December"};
   private static String [] DAYS = {"01","02","03","04","05","06",
   "07","08","09","10","11","12","13","14","15","16","17","18","19",
   "20","21","22","23","24","25","26","27","28","29","30","31"};
   private static String [] YEARS = {"2017", "2018", "2019", "2020"};

   private WeatherDatabaseClientController _controller = null;
   //Really do not need these to be global any longer--handled by
   //The Controller directly, realtime...
   private JTextField _address                         = null;
   private JTextField _port                            = null;
   private JComboBox<String> _monthCB                  = null;
   private JComboBox<String> _dayCB                    = null;
   private JComboBox<String> _yearCB                   = null;
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
      this._controller = new WeatherDatabaseClientController(this);
      this.setUpGUI();
   }

   //////////////////////////Public Methods///////////////////////////
   /**/
   public String address(){
      return this._address.getText();
   }

   /**/
   public String port(){
      return this._port.getText();
   }

   ///////WeatherDatabaseClientObserver Interface Implementaion///////
   public void alertDewpointTimeout(){}

   public void alertHeatIndexTimeout(){}

   public void alertHumidityTimeout(){}

   public void alertPressureTimeout(){}

   public void alertTemperatureTimeout(){}

   public void updateDewpointData(java.util.List<WeatherData> data){}

   public void updateHeatIndexData(java.util.List<WeatherData> data){}

   public void updateHumidityData(java.util.List<WeatherData> data){}

   public void updatePressureData(java.util.List<WeatherData> data){}

   public void updateTemperatureData(java.util.List<WeatherData> data){
      Iterator<WeatherData> it = data.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         System.out.print(wd.month()+", "+ wd.day()+", "+wd.year());
         System.out.print(", "+wd.time()+", ");
         System.out.println(String.format("%.2f", wd.englishData()));
      }
   }

   //////////////////////////Private Methods//////////////////////////
   /**/
   private void setUpCurrentDate(){
      Calendar now = Calendar.getInstance();
      int month    = now.get(Calendar.MONTH);
      int day      = now.get(Calendar.DAY_OF_MONTH) - 1;
      int year     = now.get(Calendar.YEAR) - 2017;
      this._monthCB.setSelectedIndex(month);
      this._dayCB.setSelectedIndex(day);
      this._yearCB.setSelectedIndex(year);
   }

   /**/
   private void setUpDateComboBoxes(){
      this._monthCB = new JComboBox();
      this._monthCB.setActionCommand("Month Combo Box");
      this._monthCB.setName("Month");
      this._monthCB.addActionListener(this._controller);
      //this._monthCB.addItemListener(this._controller);
      //this._monthCB.addKeyListener(this._controller);
      for(int i = 0; i < this.MONTHS.length; i++){
         this._monthCB.addItem(this.MONTHS[i].trim());
      }
      this._dayCB = new JComboBox();
      this._dayCB.setActionCommand("Day Combo Box");
      this._dayCB.setName("Day");
      this._dayCB.addActionListener(this._controller);
      //this._dayCB.addItemListener(this._controller);
      //this._dayCB.addKeyListener(this._controller);
      for(int i = 0; i < this.DAYS.length; i++){
         this._dayCB.addItem(this.DAYS[i].trim());
      }
      this._yearCB = new JComboBox();
      this._yearCB.setActionCommand("Year Combo Box");
      this._yearCB.setName("Year");
      this._yearCB.addActionListener(this._controller);
      //this._yearCB.addItemListener(this._controller);
      //this._yearCB.addKeyListener(this._controller);
      for(int i = 0; i < this.YEARS.length; i++){
         this._yearCB.addItem(this.YEARS[i].trim());
      }
      //Set the current date in the combo box
      this.setUpCurrentDate();
   }

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
   private JTabbedPane setTabbedPane(){
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

      return jtp;
   }

   /**/
   private void setUpGUI(){
      this.setLayout(new BorderLayout());
      this.setSize(WIDTH,HEIGHT);
      this.setResizable(false);
      JPanel northPanel = this.setUpNorthPanel();
      JTabbedPane jtp = this.setTabbedPane();
      this.getContentPane().add(northPanel, BorderLayout.NORTH);
      this.getContentPane().add(jtp, BorderLayout.CENTER);
      this.setVisible(true);
   }

   /**/
   private JPanel setUpNorthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

      JLabel addLabel=new JLabel("Address:  ",SwingConstants.RIGHT);
      this._address = new JTextField("Enter Address", 16);
      this._address.setName("Address");
      this._address.addActionListener(this._controller);
      this._address.addKeyListener(this._controller);
      JLabel gap = new JLabel("                  ");
      JLabel portLabel=new JLabel("Port:  ",SwingConstants.RIGHT);
      this._port    = new JTextField(5);
      this._port.setName("Port");
      this._port.addActionListener(this._controller);
      this._port.addKeyListener(this._controller);
      this._address.requestFocus();
      this._address.selectAll();
      this.setUpDateComboBoxes();

      panel.add(addLabel);
      panel.add(this._address);
      panel.add(portLabel);
      panel.add(this._port);
      panel.add(gap);
      panel.add(this._monthCB);
      panel.add(this._dayCB);
      panel.add(this._yearCB);
      return panel;
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
      temperaturePanel.add(this.setUpTemperatureSouthPanel(),
                                                  BorderLayout.SOUTH);
      temperaturePanel.add(this.setUpTemperatureNorthPanel(),
                                                  BorderLayout.NORTH);
      return temperaturePanel;
   }

   /**/
   private JPanel setUpTemperatureNorthPanel(){
      JPanel panel                 = new JPanel();
      ButtonGroup temperatureGroup = new ButtonGroup();
      ButtonGroup dataGroup        = new ButtonGroup();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JPanel temperaturePanel = new JPanel();
      JRadioButton celsius    = new JRadioButton("Celsius", true);
      celsius.setActionCommand("TCelsius");
      temperatureGroup.add(celsius);
      temperaturePanel.add(celsius);
      celsius.addItemListener(this._controller);
      //Add another listener...
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("TFahrenheit");
      temperatureGroup.add(fahrenheit);
      temperaturePanel.add(fahrenheit);
      fahrenheit.addItemListener(this._controller);
      //Add another listener...
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("TKelvin");
      temperatureGroup.add(kelvin);
      temperaturePanel.add(kelvin);
      kelvin.addItemListener(this._controller);
      //Add another listener...

      panel.add(temperaturePanel);

      JPanel dataPanel = new JPanel();

      JRadioButton graph = new JRadioButton("Graph", true);
      graph.setActionCommand("TData");
      dataGroup.add(graph);
      dataPanel.add(graph);
      graph.addItemListener(this._controller);
      //Add another Listener...
      JRadioButton data = new JRadioButton("Data");
      dataGroup.add(data);
      dataPanel.add(data);
      data.addItemListener(this._controller);
      //Add another Listener...
      panel.add(dataPanel);
      return panel;
   }

   /**/
   private JPanel setUpTemperatureSouthPanel(){
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("TemperatureRefresh");
      refresh.addActionListener(this._controller);
      refresh.addKeyListener(this._controller);

      panel.add(refresh);

      JButton save = new JButton("Save");
      save.setActionCommand("TemperatureSave");
      save.addActionListener(this._controller);
      save.addKeyListener(this._controller);

      panel.add(save);

      return panel;
   }
}
//////////////////////////////////////////////////////////////////////
