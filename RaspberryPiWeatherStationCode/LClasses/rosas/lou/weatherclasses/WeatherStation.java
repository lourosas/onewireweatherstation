/**/

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import gnu.io.*;
import rosas.lou.weatherclasses.*;
import rosas.lou.clock.StopWatch;
import rosas.lou.clock.TimeFormater;
import rosas.lou.clock.TimeListener;
import rosas.lou.clock.ClockState;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.Convert;

//Database Stuff (TBD)
//import java.sql.*;

public class WeatherStation implements TimeListener{
   private Units units;
   private DSPortAdapter dspa                  = null;
   private StopWatch     stopWatch             = null;
   private String        currentDate           = null;
   private List<Sensor>  sensorList            = null;
   //put into a list to handle multiple observers
   private List<TemperatureObserver> t_o_List  = null;
   private List<HumidityObserver>    h_o_List  = null;
   private List<BarometerObserver>   b_o_List  = null;
   private List<CalculatedObserver>  c_o_List  = null;
   private List<TimeObserver>        ti_o_List = null;

   //************************Constructors*****************************
   /*
   Constructor of no arguments
   */
   public WeatherStation(){
      this(Units.METRIC);
   }

   /*
   Constructor initializing the Units, et. al...
   */
   public WeatherStation(Units units){
      final int DEFAULTUPDATERATE = 5; //5 minutes
      this.currentDate = new String();
      this.setUpDSPA();
      this.setUpObservers();
      this.setUpdateRate(DEFAULTUPDATERATE);
   }

}
