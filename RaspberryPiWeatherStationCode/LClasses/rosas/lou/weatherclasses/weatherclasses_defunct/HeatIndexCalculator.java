package weatherclasses;

import java.lang.*;
import java.text.*;
import java.io.*;
import java.util.*;
import myclasses.*;
import weatherclasses.*;

/*****************************************************************************
** A class by Lou Rosas
** Initial release: 10/02/01
** Major changes due to the "First Addendum"
** "First Addendum" release: 11/25/01
** New methods were added to accomodate the "First Addendum" changes.  These
** Are clearly marked in the header of each.
** Other minor changes due to the "First Addendum" additions such as changes
** to methods and additions of attributes local to the class and/or a
** particular method are not so well marked.  This error previously mentioned
** will be corrected in the next improvements release, "Second Addendum".
** This will include an addition to the main header with a date comment,
** as well as additions to the headers of the methods affected by the changes.
*****************************************************************************/
public class HeatIndexCalculator extends Observable{

    private int     temperatureMode;
    private double  heat_index;
    private int     heat_index_int;
    private double  temperature;
    private double  relative_humidity;
    private String  current_date;
    private String  current_file;

    public static void main(String[] args){
        new HeatIndexCalculator();
    }

    /************************************************************************
    **  This is the default constructor.  It constructs the View and sets the
    **  temperature mode.
    *************************************************************************/
    public HeatIndexCalculator(){
        this.addObserver(new HeatIndexCalculatorView(this));
        this.setUpPrivateValues();
    }
    
    /************************************************************************
    **  There is nothing to put in this constructor yet, will leave the basic
    **  skeleton in case the design needs to be altered in any way that would
    **  require this type of constructor.
    *************************************************************************/
    public HeatIndexCalculator(HeatIndexCalculatorView theView){
        this.addObserver(theView);
        this.setUpPrivateValues();
    }

    /*************************************************************************
    **  Sets the current temperature mode.  By default the temperature mode
    **  is set to DEGREES_F.
    *************************************************************************/
    public void setMode(int temperature_mode){
        if(temperature_mode == HeatIndexCalculatorView.DEGREES_F ||
           temperature_mode == HeatIndexCalculatorView.DEGREES_C)
            temperatureMode = temperature_mode;
        else //keep the default if erratic data
            temperatureMode = HeatIndexCalculatorView.DEGREES_F;
    }

    /*************************************************************************
    **  All this method does is call the private version of this method,
    **  this is way of guranteeing no external object has access or comes
    **  in direct content of the private data:  insures one more level of
    **  privacy
    *************************************************************************/
    public void calculateHeatIndex(HeatIndexData index_data){
        this.setHeatIndex(index_data);  //set it one more layer deep
    }

    /*************************************************************************
    **  This is part of the "First Addendum" additions.  All this method is
    **  to do is to convert the values to the current scale.  If the current
    **  data that is received is at Double.MAX_VALUE, that data is assumed to
    **  corrupted data or no data registered for that particular piece of
    **  data, and therefore, nothing will be converted.
    *************************************************************************/
    public void resetHeatIndex(HeatIndexCalculatedData current_data){
        double temp  = current_data.getTemperature();
        double humid = current_data.getHumidity();
        double hi    = current_data.getHeatIndex();

        double DIVIDE_FACTOR = 10.0;
        int    MULTIPLIER    = 10;

        HeatIndexCalculatedData returnedData = new HeatIndexCalculatedData();

        if(temperatureMode == HeatIndexCalculatorView.DEGREES_C){
            if(temp != Double.MAX_VALUE){
                temperature = (5.0/9.0)*(temp-32);
                temperature = this.round(temperature,2);
                temperature *= MULTIPLIER;
                int temp_value = (int)temperature;
                temperature = temp_value/DIVIDE_FACTOR;
                returnedData.setTemperature(temperature);
            }
            else  returnedData.setTemperature(temp);
            if(humid != Double.MAX_VALUE){
                relative_humidity = humid;
                returnedData.setHumidity((int)relative_humidity);
            }
            else returnedData.setHumidity(humid);
            if(hi != Double.MAX_VALUE){
                heat_index = (5.0/9.0)*(hi-32);
                heat_index = this.round(heat_index,2);
                heat_index *= MULTIPLIER;
                int heat_temp = (int)heat_index;
                heat_index = heat_temp/DIVIDE_FACTOR;
                heat_index_int = (int)heat_index;
                returnedData.setHeatIndex(heat_index);
                returnedData.setHeatIndex(heat_index_int);
            }
            else{
                returnedData.setHeatIndex(hi);
                returnedData.setHeatIndex(Integer.MAX_VALUE);
            }
        }
        else{
            if(temp != Double.MAX_VALUE){
                temperature = (9.0/5.0)*temp+32;
                temperature = this.round(temperature,1);
                int temp_value = (int)temperature;
                temperature = temp_value/1.0;
                returnedData.setTemperature(temperature);
            }
            else  returnedData.setTemperature(temp);
            if(humid != Double.MAX_VALUE){
                relative_humidity = humid;
                returnedData.setHumidity(relative_humidity);
            }
            else  returnedData.setHumidity(humid);
            if(hi != Double.MAX_VALUE){
                heat_index = (9.0/5.0)*hi+32;
                heat_index = this.round(heat_index,1);
                heat_index_int = (int)heat_index;
                heat_index = heat_index_int/1.0;
                returnedData.setHeatIndex(heat_index);
                returnedData.setHeatIndex(heat_index_int);
            }
            else{
                returnedData.setHeatIndex(hi);
                returnedData.setHeatIndex((int)hi);
            }
        }
        this.setChanged();
        this.notifyObservers(returnedData);
        this.clearChanged();
    }


