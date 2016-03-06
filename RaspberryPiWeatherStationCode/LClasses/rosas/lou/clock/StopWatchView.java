/********************************************************************
* Copyright (C) 2015 Lou Rosas
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
********************************************************************/

package rosas.lou.clock;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import myclasses.*;
import rosas.lou.*;
import rosas.lou.clock.TimeFormater;
import rosas.lou.clock.TimeListener;
import rosas.lou.clock.ClockState;

/***
 *
 * */
public class StopWatchView extends GenericJFrame
implements TimeListener{
   private StopWatchController swc;
   private ButtonGroup         buttonGroup;
   private ButtonGroup         menuItemGroup;
   private JTextField          timeTF;
   private JTextArea           lapsTA;
   private JScrollPane         lapsSP;
   private GenericJInteractionFrame lapsFrame;
   {
      swc           = null;
      buttonGroup   = null;
      menuItemGroup = null;
      timeTF        = null;
      lapsTA        = null;
      lapsSP        = null;
      lapsFrame     = null;
   }
   ///////////////////////Public Methods//////////////////////////////
   //Constructors
   /***
   Constructor of No Arguments
   */
   public StopWatchView(){
      //Typical:  Call the constructor that accepts a String, passing
      //in an empty String
      this("");
   }

   /***
   Constructor Taking a String Attribute--for a title
   */
   public StopWatchView(String title){
      super(title);
      this.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent w){
            setVisible(false);
            killTheStopWatch();
            System.exit(0);
         }
      });
      //Set up the GUI
      this.setUpGUI();
      this.setResizable(false);
      this.setVisible(true);
   }

   //
   //
   //
   //
   public void killTheStopWatch(){
      this.swc.killTheStopWatch();
   }

   /**
   */
   public void update(ClockState clockState){
      if(clockState.getState() == State.STOP){
         this.setUpStopState();
      }
      else if(clockState.getState() == State.START){
         this.setUpStartState();
      }
      else if(clockState.getState() == State.RESET){
         //Erase all of the Lap-related data (if there is any)
         this.reset(); //Reset the entire Stop Watch
         //reset in the STOP State, as well
         this.setUpStopState();//just to be sure
      }
   }

   /**
 * Implementaion of the update() method for the TimeListener Interface
 * */
   public void update(Object o){}

   /**
   Implementation of the update() method for the TimeListener
   Interface
   */
   public void update(Object o, ClockState cs){}

   /**
   */
   public void update(Stack<TimeFormater> tfStack, ClockState cs){
      if(cs.getState() == State.LAP){
         this.displayLaps(tfStack);
      }
   }

   /**
 * Implementation of the update(...) method of the TimeListener
 * Interface
 * */
   public void update(TimeFormater timeFormater){}

   /**
 * Implementation of the update(...) method of the TimeListener
 * Interface
 * Input:  TimeFormater, ClockState
 * */
   public void update(TimeFormater tf, ClockState cs){
      if(cs.getState() == State.RUN ||
         cs.getState() == State.LAP){
         String time = tf.getDays() + ":";
         time = time.concat(tf.getHours() + ":");
         time = time.concat(tf.getMinutes() + ":");
         time = time.concat(tf.getSeconds());
         this.timeTF.setText(time);
      }
      else if(cs.getState() == State.STOP ||
              cs.getState() == State.RESET){
         this.timeTF.setText(tf.toString());
         this.update(cs);
      }
      else if(cs.getState() != State.UNKNOWN){
         this.timeTF.setText(tf.toString());
      }
   }

   ///////////////////////Private Methods/////////////////////////////
   /**
   */
   private void displayLaps(Stack<TimeFormater> tfStack){
      final int WIDTH  = 180;
      final int HEIGHT = 250;

      try{
         this.lapsFrame.setSize(WIDTH,HEIGHT);
      }
      catch(NullPointerException npe){
         this.setUpLapsDisplay();
      }
      finally{
         this.lapsTA.setText("");
         while(!tfStack.empty()){
            String currentText = this.lapsTA.getText();
            currentText += "Lap " + tfStack.size() + ":  ";
            currentText += tfStack.pop() + "\n";
            this.lapsTA.setText(currentText);
         }
         if(!this.lapsFrame.isShowing()){
            this.lapsFrame.setVisible(true);
         }
      }
   }

   /***
   */
   private void reset(){
      try{
         this.lapsTA.setText("");
         this.lapsFrame.setVisible(false);
      }
      catch(NullPointerException npe){}
   }

   /***
 * */
   private JPanel setCenterPanel(){
      final int RIGHT = SwingConstants.RIGHT;

      JPanel centerPanel = new JPanel();

      Border border = BorderFactory.createEtchedBorder();
      centerPanel.setBorder(border);
      centerPanel.setLayout(new GridLayout(0,2));

      JLabel time = new JLabel("Time:  ", RIGHT);
      this.timeTF = new JTextField();
      this.timeTF.setEditable(false);

      centerPanel.add(time);
      centerPanel.add(timeTF);

      return centerPanel;
   }

   //
   //
   //
   private JPanel setNorthPanel(StopWatchController swc){
      String s = new String("Stop Watch");
      JPanel northPanel = new JPanel();
      JLabel northLabel = new JLabel(s, SwingConstants.CENTER);

      Border border = BorderFactory.createEtchedBorder();
      northPanel.setBorder(border);
      northPanel.add(northLabel);

      return northPanel;
   }

   /***
 * */
   private JPanel setSouthPanel(StopWatchController swc){
      JPanel buttonPanel = new JPanel();
      this.buttonGroup   = new ButtonGroup();

      JButton start = new JButton("Start");
      start.setMnemonic(KeyEvent.VK_S);
      start.addActionListener(swc);
      start.addKeyListener(swc);
      buttonPanel.add(start);

      JButton stop = new JButton("Stop");
      stop.setMnemonic(KeyEvent.VK_T);
      stop.addActionListener(swc);
      stop.addKeyListener(swc);
      buttonPanel.add(stop);

      JButton lap = new JButton("Lap");
      lap.setMnemonic(KeyEvent.VK_L);
      lap.addActionListener(swc);
      lap.addKeyListener(swc);
      buttonPanel.add(lap);

      JButton reset = new JButton("Reset");
      reset.setMnemonic(KeyEvent.VK_R);
      reset.addActionListener(swc);
      reset.addKeyListener(swc);
      buttonPanel.add(reset);

      this.buttonGroup.add(start);
      this.buttonGroup.add(stop);
      this.buttonGroup.add(lap);
      this.buttonGroup.add(reset);
      return buttonPanel;
   }

   /**
   */
   private void setUpLapsDisplay(){
      final int WIDTH     = 180;
      final int HEIGHT    = 250;
      final int APPWIDTH  = 340;
      final int APPHEIGHT = 160;

      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

      this.lapsFrame = new GenericJInteractionFrame("Laps");
      this.lapsFrame.setSize(WIDTH, HEIGHT);
      this.lapsFrame.setLocation(
         (int)(dim.getWidth()/2  + APPWIDTH/2),
         (int)(dim.getHeight()/2 + APPHEIGHT/2 - HEIGHT));

      this.lapsTA = new JTextArea();
      this.lapsTA.setEditable(false);

      this.lapsSP = new JScrollPane(this.lapsTA);

      Container contentPane = this.lapsFrame.getContentPane();
      contentPane.add(this.lapsSP, BorderLayout.CENTER);

      this.lapsFrame.setResizable(false);
      this.lapsFrame.setVisible(false);
   }

   /***
 * */
   private JMenu setUpFileMenu(StopWatchController swc){
      int ctrl = InputEvent.CTRL_MASK;
      KeyStroke ks = null;
      this.menuItemGroup = new ButtonGroup();

      JMenu file = new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);

      JMenuItem start = new JMenuItem("Start", 'S');
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl);
      start.setAccelerator(ks);
      start.addActionListener(swc);
      this.menuItemGroup.add(start);
      file.add(start);

      JMenuItem stop = new JMenuItem("Stop", 'T');
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_T, ctrl);
      stop.setAccelerator(ks);
      stop.addActionListener(swc);
      this.menuItemGroup.add(stop);
      file.add(stop);

      JMenuItem lap = new JMenuItem("Lap", 'L');
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_L, ctrl);
      lap.setAccelerator(ks);
      lap.addActionListener(swc);
      this.menuItemGroup.add(lap);
      file.add(lap);

      JMenuItem reset = new JMenuItem("Reset", 'R');
      reset.addActionListener(swc);
      this.menuItemGroup.add(reset);
      file.add(reset);

      file.addSeparator();

      JMenuItem quit = new JMenuItem("Quit", 'Q');
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_Q, ctrl);
      quit.setAccelerator(ks);
      quit.addActionListener(swc);
      file.add(quit);

      return file;
   }
   /**
 * */
   private void setUpGUI(){
      final short WIDTH  = 340;
      final short HEIGHT = 160;
      this.swc = new StopWatchController();
      //Get the Content Pane
      Container contentPane = this.getContentPane();
      //Get the screen dimensions
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //Get the Screen Dimensions
      this.setSize(WIDTH, HEIGHT);
      //Set up the location
      this.setLocation((int)((dim.getWidth()  - WIDTH)/2),
                       (int)((dim.getHeight() - HEIGHT)/2));
      //Set Up the Rest of the GUI
      contentPane.add(this.setNorthPanel(swc), BorderLayout.NORTH);
      contentPane.add(this.setSouthPanel(swc), BorderLayout.SOUTH);
      contentPane.add(this.setCenterPanel(),   BorderLayout.CENTER);

      //Set up the Menu Bar
      this.setJMenuBar(this.setUpMenuBar(swc));
      this.swc.addTimeListenerToModel(this);
   }

   /**
   */
   private JMenu setUpHelpMenu(StopWatchController swc){
      JMenu help = new JMenu("Help");
      help.setMnemonic(KeyEvent.VK_H);

      //Overview Menu Item
      JMenuItem overView = new JMenuItem("Overview", 'O');
      overView.setAccelerator(
                           KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
      overView.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            showOverviewDialog();
         }
      });
      
      //About Menu Item
      JMenuItem about = new JMenuItem("About", 'A');
      about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
      about.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            showAboutDialog();
         }
      });

      //Add the Menu Items to the Help Menu
      help.add(overView);
      help.addSeparator();
      help.add(about);

      return help;
   }

   /**
 * */
   private JMenuBar setUpMenuBar(StopWatchController swc){
      JMenuBar jmenuBar = new JMenuBar();
      //Set up the File Menu
      jmenuBar.add(this.setUpFileMenu(swc));
      jmenuBar.add(this.setUpHelpMenu(swc));
      return jmenuBar;
   }

   /***
 * */
   private void setUpStartState(){
      Enumeration<AbstractButton> b = this.buttonGroup.getElements();
      while(b.hasMoreElements()){
         AbstractButton ab = b.nextElement();
         if(ab.getText().equals("Start") ||
            ab.getText().equals("Reset")){
            ab.setEnabled(false);
         }
         else{
            ab.setEnabled(true);
         }
      }
      b = this.menuItemGroup.getElements();
      while(b.hasMoreElements()){
         AbstractButton ab = b.nextElement();
         if(ab.getText().equals("Start") ||
            ab.getText().equals("Reset")){
            ab.setEnabled(false);
         }
         else{
            ab.setEnabled(true);
         }
      }
   }

   /**
 * */
   private void setUpStopState(){
      Enumeration<AbstractButton> b = this.buttonGroup.getElements();
      while(b.hasMoreElements()){
         AbstractButton ab = b.nextElement();
         if(ab.getText().equals("Stop") ||
            ab.getText().equals("Lap")){
            ab.setEnabled(false);
         }
         else{
            ab.setEnabled(true);
         }
      }
      b = this.menuItemGroup.getElements();
      while(b.hasMoreElements()){
         AbstractButton ab = b.nextElement();
         if(ab.getText().equals("Stop") ||
            ab.getText().equals("Lap")){
            ab.setEnabled(false);
         }
         else{
            ab.setEnabled(true);
         }
      }
   }

   /**
   */
   private void showAboutDialog(){
      String about = "StopWatch version 0.1\n";
      about += "Copyright (C) 2015 Lou Rosas\n";
      about += "This program comes with ABSOLUTELY NO WARANTY\n\n";
      about += "This program is free software: you can redistribute\n";
      about += "it and/or modify it under the terms of the GNU\n";
      about += "General Public License as published by the Free\n";
      about += "Software Foundation, either version 2 of the\n";
      about += "License, or (at your option) any later version.\n\n";
      about += "This program is distributed in the hope that it\n";
      about += "will be useful,but WITHOUT ANY WARRANTY; without\n";
      about += "even the implied warranty of MERCHANTABILITY or\n";
      about += "FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n";
      about += "General Public License for more details.\n\n";
      about += "You should have received a copy of the GNU General\n";
      about += "Public License along with this program.  If not,\n";
      about += "see <http://www.gnu.org/licenses/>.\n\n";
      about += "For questions or comments, please contact me at:\n";
      about += "lourosas@gmail.com";
      JTextArea textArea = new JTextArea(about);
      textArea.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(textArea);
      scrollPane.setPreferredSize(new Dimension(400, 300));
      JOptionPane.showMessageDialog(null, scrollPane, "Stop Watch",
                                     JOptionPane.INFORMATION_MESSAGE);
   }

   /**
   */
   private void showOverviewDialog(){
      String overView = "Basic Use of this Stop Watch.\n";
      overView += "This Stop Watch works like \"every other Stop\n";
      overView += "Watch\".  To Start, press the \"Start\" button.\n";
      overView += "To Stop, press the \"Stop\" button.  Pressing\n";
      overView += "the \"Lap\" button will Lap the Stop Watch and\n";
      overView += "display the laps.  The \"Reset\" button will\n";
      overView += "reset the Stop Watch, clear the Laps, and set\n";
      overView += "clear the display.\n\nThis Stop Watch keeps\n";
      overView += "track of time in the following display mode:\n";
      overView += "Days:Hours:Minutes:Seconds\nWhen the Stop Watch\n";
      overView += "is Stopped or Laps are displayed, Milliseconds\n";
      overView += "are also displayed.";
      new OverViewDialog(this, "Stop Watch Overview", overView);
   }
}
