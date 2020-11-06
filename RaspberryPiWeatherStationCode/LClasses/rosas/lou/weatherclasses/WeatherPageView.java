/*
Copyright 2020 Lou Rosas

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
//import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import rosas.lou.weatherclasses.*;
import myclasses.*;
import rosas.lou.lgraphics.WeatherPanel;

//////////////////////////////////////////////////////////////////////
/*
*/
//////////////////////////////////////////////////////////////////////
public class WeatherPageView extends GenericJFrame
implements WeatherDatabaseClientObserver{
   private static final short GRAPH        = 0;
   private static final short DATA         = 1;
   private static final short WIDTH        = 750;
   private static final short HEIGHT       = 600;
   private static final short TOTAL_PANELS = 5;
   private static String [] MONTHS = {"January", "February",
   "March", "April", "May", "June", "July", "August", "September",
   "October", "November", "December"};
   private static String [] DAYS = {"01","02","03","04","05","06",
   "07","08","09","10","11","12","13","14","15","16","17","18","19",
   "20","21","22","23","24","25","26","27","28","29","30","31"};
   private static String [] YEARS = {"2017", "2018", "2019", "2020"};

   //private WeatherPageController _controller = null;

   //private Units dewpointUnits    = Units.METRIC;
   //private Units temperatureUnits = Units.METRIC;
   //private Units heatIndexUnits   = Units.METRIC;
   //private Units pressureUnits    = Units.ABSOLUTE;

   ButtonGroup _dewpointUnitsGroup      = null;
   ButtonGroup _dewpointDisplayGroup    = null;
   ButtonGroup _heatIndexUnitsGroup     = null;
   ButtonGroup _heatIndexDisplayGroup   = null;
   ButtonGroup _humidityDisplayGroup    = null;
   ButtonGroup _pressureUnitsGroup      = null;
   ButtonGroup _pressureDisplayGroup    = null;
   ButtonGroup _temperatureUnitsGroup   = null;
   ButtonGroup _temperatureDisplayGroup = null;

   ////////////////////////Public Methods/////////////////////////////
   /////////////////////////Constructors//////////////////////////////
   /**/
   public WeatherPageView(){
      this("");
   }

   /**/
   public WeatherPageView(String title){
      super(title);
      //this._controller = new WeatherPageController(this);
      this.setUpGui();
   }
}
