/********************************************************************
********************************************************************/
package myclasses;

import java.lang.*;
import java.util.*;

public class Singleton{
   private static Singleton instance;
   {
      instance = null;
   }

   /**
   **/
   protected Singleton(){}

   /**
   The typical way of implementing the Singleton Design Pattern
   **/ 
   public static Singleton getInstance(){
      if(instance == null){
         instance = new Singleton();
      }
      return instance;
   }

   /**
   **/
   public String toString(){
      return new String("In the Singleton Class");
   }
}
