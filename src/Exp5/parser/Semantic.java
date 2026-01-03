package Exp5.parser;

import Exp5.lexer.Token;
import Exp5.lexer.TokenType;
import java.util.Stack;

public class Semantic {
    private Stack<Integer> valueStack;
    
    public Semantic() {
        valueStack = new Stack<>();
    }
    
    // 执行语义动作
    public void executeAction(int production) {
        switch (production) {
            case 1: // E -> E + T
                if (valueStack.size() < 3) {
                    valueStack.push(0);
                    return;
                }
                int tVal = valueStack.pop();  // T的值
                int plusVal = valueStack.pop(); // '+'的占位符
                int eVal = valueStack.pop();  // E的值
                valueStack.push(eVal + tVal);
                break;
                
            case 2: // E -> T
                // T的值已经在栈顶，不需要操作
                break;
                
            case 3: // T -> T * F
                if (valueStack.size() < 3) {
                    valueStack.push(0);
                    return;
                }
                int fVal = valueStack.pop();  // F的值
                int multiplyVal = valueStack.pop(); // '*'的占位符
                int tVal2 = valueStack.pop(); // T的值
                valueStack.push(tVal2 * fVal);
                break;
                
            case 4: // T -> F
                // F的值已经在栈顶，不需要操作
                break;
                
            case 5: // F -> ( E )
                if (valueStack.size() < 3) {
                    valueStack.push(0);
                    return;
                }
                int rparenVal = valueStack.pop(); // ')'的占位符
                int eVal2 = valueStack.pop();    // E的值
                int lparenVal = valueStack.pop(); // '('的占位符
                valueStack.push(eVal2);
                break;
                
            case 6: // F -> i
                // i的值已经在栈顶，不需要操作
                break;
                
            case 0: // L -> E
                // L -> E，不需要操作
                break;
        }
    }
    
    // 移进时压入值
    public void pushTokenValue(Token token) {
        if (token.getType() == TokenType.NUMBER) {
            try {
                int value = Integer.parseInt(token.getValue());
                valueStack.push(value);
            } catch (NumberFormatException e) {
                valueStack.push(0);
            }
        } else {
            // 对于运算符、括号等，压入0作为占位符
            valueStack.push(0);
        }
    }
    
    public int getResult() {
        return valueStack.isEmpty() ? 0 : valueStack.peek();
    }
    
    public void clear() {
        valueStack.clear();
    }
}