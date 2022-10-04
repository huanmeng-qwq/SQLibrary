package me.huanmeng.util.sql.impl;

import cc.carm.lib.easysql.api.SQLAction;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.builder.ConditionalBuilder;
import cc.carm.lib.easysql.api.builder.DeleteBuilder;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
import cc.carm.lib.easysql.api.builder.UpdateBuilder;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLOrderData;
import me.huanmeng.util.sql.api.SQLQueryExecute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
                return transform(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public T transform(ResultSet rs) {
        return holder.transform(rs);
    }

    public void transformToList(List<T> list, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            list.add(holder.transform(resultSet));
        }
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
            transformToList(list, query.getResultSet());
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
        fillCondition(tableQueryBuilder, all, values);
        SQLOrderData orderData = holder.metaData().orderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            if (query.getResultSet().next()) {
                return transform(query.getResultSet());
            }
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
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            transformToList(ts, query.getResultSet());
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
        fillCondition(tableQueryBuilder, true, values);
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            if (query.getResultSet().next()) {
                return transform(query.getResultSet());
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
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            transformToList(ts, query.getResultSet());
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

        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        } else if (metaData.orderData() != null) {
            tableQueryBuilder.orderBy(metaData.orderData().name(), metaData.orderData().asc());
        }
        fillCondition(tableQueryBuilder, true, values);
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            transformToList(ts, query.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public List<T> selectAll(Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName());
        fillCondition(tableQueryBuilder, false, values);
        SQLOrderData orderData = holder.metaData().orderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            transformToList(ts, query.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public List<T> selectAll() {
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName())
                .build()
                .execute()) {
            transformToList(ts, query.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public void update(T entity) {
        List<SQLEntityFieldMetaData<T>> list = holder.metaData().getAutoIncrementFields();
        UpdateBuilder updateBuilder = holder.sqlManager().createUpdate(holder.metaData().tableName()).setLimit(1);
        if (list.size() == 1) {
            SQLEntityFieldMetaData<T> field = list.get(0);
            updateBuilder.addCondition(field.fieldName(), field.getEntityValue(entity));
            SQLAction<Integer> sqlAction = updateBuilder
                    .setColumnValues(holder.names(false), holder.values(entity, false))
                    .build();
            try {
                sqlAction.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            fillCondition(updateBuilder, true, holder.keyValues(entity, true));
            updateBuilder.setColumnValues(holder.names(true), holder.values(entity, true)).build()
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T insert(T entity) {
        try {
            holder.sqlManager().createInsert(holder.metaData().tableName())
                    .setColumnNames(holder.names())
                    .setParams(holder.values(entity))
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
                long dbId = rs.getLong(1);
                return selectFirst(new String[]{holder.metaData().getAutoIncrementFields().get(0).fieldName()}, dbId);
            }
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
        boolean exist;
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName());
        fillCondition(tableQueryBuilder, false, holder.values(entity));
        try (SQLQuery query = tableQueryBuilder
                .build()
                .execute()) {
            ResultSet rs = query.getResultSet();
            exist = rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exist;
    }

    @Override
    public void delete(T entity) {
        try {
            holder.sqlManager().createDelete(holder.metaData().tableName())
                    .addCondition(holder.names(false), holder.values(entity, false))
                    .build().execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T select(T userData) {
        return selectFirst(true, holder.keyValues(userData, true));
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
    public void delete(Object... values) {
        try {
            DeleteBuilder deleteBuilder = holder.sqlManager().createDelete(holder.metaData().tableName());
            fillCondition(deleteBuilder, false, values);
            deleteBuilder.build().execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exist(Object... values) {
        boolean exist;
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.metaData().tableName());
        fillCondition(tableQueryBuilder, false, values);
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            exist = query.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exist;
    }


    private <ACTION extends SQLAction<?>, RESULT> void execute(ACTION query, boolean async, SQLQueryExecute<ACTION, RESULT> execute) {
        if (async) {
            query.executeAsync();
        }
    }

    protected void fillCondition(ConditionalBuilder<?, ?> conditionalBuilder, boolean all, Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        String[] keyNames = holder.keyNames(all);
        for (int i = 0; i < keyNames.length; i++) {
            if (i >= values.length) {
                break;
            }
            map.put(keyNames[i], values[i]);
        }
        conditionalBuilder.addCondition(map.keySet().toArray(new String[0]), map.values().toArray());
    }
}
