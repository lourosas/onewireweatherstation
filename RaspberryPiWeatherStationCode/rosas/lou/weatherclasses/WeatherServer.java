/********************************************************************
<GNU Stuff goes here>
********************************************************************/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import rosas.lou.weatherclasses.WeatherStation

public class WeatherStation{
   private int            port;
   private DatagramSocket socket;

   {
      socket = null;
      port   = -1;
   }

   //Constructor
   public WeatherStation(int port){
      this.setPort(port);
   }

   /////////////////////////Public Methods///////////////////////////

   ////////////////////////Private Methods///////////////////////////
   private void setPort(int port){
      this.port = port;
   }
}
