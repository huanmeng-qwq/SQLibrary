package me.huanmeng.util.sql.api;

import cc.carm.lib.easysql.api.SQLAction;
import cc.carm.lib.easysql.api.SQLQuery;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @param <ACTION> {@link SQLQuery}
 * @param <R>      返回的类型
 * @author huanmeng_qwq
 */
public interface SQLQueryExecute<ACTION extends SQLAction<?>, R> {
    R execute(ACTION query);
}
