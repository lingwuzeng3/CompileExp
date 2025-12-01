package Exp3.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import Exp1.G;

public final class Util {

  /**
   * 消除左递归（包含间接左递归）
   */
  public static G eliminateLeftRecursion(G original) {
    G result = new G();
    result.S = original.S;
    result.Vt.addAll(original.Vt);

    // 为非终结符排序
    List<String> orderedVn = new ArrayList<>(original.Vn);

    // 复制产生式用于处理
    Map<String, List<String>> productions = new HashMap<>();
    for (String key : original.P.keySet()) {
      String[] prods = original.P.get(key).split("\\|");
      productions.put(key, new ArrayList<>(Arrays.asList(prods)));
    }

    // 逐次代入消除间接递归
    for (int i = 0; i < orderedVn.size(); i++) {
      String current = orderedVn.get(i);

      if (!productions.containsKey(current))
        continue;

      // 代入所有更早的非终结符
      for (int j = 0; j < i; j++) {
        String earlier = orderedVn.get(j);
        substituteProductions(productions, current, earlier);
      }

      // 消除直接左递归
      eliminateDirectLeftRecursion(productions, current);
    }

    // 存入结果文法
    for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
      result.P.put(entry.getKey(), String.join("|", entry.getValue()));
      if (!result.Vn.contains(entry.getKey())) {
        result.Vn.add(entry.getKey());
      }
    }

    return result;
  }

  /**
   * 代入产生式
   */
  private static void substituteProductions(Map<String, List<String>> productions,
      String current, String substituteNT) {
    List<String> newProds = new ArrayList<>();
    List<String> currentProds = productions.get(current);

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
  private static void eliminateDirectLeftRecursion(Map<String, List<String>> productions, String nt) {
    List<String> leftRecursive = new ArrayList<>(); // A -> Aα
    List<String> nonLeftRecursive = new ArrayList<>(); // A -> β

    for (String prod : productions.get(nt)) {
      if (prod.startsWith(nt)) {
        leftRecursive.add(prod.substring(nt.length()));
      } else {
        nonLeftRecursive.add(prod);
      }
    }

    if (leftRecursive.isEmpty())
      return; // 没有左递归

    // 消除左递归
    String newNT = nt + "'";
    List<String> newBeta = new ArrayList<>();
    List<String> newAlpha = new ArrayList<>();

    // A -> βA'
    for (String beta : nonLeftRecursive) {
      newBeta.add(beta + newNT);
    }

    // A' -> αA' | ε
    for (String alpha : leftRecursive) {
      newAlpha.add(alpha + newNT);
    }
    newAlpha.add("ε");

    productions.put(nt, newBeta);
    productions.put(newNT, newAlpha);
  }

  /**
   * 提取左公因子
   */
  public static G extractLeftFactor(G original) {
    G result = new G();
    result.S = original.S;
    result.Vt.addAll(original.Vt);
    result.P.putAll(original.P);
    result.Vn.addAll(original.Vn);

    boolean changed;
    do {
      changed = false;
      Map<String, List<String>> productions = new HashMap<>();

      // 将当前文法转换为可处理的形式
      for (String key : result.P.keySet()) {
        String[] prods = result.P.get(key).split("\\|");
        productions.put(key, new ArrayList<>(Arrays.asList(prods)));
      }

      // 创建新的文法
      G newGrammar = new G();
      newGrammar.S = result.S;
      newGrammar.Vt.addAll(result.Vt);

      for (String nt : productions.keySet()) {
        List<String> prods = productions.get(nt);

        // 查找公共前缀
        Map<String, List<String>> prefixGroups = groupByCommonPrefix(prods);

        boolean leftFactored = false;
        for (List<String> group : prefixGroups.values()) {
          if (group.size() > 1) {
            // 找到公共前缀，需要提取左因子
            changed = true;
            leftFactored = true;

            String commonPrefix = findCommonPrefix(group);
            String newNT = generateNewNonTerminal(nt, newGrammar.Vn);

            // 添加 A -> αA'
            newGrammar.P.put(nt, commonPrefix + newNT);
            newGrammar.Vn.add(nt);

            // 添加 A' -> β1 | β2 | ...
            List<String> newProds = new ArrayList<>();
            for (String prod : group) {
              String suffix = prod.substring(commonPrefix.length());
              newProds.add(suffix.isEmpty() ? "ε" : suffix);
            }
            newGrammar.P.put(newNT, String.join("|", newProds));
            newGrammar.Vn.add(newNT);

          } else {
            // 没有公共前缀，直接添加
            if (newGrammar.P.containsKey(nt)) {
              String existing = newGrammar.P.get(nt);
              newGrammar.P.put(nt, existing + "|" + group.get(0));
            } else {
              newGrammar.P.put(nt, group.get(0));
            }
            newGrammar.Vn.add(nt);
          }
        }

        if (!leftFactored) {
          // 没有进行左因子提取，直接添加所有产生式
          newGrammar.P.put(nt, String.join("|", prods));
          newGrammar.Vn.add(nt);
        }
      }

      result = newGrammar;

    } while (changed);

    return result;
  }

  /**
   * 按公共前缀分组
   */
  private static Map<String, List<String>> groupByCommonPrefix(List<String> prods) {
    Map<String, List<String>> groups = new HashMap<>();

    for (String prod : prods) {
      if (prod.isEmpty())
        continue;

      String firstChar = String.valueOf(prod.charAt(0));
      groups.computeIfAbsent(firstChar, k -> new ArrayList<>()).add(prod);
    }

    return groups;
  }

  /**
   * 找到字符串列表的最长公共前缀
   */
  private static String findCommonPrefix(List<String> strings) {
    if (strings.isEmpty())
      return "";

    String first = strings.get(0);
    int prefixLength = 0;

    for (int i = 0; i < first.length(); i++) {
      char c = first.charAt(i);
      boolean allMatch = true;

      for (String str : strings) {
        if (str.length() <= i || str.charAt(i) != c) {
          allMatch = false;
          break;
        }
      }

      if (allMatch) {
        prefixLength++;
      } else {
        break;
      }
    }

    return first.substring(0, prefixLength);
  }

  /**
   * 生成新的非终结符
   */
  private static String generateNewNonTerminal(String base, List<String> existingVn) {
    int counter = 1;
    String newNT = base + "'";

    while (existingVn.contains(newNT)) {
      newNT = base + "'" + counter;
      counter++;
    }

    return newNT;
  }

}
