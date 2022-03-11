package me.huanmeng.util.sql.entity;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import me.huanmeng.util.sql.manager.SQLEntityManager;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    @SneakyThrows
    @Override
    public T select(Object... values) {
        return selectFirst(values);
    }

    @SneakyThrows
    @Override
    public T selectFirst(Object... values) {
        final TableQueryBuilder tableQueryBuilder = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName()).setLimit(1);
        tableQueryBuilder.addCondition(holder.getKeyNames(), values);
        final SQLOrderData orderData = holder.getMetaData().getOrderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.getName(), orderData.isAsc());
        }
        final SQLQuery query = tableQueryBuilder
                .build().execute();
        if (query.getResultSet().next()) {
            final T transform = holder.transform(query.getResultSet());
            query.close();
            return transform;
        }
        query.close();
        return null;
    }

    @SneakyThrows
    @Override
    public List<T> select(int limit) {
        final TableQueryBuilder tableQueryBuilder = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName())
                .setLimit(limit);
        final SQLOrderData orderData = holder.getMetaData().getOrderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.getName(), orderData.isAsc());
        }
        final SQLQuery query = tableQueryBuilder
                .build().execute();
        final ResultSet resultSet = query.getResultSet();
        final ArrayList<T> ts = Lists.newArrayList();
        while (resultSet.next()) {
            ts.add(holder.transform(resultSet));
        }
        query.close();
        return ts;
    }

    @SneakyThrows
    @Override
    public T select(SQLOrderData orderData, Object... values) {
        final TableQueryBuilder tableQueryBuilder = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName());
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.getName(), orderData.isAsc());
        }
        final SQLQuery query = tableQueryBuilder
                .build().execute();
        if (query.getResultSet().next()) {
            final T transform = holder.transform(query.getResultSet());
            query.close();
            return transform;
        }
        query.close();
        return null;
    }

    @SneakyThrows
    @Override
    public List<T> select(int limit, SQLOrderData orderData) {
        final TableQueryBuilder tableQueryBuilder = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName())
                .setLimit(limit);
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.getName(), orderData.isAsc());
        }
        final SQLQuery query = tableQueryBuilder
                .build().execute();
        final ResultSet resultSet = query.getResultSet();
        final ArrayList<T> ts = Lists.newArrayList();
        while (resultSet.next()) {
            ts.add(holder.transform(resultSet));
        }
        query.close();
        return ts;
    }

    @SneakyThrows
    @Override
    public List<T> selectAny(int limit, SQLOrderData orderData, Object... values) {
        final TableQueryBuilder tableQueryBuilder = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName())
                .setLimit(limit);
        List<String> keys = new ArrayList<>(Arrays.asList(holder.getKeyNames(true)));
        List<Object> vals = new ArrayList<>(Arrays.asList(values));
        for (int i = 0; i < vals.size(); i++) {
            if (vals.get(i) == null) {
                keys.set(i, null);
            }
        }
        keys.removeIf(Objects::isNull);
        vals.removeIf(Objects::isNull);

        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.getName(), orderData.isAsc());
        }
        tableQueryBuilder.addCondition(keys.toArray(new String[0]), vals.toArray(new Object[0]));
        final SQLQuery query = tableQueryBuilder
                .build().execute();
        final ResultSet resultSet = query.getResultSet();
        final ArrayList<T> ts = Lists.newArrayList();
        while (resultSet.next()) {
            ts.add(holder.transform(resultSet));
        }
        query.close();
        return ts;
    }

    @SneakyThrows
    @Override
    public List<T> selectAll(Object... values) {
        final TableQueryBuilder tableQueryBuilder = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName())
                .selectColumns(holder.getKeyNames());
        final SQLOrderData orderData = holder.getMetaData().getOrderData();
        if (orderData != null) {
            tableQueryBuilder.orderBy(orderData.getName(), orderData.isAsc());
        }
        final SQLQuery query = tableQueryBuilder
                .build()
                .setParams(values)
                .execute();
        final ResultSet resultSet = query.getResultSet();
        final ArrayList<T> ts = Lists.newArrayList();
        while (resultSet.next()) {
            ts.add(holder.transform(resultSet));
        }
        query.close();
        return ts;
    }

    @SneakyThrows
    @Override
    public List<T> selectAll() {
        final SQLQuery query = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName())
                .build()
                .execute();
        final ResultSet resultSet = query.getResultSet();
        final ArrayList<T> ts = Lists.newArrayList();
        while (resultSet.next()) {
            ts.add(holder.transform(resultSet));
        }
        query.close();
        return ts;
    }

    @SneakyThrows
    @Override
    public void update(T entity) {
        holder.getSqlManager().createUpdate(holder.getMetaData().getTableName())
                .setLimit(1)
                .addCondition(holder.getKeyNames(), holder.getKeyValues(entity))
                .setColumnValues(holder.getFieldNames(), holder.getFieldValues(entity))
                .build()
                .execute();
    }

    @SneakyThrows
    @Override
    public T insert(T entity) {
        holder.getSqlManager().createInsert(holder.getMetaData().getTableName())
                .setColumnNames(holder.getNames())
                .setParams(holder.getValues(entity))
                .execute();
        return select(holder.getKeyValues(entity));
    }

    @Override
    public T updateOrInsert(T entity) {
        if (exist(entity)) {
            update(entity);
            return null;
        }
        return insert(entity);
    }

    @SneakyThrows
    @Override
    public boolean exist(T entity) {
        final SQLQuery query = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName())
                .addCondition(holder.getKeyNames(), holder.getKeyValues(entity))
                .build()
                .execute();
        final ResultSet rs = query.getResultSet();
        final boolean next = rs.next();
        query.close();
        return next;
    }

    @SneakyThrows
    @Override
    public void delete(T entity) {
        holder.getSqlManager().createDelete(holder.getMetaData().getTableName())
                .addCondition(holder.getNames(false), holder.getValues(entity, false))
                .build()
                .execute();
    }

    @Override
    public T select(T userData) {
        return select(holder.getKeyValues(userData));
    }

    @Override
    public void custom(BiConsumer<SQLManager, SQLEntityInstance<T>> run) {
        run.accept(holder.getSqlManager(), holder);
    }


    @SneakyThrows
    @Override
    public void delete(Object... keys) {
        holder.getSqlManager().createDelete(holder.getMetaData().getTableName())
                .addCondition(holder.getKeyNames(), keys)
                .build()
                .execute();
    }

    @SneakyThrows
    @Override
    public boolean exist(Object... keys) {
        final SQLQuery query = holder.getSqlManager().createQuery()
                .inTable(holder.getMetaData().getTableName())
                .selectColumns(holder.getKeyNames())
                .build().setParams(keys)
                .execute();
        final boolean next = query.getResultSet().next();
        query.close();
        return next;
    }

}
