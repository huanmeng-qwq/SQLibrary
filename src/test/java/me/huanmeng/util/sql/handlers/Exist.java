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
public class Exist extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        Assert.assertTrue(manager.exist(((Object) null)/*dbId*/, "Jeb"));

        Assert.assertFalse(manager.exist("username", "Jeb1"));

        Assert.assertTrue(manager.exist(new UserData(null, "Jeb", 18)));
    }
}
