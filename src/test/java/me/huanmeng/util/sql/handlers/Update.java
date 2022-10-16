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
public class Update extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        UserData jeb = new UserData(1L, "Jeb", 100);
        manager.update(jeb);
        Assert.assertEquals(manager.select("Jeb"), jeb);

        jeb.setAge(18);
        manager.update(jeb);
        Assert.assertEquals(manager.select("Jeb"), jeb);

        UserData userData = manager.updateOrInsert(new UserData(null, "TestUser", 10));
        Assert.assertNotNull(userData);
        Assert.assertEquals(manager.selectFirst(((Object) null), 10), userData);

        manager.delete(userData);
        Assert.assertNull(manager.select("TestUser"));
    }
}