    /*************************************************************************
    **  This is part of the "First Addendum" additions.  All this method is
    **  to do is to save off the data in an SQL format. This is for the
    **  the purpose of taking the saved data and displaying it in a database
    **  format (such as a *.mdb).
    **  Normally would do: 
    **  1.  FileOutputStream fos = new FileOutputStream("A:weatherData.sql");
    **  2.  OutputStreamWriter osw = new OutputStreamWriter(fos);
    **  3.  PrintWriter pw = new PrintWriter(osw,true);
    **  But, since I would like to append to the file, went ahead and did
    **  something else (as shown in the code)
    *************************************************************************/
    public void saveOffData(HeatIndexCalculatedData calculatedData){
        Date date              = new Date();
        SimpleDateFormat sdf   = new SimpleDateFormat("MM_dd_yyyy");
        SimpleDateFormat tdf   = new SimpleDateFormat("HH:mm:ss");
        String the_date        = sdf.format(date);
        String the_time        = tdf.format(date);

        String data            = new String();
        String file            = new String("C:/weather_data_");
        FileWriter fw          = null;
        PrintWriter pw         = null;

        if(!the_date.equals(current_date))
            current_date = the_date;
        try{
            try{
                file = file + the_date + ".sql";
                current_file = file;
                new FileReader(current_file);
            }
            catch(FileNotFoundException f){
                System.out.println(file + " not found, creating.");
                fw = new FileWriter(current_file,true);
                pw = new PrintWriter(fw,true);
                String header = new String("create table " +the_date+ "(");
                header = header + "Time_stamp CHAR(10), Temperature DOUBLE, ";
                header = header + "Units CHAR(3), Humidity INTEGER, "; 
                header = header + "Percent CHAR(2), Heat_Index DOUBLE, ";
                header = header + "HI_Units CHAR(3) );";
                pw.println(header);
            }
            fw = new FileWriter(current_file,true);
            pw = new PrintWriter(fw, true);
            data = new String("insert into " + the_date + " values( ");
            data = data + "'"+the_time+" ',"+calculatedData.getTemperature();
            if(temperatureMode == HeatIndexCalculatorView.DEGREES_F)
                data = data + ", '" + '\u00B0' + "F ',";
            else
                data = data + ", '" + '\u00B0' + "C ',";
            data = data + (int)calculatedData.getHumidity() + ", '% ',";
            data = data + calculatedData.getHeatIndex() + ", ";
            if(temperatureMode == HeatIndexCalculatorView.DEGREES_F)
                data = data + "'" + '\u00B0' + "F '";
            else
                data = data + "'" + '\u00B0' + "C '";
            data = data + ");";
            pw.println(data);
            if(pw.checkError()){
                String temp = "Error occured while saving data.";
                System.out.println(temp);
            }
            else{
                System.out.print(the_time + "--");
                System.out.println("data saved in " + current_file);
            }
            pw.close();
        }
        catch(IOException i){
            i.printStackTrace();
        }
    }

    /*************************************************************************
    **  Part of the "First Addendum" additions.  This method clears out all
    **  the weather related data.
    *************************************************************************/
    public void clearData(){
        heat_index        = Double.MAX_VALUE;
        heat_index_int    = Integer.MAX_VALUE;
        temperature       = Double.MAX_VALUE;
        relative_humidity = Double.MAX_VALUE;

    }

