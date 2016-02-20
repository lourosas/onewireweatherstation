package weatherclasses;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import myclasses.*;
import weatherclasses.*;

/*****************************************************************************
** A class by Lou Rosas
** Initial release: 10/02/01
** Major changes due to the "First Addendum"
** "First Addendum" release: 11/25/01
** New methods were added to accomodate the "First Addendum" changes.  These
** Are clearly marked in the header of each.
** Other minor changes due to the "First Addendum" additions such as changes
** to methods and additions of attributes local to the class and/or a
** particular method are not so well marked.  This error previously mentioned
** will be corrected in the next improvements release, "Second Addendum".
** This will include an addition to the main header with a date comment,
** as well as additions to the headers of the methods affected by the changes.
*****************************************************************************/
public class HeatIndexCalculatorView extends GenericJFrame implements Observer{
    public static final int DEGREES_C = 1;
    public static final int DEGREES_F = 2;

    private static final int WIDTH   = 250;//this value can change
    private static final int HEIGHT  = 180;//this value can change

    //These were made visible to the class so other methods can
    //get to them easily
    private JPanel centerPanel;
    private JLabel tempLabel;
    private JLabel humidityLabel;
    private JLabel heatindexLabel;

    private JTextField temperatureText;
    private JTextField humidText;
    private JTextField heatindexTF;

    private JLabel tempShowLabel;
    private JLabel humidityShowLabel;
    private JLabel heatindexShowLabel;

    private int view_value; //set this to the view mode (degree C or degree F)
    private HeatIndexCalculatorController hicc;//the controller of the model

    /*************************************************************************
    **  The constructor
    *************************************************************************/
    public HeatIndexCalculatorView(HeatIndexCalculator hic){
        super("Heat Index Calculator");
        this.setResizable(false);
        this.view_value = DEGREES_F; //default setting (American Users)
        hicc = new HeatIndexCalculatorController(hic,this);
        this.setUpGui();
        this.setVisible(true);
    }

    /*************************************************************************
    **  This is perhaps the most complex of all the methods.
    **  this method has got to determine if the value put into the
    **  JText field is a true number (be it int or double).
    **  If this is not a true number, a Dialog box comes up requesting
    **  an actual number.  If there is no string at all in the Text field,
    **  another Dialog box appears with the request that the all the
    **  fields be filled out--this will be individuallized to the 
    **  particular text field as needed.  This will go on until a valid
    **  string is entered in ALL text fields that be converted into
    **  numbers.  In addition, negative values will be checked based on
    **  the text field.  The Temperature text field is capable of
    **  negative values, but the Humidity field is not.  I don't know
    **  if I will create an Exception to throw on this type, or I will
    **  surround it inside a branch statement block.
    **  In addition, this method SHOULD probably have an activity diagram
    **  associated with it in the design (As well as the design being
    **  updated as needed).
    **************************************************************************/
    public HeatIndexData requestData(){
        double  temp_value;
        double  humidity_value;
        boolean repeat = true;
        int     text_field_count = 0;
        String  title = "Input Error";
        String  error_temp = "This calculator works best with temperature ";
        HeatIndexData returnData = null;
        try{
            text_field_count++;
            String temp = temperatureText.getText();
            temp_value = Double.parseDouble(temp);
            text_field_count++;
            String humidity = humidText.getText();
            humidity_value = Double.parseDouble(humidity);
            //need to deal with negative humidity numbers
            if(temp_value < 78.0 && view_value == DEGREES_F){
                String s = error_temp + "greater than 77 " + '\u00B0' + 'F';
                JOptionPane.showMessageDialog
                           (this,s,title,JOptionPane.ERROR_MESSAGE); 
                temperatureText.selectAll();
                temperatureText.requestFocus();
            }
            else if(temp_value < 25 && view_value == DEGREES_C){
                String s = error_temp + "greater than 24 " + '\u00B0' + 'C';
                JOptionPane.showMessageDialog
                           (this,s,title,JOptionPane.ERROR_MESSAGE);
                temperatureText.selectAll();
                temperatureText.requestFocus();
            }
            else if(humidity_value < 0 || humidity_value > 100){
                String s = "Humidity values must be";
                if(humidity_value < 0)
                    s = s + " greater than zero";
                else
                    s = s + " less than 100%";
                JOptionPane.showMessageDialog
                                  (this,s,title,JOptionPane.ERROR_MESSAGE);
                humidText.selectAll();
                humidText.requestFocus();
            }
            else
                returnData = new HeatIndexData(temp_value,humidity_value);
        }
        catch(NumberFormatException nfe){
            String s = "A number is needed";
            if(text_field_count == 1){
                String temp = new String(s + " for temperature input");
                JOptionPane.showMessageDialog
                                 (this,temp,title,JOptionPane.ERROR_MESSAGE);
                temperatureText.selectAll();
                temperatureText.requestFocus();
            }
            else{
                 String humid = new String(s + " for humidity input");
                 JOptionPane.showMessageDialog
                                 (this,humid,title,JOptionPane.ERROR_MESSAGE);
                 humidText.selectAll();
                 humidText.requestFocus();
            }
        }
        finally{
            return returnData;
        }
    }


