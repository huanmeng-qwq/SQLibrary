package me.huanmeng.util.sql.api;

import cc.carm.lib.easysql.api.SQLManager;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.impl.SQLEntityInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public interface SQLEntityManager<T> {
    /**
     * 查询1个实体
     *
     * @param values 条件
     * @apiNote {@link SQLField#id()}
     * @see SQLEntityInstance#keyNames()
     * @see #selectFirst(Object...)
     * @deprecated
     */
    @Deprecated
    @Nullable
    T select(Object... values);

    /**
     * @param name   字段名
     * @param values 字段值
     * @return 结果
     */
    @Nullable
    T selectFirst(@NotNull String[] name, @Nullable Object... values);

    /**
     * @param name 字段名
     * @param o    字段值
     */
    @Nullable T selectFirst(@NotNull String name, @NotNull Object o);

    /**
     * @param name   字段名
     * @param values 字段值
     * @return 结果
     */
    @NotNull
    List<T> selectAny(@NotNull String[] name, @NotNull Object... values);

    /**
     * 查询1个实体
     *
     * @param values 条件
     * @apiNote {@link SQLField#id()}
     * @see SQLEntityInstance#keyNames()
     */
    @Nullable
    T selectFirst(@Nullable Object... values);

    /**
     * 查询N个实体
     *
     * @param limit 条数
     */
    @NotNull
    List<T> select(int limit);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     */
    @NotNull
    List<T> select(int limit, @Nullable SQLOrderData orderData);

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
    List<T> selectAny(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @Nullable
    T select(@Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询N个实体
     *
     * @param values 条件
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    List<T> selectAll(@NotNull Object... values);

    /**
     * 查询所有实体
     */
    @NotNull
    List<T> selectAll();

    /**
     * 更新一条数据<p>
     * 前提是它已经存在
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    void update(@NotNull T entity);

    /**
     * 写入一条数据
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    @Nullable
    T insert(@NotNull T entity);

    /**
     * 更新或写入一条数据
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    @Nullable
    T updateOrInsert(@NotNull T entity);

    /**
     * 是否存在这一条数据
     *
     * @param values 条件
     * @apiNote {@link SQLField#id()}
     */
    boolean exist(@NotNull Object... values);

    /**
     * 是否存在这一条数据
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    boolean exist(@NotNull T entity);

    /**
     * 删除一个数据
     *
     * @param values 条件
     * @apiNote {@link SQLField#id()}
     * @see #delete(T)
     */
    void delete(@NotNull Object... values);

    /**
     * 删除一条数据
     *
     * @param entity 实例
     */
    void delete(@NotNull T entity);

    /**
     * 查询一个数据
     *
     * @param entity 示例
     */
    @Nullable
    T select(@NotNull T entity);

    /**
     * 自定义操作
     *
     * @param run 执行
     */
    void custom(@NotNull BiConsumer<SQLManager, SQLEntityInstance<T>> run);

    // Debug
    boolean toggleDebug();

    void setDebug(boolean debug);

    boolean isDebug();

    /**
     * 异步
     *
     * @return {@link SQLAsyncEntityManager}
     * @see #sync()
     */
    SQLAsyncEntityManager<T> async();

    /**
     * {@link SQLAsyncEntityManager}也是实现了同步的方法 所以直接返回this是没有问题的
     *
     * @see #async()
     */
    default SQLEntityManager<T> sync() {
        return this;
    }
}
