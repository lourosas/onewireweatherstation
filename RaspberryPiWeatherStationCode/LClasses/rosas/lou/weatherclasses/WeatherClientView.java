/********************************************************************
<GNU Stuff to go here>
********************************************************************/
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.TestPanel2;

//Need to implement a Subscriber...for the Publish-Subscribe pattern
public class WeatherClientView extends GenericJFrame  implements
WeatherClientObserver{
   private static final short WIDTH  = 700;
   private static final short HEIGHT = 500;
   
   Object controller;
   ActionListener           actionListener;
   ItemListener             itemListener;
   KeyListener              keyListener;
   java.util.List<String>   missionData;
   
   //Initializer stuff here
   {
      controller     = null;
      actionListener = null;
      itemListener   = null;
      keyListener    = null;
      missionData    = null;
   }
   
   /**
   Constructor of no arguments
   **/
   public WeatherClientView(){
      this("");
   }
   
   /**
   Constructor taking the Title Attribute
   **/
   public WeatherClientView(String title){
      super(title);
      WeatherClientController wcc = new WeatherClientController(this,
                                            new WeatherClient(this));
      wcc.requestMissionData();
      this.addActionListener(wcc);
      this.addItemListener(wcc);
      this.addKeyListener(wcc);
      this.setUpGUI();
   }

   //////////////////////////Public Methods//////////////////////////
   ////////////////WeatherClientObserver implementation//////////////
   /**
   **/
   public void updateMissionData(java.util.List<String> missionData){
      try{
         this.missionData = new LinkedList<String>(missionData);
      }
      catch(NullPointerException npe){
         //TBD...try to put something here to indicate NO DATA
         npe.printStackTrace();
      }
   }

   /**
   **/
   public void updateTemperatureData(java.util.List<String> tempData){
   }

   /**
   **/
   public void addController(Object controller){
      this.controller = controller;
   }
   
   /**
   **/
   public void addActionListener(ActionListener actionListener){
      this.actionListener = actionListener;
   }
   
   /**
   **/
   public void addItemListener(ItemListener itemListener){
      this.itemListener = itemListener;
   }
   
   /**
   **/
   public void addKeyListener(KeyListener keyListener){
      this.keyListener = keyListener;
   }

   /////////////////////////Private Methods///////////////////////////
   /**
   **/
   private void setUpGUI(){
      this.setSize(WIDTH,HEIGHT);
      this.setResizable(false);
      JTabbedPane jtp = new JTabbedPane();
      jtp.addTab("Temperature",
                 null,
                 this.setUpTemperaturePanel(),
                 "Viewing Temperature Data");
      jtp.addTab("Humidity",
                 null,
                 this.setUpHumidityPanel(),
                 "Viewing Humidity Data");
      jtp.addTab("Dew Point",
                 null,
                 this.setUpDewpointPanel(),
                 "Viewing Dew Point Data");
      jtp.addTab("Heat Index",
                 null,
                 this.setUpHeatIndexPanel(),
                 "Viewing Heat Index Data");
      this.getContentPane().add(jtp);
      //this.pack();
      this.setVisible(true);
   }

   /**
   **/
   private JPanel setUpDewpointPanel(){
      JPanel dewpointPanel = new JPanel();
      dewpointPanel.setLayout(new BorderLayout());
      JPanel northPanel  = this.setUpDewpointNorthPanel();
      JPanel centerPanel = this.setUpDewpointCenterPanel();
      JPanel southPanel  = this.setUpDewpointSouthPanel();

      dewpointPanel.add(northPanel,  BorderLayout.NORTH);
      dewpointPanel.add(centerPanel, BorderLayout.CENTER);
      dewpointPanel.add(southPanel,  BorderLayout.SOUTH);

      return dewpointPanel;
   }

   /**
   **/
   private JPanel setUpDewpointCenterPanel(){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }

   /**
   **/
   private JPanel setUpDewpointNorthPanel(){
      JPanel northPanel      = new JPanel();
      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup dataGroup  = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());

      JPanel unitsPanel = new JPanel();

      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("DPCelsius");
      unitsGroup.add(celsius);
      //Set up the Item Listener
      //celsius.addItemListener(this.itemListener);
      unitsPanel.add(celsius);

      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("DPFahrenheit");
      unitsGroup.add(fahrenheit);
      //Set up the Item Listener
      //fahrenheit.addItemListener(this.itemListener);
      unitsPanel.add(fahrenheit);

      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("DPKelvin");
      unitsGroup.add(kelvin);
      //Set up the Item Listener
      //kelvin.addItemListener(this.itemListener);
      unitsPanel.add(kelvin);

      northPanel.add(unitsPanel);

      JPanel dataPanel = new JPanel();

      JRadioButton graph = new JRadioButton("Graph");
      //Somehow, set the display state...worry about that later
      //Add the Item Listener
      //graph.addItemListner(this.itemListener);
      dataGroup.add(graph);
      dataPanel.add(graph);

      JRadioButton data  = new JRadioButton("Data", true);
      dataGroup.add(data);
      //data.addItemListener(this.itemListener);
      dataPanel.add(data);

      northPanel.add(dataPanel);

      String dates[] = {"Dates"};
      JComboBox dewPointComboBox = new JComboBox(dates);
      dewPointComboBox.setActionCommand("Dewpoint Combo Box");
      dewPointComboBox.setName("Dewpoint");
      //Figure out what to do with this.
      dewPointComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayDewpoint(e);
         }
      });
      northPanel.add(dewPointComboBox);
      return northPanel;
   }

   /**
   **/
   private JPanel setUpDewpointSouthPanel(){
      JPanel southPanel = new JPanel();
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("DP Refresh");
      //refresh.addActionListener(this.actionListener);
      //refresh.addKeyListener(this.keyListener);
      southPanel.add(refresh);

      JButton save = new JButton("Save Dewpoint data");
      save.setActionCommand("DP Save");
      //save.addActionListener(this.actionListener);
      //save.addKeyListener(this.keyListener);
      southPanel.add(save);

      JButton quit = new JButton("Quit");
      quit.setActionCommand("DP Quit");
      quit.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            System.out.println(e.getSource());
            System.out.println(e.getActionCommand());
            setVisible(false);
            System.exit(0);
         }
      });
      southPanel.add(quit);

      return southPanel;
   }

   /**
   **/
   private JPanel setUpHeatIndexPanel(){
      JPanel heatIndexPanel = new JPanel();
      heatIndexPanel.setLayout(new BorderLayout());

      JPanel northPanel  = this.setUpHeatIndexNorthPanel();
      JPanel centerPanel = this.setUpHeatIndexCenterPanel();
      JPanel southPanel  = this.setUpHeatIndexSouthPanel();

      heatIndexPanel.add(northPanel,  BorderLayout.NORTH);
      heatIndexPanel.add(centerPanel, BorderLayout.CENTER);
      heatIndexPanel.add(southPanel,  BorderLayout.SOUTH);

      return heatIndexPanel;
   }

   /**
   **/
   private JPanel setUpHeatIndexCenterPanel(){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }

   /**
   **/
   private JPanel setUpHeatIndexNorthPanel(){
      JPanel northPanel      = new JPanel();
      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup dataGroup  = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());

      JPanel unitsPanel = new JPanel();

      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("HICelsius");
      unitsGroup.add(celsius);
      //Set up the Item Listener
      //celsius.addItemListener(this.itemListener);
      unitsPanel.add(celsius);

      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("HIFahrenheit");
      unitsGroup.add(fahrenheit);
      //set up the Item Listener
      //fahrenheit.addItemListener(this.itemListener);
      unitsPanel.add(fahrenheit);

      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("HIKelvin");
      unitsGroup.add(kelvin);
      //set up the Item Listener
      //kelvin.addItemListener(this.itemListener);
      unitsPanel.add(kelvin);

      northPanel.add(unitsPanel);

      JPanel dataPanel = new JPanel();

      JRadioButton graph = new JRadioButton("Graph");
      //Somehow, need to set the display state...worry later
      //Add the Item Listener
      //graph.addItemListener(this.itemListener);
      dataGroup.add(graph);
      dataPanel.add(graph);

      JRadioButton data = new JRadioButton("Data", true);
      dataGroup.add(data);
      //data.addItemListener(this.itemListener);
      dataPanel.add(data);

      northPanel.add(dataPanel);

      String dates[] = {"Dates"};
      JComboBox heatIndexComboBox = new JComboBox(dates);
      heatIndexComboBox.setActionCommand("Heat Index Combo Box");
      heatIndexComboBox.setName("HeatIndex");
      //Figure out what to do this this.
      heatIndexComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayHeatIndex(e);
         }
      });
      northPanel.add(heatIndexComboBox);
      return northPanel;
   }

   /**
   **/
   private JPanel setUpHeatIndexSouthPanel(){
      JPanel southPanel = new JPanel();
      southPanel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("HI Refresh");
      //Add Action Listener
      //refresh.addActionListener(this.actionListener);
      //Add Key Listener
      //reshresh.addKeyListener(this.keyListener);
      southPanel.add(refresh);

      JButton save = new JButton("Save Heat Index Data");
      //Add Action Listern
      //save.addActionListener(this.actionListener);
      //Add Key Listener
      //save.addKeyListener(this.keyListener);
      southPanel.add(save);

      JButton quit = new JButton("Quit");
      quit.setActionCommand("HeatIndex Quit");
      //Add Action Listener
      quit.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            System.out.println(e.getSource());
            System.out.println(e.getActionCommand());
            setVisible(false);
            System.exit(0);
         }
      });

      //Add the Key Listener (Eventually)
      //quit.addKeyListener(....)
      southPanel.add(quit);

      return southPanel;
   }

   /**
   **/
   private JPanel setUpHumidityPanel(){
      JPanel humidityPanel = new JPanel();
      humidityPanel.setLayout(new BorderLayout());
      JPanel northPanel  = this.setUpHumidityNorthPanel();
      JPanel centerPanel = this.setUpHumidityCenterPanel(); 
      JPanel southPanel  = this.setUpHumiditySouthPanel();

      humidityPanel.add(northPanel,  BorderLayout.NORTH);
      humidityPanel.add(centerPanel, BorderLayout.CENTER);
      humidityPanel.add(southPanel,  BorderLayout.SOUTH);
      return humidityPanel;
   }

   /**
   **/
   private JPanel setUpHumidityCenterPanel(){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   } 

   /**
   **/
   private JPanel setUpHumidityNorthPanel(){
      JPanel northPanel     = new JPanel();
      ButtonGroup dataGroup = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());

      JRadioButton graph = new JRadioButton("Graph");
      graph.setActionCommand("HGraph");
      //Set up the Item Listener
      //graph.addItemListener(this.itemListener);
      dataGroup.add(graph);
      northPanel.add(graph);

      JRadioButton data = new JRadioButton("Data", true);
      data.setActionCommand("HData");
      //Set up the Item Listener
      //data.addItemListener(this.itemListener);
      dataGroup.add(data);
      northPanel.add(data);

      //Humidity Combo Box Data
      String dates[] = {"All Days"};
      JComboBox humidityComboBox = new JComboBox(dates);
      humidityComboBox.setName("Humidity");
      humidityComboBox.setActionCommand("Humidity Combo Box");
      humidityComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayHumidity(e);
         }
      });
      northPanel.add(humidityComboBox);
      return northPanel;
   }

   /**
   **/
   private JPanel setUpHumiditySouthPanel(){
      JPanel southPanel = new JPanel();
      southPanel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Humidity Refresh");
      //Add Action Listener
      //refresh.addActionListener(this.actionListener);
      //Add Key Listener
      //refresh.addKeyListener(this.keyListener);
      southPanel.add(refresh);

      JButton save = new JButton("Save Humidity Data");
      save.setActionCommand("Humidity Save");
      //Add Action Listener
      //save.addActionListner(this.actionListener);
      //Add Key Listener
      //save.addKeyListener(this.keyListener);
      southPanel.add(save);

      JButton quit  = new JButton("Quit");
      quit.setActionCommand("Humidity Quit");
      //add the ActionListener
      quit.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setVisible(false);
            System.exit(0);
         }
      });
      southPanel.add(quit);
      return southPanel;
   }
   
   /**
   **/
   private JPanel setUpTemperaturePanel(){
      JPanel temperaturePanel = new JPanel();
      temperaturePanel.setLayout(new BorderLayout());
      temperaturePanel.add(this.setUpTempNorthPanel(),
                                                  BorderLayout.NORTH);
      temperaturePanel.add(this.setUpTempCenterPanel(),
                                                 BorderLayout.CENTER);
      temperaturePanel.add(this.setUpTempSouthPanel(),
                                                  BorderLayout.SOUTH);
      return temperaturePanel;
   }
   
   /**
   **/
   private JPanel setUpTempCenterPanel(){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }
   
   private JPanel setUpTempNorthPanel(){
      JPanel northPanel            = new JPanel();
      ButtonGroup temperatureGroup = new ButtonGroup();
      ButtonGroup dataGroup        = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      //Set up the temperature panel
      JPanel tempPanel = new JPanel();
      
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("TCelsius");
      temperatureGroup.add(celsius);
      //Set up the Item Listener
      celsius.addItemListener(this.itemListener);
      tempPanel.add(celsius);
      
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("TFahrenheit");
      temperatureGroup.add(fahrenheit);
      //Set up the Item Listener
      fahrenheit.addItemListener(this.itemListener);
      tempPanel.add(fahrenheit);
      
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("TKelvin");
      temperatureGroup.add(kelvin);
      //Set up the Item Listener
      kelvin.addItemListener(this.itemListener);
      tempPanel.add(kelvin);
      
      northPanel.add(tempPanel);
      
      JPanel dataPanel = new JPanel();
      
      JRadioButton graph = new JRadioButton("Graph");
      graph.setActionCommand("TGraph");
      //Somehow set the display state...will worry about that later
      dataGroup.add(graph);
      graph.addItemListener(this.itemListener);
      dataPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data", true);
      data.setActionCommand("TData");
      dataGroup.add(data);
      //Somehow, set up the display state..will worry about that later
      data.addItemListener(this.itemListener);
      dataPanel.add(data);
      
      northPanel.add(dataPanel);
      
      String dates[] = {"Dates"};
      JComboBox tempComboBox = new JComboBox(dates);
      tempComboBox.setActionCommand("Temperature Combo Box");
      tempComboBox.setName("Temperature");
      //Figure out what to do with this.
      tempComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayTemperature(e);
         }
      });
      northPanel.add(tempComboBox);
      return northPanel;
   }
   
   /**
   **/
   private JPanel setUpTempSouthPanel(){
      JPanel southPanel = new JPanel();
      southPanel.setBorder(BorderFactory.createEtchedBorder());
      
      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("Temp Refresh");
      //Add Action Listner
      refresh.addActionListener(this.actionListener);
      //Add Key Listener
      refresh.addKeyListener(this.keyListener);
      southPanel.add(refresh);
      
      JButton save = new JButton("Save Temperature Data");
      save.setActionCommand("Temp Save");
      //Add Action Listener
      save.addActionListener(this.actionListener);
      //Add Key Listener
      save.addKeyListener(this.keyListener);
      southPanel.add(save);
      
      JButton quit = new JButton("Quit");
      quit.setActionCommand("Temp Quit");
      //add the Action Listener
      quit.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setVisible(false);
            System.exit(0);
         }
      });
      //add the Key Listener (Eventually)
      //quit.addKeyListener
      southPanel.add(quit);
      
      return southPanel;
   }

   /**
   **/
   private void setTheDayDewpoint(ActionEvent e){
      System.out.println(e.getSource());
      System.out.println(e.getActionCommand());
      //Somehow, need to go and figure out how to request the data
      //from the server and populate accordingly
   }

   /****/
   private void setTheDayHeatIndex(ActionEvent e){
      System.out.println(e.getSource());
      System.out.println(e.getActionCommand());
      //Somehow, need to go and figure out what is next...
   }

   /**
   **/
   private void setTheDayHumidity(ActionEvent e){
      System.out.println(e.getSource());
      System.out.println(e.getActionCommand());
      //Somehow, need to go and figure out how to request the data
      //from the server and populate accordingly  
   }
   /**
   **/
   private void setTheDayTemperature(ActionEvent e){
      System.out.println(e.getSource());
      System.out.println(e.getActionCommand());
      //Now, somehow need to go and figure out how to request the
      //data from the server and populate accordingly
   }
}
