package me.huanmeng.util.sql.type;

import me.huanmeng.util.sql.api.SQLTypeParser;
import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLType {
    private final String name;
    private int length;
    private SQLTypeParser typeParser;

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

    public <T> void transform(ResultSet rs, SQLEntityFieldMetaData<T> fieldMetaData, T instance) {
        if (typeParser != null) {
            try {
                fieldMetaData.setValue(instance, typeParser.parser(rs, fieldMetaData.fieldName(), fieldMetaData));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (HutoolAdapter.supportHutool()) {
            fieldMetaData.setValue(instance, HutoolAdapter.getResult(rs, fieldMetaData));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SQLType)) return false;
        SQLType sqlType = (SQLType) o;
        return length == sqlType.length && Objects.equals(name, sqlType.name) && Objects.equals(typeParser, sqlType.typeParser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, length, typeParser);
    }

    public SQLTypeParser typeParser() {
        return typeParser;
    }

    public SQLType typeParser(SQLTypeParser typeParser) {
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
