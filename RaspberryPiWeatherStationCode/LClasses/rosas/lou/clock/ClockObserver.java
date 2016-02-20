/*
*/

package rosas.lou.clock;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.clock.*;

public interface ClockObserver{
   public void updateTime(long millisecs);
}