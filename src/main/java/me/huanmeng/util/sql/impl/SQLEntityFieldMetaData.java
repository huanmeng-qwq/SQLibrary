package me.huanmeng.util.sql.impl;

import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.api.annotation.SQLIgnore;
import me.huanmeng.util.sql.type.HutoolAdapter;
import me.huanmeng.util.sql.type.SQLType;
import me.huanmeng.util.sql.util.ArrayUtil;
import me.huanmeng.util.sql.util.NumberUtil;
import me.huanmeng.util.sql.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLEntityFieldMetaData<T> {
    private final SQLibrary sqlibrary;
    private final Field field;
    private String fieldName;
    private boolean key;
    private Class<?> type;
    private Class<?> componentType;
    private SQLType sqlType;
    private boolean autoIncrement;
    private SQLField.Order order = SQLField.Order.NONE;
    private String simpleName;
    private SQLField.Serialize serialize = SQLField.Serialize.NONE;

    public SQLEntityFieldMetaData(SQLibrary sqlibrary, Field field) {
        this.sqlibrary = sqlibrary;
        this.field = field;
        init();
    }

    private void init() {
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
                        final String[] split = f.sqlType().split(",");
                        for (int i = 0; i < split.length; i++) {
                            split[i] = split[i].trim();
                        }
                        if (split.length <= 1 && !split[0].isEmpty()) {
                            this.sqlType = new SQLType(split[0]);
                        } else if (!split[1].isEmpty() && NumberUtil.isInt(split[1])) {
                            this.sqlType = new SQLType(split[0], Integer.parseInt(split[1]));
                        }
                    }
                    this.autoIncrement = f.isAutoIncrement();
                    this.serialize = f.serialize();
                    return f;
                }).orElseGet(() -> {
                    this.fieldName = field.getName();
                    return null;
                });
    }

    public void setValue(T instance, Object obj) {
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


    public Object getEntityValue(T entity) {
        try {
            Object o = serialize.transform(this, field.get(entity));
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

    public Field field() {
        return field;
    }

    public String fieldName() {
        return fieldName;
    }

    public boolean key() {
        return key;
    }

    public SQLType sqlType() {
        return sqlType;
    }

    public boolean autoIncrement() {
        return autoIncrement;
    }

    public SQLField.Order order() {
        return order;
    }

    public Class<?> type() {
        return type;
    }

    public Class<?> componentType() {
        return componentType;
    }

    public SQLibrary sqlibrary() {
        return sqlibrary;
    }
}
