package Exp1;

import java.util.ArrayList;
import java.util.HashMap;

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
    public ArrayList<String> Vn = new ArrayList<String>();
    // 终结符集合
    public ArrayList<String> Vt = new ArrayList<String>();
    // 开始符号
    public String S;
    // 产生式集合
    public HashMap<String, String> P = new HashMap<String, String>();

/**
 * 从产生式中提取非终结符和终结符
 * @param str
 * @param V
 */
  public void getVnOrVt(String str){
    String[] rights = str.split("\\|");
    for(String right_part:rights){
      for(char c:right_part.toCharArray()){
        if(Character.isUpperCase(c)){
          String s = String.valueOf(c);
          if(!Vn.contains(s)){
            Vn.add(s);
          }
        }else if(Character.isLowerCase(c)){
          String s = String.valueOf(c);
          if(!Vt.contains(s)){
            Vt.add(s);
          }
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

        // System.out.println("-----------------------");
        // for (String key : P.keySet()) {
        //     System.out.println(key + "->" + P.get(key) + ";");
        // }
        
        System.out.println();
    }

}
