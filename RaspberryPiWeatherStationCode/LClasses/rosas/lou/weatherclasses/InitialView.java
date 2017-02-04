/**
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import myclasses.*;
import rosas.lou.weatherclasses.*;

public class InitialView extends GenericJFrame
implements TimeObserver, TemperatureObserver, HumidityObserver,
BarometerObserver, CalculatedObserver{
   private static final short WIDTH  = 340;
   private static final short HEIGHT = 230;

   private ButtonGroup buttonGroup;
   private ButtonGroup menuItemGroup;
   
   private java.util.List<WeatherStation> w_s_List = null;
   private java.util.List<JTextField> tfList       = null;

   //**********************Constructors*****************************
   /*
   Constructor of no arguments
   */
   public InitialView(){
      this("", null);
   }

   /*
   Constructor taking the String attribute for the title and the
   controller Object
   */
   public InitialView(String title, Object controller){
      super(title);
      //Go Ahead and set up the GUI
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
   }

   //**********************Public Methods***************************
   /*
   */
   public void addWeatherStation(WeatherStation ws){
      try{
         w_s_List.add(ws);
      }
      catch(NullPointerException npe){
         w_s_List = new Vector<WeatherStation>();
         w_s_List.add(ws);
      }
   }

   /*
   Just start running the WeatherStations
   */
   public void collectData(){
      try{
         Iterator<WeatherStation> i = w_s_List.iterator();
         while(i.hasNext()){
            WeatherStation ws = i.next();
            //Start the Weather Station and start collecting data
            new Thread(ws).start();
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   /*
   */
   public void initialize(WeatherStation ws){
      ws.addTemperatureObserver(this);
      ws.addHumidityObserver(this);
      ws.addBarometerObserver(this);
      ws.addCalculatedObserver(this);
      ws.addTimeObserver(this);
      this.addWeatherStation(ws);
   }
   
   /*
   */
   public void updateDewpoint(WeatherEvent event){}
   
   /*
   */
   public void updateHeatIndex(WeatherEvent event){}
   
   /*
   */
   public void updateHumidity(WeatherEvent event){
      Iterator<JTextField> i = this.tfList.iterator();
      String s = String.format("%.3f", event.getValue());
      while(i.hasNext()){
         JTextField jtf = i.next();
         if(jtf.getName().equals("Humidity")){
            jtf.setText(s);
         }
      }
   }
   
   /*
   */
   public void updatePressure(WeatherEvent event){
      Iterator<JTextField> i = this.tfList.iterator();
      String s = String.format("%.3f", event.getValue());
      while(i.hasNext()){
         JTextField jtf = i.next();
         if(jtf.getName().equals("Pressure")){
            jtf.setText(s);
         }
      }
   }
   
   /*
   */
   public void updateTemperature(WeatherEvent event){
      Iterator<JTextField> i = this.tfList.iterator();
      while(i.hasNext()){
         JTextField jtf = i.next();
         if(jtf.getName().equals("Temp")){
            jtf.setText("" + event.getValue());
         }
      }
   }
      
   /*
   */
   public void updateTime(){}
   
   /*
   */
   public void updateTime(String formatedTime){
      Iterator<JTextField> i = this.tfList.iterator();
      while(i.hasNext()){
         JTextField jtf = i.next();
         if(jtf.getName().equals("Time")){
            jtf.setText(formatedTime);
         }
      }
   }
   
   /*
   */
   public void updateTime(String mo, String day, String year){}
   
   /*
   */
   public void updateTime
   (
      String year,
      String month,
      String day,
      String hour,
      String min,
      String sec
   ){}
   
   /*
   Implementation of the updateWindChill() from the 
   CalculatedObserver interface
   */
   public void updateWindChill(WeatherEvent event){}   
   
   //**********************Private Methods**************************
   /*
   Set upt the Center Panel of the GUI and return the JPanel
   */
   private JPanel setCenterPanel(){
      this.tfList = new Vector<JTextField>();
      JPanel centerPanel = new JPanel();
      Border border =
              BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
      centerPanel.setBorder(border);
      centerPanel.setLayout(new GridLayout(0, 2, 4, 4));
      final int RIGHT = SwingConstants.RIGHT;
      JLabel time           = new JLabel("Time:  ", RIGHT);
      JTextField timeTF     = new JTextField("Time");
      timeTF.setName("Time");
      timeTF.setEditable(false);
      JLabel temp           = new JLabel("Temperature:  ", RIGHT);
      JTextField tempTF     = new JTextField("Temperature");
      tempTF.setName("Temp");
      tempTF.setEditable(false);
      JLabel humidity       = new JLabel("Humidity:  ", RIGHT);
      JTextField humidityTF = new JTextField("Humidity");
      humidityTF.setName("Humidity");
      humidityTF.setEditable(false);
      JLabel pressure = new JLabel("Barometric Pressure:  ", RIGHT);
      JTextField pressureTF = new JTextField("Barometric Pressure");
      pressureTF.setName("Pressure");
      pressureTF.setEditable(false);
      centerPanel.add(time); centerPanel.add(timeTF);
      centerPanel.add(temp); centerPanel.add(tempTF);
      centerPanel.add(humidity); centerPanel.add(humidityTF);
      centerPanel.add(pressure); centerPanel.add(pressureTF);
      tfList.add(timeTF);     tfList.add(tempTF);
      tfList.add(humidityTF); tfList.add(pressureTF);
      return centerPanel;
   }

   /*
   Set up the North Panel of the GUI and return the JPanel
   */
   private JPanel setNorthPanel(){
      String s            = new String("Current Weather Data");
      JPanel northPanel   = new JPanel();
      JLabel currentLabel = new JLabel(s, SwingConstants.CENTER);

      Border border = BorderFactory.createEtchedBorder();
      northPanel.setBorder(border);
      northPanel.add(currentLabel);

      return northPanel;
   }

   /*
   */
   private JPanel setSouthPanel(Object controller){
      JPanel buttonPanel = new JPanel();
      Border border = BorderFactory.createEtchedBorder();
      buttonPanel.setBorder(border);
      //Instantiate the button group
      this.buttonGroup = new ButtonGroup();

      //For now, just go ahead and create the button
      JButton start = new JButton("Start");
      start.setMnemonic(KeyEvent.VK_S);
      start.addActionListener((ActionListener)controller);
      start.addKeyListener((KeyListener)controller);
      buttonPanel.add(start);
      this.buttonGroup.add(start);

      JButton stop = new JButton("Stop");
      stop.setMnemonic(KeyEvent.VK_T);
      stop.addActionListener((ActionListener)controller);
      stop.addKeyListener((KeyListener)controller);
      buttonPanel.add(stop);
      this.buttonGroup.add(stop);

      JButton quit = new JButton("Quit");
      quit.setMnemonic(KeyEvent.VK_Q);
      quit.addActionListener((ActionListener)controller);
      quit.addKeyListener((KeyListener)controller);
      buttonPanel.add(quit);
      this.buttonGroup.add(quit);

      return buttonPanel;
   }

   /*
   */
   private JMenu setUpFileMenu(Object controller){
      JMenu file = new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);

      file.addSeparator();

      //Quit Menu Item
      JMenuItem quit = new JMenuItem("Quit", 'Q');
      quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                            InputEvent.CTRL_MASK));
      quit.addActionListener((ActionListener)controller);
      //Add the Menu Item to the Menu Item Group
      this.menuItemGroup.add(quit);
      //Add to the file menu
      file.add(quit);

      return file;
   }

   /*
   */
   private JMenuBar setUpMenuBar(Object controller){
      JMenuBar jmenuBar = new JMenuBar();

      this.menuItemGroup = new ButtonGroup();
      //Set up the File Menu
      jmenuBar.add(this.setUpFileMenu(controller));
      return jmenuBar;
   }

   /*
   Set up the GUI for display
   */
   private void setUpGUI(Object controller){
      //Get the Content Pane
      Container contentPane = this.getContentPane();
      //Get the screen dimensions
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //Set the size of the GUI
      this.setSize(WIDTH, HEIGHT);
      //Set up the location
      this.setLocation((int)((dim.getWidth()  - WIDTH)/2),
                       (int)((dim.getHeight() - HEIGHT)/2));
      //Set up the Rest of the GUI
      contentPane.add(this.setNorthPanel(),   BorderLayout.NORTH);
      contentPane.add(this.setCenterPanel(), BorderLayout.CENTER);
      contentPane.add(this.setSouthPanel(controller),
                                                BorderLayout.SOUTH);
      //Set up the Menu Bar
      this.setJMenuBar(this.setUpMenuBar(controller));
   }
}
