package Exp2;

import java.io.*;
import java.util.*;

public class SplitWords {

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


  public static String scan(String filename) throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String output_name ="docs/split_words.txt";
    BufferedWriter writer = new BufferedWriter(new FileWriter(output_name));
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
            writer.write("<" + tokenStr + " , ->");
          } else {
            writer.write("<0 , " + tokenStr + ">");
          }
          writer.newLine();
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
                writer.write("<3 , " + hexNum.toString() + ">");
                writer.newLine();
              } else if (isOctalDigit(nextChar)) {
                StringBuilder octNum = new StringBuilder();
                while (pos < line.length() && isOctalDigit(line.charAt(pos))) {
                  octNum.append(line.charAt(pos));
                  pos++;
                }
                writer.write("<2 , " + octNum.toString() + ">");
                writer.newLine();
              } else {
                writer.write("<1 , 0>");
                writer.newLine();
                pos++;
              }
            } else {//行末的0，视为十进制的普通0
              writer.write("<1 , 0>");
              writer.newLine();
              pos++;
            }
          } else {//非0开头的数字，即十进制
            StringBuilder decNum = new StringBuilder();
            while (pos < line.length() && Character.isDigit(line.charAt(pos))) {
              decNum.append(line.charAt(pos));
              pos++;
            }
            writer.write("<1 , " + decNum.toString() + ">");
            writer.newLine();
          }
        }

        // 运算符处理
        else if (SIN_OPS.contains(ch)) {
          // 检查是否为多字符运算符
          if (pos + 1 < line.length()) {
            String twoCharOp = line.substring(pos, pos + 2);
            if (MUL_OPS.contains(twoCharOp)) {
              // 处理双目运算符
              writer.write("<" + twoCharOp + " , ->");
              writer.newLine();
              pos += 2;
            }else {
              // 单字符运算符
              writer.write("<" + ch + " , ->");
              writer.newLine();
              pos++;
            }
          } else {
            // 行末的单字符运算符
            writer.write("<" + ch + " , ->");
            writer.newLine();
            pos++;
          }
        }

        // 处理其他字符（跳过）
        else {
          pos++;
        }
      }
    }
    writer.flush();
    writer.close();
    reader.close();
    return output_name;
  }

  // 判断十六进制数字
  private static boolean isHexDigit(char ch) {
    return Character.isDigit(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
  }

  // 是否为八进制数字
  private static boolean isOctalDigit(char ch) {
    return ch >= '0' && ch <= '7';
  }

  public static String[] extra(String line){
    //去除<>
    String token = line.substring(1,line.length());
    String[] tokens = token.split(",");
    //去除空格
    for(int i = 0;i<tokens.length;i++){
      tokens[i] = tokens[i].trim();
    }
    return tokens;
  }

  /**
   * 对Exp4的语句进行分词
   * @param filename
   * @return
   * @throws Exception
   */
  public static String scanForExp(String line) throws Exception {

    //读入一行语句，然后分词写入文件中
    String output_name ="docs/split_words.txt";
    BufferedWriter writer = new BufferedWriter(new FileWriter(output_name));

    line = line.trim();

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
          writer.write("<" + tokenStr + " , ->");
        } else {
          writer.write("<0 , " + tokenStr + ">");
        }
        writer.newLine();
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
              writer.write("<3 , " + hexNum.toString() + ">");
              writer.newLine();
            } else if (isOctalDigit(nextChar)) {
              StringBuilder octNum = new StringBuilder();
              while (pos < line.length() && isOctalDigit(line.charAt(pos))) {
                octNum.append(line.charAt(pos));
                pos++;
              }
              writer.write("<2 , " + octNum.toString() + ">");
              writer.newLine();
            } else {
              writer.write("<1 , 0>");
              writer.newLine();
              pos++;
            }
          } else {//行末的0，视为十进制的普通0
            writer.write("<1 , 0>");
            writer.newLine();
            pos++;
          }
        } else {//非0开头的数字，即十进制
          StringBuilder decNum = new StringBuilder();
          while (pos < line.length() && Character.isDigit(line.charAt(pos))) {
            decNum.append(line.charAt(pos));
            pos++;
          }
          writer.write("<1 , " + decNum.toString() + ">");
          writer.newLine();
        }
      }

      // 运算符处理
      else if (SIN_OPS.contains(ch)) {
        // 检查是否为多字符运算符
        if (pos + 1 < line.length()) {
          String twoCharOp = line.substring(pos, pos + 2);
          if (MUL_OPS.contains(twoCharOp)) {
            // 处理双目运算符
            writer.write("<" + twoCharOp + " , ->");
            writer.newLine();
            pos += 2;
          }else {
            // 单字符运算符
            writer.write("<" + ch + " , ->");
            writer.newLine();
            pos++;
          }
        } else {
          // 行末的单字符运算符
          writer.write("<" + ch + " , ->");
          writer.newLine();
          pos++;
        }
      }
      // 处理其他字符（跳过）
      else {
        pos++;
      }
    }
    writer.flush();
    writer.close();
    return output_name;
  }

}