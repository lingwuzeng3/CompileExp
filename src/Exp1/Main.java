package Exp1;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
  public static void main(String[] args) throws Exception{

    G g = new G();

    FileReader fileReader = new FileReader("src/Exp1/g.txt");
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    String line = bufferedReader.readLine();

    g.S = line.split("->")[0].trim();

    while(line != null){
      String[] str = line.split("->");
      String left = str[0].trim();
      String right = str[1].trim().replace(";","");

      //提取产生式
      if(!g.P.containsKey(left)){
        g.P.put(left,right);
      }else{
        g.P.put(left,g.P.get(left) + "|" + right);
      }

      //提取非终结符，即提取大写字母
      if(!g.Vn.contains(left)){
        g.Vn.add(left);
      }
      String v = right.replaceAll("[^A-Z]","");
      if(v.length()>0 && !g.Vn.contains(v)){
        g.Vn.add(v);
      }

      //提取终结符，即提取小写字母
      v = right.replaceAll("[^a-z]","");
      if(v.length()>0 && !g.Vt.contains(v)){
        g.Vt.add(v);
      }
      
      line = bufferedReader.readLine();
    }

    System.out.println("Vn = " + g.Vn);
    System.out.println("Vt = " + g.Vt);
    System.out.println("S = " + g.S);
    System.out.println("P = " + g.P);

    System.out.println("-----------------------");

    for(String key:g.P.keySet()){
    System.out.println(key + "->" + g.P.get(key) + ";");

    bufferedReader.close();
    }
  }
}
