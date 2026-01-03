package Exp4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import Exp1.*;
import Exp3.Utils.Util;
import Exp2.*;

public class CheckLL1 {

  public static void main(String[] args) throws Exception {

    //1.分析文法，将给出的文法转化为LL(1)文法；
    // G g = Main.readGrammer("docs/exp4.txt");
    // g = Util.delRecursion(g);
    // g = Util.extractLeftFactor(g);//如果直接给出预测分析表的话，这里的LL1文法其实用不到。
    // g.printGrammar();

    //2.设计一个预测分析程序，判断输入的表达式是否是给出文法的语言；
    try(
        BufferedReader br = new BufferedReader(new FileReader("docs/statement.txt"));
    ){

      //逐行读取语句，分词，预测是否正确。
      String line = br.readLine();
      while(line != null && !line.isEmpty()) {

        String split_file_name = SplitWords.scanForExp(line);
        //得到分词后的文件，并对分词文件进行预测
        boolean isCorrect = preview(split_file_name);
        System.out.println(line);
        if(isCorrect) {
          System.out.println("正确");
        } else {
          System.out.println("错误");
        }

        line = br.readLine();
      }

    }catch(Exception e){
      System.out.println("文件不存在");
      return;
    }

  }

  /*
   * 
   * //1）手工将测试的表达式写入文本文件，每个表达式写一行，用“；”表示结束；
   * //2）读入文本文件中的表达式；
   * //3）调用实验二中的词法分析程序分割单词；
   * //4）根据LL(1)文法构造预测分析表
   * // （如果实现起来困难，可以）直接将相应的预测分析表放入程序中；
   * //5）把单词送入预测分析程序，判断表达式是否正确（是否是给出文法的语言），
   * // 若错误，则给出错误信息（有余力的同学可以做）。
   */
  public static boolean preview(String filename) throws Exception {
    
    //读取文件并预测是否正确
    BufferedReader br = new BufferedReader(new FileReader(filename));
    List<String> inputTokens = new ArrayList<>();


    String line;
    while ((line = br.readLine()) != null && !line.isEmpty()) {
        String[] tokens = SplitWords.extra(line);
        String type = tokens[0];
        String data = tokens[1];
        
        // 将token转换为文法中的终结符
        // type为0,1,2,3表示标识符或整数，统一映射为 "i"
        // 其他为运算符/界符，直接使用
        String terminal;
        if(type.equals(";")){
          continue;
        }
        if (type.equals("0") || type.equals("1") || 
            type.equals("2") || type.equals("3")) {
            terminal = "i";  //标识符和数字都当作 i
        } else {
            terminal = type;  //运算符直接使用，如 +, *, (, )
        }
        inputTokens.add(terminal);
    }
    br.close();
    inputTokens.add("$");
    return analyze(inputTokens);
  }

  public static boolean analyze(List<String> inputTokens) {
        // 分析栈
        Stack<String> stack = new Stack<>();
        
        // 初始化：压入结束符和开始符号
        stack.push("$");
        stack.push("E");  // E是开始符号
        
        // 输入指针
        int ip = 0;
        
        while (!stack.isEmpty()) {
            String top = stack.peek();         // 栈顶
            String current = inputTokens.get(ip);  // 当前输入符号

            //情况1：栈顶是终结符
            if (Table.map_vt.containsKey(top)) {
                if (top.equals(current)) {
                    stack.pop();
                    ip++;
                    
                    //如果匹配到$，分析成功
                    if (top.equals("$")) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
            //情况2：栈顶是非终结符
            else if (Table.map_vn.containsKey(top)) {
                List<String> production = Table.getProduction(top, current);
                
                if (production != null) {
                    stack.pop();
                    
                    //如果不是ε产生式，逆序压栈
                    if (!production.get(0).equals("ε")) {
                        for (int i = production.size() - 1; i >= 0; i--) {
                            stack.push(production.get(i));
                        }
                    }
                } else {
                    return false;
                }
            }
            //情况3：未知符号
            else {
                return false;
            }
        }
        
        return false;
    }

}