    /*************************************************************************
    **  Part of the "First Addendum" changes.  This gets all the data to be
    **  saved off (includind the calculated heat index).  If not all the data
    **  That is required to be saved is present, a simple dialog box will pop
    **  up and and indicate that all the data is needed before anything can be
    **  saved off.  It is assumed that valid data is in the text fields before
    **  anything is saved off.
    *************************************************************************/
    public HeatIndexCalculatedData requestDataToSave(){
        double temperature      = 0;
        double humidity         = 0;
        double heat_index       = 0;
        int    text_field_count = 0;
        String title            = "Save off error";
        String temp_string      = null;
        String constant_part    = " before any thing can be saved off.";

        HeatIndexCalculatedData returnData = null;

        try{
            temperature = Double.parseDouble(temperatureText.getText());
            text_field_count++;
            humidity = Double.parseDouble(humidText.getText());
            text_field_count++;
            heat_index = Double.parseDouble(heatindexTF.getText());
            returnData = 
                 new HeatIndexCalculatedData(temperature,humidity,heat_index);
        }
        catch(NumberFormatException nfe){
            if(text_field_count == 0){
                temp_string = "Temperature input is needed";
                temperatureText.selectAll();
                temperatureText.requestFocus();
            }
            else if(text_field_count == 1){
                temp_string = "Humidity input is needed";
                humidText.selectAll();
                humidText.requestFocus();
            }
            else{
                temp_string = "A heat index must be calculated";
                heatindexTF.selectAll();
                heatindexTF.requestFocus();
            }
            temp_string = temp_string + constant_part;
            JOptionPane.showMessageDialog
                        (this,temp_string,title,JOptionPane.ERROR_MESSAGE);
        }
        finally{
            return returnData;
        }
    }

    /************************************************************************
    **  Since there are only three text fields, just hard
    **  code the clearing process in.  If there were more, should
    **  just get the text fields individually and clear them.
    ************************************************************************/
    public void clearTextFields(){  
        temperatureText.setText("");
        humidText.setText("");
        heatindexTF.setText("");
        temperatureText.requestFocus();
    }

