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

public class DailyWeatherHandler
extends CurrentWeatherDataSubscriber implements HttpHandler{
   private String[] months = {"January", "February", "March", 
                              "April", "May", "June", "July",
                              "August", "September", "October",
                              "November", "December" };
   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      final int OK          = 200;
      OutputStream os       = null;
      try{
         response.append(this.setUpHeader());
         response.append(this.setUpTemperature());
         response.append(this.setUpHumidity());
         response.append(this.setUpPressure());
         response.append(this.setUpDewpoint());
         response.append(this.setUpHeatIndex());
         response.append("\n</script>\n</head>\n");
         response.append(this.setUpBody());
         String send = response.toString();
         exchange.sendResponseHeaders(OK, send.length());
         os = exchange.getResponseBody();
         os.write(send.getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
      catch(Exception e){ e.printStackTrace(); }
      finally{
         try{
            os.close();
         }
         catch(IOException io){}
      }
   }

   //
   //
   //
   private String discernMonth(String abreviatedMonth){
      String month = null;
      if(abreviatedMonth.equals("Jan")){
         month = new String("January");
      }
      else if(abreviatedMonth.equals("Feb")){
         month = new String("February");
      }
      else if(abreviatedMonth.equals("Mar")){
         month = new String("March");
      }
      else if(abreviatedMonth.equals("Apr")){
         month = new String("April");
      }
      else if(abreviatedMonth.equals("May")){
         month = new String(abreviatedMonth);
      }
      else if(abreviatedMonth.equals("Jun")){
         month = new String("June");
      }
      else if(abreviatedMonth.equals("Jul")){
         month = new String("July");
      }
      else if(abreviatedMonth.equals("Aug")){
         month = new String("August");
      }
      else if(abreviatedMonth.equals("Sep")){
         month = new String("September");
      }
      else if(abreviatedMonth.equals("Oct")){
         month = new String("October");
      }
      else if(abreviatedMonth.equals("Nov")){
         month = new String("November");
      }
      else if(abreviatedMonth.equals("Dec")){
         month = new String("December");
      }
      return month;
   }

   //
   //
   //
   private String setUpBody(){
      String [] dates   = null;
      String [] time    = null;
      StringBuffer body = new StringBuffer();
      StringBuffer date = new StringBuffer();
      Calendar cal      = Calendar.getInstance();
      body.append("<div id = \"temp_div\" style = ");
      body.append("\"width: 80%; height: 220px;\"></div>\n");
      body.append("<br /><br />\n\n");
      body.append("<div id = \"humidity_div\" style = ");
      body.append("\"width: 80%; height: 220px;\"></div>\n");
      body.append("<br /><br />\n\n");
      body.append("<div id = \"pressure_div\" style = ");
      body.append("\"width: 80%; height: 220px;\"></div>\n");
      body.append("<br /><br />\n\n");
      body.append("<div id = \"dp_div\" style = ");
      body.append("\"width: 80%; height: 220px;\"></div>\n");
      body.append("<br /><br />\n\n");
      body.append("<div id = \"hi_div\" style = ");
      body.append("\"width: 80%; height: 220px;\"></div>\n");
      body.append("<br /><br />\n\n");
      body.append("\n</body>\n</html>\n");
      return body.toString();
   }
   
   //
   //
   //
   private String setUpDewpoint(){
      StringBuffer buffer     = new StringBuffer();
      String display          = new String();
      Calendar calendar       = Calendar.getInstance();
      WeatherDatabase mysqldb = MySQLWeatherDatabase.getInstance();
      String calendarString   = calendar.getTime().toString();
      String [] dates         = calendarString.split(" ");
      String month            = this.discernMonth(dates[1]);
      String day              = dates[2];
      String year             = dates[5];
      List<WeatherData> dps   = mysqldb.dewpoint(month,day,year);
      Iterator<WeatherData> it = dps.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         time = time.replace(":",",");
         time = "["+time+"]";
         double dp = wd.englishData();
         if(dp > Thermometer.DEFAULTTEMP){
            display = display.concat("["+time+","+dp+"], ");
         }
      }
      buffer.append("\nfunction drawDewpoint() {\n");
      buffer.append("var dpdata = new google.visualization.DataTable();");
      buffer.append("\ndpdata.addColumn('timeofday', 'X');");
      buffer.append("\ndpdata.addColumn('number','Dewpoint');\n\n");
      buffer.append("dpdata.addRows([\n"+display+"\n]);\n\n");
      buffer.append("var dpoptions = {\nhAxis:{\ntitle: 'Time'\n},\n");
      buffer.append("vAxis:{\ntitle: 'Dewpoint'\n},\ncolors:['green']\n};\n\n");
      buffer.append("var dpchart = new google.visualization.LineChart(document.getElementById('dp_div'));");
      buffer.append("\n\ndpchart.draw(dpdata, dpoptions);\n}");
      return buffer.toString();
   }
  
   //
   //
   //
   private String setUpHeatIndex(){
      StringBuffer buffer     = new StringBuffer();
      String display          = new String();
      Calendar calendar       = Calendar.getInstance();
      WeatherDatabase mysqldb = MySQLWeatherDatabase.getInstance();
      String calendarString   = calendar.getTime().toString();
      String [] dates         = calendarString.split(" ");
      String month            = this.discernMonth(dates[1]);
      String day              = dates[2];
      String year             = dates[5];
      List<WeatherData> his = mysqldb.heatIndex(month,day,year);
      Iterator<WeatherData> it = his.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         time = time.replace(":",",");
         time = "["+time+"]";
         double hi = wd.englishData();
         if(hi > Thermometer.DEFAULTTEMP){
            display = display.concat("["+time+","+hi+"], ");
         }
      }
      if(!display.isEmpty()){
         buffer.append("\nfunction drawHeatIndex() {\n");
         buffer.append("var hidata = new google.visualization.DataTable();");
         buffer.append("\nhidata.addColumn('timeofday', 'X');");
         buffer.append("\nhidata.addColumn('number','Heat Index');\n\n");
         buffer.append("hidata.addRows([\n"+display+"\n]);\n\n");
         buffer.append("var hioptions = {\nhAxis:{\ntitle: 'Time'\n},\n");
         buffer.append("vAxis:{\ntitle: 'Heat Index'\n},\ncolors:['black']\n};\n\n");
         buffer.append("var hichart = new google.visualization.LineChart(document.getElementById('hi_div'));");
         buffer.append("\n\nhichart.draw(hidata, hioptions);\n}");
      }
      return buffer.toString();
   }

   //
   //
   //
   private String setUpHumidity(){
      StringBuffer buffer     = new StringBuffer();
      String display          = new String();
      Calendar calendar       = Calendar.getInstance();
      WeatherDatabase msqldb  = MySQLWeatherDatabase.getInstance();
      String calendarString   = calendar.getTime().toString();
      String [] dates         = calendarString.split(" ");
      String month            = this.discernMonth(dates[1]);
      String day              = dates[2];
      String year             = dates[5];
      WeatherDatabase mysqldb = MySQLWeatherDatabase.getInstance();
      List<WeatherData> hums  = mysqldb.humidity(month,day,year);
      Iterator<WeatherData> it= hums.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         time = time.replace(":",",");
         time = "["+time+"]";
         double hum = wd.percentageData();
         if(hum > Hygrometer.DEFAULTHUMIDITY){
            display = display.concat("["+time+","+hum+"], ");
         }
      }
      buffer.append("\n\nfunction drawHumidity() {\n");
      buffer.append("var humdata = new google.visualization.DataTable();");
      buffer.append("\nhumdata.addColumn('timeofday', 'X');");
      buffer.append("\nhumdata.addColumn('number','Humidity');\n\n");
      buffer.append("humdata.addRows([\n"+display+"\n]);\n\n");
      buffer.append("var humidoptions = {\nhAxis:{\ntitle: 'Time'\n},\n");
      buffer.append("vAxis:{\ntitle: 'Humidity'\n},\ncolors:['blue']\n};\n\n");
      buffer.append("var humchart = new google.visualization.LineChart(document.getElementById('humidity_div'));");
      buffer.append("\n\nhumchart.draw(humdata, humidoptions);\n}");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpPressure(){
      StringBuffer buffer     = new StringBuffer();
      String display          = new String();
      Calendar calendar       = Calendar.getInstance();
      WeatherDatabase mysqldb = MySQLWeatherDatabase.getInstance();
      String calendarString   = calendar.getTime().toString();
      String [] dates         = calendarString.split(" ");
      String month            = this.discernMonth(dates[1]);
      String day              = dates[2];
      String year             = dates[5];
      List<WeatherData> press =
                           mysqldb.barometricPressure(month,day,year);
      Iterator<WeatherData> it = press.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         time = time.replace(":",",");
         time = "["+time+"]";
         double pres = wd.englishData();
         if(pres > Barometer.DEFAULTPRESSURE){
            display = display.concat("["+time+","+pres+"], ");
         }
      }
      buffer.append("\n\nfunction drawPressure() {\n");
      buffer.append("var presdata = new google.visualization.DataTable();");
      buffer.append("\npresdata.addColumn('timeofday', 'X');");
      buffer.append("\npresdata.addColumn('number','Pressure');\n\n");
      buffer.append("presdata.addRows([\n"+display+"\n]);\n\n");
      buffer.append("var presoptions = {\nhAxis:{\ntitle: 'Time'\n},\n");
      buffer.append("vAxis:{\ntitle: 'Pressure'\n}\n};\n\n");
      buffer.append("var preschart = new google.visualization.LineChart(document.getElementById('pressure_div'));");
      buffer.append("\n\npreschart.draw(presdata, presoptions);\n}");  
      return buffer.toString();
   }

   //
   //
   //
   private String setUpTemperature(){
      StringBuffer buffer     = new StringBuffer();
      String display          = new String();
      Calendar calendar       = Calendar.getInstance();
      WeatherDatabase mysqldb = MySQLWeatherDatabase.getInstance();
      String calendarString   = calendar.getTime().toString();
      String [] dates         = calendarString.split(" ");
      String month            = this.discernMonth(dates[1]);
      String day              = dates[2];
      String year             = dates[5];
      List<WeatherData> temps = mysqldb.temperature(month,day,year);
      Iterator<WeatherData> it = temps.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         time = time.replace(":",",");
         time = "["+time+"]";
         double temp = wd.englishData();
         if(temp > Thermometer.DEFAULTTEMP){
            display = display.concat("["+time+","+temp+"], ");
         }
      }
      buffer.append("\nfunction drawTemperature() {\n");
      buffer.append("var tempdata = new google.visualization.DataTable();");
      buffer.append("\ntempdata.addColumn('timeofday', 'X');");
      buffer.append("\ntempdata.addColumn('number','Temp');\n\n");
      buffer.append("tempdata.addRows([\n"+display+"\n]);\n\n");
      buffer.append("var tempoptions = {\nhAxis:{\ntitle: 'Time'\n},\n");
      buffer.append("vAxis:{\ntitle: 'Temperature'\n},\ncolors:['red']\n};\n\n");
      buffer.append("var tempchart = new google.visualization.LineChart(document.getElementById('temp_div'));");
      buffer.append("\n\ntempchart.draw(tempdata, tempoptions);\n}");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpHeader(){
      StringBuffer header = new StringBuffer();
      header.append("<html><head>\n");
      header.append("<title> Lou Rosas:  One Wire Weather Station");
      header.append(" Daily Data</title>\n");
      header.append("<meta http-equiv=\"refresh\" content=\"600\">\n");
      header.append("<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n");
      header.append("<script type=\"text/javascript\">\n");
      header.append("google.charts.load('current',{packages:['corechart','line']});");
      header.append("\n");
      header.append("google.charts.setOnLoadCallback(drawTemperature);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawHumidity);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawPressure);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawDewpoint);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawHeatIndex);");
      return header.toString();
   }
}
