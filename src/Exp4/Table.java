package Exp4;
import java.util.*;

public class Table {
    
    //终结符映射
    static Map<String, Integer> map_vt = new HashMap<>();
        static Map<String, Integer> map_vn = new HashMap<>();
    
    //预测表
    static ArrayList<String>[][] table;
    
    static {
        //初始化
        map_vt.put("+", 0);
        map_vt.put("*", 1);
        map_vt.put("(", 2);
        map_vt.put(")", 3);
        map_vt.put("i", 4);
        map_vt.put("$", 5);

        map_vn.put("E", 0);
        map_vn.put("E'", 1);
        map_vn.put("T", 2);
        map_vn.put("T'", 3);
        map_vn.put("F", 4);

        table = new ArrayList[5][6];
        
        //初始化为null
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                table[i][j] = null;
            }
        }
        
        //填充预测分析表

        //1. E行
        table[0][2] = new ArrayList<>(Arrays.asList("F", "T'", "E'"));  // (
        table[0][4] = new ArrayList<>(Arrays.asList("F", "T'", "E'"));  // i
        
        //2. E'行
        table[1][0] = new ArrayList<>(Arrays.asList("+", "T", "E'"));   // +
        table[1][3] = new ArrayList<>(Arrays.asList("ε"));             // )
        table[1][5] = new ArrayList<>(Arrays.asList("ε"));             // $
        
        //3. T行
        table[2][2] = new ArrayList<>(Arrays.asList("F", "T'"));       // (
        table[2][4] = new ArrayList<>(Arrays.asList("F", "T'"));       // i
        
        //T'行
        table[3][0] = new ArrayList<>(Arrays.asList("ε"));             // +
        table[3][1] = new ArrayList<>(Arrays.asList("*", "F", "T'"));  // *
        table[3][3] = new ArrayList<>(Arrays.asList("ε"));             // )
        table[3][5] = new ArrayList<>(Arrays.asList("ε"));             // $
        
        //F行
        table[4][2] = new ArrayList<>(Arrays.asList("(", "E", ")"));   // (
        table[4][4] = new ArrayList<>(Arrays.asList("i"));             // i
    }
    
    //根据非终结符和终结符获取产生式
    public static List<String> getProduction(String vn, String vt) {
        Integer row = map_vn.get(vn);
        Integer col = map_vt.get(vt);
        
        if (row == null || col == null) {
            return null;
        }
        
        return table[row][col];
    }
    
    //打印完整的预测分析表
    public static void printTable() {
        System.out.println("=========== LL(1) 预测分析表 ===========");
        
        System.out.printf("%-8s", "");
        String[] vtSymbols = {"+", "*", "(", ")", "i", "$"};
        for (String vt : vtSymbols) {
            System.out.printf("| %-12s ", vt);
        }
        System.out.println();
                System.out.print("--------");
        for (int i = 0; i < 6; i++) {
            System.out.print("|-------------");
        }
        System.out.println();
        
        String[] vnSymbols = {"E", "E'", "T", "T'", "F"};
        for (int i = 0; i < 5; i++) {
            System.out.printf("%-8s", vnSymbols[i]);
            
            for (int j = 0; j < 6; j++) {
                List<String> production = table[i][j];
                if (production != null) {
                    String prodStr = String.join(" ", production);
                    System.out.printf("| %-12s ", prodStr);
                } else {
                    System.out.printf("| %-12s ", "error");
                }
            }
            System.out.println();
        }
    }
}