package me.huanmeng.util.sql.entity;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import me.huanmeng.util.sql.annotation.SQLEntity;
import me.huanmeng.util.sql.annotation.SQLField;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLEntityMetaData<T> {
    @Getter
    private final Class<T> clazz;
    @Getter
    private String tableName;
    private final List<SQLEntityFieldMetaData<T>> fields;
    @Getter
    private SQLOrderData orderData;

    @SneakyThrows
    public SQLEntityMetaData(Class<T> clazz) {
        this.clazz = clazz;
        this.fields = new CopyOnWriteArrayList<>();
        init();
    }

    public void init() {
        tableName = StrUtil.toCamelCase(StrUtil.toUnderlineCase(clazz.getSimpleName()));
        Optional.ofNullable(AnnotationUtil.getAnnotation(clazz, SQLEntity.class))
                .ifPresent(e -> {
                    if (!e.value().trim().isEmpty()) {
                        tableName = e.value();
                    }
                });
        for (Field field : clazz.getDeclaredFields()) {
            final SQLEntityFieldMetaData<T> fieldMetaData = new SQLEntityFieldMetaData<>(field);
            fields.add(fieldMetaData);
            if (fieldMetaData.getOrder() != SQLField.Order.NONE) {
                orderData = new SQLOrderData(fieldMetaData.getFieldName(), fieldMetaData.getOrder() == SQLField.Order.ASC);
            }
        }
    }

    public SQLEntityFieldMetaData<T> getField(String name){
        return fields.stream().filter(e->e.getFieldName().equals(name)).findFirst().orElse(null);
    }

    public List<SQLEntityFieldMetaData<T>> getFields() {
        return Collections.unmodifiableList(fields);
    }
}
