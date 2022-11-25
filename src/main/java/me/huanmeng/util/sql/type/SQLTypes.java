package me.huanmeng.util.sql.type;

import me.huanmeng.util.sql.api.SQLTypeParser;
import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;
import me.huanmeng.util.sql.util.BasicType;
import me.huanmeng.util.sql.util.BiFunctionThrowable;
import me.huanmeng.util.sql.util.ClassUtil;
import me.huanmeng.util.sql.util.IoUtil;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 * 解析集
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unchecked")
public class SQLTypes {
    protected static final Logger log = Logger.getLogger("SQLTypes");
    private final Map<Class<?>, SQLType<?>> types = new ConcurrentHashMap<>();

    public SQLTypes() {
        init();
    }

    /**
     * 注册默认的解析器
     */
    protected void init() {
        registerSQLTypeWithParser(String.class, new SQLType<>("VARCHAR", 255), SQLTypeParser.of(ResultSet::getString));
        registerSQLTypeWithParser(long.class, new SQLType<>("BIGINT",20), SQLTypeParser.of(ResultSet::getLong));
        registerSQLTypeWithParser(int.class, new SQLType<>("BIGINT",20), SQLTypeParser.of(ResultSet::getInt));
        registerSQLTypeWithParser(double.class, new SQLType<>("DOUBLE"), SQLTypeParser.of(ResultSet::getDouble));
        registerSQLTypeWithParser(UUID.class, new SQLType<>("VARCHAR", 36), SQLTypeParser.of((BiFunctionThrowable<ResultSet, String, Object, SQLException>) (resultSet, s) -> UUID.fromString(resultSet.getString(s))));
        registerSQLTypeWithParser(boolean.class, new SQLType<>("BOOLEAN"), SQLTypeParser.of(ResultSet::getBoolean));


        registerSQLTypeWithParser(Timestamp.class, new SQLType<>("DATETIME"), SQLTypeParser.of(ResultSet::getTimestamp));
        registerSQLTypeWithParser(byte[].class, new SQLType<>("BLOB"), SQLTypeParser.of((BiFunctionThrowable<ResultSet, String, Object, SQLException>) (resultSet, s) -> {
            InputStream binaryStream = resultSet.getBinaryStream(s);
            return IoUtil.readBytes(binaryStream);
        }));
        registerSQLTypeWithParser(InputStream.class, new SQLType<>("BLOB"), SQLTypeParser.of(ResultSet::getBinaryStream));

        registerSQLTypeWithParser(Enum.class, new SQLType<>("VARCHAR", 100), new SQLTypeParser<Enum>() {
            @Override
            public <I> Enum parser(ResultSet resultSet, String fieldName, SQLEntityFieldMetaData<I, Enum> fieldMetaData) throws SQLException {
                return Enum.valueOf(fieldMetaData.type(), resultSet.getString(fieldName));
            }
        });
        if (HutoolAdapter.supportHutool()) {
            HutoolAdapter.registerSQLType(this);
        }
    }

    /**
     * 注册SQLType
     *
     * @param clazz         对应类
     * @param sqlType       {@link SQLType}
     * @param sqlTypeParser 解析器
     */
    public <T> void registerSQLTypeWithParser(Class<?> clazz, SQLType<T> sqlType, SQLTypeParser<T> sqlTypeParser) {
        registerSQLType(clazz, sqlType.typeParser(sqlTypeParser));
    }

    /**
     * 注册SQLType
     *
     * @param clazz 对应类
     * @param type  {@link SQLType}
     */
    public void registerSQLType(Class<?> clazz, SQLType<?> type) {
        types.put(clazz, type);
    }

    /**
     * 获取SQLType
     *
     * @param clazz 对应类
     * @apiNote 未注册则返回VARCHAR, 255
     */
    public <T> SQLType<T> getSQLType(Class<?> clazz) {
        if (clazz.isEnum()) {
            return (SQLType<T>) types.get(Enum.class);
        }
        return (SQLType<T>) types.computeIfAbsent(ClassUtil.isBasicType(clazz) ? BasicType.unWrap(clazz) : clazz, e -> {
            log.warning("No SQLType registered for class " + clazz.getName());
            log.warning("Using default SQLType(VARCHAR 255) for class " + clazz.getName());
            return new SQLType<>("VARCHAR", 255);
        });
    }
}
