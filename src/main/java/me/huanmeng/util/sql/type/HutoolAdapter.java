package me.huanmeng.util.sql.type;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.impl.CollectionConverter;
import cn.hutool.core.convert.impl.MapConverter;
import cn.hutool.core.util.ClassUtil;
import me.huanmeng.util.sql.api.SQLTypeParser;
import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;
import me.huanmeng.util.sql.util.ArrayUtil;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class HutoolAdapter {
    private static boolean hutool = false;

    static {
        try {
            Class.forName("cn.hutool.core.util.StrUtil");
            hutool = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    protected static void registerSQLType(SQLTypes sqlTypes) {
        SQLTypeParser<Collection<?>> collectionParser = new SQLTypeParser<Collection<?>>() {
            @Override
            public <I> Collection<?> parser(ResultSet resultSet, String fieldName, SQLEntityFieldMetaData<I, Collection<?>> fieldMetaData) throws SQLException {
                return new CollectionConverter(fieldMetaData.type()).convert(resultSet.getString(fieldName), null);
            }
        };
        SQLTypeParser<Object[]> arrayParser = new SQLTypeParser<Object[]>() {
            @Override
            public <I> Object[] parser(ResultSet resultSet, String fieldName, SQLEntityFieldMetaData<I, Object[]> fieldMetaData) throws SQLException {
                String data = resultSet.getString(fieldName);
                int min = Math.min(data.length(), 1);
                int max = Math.max(0, data.length() - 1);
                String str = data.substring(min, max);
                String[] strings = Arrays.stream(str.split(",")).map(String::trim).toArray(String[]::new);
                Class<?> componentType = fieldMetaData.componentType();
                if (componentType == null) {
                    return null;
                }
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
            }
        };
        sqlTypes.registerSQLTypeWithParser(Map.class, new SQLType<>("MEDIUMTEXT"), new SQLTypeParser<Object>() {
            @Override
            public <I> Object parser(ResultSet resultSet, String fieldName, SQLEntityFieldMetaData<I, Object> fieldMetaData) throws SQLException {
                return new MapConverter(fieldMetaData.type()).convert(resultSet.getString(fieldName), null);
            }
        });
        sqlTypes.registerSQLTypeWithParser(Set.class, new SQLType<>("MEDIUMTEXT"), collectionParser);
        sqlTypes.registerSQLTypeWithParser(List.class, new SQLType<>("MEDIUMTEXT"), collectionParser);
        sqlTypes.registerSQLTypeWithParser(Collection.class, new SQLType<>("MEDIUMTEXT"), collectionParser);

        sqlTypes.registerSQLTypeWithParser(int[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(Integer[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(Double[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(double[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(Long[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(long[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(Short[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(short[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(Float[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(float[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(Boolean[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(boolean[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(String[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(Object[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
        sqlTypes.registerSQLTypeWithParser(String[].class, new SQLType<>("MEDIUMTEXT"), arrayParser);
    }

    public static boolean supportHutool() {
        return hutool;
    }

    public static <I, T> T getResult(ResultSet rs, SQLEntityFieldMetaData<I, T> meta) {
        try {
            return convert(meta.type(), rs.getString(meta.fieldName()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(Class<T> clazz, Object o) {
        return Convert.convert(clazz, o);
    }

    public static void setFieldValue(Field field, Class<?> clazz, Object instance, Object obj) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(instance, Convert.convert(clazz, obj));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
