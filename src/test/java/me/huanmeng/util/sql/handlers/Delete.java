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
public class Delete extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);

        Assert.assertEquals(4, manager.selectAll().size());

        manager.delete("Jeb");
        Assert.assertNull(manager.selectFirst("Jeb"));

        manager.delete(new UserData(null, "Lib", 19));
        Assert.assertNull(manager.selectFirst("Lib"));

        // 总共4个 删了2个
        Assert.assertEquals(2, manager.selectAll().size());

        manager.delete();
        Assert.assertTrue(manager.selectAll().isEmpty());
    }
}
