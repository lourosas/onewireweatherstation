/*
*/

package rosas.lou.lgraphics;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class TestPanel extends JPanel{
   int[] x = { 1, 200, 230, 240, 330};
   int[] y = {20, 4, 8, 7, 6};
   
   double[] dx = {  0, 198.23, 202.36, 240.18, 340.0};
   double[] dy = { 100., 150.45, 98.23, 157.84, 100.23};

   public void paintComponent(Graphics g){
      super.paintComponent(g);
      
      this.setBackground(Color.WHITE);
      
      g.setColor(Color.RED);
      g.drawLine(5,30, 300, 30);
      
      g.setColor(Color.BLACK);
      for(int i = 0; i < x.length - 1; i++){
         int x1 = x[i];     int y1 = y[i];
         int x2 = x[i + 1]; int y2 = y[i + 1];
         g.drawLine(x1, y1, x2, y2);
      }
      
      g.setColor(Color.BLUE);
      Graphics2D g2 = (Graphics2D)g;
      for(int i = 0; i < dx.length - 1; i++){
         double dx1 = dx[i]; double dx2 = dx[i + 1];
         double dy1 = dy[i]; double dy2 = dy[i + 1];
         g2.draw(new Line2D.Double(dx1, dy1, dx2, dy2));
      }
      g2.setPaint(Color.BLUE);
      for(int i = 0; i < dx.length; i++){
         double dx1 = dx[i];
         double dy1 = dy[i];
         g2.fill(new Ellipse2D.Double(dx1-2, dy1-2, 3, 3));
      }
      g2.setPaint(Color.GREEN);
      int w = this.getWidth();
      int h = this.getHeight();
      System.out.println(w); System.out.println(h);
      for(int i = 0; i < dx.length - 1; i++){
         //In this case, the PAD is 15 from the x, y axes
         double dx1 = dx[i] + 15; double dx2 = dx[i + 1] + 15;
         double dy1 = h-dy[i]-15; double dy2 = h-dy[i + 1]-15;
         g2.draw(new Line2D.Double(dx1, dy1, dx2, dy2));
      }
      for(int i = 0; i < dx.length; i++){
         double dx1 = dx[i] + 15;
         double dy1 = h - dy[i] - 15;
         g2.fill(new Ellipse2D.Double(dx1-2, dy1-2, 3, 3));
      }
      
      g2.setPaint(Color.BLACK);
      g2.drawLine(15, 0, 15, h - 15);
      g2.drawLine(15, h - 15, w, h - 15);
      Font font = g2.getFont();
      FontRenderContext frc = g2.getFontRenderContext();
      LineMetrics lm = font.getLineMetrics("0", frc);
      double sh = lm.getAscent() + lm.getDescent();
      String xstring = "x-axis";
      String ystring = "data";
      double sy = (h - ystring.length()*sh)/2. + lm.getAscent();
      double sx = 2;
      for(int i = 0; i < ystring.length(); i++){
         String letter = String.valueOf(ystring.charAt(i));
         //Centers in the "pad region"
         double sw = (double)font.getStringBounds(letter,frc).getWidth();
         sx = (15 - sw)/2.;
         g2.drawString(letter, (float)sx, (float)sy);
         sy += sh;
      }
      //Centers the text in the y-area
      sy = h - 15 + (15 - sh)/2. + lm.getAscent();
      double sw = (double)font.getStringBounds(xstring,frc).getWidth();
      sx = (w - sw)/2.; //center the text in x-direction
      //sx = w/4.;  //For now, make these real easy
      //sy = h - 5; //For now, make these real easy
      g2.drawString(xstring, (float)sx, (float)sy);
   }
}