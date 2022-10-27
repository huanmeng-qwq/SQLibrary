package me.huanmeng.util.sql;

import me.huanmeng.util.sql.api.annotation.SQLField;

/**
 * 2022/10/21<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class TestModel {
    @SQLField(id = true,isAutoIncrement = true)
    private Long sid;
    @SQLField(id = true)
    private String username;
    private String clazz;

    public TestModel() {
    }

    public TestModel(Long sid, String username, String clazz) {
        this.sid = sid;
        this.username = username;
        this.clazz = clazz;
    }


}
