package Exp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) throws Exception {

    G g = readGrammer("docs/g.txt");

    g.printGrammar();

  }

  /**
   * /**
   * S->Qc|c|cab;
   * Q->Rb|b;
   * R->Sa|a; 
   * 
   * @param filename
   * @return
   */
  public static G readGrammer(String fileName) {

    G g = new G();

    try (
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);) {
      String line = bufferedReader.readLine();

      g.S = line.split("->")[0].trim();

      while (line != null) {
        String[] str = line.split("->");
        String left = str[0].trim();
        String right = str[1].trim().replace(";", "");

        // 提取产生式
        ArrayList<String> productions;
        if(g.P.get(left) == null){
          productions = new ArrayList<>();
        }else{
          productions = g.P.get(left);
        }
        String[] rights = right.split("\\|");
        for(String right_part:rights){
          productions.add(right_part);
        }
        g.P.put(left, productions);

        // 提取文法左边提取非终结符，即提取大写字母
        if (!g.Vn.contains(left)) {
          g.Vn.add(left);
        }

        // 从产生式右边提取非终结符和终结符
        g.getVnOrVt(right);

        line = bufferedReader.readLine();
        
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return g;

  }

}
