//*******************************************************************
//RunDatafile Class
//Copyright (C) 2008 Lou Rosas
//This file is part of PaceCalculator.
//PaceCalculator is free software; you can redistribute it
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
package rosas.lou.calculator;

import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.*;
import rosas.lou.*;
import rosas.lou.calculator.*;
import myclasses.*;

/********************************************************************
The RunDatafile Class by Lou Rosas.  This is the class to write out
the RunData to a specific file.
********************************************************************/
public class RunDatafile{
   private RunData runData;
   private String  fileName;
   
   //**************************Public Methods************************
   /*****************************************************************
   Constructor no arguments
   *****************************************************************/
   public RunDatafile(){
   }
   
   /*****************************************************************
   Constructor taking both the RunData and File attributes
   *****************************************************************/
   public RunDatafile(RunData runData_, File file_){
      char sep = File.separatorChar;
      String fileName = new String(file_.getParent() + sep);
      fileName = fileName.concat(file_.getName());
      this.setRunData(runData_);
      this.setFileName(fileName);
   }
   
   /*****************************************************************
   Constructor taking both the RunData and File Name attributes.
   The File Name is in the form of a String.
   *****************************************************************/
   public RunDatafile(RunData runData_, String fileName_){
      this.setRunData(runData_);
      this.setFileName(fileName_);
   }
   
   /*****************************************************************
   Return the current File Name.
   *****************************************************************/
   public String getFileName(){
      return new String(this.fileName);
   }
   
   /*****************************************************************
   Return the current RunData object
   *****************************************************************/
   public RunData getRunData(){
      return new RunData(this.runData);
   }

   /*****************************************************************
   Set the File Name String
   *****************************************************************/
   public void setFileName(String fileName_){
      this.fileName = new String(fileName_);
   }
   
   /*****************************************************************
   Set the RunData
   *****************************************************************/
   public void setRunData(RunData runData_){
      this.runData = new RunData(runData_);
   }
   
   /*****************************************************************
   Print the RunData to a specific file.
   If the file is unable to be saved in any way, indicate that.
   Throws:  IOException
   *****************************************************************/
   public void save() throws IOException{
      FileWriter  fileWriter  = null;
      FileReader  tempReader  = null;
      PrintWriter printWriter = null;
      boolean     writeHeader = false;
      
      try{
         tempReader = new FileReader(this.getFileName());
      }
      catch(FileNotFoundException fnfe){
         writeHeader = true;
      }
      try{
         fileWriter  = new FileWriter(this.getFileName(), true);
         printWriter = new PrintWriter(fileWriter, true);
         if(writeHeader){
            String header = new String();
            header = header.concat("Date\t\t\tRun Time\tPace\t\t");
            header = header.concat("Distance\n");
            header = header.concat("------------------------------");
            header = header.concat("------------------------------");
            printWriter.println(header);
         }
         //See if this will work - if not, go ahead and get the
         //toString() method for the object
         printWriter.println(this.getRunData());
         if(printWriter.checkError()){
            throw(new IOException("Save Error Occurred"));
         }
      }
      catch(IOException ioe){
         ioe.printStackTrace();
         throw(ioe);
      }
      finally{
         fileWriter.close();
         printWriter.close();
      }  
   }
}