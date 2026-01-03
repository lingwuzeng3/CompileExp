// src/parser/Semantic.java
package Exp5.parser;

import java.util.Stack;

public class Semantic {
    // 语义栈，用于存储表达式的值
    private Stack<Integer> valueStack;
    
    public Semantic() {
        valueStack = new Stack<>();
    }
    
    // 执行语义动作
    public void executeAction(int production, String[] symbols) {
        switch (production) {
            case 0: // L -> E
                // 不做操作，结果已经在栈顶
                break;
            case 1: // E -> E + T
                int tVal = valueStack.pop();
                int eVal = valueStack.pop();
                valueStack.push(eVal + tVal);
                break;
            case 2: // E -> T
                // 值已经在栈顶，不需要操作
                break;
            case 3: // T -> T * F
                int fVal = valueStack.pop();
                int tVal2 = valueStack.pop();
                valueStack.push(tVal2 * fVal);
                break;
            case 4: // T -> F
                // 值已经在栈顶
                break;
            case 5: // F -> ( E )
                valueStack.pop(); // 弹出右括号
                int eVal2 = valueStack.pop(); // 弹出E的值
                valueStack.pop(); // 弹出左括号
                valueStack.push(eVal2);
                break;
            case 6: // F -> i
                // i的值已经在栈顶
                break;
        }
    }
    
    public void pushValue(int value) {
        valueStack.push(value);
    }
    
    public int getResult() {
        return valueStack.isEmpty() ? 0 : valueStack.peek();
    }
    
    public void clear() {
        valueStack.clear();
    }
}