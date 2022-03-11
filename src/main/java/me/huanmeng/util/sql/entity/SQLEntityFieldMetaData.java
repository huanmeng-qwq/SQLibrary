package me.huanmeng.util.sql.entity;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import me.huanmeng.util.sql.annotation.SQLField;
import me.huanmeng.util.sql.util.SQLType;
import me.huanmeng.util.sql.util.SQLTypeUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@Getter
public class SQLEntityFieldMetaData<T> {
    private static final Logger log = LogManager.getLogManager().getLogger("SQLEntityField");
    private final Field field;
    private String fieldName;
    private boolean key;
    private Class<?> type;
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
                    log.warning(String.format("%s not found @SQLField annotation", fieldName));
                    return null;
                });
    }

    public void setValue(T instance, Object type) {
        try {
            field.set(instance, ConverterRegistry.getInstance().convert(getType(), type));
        } catch (Exception e) {
            throw new RuntimeException(String.format("设置字段 %s 时出先了错误,value:%s", getType().getSimpleName() + "#" + fieldName, type), e);
        }
    }

    @SneakyThrows
    public Object getValue(T entity) {
        return field.get(entity);
    }
}
