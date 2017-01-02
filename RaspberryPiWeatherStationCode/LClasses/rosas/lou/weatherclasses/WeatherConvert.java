//******************************************************************
//Weather Convert Class
//Copyright (C) 2017 by Lou Rosas
//This file is part of onewireweatherstation application.
//onewireweatherstation is free software; you can redistribute it
//and/or modify
//it under the terms of the GNU General Public License as published
//by the Free Software Foundation; either version 3 of the License,
//or (at your option) any later version.
//PaceCalculator is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied
//warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with this program.
//If not, see <http://www.gnu.org/licenses/>.
//*******************************************************************

package rosas.lou.weatherclasses;

import java.util.*;
import java.lang.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.utils.Convert;

public class WeatherConvert{
   /*
   Converts a temperature from Celsius to Fahrenheit
   */
   public static double celsiusToFahrenheit(double celsius){
      return Convert.toFahrenheit(celsius);
   }
   
   /*
   Converts a temperauture from Celsius to Kelvin
   */
   public static double celsiusToKelvin(double celsius){
      final double KELVIN_CONVERSION = 273.15;
      return celsius + KELVIN_CONVERSION;
   }
   
   /*
   Converts a temperature from Kelvin to Celsius
   */
   public static double kelvinToCelsius(double kelvin){
      final double KELVIN_CONVERSION = 273.15;
      return kelvin - KELVIN_CONVERSION;
   }
   
   /*
   Converts a temperature from Kelvin to Fahrenheit
   */
   public static double kelvinToFahrenheit(double kelvin){
      double celsius = kelvinToCelsius(kelvin);
      return celsiusToFahrenheit(celsius);
   }
   
   /*
   Converts a temperature from Fahrenheit to Celsius
   */
   public static double fahrenheitToCelsius(double fahrenheit){
      return Convert.toCelsius(fahrenheit);
   }
   
   /*
   Converts a tempertaure from Fahrenheit to Kelvin
   */
   public static double fahrenheitToKelvin(double fahrenheit){
      double celsius = fahrenheitToCelsius(fahrenheit);
      return celsiusToKelvin(celsius);
   }
   
   /*
   Convert inches to centi-meters
   */
   public static double inchesToCentimeters(double inches){
      final double CENTIMETERCONVERSION = 2.54;
      return inches * CENTIMETERCONVERSION;
   }
   
   /*
   Convert inches to milli-meters
   */
   public static double inchesToMillimeters(double inches){
      return inchesToCentimeters(inches) * 10.0;
   }
   
   /*
   Convert barometric pressure in inches to milli-bars
   */
   public static double inchesToMillibars(double inches){
      final double MILLIBARCONVERSION = 33.8639;
      return inches * MILLIBARCONVERSION;
   }
   
   /*
   Convert centimeters to inches
   */
   public static double centimetersToInches(double centimeters){
      final double CENTIMETERCONVERSION = 2.54;
      return centimeters/CENTIMETERCONVERSION;
   }
   
   /*
   Convert millimeters to inches
   */
   public static double millimetersToInches(double millimeters){
      return centimetersToInches(millimeters * .1);
   }
   
   /*
   Convert millibars to inches
   */
   public static double millibarsToInches(double milliB){
      final double INCHCONVERSION = (1/33.8639);
      return (milliB * INCHCONVERSION);
   }
   
   /*
   Convert millibars to millimeters
   */
   public static double millibarsToMillimeters(double milliB){
      final double MMCONVERSION = 0.75006375541921;
      return(milliB * MMCONVERSION);
   }
}
