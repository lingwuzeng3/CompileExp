package Exp1;

import java.io.BufferedReader;
import java.io.FileReader;

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
        if (!g.P.containsKey(left)) {
          g.P.put(left, right);
        } else {
          g.P.put(left, g.P.get(left) + "|" + right);
        }

        // 提取非终结符，即提取大写字母
        if (!g.Vn.contains(left)) {
          g.Vn.add(left);
        }
        g.getVn(right);

        // 提取终结符，即提取小写字母
        g.getVt(right);
        
        

        line = bufferedReader.readLine();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return g;

  }

}
