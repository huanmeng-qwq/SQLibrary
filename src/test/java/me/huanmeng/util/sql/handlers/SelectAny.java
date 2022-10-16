package me.huanmeng.util.sql.handlers;

import me.huanmeng.util.sql.SQLTestHandler;
import me.huanmeng.util.sql.UserData;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLOrderData;
import me.huanmeng.util.sql.api.SQLibrary;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 2022/10/16<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SelectAny extends SQLTestHandler {
    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        {
            List<UserData> list = manager.selectAny(new String[]{"age"}, 19);
            Assert.assertTrue(Arrays.asList(InsertData.LIB, InsertData.LI_YANG).containsAll(list));
        }

        {
            List<UserData> list = manager.selectAny(1, new SQLOrderData("username", true));
            Assert.assertEquals(list, Collections.singletonList(InsertData.JEB));
        }

    }
}
