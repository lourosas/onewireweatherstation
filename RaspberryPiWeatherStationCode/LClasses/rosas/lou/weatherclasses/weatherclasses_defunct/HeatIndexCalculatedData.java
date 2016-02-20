package weatherclasses;

import java.lang.*;
import java.util.*;
import myclasses.*;
import weatherclasses.*;

public class HeatIndexCalculatedData extends HeatIndexData{
    public double heat_index;
    public int    heat_index_int;

    public HeatIndexCalculatedData(){
        super(Double.MAX_VALUE,Double.MAX_VALUE);
        heat_index     = Double.MAX_VALUE;
        heat_index_int = Integer.MAX_VALUE;
    }

    public HeatIndexCalculatedData(double temp,double humid,double heat_indx){
        super(temp,humid);
        this.setHeatIndex(heat_indx);
    }

    public void setHeatIndex(double heat_indx){
        this.heat_index = heat_indx;
        this.setHeatIndex((int)heat_index);
    }

    public void setHeatIndex(int heat_indx){
        this.heat_index_int = heat_indx;
    }

    public double getHeatIndex(){
        return heat_index;
    }

    public int getHeatIndexInt(){
        return heat_index_int;
    }
}