    /*************************************************************************
    **  Part of the "first addendum" additions.  This method adds the
    **  the functionality to not only clear out the text fields, but to
    **  get the data first and put it into a HeatIndexData object to be
    **  sent to the model to recalculate the heat index value in the new
    **  scale.  This is implemented in this fashion so as to preserve the
    **  Model-View-Controller architecture.  If the data in the text fields
    **  are not "valid numbers" the Double.MAX_VALUE is put in them.  This
    **  is the indecation to the Model and view objects that valid data is
    **  NOT in these text fields and to set them blank.
    *************************************************************************/
    public HeatIndexCalculatedData resetScale(int viewValue){
        //get the data from the text fields
        double temp_value       = Double.MAX_VALUE;
        double humidity_value   = Double.MAX_VALUE;
        double heat_index_value = Double.MAX_VALUE;

        HeatIndexCalculatedData return_data = null;

        try{
            temp_value = Double.parseDouble(temperatureText.getText());
        }
        catch(NumberFormatException nfe){
            temp_value = Double.MAX_VALUE;
        }
        try{
            humidity_value = Double.parseDouble(humidText.getText());
        }
        catch(NumberFormatException nfe){
            humidity_value = Double.MAX_VALUE;
        }
        try{
            heat_index_value = Double.parseDouble(heatindexTF.getText());
        }
        catch(NumberFormatException nfe){
            heat_index_value = Double.MAX_VALUE;
        }
        catch(Exception e){e.printStackTrace();}
        finally{
            this.resetView(viewValue);
            return return_data = new HeatIndexCalculatedData
                                     (temp_value,
                                      humidity_value,
                                      heat_index_value
                                     );
        }
    }

    /*************************************************************************
    **  This method resets the view to either Fahreheit or Celsius.
    **  By default, the view is set to Fahrenheit.  The value that controls 
    **  what is to be displayed is set and the label are changed accordingly
    *************************************************************************/
    public void resetView(int viewValue){
        this.clearTextFields();
        view_value = viewValue;
        if(view_value == DEGREES_C){
            tempShowLabel.setText(""+'\u00B0' + 'C');
            heatindexShowLabel.setText(""+'\u00B0' + 'C');
            JMenuBar  jmb = this.getJMenuBar();
            JMenu     jm  = jmb.getMenu(1);//get the Scale Menu
            (jm.getItem(0)).setEnabled(true);
            (jm.getItem(1)).setEnabled(false);
        }
        else{//in Farhenheit mode
            tempShowLabel.setText(""+'\u00B0' + 'F');
            heatindexShowLabel.setText(""+'\u00B0' + 'F');
            JMenuBar   jmb = this.getJMenuBar();
            JMenu      jm = jmb.getMenu(1);
            (jm.getItem(0)).setEnabled(false);
            (jm.getItem(1)).setEnabled(true);
        }
    }

    /*************************************************************************
    **  Part of the "first addendum" additions.  This method shows a Help
    **  Dialog to guide the user on how to use the program.
    *************************************************************************/
    public void showHelpDialog(){
        String help = "1) This application calculates the heat index ";
        help = help + "given the temperature and relative ";
        help = help + "humidity.\n\n";
        help = help + "2) The heat index is calculated in either degrees ";
        help = help + "Celsius or Fahrenheit.\n\n";
        help = help + "3) Both the temperature and ";
        help = help + "relative humidity must be input before a calculation\n";
        help = help + "can take place.\n\n";
        help = help + "4) The user can choose to calculate ";
        help = help + "the heat index,\nclear the text values or quit the ";
        help = help + "application via button selections on the panel.\n\n";
        help = help + "5) These selections are also available as menu ";
        help = help + "selctions. \n\n";
        help = help + "6) In addition, the user can save the calculated data,";
        help = help + " toggle between scales\n(Celsius and Fahrenheit), and ";
        help = help + "find out about this\napplication or get help if ";
        help = help + "needed via menu selections.\n\n";
        help = help + "7) To navigate between text fields, use the ";
        help = help + "<Tab> button. Pressing the <Enter> button\nwill ";
        help = help + "calculate the heat index:  provided all the correct ";
        help = help + "information is entered\n(or you could use the button ";
        help = help + "or menu selections).\n\n";
        help = help + "8) Saving the the information appends ";
        help = help + "the data to a big *.sql file which is saved directly";
        help = help + "\nat the root level on the C";
        help = help + " drive: using a date stamp to differentiate between the";
        help = help + " \ndifferent files. The files are separated based on ";
        help = help + "date, once the date\nchanges, a new file is created ";
        help = help + "and heat index data for that\ndate is collected in ";
        help = help + "its own separate file (this is done automatically).\n";
        help = help + "Data is saved in *.sql format for the user to have ";
        help = help + "the\nability to place the data in a data base of their";
        help = help + " liking.\nThe data from all the different *.sql files";
        help = help + " could ";
        help = help + "be stored in one\nlarge data base file. ";
        JOptionPane.showMessageDialog
            (this,help,"Help!",JOptionPane.INFORMATION_MESSAGE);
    }
    
