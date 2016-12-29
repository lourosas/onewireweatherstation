/////////////////////////////////////////////////////////////////////
//
//
//
//
//
/////////////////////////////////////////////////////////////////////
package rosas.lou;

import java.lang.*;
import java.util.*;
import java.io.*;


public interface IOObserver{
   public void alertGeneralIOError(File f, Exception e);
   public void alertNoDataError(File f);
   public void alertIOExceptionError(File f);
}
