import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.*;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

public class TestPorts{
   public static void main(String [] args){
      PortSniffer ps = new PortSniffer(PortSniffer.PORT_USB);
      Hashtable returnHash = ps.findPorts();
      System.out.println("PortSniffer:  " + ps);
      System.out.println("Hash:  " + returnHash);
      //ps.printAllPorts();
      /*
      try{
         DSPortAdapter adapter = null;
         //adapter = OneWireAccessProvider.getDefaultAdapter();
         //System.out.println(adapter);
         adapter = OneWireAccessProvider.getAdapter("DS9490", "USB1");
         System.out.println(adapter);
      }
      catch(OneWireIOException owioe){ System.out.println(owioe); }
      catch(OneWireException owe){ System.out.println(owe); }
      */
   }
}
