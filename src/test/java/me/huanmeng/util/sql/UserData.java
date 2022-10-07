package me.huanmeng.util.sql;

import me.huanmeng.util.sql.api.annotation.SQLEntity;
import me.huanmeng.util.sql.api.annotation.SQLField;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@SQLEntity("users")
public class UserData {
    @SQLField(id = true, isAutoIncrement = true)
    private Long dbId;
    @SQLField
    private String username;
    @SQLField
    private Integer age;

    public UserData(Long dbId, String username, Integer age) {
        this.dbId = dbId;
        this.username = username;
        this.age = age;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "dbId=" + dbId +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
