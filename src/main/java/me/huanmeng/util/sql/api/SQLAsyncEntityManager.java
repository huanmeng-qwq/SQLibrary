package me.huanmeng.util.sql.api;

import cc.carm.lib.easysql.api.SQLManager;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.impl.SQLEntityInstance;
import me.huanmeng.util.sql.util.BiConsumerThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 * @see SQLEntityManager
 */
public interface SQLAsyncEntityManager<T> extends SQLEntityManager<T> {
    /**
     * 查询1个实体
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @see SQLEntityInstance#keyNames()
     * @see #selectFirst(Object...)
     * @deprecated
     */
    @Deprecated
    @NotNull
    CompletableFuture<@Nullable T> selectAsync(Object... values);

    /**
     * @param name   字段名
     * @param values 字段值
     * @return 结果
     */
    @NotNull
    CompletableFuture<@Nullable T> selectFirstAsync(@NotNull String[] name, @NotNull Object... values);

    /**
     * @param name   字段名
     * @param values 字段值
     * @return 结果
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAnyAsync(@NotNull String[] name, @NotNull Object... values);

    /**
     * 查询1个实体
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @see SQLEntityInstance#keyNames()
     */
    @NotNull
    CompletableFuture<@Nullable T> selectFirstAsync(@NotNull Object... values);

    /**
     * 查询1个实体
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @see SQLEntityInstance#keyNames()
     */
    @NotNull
    CompletableFuture<@Nullable T> selectFirstByAllFieldAsync(@Nullable Object... values);

    /**
     * @param name 字段名
     * @param o    字段值
     */
    @NotNull
    CompletableFuture<@Nullable T> selectFirstAsync(@NotNull String name, @NotNull Object o);

    /**
     * 查询N个实体
     *
     * @param limit 条数
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAsync(int limit);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     * @param values    条件 仅自增字段与{@link SQLField#id()}
     * @apiNote 条件数组是
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAnyByFieldAsync(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     * @param values    条件 所有字段
     * @apiNote 条件数组是
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAnyByAllFieldAsync(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAsync(int limit, @Nullable SQLOrderData orderData);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     * @param values    条件
     * @apiNote 条件数组是
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAnyAsync(int limit, @Nullable SQLOrderData orderData, @NotNull Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件 仅自增字段与{@link SQLField#id()}
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    CompletableFuture<@Nullable T> selectAsync(@Nullable SQLOrderData orderData, @NotNull Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件 所有字段(不包括自增字段)
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    CompletableFuture<@Nullable T> selectByFieldAsync(@Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件 所有字段
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    CompletableFuture<@Nullable T> selectByAllFieldAsync(@Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询N个实体
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAllAsync(@NotNull Object... values);

    /**
     * 查询N个实体
     *
     * @param values 条件 所有字段
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAllByAllFieldAsync(@Nullable Object... values);

    /**
     * 查询所有实体
     */
    @NotNull
    CompletableFuture<@NotNull List<T>> selectAllAsync();

    /**
     * 更新一条数据<p>
     * 前提是它已经存在
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    CompletableFuture<@Nullable Void> updateAsync(@NotNull T entity);

    /**
     * 写入一条数据
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    CompletableFuture<@Nullable T> insertAsync(@NotNull T entity);

    /**
     * 更新或写入一条数据
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    CompletableFuture<@Nullable T> updateOrInsertAsync(@NotNull T entity);

    /**
     * 是否存在这一条数据
     *
     * @param values 条件 仅自增字段
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> existAsync(@NotNull Object... values);

    /**
     * 是否存在这一条数据
     *
     * @param name 字段名
     * @param o    字段值
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> existAsync(@NotNull String name, @NotNull Object o);

    /**
     * 是否存在这一条数据
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> existAsync(@NotNull T entity);

    /**
     * 删除一个数据
     * <p>
     * 不传参将删除所有的数据！！！！！！！！！！！！
     * 不传参将删除所有的数据！！！！！！！！！！！！
     * 不传参将删除所有的数据！！！！！！！！！！！！
     *
     * @param values 条件
     * @apiNote {@link SQLField#id()}
     * @see #delete(T)
     */
    @NotNull
    CompletableFuture<@Nullable Void> deleteAsync(@NotNull Object... values);

    /**
     * 删除一个数据
     * <p>
     * 不传参将删除所有的数据！！！！！！！！！！！！
     * 不传参将删除所有的数据！！！！！！！！！！！！
     * 不传参将删除所有的数据！！！！！！！！！！！！
     *
     * @param values 条件 所有字段
     * @see #delete(T)
     */
    @NotNull
    CompletableFuture<@Nullable Void> deleteByAllFieldAsync(@NotNull Object... values);

    /**
     * 删除一条数据
     *
     * @param entity 实例
     */
    @NotNull
    CompletableFuture<@Nullable Void> deleteAsync(@NotNull T entity);

    /**
     * 查询一个数据
     *
     * @param entity 示例
     */
    @NotNull
    CompletableFuture<@Nullable T> selectAsync(@NotNull T entity);

    /**
     * 自定义操作
     *
     * @param run 执行
     */
    @NotNull
    CompletableFuture<@Nullable Void> customAsync(@NotNull BiConsumerThrowable<SQLManager, SQLEntityInstance<T>, SQLException> run);
}