    /*************************************************************************
    **  An addition so insignificant that it was not worthy to be put on the
    **  "first addendum" additions.  This method shows who created this
    **  application.
    *************************************************************************/
    public void showAboutDialog(){
        String about = "The Heat Index Calculator, ";
        about = about + "an application by Lou Rosas";
        JOptionPane.showMessageDialog
            (this,about,"About",JOptionPane.INFORMATION_MESSAGE);
    }
    
    /*************************************************************************
    **  Once the model is done crunching the data, it will
    **  notify all Observers, which calls this method here.  In this
    **  particular manifistation, I am including the actual Object to 
    **  be displayed. If the incoming Object is NULL, that means
    **  the application is quiting and is giving the GUI the chance
    **  to "bow-out" gracefully.
    *************************************************************************/
    public void update(Observable o, Object arg){
        double DIVIDE_FACTOR = 10.0;
        if(arg instanceof String){
            String heat_index_value = (String)arg;
            heatindexTF.setText(heat_index_value);
            heatindexTF.requestFocus();
        }
        else if(arg instanceof Double || arg instanceof Integer){
            if(arg instanceof Double){
                double hi = ((Double)arg).doubleValue();
                heatindexTF.setText("" + hi);
            }
            else{
                int h_i = ((Integer)arg).intValue();
                heatindexTF.setText("" + h_i);
            }
            heatindexTF.requestFocus();
        }
        else if(arg instanceof HeatIndexCalculatedData){
            this.setConvertedValues(arg);
        }
        else{
             //if the Object is not of the types above, then
             //either there is something drastically wrong w/the application
             //(And this is a good way of finding that out)
             //or the Object is null, in which the application is quiting as
             //designed
             this.setVisible(false);
        }         
    }

    /*************************************************************************
    **  Part of the "First Addendum" additions.  This set the converted
    **  that are updated via the Model (HeatIndexCalculator).  If the current
    **  view is in Fahrenheit mode, the values are converted to integers and
    **  displayed.  If in Celsius mode, the values are left as doubles to be
    **  displayed.  The MAX_VALUE is the marker to indicate that particular
    **  and individual incomming value is not valid.  If that is the case,
    **  no value is shown and the corresponding text field is left blank.
    *************************************************************************/
    private void setConvertedValues(Object arg){
        if(arg instanceof HeatIndexCalculatedData){
            HeatIndexCalculatedData hicd = (HeatIndexCalculatedData)arg;
            double temp          = hicd.getTemperature();
            double humidity      = hicd.getHumidity();
            double heat_indx     = hicd.getHeatIndex();
            int    heat_indx_int = hicd.getHeatIndexInt();

            if(temp != Double.MAX_VALUE){
                if(view_value == DEGREES_F)
                    temperatureText.setText("" + (int)temp);
                else
                    temperatureText.setText("" + temp);
            }
            else
                temperatureText.setText("");

            if(humidity != Double.MAX_VALUE)
                humidText.setText("" + (int)humidity);
            else
                humidText.setText("");

            if(heat_indx != Double.MAX_VALUE){
                if(view_value == DEGREES_F)
                    heatindexTF.setText("" + heat_indx_int);
                else
                    heatindexTF.setText("" + heat_indx);
            }
            else
                heatindexTF.setText("");
        }
    }

