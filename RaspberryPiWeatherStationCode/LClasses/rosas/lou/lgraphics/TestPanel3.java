/**
********************************************************************
*/

package rosas.lou.lgraphics;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

/**
*/
public class TestPanel3 extends JPanel{
   private java.util.List<Object> data = null;
   private java.util.list<Data>   time = null;
   int min = Integer.MAX_VALUE;
   int max = Integer.MIN_VALUE;
   static final int PAD = 25;

   /**
   */
   public TestPanel3
   (
      LinkedList<Object> data,
      LinkedList<Date>   time
   ){
      this.data = new LinkedList(data);
      this.time = new LinkedList(time);
      counter++;
   }

   /**
   */
   public TestPanel3
   (
      LinkedList<Object> data,
      double min,
      double max,
      LinkedList<Date> time
   ){
      this.data = new LinkedList(data);
      this.min = (int)min - 1;
      this.max = (int)max + 1;
      this.time = new LinkedList(time);
      counter++;
   }

   /**
   */
   public void paintComponent(Graphics g){
      //Set the Size Based on the data!
      //Need the min and the max values
   }
}
