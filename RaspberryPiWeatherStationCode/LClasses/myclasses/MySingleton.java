/********************************************************************
********************************************************************/
package myclasses;

import java.lang.*;
import java.util.*;
import myclasses.Singleton;

public class MySingleton extends Singleton{
   private static MySingleton instance;
   {
      instance = null;
   }

   /**
   **/
   protected MySingleton(){}

   /**
   **/
   public static MySingleton getInstance(){
      if(instance == null){
         instance = new MySingleton();
      }
      return instance;
   }

   /**
   **/
   public String toString(){
      return new String("In the MySingleton Class");
   }
}
