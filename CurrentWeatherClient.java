/*
Copyright 2018 Lou Rosas

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
import rosas.lou.weatherclasses.*;

public class CurrentWeatherClient extends WeatherClientDataPublisher
implements Runnable{
   /*TODO--make a static method in CurrentWeatherServer to get
     this value*/
   private static final int PORT    = 19000;
   private static final int TIMEOUT = 20000;
   
   private DatagramSocket _socket;
   private byte[] _addr;
   private String _rawData;
   {
      _socket = null;
      _addr = new byte[]{(byte)192, (byte)168, (byte)1, (byte)145};
      _rawData = null;
   }

   ///////////////////////Constructors///////////////////////////////
   /*
   TODO--make a wholebunch more constructors that that take observers
   */
   public CurrentWeatherClient(){}

   //////////////////Runnable Interface Implementation///////////////
   public void run(){
      while(true){
         try{
            this.requestWeatherDataFromServer();
            this.publishData(this._rawData);
            Thread.sleep(60000);
         }
         catch(InterruptedException ie){}
      }
   }

   //////////////////Private Methods/////////////////////////////////
   /*
   */
   private void requestWeatherDataFromServer(){
      DatagramPacket sendPacket    = null;
      DatagramPacket receivePacket = null;
      List<String> data            = new LinkedList();
      try{
         this._socket = new DatagramSocket();
         //Set a receive timeout for a given time, if a packet is
         //NOT received within a given amount of time, Throw a
         //SocketTimeoutException and GET OUT!!!  Don't "sit there
         //and spin" waiting for packets that are never comming!!
         this._socket.setSoTimeout(TIMEOUT);
         String testData = new String("poop");
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
   }
}
