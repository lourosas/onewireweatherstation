import java.lang.*;
import java.util.*;
import rosas.lou.calculator.*;

public class TestRPNCalc{
   public static void main(String [] args){
      new TestRPNCalc();
   }

   public TestRPNCalc(){
      RPNCalculator<Fraction> rpnfc = new RPNFractionCalculator();
      FractionCalculatorView rpnfv = new RPNFractionCalculatorView();
      new FractionCalculatorActionListener(rpnfv, rpnfc);
      new FractionCalculatorItemAdapter(rpnfv, rpnfc);
      new FractionCalculatorKeyListener(rpnfv, rpnfc);
   }
}
