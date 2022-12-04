/********************************************************************
The Pace Calculator Application.
Copyright (C) 2008 Lou Rosas

This file is part of PaceCalculator.
PaceCalculator is free software; you can redistribute it
and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

PaceCalculator is distributed in the hope that it will be
useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
********************************************************************/
import java.lang.*;
import java.util.*;
import myclasses.*;
import rosas.lou.*;
import rosas.lou.calculator.*;

public class PaceCalculatorMain{
   static public void main(String [] args){
      PaceCalculatorController pcc = new PaceCalculatorController();
      PaceCalculatorView pcv;
      pcv = new PaceCalculatorView("Pace Calculator", pcc);
      PaceCalculator pc = new PaceCalculator(pcv);
      pcc.setView(pcv);
      pcc.setModel(pc);
   }
}