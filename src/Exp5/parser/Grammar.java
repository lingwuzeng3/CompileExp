// src/parser/Grammar.java
package Exp5.parser;

import java.util.HashMap;
import java.util.Map;

public class Grammar {
    // 产生式列表
    public static final int[][] PRODUCTIONS = {
        {1},                // 0: L -> E
        {2, 3},            // 1: E -> E + T
        {4},               // 2: E -> T
        {5, 6},            // 3: T -> T * F
        {7},               // 4: T -> F
        {8, 9, 10},        // 5: F -> ( E )
        {11}               // 6: F -> i
    };
    
    // 产生式长度
    public static final int[] PRODUCTION_LENGTH = {
        1,  // L -> E
        3,  // E -> E + T
        1,  // E -> T
        3,  // T -> T * F
        1,  // T -> F
        3,  // F -> ( E )
        1   // F -> i
    };
    
    // 终结符映射
    private static final Map<String, Integer> TERMINAL_MAP = new HashMap<>();
    static {
        TERMINAL_MAP.put("i", 0);
        TERMINAL_MAP.put("+", 1);
        TERMINAL_MAP.put("*", 2);
        TERMINAL_MAP.put("(", 3);
        TERMINAL_MAP.put(")", 4);
        TERMINAL_MAP.put("#", 5);
    }
    
    // 非终结符映射
    private static final Map<String, Integer> NON_TERMINAL_MAP = new HashMap<>();
    static {
        NON_TERMINAL_MAP.put("E", 0);
        NON_TERMINAL_MAP.put("T", 1);
        NON_TERMINAL_MAP.put("F", 2);
    }
    
    public static int getTerminalIndex(String terminal) {
        return TERMINAL_MAP.getOrDefault(terminal, -1);
    }
    
    public static int getNonTerminalIndex(String nonTerminal) {
        return NON_TERMINAL_MAP.getOrDefault(nonTerminal, -1);
    }
}