package me.huanmeng.util.sql.api;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.huanmeng.util.sql.impl.SQLEntityInstance;
import me.huanmeng.util.sql.type.SQLType;
import me.huanmeng.util.sql.type.SQLTypes;

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
@SuppressWarnings("unchecked")
public class SQLibrary {
    private final Map<Class<?>, SQLEntityInstance<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    private final DataSource dataSource;
    protected final SQLTypes sqlTypes;
    protected Gson gson;

    public SQLibrary(DataSource dataSource) {
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
    public <T> SQLEntityInstance<T> instanceOrCreate(Class<T> clazz, Supplier<SQLManager> sqlManager) {
        return (SQLEntityInstance<T>) INSTANCE_MAP.computeIfAbsent(clazz, c -> {
            try {
                return new SQLEntityInstance<>(this, c, sqlManager.get());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> SQLEntityInstance<T> instance(Class<T> clazz) {
        return instanceOrCreate(clazz, createManager());
    }

    public <T> SQLEntityManager<T> manager(Class<T> clazz) {
        return manager(clazz, createManager());
    }

    public <T> SQLEntityManager<T> manager(Class<T> clazz, Supplier<SQLManager> sqlManager) {
        return instanceOrCreate(clazz, sqlManager).sqlEntityManager();
    }

    public Supplier<SQLManager> createManager() {
        return () -> new SQLManagerImpl(dataSource);
    }

    public Supplier<SQLManager> createManager(DataSource dataSource) {
        return () -> new SQLManagerImpl(dataSource);
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public SQLTypes sqlTypes() {
        return sqlTypes;
    }

    public SQLType typeByClass(Class<?> type) {
        return sqlTypes.getSQLType(type);
    }

    public boolean remove(Class<?> clazz) {
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

    public Gson gson() {
        return gson;
    }

    public SQLibrary gson(Gson gson) {
        this.gson = gson;
        return this;
    }
}
