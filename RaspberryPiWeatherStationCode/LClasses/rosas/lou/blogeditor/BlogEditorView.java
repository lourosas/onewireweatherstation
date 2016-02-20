/********************************************************************
********************************************************************/
package rosas.lou.blogeditor;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import myclasses.*;

/*******************************************************************
*******************************************************************/
public class BlogEditorView extends GenericJFrame{
   private static final short WIDTH  = 540;
   private static final short HEIGHT = 700;
   
   private ButtonGroup buttonGroup;
   private ButtonGroup menuItemGroup;
   private JTextField  titleTextField;
   private JTextField  headingTextField;
   private JTextArea   pageTextArea;
   private String      currentHeader;
   
   private boolean     completelyCleared;
   
   private java.util.List<String> titleList;
   private java.util.List<String> headerList; //Associate the Header
   //Associate the body text with the header
   private java.util.List<String> bodyList;
   
   //************************Public Methods*************************
   /**
   */
   public BlogEditorView(){
      this("");
   }
   
   /**
   */
   public BlogEditorView(String title){
      super(title);
      
      this.completelyCleared = false;
      
      this.titleList  = new LinkedList<String>();
      this.headerList = new LinkedList<String>();
      this.bodyList   = new LinkedList<String>();
      this.setUpGUI();
      this.setResizable(false);
      this.setVisible(true);
   }
   
   //*********************Private Methods***************************
   /**
   */
   private void appendBody(PrintWriter pw, String body){
      if(body.length() > 0){
         Scanner sc = new Scanner(body).useDelimiter("\\n");
         pw.print("<p>\n");
         while(sc.hasNext()){
            String s = sc.next();
            if(s.length() > 0){
               pw.print(s + "\n");
            }
            else{
               pw.print("</p>\n");
               pw.print("<p>\n");
            }
         }
         pw.print("</p>\n");
         sc.close();
      }
   }
   
   /**
   */
   private void clearEverything(){
      this.completelyCleared = true;
      this.titleList.clear();
      this.headerList.clear();
      this.bodyList.clear();
      this.titleTextField.setText("");
      this.headingTextField.setText("");
      this.pageTextArea.setText("");
      this.titleTextField.requestFocus();
      this.titleTextField.selectAll();
   }
   
   /**
   */
   private void grabCurrentHeaderAndBody(){
      int index = 
            this.headerList.indexOf(this.headingTextField.getText());
      try{
         this.bodyList.set(index, this.pageTextArea.getText());
      }
      catch(IndexOutOfBoundsException e){
         this.headerList.add(this.headingTextField.getText());
         this.bodyList.add(this.pageTextArea.getText());
      }
      finally{
         this.completelyCleared = false;
      }
   }
   
   /**
   */
   private void grabCurrentTitle(){
      int index =
               this.titleList.indexOf(this.titleTextField.getText());
      try{
         this.titleList.set(index, this.titleTextField.getText());
      }
      catch(IndexOutOfBoundsException e){
         this.titleList.add(this.titleTextField.getText());
      }
      finally{
         this.completelyCleared = false;
      }
   }
   
   /**
   */
   private void setHeader(PrintWriter pw, String header){
      pw.print("\n");
      pw.print("<h2 align = \"center\">\n");
      pw.print(header + "\n");
      pw.print("</h2>\n");
      pw.print("\n");
   }
   
   /**
   */
   private void setUpGUI(){
      //Get the content pane
      Container contentPane = this.getContentPane();
      //Get the screen dimensions
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      //Set the size
      this.setSize(WIDTH, HEIGHT);
      //Set up the location
      this.setLocation((int)((dim.getWidth()  - WIDTH)/2),
                       (int)((dim.getHeight() - HEIGHT)/2));
      //Set up the rest of the GUI
      contentPane.add(this.setNorthPanel(),    BorderLayout.NORTH);
      contentPane.add(this.setCenterPanel(),   BorderLayout.CENTER);
      contentPane.add(this.setUpButtonPanel(), BorderLayout.SOUTH);
      //Set up the menu bar
      this.setJMenuBar(this.setUpMenuBar());
   }
   
   /**
   */
   private JPanel setCenterPanel(){
      this.headingTextField    = new JTextField();
      this.headingTextField.setColumns(35);
      String headingString     = new String("Current Heading  ");
      JPanel northCenterPanel  = new JPanel();
      JPanel centerCenterPanel = new JPanel();
      JPanel centerPanel       = new JPanel();
      centerPanel.setLayout(new BorderLayout());
      centerPanel.setBorder(BorderFactory.createEtchedBorder());
      
      northCenterPanel.setBorder(
                       BorderFactory.createEmptyBorder(10,10,10,10));
      JLabel headingLabel
                  = new JLabel(headingString, SwingConstants.CENTER);
      northCenterPanel.add(headingLabel);
      northCenterPanel.add(this.headingTextField);
      centerPanel.add(northCenterPanel, BorderLayout.NORTH);
      
      this.pageTextArea = new JTextArea(28, 35);
      this.pageTextArea.setLineWrap(true);
      this.pageTextArea.setWrapStyleWord(true);
      this.pageTextArea.setTabSize(3);
      JScrollPane scrollPane = new JScrollPane(this.pageTextArea);
      centerCenterPanel.add(scrollPane);
      centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
      return centerPanel;
   }
   
