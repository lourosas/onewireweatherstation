package weatherclasses;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import myclasses.*;
import weatherclasses.*;

/*****************************************************************************
** A class by Lou Rosas (who has no class)
** Initial realse: 10/02/01
** No major addendums added to the code:  just continuations of functionality
** made post-initial realease throughout the months of October and Novemeber
** of 2001. The main change was the addition of the HeatIndexCalculatedData
** object.
** This class is the Controller of the HeatIndexCalculator Application.
** It controlls the model as well as the view.  This listens for all events
** related to the view.  All events that require attention by the
** HeatIndexCalculator application are handled by this ADT.
*****************************************************************************/

public class HeatIndexCalculatorController 
implements ActionListener, KeyListener{
    private HeatIndexCalculatorView  the_view;
    private HeatIndexCalculator      the_model;
    
    private int       typed_amount = 0;
    private int       max_amount;
    private int       max_amount_minus;
    private boolean   decimal_entered;

    /*************************************************************************
    **  The constructor
    *************************************************************************/
    public HeatIndexCalculatorController
           (HeatIndexCalculator hic, HeatIndexCalculatorView hicv){
        the_view                = hicv;
        the_model               = hic;
    }

    /*************************************************************************
    **  This actionPerformed method is implemented in such a way as to
    **  determine the source of the event and pass it on the the appropriate
    **  method.  This was done in this fashion as a way to keep the method
    **  from exploding.
    *************************************************************************/
    public void actionPerformed(ActionEvent e){
        Object o = e.getSource();
        if(o instanceof JButton){
            this.handleJButton((JButton)o);
        }
        else if(o instanceof JMenuItem){
            this.handleJMenuItem((JMenuItem)o);            
        }
        else if(o instanceof JTextField){
            this.handleJTextField((JTextField)o);
        }
    }

    /*************************************************************************
    **  This keyPressed method implementation only handles when the <Enter>
    **  button is pressed inside of a JButton.  I decided to call "doClick()"
    **  because the method will go through all the steps as if a user
    **  clicked the button.  This seems like a better user response than
    **  just handling the event outright and calling the appropriate method.
    *************************************************************************/
    public void keyPressed(KeyEvent k){
        int key_code = k.getKeyCode();
        Object arg = k.getSource();
        if(arg instanceof JButton){
            JButton jb = (JButton)arg;
            if(key_code == KeyEvent.VK_ENTER){
                jb.doClick();
            }
       }
    }

    /*************************************************************************
    **  nothing implemented here.
    *************************************************************************/
    public void keyReleased(KeyEvent k){}

    /*************************************************************************
    **  This keyTyped method implementation is used to limit the number of
    **  characters that can be typed in the JTextField that has current focus
    **  if there is a '-' in front, that number increases by one.  If there
    **  is a decimal pressed, that number increases by one.  There is a
    **  separate comparison number when negative numbers are compared as
    **  opposed to positive numbers (for negative numbers, the comparison
    **  number is greater by one, this is to insure the same amount of digits
    **  can be entered regardless).
    *************************************************************************/
    public void keyTyped(KeyEvent k){
        Object arg = k.getSource();
        if(arg instanceof JTextField){
            JTextField jtf = (JTextField)arg;
            try{
                char ch = k.getKeyChar();
                String s = jtf.getText();
                typed_amount = s.length();
                if(s.charAt(typed_amount-1)=='.' && !decimal_entered){
                    max_amount++;
                    max_amount_minus++;
                    decimal_entered = true;
                }
                if(s.charAt(0) == '-'){
                    if(typed_amount > max_amount_minus &&
                      !(Character.isISOControl(ch)))
                        k.consume();
                }
                else{
                    if(typed_amount > max_amount && 
                      !(Character.isISOControl(ch)))
                        k.consume();
                }
            }
            catch(NullPointerException n){
                n.printStackTrace();
                typed_amount = 0;
                max_amount = 2;
                max_amount_minus = 3;
                decimal_entered = false;
            }
            catch(IndexOutOfBoundsException e){
                typed_amount = 0;
                max_amount = 2;
                max_amount_minus = 3;
                decimal_entered = false;
            }
        }
    }

    /*************************************************************************
    **  This handles ActionEvents that originated from a JButton
    *************************************************************************/
    private void handleJButton(JButton jb){
        HeatIndexData  current_data = null;
        String selection = jb.getText();
        if(selection.equals("Quit"))
            the_model.quit();
        else if(selection.equals("Calculate")){
            current_data = the_view.requestData();
            //the only time to process data is when there is data
            if(current_data != null)
                the_model.calculateHeatIndex(current_data);
        }
        else if(selection.equals("Clear"))
            the_view.clearTextFields();
        else ;
    }

    /*************************************************************************
    **  This handles ActionEvents originating from a JMenuItem
    *************************************************************************/
    private void handleJMenuItem(JMenuItem jmi){
        HeatIndexData            current_data            = null;
        HeatIndexCalculatedData  current_calculated_data = null;
        String selection = jmi.getText();
        if(selection.equals("Calculate")){
            current_data = the_view.requestData();
            if(current_data != null)
                the_model.calculateHeatIndex(current_data);
        }
        else if(selection.equals("Clear")){
            the_model.clearData();
            the_view.clearTextFields();
        }
        else if(selection.equals("Save")){
            current_calculated_data = the_view.requestDataToSave();
            if(current_calculated_data != null){
                the_model.saveOffData(current_calculated_data);
            }
        }
        else if(selection.equals("Exit"))
            the_model.quit();
        else if(selection.equals("Fahrenheit")){
            the_model.setMode(HeatIndexCalculatorView.DEGREES_F);
            current_calculated_data = 
                       the_view.resetScale(HeatIndexCalculatorView.DEGREES_F);
            if(current_calculated_data != null);
                the_model.resetHeatIndex(current_calculated_data);
        }
        else if(selection.equals("Celsius")){
            the_model.setMode(HeatIndexCalculatorView.DEGREES_C);
            current_calculated_data =
                       the_view.resetScale(HeatIndexCalculatorView.DEGREES_C);
            if(current_calculated_data != null);
                the_model.resetHeatIndex(current_calculated_data);
        }
        else if(selection.equals("Help")){
            the_view.showHelpDialog();
        }
        else if(selection.equals("About")){
            the_view.showAboutDialog();
        }
        else ;
    }
    
    /*************************************************************************
    **  This handles ActionEvents originating from a JTextField
    *************************************************************************/
    private void handleJTextField(JTextField jtf){
        HeatIndexData current_data = null;
        current_data = the_view.requestData();
        if(current_data != null)
            the_model.calculateHeatIndex(current_data);
    }
}