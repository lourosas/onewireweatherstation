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
import java.text.DateFormat;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import myclasses.*;
import rosas.lou.clock.*;

public class TimerView extends GenericJFrame
implements TimeObserver{

   private static final short WIDTH  = 340;
   //private static final short HEIGHT = 145;
   private static final short HEIGHT = 160;

   private ButtonGroup buttonGroup;
   private ButtonGroup menuItemGroup;

   private JTextField  timeTF;

   private TimerController timercontroller = null;

   //*******************Constructor*********************************
   /*
   Constructor of no arguments
   */
   public TimerView(){
      this("", null);
   }

   /*
   Constructor taking the Title attribute and the Controller Object
   */
   public TimerView(String title, Object controller){
      super(title);
      //Go ahead and set up the GUI
      this.setUpGUI(controller);
      this.setResizable(false);
      this.setVisible(true);
   }

   //************************Public Methods*************************
   /*
   Add a timer, regardless of initialization
   */
   public void addTimer(LTimer t){
      t.addTimerObserver(this);
      this.timercontroller.addTimer(t);
   }
   
   /*
   Initialize the Timer
   */
   public void initialize(LTimer t){
      this.addTimer(t);
      this.timercontroller.initialize(t, this);
   }

   /*
   */
   public void resetPressed(){
      Enumeration<AbstractButton> e = this.buttonGroup.getElements();
      Enumeration<AbstractButton> m=this.menuItemGroup.getElements();
      while(e.hasMoreElements()){
         AbstractButton b = e.nextElement();
         if(b.getText().equals("Reset") ||
            b.getText().equals("Stop")){
            b.setEnabled(false);
         }
         else if(b.getText().equals("Start")){
            b.setEnabled(true);
         }
      }
      while(m.hasMoreElements()){
         AbstractButton b = m.nextElement();
         if(b.getText().equals("Reset") ||
            b.getText().equals("Stop")){
            b.setEnabled(false);
         }
         else if(b.getText().equals("Start")){
            b.setEnabled(true);
         }
      }
   }

   /*
   */
   public void startPressed(){
      Enumeration<AbstractButton> e = this.buttonGroup.getElements();
      Enumeration<AbstractButton> m=this.menuItemGroup.getElements();
      while(e.hasMoreElements()){
         AbstractButton b = e.nextElement();
         if(b.getText().equals("Start") ||
            b.getText().equals("Reset")){
            b.setEnabled(false);
         }
         else if(b.getText().equals("Stop")){
            b.setEnabled(true);
         }
      }
      while(m.hasMoreElements()){
         AbstractButton b = m.nextElement();
         if(b.getText().equals("Start") ||
            b.getText().equals("Reset")){
            b.setEnabled(false);
         }
         else if(b.getText().equals("Stop")){
            b.setEnabled(true);
         }
      }
   }

   /*
   */
   public void stopPressed(){
      Enumeration<AbstractButton> e = this.buttonGroup.getElements();
      Enumeration<AbstractButton> m=this.menuItemGroup.getElements();
      while(e.hasMoreElements()){
         AbstractButton b = e.nextElement();
         if(b.getText().equals("Start") ||
            b.getText().equals("Reset")){
            b.setEnabled(true);
         }
         else if(b.getText().equals("Stop")){
            b.setEnabled(false);
         }
      }
      while(m.hasMoreElements()){
         AbstractButton b = m.nextElement();
         if(b.getText().equals("Start") ||
            b.getText().equals("Reset")){
            b.setEnabled(true);
         }
         else if(b.getText().equals("Stop")){
            b.setEnabled(false);
         }
      }
   }

   /*
   Implementation of the updateTime() method in the TimeObserver
   interface
   */
   public void updateTime(String formatedTime){
      this.timeTF.setText(formatedTime);
   }

   /*
   Implementation of the updateTime(...) method in the TimeObserver
   interface
   */
   public void updateTime(long time){
      this.timeTF.setText("" + time);
   }
   
   /*
   Implementation of the TimeObserver interface
   */
   public void updateTime(int time){
      this.timeTF.setText("" + time);
   }
   
   /*
   Implementation of the TimeObserver interface
   */
   public void updateTime(Date date){
      this.timeTF.setText("" + date);
   }
   
   /*
   Implementation of the TimeObserver interface
   */
   public void updateTime(DateFormat dateFormat){}
   
   /*
   Implementation of the TimeObserver interface
   */
   public void updateTime(TimeObject timeObject){}
   

   //************************Private Methods************************
   /*
   */
   private JPanel setCenterPanel(){
      final int RIGHT = SwingConstants.RIGHT;

      JPanel centerPanel = new JPanel();

      Border border = BorderFactory.createEtchedBorder();
      centerPanel.setBorder(border);
      centerPanel.setLayout(new GridLayout(0, 2));

      JLabel time = new JLabel("Time:  ", RIGHT);
      this.timeTF = new JTextField();
      this.timeTF.setEditable(false);

      centerPanel.add(time);
      centerPanel.add(this.timeTF);

      return centerPanel;
   }

   /*
   */
   private JPanel setNorthPanel(){
      String s = new String("Initial Timer");
      JPanel northPanel = new JPanel();
      JLabel northLabel = new JLabel(s, SwingConstants.CENTER);

      Border border = BorderFactory.createEtchedBorder();
      northPanel.setBorder(border);
      northPanel.add(northLabel);

      return northPanel;
   }

   /*
   */
   private JPanel setSouthPanel(Object controller){
      JPanel buttonPanel = new JPanel();
      
      this.buttonGroup = new ButtonGroup();

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
      stop.setEnabled(false);
      buttonPanel.add(stop);
      this.buttonGroup.add(stop);

      JButton reset = new JButton("Reset");
      reset.setMnemonic(KeyEvent.VK_R);
      reset.addActionListener((ActionListener)controller);
      reset.addKeyListener((KeyListener)controller);
      reset.setEnabled(false);
      buttonPanel.add(reset);
      this.buttonGroup.add(reset);

      JButton quit = new JButton("Quit");
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

      //Start Menu Item
      JMenuItem start = new JMenuItem("Start", 'S');
      start.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                             InputEvent.CTRL_MASK));
      start.addActionListener((ActionListener)controller);
      this.menuItemGroup.add(start);
      //Add Start to the File Menu
      file.add(start);

      //Stop Menu Item
      JMenuItem stop = new JMenuItem("Stop", 'T');
      stop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                                             InputEvent.CTRL_MASK));
      stop.addActionListener((ActionListener)controller);
      stop.setEnabled(false);
      this.menuItemGroup.add(stop);
      //Add Stop to the File Menu
      file.add(stop);

      //Reset Menu Item
      JMenuItem reset = new JMenuItem("Reset", 'R');
      reset.addActionListener((ActionListener)controller);
      reset.setEnabled(false);
      this.menuItemGroup.add(reset);
      //Add Reset tot the File Menu
      file.add(reset);

      file.addSeparator();

      //Quit Menu Item
      JMenuItem quit = new JMenuItem("Quit", 'Q');
      quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                             InputEvent.CTRL_MASK));
      quit.addActionListener((ActionListener)controller);

      this.menuItemGroup.add(quit);
      //Add Quit to the File Menu
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
   */
   private void setUpGUI(Object controller){
      try{
         UIManager.setLookAndFeel(
                  "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
         //UIManager.setLookAndFeel(
         //              UIManager.getSystemLookAndFeelClassName());
         //UIManager.setLookAndFeel(
         //              "javax.swing.plaf.metal.MetalLookAndFeel");
      }
      catch(Exception e){ e.printStackTrace(); }
      this.timercontroller = (TimerController)controller;
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
      contentPane.add(this.setNorthPanel(),  BorderLayout.NORTH);
      contentPane.add(this.setCenterPanel(), BorderLayout.CENTER);
      contentPane.add(this.setSouthPanel(controller),
                                                BorderLayout.SOUTH);
      //Set up the Menu Bar
      this.setJMenuBar(this.setUpMenuBar(controller));
   }
}
