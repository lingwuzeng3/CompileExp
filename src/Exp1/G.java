package Exp1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// G（Vn, Vt, S, P）
// 建立文法的数据结构
/**
 * Vn = [S, Q, R]
 * Vt = [c, b, a]
 * S = S
 * P = {Q=Rb|b, R=Sa|a, S=Qc|c}
 */
public class G {

    public G() {
    }

    // 非终结符集合
    public Set<String> Vn = new HashSet<String>();
    // 终结符集合
    public Set<String> Vt = new HashSet<String>();
    // 开始符号
    public String S;
    // 产生式集合
    public HashMap<String, ArrayList<String>> P = new HashMap<String, ArrayList<String>>();

/**
 * 从产生式中提取非终结符和终结符
 * @param str
 * @param V
 */
  public void getVnOrVt(String str){
    String[] rights = str.split("\\|");
    for(String right_part:rights){
      for(char c:right_part.toCharArray()){
        //大写字母为非终结符，其余均算作终结符。
        if(Character.isUpperCase(c)){
          String s = String.valueOf(c);
          Vn.add(s);
        }else{
            String s = String.valueOf(c);
            Vt.add(s);
        }
      }
    }  
  }

    // 打印文法
    public void printGrammar() {
      System.out.println("Vn = " + Vn);
      System.out.println("Vt = " + Vt);
      System.out.println("S = " + S);
      System.out.println("P = " + P);

      System.out.println("文法G:");
      for (Map.Entry<String, ArrayList<String>> entry : P.entrySet()) {
            System.out.println(entry.getKey() + " -> " + String.join("|", entry.getValue()) + ";");
      }
      
      System.out.println();
    }

    public void outputGrammer(String filename){
      try(
        BufferedWriter bw = new BufferedWriter(new FileWriter("docs/new_" + filename + ".txt"))
      ){
        for (Map.Entry<String, ArrayList<String>> entry : P.entrySet()) {
          bw.write(entry.getKey() + "->" + String.join("|", entry.getValue()) + ";");
          bw.newLine();
        }
      }catch(Exception e){
        e.printStackTrace();
      }
    }

}
