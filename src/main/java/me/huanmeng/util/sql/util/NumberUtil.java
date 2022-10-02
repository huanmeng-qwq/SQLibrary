package me.huanmeng.util.sql.util;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class NumberUtil {
    private NumberUtil(){
    }

    public static boolean isInt(String s){
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
