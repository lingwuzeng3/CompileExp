package Exp3.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Exp1.G;

public final class Util {

    /**
     * 消除左递归
     */
    public static G delRecursion(G g) {
        G result = new G();
        result.S = g.S;
        result.Vt.addAll(g.Vt);
        result.Vn.addAll(g.Vn);

        //非终结符，转换为List以便按顺序处理
        List<String> orderedVn = new ArrayList<>(g.Vn);

        //复制产生式用于处理
        HashMap<String, ArrayList<String>> productions = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : g.P.entrySet()) {
            productions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        //逐次代入消除间接递归
        for (int i = 0; i < orderedVn.size(); i++) {
            String current = orderedVn.get(i);

            //判断大写字母，即非终结符有无产生式
            if (!productions.containsKey(current)) {
                continue;
            }

            //代入所有更早的非终结符
            for (int j = 0; j < i; j++) {
                String earlier = orderedVn.get(j);
                substituteProductions(productions, current, earlier);
            }

            //消除直接左递归
            delLeftRecursion1(productions, current, result.Vn);
        }

        //存入结果文法
        result.P.putAll(productions);
        return result;
    }

    /**
     * 代入产生式，将current的所有产生式中以substituteNT开头的替换为substituteNT的所有产生式
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
                //假设，需要代入：A -> Bα，将B的所有产生式代入
                String suffix = prod.substring(substituteNT.length());//这里截取B后边的部分
                for (String subProd : productions.get(substituteNT)) {
                    newProds.add(subProd + suffix);//B的产生式 + α
                }
            } else {
                //保留
                newProds.add(prod);
            }
        }
        productions.put(current, newProds);//更新A的产生式
    }

    /**
     *消除直接左递归
     */
    private static void delLeftRecursion1(HashMap<String, ArrayList<String>> productions, 
                                                   String nt, Set<String> Vn) {
        ArrayList<String> leftRecursive = new ArrayList<>(); // A -> Aα
        ArrayList<String> nonLeftRecursive = new ArrayList<>(); // A -> β

        ArrayList<String> prods = productions.get(nt);
        for (String prod : prods) {
            if (prod.startsWith(nt)) {//A -> Aα，这里看是不是以自身开头
                leftRecursive.add(prod.substring(nt.length()));//提取a部分
            } else {
                nonLeftRecursive.add(prod);//直接加入
            }
        }

        if (leftRecursive.isEmpty()) {
            return; 
        }

        //消除左递归
        String newNT = nt + "'";//创建新的非终结符A'
        
        //确保新非终结符不重复
        int counter = 1;
        while (Vn.contains(newNT)) {
            newNT = nt + "'" + counter;//若已存在则添加数字后缀
            counter++;
        }
        Vn.add(newNT);

        ArrayList<String> newBeta=new ArrayList<>();//存储A的新产生式：A->βA'
        ArrayList<String> newAlpha=new ArrayList<>();//存储A'的新产生式：A'->αA'|ε

        //构建A->βA'产生式
        for(String beta:nonLeftRecursive){//遍历每个非左递归产生式β
            if(beta.isEmpty()){//如果β为空（即A->ε）
            newBeta.add(newNT);//则A->A'
            }else{
            newBeta.add(beta+newNT);//否则A->βA'
            }
        }

        //构建A'->αA'|ε产生式
        for(String alpha:leftRecursive){//遍历每个左递归的α部分
            if(alpha.isEmpty()){//如果α为空
            newAlpha.add(newNT);//则A'->A'（实际是A'->εA'，但需要避免左递归）
            }else{
            newAlpha.add(alpha+newNT);//否则A'->αA'
            }
        }
        newAlpha.add("ε");//添加ε产生式：A'->ε

        productions.put(nt,newBeta);//更新原非终结符A的产生式
        productions.put(newNT,newAlpha);//添加新非终结符A'的产生式
        

    }

/**
 * 提取左公因子
 */
public static G extractLeftFactor(G g) {
    G result = new G();
    result.S = g.S;
    result.Vt.addAll(g.Vt);
    result.Vn.addAll(g.Vn);

    //深拷贝产生式
    for (Map.Entry<String, ArrayList<String>> entry : g.P.entrySet()) {
        result.P.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }

    //标记是否进行了左公因子提取
    boolean changed;
    do {
        changed = false;

        for (String nt : new ArrayList<>(result.P.keySet())) {
            //获取当前非终结符的产生式
            ArrayList<String> prods = result.P.get(nt);

            //按公共前缀对产生式进行分组
            Map<String, List<String>> prefixGroups = groupByCommonPrefix(prods);

            //检查每个分组是否有多条产生式（即有左公因子）
            for (Map.Entry<String, List<String>> groupEntry : prefixGroups.entrySet()) {
                //获取分组内的产生式列表
                List<String> group = groupEntry.getValue();

                if (group.size() > 1) {
                    //有超过1条产生式，说明存在左公因子
                    //标记为已修改
                    changed = true;

                    //找到这些产生式的最长公共前缀
                    String commonPrefix = findCommonPrefix(group);
                    // 生成新的非终结符（如A'）
                    String newNT = generateNewVn(nt, result.Vn);

                    //构建原非终结符A的新产生式：保留无公因子的产生式，添加A->αA'
                    ArrayList<String> newMainProds = new ArrayList<>();
                    for (String prod : prods) {
                        if (!group.contains(prod)) {
                            newMainProds.add(prod);
                        }
                    }
                    //添加提取左公因子后的新产生式
                    newMainProds.add(commonPrefix + newNT);

                    //构建新非终结符A'的产生式：A'->β1|β2|..
                    ArrayList<String> newProds = new ArrayList<>();
                    for (String prod : group) {
                        // 提取公因子后的后缀
                        String suffix = prod.substring(commonPrefix.length());
                        // 如果后缀为空则用ε代替
                        newProds.add(suffix.isEmpty() ? "ε" : suffix);
                    }

                    //更新
                    result.P.put(nt, newMainProds);
                    result.P.put(newNT, new ArrayList<>(newProds));
                    result.Vn.add(newNT);

                    break;
                }
            }

            if (changed) {
                break;
            }
        }

    } while (changed); //可能需要多次提取

    return result;
}

/**
 * 按公共前缀对产生式分组
 */
private static Map<String, List<String>> groupByCommonPrefix(ArrayList<String> prods) {
    //存储分组结果
    Map<String, List<String>> groups = new HashMap<>();

    for (String prod : prods) {
        if (prod.isEmpty()) {
            continue;
        }

        //以产生式的第一个字符作为初始分组键
        String firstChar = String.valueOf(prod.charAt(0));
        if (!groups.containsKey(firstChar)) {
            //创建新的分组
            groups.put(firstChar, new ArrayList<>());
        }
        //将产生式加入对应分组
        groups.get(firstChar).add(prod);
    }

    //合并有更长公共前缀的组（迭代合并过程）
    boolean merged;
    do {
        //标记本轮是否进行了合并
        merged = false;
        //创建新的分组映射
        Map<String, List<String>> newGroups = new HashMap<>();

        //遍历现有每个分组
        for (List<String> group : groups.values()) {
            if (group.size() <= 1) {
                // 如果分组内只有1条或0条产生式
                // 使用首字符作为键
                String key = group.get(0).substring(0, 1);
                // 直接保留原分组
                newGroups.put(key, group);
                continue;
            }

            // 查找分组内所有产生式的公共前缀
            String commonPrefix = findCommonPrefix(group);
            if (commonPrefix.length() > 1) {
                // 如果公共前缀长度大于1
                // 使用公共前缀作为新键
                newGroups.put(commonPrefix, group);
                // 标记为已合并（需要继续迭代）
                merged = true;
            } else {
                newGroups.put(commonPrefix, group);
            }
        }

        groups = newGroups;
    } while (merged);
    return groups;
}

/**
 * 查找字符串列表的最长公共前缀
 */
private static String findCommonPrefix(List<String> strings) {
    if (strings.isEmpty()) {
        //空列表返回空字符串
        return "";
    }
    if (strings.size() == 1) {
        //单元素列表返回该元素本身
        return strings.get(0);
    }

    //以第一个字符串为基准
    String first = strings.get(0);
    //初始化前缀长度为第一个字符串的长度
    int prefixLength = first.length();

    //从第二个字符串开始比较
    for (int i = 1; i < strings.size(); i++) {
        //获取当前字符串
        String current = strings.get(i);
        int j = 0;
        //逐个字符比较，直到找到不匹配的位置
        while (j < prefixLength && j < current.length() && first.charAt(j) == current.charAt(j)) {
            j++;
        }
        //更新公共前缀长度
        prefixLength = j;
        if (prefixLength == 0) {
            break;
        }
    }

    return first.substring(0, prefixLength);
}

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