// src/lexer/TokenType.java
package Exp5.lexer;

public enum TokenType {
    ID,         // 标识符（变量名）
    NUMBER,     // 数字常量
    PLUS,       // +
    MULTIPLY,   // *
    LPAREN,     // (
    RPAREN,     // )
    SEMICOLON,  // ;
    EOF         // 文件结束
}