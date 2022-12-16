//////////////////////////////////////////////////////////////////////
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
//////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.text.ParseException;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.*;

public class SleepTimeFrame extends GenericJInteractionFrame{
   private static SleepTimeFrame _instance      = null;
   private static int WIDTH                     = 400;
   private static int HEIGHT                    = 300;

   private CurrentWeatherController _controller = null;
   private JTextField               _hrs        = null;
   private JTextField               _mins       = null;
   private JTextField               _secs       = null;

   ///////////////////////Public Methods//////////////////////////////
   
}
