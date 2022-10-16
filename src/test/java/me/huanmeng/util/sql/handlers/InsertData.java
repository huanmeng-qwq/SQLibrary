package me.huanmeng.util.sql.handlers;

import me.huanmeng.util.sql.SQLTestHandler;
import me.huanmeng.util.sql.UserData;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;

/**
 * 2022/10/16<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class InsertData extends SQLTestHandler {
    public static UserData JEB;
    public static UserData MSQL;
    public static UserData LI_YANG;
    public static UserData LIB;

    @Override
    public void onTest(SQLibrary sqlibrary) throws Exception {
        SQLEntityManager<UserData> manager = sqlibrary.manager(UserData.class);
        manager.setDebug(true);
        JEB = manager.insert(new UserData(null, "Jeb", 18));
        MSQL = manager.insert(new UserData(null, "MySQL", 20));
        LI_YANG = manager.insert(new UserData(null, "LiYang", 19));
        LIB = manager.insert(new UserData(null, "Lib", 19));
    }
}
