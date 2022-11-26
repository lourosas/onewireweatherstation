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
import java.text.*;
import rosas.lou.weatherclasses.*;

public class RawDataToWeatherDataConverter{
   private static Calendar _cal = null;

   /////////////////////Constructors//////////////////////////////////
   /**/
   public RawDataToWeatherDataConverter(){}

   ///////////////////////Public Methods//////////////////////////////
   ///////////////////////Static Methods//////////////////////////////
   /*
   */
   public static WeatherData barometricPressure(String rawData)
   throws RuntimeException{
      double      pressure = WeatherData.DEFAULTVALUE;
      WeatherData data     = null;
      try{
         calendar(rawData);
         try{
            pressure  = Double.parseDouble(rawData.split(" ")[4]);
            pressure += 103.5; //Altitude Adjustment
            data      = new PressureData(Units.ABSOLUTE,
                                         pressure,
                                         "good",
                                         _cal);
         }
         catch(NumberFormatException e){
            pressure = WeatherData.DEFAULTVALUE;
            data     = new PressureData(Units.ABSOLUTE,
                                        pressure,
                                        e.getMessage(),
                                        _cal);
         }
         return data;
      }
      catch(ArrayIndexOutOfBoundsException e){
         throw new RuntimeException(e);
      }
   }

   /*
   Very Simple:
   Calculate the dewpoint for a given pair of humidity and
   temperature sensors.  This is based on the temperature and
   humidty sensor stacks and is based on the assumption that
   That each temp sensor in the temperature stack corresponds
   to an exact humidity sensor at the same position in the
   humidity stack.  The dewpoint is a formulaic calculation
   based on temperature and relative humidity and is an
   approximation.  The actual calculation depends upon wetbulb
   and dry bulb measurements.  This is an approximation based on
   the Manus-Tetens formula.
   Td = (243.12 * alpha[t,RH])/(17.62 - alpha[t, RH])
   where alpha[t,RH] = (17.62*t/(243.12 + t)) + ln(RH/100)
   and 0.0 < RH < 100.0.
   Temperature -45 to 60 degrees celsius
   */
   public static WeatherData dewpoint(String rData){
      double      dewpoint = WeatherData.DEFAULTVALUE;
      WeatherData data     = null;
      try{
         calendar(rData);
         final double l  = 243.12; //Lambda Constant
         final double b  =  17.62; //Beta Constand
         try{
            double temp = Double.parseDouble(rData.split(" ")[2]);
            double humidity = Double.parseDouble(rData.split(" ")[3]);
            if(humidity < 0.0){
               throw new ArithmeticException("Humidity too Low");
            }
            double alpha =((b*temp)/(l+temp))+Math.log(humidity*0.01);
            dewpoint     = (l * alpha)/(b - alpha);
            data         = new DewpointData(Units.METRIC,
                                            dewpoint,
                                            "good",
                                            _cal);

         }
         catch(ArithmeticException e){
            dewpoint = WeatherData.DEFAULTVALUE;
            data     = new DewpointData(Units.METRIC,
                                        dewpoint,
                                        e.getMessage(),
                                        _cal);
         }
         catch(NumberFormatException e){
            dewpoint = WeatherData.DEFAULTVALUE;
            data     = new DewpointData(Units.METRIC,
                                        dewpoint,
                                        e.getMessage(),
                                        _cal);
         }
         return data;
      }
      catch(ArrayIndexOutOfBoundsException e){
         throw new RuntimeException(e);
      }
   }

