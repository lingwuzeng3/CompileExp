package Exp3.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Exp1.G;

public final class Util {

    /**
     * 消除左递归（包含间接左递归）
     */
    public static G delRecursion(G g) {
        G result = new G();
        result.S = g.S;
        result.Vt.addAll(g.Vt);
        result.Vn.addAll(g.Vn);

        // 为非终结符排序（转换为List以便按顺序处理）
        List<String> orderedVn = new ArrayList<>(g.Vn);

        // 复制产生式用于处理
        HashMap<String, ArrayList<String>> productions = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : g.P.entrySet()) {
            productions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        // 逐次代入消除间接递归
        for (int i = 0; i < orderedVn.size(); i++) {
            String current = orderedVn.get(i);

            if (!productions.containsKey(current)) {
                continue;
            }

            // 代入所有更早的非终结符
            for (int j = 0; j < i; j++) {
                String earlier = orderedVn.get(j);
                substituteProductions(productions, current, earlier);
            }

            // 消除直接左递归
            delLeftRecursion1(productions, current, result.Vn);
        }

        // 存入结果文法
        result.P.putAll(productions);
        return result;
    }

    /**
     * 代入产生式
     */
    private static void substituteProductions(HashMap<String, ArrayList<String>> productions,
                                            String current, String substituteNT) {
        if (!productions.containsKey(substituteNT)) {
            return;
        }

        ArrayList<String> newProds = new ArrayList<>();
        ArrayList<String> currentProds = productions.get(current);

        for (String prod : currentProds) {
            if (prod.startsWith(substituteNT)) {
                // 需要代入：A -> Bα，将B的所有产生式代入
                String suffix = prod.substring(substituteNT.length());
                for (String subProd : productions.get(substituteNT)) {
                    newProds.add(subProd + suffix);
                }
            } else {
                // 直接保留
                newProds.add(prod);
            }
        }
        productions.put(current, newProds);
    }

    /**
     * 消除直接左递归
     */
    private static void delLeftRecursion1(HashMap<String, ArrayList<String>> productions, 
                                                   String nt, Set<String> Vn) {
        ArrayList<String> leftRecursive = new ArrayList<>(); // A -> Aα
        ArrayList<String> nonLeftRecursive = new ArrayList<>(); // A -> β

        ArrayList<String> prods = productions.get(nt);
        for (String prod : prods) {
            if (prod.startsWith(nt)) {
                leftRecursive.add(prod.substring(nt.length()));
            } else {
                nonLeftRecursive.add(prod);
            }
        }

        if (leftRecursive.isEmpty()) {
            return; // 没有左递归
        }

        // 消除左递归
        String newNT = nt + "'";
        
        // 确保新非终结符不重复
        int counter = 1;
        while (Vn.contains(newNT)) {
            newNT = nt + "'" + counter;
            counter++;
        }
        Vn.add(newNT);

        ArrayList<String> newBeta = new ArrayList<>();
        ArrayList<String> newAlpha = new ArrayList<>();

        // A -> βA'
        for (String beta : nonLeftRecursive) {
            if (beta.isEmpty()) {
                newBeta.add(newNT);
            } else {
                newBeta.add(beta + newNT);
            }
        }

        // A' -> αA' | ε
        for (String alpha : leftRecursive) {
            if (alpha.isEmpty()) {
                newAlpha.add(newNT);
            } else {
                newAlpha.add(alpha + newNT);
            }
        }
        newAlpha.add("ε");

        productions.put(nt, newBeta);
        productions.put(newNT, newAlpha);
    }

