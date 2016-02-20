/********************************************************************
The Serial Port Lister Model (The Actual Serial Port Lister)
********************************************************************/
package rosas.lou.weatherclasses;

import java.util.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.container.OneWireContainer;

//
//
//
public class SerialPortLister extends Observable implements Observer{
   private SearchPort searchPort;

   //********************Public Methods******************************
   //
   //Constructor of no arguments
   //
   public SerialPortLister(){
      this.searchPort = new SearchPort();
      this.searchPort.addObserver(this);
   }
   
   //
   //Constructor Accepting the Observer
   //
   public SerialPortLister(Observer observer){
      this.addObserver(observer);
      this.searchPort = new SearchPort();
      this.searchPort.addObserver(this);
   }

   //
   //Quit the application
   //
   public void quit(){
      System.exit(0);
   }
   
   //
   //Request adapter info from a given port (if there is an adapter
   //AND if that adapter has any one wire devices attatched to it)
   //
   public void requestAdapterInfo(String port){
      Stack adapterStack = new Stack();

      this.setChanged();
      this.notifyObservers(new String("Searching...\n"));
      this.clearChanged();
      this.searchPort.setCurrentPort(port);
      Thread t = new Thread(this.searchPort);
      t.start();
   }
   
   //
   //Implementation of the update() as part of implementing the
   //Runnable interface
   //
   public void update(Observable o, Object arg){
      if(o instanceof SearchPort){
         String updateString = new String();
         updateString = this.searchPort.getPortSearchResults();
         this.setChanged();
         this.notifyObservers(updateString);
         this.clearChanged();
      }
   }

   //*******************Private Methods******************************
}