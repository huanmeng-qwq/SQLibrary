package me.huanmeng.util.sql.type;

import me.huanmeng.util.sql.api.SQLTypeParser;
import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLType<T> {
    protected final String name;
    protected int length;
    protected SQLTypeParser<T> typeParser;

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

    public <I> void transform(ResultSet rs, SQLEntityFieldMetaData<I, T> fieldMetaData, I instance) {
        if (typeParser != null) {
            try {
                fieldMetaData.setValue(instance, typeParser.parser(rs, fieldMetaData.fieldName(), fieldMetaData));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        fieldMetaData.setValue(instance, HutoolAdapter.getResult(rs, fieldMetaData));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SQLType)) return false;
        SQLType<?> sqlType = (SQLType<?>) o;
        return length == sqlType.length && Objects.equals(name, sqlType.name) && Objects.equals(typeParser, sqlType.typeParser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, length, typeParser);
    }

    @NotNull
    public SQLTypeParser<T> typeParser() {
        return typeParser;
    }

    @NotNull
    public SQLType<T> typeParser(SQLTypeParser<T> typeParser) {
        this.typeParser = typeParser;
        return this;
    }

    public String name() {
        return name;
    }

    public int length() {
        return length;
    }
}
