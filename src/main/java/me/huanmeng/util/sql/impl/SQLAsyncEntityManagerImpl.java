package me.huanmeng.util.sql.impl;

import cc.carm.lib.easysql.api.SQLManager;
import me.huanmeng.util.sql.api.SQLAsyncEntityManager;
import me.huanmeng.util.sql.api.SQLOrderData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

/**
 * 2022/10/7<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLAsyncEntityManagerImpl<T> extends SQLEntityManagerImpl<T> implements SQLAsyncEntityManager<T> {
    public SQLAsyncEntityManagerImpl(SQLEntityInstance<T> holder) {
        super(holder);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> selectAsync(Object... values) {
        return CompletableFuture.supplyAsync(() -> select(values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> selectFirstAsync(@NotNull String[] name, @NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> selectFirst(name, values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<T>> selectAnyAsync(@NotNull String[] name, @NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> selectAny(name, values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> selectFirstAsync(@NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> selectFirst(values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> selectFirstAsync(@NotNull String name, @NotNull Object o) {
        return CompletableFuture.supplyAsync(() -> selectFirst(name, o), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<T>> selectAsync(int limit) {
        return CompletableFuture.supplyAsync(() -> select(limit), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<T>> selectAsync(int limit, @Nullable SQLOrderData orderData) {
        return CompletableFuture.supplyAsync(() -> select(limit, orderData), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<T>> selectAnyAsync(int limit, @Nullable SQLOrderData orderData, @NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> selectAny(limit, orderData, values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> selectAsync(@Nullable SQLOrderData orderData, @NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> select(orderData, values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<T>> selectAllAsync(@NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> selectAll(values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<T>> selectAllAsync() {
        return CompletableFuture.supplyAsync(this::selectAll, executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> updateAsync(@NotNull T entity) {
        return CompletableFuture.supplyAsync(() -> {
            update(entity);
            return null;
        }, executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> insertAsync(@NotNull T entity) {
        return CompletableFuture.supplyAsync(() -> insert(entity), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> updateOrInsertAsync(@NotNull T entity) {
        return CompletableFuture.supplyAsync(() -> updateOrInsert(entity), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> existAsync(@NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> exist(values), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> existAsync(@NotNull T entity) {
        return CompletableFuture.supplyAsync(() -> exist(entity), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> deleteAsync(@NotNull Object... values) {
        return CompletableFuture.supplyAsync(() -> {
            delete(values);
            return null;
        }, executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> deleteAsync(@NotNull T entity) {
        return CompletableFuture.supplyAsync(() -> {
            deleteAsync(entity);
            return null;
        }, executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable T> selectAsync(@NotNull T entity) {
        return CompletableFuture.supplyAsync(() -> select(entity), executorService());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> customAsync(@NotNull BiConsumer<SQLManager, SQLEntityInstance<T>> run) {
        return CompletableFuture.supplyAsync(() -> {
            custom(run);
            return null;
        }, executorService());
    }

    public @NotNull ExecutorService executorService() {
        return holder.sqlManager().getExecutorPool();
    }

    @Override
    public SQLAsyncEntityManager<T> async() {
        return this;
    }
}
