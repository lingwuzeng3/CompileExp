package Exp5.parser;

import java.util.HashMap;
import java.util.Map;

public class Grammar {
    // 产生式右部的符号数量（与LR分析表的归约编号对应）
    public static final int[] PRODUCTION_LENGTH = {
        1,  // 0: L -> E
        3,  // 1: E -> E + T
        1,  // 2: E -> T
        3,  // 3: T -> T * F
        1,  // 4: T -> F
        3,  // 5: F -> ( E )
        1   // 6: F -> i
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
    
    public static String getLeftSymbol(int productionNum) {
        switch (productionNum) {
            case 0: return "L";
            case 1:
            case 2: return "E";
            case 3:
            case 4: return "T";
            case 5:
            case 6: return "F";
            default: return "";
        }
    }
}