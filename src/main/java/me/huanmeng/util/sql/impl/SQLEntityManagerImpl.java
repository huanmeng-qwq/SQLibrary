package me.huanmeng.util.sql.impl;

import cc.carm.lib.easysql.api.SQLAction;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLQueryExecute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLEntityManagerImpl<T> implements SQLEntityManager<T> {
    private final SQLEntityInstance<T> holder;

    public SQLEntityManagerImpl(SQLEntityInstance<T> holder) {
        this.holder = holder;
    }

    @Override
    public T select(Object... values) {
        return selectFirst(values);
    }

    @Override
    public T selectFirst(String[] name, Object... values) {
        TableQueryBuilder builder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .setLimit(1)
                .addCondition(name, values);
        if (holder.metaData().orderData() != null) {
            builder.orderBy(holder.metaData().orderData().name(), holder.metaData().orderData().asc());
        }
        try (SQLQuery query = builder
                .build().execute()) {
            ResultSet rs = query.getResultSet();
            if (rs.next()) {
                T transform = holder.transform(rs);
                query.close();
                return transform;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<T> selectAny(String[] name, Object... values) {
        List<T> list = new ArrayList<>();
        TableQueryBuilder builder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .addCondition(name, values);
        if (holder.metaData().orderData() != null) {
            builder.orderBy(holder.metaData().orderData().name(), holder.metaData().orderData().asc());
        }
        try (SQLQuery query = builder.build().execute()) {
            ResultSet rs = query.getResultSet();
            while (rs.next()) {
                list.add(holder.transform(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public T selectFirst(Object... values) {
        return selectFirst(false, values);
    }

    public T selectFirst(boolean all, Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName()).setLimit(1);
        tableQueryBuilder.addCondition(holder.getKeyNames(all), values);
        SQLOrderData orderData = holder.metaData().orderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            if (query.getResultSet().next()) {
                T transform = holder.transform(query.getResultSet());
                query.close();
                return transform;
            }
            query.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<T> select(int limit) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .setLimit(limit);
        SQLOrderData orderData = holder.metaData().orderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        ArrayList<T> ts;
        try (SQLQuery query = tableQueryBuilder
                .build().execute()) {
            ResultSet resultSet = query.getResultSet();
            ts = new ArrayList<>();
            while (resultSet.next()) {
                ts.add(holder.transform(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public T select(SQLOrderData orderData, Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName());
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        try (SQLQuery query = tableQueryBuilder
                .build().execute()) {
            if (query.getResultSet().next()) {
                T transform = holder.transform(query.getResultSet());
                query.close();
                return transform;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<T> select(int limit, SQLOrderData orderData) {
        SQLEntityMetaData<T> metaData = holder.metaData();
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(metaData.tableName())
                .setLimit(limit);
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        } else if (metaData.orderData() != null) {
            tableQueryBuilder.orderBy(metaData.orderData().name(), metaData.orderData().asc());
        }
        ArrayList<T> ts;
        try (SQLQuery query = tableQueryBuilder
                .build().execute()) {
            ResultSet resultSet = query.getResultSet();
            ts = new ArrayList<>();
            while (resultSet.next()) {
                ts.add(holder.transform(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public List<T> selectAny(int limit, SQLOrderData orderData, Object... values) {
        SQLEntityMetaData<T> metaData = holder.metaData();
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(metaData.tableName())
                .setLimit(limit);
        // 这里使用ArrayList再包装是因为Arrays#asList返回的List是无法修改的.
        List<String> keys = new ArrayList<>(Arrays.asList(holder.getKeyNames(true)));
        List<Object> objects = new ArrayList<>(Arrays.asList(values));
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.get(i);
            if (o != null) {
                map.put(keys.get(i), o);
            }
        }


        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        } else if (metaData.orderData() != null) {
            tableQueryBuilder.orderBy(metaData.orderData().name(), metaData.orderData().asc());
        }
        tableQueryBuilder.addCondition(map.keySet().toArray(new String[0]), map.values().toArray(new Object[0]));
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            ResultSet resultSet = query.getResultSet();
            while (resultSet.next()) {
                ts.add(holder.transform(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public List<T> selectAll(Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .addCondition(holder.getKeyNames(), values);
        SQLOrderData orderData = holder.metaData().orderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        ArrayList<T> ts;
        try (SQLQuery query = tableQueryBuilder
                .build()
                .execute()) {
            ResultSet resultSet = query.getResultSet();
            ts = new ArrayList<>();
            while (resultSet.next()) {
                ts.add(holder.transform(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public List<T> selectAll() {
        ArrayList<T> ts;
        try (SQLQuery query = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .build()
                .execute()) {
            ResultSet resultSet = query.getResultSet();
            ts = new ArrayList<>();
            while (resultSet.next()) {
                ts.add(holder.transform(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public void update(T entity) {
        List<SQLEntityFieldMetaData<T>> list = holder.metaData().getAutoIncrementFields();
        if (list.size() == 1) {
            SQLEntityFieldMetaData<T> field = list.get(0);
            SQLAction<Integer> sqlAction = holder.sqlManager().createUpdate(holder.metaData().tableName())
                    .setLimit(1)
                    .addCondition(field.fieldName(), field.getEntityValue(entity))
                    .setColumnValues(holder.getNames(false), holder.getValues(entity, false))
                    .build();
            try {
                sqlAction.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            holder.sqlManager().createUpdate(holder.metaData().tableName())
                    .setLimit(1)
                    .addCondition(holder.getKeyNames(true), holder.getKeyValues(entity, true))
                    .setColumnValues(holder.getNames(true), holder.getValues(entity, true))
                    .build()
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T insert(T entity) {
        try {
            holder.sqlManager().createInsert(holder.metaData().tableName())
                    .setColumnNames(holder.getNames())
                    .setParams(holder.getValues(entity))
                    .returnGeneratedKey()
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //只有在 只有一个自增id时才使用LastInsertID来查询写入的entity
        if (holder.metaData().getAutoIncrementFields().size() != 1) {
            return entity;
        }
        return selectLastInsertById();
    }

    private T selectLastInsertById() {
        try (SQLQuery query = holder.sqlManager().createQuery().withPreparedSQL("select LAST_INSERT_ID()").execute()) {
            ResultSet rs = query.getResultSet();
            if (rs.next()) {
                int dbId = rs.getInt(1);
                query.close();
                return selectFirst(new String[]{holder.metaData().getAutoIncrementFields().get(0).fieldName()}, dbId);
            }
            query.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public T updateOrInsert(T entity) {
        if (exist(entity)) {
            update(entity);
            return null;
        }
        return insert(entity);
    }

    @Override
    public boolean exist(T entity) {
        boolean next;
        try (SQLQuery query = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .addCondition(holder.getKeyNames(), holder.getKeyValues(entity))
                .build()
                .execute()) {
            ResultSet rs = query.getResultSet();
            next = rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return next;
    }

    @Override
    public void delete(T entity) {
        try {
            holder.sqlManager().createDelete(holder.metaData().tableName())
                    .addCondition(holder.getNames(false), holder.getValues(entity, false))
                    .build()
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T select(T userData) {
        return selectFirst(true, holder.getKeyValues(userData, true));
    }

    @Override
    public void custom(BiConsumer<SQLManager, SQLEntityInstance<T>> run) {
        run.accept(holder.sqlManager(), holder);
    }

    @Override
    public boolean toggleDebug() {
        boolean debugMode = !holder.sqlManager().isDebugMode();
        holder.sqlManager().setDebugMode(debugMode);
        return debugMode;
    }

    @Override
    public void setDebug(boolean debug) {
        holder.sqlManager().setDebugMode(debug);
    }

    @Override
    public boolean isDebug() {
        return holder.sqlManager().isDebugMode();
    }


    @Override
    public void delete(Object... keys) {
        try {
            holder.sqlManager().createDelete(holder.metaData().tableName())
                    .addCondition(holder.getKeyNames(), keys)
                    .build()
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exist(Object... keys) {
        boolean next;
        try (SQLQuery query = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .addCondition(holder.getKeyNames(), keys)
                .build()
                .execute();) {

            next = query.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return next;
    }


    private <ACTION extends SQLAction<?>, RESULT> void execute(ACTION query, boolean async, SQLQueryExecute<ACTION, RESULT> execute) {
        if (async) {
            query.executeAsync();
        }
    }
}
