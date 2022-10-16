package me.huanmeng.util.sql.handlers;

import me.huanmeng.util.sql.SQLTestHandler;
import me.huanmeng.util.sql.UserData;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * 2022/10/16<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SelectAll extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        Assert.assertEquals(manager.selectAll(), Arrays.asList(InsertData.JEB, InsertData.MSQL, InsertData.LI_YANG, InsertData.LIB));

        List<UserData> list = manager.selectAll(null, InsertData.LI_YANG.getAge());
        Assert.assertTrue(Arrays.asList(InsertData.LI_YANG, InsertData.LIB).containsAll(list));
    }
}
