package me.huanmeng.util.sql.impl;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableAlterBuilder;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.enums.NumberType;
import me.huanmeng.util.sql.api.SQLibrary;
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

    public void createTable() throws SQLException {
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

    public T transform(ResultSet rs) {
        final T instance = newInstance();
        for (SQLEntityFieldMetaData<T> field : metaData().fields()) {
            field.sqlType().transform(rs, field, instance);
        }
        return instance;
    }

    public T newInstance() {
        return ReflectUtil.newInstanceIfPossible(clazz);
    }

    public String[] keyNames() {
        return keyNames(false);
    }

    public String[] keyNames(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (field.key() && (!field.autoIncrement() || all)) {
                list.add(field.fieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    public Object[] keyValues(T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (field.key() && !field.autoIncrement()) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    public Object[] keyValues(T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (field.key() && (all || !field.autoIncrement())) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    public String[] fieldNames() {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (!field.key() && !field.autoIncrement()) {
                list.add(field.fieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    public Object[] fieldValues(T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (!field.key() && !field.autoIncrement()) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    public String[] names(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (!field.autoIncrement() || all) {
                list.add(field.fieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    public String[] names() {
        return names(false);
    }

    public Object[] values(T entity) {
        return values(entity, false);
    }

    public Object[] values(T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.fields()) {
            if (!field.autoIncrement() || all) {
                list.add(field.getEntityValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    public SQLManager sqlManager() {
        return sqlManager;
    }

    public SQLEntityManagerImpl<T> sqlEntityManager() {
        return sqlEntityManager;
    }

    public SQLEntityMetaData<T> metaData() {
        return metaData;
    }
}
