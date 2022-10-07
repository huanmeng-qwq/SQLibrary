package me.huanmeng.util.sql.api;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.huanmeng.util.sql.impl.SQLEntityInstance;
import me.huanmeng.util.sql.type.SQLType;
import me.huanmeng.util.sql.type.SQLTypes;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 2022/3/11<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings({"unchecked", "unused"})
public class SQLibrary {
    protected final Map<Class<?>, SQLEntityInstance<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    protected final DataSource dataSource;
    protected final SQLTypes sqlTypes;
    protected Gson gson;

    public SQLibrary(@NotNull DataSource dataSource) {
        this.dataSource = dataSource;
        this.sqlTypes = new SQLTypes();
        this.gson = new GsonBuilder().create();
    }

    /**
     * 获取{@link SQLEntityInstance}
     *
     * @param clazz      对应类
     * @param sqlManager {@link SQLManager}
     */
    @NotNull
    public <T> SQLEntityInstance<T> instanceOrCreate(@NotNull Class<T> clazz, @NotNull Supplier<@NotNull SQLManager> sqlManager) {
        return (SQLEntityInstance<T>) INSTANCE_MAP.computeIfAbsent(clazz, c -> {
            try {
                return new SQLEntityInstance<>(this, c, sqlManager.get());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NotNull
    public <T> SQLEntityInstance<T> instance(@NotNull Class<T> clazz) {
        return instanceOrCreate(clazz, createManager());
    }

    // ***** Sync *****

    @NotNull
    public <T> SQLEntityManager<T> manager(@NotNull Class<T> clazz) {
        return manager(clazz, createManager());
    }

    @NotNull
    public <T> SQLEntityManager<T> manager(@NotNull Class<T> clazz, @NotNull Supplier<@NotNull SQLManager> sqlManager) {
        return instanceOrCreate(clazz, sqlManager).sqlEntityManager();
    }

    // ***** Async *****

    @NotNull
    public <T> SQLEntityManager<T> managerAsync(@NotNull Class<T> clazz) {
        return manager(clazz).async();
    }

    @NotNull
    public <T> SQLAsyncEntityManager<T> managerAsync(@NotNull Class<T> clazz, @NotNull Supplier<@NotNull SQLManager> sqlManager) {
        return manager(clazz, sqlManager).async();
    }

    @NotNull
    public Supplier<@NotNull SQLManager> createManager() {
        return () -> new SQLManagerImpl(dataSource);
    }

    @NotNull
    public Supplier<@NotNull SQLManager> createManager(@NotNull DataSource dataSource) {
        return () -> new SQLManagerImpl(dataSource);
    }

    @NotNull
    public DataSource dataSource() {
        return dataSource;
    }

    @NotNull
    public SQLTypes sqlTypes() {
        return sqlTypes;
    }

    @NotNull
    public SQLType typeByClass(@NotNull Class<?> type) {
        return sqlTypes.getSQLType(type);
    }

    public boolean remove(@NotNull Class<?> clazz) {
        if (INSTANCE_MAP.containsKey(clazz)) {
            SQLEntityInstance<?> sqlEntityInstance = INSTANCE_MAP.get(clazz);
            EasySQL.shutdownManager(sqlEntityInstance.sqlManager());
            INSTANCE_MAP.remove(clazz);
            return true;
        }
        return false;
    }

    public boolean removeAll() {
        for (Map.Entry<Class<?>, SQLEntityInstance<?>> entry : INSTANCE_MAP.entrySet()) {
            SQLEntityInstance<?> sqlEntityInstance = entry.getValue();
            EasySQL.shutdownManager(sqlEntityInstance.sqlManager());
        }
        INSTANCE_MAP.clear();
        return true;
    }

    @NotNull
    public Gson gson() {
        return gson;
    }

    @NotNull
    public SQLibrary gson(@NotNull Gson gson) {
        this.gson = gson;
        return this;
    }
}
