/////////////////////////////////////////////////////////////////////
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
/////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import rosas.lou.weatherclasses.*;

public class CurrentHttpHandler 
extends CurrentWeatherDataSubscriber, implements HttpHandler{
   ////////////////////////Constructors///////////////////////////////
   /*
   */
   public CurrentHttpHandler(){
      super();
   }

   ///////////////////Interface Implementations///////////////////////
   /*
   Implementation of the HttpHandler Interface
   */
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      final int OK          = 200;
      OutputStream os       = null;
      try{
         response.append(this.setUpHeader());
         response.append(this.closeOutHeader());
         response.append(this.setUpBody());
         response.append(this.setUpFooter());
         String send = response.toString();
         exchange.sendResponseHeaders(OK, send.length());
         os = exchange.getResponseBody();
         os.write(send.getBytes());
         //os.close();
      }
      catch(IOException ioe{
         ioe.printStackTrace();
      }
      finally{
         try{
            os.close();
         }
         catch(IOException ioe){}
      }
   }

   ////////////////////Protected Methods//////////////////////////////
   /*
   */
   protected String closeOutHeader(){
      StringBuffer close = new StringBuffer();
      close.append("\n</script></head>\n");
      return close.toString();
   }

   /*
   */
   protected String setUpBody(){
      StringBuffer body = new StringBuffer();
      String date = this._wdp.parseCalendar(this._data);
      body.append("\n<body>\n");
      body.append("<h2>Tucson, AZ<br>");
      body.append("Rita Ranch Neighborhood<br>Weather Conditions");
      body.append("<br>As of: " + date +"</h2>\n");
      body.append("</body>\n");
      return body.toString();
   }

   /*
   */
   protected String setUpFooter(){
      StringBuffer footer = new StringBuffer();
      footer.append("\n</html>");
      return footer.toString();
   }

   /*
   */
   protected String setUpHeader(){
      StringBuffer header = new StringBuffer();
      header.append("<html><head>\n");
      header.append("<title>A One Wire Weather Station</title>\n");
      header.append("<meta http-equiv=\"refresh\" content=\"600\">");
      header.append("\n");
      return header.toString();
   }
}
