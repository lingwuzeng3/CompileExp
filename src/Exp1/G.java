package Exp1;

import java.util.ArrayList;
import java.util.HashMap;

// G（Vn, Vt, S, P）
// 建立文法的数据结构
public class G {

    public G(){
    }

    // 非终结符集合
    public ArrayList<String> Vn = new ArrayList<String>();
    // 终结符集合
    public ArrayList<String> Vt = new ArrayList<String>();
    // 开始符号
    public String S;
    // 产生式集合
    public HashMap<String,String> P = new HashMap<String,String>();

}
