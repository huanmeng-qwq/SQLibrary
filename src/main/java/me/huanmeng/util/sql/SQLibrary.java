package me.huanmeng.util.sql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.beecp.BeeDataSource;
import cc.carm.lib.easysql.beecp.BeeDataSourceConfig;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import me.huanmeng.util.sql.entity.SQLEntityInstance;
import me.huanmeng.util.sql.manager.SQLEntityManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2022/3/11<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unchecked")
public class SQLibrary {
    private static final Map<Class<?>, SQLEntityInstance<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static <T> SQLEntityInstance<T> getInstance(Class<T> clazz, SQLManager sqlManager) {
        return (SQLEntityInstance<T>) INSTANCE_MAP.computeIfAbsent(clazz, c -> new SQLEntityInstance<>(c, sqlManager));
    }

    public static <T> SQLEntityManager<T> getManager(Class<T> clazz, SQLManager sqlManager) {
        return (SQLEntityManager<T>) getInstance(clazz, sqlManager).getSqlEntityManager();
    }

    public static SQLManager createManager(@NotNull String driver, @NotNull String url, @NotNull String username, @Nullable String password) {
        return createManager(new BeeDataSourceConfig(driver, url, username, password));
    }

    public static SQLManager createManager(@NotNull BeeDataSourceConfig config) {
        return new SQLManagerImpl(new BeeDataSource(config));
    }

    public static SQLManager createManager(DataSource dataSource) {
        return new SQLManagerImpl(dataSource);
    }
}
