package me.huanmeng.util.sql.handlers;

import me.huanmeng.util.sql.SQLTestHandler;
import me.huanmeng.util.sql.UserData;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;
import org.junit.Assert;

import java.util.Objects;

/**
 * 2022/10/17<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class Custom extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        manager.custom((sql, holder) -> {
            Assert.assertEquals(
                    1,
                    Objects.requireNonNull(
                            sql.executeSQLBatch("SELECT * from users where username='Jeb' FOR UPDATE")
                    ).get(0).intValue()
            );
        });
    }
}
