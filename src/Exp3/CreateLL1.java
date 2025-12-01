package Exp3;

import Exp1.G;
import Exp1.Main;
import Exp3.Utils.Util;

public class CreateLL1 {
  public static void main(String[] args){

    String filename = "docs/exp3.txt";
    G g = Main.readGrammer(filename);
    System.out.println("原始文法:");
    g.printGrammar();

    g = Util.eliminateLeftRecursion(g);
    System.out.println("消除左递归后的文法:");
    g.printGrammar();

    g = Util.extractLeftFactor(g);//TODO可能这里出了点问题，需要修正P的结构
    System.out.println("提取左因子后的文法:");
    g.printGrammar();

  }

}
