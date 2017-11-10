/////////////////////////////////////////////////////////////////////
/*
<GNU Stuff to go here>
*/
/////////////////////////////////////////////////////////////////////
package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import rosas.lou.weatherclasses.*;

public class DailyWeatherHandler extends BaseWeatherHandler
implements HttpHandler{
   private String[] months = {"January", "February", "March", 
                              "April", "May", "June", "July",
                              "August", "September", "October",
                              "November", "December" };
   private List<Double> dewPointData;
   private List<String> dewPointTimes;
   private List<Double> heatIndexData;
   private List<String> heatIndexTimes;
   private List<Double> humidityData;
   private List<String> humidityTimes;
   private List<Double> pressureData;
   private List<String> pressureTimes;
   private List<Double> temperatureData;
   private List<String> temperatureTimes;
   private String []    dates;
   
   {
      dewPointData     = null;
      dewPointTimes    = null;
      humidityData     = null;
      heatIndexData    = null;
      heatIndexTimes   = null;
      humidityTimes    = null;
      pressureData     = null;
      pressureTimes    = null;
      temperatureData  = null;
      temperatureTimes = null;
      dates            = null;
   }

   //
   //Implementation of the HttpHandler interface
   //
   public void handle(HttpExchange exchange){
      StringBuffer response = new StringBuffer();
      try{
         /*
         //response.append(this.setUpData());
         response.append(this.setUpTemperature());
         response.append(this.setUpHumidity());
         response.append(this.setUpPressure());
         response.append(this.setUpDewpoint());
         response.append(this.setUpHeatIndex());
         response.append("\n</script></head>\n");
         */
         this.setDates();
         response.append(this.setUpHeader());
         response.append(this.setUpTemperature());
         response.append(this.setUpHumidity());
         response.append(this.setUpPressure());
         response.append(this.setUpDewpoint());
         response.append(this.setUpHeatIndex());
         response.append("\n</script>\n</head>\n");
         response.append(this.setUpBody());
         String send = response.toString();
         exchange.sendResponseHeaders(200, send.length());
         OutputStream os = exchange.getResponseBody();
         os.write(send.getBytes());
         os.close();
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
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
   private Double getValue(String data, Units type){
      Double value = Double.NEGATIVE_INFINITY;
      String current = null;
      try{
         if(type == Units.METRIC || type == Units.NULL){
            current = data.split(",")[4].trim();
         }
         else if(type == Units.ENGLISH){
            current = data.split(",")[5].trim();
         }
         else{
            current = data.split(",")[6].trim();
         }
         value   = Double.parseDouble(current);
      }
      catch(NumberFormatException nfe){}
      finally{
         return value;
      }
   }
   
   //
   //
   //
   private void setDates(){
      try{
         this.dates = this.getDate().split(" ");
      }
      catch(NullPointerException npe){
         this.dates = null;
      }
   }

   //
   //
   //
   private Integer[] separateOutTime(String data){
      String [] line = data.split(",");
      String completeTime = line[3].trim();
      String time = completeTime.split(" ")[0].trim();
      String [] hms = time.split(":");
      Integer[] theHms = new Integer[3];
      try{
         theHms[0] = new Integer(Integer.parseInt(hms[0].trim()));
         theHms[1] = new Integer(Integer.parseInt(hms[1].trim()));
         theHms[2] = new Integer(Integer.parseInt(hms[2].trim()));
      }
      catch(NumberFormatException nfe){}
      finally{
         return theHms;
      }
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
      /*
      try{
         date.append(this.getDate());
         dates = this.getDate().split(" ");
         time = dates[3].split(":");
         
         int month = cal.get(Calendar.MONTH);
         date.append(months[month] + ", ");
         int day = cal.get(Calendar.DAY_OF_MONTH);
         date.append(Integer.toString(day) + ", ");
         int year = cal.get(Calendar.YEAR);
         date.append(Integer.toString(year));
         
         String month = this.discernMonth(dates[1]);
         this.setTemperatureData(month, dates[2], dates[5]);
         body.append("\n<body>");
         body.append("<h2>" + date + "</h2>");
         body.append("\n<h2>Date:  " + month + "</h2>");
         body.append("<h2> "+dates[2]+"  "+dates[3]+" </h2>");
         body.append("<h2> "+time[0]+","+time[1]+","+time[2]);
         body.append("\n</body>\n</html>");
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         date = new StringBuffer("No Date!!!");
         body.append("\n<body>");
         body.append("<h2>" + date + "</h2>");
         body.append("\n</body>\n</html>");
      }
      finally{
         return body.toString();
      }
      */
      body.append("\n<body>\n");
      body.append("<div id = \"humidity_div\" style = ");
      body.append("\"width: 80%; height: 220px;\"></div>\n");
      body.append("<br /><br />\n\n");
      body.append("<div id = \"temp_div\" style = ");
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
      StringBuffer buffer = new StringBuffer();
      String month        = this.discernMonth(this.dates[1]);
      String display      = new String();
      this.setDewpointData(month, dates[2], dates[5]);
      Iterator<String> times    = this.dewPointTimes.iterator();
      Iterator<Double> dewpoint = this.dewPointData.iterator();
      while(dewpoint.hasNext()){
         display = display.concat("[" + times.next() + ",");
         display = display.concat(dewpoint.next() +"], "); 
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
      StringBuffer buffer = new StringBuffer();
      String month        = this.discernMonth(this.dates[1]);
      String display      = new String();
      this.setHeatIndexData(month, dates[2], dates[5]);
      Iterator<String> times     = this.heatIndexTimes.iterator();
      Iterator<Double> heatIndex = this.heatIndexData.iterator(); 
      while(heatIndex.hasNext()){
         Double hi   = heatIndex.next();
         String time = times.next();
         if(hi > Thermometer.DEFAULTTEMP){
            display = display.concat("[" + time + ",");
            display = display.concat(hi +"], ");
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
   private String setUpHumidity(){
      StringBuffer buffer = new StringBuffer();
      String month        = this.discernMonth(this.dates[1]);
      String display      = new String();
      this.setHumidityData(month, dates[2], dates[5]);
      Iterator<String> times    = this.humidityTimes.iterator();
      Iterator<Double> humidity = this.humidityData.iterator();
      while(humidity.hasNext()){
         display = display.concat("[" + times.next() + ",");
         display = display.concat(humidity.next() +"], ");         
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
      StringBuffer buffer = new StringBuffer();
      String month        = this.discernMonth(this.dates[1]);
      String display      = new String();
      this.setPressureData(month, dates[2], dates[5]);
      Iterator<String> times    = this.pressureTimes.iterator();
      Iterator<Double> pressure = this.pressureData.iterator();
      while(pressure.hasNext()){
         display = display.concat("[" + times.next() + ",");
         display = display.concat(pressure.next() +"], ");
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
      StringBuffer buffer = new StringBuffer();
      String month        = this.discernMonth(this.dates[1]);
      String display      = new String();
      this.setTemperatureData(month, dates[2], dates[5]);
      Iterator<String> times = this.temperatureTimes.iterator();
      Iterator<Double> temps = this.temperatureData.iterator();
      while(temps.hasNext()){
         display = display.concat("[" + times.next() + ",");
         display = display.concat(temps.next() +"], ");
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
   private void setDewpointData
   (
      String month,
      String day,
      String year
   ){
      Double dewpoint     = Double.NEGATIVE_INFINITY;
      Integer[] hms       = null;
      List<String> dpData = null;
      
      WeatherStorage ws = WeatherStorage.getInstance();
      String command=new String("SELECT * from");
      command=command.concat(" dewpointdata where month = \'");
      command=command.concat(month + "\' AND day = \'" + day +"\'");
      command=command.concat(" AND year = \'" + year + "\'");
      try{
         this.dewPointData  = new LinkedList<Double>();
         this.dewPointTimes = new LinkedList<String>();
         dpData             = ws.requestData(command);
         Iterator<String> it = dpData.iterator();
         while(it.hasNext()){
            String values = it.next();
            hms = this.separateOutTime(values);
            //The Dewpoint is stored in the METRIC location
            dewpoint = this.getValue(values, Units.ENGLISH);
            this.dewPointData.add(dewpoint);
            String time = new String("["+hms[0]+","+hms[1]+",");
            time = time.concat(hms[2]+"]");
            this.dewPointTimes.add(time);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }      
   }
   
   //
   //
   //
   private void setHeatIndexData
   (
      String month,
      String day,
      String year
   ){
      Double heatIndex      = Double.NEGATIVE_INFINITY;
      Integer[] hms         = null;
      List<String> hiData   = null;
      
      WeatherStorage ws = WeatherStorage.getInstance();
      String command=new String("SELECT * from");
      command=command.concat(" heatindexdata where month = \'");
      command=command.concat(month + "\' AND day = \'" + day +"\'");
      command=command.concat(" AND year = \'" + year + "\'");
      try{
         this.heatIndexData  = new LinkedList<Double>();
         this.heatIndexTimes = new LinkedList<String>();
         hiData              = ws.requestData(command);
         Iterator<String> it = hiData.iterator();
         while(it.hasNext()){
            String values = it.next();
            hms = this.separateOutTime(values);
            heatIndex = this.getValue(values, Units.ENGLISH);
            this.heatIndexData.add(heatIndex);
            String time = new String("["+hms[0]+","+hms[1]+",");
            time = time.concat(hms[2]+"]");
            this.heatIndexTimes.add(time);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }      
   }
   
   //
   //
   //
   private void setHumidityData
   (
      String month,
      String day,
      String year
   ){
      Double humidity           = Double.NEGATIVE_INFINITY;
      Integer[] hms             = null;
      List<String> humidData    = null;
      
      WeatherStorage ws = WeatherStorage.getInstance();
      String command=new String("SELECT * from");
      command=command.concat(" humiditydata where month = \'");
      command=command.concat(month + "\' AND day = \'" + day +"\'");
      command=command.concat(" AND year = \'" + year + "\'");
      try{
         this.humidityData  = new LinkedList<Double>();
         this.humidityTimes = new LinkedList<String>();
         humidData       = ws.requestData(command);
         Iterator<String> it = humidData.iterator();
         while(it.hasNext()){
            String values = it.next();
            hms = this.separateOutTime(values);
            //The Relative Humidity is stored in the METRIC location
            humidity = this.getValue(values, Units.METRIC);
            this.humidityData.add(humidity);
            String time = new String("["+hms[0]+","+hms[1]+",");
            time = time.concat(hms[2]+"]");
            this.humidityTimes.add(time);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   //
   //
   //
   private void setPressureData
   (
      String month,
      String day,
      String year
   ){
      Double pressure           = Double.NEGATIVE_INFINITY;
      Integer[] hms             = null;
      List<String> pressureData = null;
      
      WeatherStorage ws = WeatherStorage.getInstance();
      String command=new String("SELECT * from");
      command=command.concat(" pressuredata where month = \'");
      command=command.concat(month + "\' AND day = \'" + day +"\'");
      command=command.concat(" AND year = \'" + year + "\'");
      try{
         this.pressureData  = new LinkedList<Double>();
         this.pressureTimes = new LinkedList<String>();
         pressureData        = ws.requestData(command);
         Iterator<String> it = pressureData.iterator();
         while(it.hasNext()){
            String values = it.next();
            hms = this.separateOutTime(values);
            pressure = this.getValue(values, Units.ENGLISH);
            this.pressureData.add(pressure);
            String time = new String("["+hms[0]+","+hms[1]+",");
            time = time.concat(hms[2]+"]");
            this.pressureTimes.add(time);
         }
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
      }
   }
   
   //
   //
   //
   private void setTemperatureData
   (
      String month,
      String day,
      String year
   ){
      Double  temp          = Double.NEGATIVE_INFINITY;
      Integer[] hms         = null;
      List<String> tempData = null;

      WeatherStorage ws = WeatherStorage.getInstance();
      String command=new String("SELECT * from");
      command=command.concat(" temperaturedata where month = \'");
      command=command.concat(month + "\' AND day = \'" + day +"\'");
      command=command.concat(" AND year = \'" + year + "\'");
      try{
         this.temperatureData  = new LinkedList<Double>();
         this.temperatureTimes = new LinkedList<String>();
         tempData              = ws.requestData(command);
         Iterator<String> it   = tempData.iterator();
         while(it.hasNext()){
            String values = it.next();
            hms  = this.separateOutTime(values);
            temp = this.getValue(values, Units.ENGLISH);
            this.temperatureData.add(temp);
            String time = new String("["+hms[0]+","+hms[1]+",");
            time = time.concat(hms[2]+"]");
            this.temperatureTimes.add(time);
            
         }
      }
      catch(NullPointerException npe){ npe.printStackTrace(); }
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