    /*************************************************************************
    **  This method brings the application to a halt.  This was put in here
    **  due to the nature of the class--this is the actual model, which is
    **  the software representation of the system being modeled.  Therefore,
    **  it should handle the user's desire to quit.  It does allow the
    **  Observer objects to do what they need to do to quit gracefully: to
    **  insure all processes related to this application are halted.
    *************************************************************************/
    public void quit(){
        this.setChanged();
        //indicate to all observing the application is quiting
        this.notifyObservers();
        this.clearChanged();
        System.exit(0);
    }

    /*************************************************************************
    **  This is the private version of actually calculating the heat index.
    **  This method does not actually do the "number crunching".  Rather,
    **  it sets up and does all the support work to make sure the data is in
    **  the correct format and sent off to those interrested.  The formula
    **  was separated out of this method for the mere fact is is so large
    **  and relatively complex:  it almost needed its own method if for no
    **  other reason than to keep complexity to a minimum and maintainability
    **  to a maximum.
    *************************************************************************/
    private void setHeatIndex(HeatIndexData index_data){
        //if this value is returned, something definately went wrong        
        double DIVIDE_FACTOR     = 10.0;
        int    MULTIPLIER        = 10;
        
        temperature        = index_data.getTemperature();
        relative_humidity  = index_data.getHumidity();
        if(temperatureMode == HeatIndexCalculatorView.DEGREES_C)
            temperature = (9.0/5.0)*temperature + 32;//convert to Fahrenheit
        
        heat_index = returnDataFromFormula(temperature,relative_humidity);
        
        //the HI is calculated in Deg. F, needs to be converted
        //back if in Deg. C
        if(temperatureMode == HeatIndexCalculatorView.DEGREES_C){
            heat_index = (5.0/9.0)*(heat_index-32);
            heat_index = this.round(heat_index,2);
            heat_index *= MULTIPLIER;
            int temp_value = (int)heat_index;
            heat_index = temp_value/DIVIDE_FACTOR;
        }
        else{
            heat_index = this.round(heat_index,1);
            heat_index_int = (int)heat_index;
        }
        this.setChanged();
        if(temperatureMode == HeatIndexCalculatorView.DEGREES_C)
            this.notifyObservers(new Double(heat_index));
        else
            this.notifyObservers(new Integer(heat_index_int));
        this.clearChanged();
    }

    /*************************************************************************
    **  This is the actual "number crunching" method.  It is mighty complex.
    **  Another, less complex version of this formula could be used, but it
    **  does not guarantee the accuracy this formula can deliver.  But, the
    **  accuracy of this formula might be so miniscule, that it does not
    **  warant the extra CPU cycles to plow through this formula.  If that is
    **  the case, the other formula will be used, and this method will be
    **  depricated in an improved version of this application (if one is 
    **  needed).
    *************************************************************************/
    private double returnDataFromFormula(double t, double rh){
        double hi = Double.POSITIVE_INFINITY;
        
        hi = 16.923+.185212*t+5.37941*rh-.100254*t*rh
            +.00941695*t*t
            +.00728898*rh*rh
            +.000345372*t*t*rh
            -.000814971*t*rh*rh
            +.0000102102*t*t*rh*rh
            -.000038646*t*t*t
            +.0000291583*rh*rh*rh
            +.00000142721*t*t*t*rh
            +.000000197483*t*rh*rh*rh
            -.000000021429*t*t*t*rh*rh
            +.000000000843296*t*t*rh*rh*rh
            -.0000000000481975*t*t*t*rh*rh*rh;
 
        return hi;
    }

    /*************************************************************************
    **  This will round the values.  For Fahrenheit, it will round it to the
    **  next integer degree value.  For Celsius, it will round it to the 
    **  nearest tenth of a degree.
    *************************************************************************/
    private double round(double value, int place){
        int MULTIPLIER = 10;
        double return_value = value*(Math.pow(MULTIPLIER,place));
        int round_number = (int)return_value % 10;
        if(round_number > 4)
            return_value += MULTIPLIER;
        return_value /= (Math.pow(10.0,place));
        return return_value;
    }

    /*************************************************************************
    **  This method initializes all the private attributes in the class
    *************************************************************************/
    private void setUpPrivateValues(){
        temperatureMode   = HeatIndexCalculatorView.DEGREES_F;
        heat_index        = Double.MAX_VALUE;
        heat_index_int    = Integer.MAX_VALUE;
        temperature       = Double.MAX_VALUE;
        relative_humidity = Double.MAX_VALUE;
        current_date      = new String("");
        current_file      = new String("");
    }
}