// src/parser/LRParser.java
package Exp5.parser;

import Exp5.lexer.Token;
import Exp5.lexer.TokenType;
import java.util.List;
import java.util.Stack;

public class LRParser {
    // LR分析表
    private static final String[][] ACTION_TABLE = {
        // i    +    *    (    )    #
        {"s5", "",  "",  "s4", "",  ""},      // 0
        {"",   "s6", "", "",   "",  "acc"},   // 1
        {"",   "r2", "s7", "", "r2", "r2"},   // 2
        {"",   "r4", "r4", "", "r4", "r4"},   // 3
        {"s5", "",  "",  "s4", "",  ""},      // 4
        {"",   "r6", "r6", "", "r6", "r6"},   // 5
        {"s5", "",  "",  "s4", "",  ""},      // 6
        {"s5", "",  "",  "s4", "",  ""},      // 7
        {"",   "s6", "", "",   "s11", ""},    // 8
        {"",   "r1", "s7", "", "r1", "r1"},   // 9
        {"",   "r3", "r3", "", "r3", "r3"},   // 10
        {"",   "r5", "r5", "", "r5", "r5"}    // 11
    };
    
    private static final int[][] GOTO_TABLE = {
        // E   T   F
        {1,  2,  3},   // 0
        {-1, -1, -1},  // 1
        {-1, -1, -1},  // 2
        {-1, -1, -1},  // 3
        {8,  2,  3},   // 4
        {-1, -1, -1},  // 5
        {-1, 9,  3},   // 6
        {-1, -1, 10},  // 7
        {-1, -1, -1},  // 8
        {-1, -1, -1},  // 9
        {-1, -1, -1},  // 10
        {-1, -1, -1}   // 11
    };
    
    private Stack<Integer> stateStack;
    private Stack<String> symbolStack;
    private Semantic semantic;
    private int errorPosition;
    private String errorMessage;
    
    public LRParser() {
        stateStack = new Stack<>();
        symbolStack = new Stack<>();
        semantic = new Semantic();
        stateStack.push(0);
    }
    
    public ParseResult parse(List<Token> tokens) {
        int tokenIndex = 0;
        errorPosition = -1;
        errorMessage = "";
        semantic.clear();
        
        while (true) {
            int currentState = stateStack.peek();
            Token token = tokens.get(tokenIndex);
            String symbol = tokenToSymbol(token);
            
            // 获取ACTION表中的动作
            int symbolIndex = Grammar.getTerminalIndex(symbol);
            if (symbolIndex == -1) {
                setError(tokenIndex, "Invalid symbol: " + symbol);
                return new ParseResult(false, 0, errorPosition, errorMessage);
            }
            
            String action = ACTION_TABLE[currentState][symbolIndex];
            
            if (action.isEmpty()) {
                setError(tokenIndex, "Syntax error at token: " + token.getValue());
                return new ParseResult(false, 0, tokenIndex, errorMessage);
            }
            
            if (action.equals("acc")) {
                // 接受
                return new ParseResult(true, semantic.getResult(), -1, "");
            }
            
            if (action.startsWith("s")) {
                // 移进
                int nextState = Integer.parseInt(action.substring(1));
                stateStack.push(nextState);
                symbolStack.push(symbol);
                
                // 如果是数字，将值压入语义栈
                if (token.getType() == TokenType.NUMBER) {
                    try {
                        int value = Integer.parseInt(token.getValue());
                        semantic.pushValue(value);
                    } catch (NumberFormatException e) {
                        semantic.pushValue(0);
                    }
                } else if (token.getType() == TokenType.ID) {
                    // 标识符的值设为0或从符号表中获取
                    semantic.pushValue(0);
                }
                
                tokenIndex++;
            } 
            else if (action.startsWith("r")) {
                // 归约
                int productionNum = Integer.parseInt(action.substring(1));
                int productionLength = Grammar.PRODUCTION_LENGTH[productionNum];
                
                // 弹出产生式右部的符号和状态
                String[] poppedSymbols = new String[productionLength];
                for (int i = 0; i < productionLength; i++) {
                    stateStack.pop();
                    poppedSymbols[productionLength - 1 - i] = symbolStack.pop();
                }
                
                // 执行语义动作
                semantic.executeAction(productionNum, poppedSymbols);
                
                // 获取当前状态和产生式左部
                int currentStateAfterPop = stateStack.peek();
                String leftSymbol = getLeftSymbol(productionNum);
                
                // 查找GOTO表
                int nonTerminalIndex = Grammar.getNonTerminalIndex(leftSymbol);
                int nextState = GOTO_TABLE[currentStateAfterPop][nonTerminalIndex];
                
                if (nextState == -1) {
                    setError(tokenIndex, "GOTO error for symbol: " + leftSymbol);
                    return new ParseResult(false, 0, tokenIndex, errorMessage);
                }
                
                stateStack.push(nextState);
                symbolStack.push(leftSymbol);
            }
        }
    }
    
    private String tokenToSymbol(Token token) {
        switch (token.getType()) {
            case NUMBER:
            case ID:
                return "i";
            case PLUS:
                return "+";
            case MULTIPLY:
                return "*";
            case LPAREN:
                return "(";
            case RPAREN:
                return ")";
            case SEMICOLON:
            case EOF:
                return "#";
            default:
                return "";
        }
    }
    
    private String getLeftSymbol(int productionNum) {
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
    
    private void setError(int position, String message) {
        this.errorPosition = position;
        this.errorMessage = message;
    }
    
    public static class ParseResult {
        private boolean success;
        private int result;
        private int errorPosition;
        private String errorMessage;
        
        public ParseResult(boolean success, int result, int errorPosition, String errorMessage) {
            this.success = success;
            this.result = result;
            this.errorPosition = errorPosition;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() { return success; }
        public int getResult() { return result; }
        public int getErrorPosition() { return errorPosition; }
        public String getErrorMessage() { return errorMessage; }
    }
}