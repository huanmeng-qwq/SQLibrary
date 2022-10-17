package me.huanmeng.util.sql.impl;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.IndexType;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.util.ReflectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class SQLEntityInstance<T> {
    protected final Class<T> clazz;
    protected final SQLEntityMetaData<T> metaData;
    protected final SQLManager sqlManager;
    protected final SQLEntityManagerImpl<T> sqlEntityManager;
    protected String tableName;

    public SQLEntityInstance(SQLibrary sqlibrary, Class<T> clazz, SQLManager sqlManager) throws SQLException {
        this.clazz = clazz;
        this.metaData = new SQLEntityMetaData<>(sqlibrary, clazz);
        this.sqlManager = sqlManager;
        this.tableName = metaData().tableName0();
        createTable();
        this.sqlEntityManager = new SQLEntityManagerImpl<>(this);
    }

    /**
     * 创建表
     *
     * @throws SQLException sqlException
     */
    public void createTable() throws SQLException {
        // create table.
        TableCreateBuilder table = sqlManager.createTable(tableName());
        List<String> keys = new ArrayList<>();
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (field.key()) {
                keys.add(field.fieldName());
            }
            if (field.autoIncrement()) {
                table.addAutoIncrementColumn(field.fieldName(), true, true);
            } else {
                table.addColumn(field.fieldName(), field.sqlType().toSQLString());
            }
        }
        // 这里的keys集合会存在修改 但是alter也需要 所以这里先存一份在这里
        ArrayList<String> alterList = new ArrayList<>(keys);
        if (keys.size() == 1) {
            table.setIndex(keys.remove(0), IndexType.PRIMARY_KEY);
        } else if (keys.size() >= 2) {
            table.setIndex(IndexType.PRIMARY_KEY, null, keys.remove(0), keys.toArray(new String[0]));
        }
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (keys.contains(field.fieldName())) {
                table.setIndex(field.fieldName(), field.indexType());
            }
        }
        table.setTableSettings("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        table.build().execute();

        // alter - 如果之前的表的结构类型不一致则自动转换为一致.
        Set<String> dbColumns;
        try {
            dbColumns = new LinkedHashSet<>(sqlManager.fetchTableMetadata(tableName()).listColumns().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        Map<String, SQLEntityFieldMetaData<T, ?>> columnRemap = new LinkedHashMap<>(dbColumns.size());
        Set<String> dropColumns = new LinkedHashSet<>();
        for (String column : dbColumns) {
            SQLEntityFieldMetaData<T, Object> field = metaData.getField(column);
            if (field == null) {
                columnRemap.put(column, field);
                continue;
            }
            dbColumns.add(column);
        }

    }

    /**
     * 将结果机转换为实例
     *
     * @param rs 结果集
     * @return 实例
     */
    @Nullable
    public T transform(@NotNull ResultSet rs) {
        final T instance = newInstance();
        for (SQLEntityFieldMetaData<T, Object> field : metaData().fields()) {
            field.deserialize(rs, instance);
        }
        return instance;
    }

    /**
     * 创建一个新的空数据实例
     *
     * @return 空数据实例
     * @apiNote 包装类型默认0/false
     */
    @Nullable
    public T newInstance() {
        return ReflectUtil.newInstanceIfPossible(clazz);
    }

    /**
     * @return 所有索引字段名字
     * @see SQLField#id()
     */
    @NotNull
    public String[] keyNames() {
        return keyNames(false);
    }

    /**
     * @param all 是否包含自增的字段
     * @return 所有索引字段名字
     */
    @NotNull
    public String[] keyNames(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
//        List<SQLEntityFieldMetaData<T, Object>> fields = metaData.getAutoIncrementFields();
//        if (fields.size() == 1 && fields.get(0).key() && fields.get(0).autoIncrement()) {
//            return new String[]{fields.get(0).fieldName()};
//        }
        for (SQLEntityFieldMetaData<T, ?> field : metaData.fields()) {
            if (field.key() && (!field.autoIncrement() || all)) {
                list.add(field.fieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * @param entity 实例
     * @return 实例的所有字段的值(除了自增的)
     */
    @NotNull
    public Object[] keyValues(@NotNull T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (field.key() && !field.autoIncrement()) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    /**
     * @param entity 实例
     * @param all    是否包含自增字段
     * @return 实例的所有字段的值
     */
    @NotNull
    public Object[] keyValues(@NotNull T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (field.key() && (all || !field.autoIncrement())) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    /**
     * @return 所有字段名字
     */
    @NotNull
    public String[] fieldNames() {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (!field.key() && !field.autoIncrement()) {
                list.add(field.fieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * @param entity 实例
     * @return 所有字段的值
     */
    @NotNull
    public Object[] fieldValues(@NotNull T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (!field.key() && !field.autoIncrement()) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    /**
     * @param all 是否包含自增字段
     * @return 所有字段的名字
     */
    @NotNull
    public String[] names(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (!field.autoIncrement() || all) {
                list.add(field.fieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * @return 所有字段的名字
     */
    @NotNull
    public String[] names() {
        return names(false);
    }

    /**
     * @return 所有字段的值
     */
    @NotNull
    public Object[] values(@NotNull T entity) {
        return values(entity, false);
    }

    /**
     * @param entity 实例
     * @param all    是否包含自增字段
     * @return 所有字段的值
     */
    @NotNull
    public Object[] values(@NotNull T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T, Object> field : metaData.fields()) {
            if (!field.autoIncrement() || all) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    // Getter

    /**
     * @return {@link SQLManager}
     */
    @NotNull
    public SQLManager sqlManager() {
        return sqlManager;
    }

    /**
     * @return {@link SQLEntityManager}
     */
    @NotNull
    public SQLEntityManagerImpl<T> sqlEntityManager() {
        return sqlEntityManager;
    }

    /**
     * @return {@link SQLEntityMetaData}
     */
    @NotNull
    public SQLEntityMetaData<T> metaData() {
        return metaData;
    }

    @NotNull
    public String tableName() {
        return tableName;
    }

    /**
     * @see #createTable()
     */
    @NotNull
    public SQLEntityInstance<T> tableName(@NotNull String tableName) {
        this.tableName = tableName;
        return this;
    }
}
