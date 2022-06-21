package me.huanmeng.util.sql.entity;

import cc.carm.lib.easysql.api.SQLManager;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import me.huanmeng.util.sql.annotation.SQLField;
import me.huanmeng.util.sql.annotation.SQLIgnore;
import me.huanmeng.util.sql.util.SQLType;
import me.huanmeng.util.sql.util.SQLTypeUtils;
import me.huanmeng.util.sql.util.VersionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@Getter
public class SQLEntityFieldMetaData<T> {
    private static final String EASY_SQL_VERSION = VersionUtils.getMavenVersion(SQLManager.class, "cc.carm.lib/easysql-api");
    private static final Logger log = Logger.getLogger("SQLEntityField");
    private final Field field;
    private String fieldName;
    private boolean key;
    private Class<?> type;
    private Class<?> componentType;
    private SQLType sqlType;
    private boolean isAutoIncrement;
    private SQLField.Order order = SQLField.Order.NONE;

    public SQLEntityFieldMetaData(Field field) {
        this.field = field;
        init();
    }

    private void init() {
        field.setAccessible(true);
        type = field.getType();
        componentType = field.getType().getComponentType();
        sqlType = SQLTypeUtils.getSQLType(type);
        Optional.ofNullable(AnnotationUtil.getAnnotation(field, SQLField.class))
                .map(f -> {
                    if (f.value().trim().isEmpty()) {
                        fieldName = field.getName();
                    } else {
                        fieldName = f.value();
                    }
                    order = f.orderBy();
                    key = f.id();
                    if (!f.sqlType().trim().isEmpty()) {
                        final String[] split = f.sqlType().split(",");
                        for (int i = 0; i < split.length; i++) {
                            split[i] = split[i].trim();
                        }
                        if (split.length <= 1 && !split[0].isEmpty()) {
                            sqlType = new SQLType(split[0]);
                        } else if (StrUtil.isNumeric(split[1]) && !split[1].isEmpty()) {
                            sqlType = new SQLType(split[0], Integer.parseInt(split[1]));
                        }
                    }
                    isAutoIncrement = f.isAutoIncrement();
                    return f;
                }).orElseGet(() -> {
                    fieldName = field.getName();
                    return null;
                });
    }

    public void setValue(T instance, Object type) {
        try {
            final Method method = ReflectUtil.getMethod(instance.getClass(), true, "set" + field.getName(), this.type);
            if (method != null) {
                if (!AnnotationUtil.hasAnnotation(method, SQLIgnore.class)) {
                    ReflectUtil.invoke(instance, method, type);
                    return;
                }
            }
            field.set(instance, Convert.convert(getType(), type));
        } catch (Exception e) {
            throw new RuntimeException(String.format("设置字段 %s 时出现了错误,value:%s", getType().getSimpleName() + "#" + fieldName, type), e);
        }
    }


    @SneakyThrows
    public Object getValue(T entity) {
        Object o = field.get(entity);
        if (o instanceof Collection) {
            o = o.toString();
        } else if (ArrayUtil.isArray(o)) {
            return ArrayUtil.toString(o);
        }
        return o;
    }
}
