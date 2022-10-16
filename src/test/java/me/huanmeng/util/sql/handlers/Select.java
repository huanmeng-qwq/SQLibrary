package me.huanmeng.util.sql.handlers;

import me.huanmeng.util.sql.SQLTestHandler;
import me.huanmeng.util.sql.UserData;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLOrderData;
import me.huanmeng.util.sql.api.SQLibrary;
import org.junit.Assert;

import java.util.Arrays;

/**
 * 2022/10/16<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class Select extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        Assert.assertEquals(manager.select(3), Arrays.asList(InsertData.JEB, InsertData.MSQL, InsertData.LI_YANG));
        Assert.assertTrue(
                Arrays.asList(InsertData.JEB, InsertData.LI_YANG, InsertData.LIB).containsAll(
                        manager.select(3, new SQLOrderData("age", true))
                )
        );

        {
            UserData userData = manager.select(new SQLOrderData("age", false), null/*dbId*/, InsertData.MSQL.getUsername());
            Assert.assertEquals(userData, InsertData.MSQL);
        }
        {
            UserData userData = manager.select(new UserData(null, "Jeb", 17));
            Assert.assertNull(userData);
        }

        {
            UserData jeb = manager.select("Jeb");
            Assert.assertEquals(jeb, InsertData.JEB);
        }
    }
}
