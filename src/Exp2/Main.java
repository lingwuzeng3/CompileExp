package Exp2;

import java.io.*;
import java.util.*;

public class Main {

  // 关键字集合
  private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
      "if", "then", "else", "while", "do"));

  // 运算符和界符集合
  private static final Set<Character> OPERATORS = new HashSet<>(Arrays.asList(
      '+', '-', '*', '/', '>', '<', '=', '(', ')', ';'));

  public static void main(String[] args) {
    try {
      scan("src/Exp2/program.txt");
    } catch (Exception e) {
      System.out.println("文件读取错误: " + e.getMessage());
    }
  }

  public static void scan(String filename) throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;

    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty())
        continue;

      int pos = 0;
      while (pos < line.length()) {
        char ch = line.charAt(pos);

        // 跳过空白字符
        if (Character.isWhitespace(ch)) {
          pos++;
          continue;
        }

        // 处理标识符和关键字 标识符正则式：<字母|_>(<字母>|<数字字符>|_)*
        if (Character.isLetter(ch) || ch == '_') {
          StringBuilder token = new StringBuilder();
          // 提取字符串，直到遇到非字母数字字符
          while (pos < line.length() &&
              (Character.isLetterOrDigit(line.charAt(pos)) || line.charAt(pos) == '_')) {
            token.append(line.charAt(pos));
            pos++;
          }
          String tokenStr = token.toString();

          //判断这个字符串是不是在关键字集合中
          if (KEYWORDS.contains(tokenStr)) {
            //<关键字, ->
            System.out.println("<" + tokenStr + " , ->");
          } else {
            //<0, 标识符>
            System.out.println("<0 , " + tokenStr + ">");
          }
        }

        // 处理数字，十进制整数正则式：0|(1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)*
        else if (Character.isDigit(ch)) {
          if (ch == '0') {
            // 可能是八进制、十六进制或单独的0，看下一个字符判断
            if (pos + 1 < line.length()) {
              char nextChar = line.charAt(pos + 1);
              if (nextChar == 'x' || nextChar == 'X') {
                // 十六进制数
                pos += 2; // 跳过0x
                StringBuilder hexNum = new StringBuilder();
                while (pos < line.length() && isHexDigit(line.charAt(pos))) {
                  hexNum.append(line.charAt(pos));
                  pos++;
                }
                System.out.println("<3 , " + hexNum.toString() + ">");
              } else if (isOctalDigit(nextChar)) {
                // 八进制数
                StringBuilder octNum = new StringBuilder();
                while (pos < line.length() && isOctalDigit(line.charAt(pos))) {
                  octNum.append(line.charAt(pos));
                  pos++;
                }
                System.out.println("<2 , " + octNum.toString() + ">");
              } else {
                // 单独的0（十进制）
                System.out.println("<1 , 0>");
                pos++;
              }
            } else {
              // 行末的0
              System.out.println("<1 , 0>");
              pos++;
            }
          } else {
            // 十进制数（首位不为0）
            StringBuilder decNum = new StringBuilder();
            while (pos < line.length() && Character.isDigit(line.charAt(pos))) {
              decNum.append(line.charAt(pos));
              pos++;
            }
            System.out.println("<1 , " + decNum.toString() + ">");
          }
        }

        // 处理运算符和界符，这里的运算符都是单目运算符，界符也是单个字符
        //所以这里直接在OPERATORS集合中判断即可
        else if (OPERATORS.contains(ch)) {
          System.out.println("<" + ch + " , ->");
          pos++;
        }
        // 处理其他字符（跳过）
        else {
          pos++;
        }
      }
    }
    reader.close();
  }

  // 判断十六进制数字
  private static boolean isHexDigit(char ch) {
    return Character.isDigit(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
  }

  // 是否为八进制数字
  private static boolean isOctalDigit(char ch) {
    return ch >= '0' && ch <= '7';
  }
}