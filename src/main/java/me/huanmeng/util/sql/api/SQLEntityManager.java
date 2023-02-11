package me.huanmeng.util.sql.api;

import cc.carm.lib.easysql.api.SQLManager;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.impl.SQLEntityInstance;
import me.huanmeng.util.sql.util.BiConsumerThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

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
     * @param values 条件 所有字段(不包括自增字段)
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
     * @param values 条件 所有字段(不包括自增字段)
     * @see SQLEntityInstance#keyNames()
     */
    @Nullable
    T selectFirst(@Nullable Object... values);

    /**
     * 查询1个实体
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @see SQLEntityInstance#keyNames()
     */
    @Nullable
    T selectFirstByAllField(@Nullable Object... values);

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
     * @param values    条件 仅自增字段与{@link SQLField#id()}
     * @apiNote 条件数组是
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    List<T> selectAny(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     * @param values    条件 所有字段(不包括自增字段)
     * @apiNote 条件数组是
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    List<T> selectAnyByField(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     * @param values    条件 所有字段
     * @return 结果
     * @apiNote 条件数组是
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @NotNull
    List<T> selectAnyByAllField(int limit, @Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件 仅自增字段与{@link SQLField#id()}
     * @return 结果
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @Nullable
    T select(@Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件 所有字段(不包括自增字段)
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @Nullable
    T selectByField(@Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件 所有字段
     * @apiNote {@link SQLField#id()}
     * @apiNote {@link SQLField#isAutoIncrement()}
     */
    @Nullable
    T selectByAllField(@Nullable SQLOrderData orderData, @Nullable Object... values);

    /**
     * 查询N个实体
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    List<T> selectAll(@Nullable Object... values);

    /**
     * 查询N个实体
     *
     * @param values 条件 所有字段
     * @apiNote {@link SQLField#id()}
     */
    @NotNull
    List<T> selectAllByAllField(@Nullable Object... values);

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
     * @param values 条件 仅自增字段
     * @apiNote {@link SQLField#id()}
     */
    boolean exist(@Nullable Object... values);

    /**
     * 是否存在这一条数据
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @apiNote {@link SQLField#id()}
     */
    boolean existByField(@Nullable Object... values);

    /**
     * 是否存在这一条数据
     *
     * @param values 条件 所有字段
     * @apiNote {@link SQLField#id()}
     */
    boolean existByAllField(@Nullable Object... values);

    /**
     * 是否存在这一条数据
     *
     * @param name 字段名
     * @param o    字段值
     */
    boolean exist(@NotNull String name, @NotNull Object o);

    /**
     * 是否存在这一条数据
     *
     * @param entity 实例
     * @apiNote {@link SQLField#id()}
     */
    boolean exist(@NotNull T entity);

    /**
     * 删除一个数据
     * <p>
     * 不传参将删除所有的数据！！！！！！！！！！！！
     * 不传参将删除所有的数据！！！！！！！！！！！！
     * 不传参将删除所有的数据！！！！！！！！！！！！
     *
     * @param values 条件 所有字段(不包括自增字段)
     * @see #delete(T)
     */
    void delete(@NotNull Object... values);

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
    void deleteByAllField(@NotNull Object... values);

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
    void custom(@NotNull BiConsumerThrowable<SQLManager, SQLEntityInstance<T>, SQLException> run);

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
