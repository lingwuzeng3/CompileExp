// src/parser/Action.java
package Exp5.parser;

public enum Action {
    SHIFT,      // 移进
    REDUCE,     // 归约
    ACCEPT,     // 接受
    ERROR       // 错误
}