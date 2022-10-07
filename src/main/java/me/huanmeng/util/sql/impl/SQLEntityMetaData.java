package me.huanmeng.util.sql.impl;

import me.huanmeng.util.sql.api.SQLOrderData;
import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.api.annotation.SQLEntity;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.util.NamingCase;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
@SuppressWarnings("unused")
public class SQLEntityMetaData<T> {
    private final SQLibrary sqlibrary;
    private final Class<T> clazz;
    private String tableName;
    private final List<SQLEntityFieldMetaData<T, Object>> fields;
    private SQLOrderData orderData;

    public SQLEntityMetaData(@NotNull SQLibrary sqlibrary, @NotNull Class<T> clazz) {
        this.sqlibrary = sqlibrary;
        this.clazz = clazz;
        this.fields = new CopyOnWriteArrayList<>();
        init();
    }

    /**
     * 初始化
     */
    protected void init() {
        tableName = NamingCase.toCamelCase(NamingCase.toUnderlineCase(clazz.getSimpleName()));
        Optional.ofNullable(clazz.getAnnotation(SQLEntity.class))
                .ifPresent(e -> {
                    if (!e.value().trim().isEmpty()) {
                        tableName = e.value();
                    }
                });
        for (Field field : clazz.getDeclaredFields()) {
            final SQLEntityFieldMetaData<T, Object> fieldMetaData = new SQLEntityFieldMetaData<>(sqlibrary, field);
            fields.add(fieldMetaData);
            if (orderData == null && fieldMetaData.order() != SQLField.Order.NONE) {
                orderData = new SQLOrderData(fieldMetaData.fieldName(), fieldMetaData.order() == SQLField.Order.ASC);
            }
        }
    }

    @Nullable
    public SQLEntityFieldMetaData<T, Object> getField(@NotNull String name) {
        return fields.stream().filter(e -> e.fieldName().equals(name)).findFirst().orElse(null);
    }

    @NotNull
    public List<SQLEntityFieldMetaData<T, Object>> fields() {
        return Collections.unmodifiableList(fields);
    }

    @NotNull
    public List<SQLEntityFieldMetaData<T, Object>> getAutoIncrementFields() {
        return fields().stream().filter(SQLEntityFieldMetaData::autoIncrement).collect(Collectors.toList());
    }

    /**
     * 要获取表明请使用{@link SQLEntityInstance#tableName()}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5")
    @NotNull
    public String tableName() {
        return tableName;
    }

    @NotNull
    protected String tableName0() {
        return tableName;
    }

    @Nullable
    public SQLOrderData orderData() {
        return orderData;
    }
}
