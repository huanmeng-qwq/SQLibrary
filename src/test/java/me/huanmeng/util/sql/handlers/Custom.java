package me.huanmeng.util.sql.handlers;

import cc.carm.lib.easysql.api.SQLQuery;
import me.huanmeng.util.sql.SQLTestHandler;
import me.huanmeng.util.sql.UserData;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;
import org.junit.Assert;

import java.sql.ResultSet;

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

            try (SQLQuery query = sql.createQuery().withSQL("SELECT * from users where username='Jeb' FOR UPDATE").execute()) {
                ResultSet resultSet = query.getResultSet();
                if (resultSet.next()) {
                    UserData userData = holder.transform(resultSet);
                    Assert.assertEquals(InsertData.JEB, userData);
                }
            }
        });
    }
}
