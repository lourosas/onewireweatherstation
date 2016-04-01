/**/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

public class WeatherExtreme{
   private double maxTempC;
   private double maxTempF;
   private double maxTempK;
   private double minTempC;
   private double minTempF;
   private double minTempK;
   private String maxTempDate;
   private String minTempDate;
   private double maxHumidity;
   private double minHumidity;
   private String maxHumidityDate;
   private String minHumidityDate;
   private double maxDPC;
   private double maxDPF;
   private double maxDPK;
   private double minDPC;
   private double minDPF;
   private double minDPK;
   private String maxDPDate;
   private String minDPDate;
   private double maxHIC;
   private double maxHIF;
   private double maxHIK;
   private double minHIC;
   private double minHIF;
   private double minHIK;
   private String maxHIDate;
   private String minHIDate;
   
   {
      maxTempC    =  Thermometer.DEFAULTTEMP;
      maxTempF    =  Thermometer.DEFAULTTEMP;
      maxTempK    =  Thermometer.DEFAULTTEMP;
      minTempC    = -Thermometer.DEFAULTTEMP;
      minTempF    = -Thermometer.DEFAULTTEMP;
      minTempK    = -Thermometer.DEFAULTTEMP;
      maxTempDate = null;
      minTempDate = null;
      
      maxHumidity     =  Hygrometer.DEFAULTHUMIDITY;
      minHumidity     = -Hygrometer.DEFAULTHUMIDITY;
      maxHumidityDate = null;
      minHumidityDate = null;
      
      maxDPC    =  Thermometer.DEFAULTTEMP;
      maxDPF    =  Thermometer.DEFAULTTEMP;
      maxDPK    =  Thermometer.DEFAULTTEMP;
      minDPC    = -Thermometer.DEFAULTTEMP;
      minDPF    = -Thermometer.DEFAULTTEMP;
      minDPK    = -Thermometer.DEFAULTTEMP;
      maxDPDate = null;
      minDPDate = null;
      
      maxHIC    =  Thermometer.DEFAULTTEMP;
      maxHIF    =  Thermometer.DEFAULTTEMP;
      maxHIK    =  Thermometer.DEFAULTTEMP;
      minHIC    = -Thermometer.DEFAULTTEMP;
      minHIF    = -Thermometer.DEFAULTTEMP;
      minHIK    = -Thermometer.DEFAULTTEMP;
      maxHIDate = null;
      minHIDate = null;
   }
}