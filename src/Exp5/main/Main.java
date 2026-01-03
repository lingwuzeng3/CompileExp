// src/main/Main.java
package Exp5.main;

import Exp5.lexer.Lexer;
import Exp5.lexer.Token;
import Exp5.lexer.TokenType;
import Exp5.parser.LRParser;
import Exp5.parser.LRParser.ParseResult;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("请输入表达式文件路径expression.txt:");
        String filePath = scanner.nextLine();
        if (filePath.isEmpty()) {
            filePath = "docs/expression.txt";
        }
        
        try {
            processExpressions(filePath);
        } catch (IOException e) {
            System.err.println("文件处理错误: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    
    private static void processExpressions(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("文件不存在: " + filePath);
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                System.out.println("\n处理表达式: " + line);
                
                // 去除末尾可能的分号
                line = line.trim();
                if (line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1);
                }
                
                // 词法分析
                Lexer lexer = new Lexer(line);
                List<Token> tokens = lexer.tokenize();
                
                // 添加结束符
                tokens.add(new Token(TokenType.EOF, "", 0, 0));
                
                // 输出词法分析结果
                // System.out.println("词法分析结果:");
                // for (Token token : tokens) {
                //     if (!token.getType().equals(TokenType.EOF)) {
                //         System.out.print(token + " ");
                //     }
                // }
                // System.out.println();
                
                // 语法分析
                LRParser parser = new LRParser();
                ParseResult result = parser.parse(tokens);
                
                // 输出结果
                if (result.isSuccess()) {
                    System.out.println("正确,表达式结果: " + result.getResult());
                } else {
                    System.out.println("语法错误");
                    if (result.getErrorPosition() >= 0 && 
                        result.getErrorPosition() < tokens.size()) {
                        Token errorToken = tokens.get(result.getErrorPosition());
                        System.out.println("错误位置: 第" + errorToken.getLine() + 
                                         "行, 第" + errorToken.getColumn() + "列");
                        System.out.println("错误信息: " + result.getErrorMessage());
                    }
                }
            }
        }
    }
}