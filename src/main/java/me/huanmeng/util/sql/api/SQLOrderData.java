package me.huanmeng.util.sql.api;

/**
 * 2022/2/4<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLOrderData {
    private final String name;
    private final boolean asc;

    public SQLOrderData(String name, boolean asc) {
        this.name = name;
        this.asc = asc;
    }

    public String name() {
        return name;
    }

    public boolean asc() {
        return asc;
    }
}
