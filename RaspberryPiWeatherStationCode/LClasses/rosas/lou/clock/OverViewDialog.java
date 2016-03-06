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

public class OverViewDialog extends JDialog{
   public OverViewDialog(JFrame parent, String title, String message){
      super(parent, title, false); //Do not set modal
      if(parent != null){
         Dimension parentSize = parent.getSize();
         Point p = parent.getLocation();
         this.setLocation(p.x + parentSize.width/4,
                                           p.y + parentSize.height/4);
      }
      JPanel messagePane = new JPanel();
      JTextArea dialogTextArea = new JTextArea(message);
      dialogTextArea.setEditable(false);
      messagePane.add(dialogTextArea);
      JScrollPane scrollPane = new JScrollPane(messagePane);
      scrollPane.setPreferredSize(new Dimension(400,250));
      this.getContentPane().add(scrollPane);
      JPanel buttonPane = new JPanel();
      JButton button = new JButton("OK");
      buttonPane.add(button);
      button.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
           setVisible(false);
           dispose();
         }
      });
      this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      pack();
      setVisible(true);
   }
}
