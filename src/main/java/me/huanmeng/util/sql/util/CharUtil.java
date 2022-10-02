package me.huanmeng.util.sql.util;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class CharUtil {
    private CharUtil() {
    }

    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
