/*
Copyright 2022 Lou Rosas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rosas.lou.weatherclasses.*;

/*
Generic Weather Controller abstract class for Current Weather
Observations
*/
public abstract class CurrentWeatherController
implements ActionListener, KeyListener, ItemListener{
   public void addModel(CurrentWeatherDataSubscriber model){}
   //////////////////////////Interface Implementations////////////////
   public void actionPerformed(ActionEvent ae){}
   public void keyPressed(KeyEvent ke){}
   public void keyReleased(KeyEvent ke){}
   public void keyTyped(KeyEvent ke){}
   public void itemStateChanged(ItemEvent ie){}
}
