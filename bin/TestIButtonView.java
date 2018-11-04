import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import rosas.lou.weatherclasses.*;
import myclasses.GenericJFrame;

public class TestIButtonView{
   public TestIButtonView(){
      new IButtonView("IButtonView", null);
   }

   public static void main(String args[]){
      new TestIButtonView();
   }
}
