package me.huanmeng.util.sql.entity;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.IndexType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@Getter
public class SQLEntityInstance<T> {
    private final Class<T> clazz;
    private final SQLEntityMetaData<T> metaData;
    private final SQLManager sqlManager;
    private final SQLEntityManagerImpl<T> sqlEntityManager;

    public SQLEntityInstance(Class<T> clazz, SQLManager sqlManager) {
        this.clazz = clazz;
        this.metaData = new SQLEntityMetaData<>(clazz);
        this.sqlManager = sqlManager;
        createTable();
        this.sqlEntityManager = new SQLEntityManagerImpl<>(this);
    }

    @SneakyThrows
    public void createTable() {
        final TableCreateBuilder table = sqlManager.createTable(metaData.getTableName());
        List<String> keys = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (field.isKey()) {
                keys.add(field.getFieldName());
            }
            if (field.isAutoIncrement()) {
                table.addAutoIncrementColumn(field.getFieldName(), false);
            } else {
                table.addColumn(field.getFieldName(), field.getSqlType().toSQLString());
            }
        }
        if (keys.size() == 1) {
            table.setIndex(keys.remove(0), IndexType.PRIMARY_KEY);
        } else if (keys.size() >= 2) {
            table.setIndex(IndexType.PRIMARY_KEY, null, keys.remove(0), keys.toArray(new String[0]));
        }
        table.setTableSettings("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        table.build().execute();
    }

    @SneakyThrows
    public T transform(ResultSet rs) {
        final T instance = newInstance();
        for (SQLEntityFieldMetaData<T> field : getMetaData().getFields()) {
            field.getSqlType().transform(field).apply(rs, instance);
        }
        return instance;
    }

    @SneakyThrows
    public T newInstance() {
        return clazz.newInstance();
    }

    public String[] getKeyNames() {
        return getKeyNames(false);
    }

    public String[] getKeyNames(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (field.isKey() && (!field.isAutoIncrement() || all)) {
                list.add(field.getFieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    public Object[] getKeyValues(T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (field.isKey() && !field.isAutoIncrement()) {
                list.add(field.getValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    public Object[] getKeyValues(T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (field.isKey() && (all || !field.isAutoIncrement())) {
                list.add(field.getValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    public String[] getFieldNames() {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (!field.isKey() && !field.isAutoIncrement()) {
                list.add(field.getFieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    public Object[] getFieldValues(T entity) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (!field.isKey() && !field.isAutoIncrement()) {
                list.add(field.getValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }

    public String[] getNames(boolean all) {
        final ArrayList<String> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (!field.isAutoIncrement() || all) {
                list.add(field.getFieldName());
            }
        }
        return list.toArray(new String[0]);
    }

    public String[] getNames() {
        return getNames(false);
    }

    public Object[] getValues(T entity) {
        return getValues(entity, false);
    }

    public Object[] getValues(T entity, boolean all) {
        final ArrayList<Object> list = new ArrayList<>();
        for (SQLEntityFieldMetaData<T> field : metaData.getFields()) {
            if (!field.isAutoIncrement() || all) {
                list.add(field.getValue(entity));
            }
        }
        return list.toArray(new Object[0]);
    }
}
