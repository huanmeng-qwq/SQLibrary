package me.huanmeng.util.sql.impl;

import me.huanmeng.util.sql.api.SQLOrderData;
import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.api.annotation.SQLEntity;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.util.NamingCase;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class SQLEntityMetaData<T> {
    private final SQLibrary sqlibrary;
    private final Class<T> clazz;
    private String tableName;
    private final List<SQLEntityFieldMetaData<T>> fields;
    private SQLOrderData orderData;

    public SQLEntityMetaData(SQLibrary sqlibrary, Class<T> clazz) {
        this.sqlibrary = sqlibrary;
        this.clazz = clazz;
        this.fields = new CopyOnWriteArrayList<>();
        init();
    }

    public void init() {
        tableName = NamingCase.toCamelCase(NamingCase.toUnderlineCase(clazz.getSimpleName()));
        Optional.ofNullable(clazz.getAnnotation(SQLEntity.class))
                .ifPresent(e -> {
                    if (!e.value().trim().isEmpty()) {
                        tableName = e.value();
                    }
                });
        for (Field field : clazz.getDeclaredFields()) {
            final SQLEntityFieldMetaData<T> fieldMetaData = new SQLEntityFieldMetaData<>(sqlibrary, field);
            fields.add(fieldMetaData);
            if (fieldMetaData.order() != SQLField.Order.NONE) {
                orderData = new SQLOrderData(fieldMetaData.fieldName(), fieldMetaData.order() == SQLField.Order.ASC);
            }
        }
    }

    public SQLEntityFieldMetaData<T> getField(String name) {
        return fields.stream().filter(e -> e.fieldName().equals(name)).findFirst().orElse(null);
    }

    public List<SQLEntityFieldMetaData<T>> fields() {
        return Collections.unmodifiableList(fields);
    }

    public List<SQLEntityFieldMetaData<T>> getAutoIncrementFields() {
        return fields().stream().filter(SQLEntityFieldMetaData::autoIncrement).collect(Collectors.toList());
    }

    public String tableName() {
        return tableName;
    }

    public SQLOrderData orderData() {
        return orderData;
    }
}
