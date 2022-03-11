package me.huanmeng.util.sql.util;

import lombok.*;
import me.huanmeng.util.sql.entity.SQLEntityFieldMetaData;

import java.sql.ResultSet;
import java.util.function.BiFunction;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@Getter
@EqualsAndHashCode
@ToString
public class SQLType {
    private final String name;
    private int length;
    @Setter
    private BiFunction<ResultSet, String, Object> fieldNameFunction;
    @Setter
    private BiFunction<ResultSet, SQLEntityFieldMetaData<?>, Object> fieldFunction;

    public SQLType(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public SQLType(String name) {
        this.name = name;
    }

    public String toSQLString() {
        return length > 0 ? name + "(" + length + ")" : name;
    }

    public <T> BiFunction<ResultSet, T, ResultSet> transform(SQLEntityFieldMetaData<T> meta) {
        return (resultSet, t) -> {
            if (fieldFunction != null) {
                meta.setValue(t, fieldFunction.apply(resultSet, meta));
            } else if (fieldNameFunction == null) {
                meta.setValue(t, getResult(resultSet, meta.getFieldName()));
            } else {
                meta.setValue(t, fieldNameFunction.apply(resultSet, meta.getFieldName()));
            }
            return resultSet;
        };
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <R> R getResult(ResultSet rs, String name) {
        return (R) rs.getString(name);
    }
}
