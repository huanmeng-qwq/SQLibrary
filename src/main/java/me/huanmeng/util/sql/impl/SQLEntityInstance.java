package me.huanmeng.util.sql.impl;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableAlterBuilder;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.enums.NumberType;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.util.ReflectUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLEntityInstance<T> {
    private final Class<T> clazz;
    private final SQLEntityMetaData<T> metaData;
    private final SQLManager sqlManager;
    private final SQLEntityManagerImpl<T> sqlEntityManager;

    public SQLEntityInstance(SQLibrary sqlibrary, Class<T> clazz, SQLManager sqlManager) throws SQLException {
        this.clazz = clazz;
        this.metaData = new SQLEntityMetaData<>(sqlibrary, clazz);
        this.sqlManager = sqlManager;
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
        final TableCreateBuilder table = sqlManager.createTable(metaData.tableName());
        List<String> keys = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (field.key()) {
                keys.add(field.fieldName());
            }
            if (field.autoIncrement()) {
                table.addAutoIncrementColumn(field.fieldName(), false);
            } else {
                table.addColumn(field.fieldName(), field.sqlType().toSQLString());
            }
        }
        ArrayList<String> alterList = new ArrayList<>(keys);
        if (keys.size() == 1) {
            table.setIndex(keys.remove(0), IndexType.PRIMARY_KEY);
        } else if (keys.size() >= 2) {
            table.setIndex(IndexType.PRIMARY_KEY, null, keys.remove(0), keys.toArray(new String[0]));
        }
        table.setTableSettings("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        table.build().execute();

        // alter - 如果之前的表的结构类型不一致则自动转换为一致.
        Set<String> columns;
        try {
            columns = sqlManager.fetchTableMetadata(metaData.tableName()).listColumns().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        List<SQLEntityFieldMetaData<T>> notFound = metaData.fields()
                .stream()
                .filter(e -> !columns.contains(e.fieldName()))
                .collect(Collectors.toList());
        if (!notFound.isEmpty()) {
            TableAlterBuilder tableAlter = sqlManager.alterTable(metaData.tableName());
            for (SQLEntityFieldMetaData<T> field : notFound) {
                if (field.autoIncrement()) {
                    tableAlter.addAutoIncrementColumn(field.fieldName(), NumberType.INT).execute();
                } else {
                    tableAlter.addColumn(field.fieldName(), field.sqlType().toSQLString()).execute();
                }
            }
            if (alterList.size() == 1 && notFound.stream().anyMatch(e -> e.fieldName().equals(alterList.get(0)))) {
                tableAlter.addIndex(IndexType.PRIMARY_KEY, null, alterList.remove(0)).execute();
            } else if (alterList.size() >= 2 && notFound.stream().anyMatch(e -> e.fieldName().equals(alterList.get(0)))) {
                tableAlter.addIndex(IndexType.PRIMARY_KEY, null, alterList.remove(0), alterList.toArray(new String[0])).execute();
            }
        }
    }

    /**
     * 将结果机转换为实例
     *
     * @param rs 结果集
     * @return 实例
     */
    public T transform(ResultSet rs) {
        final T instance = newInstance();
        for (SQLEntityFieldMetaData<T> field : metaData().fields()) {
            field.sqlType().transform(rs, field, instance);
        }
        return instance;
    }

    /**
     * 创建一个新的空数据实例
     *
     * @return 空数据实例
     * @apiNote 包装类型默认0/false
     */
    public T newInstance() {
        return ReflectUtil.newInstanceIfPossible(clazz);
    }

    /**
     * @return 所有索引字段名字
     * @see SQLField#id()
     */
    public String[] keyNames() {
        return keyNames(false);
    }

    /**
     * @param all 是否包含自增的字段
     * @return 所有索引字段名字
     */
    public String[] keyNames(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
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
    public Object[] keyValues(T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (field.key() && !field.autoIncrement()) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    /**
     * @param entity 实例
     * @param all 是否包含自增字段
     * @return 实例的所有字段的值
     */
    public Object[] keyValues(T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (field.key() && (all || !field.autoIncrement())) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    /**
     * @return 所有字段名字
     */
    public String[] fieldNames() {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
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
    public Object[] fieldValues(T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
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
    public String[] names(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (!field.autoIncrement() || all) {
                list.add(field.fieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * @return 所有字段的名字
     */
    public String[] names() {
        return names(false);
    }

    /**
     * @return 所有字段的值
     */
    public Object[] values(T entity) {
        return values(entity, false);
    }

    /**
     * @param entity 实例
     * @param all 是否包含自增字段
     * @return 所有字段的值
     */
    public Object[] values(T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
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
    public SQLManager sqlManager() {
        return sqlManager;
    }

    /**
     * @return {@link SQLEntityManager}
     */
    public SQLEntityManagerImpl<T> sqlEntityManager() {
        return sqlEntityManager;
    }

    /**
     * @return {@link SQLEntityMetaData}
     */
    public SQLEntityMetaData<T> metaData() {
        return metaData;
    }
}
