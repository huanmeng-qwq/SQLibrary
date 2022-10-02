package me.huanmeng.util.sql.api;

import cc.carm.lib.easysql.api.SQLManager;
import me.huanmeng.util.sql.impl.SQLEntityInstance;
import me.huanmeng.util.sql.impl.SQLOrderData;
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
     * @see SQLEntityInstance#getKeyNames()
     * @see #selectFirst(Object...)
     * @deprecated
     */
    @Deprecated
    T select(Object... values);

    T selectFirst(String[] name,Object... values);

    List<T> selectAny(String[] name,Object... values);

    /**
     * 查询1个实体
     *
     * @param values 条件
     * @see SQLEntityInstance#getKeyNames()
     */
    T selectFirst(Object... values);

    /**
     * 查询N个实体
     *
     * @param limit 条数
     */
    List<T> select(int limit);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     */
    List<T> select(int limit, @Nullable SQLOrderData orderData);

    /**
     * 查询N个实体并排序
     *
     * @param limit     条数
     * @param orderData 排序 可选
     * @param values    条件
     * @apiNote 条件数组是
     */
    List<T> selectAny(int limit, @Nullable SQLOrderData orderData, Object... values);

    /**
     * 查询1个实体并排序
     *
     * @param orderData 排序 可选
     * @param values    条件
     */
    T select(@Nullable SQLOrderData orderData, Object... values);

    /**
     * 查询N个实体
     *
     * @param values 条件
     */
    List<T> selectAll(Object... values);

    /**
     * 查询所有实体
     */
    List<T> selectAll();

    /**
     * 更新一条记录<p>
     * 前提是它已经存在
     *
     * @param entity 实例
     */
    void update(T entity);

    /**
     * 写入一条记录
     *
     * @param entity 实例
     */
    T insert(T entity);

    /**
     * 更新或写入一条记录
     *
     * @param entity 实例
     */
    T updateOrInsert(T entity);

    /**
     * 是否存在这一条记录
     *
     * @param values 条件
     */
    boolean exist(Object... values);

    /**
     * 是否存在这一天记录
     *
     * @param entity 实例
     */
    boolean exist(T entity);

    /**
     * 删除一个记录
     *
     * @param values 条件
     * @see #delete(T)
     */
    void delete(Object... values);

    /**
     * 删除一条记录
     *
     * @param entity 实例
     */
    void delete(T entity);

    /**
     * 查询一个纪律
     *
     * @param entity 示例
     */
    T select(T entity);

    /**
     * 自定义操作
     *
     * @param run 执行
     */
    void custom(BiConsumer<SQLManager, SQLEntityInstance<T>> run);

    boolean toggleDebug();

    void setDebug(boolean debug);

    boolean isDebug();
}
