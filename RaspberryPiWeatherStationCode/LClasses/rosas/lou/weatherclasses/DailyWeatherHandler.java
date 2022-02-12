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
   private String _month;
   private String _date;
   private String _year;
   private String _unit;
   private String[] months = {"January", "February", "March",
                              "April", "May", "June", "July",
                              "August", "September", "October",
                              "November", "December" };
   private String [] dates = {"01","02","03","04","05","06","07","08",
                              "09","10","11","12","13","14","15","16",
                              "17","18","19","20","21","22","23","24",
                              "25","26","27","28","29","30","31"};
   private String [] years = {"2022","2021","2020", "2019", "2018", "2017"};
   private String [] units = {"English", "metric", "absolute"};
   List<WeatherData> _temperature;
   List<WeatherData> _humidity;
   List<WeatherData> _barometricPressure;
   List<WeatherData> _dewPoint;
   List<WeatherData> _heatIndex;
   WeatherData       _tempMax;
   WeatherData       _tempMin;
   WeatherData       _tempAvg;
   WeatherData       _humidityMax;
   WeatherData       _humidityMin;
   WeatherData       _humidityAvg;
   WeatherData       _dewPointMax;
   WeatherData       _dewPointMin;
   WeatherData       _dewPointAvg;
   WeatherData       _heatIndexMax;
   WeatherData       _heatIndexMin;
   WeatherData       _heatIndexAvg;

   {
      _month              = null;
      _date               = null;
      _year               = null;
      _unit               = null;
      _temperature        = null;
      _humidity           = null;
      _barometricPressure = null;
      _dewPoint           = null;
      _heatIndex          = null;
      _tempMax            = null;
      _tempMin            = null;
      _tempAvg            = null;
      _humidityMax        = null;
      _humidityMin        = null;
      _humidityAvg        = null;
      _dewPointMax        = null;
      _dewPointMin        = null;
      _dewPointAvg        = null;
      _heatIndexMax       = null;
      _heatIndexMin       = null;
      _heatIndexAvg       = null;
   };

   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      final int OK          = 200;
      OutputStream os       = null;
      try{
         String parts[] = null;
         String query = exchange.getRequestURI().getQuery();
         try{
            parts = query.split("&");
            this._month = parts[0].split("=")[1].trim();
            this._date  = parts[1].split("=")[1].trim();
            this._year  = parts[2].split("=")[1].trim();
            this._unit  = parts[3].split("=")[1].trim();
         }
         catch(NullPointerException npe){
            Calendar calendar     = Calendar.getInstance();
            String calendarString = calendar.getTime().toString();
            String [] dates       = calendarString.split(" ");
            this._month           = this.discernMonth(dates[1]);
            this._date            = dates[2];
            this._year            = dates[5];
            this._unit            = this.units[0];
         }
         //There may be so many more exception that need to be
         //handled...just give them the latest data and be done
         //with it!!!
         catch(Exception e){
            Calendar calendar     = Calendar.getInstance();
            String calendarString = calendar.getTime().toString();
            String [] dates       = calendarString.split(" ");
            this._month           = this.discernMonth(dates[1]);
            this._date            = dates[2];
            this._year            = dates[5];
            this._unit            = this.units[0];
         }
         finally{
            this.setUpMeasurementLists();
         }

         response.append(this.setUpHeader());
         response.append(this.setUpHighLowTable());
         response.append(this.setUpTemperature());
         response.append(this.setUpHumidity());
         response.append(this.setUpPressure());
         response.append(this.setUpDewpoint());
         response.append(this.setUpHeatIndex());
         response.append(this.setUpDataTable());
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
   private List<String> getDewpointStringList(){
      List<String> dewpoints   = new LinkedList<String>();
      List<Calendar> cals      = this.getMainCalendarList();
      //Iterator<WeatherData> it = this._dewPoint.iterator();
      double dewpt             = WeatherData.DEFAULTVALUE;
      //Should just SORT the Collection, and go through it once!!
      Iterator<Calendar> calIt = cals.iterator();
      while(calIt.hasNext()){
         Calendar cal = calIt.next();
         String dewpoint = new String();
         try{
            int hr  = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            Iterator<WeatherData> it = this._dewPoint.iterator();
            boolean found = false;
            while(it.hasNext() && !found){
               WeatherData wd = it.next();
               int hour   = wd.calendar().get(Calendar.HOUR_OF_DAY);
               int minute = wd.calendar().get(Calendar.MINUTE);
               if((hr == hour)&&(min == minute)){
                  if(this._unit.equals(this.units[0])){
                     dewpt = wd.englishData();
                  }
                  else if(this._unit.equals(this.units[1])){
                     dewpt = wd.metricData();
                  }
                  else{
                     dewpt = wd.absoluteData();
                  }
                  if(dewpt > WeatherData.DEFAULTVALUE){
                     dewpoint = "'"+String.format("%.2f", dewpt)+"'";
                  }
                  else{
                     dewpoint = new String("'N/A'");
                  }
                  found = true;
               }
            }
            if(!found){
               dewpoint = new String("'N/A'");
            }
            dewpoints.add(dewpoint);
         }
         catch(NullPointerException npe){}
      }
      return dewpoints;
   }

   //
   //
   //
   private List<String> getHeatIndexStringList(){
      List<String> indices          = new LinkedList<String>();
      List<Calendar> cals           = this.getMainCalendarList();
      //Iterator<WeatherData> it = this._heatIndex.iterator();
      ListIterator<WeatherData> lit = null;
      Iterator<Calendar> calIt      = cals.iterator();
      int index                     = 0;
      double heatIdx                = WeatherData.DEFAULTVALUE;
      while(calIt.hasNext()){
         Calendar cal     = calIt.next();
         String heatIndex = new String();
         try{
            int hr  = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            lit     = this._heatIndex.listIterator(index);
            boolean found = false;
            while(lit.hasNext() && !found){
               WeatherData wd = lit.next();
               int hour   = wd.calendar().get(Calendar.HOUR_OF_DAY);
               int minute = wd.calendar().get(Calendar.MINUTE);
               if((hr == hour) && (min == minute)){
                  if(this._unit.equals(this.units[0])){
                     heatIdx = wd.englishData();
                  }
                  else if(this._unit.equals(this.units[1])){
                     heatIdx = wd.metricData();
                  }
                  else{
                     heatIdx = wd.absoluteData();
                  }
                  if(heatIdx > WeatherData.DEFAULTVALUE){
                     heatIndex="'"+String.format("%.2f", heatIdx)+"'";
                  }
                  else{
                     heatIndex = new String("'N/A'");
                  }
                  found = true;
                  ++index;
               }
            }
            if(!found){
               heatIndex = new String("'N/A'");
            }
            indices.add(heatIndex);
         }
         catch(NullPointerException npe){}
      }
      return indices;
   }

   //
   //
   //
   private List<String> getHumidityStringList(){
      List<String> humis            = new LinkedList<String>();
      List<Calendar> cals           = this.getMainCalendarList();
      ListIterator<WeatherData> lit = null;
      Iterator<Calendar> calIt      = cals.iterator();
      int index                     = 0;
      double humi                   = WeatherData.DEFAULTHUMIDITY;
      while(calIt.hasNext()){
         Calendar cal    = calIt.next();
         String humidity = new String();
         try{
            int hr  = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            lit = this._humidity.listIterator(index);
            boolean found = false;
            while(lit.hasNext() && !found){
               WeatherData wd = lit.next();
               int hour   = wd.calendar().get(Calendar.HOUR_OF_DAY);
               int minute = wd.calendar().get(Calendar.MINUTE);
               if((hr == hour) && (min == minute)){
                  humi = wd.percentageData();
                  if(humi > WeatherData.DEFAULTHUMIDITY){
                     humidity = "'"+String.format("%.2f",humi)+"'";
                  }
                  else{
                     humidity = new String("'N/A'");
                  }
                  found = true;
                  ++index;
               }
            }
            if(!found){
               humidity = new String("'N/A'");
            }
            humis.add(humidity);
         }
         catch(NullPointerException npe){}
      }
      return humis;
   }

   //
   //
   //
   private List<Calendar> getMainCalendarList(){
      List<Calendar> cals      = new LinkedList<Calendar>();
      Iterator<WeatherData> it = this._temperature.iterator();
      while(it.hasNext()){
         cals.add(it.next().calendar());
      }
      return cals;
   }

   //
   //
   //
   private List<String> getPressureStringList(){
      List<String> press            = new LinkedList<String>();
      List<Calendar> cals           = this.getMainCalendarList();
      //Iterator<WeatherData> it = this._barometricPressure.iterator();
      ListIterator<WeatherData> lit = null;
      Iterator<Calendar> calIt   = cals.iterator();
      int index                     = 0;
      double pres                   = WeatherData.DEFAULTVALUE;
      /*
      while(it.hasNext()){
         WeatherData wd  = it.next();
         if(this._unit.equals(this.units[0])){
            pres = wd.englishData();
         }
         else if(this._unit.equals(this.units[1])){
            pres = wd.metricData();
         }
         else{
            pres = wd.absoluteData();
         }
         String pressure = new String();
         if(pres > WeatherData.DEFAULTVALUE){
            pressure = "'" + String.format("%.2f", pres) + "'";
         }
         else{
            pressure = new String("'N/A'");
         }
         press.add(pressure);
      }
      */
      while(calIt.hasNext()){
         Calendar cal    = calIt.next();
         String pressure = new String();
         try{
            int hr   = cal.get(Calendar.HOUR_OF_DAY);
            int min  = cal.get(Calendar.MINUTE);
            lit      = this._barometricPressure.listIterator(index);
            boolean found = false;
            while(lit.hasNext() && !found){
               WeatherData wd = lit.next();
               int hour   = wd.calendar().get(Calendar.HOUR_OF_DAY);
               int minute = wd.calendar().get(Calendar.MINUTE);
               if((hr == hour) && (min == minute)){
                  if(this._unit.equals(this.units[0])){
                     pres = wd.englishData();
                  }
                  else if(this._unit.equals(this.units[1])){
                     pres = wd.metricData();
                  }
                  else{
                     pres = wd.absoluteData();
                  }
                  if(pres > WeatherData.DEFAULTVALUE){
                     pressure = "'"+String.format("%.2f",pres)+"'";
                  }
                  else{
                     pressure = new String("'N/A'");
                  }
                  found = true;
                  ++index;
               }
            }
            if(!found){
               pressure = new String("'N/A'");
            }
            press.add(pressure);
         }
         catch(NumberFormatException npe){}
      }
      return press;
   }

   //
   //
   //
   private List<String> getTemperatureStringList(){
      List<String> temps       = new LinkedList<String>();
      List<Calendar> cals      = this.getMainCalendarList();
      Iterator<WeatherData> it = this._temperature.iterator();
      double temp              = WeatherData.DEFAULTVALUE;
      while(it.hasNext()){
         WeatherData wd    = it.next();
         if(this._unit.equals(this.units[0])){
            temp = wd.englishData();
         }
         else if(this._unit.equals(this.units[1])){
            temp = wd.metricData();
         }
         else{
            temp = wd.absoluteData();
         }
         String stringtemp = new String();
         if(temp > WeatherData.DEFAULTVALUE){
            stringtemp = "'" + String.format("%.2f", temp) + "'";
         }
         else{
            stringtemp = new String("'N/A'");
         }
         temps.add(stringtemp);
      }
      return temps;
   }

   //
   //
   //
   private List<String> getTimeStringList(){
      List<String> times = new LinkedList<String>();
      Iterator<WeatherData> it = this._temperature.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         time = time.replace(":",",");
         time = "["+time+"]";
         times.add(time);
      }
      return times;
   }

   //
   //
   //
   private String setUpBody(){
      StringBuffer body = new StringBuffer();
      body.append("\n<body>");
      body.append(this.setUpTheForm());
      body.append("<div id = \"lowhigh_div\"></div>\n");
      body.append("<br /><br />\n\n");
      body.append("<div class = \"tab\">\n");
      body.append("<button class=\"tablinks\" onclick=\"openData(event,'Graph')\">Graph</button>");
      body.append("\n");
      body.append("<button class=\"tablinks\" onclick=\"openData(event,'Tabular')\">Table</button>");
      body.append("\n</div>\n\n");
      body.append("<div id=\"Graph\" class=\"tabcontent\">\n");
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
      body.append("<br /><br /></div>\n\n");
      body.append("<div id=\"Tabular\" class=\"tabcontent\">\n");
      body.append("<div id = \"table_div\"></div>\n");
      body.append("</div>\n\n");
      body.append("\n</body>\n</html>\n");
      return body.toString();
   }

   //Unlike the graph, everything needs to go into this table.
   //I will put everthing in this table, regardless of a good
   //measurement.
   private String setUpDataTable(){
      StringBuffer buffer = new StringBuffer();
      String display      = new String();
      //Somehow, get the measurement data in String form
      List<String> times = this.getTimeStringList();
      List<String> temps = this.getTemperatureStringList();
      List<String> humis = this.getHumidityStringList();
      List<String> press = this.getPressureStringList();
      List<String> dewps = this.getDewpointStringList();
      List<String> heats = this.getHeatIndexStringList();

      buffer.append("\nfunction drawAsTable() {");
      buffer.append("\nvar i;");
      buffer.append("\nvar theData = new google.visualization.DataTable();");
      buffer.append("\ntheData.addColumn('timeofday','Time');");
      buffer.append("\ntheData.addColumn('string','Temperature');");
      buffer.append("\ntheData.addColumn('string','Humidity');");
      buffer.append("\ntheData.addColumn('string','Pressure');");
      buffer.append("\ntheData.addColumn('string','Dewpoint');");
      buffer.append("\ntheData.addColumn('string','Heat Index');");
      buffer.append("\n\ntheData.addRows([\n");

      Iterator<String> ittimes = times.iterator();
      Iterator<String> ittemps = temps.iterator();
      Iterator<String> ithumis = humis.iterator();
      Iterator<String> itpress = press.iterator();
      Iterator<String> itdewps = dewps.iterator();
      Iterator<String> itheats = heats.iterator();
      while(ittemps.hasNext()){
         buffer.append("[");
         buffer.append(ittimes.next() + "," + ittemps.next() + ",");
         buffer.append(ithumis.next() + "," + itpress.next() + ",");
         buffer.append(itdewps.next() + "," + itheats.next());
         buffer.append("],");
      }
      buffer.append("\n]);\n");
      buffer.append("\nvar theTable = new google.visualization.Table(document.getElementById('table_div'));");
      buffer.append("\n\n");
      buffer.append("theTable.draw(theData,{showRowNumber:false,width:'80%',height:'85%'});");
      buffer.append("\n}\n\n");
      buffer.append("function openData(evt,dataType){\n");
      buffer.append("var i, tabcontent, tablinks;\n");
      buffer.append("tabcontent=document.getElementsByClassName(\"tabcontent\");");
      buffer.append("\nfor(i=0;i<tabcontent.length;i++){");
      buffer.append("\ntabcontent[i].style.display = \"none\";");
      buffer.append("\n}\n");
      buffer.append("tablinks = document.getElementsByClassName(\"tablinks\");");
      buffer.append("\nfor(i=0;i<tablinks.length;i++){\n");
      buffer.append("tablinks[i].className=tablinks[i].className.replace(\" active\",\"\");");
      buffer.append("\n}\n");
      buffer.append("document.getElementById(dataType).style.display = \"block\";");
      buffer.append("\nevt.currentTarget.className += \" active\";");
      buffer.append("\n}\n");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpDewpoint(){
      StringBuffer buffer     = new StringBuffer();
      String display          = new String();
      Iterator<WeatherData> it = this._dewPoint.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         double dp = WeatherData.DEFAULTVALUE;
         time = time.replace(":",",");
         time = "["+time+"]";
         if(this._unit.equals(this.units[0])){
            dp = wd.englishData();
         }
         else if(this._unit.equals(this.units[1])){
            dp = wd.metricData();
         }
         else{
            dp = wd.absoluteData();
         }
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
   private String setUpDewPointRow(){
      WeatherData     data    = null;
      StringBuffer    buffer  = new StringBuffer();
      double min = WeatherData.DEFAULTVALUE;
      double max = WeatherData.DEFAULTVALUE;
      double avg = WeatherData.DEFAULTVALUE;
      if(this._unit.equals(this.units[0])){
         min = this._dewPointMin.englishData();
         max = this._dewPointMax.englishData();
         avg = this._dewPointAvg.englishData();
      }
      else if(this._unit.equals(this.units[1])){
         min = this._dewPointMin.metricData();
         max = this._dewPointMax.metricData();
         avg = this._dewPointAvg.metricData();
      }
      else{
         min = this._dewPointMin.absoluteData();
         max = this._dewPointMax.absoluteData();
         avg = this._dewPointAvg.absoluteData();
      }

      buffer.append("['Dew Point', ");
      if(min > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",min)+"', ");
      }
      else{
         buffer.append("'N/A', ");
      }
      if(max > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",max)+"', ");
      }
      else{
         buffer.append("'N/A', ");
      }
      if(avg > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",avg)+"'],\n");
      }
      else{
         buffer.append("'N/A'],\n");
      }
      return buffer.toString();
   }

   //
   //
   //
   private String setUpHeatIndex(){
      StringBuffer buffer      = new StringBuffer();
      String display           = new String();
      Iterator<WeatherData> it = this._heatIndex.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         double hi = WeatherData.DEFAULTVALUE;
         time = time.replace(":",",");
         time = "["+time+"]";
         if(this._unit.equals(this.units[0])){
            hi = wd.englishData();
         }
         else if(this._unit.equals(this.units[1])){
            hi = wd.metricData();
         }
         else{
            hi = wd.absoluteData();
         }
         if(hi > Thermometer.DEFAULTTEMP){
            display = display.concat("["+time+","+hi+"], ");
         }
      }
      buffer.append("\nfunction drawHeatIndex() {\n");
      buffer.append("var hidata = new google.visualization.DataTable();");
      buffer.append("\nhidata.addColumn('timeofday', 'X');");
      buffer.append("\nhidata.addColumn('number','Heat Index');\n\n");
      buffer.append("hidata.addRows([\n"+display+"\n]);\n\n");
      buffer.append("var hioptions = {\nhAxis:{\ntitle: 'Time'\n},\n");
      buffer.append("vAxis:{\ntitle: 'Heat Index'\n},\ncolors:['black']\n};\n\n");
      buffer.append("var hichart = new google.visualization.LineChart(document.getElementById('hi_div'));");
      buffer.append("\n\nhichart.draw(hidata, hioptions);\n}");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpHeatIndexRow(){
      WeatherData     data    = null;
      StringBuffer    buffer  = new StringBuffer();
      double min = WeatherData.DEFAULTVALUE;
      double max = WeatherData.DEFAULTVALUE;
      double avg = WeatherData.DEFAULTVALUE;
      if(this._unit.equals(this.units[0])){
         min = this._heatIndexMin.englishData();
         max = this._heatIndexMax.englishData();
         avg = this._heatIndexAvg.englishData();
      }
      else if(this._unit.equals(this.units[1])){
         min = this._heatIndexMin.metricData();
         max = this._heatIndexMax.metricData();
         avg = this._heatIndexAvg.metricData();
      }
      else{
         min = this._heatIndexMin.absoluteData();
         max = this._heatIndexMax.absoluteData();
         avg = this._heatIndexAvg.absoluteData();
      }
      buffer.append("['Heat Index', ");
      if(min > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",min)+"', ");
      }
      else{
         buffer.append("'N/A', ");
      }
      if(max > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",max)+"', ");
      }
      else{
         buffer.append("'N/A',");
      }
      if(avg > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",avg)+"'],\n");
      }
      else{
         buffer.append("'N/A'],\n");
      }
      return buffer.toString();
   }

   //
   //
   //
   private String setUpHighLowTable(){
      StringBuffer buffer     = new StringBuffer();
      buffer.append("\n\nfunction drawHighLowTable() {\n");
      buffer.append("var hldata = new google.visualization.DataTable();");
      buffer.append("hldata.addColumn('string', 'Measurement');\n");
      buffer.append("hldata.addColumn('string', 'Low');\n");
      buffer.append("hldata.addColumn('string', 'High');\n");
      buffer.append("hldata.addColumn('string', 'Average');\n");
      buffer.append("hldata.addRows([\n");
      buffer.append(this.setUpTemperatureRow());
      buffer.append(this.setUpHumidityRow());
      buffer.append(this.setUpDewPointRow());
      buffer.append(this.setUpHeatIndexRow());
      buffer.append("]);\n");
      buffer.append("var hltable = new google.visualization.Table(document.getElementById('lowhigh_div'));");
      buffer.append("\n\n");
      buffer.append("hltable.draw(hldata,{showRowNumber:false,width:'20%',height:'20%'});");
      buffer.append("\n}\n");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpHumidity(){
      StringBuffer buffer     = new StringBuffer();
      String display          = new String();
      Iterator<WeatherData> it = this._humidity.iterator();
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
   private String setUpHumidityRow(){
      WeatherData     data    = null;
      StringBuffer    buffer  = new StringBuffer();
      double min = this._humidityMin.percentageData();
      double max = this._humidityMax.percentageData();
      double avg = this._humidityAvg.percentageData();

      buffer.append("['Humidity', ");
      if(min > WeatherData.DEFAULTHUMIDITY){
         buffer.append("'" + String.format("%.2f",min) + "', ");
      }
      else{
         buffer.append("'N/A', ");
      }
      if(max > WeatherData.DEFAULTHUMIDITY){
         buffer.append("'"+String.format("%.2f",max)+"',");
      }
      else{
         buffer.append("'N/A',");
      }
      if(avg > WeatherData.DEFAULTHUMIDITY){
         buffer.append("'"+String.format("%.2f",avg)+"'],\n");
      }
      else{
         buffer.append("'N/A'],\n");
      }

      return buffer.toString();
   }

   //Might as well put all (or most) of the database "hits" in one
   //method
   //
   private void setUpMeasurementLists(){
      WeatherDatabase mysqldb = MySQLWeatherDatabase.getInstance();
      this._temperature =
               mysqldb.temperature(this._month,this._date,this._year);
      this._humidity =
                  mysqldb.humidity(this._month,this._date,this._year);
      this._barometricPressure =
        mysqldb.barometricPressure(this._month,this._date,this._year);
      this._dewPoint =
                  mysqldb.dewpoint(this._month,this._date,this._year);
      this._heatIndex =
                 mysqldb.heatIndex(this._month,this._date,this._year);
      //Put together the Max,Min,Ave stuff, as well...
      this._tempMax =
            mysqldb.temperatureMax(this._month,this._date,this._year);
      this._tempMin =
            mysqldb.temperatureMin(this._month,this._date,this._year);
      this._tempAvg =
            mysqldb.temperatureAvg(this._month,this._date,this._year);
      _humidityMax  = mysqldb.humidityMax(_month, _date, _year);
      _humidityMin  = mysqldb.humidityMin(_month, _date, _year);
      _humidityAvg  = mysqldb.humidityAvg(_month, _date, _year);
      _dewPointMax  = mysqldb.dewPointMax(_month, _date, _year);
      _dewPointMin  = mysqldb.dewPointMin(_month, _date, _year);
      _dewPointAvg  = mysqldb.dewPointAvg(_month, _date, _year);
      _heatIndexMax = mysqldb.heatIndexMax(_month, _date, _year);
      _heatIndexMin = mysqldb.heatIndexMin(_month, _date, _year);
      _heatIndexAvg = mysqldb.heatIndexAvg(_month, _date, _year);
   }

   //
   //
   //
   private String setUpPressure(){
      StringBuffer buffer      = new StringBuffer();
      String display           = new String();
      Iterator<WeatherData> it = this._barometricPressure.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         double pres = WeatherData.DEFAULTVALUE;
         time = time.replace(":",",");
         time = "["+time+"]";
         if(this._unit.equals(this.units[0])){
            pres = wd.englishData();
         }
         else if(this._unit.equals(this.units[1])){
            pres = wd.metricData();
         }
         else{
            pres = wd.absoluteData();
         }
         if(pres > WeatherData.DEFAULTVALUE){
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
      Iterator<WeatherData> it = this._temperature.iterator();
      while(it.hasNext()){
         WeatherData wd = it.next();
         String calString = wd.calendar().getTime().toString();
         String time = calString.split(" ")[3].trim();
         double temp = WeatherData.DEFAULTVALUE;
         time = time.replace(":",",");
         time = "["+time+"]";
         if(this._unit.equals(this.units[0])){
            temp = wd.englishData();
         }
         else if(this._unit.equals(this.units[1])){
            temp = wd.metricData();
         }
         else{
            temp = wd.absoluteData();
         }
         if(temp > WeatherData.DEFAULTVALUE){
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
      buffer.append("\n\ntempchart.draw(tempdata, tempoptions);\n");
      buffer.append("}");
      return buffer.toString();
   }

   //
   //
   //
   private String setUpTemperatureRow(){
      WeatherData  data    = null;
      StringBuffer buffer  = new StringBuffer();
      double min = WeatherData.DEFAULTVALUE;
      double max = WeatherData.DEFAULTVALUE;
      double avg = WeatherData.DEFAULTVALUE;
      if(this._unit.equals(this.units[0])){
         min = this._tempMin.englishData();
         max = this._tempMax.englishData();
         avg = this._tempAvg.englishData();
      }
      else if(this._unit.equals(this.units[1])){
         min = this._tempMin.metricData();
         max = this._tempMax.metricData();
         avg = this._tempAvg.metricData();
      }
      else{
         min = this._tempMin.absoluteData();
         max = this._tempMax.absoluteData();
         avg = this._tempAvg.absoluteData();
      }
      buffer.append("['Temperature', ");
      if(min > WeatherData.DEFAULTVALUE){
         String temp = String.format("%.2f",min);
         buffer.append("'"+temp+"', ");
      }
      else{
         buffer.append("'N/A', ");
      }
      if(max > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",max)+"',");
      }
      else{
         buffer.append("'N/A', ");
      }
      if(avg > WeatherData.DEFAULTVALUE){
         buffer.append("'"+String.format("%.2f",avg)+"'],\n");
      }
      else{
         buffer.append("'N/A'],\n");
      }
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
      //header.append("<meta http-equiv=\"refresh\" content=\"600\">\n");
      header.append("<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n");
      header.append("<script type=\"text/javascript\">\n");
      header.append("google.charts.load('current',{packages:['corechart','line']});");
      header.append("\n");
      header.append("google.charts.load('current',{packages:['table']});");
      header.append("\ngoogle.charts.setOnLoadCallback(drawHighLowTable);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawTemperature);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawHumidity);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawPressure);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawDewpoint);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawHeatIndex);");
      header.append("\ngoogle.charts.setOnLoadCallback(drawAsTable);");
      return header.toString();
   }

   //
   //
   //
   private String setUpTheForm(){
      StringBuffer form = new StringBuffer();
      form.append("\n");
      //form.append("<form action=\"http://68.230.27.225:8000/daily\"");
      form.append("<form action=\"http://onewireweather.us:8500/daily\"");
      //form.append("<form action=\"http://www.onewireweather.us/daily\"");
      form.append(" method=\"GET\">\n");
      form.append("<select name=\"month\">\n");
      for(int i = 0; i < months.length; i++){
         form.append("<option value=\"" + months[i] + "\"");
         if(months[i].equals(this._month)){
            form.append(" selected");
         }
         form.append(">" + months[i] + "</option>\n");
      }
      form.append("</select>\n");
      form.append("<select name=\"date\">\n");
      for(int i = 0; i < dates.length; i++){
         form.append("<option value=\"" + dates[i] + "\"");
         if(dates[i].equals(this._date)){
            form.append(" selected");
         }
         form.append(">" + (i+1) + "</option>\n");
      }
      form.append("</select>\n");
      form.append("<select name=\"year\">\n");
      for(int i = 0; i < years.length; i++){
         form.append("<option value=\"" + years[i] + "\"");
         if(years[i].equals(this._year)){
            form.append(" selected");
         }
         form.append(">" + years[i] + "</option>\n");
      }
      form.append("</select>\n");
      form.append("<select name=\"units\">\n");
      for(int i = 0; i < units.length; i++){
         form.append("<option value=\"" + units[i]+ "\"");
         if(units[i].equals(this._unit)){
            form.append(" selected");
         }
         form.append(">" + units[i] + "</option>\n");
      }
      form.append("</select>\n");
      form.append("<button type=\"submit\">View</button><br>\n");
      form.append("</form>\n\n");
      return form.toString();
   }
}
