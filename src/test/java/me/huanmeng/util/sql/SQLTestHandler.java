package me.huanmeng.util.sql;

import me.huanmeng.util.sql.api.SQLibrary;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * 2022/10/16<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public abstract class SQLTestHandler {
    @ApiStatus.OverrideOnly
    public abstract void onTest(SQLibrary sqlibrary) throws Exception;

    public boolean executeTest(int index, SQLibrary sqlibrary) {
        String testName = getClass().getSimpleName();

        print("  #%s 测试 @%s 开始", index, testName);
        long start = System.currentTimeMillis();

        try {
            onTest(sqlibrary);
            print("  #%s 测试 @%s 成功，耗时 %s ms。", index, testName, (System.currentTimeMillis() - start));
            return true;
        } catch (Exception exception) {
            print("  #%s 测试 @%s 失败，耗时 %s ms。", index, testName, (System.currentTimeMillis() - start));
            exception.printStackTrace();
            return false;
        }
    }


    protected static void print(@NotNull String format, Object... params) {
        SQLibraryTest.print(format, params);
    }
}
