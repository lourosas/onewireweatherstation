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
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import rosas.lou.weatherclasses.*;

public class InstantWeatherDataClient extends WeatherClientDataPublisher
implements Runnable{
   private static final int PORT     = 23000;
   private static final int TIMEOUT  = 20000;

   private DatagramSocket    _socket;
   private byte[]            _addr;
   private String            _rawData;
   private List<WeatherData> _weatherData;
   private boolean           _request;

   {
      _addr = new byte[]{(byte)192,(byte)168,(byte)0,(byte)173};
      _socket      = null;
      _rawData     = null;
      _weatherData = null;
      _request     = false;
   };

   //////////////////////Constructors/////////////////////////////////
   /**/
   public InstantWeatherDataClient(){}


   //////////////////Runnable Interface Implementation////////////////
   /**/
   public void run(){
      while(true){
         try{
            Thread.sleep(50);
            if(this._request){
               this.requestWeatherDataFromServer();
            }
         }
         catch(InterruptedException ie){}
      }
   }

   /////////////////////Public Methods////////////////////////////////
   /**/
   public void request(){
      this._request = true;
   }

   ////////////////////Private Methods////////////////////////////////
   /**/
   private void requestWeatherDataFromServer(){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      LinkedList<String> data      = new LinkedList();
      try{
         this._socket = new DatagramSocket();
         this._socket.setSoTimeout(TIMEOUT);
         String testData = new String("Request");
         byte test[] = testData.getBytes();
         InetAddress iNetAddr = InetAddress.getByAddress(this._addr);
         byte[] receiveData = new byte[1024];
         System.out.println(iNetAddr.getHostName());
         System.out.println(iNetAddr.getHostAddress());
         sendPacket = new DatagramPacket(test,
                                         test.length,
                                         iNetAddr,
                                         PORT);
         this._socket.send(sendPacket);
         receivePacket =
                  new DatagramPacket(receiveData, receiveData.length);
         this._socket.receive(receivePacket);
         this._rawData = new String(receivePacket.getData());
         System.out.println(receivePacket.getAddress());
         System.out.println(receivePacket.getPort());
         System.out.println(receivePacket.getLength());
         System.out.println(this._rawData);
      }
      catch(SocketTimeoutException ste){
         ste.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
      finally{
         this._request = false;
      }
   }
}
