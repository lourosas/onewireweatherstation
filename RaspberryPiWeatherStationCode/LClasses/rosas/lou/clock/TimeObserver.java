/*
*/

package rosas.lou.clock;

import java.lang.*;
import java.util.*;
import java.text.DateFormat;
import rosas.lou.clock.*;

public interface TimeObserver{
   public void updateTime(String formatedTime);
   public void updateTime(long time);
   public void updateTime(int time);
   public void updateTime(Date date);
   public void updateTime(DateFormat dateFormat);
   public void updateTime(TimeObject timeObject);
}
