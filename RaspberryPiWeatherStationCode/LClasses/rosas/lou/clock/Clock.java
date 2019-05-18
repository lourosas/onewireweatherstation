/*********************************************************************
* Copyright (C) 2015 Lou Rosas
* This file is part of many applications registered with
* the GNU General Public License as published
* by the Free Software Foundation; either version 3 of the License,
* or (at your option) any later version.
* PaceCalculator is distributed in the hope that it will be
* useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.
* If not, see <http://www.gnu.org/licenses/>.
*********************************************************************/

package rosas.lou.clock;

import java.util.*;
import java.lang.*;
import java.text.DateFormat;
import rosas.lou.clock.*;

public class Clock{
   private long time;
   private Date date;

   {
      time = 0;
      date = null;
   }

   //*********************Constructor********************************
   /*
   Constructor of no arguments
   */
   public Clock(){}

   //*********************Public Methods*****************************
   /*
   */
   public long getTime(){
      this.time = Calendar.getInstance().getTimeInMillis();
      this.date = Calendar.getInstance().getTime();
      return this.time;
   }

   /*
   */
   public Date getDate(){
      this.date = Calendar.getInstance().getTime();
      return this.date;
   }
}
