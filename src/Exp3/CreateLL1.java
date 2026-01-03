package Exp3;

import Exp1.G;
import Exp1.Main;
import Exp3.Utils.Util;

public class CreateLL1 {
  public static void main(String[] args){

    String filename = "exp3";
    G g = Main.readGrammer("docs/"+filename +".txt");
    System.out.println("原始文法:");
    g.printGrammar();

    g = Util.delRecursion(g);
    System.out.println("消除左递归后的文法:");
    g.printGrammar();

    g = Util.extractLeftFactor(g);//TODO修正P的结构
    System.out.println("提取左因子后的文法:");
    g.printGrammar();

    //将新文法输出到docs/new_filename.txt
    g.outputGrammer(filename);

  }

}
