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
        String filePath = "docs/expression.txt";
      
        try {
            processExpressions(filePath);
        } catch (IOException e) {
            System.err.println("文件处理错误: " + e.getMessage());
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
            int expressionCount = 0;
            
            System.out.println("\n=== LR分析程序运行结果 ===");
            
            while ((line = reader.readLine()) != null) {
                expressionCount++;
                String originalLine = line.trim();
                
                // 去除末尾可能的分号用于分析
                String analysisLine = originalLine;
                if (analysisLine.endsWith(";")) {
                    analysisLine = analysisLine.substring(0, analysisLine.length() - 1);
                }
                
                try {
                    // 词法分析
                    Lexer lexer = new Lexer(analysisLine);
                    List<Token> tokens = lexer.tokenize();
                    
                    // 添加结束符
                    tokens.add(new Token(TokenType.EOF, "", 0, 0));
                    
                    // 语法分析
                    LRParser parser = new LRParser();
                    ParseResult result = parser.parse(tokens);
                    
                    // 按照要求格式输出
                    System.out.print("（" + expressionCount + "）" + originalLine + "\n输出：");
                    
                    if (result.isSuccess()) {
                        System.out.println("正确，表达式结果为" + result.getResult());
                    } else {
                        System.out.print("错误");
                        if (result.getErrorPosition() >= 0 && 
                            result.getErrorPosition() < tokens.size()) {
                            Token errorToken = tokens.get(result.getErrorPosition());
                            System.out.println("   错误信息：语法错误，位置：" + 
                                             errorToken.getLine() + "行" + 
                                             errorToken.getColumn() + "列");
                        } else {
                            System.out.println("   错误信息：" + result.getErrorMessage());
                        }
                    }
                    
                } catch (Exception e) {
                    // 如果分析过程中出现异常，也认为是错误
                    System.out.print("（" + expressionCount + "）" + originalLine + "\n输出：");
                    System.out.println("错误   错误信息：分析异常 - " + e.getMessage());
                }
            }
            System.out.println("=== 分析结束 ===");
        }
    }
}