    /*************************************************************************
    **  This is the method that sets up the GUI.  All it actully does
    **  is call the other methods that actually make up the GUI.
    *************************************************************************/
    private void setUpGui(){
        //set up the frame
        this.setSize(WIDTH,HEIGHT);
        this.setLocation(WIDTH,HEIGHT);
        JPanel contentPane = (JPanel)this.getContentPane();

        //set up the menubar and menus with menu items
        this.setJMenuBar(this.setUpMenu());
        //set up the center panel labels and text fields
        contentPane.add("Center",this.setUpCenterPanel());
        //set up the button panel
        contentPane.add("South",this.setUpButtonPanel());      
    }

    /*************************************************************************
    **  This sets up the menu bar, and all the menus, it is pretty long
    **  in future developments, I will try to remember the "unwritten" rule
    **  of programming:  methods should not be longer than a screen length.
    **  But, because this method is so involved, it took up a lot of space.
	**  As part of the "First Addendum" additions, a "Szve" menu item was
	**  added to the "File" menu.
    *************************************************************************/
    private JMenuBar setUpMenu(){
        JMenuBar jmb = new JMenuBar();
        //File menu
        JMenu file = new JMenu("File");
        file.setMnemonic('F');//set up the ALT+F command
        //File menu items
        JMenuItem calculate = new JMenuItem("Calculate",'C');
        calculate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK));
        calculate.addActionListener(hicc);
        file.add(calculate);
        JMenuItem clear = new JMenuItem("Clear",'L');
        clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,InputEvent.CTRL_MASK));
        clear.addActionListener(hicc);
        file.add(clear);
        file.addSeparator();
        JMenuItem save = new JMenuItem("Save", 'S');
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
        save.addActionListener(hicc);
        file.add(save);
        file.addSeparator();
        JMenuItem exit = new JMenuItem("Exit",'X');
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_MASK));
        exit.addActionListener(hicc);
        file.add(exit);
        jmb.add(file);
        //Scale menu
        JMenu scale = new JMenu("Scale");
        scale.setMnemonic('S');
        //Scale menu items
        //one of these menu items needs to be selected and/or deselected
        JMenuItem Fahrenheit = new JMenuItem("Fahrenheit",'F');
        Fahrenheit.addActionListener(hicc);
        Fahrenheit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
        scale.add(Fahrenheit);
        JMenuItem Celsius = new JMenuItem("Celsius",'C');
        Celsius.addActionListener(hicc);
        Celsius.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
        scale.add(Celsius);
        jmb.add(scale);
        //Help menu
        JMenu help = new JMenu("Help");
        help.setMnemonic('H');
        //Help menu items
        JMenuItem help_selection = new JMenuItem("Help",'H');
        help_selection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
        help_selection.addActionListener(hicc);
        help.add(help_selection);
        help.addSeparator();
        JMenuItem about = new JMenuItem("About",'A');
        about.addActionListener(hicc);
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,0));
        help.add(about);
        jmb.add(help);
        if(view_value == DEGREES_C){
            Fahrenheit.setEnabled(true);
            Celsius.setEnabled(false);
        }
        else{
            Fahrenheit.setEnabled(false);
            Celsius.setEnabled(true);
        }
        return jmb;
    }

    /*************************************************************************
    **  This method sets up the center panel.  This is the one that holds
    **  all the text fields and lables that are displayed.  It was decided to
    **  set it up as a GridBagLayout:  for the purpose of putting everything
    **  where I wanted it, and setting it up to have somewhat a "visualy
    **  astetic apeal" (I wanted it to look visually apealing to users).  
    **  This method is too long, also.  This method should be broken up into
    **  three, posisbly four other methods.
    *************************************************************************/
    private JPanel setUpCenterPanel(){
        //Set up the Center Panel that takes everyting
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        centerPanel.setOpaque(true);

        //Set up the components to go in the Center Panel
        //Temperature Label
        tempLabel = new JLabel("Temperature");
        tempLabel.setOpaque(true);
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 100;
        gbc.weighty = 100; //don't make either one expand
        this.add(centerPanel, tempLabel,gbc,0,0,1,1);

        //Temperature Text Field Somehow, need to figure out a way to
        //limit the number of text entries to 3 (perhaps throw
        //own defined exception)
        temperatureText = new JTextField();
        temperatureText.setName("Temperature");
        temperatureText.setColumns(3);
        temperatureText.addActionListener(hicc);
        temperatureText.addKeyListener(hicc);
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        this.add(centerPanel, temperatureText,gbc,1,0,1,1);

        //Temperature Show Label
        tempShowLabel = new JLabel();
        tempShowLabel.setOpaque(true);
        if(view_value == DEGREES_C) 
        tempShowLabel.setText(""  + '\u00B0' + 'C');
        else tempShowLabel.setText("" + '\u00B0' + 'F');
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(centerPanel,tempShowLabel,gbc,2,0,1,1);

        //Humidity Label
        humidityLabel = new JLabel("Relative Humididty");
        humidityLabel.setOpaque(true);
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(centerPanel,humidityLabel,gbc,0,1,1,1);

        //Humidity Text Field Somehow need to figure out a way to
        //limit the number of text entries to 2(perhaps throw 
        //own defined exception)
        humidText = new JTextField();
        humidText.setName("Humidity");
        humidText.addActionListener(hicc);
        humidText.addKeyListener(hicc);
        humidText.setColumns(3);
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        this.add(centerPanel,humidText,gbc,1,1,1,1);

        //Humidity Show Label
        humidityShowLabel = new JLabel(" % ");
        humidityShowLabel.setOpaque(true);
        gbc.anchor = GridBagConstraints.WEST;
        this.add(centerPanel,humidityShowLabel,gbc,2,1,1,1);

        //Heat Index Label
        heatindexLabel = new JLabel("Heat Index");
        heatindexLabel.setOpaque(true);
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(centerPanel,heatindexLabel,gbc,0,2,1,1);

        //Heat Index Text Field
        heatindexTF = new JTextField();
        heatindexTF.setName("Heat Index");
        heatindexTF.setEditable(false);
        heatindexTF.setColumns(3);
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        this.add(centerPanel,heatindexTF,gbc,1,2,1,1);

        //Heat Index Show Label
        heatindexShowLabel = new JLabel();
        heatindexShowLabel.setOpaque(true);
        if(view_value == DEGREES_C)
            heatindexShowLabel.setText(""+'\u00B0' + 'C');
        else heatindexShowLabel.setText(""+'\u00B0' + 'F');
        gbc.anchor = GridBagConstraints.WEST;
        this.add(centerPanel,heatindexShowLabel,gbc,2,2,1,1);
        return centerPanel;
    }

    /*************************************************************************
    **  This sets up the Button panel at the bottom of the frame
    *************************************************************************/
    private JPanel setUpButtonPanel(){
        JPanel buttonPanel = new JPanel();
        //Quit Button
        JButton quitButton = new JButton("Quit");
        quitButton.setMnemonic('Q');
        quitButton.addActionListener(hicc);
        quitButton.addKeyListener(hicc);
        buttonPanel.add(quitButton);
        //Caluculate Button
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setMnemonic('C');
        calculateButton.addActionListener(hicc);
        calculateButton.addKeyListener(hicc);
        buttonPanel.add(calculateButton);
        //Clear Button
        JButton clearButton = new JButton("Clear");
        clearButton.setMnemonic('L');
        clearButton.addActionListener(hicc);
        clearButton.addKeyListener(hicc);
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

    /*************************************************************************
    **  Dealing with GridBagLayouts is relatively complex.  This method was
    **  included to make dealing with adding the components to the
    **  GridBagLayout less complicated.
    *************************************************************************/
    private void add(Container c, Component toAdd, GridBagConstraints gbc, 
      int x, int y, int width, int height){
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth  = width;
        gbc.gridheight = height;
        c.add(toAdd,gbc);
    }
}