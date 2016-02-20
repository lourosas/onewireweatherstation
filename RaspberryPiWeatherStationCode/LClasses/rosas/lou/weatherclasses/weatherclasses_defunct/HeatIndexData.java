package weatherclasses;

import java.lang.*;
import java.util.*;
import myclasses.*;
import weatherclasses.*;

public class HeatIndexData{

    private double temperature;
    private double humidity;

    public HeatIndexData(double temp, double humid){
        this.setData(temp, humid);
    }

    public double getTemperature(){
        return temperature;
    }
    
    public double getHumidity(){
        return humidity;
    }
    
    public void setTemperature(double tempValue){
        temperature = tempValue;
    }
    
    public void setHumidity(double humidValue){
        humidity = humidValue;
    }

    private void setData(double temp, double humid){
        temperature = temp;
        humidity    = humid;
    }        
}