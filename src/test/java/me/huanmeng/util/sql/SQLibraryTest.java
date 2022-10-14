package me.huanmeng.util.sql;

import cc.carm.lib.easysql.beecp.BeeDataSource;
import me.huanmeng.util.sql.api.SQLAsyncEntityManager;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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
        testAsync();
    }

    public static void main(String[] args) {
        new SQLibraryTest();
    }

    public void test() {
        SQLEntityManager<UserData> userDataMapper = sqlibrary.manager(UserData.class);

        // Insert
        UserData userData = new UserData(null, Arrays.asList("SQLibrary", "SQLibrary2.0"), 18);
        // 这里的UserData的dbId已被自动写入
        UserData insertedUserData = userDataMapper.insert(userData);
        Optional.ofNullable(insertedUserData).ifPresent(e -> System.out.println("dbId: " + insertedUserData.getDbId()));

        // Update
        Optional.ofNullable(insertedUserData).ifPresent(e -> {
            insertedUserData.setUsername(Collections.singletonList("SQLibrary2.0"));
            userDataMapper.update(insertedUserData);
        });

        // Select
        Optional.ofNullable(insertedUserData).ifPresent(e -> {
            UserData selectUserData = userDataMapper.selectFirst(insertedUserData.getDbId());
            Optional.ofNullable(selectUserData).ifPresent(select -> System.out.println("userName: " + selectUserData.getUsername()));
        });
    }

    public void testAsync() {
        SQLAsyncEntityManager<UserData> asyncUserDataMapper = sqlibrary.manager(UserData.class).async();

        UserData userData = new UserData(null, Arrays.asList("SQLibraryAsync", "SQLibrary2.1"), 18);
        // 这里的UserData的dbId已被自动写入
        // Insert
        // 也可以使用
        // UserData insertedUserData asyncUserDataMapper.insertAsync(userData).get();
        asyncUserDataMapper.insertAsync(userData)
                .handle((data, throwable) -> {
                    Optional.ofNullable(data).ifPresent(e -> System.out.println("dbId: " + data.getDbId()));

//                    System.out.println(data);
                    // Update
                    Optional.ofNullable(data).ifPresent(e -> {
                        data.setUsername(Collections.singletonList("SQLibraryAsync2.1"));
                        asyncUserDataMapper.updateAsync(data).join();
                    });

                    // Select
                    Optional.ofNullable(data).ifPresent(e -> {
                        asyncUserDataMapper.selectFirstAsync(data.getDbId())
                                .whenComplete((selectUserData, throwable1) -> {
                                    Optional.ofNullable(selectUserData).ifPresent(select -> System.out.println("userName: " + selectUserData.getUsername()));
                                })
                                .join();
                    });
                    assert data != null;
                    return asyncUserDataMapper.selectFirstAsync(data.getDbId());
                }).join();

    }
}
