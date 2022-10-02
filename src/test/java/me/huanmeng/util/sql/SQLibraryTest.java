package me.huanmeng.util.sql;

import cc.carm.lib.easysql.beecp.BeeDataSource;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLibraryTest {
    private final SQLibrary sqlibrary;

    public SQLibraryTest() {
        // 修改为自己的数据库连接信息
        sqlibrary = new SQLibrary(new BeeDataSource("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:857/test",
                "root", "123456"));
        test();
    }

    public static void main(String[] args) {
        new SQLibraryTest();
    }

    public void test() {
        SQLEntityManager<UserData> userDataMapper = sqlibrary.manager(UserData.class);

        // Insert
        UserData userData = new UserData(null, "SQLibrary", 18);
        // 这里的UserData的dbId已被自动写入
        UserData insertedUserData = userDataMapper.insert(userData);
        System.out.println("dbId: " + insertedUserData.getDbId());

        // Update
        insertedUserData.setUsername("SQLibrary2.0");
        userDataMapper.update(insertedUserData);

        // Select
        UserData selectUserData = userDataMapper.selectFirst(insertedUserData.getDbId());
        System.out.println("userName: " + selectUserData.getUsername());
    }
}
