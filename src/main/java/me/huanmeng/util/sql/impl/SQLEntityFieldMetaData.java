package me.huanmeng.util.sql.impl;

import me.huanmeng.util.sql.api.SQLTypeParser;
import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.api.annotation.SQLIgnore;
import me.huanmeng.util.sql.type.HutoolAdapter;
import me.huanmeng.util.sql.type.SQLType;
import me.huanmeng.util.sql.util.ArrayUtil;
import me.huanmeng.util.sql.util.NumberUtil;
import me.huanmeng.util.sql.util.ReflectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @param <I> 类 class
 * @param <T> 字段的类型 这里不明确 建议使用{@link Object}
 * @author huanmeng_qwq
 */
@SuppressWarnings({"unused", "unchecked"})
public class SQLEntityFieldMetaData<I, T> {
    protected static Logger logger = LoggerFactory.getLogger("SQLFieldMetaData");
    protected final SQLibrary sqlibrary;
    protected final Field field;
    protected String fieldName;
    protected boolean key;
    protected Class<?> type;
    protected Class<?> componentType;
    protected SQLType<T> sqlType;
    protected boolean autoIncrement;
    protected SQLField.Order order = SQLField.Order.NONE;
    protected String simpleName;
    protected SQLField.Serialize serialize = SQLField.Serialize.NONE;
    public static final int MAX_KEY_LENGTH;
    public static final int CHAR_BYTE;

    static {
        try {
            MAX_KEY_LENGTH = Integer.parseInt(System.getProperty("sqlibrary.max-key-length", "767"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            CHAR_BYTE = Integer.parseInt(System.getProperty("sqlibrary.char-byte", "4"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SQLEntityFieldMetaData(@NotNull SQLibrary sqlibrary, @NotNull Field field) {
        this.sqlibrary = sqlibrary;
        this.field = field;
        init();
    }

    /**
     * 初始化
     */
    protected void init() {
        this.field.setAccessible(true);
        this.type = this.field.getType();
        this.componentType = this.field.getType().getComponentType();
        this.sqlType = sqlibrary.typeByClass(this.type());
        this.simpleName = this.type().getSimpleName();
        Optional.ofNullable(field.getAnnotation(SQLField.class))
                .map(f -> {
                    if (f.value().trim().isEmpty()) {
                        this.fieldName = this.field.getName();
                    } else {
                        this.fieldName = f.value();
                    }
                    this.order = f.orderBy();
                    this.key = f.id();
                    if (!f.sqlType().trim().isEmpty()) {
                        String[] split = f.sqlType().split(",");
                        for (int i = 0; i < split.length; i++) {
                            split[i] = split[i].trim();
                        }
                        if (split.length <= 1 && !split[0].isEmpty()) {
                            this.sqlType = new SQLType<>(split[0]);
                        } else if (!split[1].isEmpty() && NumberUtil.isInt(split[1])) {
                            this.sqlType = new SQLType<>(split[0], Integer.parseInt(split[1]));
                        }
                    }
                    // 索引字段的长度不能是255
                    /*
                        1071 - Specified key was too long; max key length is 767 bytes
                     */
                    if (this.key && this.sqlType.length() * CHAR_BYTE >= MAX_KEY_LENGTH) {
                        logger.warn("Errors will occur in {} and have been corrected automatically", sqlType.toSQLString());
                        SQLTypeParser<T> sqlTypeParser = sqlType.typeParser();
                        // def: 191=767/4
                        sqlType = new SQLType<T>(sqlType.name(), Math.floorDiv(MAX_KEY_LENGTH, CHAR_BYTE)).typeParser(sqlTypeParser);
                    }
                    this.autoIncrement = f.isAutoIncrement();
                    this.serialize = f.serialize();
                    return f;
                }).orElseGet(() -> {
                    this.fieldName = field.getName();
                    return null;
                });
    }

    /**
     * 设置实例的值
     *
     * @param instance 实例
     * @param obj      值
     */
    public void setValue(@NotNull I instance, @Nullable T obj) {
        try {
            final Method method = ReflectUtil.getMethod(instance.getClass(), true, "set" + this.field.getName(), this.type());
            if (method != null) {
                if (method.getAnnotation(SQLIgnore.class) == null) {
                    ReflectUtil.invoke(instance, method, obj);
                    return;
                }
            }
            if (HutoolAdapter.supportHutool()) {
                HutoolAdapter.setFieldValue(field, type(), obj, instance);
            } else {
                field.set(instance, obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("设置成员变量 %s 时出现了错误,value: %s", this.simpleName + "#" + this.fieldName, obj), e);
        }
    }


    /**
     * @param entity 实例
     * @return 该成员变量的值
     * @see SQLField.Serialize
     */
    public Object getEntityValue(@NotNull I entity) {
        try {
            Object o = serialize.serialize(this, field.get(entity));
            if (o instanceof Collection) {
                o = o.toString();
            } else if (ArrayUtil.isArray(o)) {
                if (o instanceof byte[]) {
                    return o;
                }
                return ArrayUtil.toString(o);
            }
            return o;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("获取 %s 的 %s 成员变量出现了错误", entity, fieldName), e);
        }
    }

    /**
     * 反序列化字段
     * @param resultSet 结果集
     * @param instance 对象实例
     */
    public void deserialize(ResultSet resultSet, I instance) {
        try {
            serialize.deserialize(this, resultSet, instance);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return {@link Field}
     */
    @NotNull
    public Field field() {
        return field;
    }

    /**
     * @return 字段名称
     * @see Field#getName()
     * @see SQLField#value()
     */
    @NotNull
    public String fieldName() {
        return fieldName;
    }

    /**
     * @return {@link SQLField#id()}
     */
    public boolean key() {
        return key;
    }

    /**
     * @return {@link SQLType}
     */
    @NotNull
    public SQLType<T> sqlType() {
        return sqlType;
    }

    /**
     * @return 自增id
     */
    public boolean autoIncrement() {
        return autoIncrement;
    }

    /**
     * @return {@link SQLField.Order}
     */
    @NotNull
    public SQLField.Order order() {
        return order;
    }

    /**
     * @return 成员变量类型
     */
    @NotNull
    public Class<T> type() {
        return (Class<T>) type;
    }

    /**
     * @return 数组的类型
     * @apiNote 为数组时有效
     */
    @Nullable
    public Class<?> componentType() {
        return componentType;
    }

    @NotNull
    public SQLibrary sqlibrary() {
        return sqlibrary;
    }
}
