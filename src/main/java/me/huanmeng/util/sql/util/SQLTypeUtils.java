package me.huanmeng.util.sql.util;

import cn.hutool.core.convert.BasicType;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.impl.CollectionConverter;
import cn.hutool.core.convert.impl.MapConverter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import lombok.SneakyThrows;
import me.huanmeng.util.sql.entity.SQLEntityFieldMetaData;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLTypeUtils {
    private static final Logger log = Logger.getLogger("SQLTypes");
    private static final Map<Class<?>, SQLType> types = new ConcurrentHashMap<>();

    static {
        init();
    }

    private SQLTypeUtils() {
    }

    @SneakyThrows
    private static void init() {
        registerSQLType(String.class, new SQLType("VARCHAR", 255));
        registerSQLType(long.class, new SQLType("MEDIUMINT"), (rs, name) -> {
            try {
                return rs.getLong(name);
            } catch (Exception e) {
                return null;
            }
        });
        registerSQLType(int.class, new SQLType("MEDIUMINT"), (rs, name) -> {
            try {
                return rs.getInt(name);
            } catch (Exception e) {
                return null;
            }
        });
        registerSQLType(double.class, new SQLType("DOUBLE"), (rs, name) -> {
            try {
                return rs.getDouble(name);
            } catch (Exception e) {
                return null;
            }
        });
        registerSQLType(UUID.class, new SQLType("VARCHAR", 36), (rs, name) -> {
            try {
                return UUID.fromString(rs.getString(name));
            } catch (Exception e) {
                return null;
            }
        });
        registerSQLType(boolean.class, new SQLType("BOOLEAN"), (rs, name) -> {
            try {
                return rs.getBoolean(name);
            } catch (Exception e) {
                try {
                    return Boolean.parseBoolean(rs.getString(name));
                } catch (Exception a) {
                    return null;
                }
            }
        });
        final BiFunction<ResultSet, SQLEntityFieldMetaData<?>, Object> collectionFunction = (rs, field) -> {
            try {
                return new CollectionConverter(field.getType()).convert(rs.getString(field.getFieldName()), null);
            } catch (Exception e) {
                return null;
            }
        };
        final BiFunction<ResultSet, SQLEntityFieldMetaData<?>, Object> arrayFunction = (rs, field) -> {
            try {
                String data = rs.getString(field.getFieldName());
                int min = Math.min(data.length(), 1);
                int max = Math.max(0, data.length() - 1);
                String str = data.substring(min, max);
                String[] strings = Arrays.stream(str.split(",")).map(String::trim).toArray(String[]::new);
                Class<?> componentType = field.getComponentType();
                Object[] array = ArrayUtil.newArray(componentType, strings.length);
                for (int i = 0; i < strings.length; i++) {
                    String s = strings[i];
                    if (s.equals("null")) {
                        array[i] = null;
                    } else {
                        array[i] = Convert.convert(componentType, s, ClassUtil.getDefaultValue(componentType));
                    }
                }
                return array;
            } catch (Exception e) {
                return null;
            }
        };
        SQLTypeUtils.registerSQLTypeWithField(Map.class, new SQLType("MEDIUMTEXT"), (rs, field) -> {
            try {
                return new MapConverter(field.getType()).convert(rs.getString(field.getFieldName()), null);
            } catch (Exception e) {
                return null;
            }
        });
        SQLTypeUtils.registerSQLTypeWithField(Set.class, new SQLType("MEDIUMTEXT"), collectionFunction);
        SQLTypeUtils.registerSQLTypeWithField(List.class, new SQLType("MEDIUMTEXT"), collectionFunction);
        SQLTypeUtils.registerSQLTypeWithField(Collection.class, new SQLType("MEDIUMTEXT"), collectionFunction);

        SQLTypeUtils.registerSQLTypeWithField(int[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(Integer[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(Double[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(double[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(Long[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(long[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(Short[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(short[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(Float[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(float[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(Boolean[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(boolean[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(String[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(Object[].class, new SQLType("MEDIUMTEXT"), arrayFunction);
        SQLTypeUtils.registerSQLTypeWithField(String[].class, new SQLType("MEDIUMTEXT"), arrayFunction);

        SQLTypeUtils.registerSQLType(Timestamp.class, new SQLType("DATETIME"), (rs, name) -> {
            try {
                return rs.getTimestamp(name);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public static void registerSQLType(Class<?> clazz, SQLType type) {
        types.put(clazz, type);
    }

    public static void registerSQLType(Class<?> clazz, SQLType type, BiFunction<ResultSet, String, Object> function) {
        type.setFieldNameFunction(function);
        types.put(clazz, type);
    }

    public static void registerSQLTypeWithField(Class<?> clazz, SQLType type, BiFunction<ResultSet, SQLEntityFieldMetaData<?>, Object> function) {
        type.setFieldFunction(function);
        types.put(clazz, type);
    }


    public static SQLType getSQLType(Class<?> clazz) {
        return types.computeIfAbsent(ClassUtil.isBasicType(clazz) ? BasicType.unWrap(clazz) : clazz, e -> {
            log.warning("No SQLType registered for class " + clazz.getName());
            log.warning("Using default SQLType(VARCHAR 255) for class " + clazz.getName());
            return new SQLType("VARCHAR", 255);
        });
    }
}
