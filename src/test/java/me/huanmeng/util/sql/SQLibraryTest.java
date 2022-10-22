package me.huanmeng.util.sql;

import cc.carm.lib.easysql.beecp.BeeDataSource;
import cc.carm.lib.easysql.beecp.BeeDataSourceConfig;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.handlers.*;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 * 使用了EasySQl相同的测试风格
 *
 * @author huanmeng_qwq
 */
public class SQLibraryTest {
    protected SQLibrary sqlibrary;

    public static void print(@NotNull String format, Object... params) {
        System.out.printf((format) + "%n", params);
    }

    @Before
    public void initialize() {
        BeeDataSourceConfig config = new BeeDataSourceConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL;");
        sqlibrary = new SQLibrary(new BeeDataSource(config));
    }

    @Test
    public void test() {
        print("加载测试类...");
        SQLEntityManager<TestModel> manager = sqlibrary.manager(TestModel.class);
        manager.insert(new TestModel(null, "a", "class"));
        Set<SQLTestHandler> tests = new LinkedHashSet<>();
        tests.add(new InsertData());
        tests.add(new Exist());
        tests.add(new Custom());
        tests.add(new Select());
        tests.add(new SelectFirst());
        tests.add(new SelectAll());
        tests.add(new SelectAny());
        tests.add(new Update());
        tests.add(new Delete());
        print("准备进行测试...");

        int index = 1;
        int success = 0;

        for (SQLTestHandler currentTest : tests) {
            print("-------------------------------------------------");
            if (currentTest.executeTest(index, sqlibrary)) {
                success++;
            }

            index++;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        print(" ");
        print("全部测试执行完毕，成功 %s 个，失败 %s 个。",
                success, (tests.size() - success)
        );
    }

    @After
    public void shutdown() {
        sqlibrary.removeAll();
    }


}
