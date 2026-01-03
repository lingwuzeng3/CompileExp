package Exp5.lexer;
// src/lexer/Lexer.java

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String input;
    private int position;
    private int line;
    private int column;
    private char currentChar;
    
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        if (input.length() > 0) {
            currentChar = input.charAt(0);
        } else {
            currentChar = '\0';
        }
    }
    
    private void advance() {
        position++;
        if (position >= input.length()) {
            currentChar = '\0';
        } else {
            currentChar = input.charAt(position);
            column++;
        }
    }
    
    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            if (currentChar == '\n') {
                line++;
                column = 0;
            }
            advance();
        }
    }
    
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        
        while (currentChar != '\0') {
            skipWhitespace();
            
            if (currentChar == '\0') break;
            
            if (Character.isDigit(currentChar)) {
                tokens.add(readNumber());
            } else if (Character.isLetter(currentChar)) {
                tokens.add(readIdentifier());
            } else {
                switch (currentChar) {
                    case '+':
                        tokens.add(new Token(TokenType.PLUS, "+", line, column));
                        advance();
                        break;
                    case '*':
                        tokens.add(new Token(TokenType.MULTIPLY, "*", line, column));
                        advance();
                        break;
                    case '(':
                        tokens.add(new Token(TokenType.LPAREN, "(", line, column));
                        advance();
                        break;
                    case ')':
                        tokens.add(new Token(TokenType.RPAREN, ")", line, column));
                        advance();
                        break;
                    case ';':
                        tokens.add(new Token(TokenType.SEMICOLON, ";", line, column));
                        advance();
                        break;
                    default:
                        throw new RuntimeException(
                            String.format("Unexpected character '%c' at line %d, column %d", 
                                         currentChar, line, column));
                }
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
    
    private Token readNumber() {
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            sb.append(currentChar);
            advance();
        }
        
        return new Token(TokenType.NUMBER, sb.toString(), line, startColumn);
    }
    
    private Token readIdentifier() {
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (currentChar != '\0' && 
               (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            sb.append(currentChar);
            advance();
        }
        
        return new Token(TokenType.ID, sb.toString(), line, startColumn);
    }
    
    // 静态方法：分析表达式并生成令牌文件
    public static String analyzeExpression(String expression, String outputPath) 
            throws IOException {
        Lexer lexer = new Lexer(expression);
        List<Token> tokens = lexer.tokenize();
        
        File outputFile = new File(outputPath);
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            for (Token token : tokens) {
                writer.println(token);
            }
        }
        
        return outputFile.getAbsolutePath();
    }
}