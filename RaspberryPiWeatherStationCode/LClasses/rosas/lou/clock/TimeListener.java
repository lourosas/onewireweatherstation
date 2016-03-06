/********************************************************************
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
********************************************************************/

package rosas.lou.clock;

import java.lang.*;
import java.util.*;
import rosas.lou.clock.TimeFormater;
import rosas.lou.clock.ClockState;

public interface TimeListener{
   public void update(ClockState clockState);
   public void update(Object o);
   public void update(Object o, ClockState cs);
   public void update(Stack<TimeFormater> tfStack,ClockState cs);
   public void update(TimeFormater timeFormater);
   public void update(TimeFormater tf, ClockState cs);
}
