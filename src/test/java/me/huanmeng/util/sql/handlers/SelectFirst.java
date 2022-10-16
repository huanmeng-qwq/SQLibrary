package me.huanmeng.util.sql.handlers;

import me.huanmeng.util.sql.SQLTestHandler;
import me.huanmeng.util.sql.UserData;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;
import org.junit.Assert;

/**
 * 2022/10/16<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SelectFirst extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        {
            UserData userData = manager.selectFirst(InsertData.JEB.getUsername());
            Assert.assertEquals(userData, InsertData.JEB);
        }
        {
            UserData userData = manager.selectFirst("username", InsertData.JEB.getUsername());
            Assert.assertEquals(userData, InsertData.JEB);
        }
        {
            UserData userData = manager.selectFirst(new String[]{"username"}, InsertData.JEB.getUsername());
            Assert.assertEquals(userData, InsertData.JEB);
        }
    }
}