    /**
     * 提取左公因子
     */
    public static G extractLeftFactor(G g) {
        G result = new G();
        result.S = g.S;
        result.Vt.addAll(g.Vt);
        result.Vn.addAll(g.Vn);
        
        // 深拷贝产生式
        for (Map.Entry<String, ArrayList<String>> entry : g.P.entrySet()) {
            result.P.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        boolean changed;
        do {
            changed = false;
            
            // 处理每个非终结符
            for (String nt : new ArrayList<>(result.P.keySet())) {
                ArrayList<String> prods = result.P.get(nt);
                
                // 查找公共前缀分组
                Map<String, List<String>> prefixGroups = groupByCommonPrefix(prods);
                
                for (Map.Entry<String, List<String>> groupEntry : prefixGroups.entrySet()) {
                    List<String> group = groupEntry.getValue();
                    
                    if (group.size() > 1) {
                        // 找到公共前缀，需要提取左因子
                        changed = true;
                        
                        String commonPrefix = findCommonPrefix(group);
                        String newNT = generateNewVn(nt, result.Vn);
                        
                        // 添加新产生式 A -> αA'
                        ArrayList<String> newMainProds = new ArrayList<>();
                        for (String prod : prods) {
                            if (!group.contains(prod)) {
                                newMainProds.add(prod);
                            }
                        }
                        newMainProds.add(commonPrefix + newNT);
                        
                        // 添加 A' -> β1 | β2 | ...
                        ArrayList<String> newProds = new ArrayList<>();
                        for (String prod : group) {
                            String suffix = prod.substring(commonPrefix.length());
                            newProds.add(suffix.isEmpty() ? "ε" : suffix);
                        }
                        
                        result.P.put(nt, newMainProds);
                        result.P.put(newNT, new ArrayList<>(newProds));
                        result.Vn.add(newNT);
                        
                        break; // 一次只处理一个左公因子，然后重新开始
                    }
                }
                
                if (changed) {
                    break; // 重新开始循环
                }
            }
            
        } while (changed);

        return result;
    }

    /**
     * 按公共前缀分组
     */
    private static Map<String, List<String>> groupByCommonPrefix(ArrayList<String> prods) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String prod : prods) {
            if (prod.isEmpty()) {
                continue;
            }

            // 以第一个字符作为初始分组键
            String firstChar = String.valueOf(prod.charAt(0));
            if (!groups.containsKey(firstChar)) {
                groups.put(firstChar, new ArrayList<>());
            }
            groups.get(firstChar).add(prod);
        }

        // 合并有更长公共前缀的组
        boolean merged;
        do {
            merged = false;
            Map<String, List<String>> newGroups = new HashMap<>();
            
            for (List<String> group : groups.values()) {
                if (group.size() <= 1) {
                    // 单元素组直接保留
                    String key = group.get(0).substring(0, 1);
                    newGroups.put(key, group);
                    continue;
                }
                
                // 查找组内的公共前缀
                String commonPrefix = findCommonPrefix(group);
                if (commonPrefix.length() > 1) {
                    // 有更长的公共前缀，创建新组
                    newGroups.put(commonPrefix, group);
                    merged = true;
                } else {
                    // 保持原组
                    newGroups.put(commonPrefix, group);
                }
            }
            
            groups = newGroups;
        } while (merged);

        return groups;
    }

    /**
     * 找到字符串列表的最长公共前缀
     */
    private static String findCommonPrefix(List<String> strings) {
        if (strings.isEmpty()) {
            return "";
        }
        if (strings.size() == 1) {
            return strings.get(0);
        }

        String first = strings.get(0);
        int prefixLength = first.length();

        for (int i = 1; i < strings.size(); i++) {
            String current = strings.get(i);
            int j = 0;
            while (j < prefixLength && j < current.length() && first.charAt(j) == current.charAt(j)) {
                j++;
            }
            prefixLength = j;
            
            if (prefixLength == 0) {
                break;
            }
        }

        return first.substring(0, prefixLength);
    }

    /**
     * 生成新的非终结符
     */
    private static String generateNewVn(String base, Set<String> existingVn) {
        int counter = 1;
        String newNT = base + "'";

        while (existingVn.contains(newNT)) {
            newNT = base + "'" + counter;
            counter++;
        }

        return newNT;
    }

}