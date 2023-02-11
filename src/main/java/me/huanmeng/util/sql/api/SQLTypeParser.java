package me.huanmeng.util.sql.api;

import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;
import me.huanmeng.util.sql.util.BiFunctionThrowable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 * 将 {@link ResultSet} 转换为一个 {@link T}
 *
 * @param <T> 类型
 * @author huanmeng_qwq
 */
public interface SQLTypeParser<T> {
    /**
     * @param resultSet 结果集
     * @param fieldName 字段名
     * @return 解析后的Object
     */
    <I> T parser(ResultSet resultSet, String fieldName, SQLEntityFieldMetaData<I, T> fieldMetaData) throws SQLException;

    /**
     * 快速构建一个通过{@link ResultSet}中的get方法实现
     *
     * @param function 方法
     * @param <T>      类型
     */
    static <T> SQLTypeParser<T> of(BiFunctionThrowable<ResultSet, String, T, SQLException> function) {
        return new SQLTypeParser<T>() {
            @Override
            public <I> T parser(ResultSet resultSet, String fieldName, SQLEntityFieldMetaData<I, T> fieldMetaData) throws SQLException {
                return function.apply(resultSet, fieldName);
            }
        };
    }
}