   /*
   Calculate the heat index for a given pair of humidity and
   temperature sensors.  This is based on the temperature and
   humidity sensor stacks and is based on the assumption that
   each temperature sensor in the temperature stack corresponds
   to an excact humidity sensor at the same position in the
   humidity stack.  The heat index is a formulaic calculation
   based on temprature and relative humidity and is an
   approximation (although pretty good) which is based on sixteen
   calculations.
   heatindex = 16.923                            +
               (.185212*tf)                      +
               (5.37941 * rh)                    -
               (.100254*tf*rh)                   +
               ((9.41685 * 10^-3) * tf^2)        +
               ((7.28898 * 10^-3) * rh^2)        +
               ((3.45372*10^-4) * tf^2*rh)       -
               ((8.14971*10^-4) * tf *rh^2)      +
               ((1.02102*10^-5) * tf^2 * rh^2)   -
               ((3.8646*10^-5) * tf^3)           +
               ((2.91583*10^-5) * rh^3)          +
               ((1.42721*10^-6) * tf^3 * rh)     +
               ((1.97483*10^-7) * tf * rh^3)     -
               ((2.18429*10^-8) * tf^3 * rh^2)   +
               ((8.43296*10^-10) * tf^2 * rh^3)  -
               ((4.81975*10^-11)*t^3*rh^3)

   NOTE:  The Heat Index Calculation becomes inaccurate at a Dry Bulb
   less than 70 F.  If that is the case, the default value
   is set.  For those values, the System will have to determine
   The reason for the default Heat Index.  It is up to the
   System to figure out the appropriateness of the Heat Index
   data for display.
   */
   public static WeatherData heatindex(String rawData)
   throws RuntimeException{
      final double MINIMUMTEMP = 70.0;
      double heatI = WeatherData.DEFAULTVALUE;
      WeatherData data = null;
      try{
         calendar(rawData);
         try{
            double tf = Double.parseDouble(rawData.split(" ")[2]);
            double rh = Double.parseDouble(rawData.split(" ")[3]);
            WeatherData tempData=new TemperatureData(Units.METRIC,tf);
            tf = tempData.englishData();
            if(tf < MINIMUMTEMP){
               throw new NumberFormatException("Temperature too low");
            }
            heatI  = 16.923;
            heatI += (0.185212 * tf);
            heatI += (5.37941  * rh);
            heatI -= ((0.100254) * tf * rh);
            heatI += ((9.41685 * 0.001) * tf * tf);
            heatI += ((7.28898 * 0.001) * rh * rh);
            heatI += ((3.45372 * 0.0001) * tf * tf * rh);
            heatI -= ((8.14971 * 0.0001) * tf * rh * rh);
            heatI += ((1.02102 * 0.00001) * tf * tf * rh * rh);
            heatI -= ((3.8646  * 0.00001) * tf * tf * tf);
            heatI += ((2.91583 * 0.00001) * rh * rh * rh);
            heatI += ((1.42721 * .000001)* tf * tf * tf *rh);
            heatI += ((1.97483 * .0000001) * tf * rh * rh * rh);
            heatI -= ((2.18429 * .00000001) *tf*tf*tf* rh * rh);
            heatI += ((8.43196 * .0000000001)*tf*tf*rh*rh*rh);
            heatI -= ((4.81975 * .00000000001)*tf*tf*tf*rh*rh*rh);
            data = new HeatIndexData(Units.ENGLISH,
                                            heatI,
                                            "good",
                                            _cal);
         }
         catch(NumberFormatException nfe){
            nfe.printStackTrace();
            data = new HeatIndexData(Units.ENGLISH,
                                            heatI,
                                            nfe.getMessage(),
                                            _cal);
         }
         return data;
      }
      catch(ArrayIndexOutOfBoundsException e){
         throw new RuntimeException(e);
      }
   }

   /*
   */
   public static WeatherData humidity(String rawData)
   throws RuntimeException{
      double humid     = WeatherData.DEFAULTHUMIDITY;
      WeatherData data = null;
      try{
         calendar(rawData);
         try{
            humid = Double.parseDouble(rawData.split(" ")[3]);
            data  = new HumidityData(Units.PERCENTAGE,
                                     humid,
                                     "good",
                                     _cal);
         }
         catch(NumberFormatException e){
            humid = WeatherData.DEFAULTHUMIDITY;
            data  = new HumidityData(Units.PERCENTAGE,
                                    humid,
                                    e.getMessage(),
                                    _cal);
         }
         return data;
      }
      catch(ArrayIndexOutOfBoundsException e){
         throw new RuntimeException(e);
      }
   }

   /*
   */
   public static WeatherData temperature(String rawData)
   throws RuntimeException{
      double temp      = WeatherData.DEFAULTVALUE;
      WeatherData data = null;
      try{
         calendar(rawData);
         try{
            temp = Double.parseDouble(rawData.split(" ")[2]);
            data = new TemperatureData(Units.METRIC,temp,"good",_cal);
         }
         catch(NumberFormatException e){
            temp = WeatherData.DEFAULTVALUE;
            data = new TemperatureData(Units.METRIC,
                                       temp,
                                       e.getMessage(),
                                       _cal);
         }
         return data;
      }
      catch(ArrayIndexOutOfBoundsException e){
         throw new RuntimeException(e);
      }
   }

   //////////////////////Private Methods//////////////////////////////
   /**/
   private static void calendar(String rawData)
   throws ArrayIndexOutOfBoundsException{
      _cal = Calendar.getInstance();
      try{
         String mdy = rawData.split(" ")[0];
         String hms = rawData.split(" ")[1];
         String [] yearmonthday = mdy.split("-");
         String yr              = yearmonthday[0];
         String mt              = yearmonthday[1];
         String dy              = yearmonthday[2];
         int year               = Integer.parseInt(yr);
         int month              = Integer.parseInt(mt);
         int day                = Integer.parseInt(dy);
         String [] hourminsec   = hms.split(":");
         int hour               = Integer.parseInt(hourminsec[0]);
         int min                = Integer.parseInt(hourminsec[1]);
         int sec = Integer.parseInt(hourminsec[2].split("\\.")[0]);
         _cal.set(year, month - 1, day, hour, min, sec);
      }
      catch(NumberFormatException nfe){
         nfe.printStackTrace();
      }
      catch(ArrayIndexOutOfBoundsException e){
         /*
         WeatherDataParser wdp = new WeatherDataParser();
         String date = wdp.parseCalendar(rawData);
         SimpleDateFormat df =
                    new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
         try{
            _cal.setTime(df.parse(date));
         }
         catch(ParseException pe){
            pe.printStackTrace();
         }
         */
         throw e;
      }
   }
}
