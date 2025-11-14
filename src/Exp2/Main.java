package Exp2;

import java.io.*;
import java.util.*;

public class Main {

  // 关键字集合
  private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
      "if", "then", "else", "while", "do"));

  // 单字符运算符和界符集合
  private static final Set<Character> SIN_OPS = new HashSet<>(Arrays.asList(
      '+', '-', '*', '/', '>', '<', '=', '(', ')', ';', '!', '&', '|', '?', ':'));

  // 多字符运算符集合（双目和三目运算符）
  private static final Set<String> MUL_OPS = new HashSet<>(Arrays.asList(
      "==", "!=", ">=", "<=", "&&", "||", "++", "--", "+=", "-=", "*=", "/="));

  public static void main(String[] args) {
    try {
      scan("docs/program.txt");
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

        // 处理标识符和关键字
        if (Character.isLetter(ch) || ch == '_') {
          StringBuilder token = new StringBuilder();
          while (pos < line.length() &&
              (Character.isLetterOrDigit(line.charAt(pos)) || line.charAt(pos) == '_')) {
            token.append(line.charAt(pos));
            pos++;
          }
          String tokenStr = token.toString();

          if (KEYWORDS.contains(tokenStr)) {
            System.out.println("<" + tokenStr + " , ->");
          } else {
            System.out.println("<0 , " + tokenStr + ">");
          }
        }

        // 处理数字
        else if (Character.isDigit(ch)) {
          if (ch == '0') {//0开头的数字，可能是十六进制、八进制
            if (pos + 1 < line.length()) {
              char nextChar = line.charAt(pos + 1);
              if (nextChar == 'x' || nextChar == 'X') {
                pos += 2;
                StringBuilder hexNum = new StringBuilder();
                while (pos < line.length() && isHexDigit(line.charAt(pos))) {
                  hexNum.append(line.charAt(pos));
                  pos++;
                }
                System.out.println("<3 , " + hexNum.toString() + ">");
              } else if (isOctalDigit(nextChar)) {
                StringBuilder octNum = new StringBuilder();
                while (pos < line.length() && isOctalDigit(line.charAt(pos))) {
                  octNum.append(line.charAt(pos));
                  pos++;
                }
                System.out.println("<2 , " + octNum.toString() + ">");
              } else {
                System.out.println("<1 , 0>");
                pos++;
              }
            } else {//行末的0，视为十进制的普通0
              System.out.println("<1 , 0>");
              pos++;
            }
          } else {//非0开头的数字，即十进制
            StringBuilder decNum = new StringBuilder();
            while (pos < line.length() && Character.isDigit(line.charAt(pos))) {
              decNum.append(line.charAt(pos));
              pos++;
            }
            System.out.println("<1 , " + decNum.toString() + ">");
          }
        }

        // 运算符处理
        else if (SIN_OPS.contains(ch)) {
          // 检查是否为多字符运算符
          if (pos + 1 < line.length()) {
            String twoCharOp = line.substring(pos, pos + 2);
            if (MUL_OPS.contains(twoCharOp)) {
              // 处理双目运算符
              System.out.println("<" + twoCharOp + " , ->");
              pos += 2;
            }else {
              // 单字符运算符
              System.out.println("<" + ch + " , ->");
              pos++;
            }
          } else {
            // 行末的单字符运算符
            System.out.println("<" + ch + " , ->");
            pos++;
          }
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