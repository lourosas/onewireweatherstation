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
   
   //This is subject to change
   private static final short TOTAL_PANELS = 5;
   
   Object controller;
   ActionListener           actionListener;
   ItemListener             itemListener;
   KeyListener              keyListener;
   java.util.List<String>   missionData;
   java.util.List<String>   currentTempData;
   JComboBox                tempComboBox;
   JComboBox                humidityComboBox;
   JComboBox                heatIndexComboBox;
   JComboBox                dewPointComboBox;
   JComboBox                pressureComboBox;
   ButtonGroup              temperatureGroup;
   ButtonGroup              tempDataGroup;
   
   //Initializer stuff here
   {
      controller        = null;
      actionListener    = null;
      itemListener      = null;
      keyListener       = null;
      missionData       = null;
      tempComboBox      = null;
      humidityComboBox  = null;
      dewPointComboBox  = null;
      heatIndexComboBox = null;
      pressureComboBox  = null;
      temperatureGroup  = null;
      tempDataGroup     = null;
      currentTempData   = null;
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
      WeatherClient wc = new WeatherClient(this);
      WeatherClientController wcc =
                                new WeatherClientController(this,wc);
      this.addActionListener(wcc);
      this.addItemListener(wcc);
      this.addKeyListener(wcc);
      this.setUpGUI();
      //Request the Mission Data:  which holds the current dates of
      //data recorded
      wcc.requestMissionData();
   }

   //////////////////////////Public Methods//////////////////////////
   ////////////////WeatherClientObserver implementation//////////////
   /**
   **/
   public void updateMissionData(java.util.List<String> missionData){
      try{
         this.missionData = new LinkedList<String>(missionData);
         Iterator<String> it = this.missionData.iterator();
         while(it.hasNext()){            
            String date = it.next();
            this.tempComboBox.addItem(date.trim());
            this.humidityComboBox.addItem(date.trim());
            this.dewPointComboBox.addItem(date.trim());
            this.heatIndexComboBox.addItem(date.trim());
            this.pressureComboBox.addItem(date.trim());
         }
      }
      catch(NullPointerException npe){
         //TBD...try to put something here to indicate NO DATA
         //A Dialog box
         npe.printStackTrace();
      }
   }

   /**
   **/
   public void updateTemperatureData(java.util.List<String> tempData){
      Enumeration<AbstractButton> e =
                                    this.tempDataGroup.getElements();
      while(e.hasMoreElements()){
         JRadioButton jrb = (JRadioButton)e.nextElement();
         if(jrb.isSelected()){ 
            if(jrb.getText().equals("Data")){
               this.setUpTemperatureData(tempData);
            }
            else if(jrb.getText().equals("Graph")){
               this.setUpTemperatureGraph(tempData);
            }
         }
      }
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

   /**
   **/
   public ViewState requestTemperatureState(){
      ViewState returnState = new ViewState();
      Enumeration<AbstractButton> e =
                                 this.temperatureGroup.getElements();
      while(e.hasMoreElements()){
         JRadioButton jrb =(JRadioButton)e.nextElement();
         if(jrb.isSelected()){
            if(jrb.getText().equals("Celsius")){
               returnState.units = Units.METRIC;
            }
            else if(jrb.getText().equals("Fahrenheit")){
               returnState.units = Units.ENGLISH;
            }
            else{
               returnState.units = Units.ABSOLUTE;
            }
         }
      }
      String date = (String)tempComboBox.getSelectedItem();
      String[] values = date.split(",");
      returnState.month = values[0].trim();
      returnState.day   = values[1].trim();
      returnState.year  = values[2].trim();
      return returnState;
   }

   /////////////////////////Private Methods///////////////////////////
   /**
   **/
   private JScrollPane printHeatRelatedText
   (
      java.util.List<String> data
   ){
      JScrollPane pane = null;
      try{
         String delimeter = "";
         Enumeration<AbstractButton> e =
                                 this.temperatureGroup.getElements();
         while(e.hasMoreElements()){
            JRadioButton jrb = (JRadioButton)e.nextElement();
            if(jrb.isSelected()){
               if(jrb.getText().equals("Celsius")){
                  delimeter = new String("\u00B0C");
               }
               else if(jrb.getText().equals("Fahrenheit")){
                  delimeter = new String("\u00B0F");
               }
               else if(jrb.getText().equals("Kelvin")){
                  delimeter = new String("K");
               }
            }
         }
         //Check to see the units by looking at the Temperature
         //Button Group
         JTextArea textArea = new JTextArea(28,35);
         textArea.setEditable(false);
         Iterator<String> i = data.iterator();
         while(i.hasNext()){
            String currentData = (String)i.next();
            String[] currentDataArray = currentData.split(",");
            String publishedText = new String(currentDataArray[0]);
            publishedText += " " + currentDataArray[1] + ", ";
            publishedText += currentDataArray[2] + "; ";
            publishedText += currentDataArray[3] + ":  ";
            publishedText += currentDataArray[4];
            publishedText += delimeter + "\n";
            textArea.append(publishedText);
         }
         pane = new JScrollPane(textArea);
      }
      catch(NullPointerException npe){
         //TBD:  need to figure this out at a different time:  where
         //to handle this exception
      }
      finally{
         return pane;
      }
   }
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
      jtp.addTab("Barometric Pressure",
                 null,
                 this.setUpPressurePanel(),
                 "Viewing Barometric Pressure Data");
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
   private JPanel setUpPressurePanel(){
      JPanel pressurePanel = new JPanel();
      pressurePanel.setLayout(new BorderLayout());
      JPanel northPanel  = this.setUpPressureNorthPanel();
      JPanel centerPanel = this.setUpPressureCenterPanel();
      JPanel southPanel  = this.setUpPressureSouthPanel();

      pressurePanel.add(northPanel,  BorderLayout.NORTH);
      pressurePanel.add(centerPanel, BorderLayout.CENTER);
      pressurePanel.add(southPanel,  BorderLayout.SOUTH);

      return pressurePanel;
   }

   /**
   **/
   private JPanel setUpPressureCenterPanel(){
      JPanel centerPanel = new JPanel();
      centerPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
      return centerPanel;
   }

   /**
   **/
   private JPanel setUpPressureNorthPanel(){
      JPanel northPanel      = new JPanel();
      ButtonGroup unitsGroup = new ButtonGroup();
      ButtonGroup dataGroup  = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());

      JPanel unitsPanel = new JPanel();

      JRadioButton mmHg = new JRadioButton("mmHg", true);
      mmHg.setActionCommand("PMetric");
      unitsGroup.add(mmHg);
      //Set up the Item Listener
      //mmHg.addItemListener(this.itemListener);
      unitsPanel.add(mmHg);

      JRadioButton inHg = new JRadioButton("inHg");
      inHg.setActionCommand("PEnglish");
      unitsGroup.add(inHg);
      //Set up the Item Listener
      //inHg.addItemListener(this.itemListener);
      unitsPanel.add(inHg);

      JRadioButton milliBars = new JRadioButton("milliBars");
      milliBars.setActionCommand("PAbsolute");
      unitsGroup.add(milliBars);
      //Set up the Item Listener
      //milliBars.addItemListener(this.itemListener);
      unitsPanel.add(milliBars);

      northPanel.add(unitsPanel);

      JPanel dataPanel = new JPanel();

      JRadioButton graph = new JRadioButton("Graph");
      //Somehow, set the display state...
      //Add the Item Listener
      //graph.addItemListener(this.itemListener);
      dataPanel.add(graph);
      dataGroup.add(graph);

      JRadioButton data = new JRadioButton("Data", true);
      //data.addItemListner(this.itemListener);
      dataPanel.add(data);
      dataGroup.add(data);

      northPanel.add(dataPanel);

      String dates[] = {""};
      this.pressureComboBox = new JComboBox(dates);
      this.pressureComboBox.setActionCommand("Pressure Combo Box");
      this.pressureComboBox.setName("Pressure");
      this.pressureComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayPressure(e);
         }
      });
      northPanel.add(this.pressureComboBox);
      return northPanel;

   }

   /**
   **/
   private JPanel setUpPressureSouthPanel(){
      JPanel southPanel = new JPanel();
      southPanel.setBorder(BorderFactory.createEtchedBorder());

      JButton refresh = new JButton("Refresh");
      refresh.setActionCommand("P Refresh");
      //refresh.addActionListener(this.actionListener);
      //refresh.addKeyListener(this.keyListener);
      southPanel.add(refresh);

      JButton save = new JButton("Save Pressure Data");
      save.setActionCommand("P Save");
      //save.addActionListener(this.actionListener);
      //save.addKeyListener(this.keyListener);
      southPanel.add(save);

      JButton quit = new JButton("Quit");
      quit.setActionCommand("P Quit");
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
      //graph.addItemListener(this.itemListener);
      dataGroup.add(graph);
      dataPanel.add(graph);

      JRadioButton data  = new JRadioButton("Data", true);
      dataGroup.add(data);
      //data.addItemListener(this.itemListener);
      dataPanel.add(data);

      northPanel.add(dataPanel);

      String dates[] = {""};
      this.dewPointComboBox = new JComboBox(dates);
      this.dewPointComboBox.setActionCommand("Dewpoint Combo Box");
      this.dewPointComboBox.setName("Dewpoint");
      //Figure out what to do with this.
      this.dewPointComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayDewpoint(e);
         }
      });
      northPanel.add(this.dewPointComboBox);
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

      JButton save = new JButton("Save Dewpoint Data");
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

      String dates[] = {""};
      this.heatIndexComboBox = new JComboBox(dates);
      this.heatIndexComboBox.setActionCommand("Heat Index Combo Box");
      this.heatIndexComboBox.setName("HeatIndex");
      //Figure out what to do this this.
      this.heatIndexComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayHeatIndex(e);
         }
      });
      northPanel.add(this.heatIndexComboBox);
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
      String dates[] = {""};
      this.humidityComboBox = new JComboBox(dates);
      this.humidityComboBox.setName("Humidity");
      this.humidityComboBox.setActionCommand("Humidity Combo Box");
      this.humidityComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayHumidity(e);
         }
      });
      northPanel.add(this.humidityComboBox);
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
   private void setUpTemperatureData(java.util.List<String> data){
      try{
         int tempPanelIndex = -1;
         this.currentTempData = new LinkedList<String>(data);
         JTabbedPane jtp =
                  (JTabbedPane)this.getContentPane().getComponent(0);
         for(int i = 0; i < jtp.getTabCount(); i++){
            if(jtp.getTitleAt(i).equals("Temperature")){
               jtp.setSelectedIndex(i);
               tempPanelIndex = i;
            }
         }
         JPanel tempPanel = (JPanel)jtp.getSelectedComponent();
         //Get the middle component
         JPanel dataPanel = (JPanel)tempPanel.getComponent(1);
         //Remove Everything and redraw
         if(dataPanel.getComponentCount() > 0){
            dataPanel.removeAll();
         }
         dataPanel.setLayout(new BorderLayout());
         JScrollPane jsp = this.printHeatRelatedText(data);
         dataPanel.add(jsp, BorderLayout.CENTER);
         //A "cheesey" way to get the GUI to redraw the data.
         jtp.setSelectedIndex((tempPanelIndex + 1) % TOTAL_PANELS);
         jtp.setSelectedIndex(tempPanelIndex);
      }
      catch(NullPointerException npe){
         //TBD...may need to come up with something other than this
         npe.printStackTrace();
      }
   }
   
   /**
   **/
   private void setUpTemperatureGraph(java.util.List<String> data){
      try{
         int tempPanelIndex = -1;
         this.currentTempData = new LinkedList<String>(data);
         
      }
      catch(NullPointerException npe){
         //TBD...may need to come up with something other than this
         npe.printStackTrace();
      }
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
   
   /**
   **/
   private JPanel setUpTempNorthPanel(){
      JPanel northPanel     = new JPanel();
      this.temperatureGroup = new ButtonGroup();
      this.tempDataGroup    = new ButtonGroup();
      northPanel.setBorder(BorderFactory.createEtchedBorder());
      //Set up the temperature panel
      JPanel tempPanel = new JPanel();
      
      JRadioButton celsius = new JRadioButton("Celsius", true);
      celsius.setActionCommand("TCelsius");
      this.temperatureGroup.add(celsius);
      //Set up the Item Listener
      celsius.addItemListener(this.itemListener);
      tempPanel.add(celsius);
      
      JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
      fahrenheit.setActionCommand("TFahrenheit");
      this.temperatureGroup.add(fahrenheit);
      //Set up the Item Listener
      fahrenheit.addItemListener(this.itemListener);
      tempPanel.add(fahrenheit);
      
      JRadioButton kelvin = new JRadioButton("Kelvin");
      kelvin.setActionCommand("TKelvin");
      this.temperatureGroup.add(kelvin);
      //Set up the Item Listener
      kelvin.addItemListener(this.itemListener);
      tempPanel.add(kelvin);
      
      northPanel.add(tempPanel);
      
      JPanel dataPanel = new JPanel();
      
      JRadioButton graph = new JRadioButton("Graph");
      graph.setActionCommand("TGraph");
      //Somehow set the display state...will worry about that later
      this.tempDataGroup.add(graph);
      graph.addItemListener(this.itemListener);
      dataPanel.add(graph);
      
      JRadioButton data = new JRadioButton("Data", true);
      data.setActionCommand("TData");
      this.tempDataGroup.add(data);
      //Somehow, set up the display state..will worry about that later
      data.addItemListener(this.itemListener);
      dataPanel.add(data);
      
      northPanel.add(dataPanel);
      
      String dates[] = {""};
      this.tempComboBox = new JComboBox(dates);
      this.tempComboBox.setActionCommand("Temperature Combo Box");
      this.tempComboBox.setName("Temperature");
      //Figure out what to do with this.
      this.tempComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            setTheDayTemperature(e);
         }
      });
      this.tempComboBox.addActionListener(this.actionListener);
      this.tempComboBox.addKeyListener(this.keyListener);
      northPanel.add(this.tempComboBox);
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

   /**
   **/
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
   private void setTheDayPressure(ActionEvent e){
      System.out.println(e.getSource());
      System.out.println(e.getActionCommand());
      //Somehow, need to go and figure out what is next...
   }

   /**
   **/
   private void setTheDayTemperature(ActionEvent e){
      JComboBox box = (JComboBox)e.getSource();
      if(e.getModifiers() == java.awt.event.InputEvent.BUTTON1_MASK){
         String date = (String)box.getSelectedItem();
      }
   }
}
