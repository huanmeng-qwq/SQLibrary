package me.huanmeng.util.sql.impl;

import cc.carm.lib.easysql.api.SQLAction;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.action.SQLUpdateAction;
import cc.carm.lib.easysql.api.builder.ConditionalBuilder;
import cc.carm.lib.easysql.api.builder.DeleteBuilder;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
import cc.carm.lib.easysql.api.builder.UpdateBuilder;
import me.huanmeng.util.sql.api.SQLAsyncEntityManager;
import me.huanmeng.util.sql.api.SQLEntityManager;
import me.huanmeng.util.sql.api.SQLOrderData;
import me.huanmeng.util.sql.util.BiConsumerThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLEntityManagerImpl<T> implements SQLEntityManager<T> {
    protected final SQLEntityInstance<T> holder;
    protected SQLAsyncEntityManager<T> asyncEntityManager;

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
                .inTable(holder.tableName())
                .setLimit(1)
                .addCondition(name, values);
        SQLOrderData sqlOrderData = holder.metaData().orderData();
        if (sqlOrderData != null) {
            builder.orderBy(sqlOrderData.name(), sqlOrderData.asc());
        }
        try (SQLQuery query = builder.build().execute()) {
            ResultSet rs = query.getResultSet();
            if (rs.next()) {
                return transform(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public T selectFirst(@NotNull String name, @NotNull Object o) {
        return selectFirst(new String[]{name}, o);
    }

    @Nullable
    public T transform(@NotNull ResultSet rs) {
        return holder.transform(rs);
    }

    public void transformToList(@NotNull List<T> list, @NotNull ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            list.add(holder.transform(resultSet));
        }
    }

    @Override
    public @NotNull List<T> selectAny(String[] name, Object... values) {
        List<T> list = new ArrayList<>();
        TableQueryBuilder builder = holder.sqlManager().createQuery()
                .inTable(holder.tableName())
                .addCondition(name, values);
        SQLOrderData sqlOrderData = holder.metaData().orderData();
        if (sqlOrderData != null) {
            builder.orderBy(sqlOrderData.name(), sqlOrderData.asc());
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

    @Override
    public @Nullable T selectFirstByAllField(@Nullable Object... values) {
        return selectFirst(true, values);
    }

    public T selectFirst(boolean all, @NotNull Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName()).setLimit(1);
        fillCondition(tableQueryBuilder, holder.names(all), values);
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
    public @NotNull List<T> select(int limit) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName())
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
    public @Nullable T select(SQLOrderData orderData, Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
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
    public @Nullable T selectByField(@Nullable SQLOrderData orderData, @Nullable Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        fillCondition(tableQueryBuilder, holder.names(false), values);
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
    public @Nullable T selectByAllField(@Nullable SQLOrderData orderData, @Nullable Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        }
        fillCondition(tableQueryBuilder, holder.names(true), values);
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
    public @NotNull List<T> select(int limit, SQLOrderData orderData) {
        SQLEntityMetaData<T> metaData = holder.metaData();
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName())
                .setLimit(limit);
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        } else {
            SQLOrderData sqlOrderData = metaData.orderData();
            if (sqlOrderData != null) {
                tableQueryBuilder.orderBy(sqlOrderData.name(), sqlOrderData.asc());
            }
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
    public @NotNull List<T> selectAny(int limit, SQLOrderData orderData, @Nullable Object... values) {
        SQLEntityMetaData<T> metaData = holder.metaData();
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName())
                .setLimit(limit);

        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        } else {
            SQLOrderData sqlOrderData = metaData.orderData();
            if (sqlOrderData != null) {
                tableQueryBuilder.orderBy(sqlOrderData.name(), sqlOrderData.asc());
            }
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
    public @NotNull List<T> selectAnyByField(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values) {
        SQLEntityMetaData<T> metaData = holder.metaData();
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName())
                .setLimit(limit);

        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        } else {
            SQLOrderData sqlOrderData = metaData.orderData();
            if (sqlOrderData != null) {
                tableQueryBuilder.orderBy(sqlOrderData.name(), sqlOrderData.asc());
            }
        }
        fillCondition(tableQueryBuilder, holder.names(false), values);
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            transformToList(ts, query.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public @NotNull List<T> selectAnyByAllField(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values) {
        SQLEntityMetaData<T> metaData = holder.metaData();
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName())
                .setLimit(limit);

        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.name(), orderData.asc());
        } else {
            SQLOrderData sqlOrderData = metaData.orderData();
            if (sqlOrderData != null) {
                tableQueryBuilder.orderBy(sqlOrderData.name(), sqlOrderData.asc());
            }
        }
        fillCondition(tableQueryBuilder, holder.names(true), values);
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            transformToList(ts, query.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public @NotNull List<T> selectAll(Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        fillCondition(tableQueryBuilder, holder.names(false), values);
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
    public @NotNull List<T> selectAllByAllField(@Nullable Object... values) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        fillCondition(tableQueryBuilder, holder.names(true), values);
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
    public @NotNull List<T> selectAll() {
        ArrayList<T> ts = new ArrayList<>();
        try (SQLQuery query = holder.sqlManager().createQuery()
                .inTable(holder.tableName())
                .build()
                .execute()) {
            transformToList(ts, query.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ts;
    }

    @Override
    public void update(@NotNull T entity) {
        List<SQLEntityFieldMetaData<T, Object>> list = holder.metaData().getAutoIncrementFields();
        UpdateBuilder updateBuilder = holder.sqlManager().createUpdate(holder.tableName()).setLimit(1);
        if (list.size() == 1) {
            SQLEntityFieldMetaData<T, Object> field = list.get(0);
            updateBuilder.addCondition(field.fieldName(), field.getEntityValue(entity));
            SQLAction<Integer> sqlAction = updateBuilder
                    .setColumnValues(holder.fieldNames(), holder.fieldValues(entity))
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
    public @Nullable T insert(@NotNull T entity) {
        try {
            SQLUpdateAction<?> updateAction = holder.sqlManager().createInsert(holder.tableName())
                    .setColumnNames(holder.names())
                    .setParams(holder.values(entity));
            if (holder.isSupportReturnKey()) {
                List<SQLEntityFieldMetaData<T, Object>> fields = holder.metaData.getAutoIncrementFields();
                Integer id = updateAction.returnGeneratedKey(Integer.class).execute();
                return selectFirst(new String[]{fields.get(0).fieldName}, id);
            }
            updateAction.execute();
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
    public @Nullable T updateOrInsert(@NotNull T entity) {
        if (exist(entity)) {
            update(entity);
            return select(entity);
        }
        return insert(entity);
    }

    @Override
    public boolean exist(@NotNull T entity) {
        boolean exist;
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        List<SQLEntityFieldMetaData<T, Object>> list = holder.metaData().getAutoIncrementFields();
        List<SQLEntityFieldMetaData<T, Object>> collect = holder.metaData().fields().stream().filter(e -> !e.autoIncrement() && e.key()).collect(Collectors.toList());
        if (list.isEmpty() && !collect.isEmpty()) {
            for (SQLEntityFieldMetaData<T, Object> fieldMetaData : collect) {
                Object entityValue = fieldMetaData.getEntityValue(entity);
                if (entityValue == null) {
                    continue;
                }
                tableQueryBuilder.addCondition(fieldMetaData.fieldName, entityValue);
            }
        } else {
            fillCondition(tableQueryBuilder, holder.names(false), holder.values(entity));
        }
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
    public void delete(@NotNull T entity) {
        try {
            holder.sqlManager().createDelete(holder.tableName())
                    .addCondition(holder.names(false), holder.values(entity, false))
                    .build().execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable T select(@NotNull T userData) {
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName()).setLimit(1);
        fillCondition(tableQueryBuilder, holder.names(false), holder.values(userData));
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
    public void custom(@NotNull BiConsumerThrowable<SQLManager, SQLEntityInstance<T>, SQLException> run) {
        try {
            run.accept(holder.sqlManager(), holder);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    public SQLAsyncEntityManager<T> async() {
        if (asyncEntityManager != null) {
            return asyncEntityManager;
        }
        return asyncEntityManager = new SQLAsyncEntityManagerImpl<>(holder);
    }


    @Override
    public void delete(@NotNull Object @NotNull ... values) {
        try {
            DeleteBuilder deleteBuilder = holder.sqlManager().createDelete(holder.tableName());
            fillCondition(deleteBuilder, holder.names(false), values);
            deleteBuilder.build().execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByAllField(@NotNull Object... values) {
        try {
            DeleteBuilder deleteBuilder = holder.sqlManager().createDelete(holder.tableName());
            fillCondition(deleteBuilder, holder.names(true), values);
            deleteBuilder.build().execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exist(@Nullable Object... values) {
        boolean exist;
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        fillCondition(tableQueryBuilder, false, values);
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            exist = query.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exist;
    }

    @Override
    public boolean existByField(@Nullable Object... values) {
        boolean exist;
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        fillCondition(tableQueryBuilder, holder.names(false), values);
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            exist = query.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exist;
    }

    @Override
    public boolean existByAllField(@Nullable Object... values) {
        boolean exist;
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        fillCondition(tableQueryBuilder, holder.names(true), values);
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            exist = query.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exist;
    }

    @Override
    public boolean exist(@NotNull String name, @NotNull Object o) {
        boolean exist;
        TableQueryBuilder tableQueryBuilder = holder.sqlManager().createQuery()
                .inTable(holder.tableName());
        tableQueryBuilder.addCondition(name, o);
        try (SQLQuery query = tableQueryBuilder.build().execute()) {
            exist = query.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exist;
    }

    protected void fillCondition(@NotNull ConditionalBuilder<?, ?> conditionalBuilder, boolean all, @Nullable Object... values) {
        fillCondition(conditionalBuilder, holder.keyNames(all), values);
    }

    protected void fillCondition(@NotNull ConditionalBuilder<?, ?> conditionalBuilder, String[] names, @Nullable Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < names.length; i++) {
            if (i >= values.length) {
                break;
            }
            Object value = values[i];
            if (value == null) {
                continue;
            }
            map.put(names[i], value);
        }
        conditionalBuilder.addCondition(map.keySet().toArray(new String[0]), map.values().toArray());
    }
}