   /**
   */
   private JPanel setNorthPanel(){
      //Set the title of the current html page
      this.titleTextField = new JTextField();
      this.titleTextField.setColumns(35);
      String titleString  = new String("Set Title  ");
      JPanel northPanel   = new JPanel();
      JLabel titleLabel
                    = new JLabel(titleString, SwingConstants.CENTER);
      //Set up the border
      northPanel.setBorder(
                       BorderFactory.createEmptyBorder(10,10,10,10));
      northPanel.add(titleLabel);
      northPanel.add(this.titleTextField);
      
      return northPanel;
   }
   
   /**
   */
   private JPanel setUpButtonPanel(){
      JPanel buttonPanel = new JPanel();
      buttonPanel.setBorder(
                        BorderFactory.createEmptyBorder(15,10,10,1));
      this.buttonGroup = new ButtonGroup();
      
      JButton clear   = new JButton("Clear");
      clear.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            clearEverything();
         }
      });
      JButton save = new JButton("Save");
      save.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            saveToFile();
            
         }
      });
      JButton quit    = new JButton("Quit");
      quit.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            System.exit(0);
         }
      });
      
      this.buttonGroup.add(clear);
      this.buttonGroup.add(save);
      this.buttonGroup.add(quit);
      
      buttonPanel.add(clear);
      buttonPanel.add(save);
      buttonPanel.add(quit);
      
      return buttonPanel;
   }
   
   /**
   */
   private JMenu setUpFileMenu(){
      JMenu file = new JMenu("File");
      file.setMnemonic(KeyEvent.VK_F);
      
      //Clear Menu Item
      JMenuItem clear = new JMenuItem("Clear", 'C');
      clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                                              InputEvent.CTRL_MASK));
      clear.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            clearEverything();
         }
      });
      file.add(clear);
      this.menuItemGroup.add(clear);
      
      //Save Menu Item
      JMenuItem save = new JMenuItem("Save", 'S');
      save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                              InputEvent.CTRL_MASK));
      save.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            /*System.out.println(titleTextField.getText());
            System.out.println(headingTextField.getText());
            System.out.println(pageTextArea.getText());*/
            saveToFile();
         }
      });
      file.add(save);
      this.menuItemGroup.add(save);
      
      file.addSeparator();
      
      JMenuItem quit = new JMenuItem("Quit", 'Q');
      quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                              InputEvent.CTRL_MASK));
      quit.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            System.exit(0);
         }
      });
      file.add(quit);
      this.menuItemGroup.add(quit);
      
      return file;
   }
   
   /**
   */
   private JMenuBar setUpMenuBar(){
      JMenuBar jmenuBar = new JMenuBar();
      
      this.menuItemGroup = new ButtonGroup();
      
      //Set up the File Menu
      jmenuBar.add(this.setUpFileMenu());
      return jmenuBar;
   }
   
   /**
   */
   private void saveToFile(){
      JFileChooser jfc = new JFileChooser();
      int data = jfc.showSaveDialog(this);
      if(data == JFileChooser.APPROVE_OPTION){
         File f = jfc.getSelectedFile();
         this.grabCurrentTitle();
         this.grabCurrentHeaderAndBody();
         this.setUpData(f);
      }
   }
   
   /**
   */
   private void setTitle(PrintWriter pw, String title){
      pw.print("<html>\n");
      pw.print("<head>\n");
      pw.print("<title>\n");
      pw.print(title + "\n");
      pw.print("</title>\n");
      pw.print("</head>\n");
      pw.print("<body>\n");
   }
   
   /**
   */
   private void setUpData(File f){
      try{
         FileWriter  fw = new FileWriter(f, false);
         PrintWriter pw = new PrintWriter(fw, true);
         //Should be only one title
         for(int i = 0; i < this.titleList.size(); i++){
            this.setTitle(pw, this.titleList.get(i));
         }
         for(int j = 0; j < this.headerList.size(); j++){
            this.setHeader(pw, this.headerList.get(j));
            this.appendBody(pw, this.bodyList.get(j));
         }
         pw.print("</body>\n");
         pw.print("</html>");
         pw.close();
         fw.close();
      }
      catch(IndexOutOfBoundsException e){
         e.printStackTrace();
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
   }
}