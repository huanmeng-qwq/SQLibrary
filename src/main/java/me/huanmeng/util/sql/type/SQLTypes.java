package me.huanmeng.util.sql.type;

import me.huanmeng.util.sql.api.SQLTypeParser;
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
 *
 * @author huanmeng_qwq
 */
public class SQLTypes {
    private static final Logger log = Logger.getLogger("SQLTypes");
    private final Map<Class<?>, SQLType> types = new ConcurrentHashMap<>();

    public SQLTypes() {
        init();
    }

    /**
     * 注册默认的解析器
     */
    private void init() {
        registerSQLType(String.class, new SQLType("VARCHAR", 255));
        registerSQLTypeWithParser(long.class, new SQLType("MEDIUMINT"), SQLTypeParser.of(ResultSet::getLong));
        registerSQLTypeWithParser(int.class, new SQLType("MEDIUMINT"), SQLTypeParser.of(ResultSet::getInt));
        registerSQLTypeWithParser(double.class, new SQLType("DOUBLE"), SQLTypeParser.of(ResultSet::getDouble));
        registerSQLTypeWithParser(UUID.class, new SQLType("VARCHAR", 36), SQLTypeParser.of((BiFunctionThrowable<ResultSet, String, Object, SQLException>) (resultSet, s) -> UUID.fromString(resultSet.getString(s))));
        registerSQLTypeWithParser(boolean.class, new SQLType("BOOLEAN"), SQLTypeParser.of(ResultSet::getBoolean));


        registerSQLTypeWithParser(Timestamp.class, new SQLType("DATETIME"), SQLTypeParser.of(ResultSet::getTimestamp));
        registerSQLTypeWithParser(byte[].class, new SQLType("BLOB"), SQLTypeParser.of((BiFunctionThrowable<ResultSet, String, Object, SQLException>) (resultSet, s) -> {
            InputStream binaryStream = resultSet.getBinaryStream(s);
            return IoUtil.readBytes(binaryStream);
        }));
        registerSQLTypeWithParser(InputStream.class, new SQLType("BLOB"), SQLTypeParser.of(ResultSet::getBinaryStream));

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
    public void registerSQLTypeWithParser(Class<?> clazz, SQLType sqlType, SQLTypeParser sqlTypeParser) {
        sqlType.typeParser(sqlTypeParser);
        registerSQLType(clazz, sqlType);
    }

    /**
     * 注册SQLType
     *
     * @param clazz 对应类
     * @param type  {@link SQLType}
     */
    public void registerSQLType(Class<?> clazz, SQLType type) {
        types.put(clazz, type);
    }

    /**
     * 获取SQLType
     *
     * @param clazz 对应类
     * @apiNote 未注册则返回VARCHAR, 255
     */
    public SQLType getSQLType(Class<?> clazz) {
        return types.computeIfAbsent(ClassUtil.isBasicType(clazz) ? BasicType.unWrap(clazz) : clazz, e -> {
            log.warning("No SQLType registered for class " + clazz.getName());
            log.warning("Using default SQLType(VARCHAR 255) for class " + clazz.getName());
            return new SQLType("VARCHAR", 255);
        });
    }
}